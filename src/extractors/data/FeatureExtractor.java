package extractors.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.Scanner;

import events.GenericEvent;

/**
 * Handles the loading and running of various feature extraction modules.
 * 
 * @author Patrick Koch (TestVectorShutdown by Adam Goodkind)
 */
public class FeatureExtractor {
	private LinkedList<DataNode> data;
	private LinkedList<ExtractionModule> moduleList = new LinkedList<>();
	private LinkedList<TestVectorShutdownModule> shutdownList = new LinkedList<>();
	private String query;
	private String outputDirectory;
	private boolean useCustomOutputDir;
	private String customOutputDirectoryString;
	private String defaultOutputDir;

	private PrintStream standardOut = System.out;
	public int getCurSlice() {
		return curSlice;
	}

	public void setCurSlice(int curSlice) {
		this.curSlice = curSlice;
	}

	private PrintStream standardErr = System.err;

	private boolean suppressSystemOut;

	// TODO HANDLE THESE SO THAT THEY CAN BE USED IN CUSTOM OUTPUTDIRS
	@SuppressWarnings("unused")
	private String mode;

	public boolean isSuppressSystemOut() {
		return suppressSystemOut;
	}

	public void setSuppressSystemOut(boolean suppressSystemOut) {
		this.suppressSystemOut = suppressSystemOut;
	}

	@SuppressWarnings("unused")
	private String unit;
	@SuppressWarnings("unused")
	private int sliceSize;
	@SuppressWarnings("unused")
	private boolean wrapAnswers;
	@SuppressWarnings("unused")
	private boolean allowPartials;
	@SuppressWarnings("unused")
	private int partialPercent;
	@SuppressWarnings("unused")
	private boolean incremental;
	@SuppressWarnings("unused")
	private int stepSize;
	@SuppressWarnings("unused")
	private int stepLimit;

	private int curSlice;

	/**
	 * Loads modules into the FeatureExtractor to be used during template/test
	 * processes.
	 * <p>
	 * <p>
	 * Modules are loaded from modules.conf by the fully qualified name of the
	 * module's Java Class (i.e. features.nyit.KeyHold)
	 * 
	 * @throws Exception
	 */
	public void loadModules() throws Exception {

		Scanner scanner = null;
		try {
			scanner = new Scanner(new File("modules.conf"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		while (scanner.hasNextLine()) {
			String token = scanner.nextLine().trim();
			if (!token.isEmpty()) {
				if (token.charAt(0) != '#') {
					try { 
						moduleList.add((ExtractionModule) Class.forName(token).newInstance());
						// if module uses TextVectorShutdown, add to list
						if (TestVectorShutdownModule.class.isAssignableFrom(Class.forName(token))) {
							shutdownList.add((TestVectorShutdownModule) Class.forName(token)
									.newInstance() );
						}
					} catch (ClassNotFoundException | InstantiationException
							| IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}
		scanner.close();
		if (moduleList.isEmpty()) {
			throw new Exception("No Modules Selected.");
		}
	}

	/**
	 * Loads modules into the FeatureExtractor to be used during template/test
	 * processes. For use when not loading through AppWindow
	 * <p>
	 * <p>
	 * Modules are loaded from modules.conf by the fully qualified name of the
	 * module's Java Class (i.e. features.nyit.KeyHold)
	 * 
	 * @throws Exception
	 * @author Adam Goodkind
	 */
	public void loadModulesExternal() throws Exception {

		Scanner scanner = null;
		try {
			scanner = new Scanner(new File("/Users/adamg/kd/darpa_aa_code/mockup/modules.conf"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		while (scanner.hasNextLine()) {
			String token = scanner.nextLine().trim();
			if (!token.isEmpty()) {
				if (token.charAt(0) != '#') {
					try {
						moduleList.add((ExtractionModule) Class.forName(token)
								.newInstance());
					} catch (ClassNotFoundException | InstantiationException
							| IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}
		scanner.close();
		if (moduleList.isEmpty()) {
			throw new Exception("No Modules Selected.");
		}
	}
	
	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getOutputDirectory() {
		return outputDirectory;
	}

	private void setOutputDirectory(String outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

	public void setCustomOutputDirectory(String customOutputDirectoryString) {
		this.customOutputDirectoryString = customOutputDirectoryString;
		this.setUseCustomOutputDir(true);
		this.setOutputDirectory(parseCustomDirectoryString(customOutputDirectoryString));
	}

	// TODO WRITE DOCUMENTATION!
	// FIX UNIT CONVERSIONS.
	public String parseCustomDirectoryString(String customOutputDirectoryString) {
		Calendar now = Calendar.getInstance();
		// String time = String.format("%tH-%tM-%tS",
		// now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE),
		// now.get(Calendar.SECOND));
		// String date = String.format("%tC-%tm-%td", now.get(Calendar.YEAR),
		// now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH));

		// customOutputDirectoryString =
		// customOutputDirectoryString.replaceAll("%t", time);
		// customOutputDirectoryString =
		// customOutputDirectoryString.replaceAll("%d", date);
		customOutputDirectoryString = customOutputDirectoryString.replaceAll(
				"%slice%", String.format("%02d", curSlice));
		return customOutputDirectoryString;
	}

	private void systemOutToNull(boolean suppress) {
		if (suppress) {
			System.setOut(new PrintStream(new OutputStream() {
				public void write(int b) {
					// DO NOTHING
				}
			}));
			System.setErr(new PrintStream(new OutputStream() {
				public void write(int b) {
					// DO NOTHING
				}
			}));

		} else {
			System.setOut(standardOut);
			System.setErr(standardErr);
		}
	}

	/**
	 * Clears the internal moduleList.
	 */
	public void flushModules() {
		moduleList.clear();
	}

	/**
	 * Returns true if there are extraction modules loaded.
	 * 
	 * @return true if there are extraction modules loaded.
	 */
	public boolean hasModulesLoaded() {
		if (moduleList.size() > 0) {
			return true;
		}
		return false;
	}

	/**
	 * Creates a new FeatureExtractor for the specified data.
	 * 
	 * @param data
	 *            Collection of DataNode objects
	 * @see Collection
	 * @see DataNode
	 */
	public FeatureExtractor(LinkedList<DataNode> data) {
		this.data = data;
	}

	/**
	 * Creates a template from the current data and extraction modules.
	 * <p>
	 * <p>
	 * Future revisions will dump this as a flat text file.
	 * 
	 * @throws FileNotFoundException
	 */
	public void createTemplates() throws FileNotFoundException {
		Date dateBegin = new Date();
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd[H-m-s]Z");
		StringBuilder now = new StringBuilder(dateformat.format(dateBegin));
		PrintWriter log = null;
		if (!isUseCustomOutputDir())
			outputDirectory = "Templates" + now;
		else
			outputDirectory = parseCustomDirectoryString(customOutputDirectoryString);
		new File(outputDirectory).mkdirs();

		try {
			log = new PrintWriter(new File(outputDirectory + "/Experiment.log"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		PrintWriter template = null;
		if (data != null) {
			log.println("Templating Process Begun At: " + now);
			System.out.println("Templating Process Begun At: " + now);
			log.println("Modules Run:");
			for (ExtractionModule em : moduleList)
				log.println(em.getName());
			log.println("Data Selection Query: " + query);
			log.println("Total Users: " + data.size());
			log.println("Begin User List:");
			for (DataNode dn : data) {
				System.out
						.println("Processing User: " + dn.getUserID() + "...");
				log.println(dn.getUserID());
				template = new PrintWriter(new File(outputDirectory
						+ "/TemplateUser" + dn.getUserID() + ".txt"));
				for (ExtractionModule em : moduleList) {
					if (suppressSystemOut)
						systemOutToNull(true);
					Collection<Feature> features = em.extract(dn);
					if (suppressSystemOut)
						systemOutToNull(false);
					if (features != null) {
						for (Feature f : features) {
							template.println(f.toTemplate());
							// System.out.println(f.toTemplate());
						}
					} else {/* do nothing */
					}
				}
				template.close();
			}
			Date dateFinish = new Date();
			now = new StringBuilder(dateformat.format(dateFinish));
			log.println("Templating Process Finished At: " + now);
			System.out.println("Templating Process Finished At: " + now);
			log.println("Total Extraction Time: "
					+ ((dateFinish.getTime() - dateBegin.getTime()) / 1000)
					+ "seconds");
			log.close();
		}
	}

	/**
	 * Entry point for creating test vectors.
	 * 
	 * @throws FileNotFoundException
	 */
	public void createTestVectors(String unit, int sliceSize,
			boolean wrapAnswers, boolean allowPartials, int partialPercent,
			boolean incremental, int stepSize, int stepLimit)
			throws FileNotFoundException {
		this.unit = unit;
		this.sliceSize = sliceSize;
		this.wrapAnswers = wrapAnswers;
		this.allowPartials = allowPartials;
		this.partialPercent = partialPercent;
		this.incremental = incremental;
		this.stepSize = stepSize;
		this.stepLimit = stepLimit;

		if (!incremental) {
			stepSize = 1;
			stepLimit = sliceSize;
		}
		for (int curSlice = sliceSize; curSlice <= stepLimit; curSlice += stepSize) {
			this.curSlice = curSlice;
			if (wrapAnswers) {
				windowsWrapAnswers(unit, curSlice, allowPartials,
						partialPercent);
			} else {
				makeTestVectorSlice(unit, curSlice, allowPartials, partialPercent);
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Runs a Non-Answer Wrapping test vector creation session.
	 * 
	 * @throws FileNotFoundException
	 */
	public void makeTestVectorSlice(String unit, int value, boolean allowPartials,
			int partialPercent) throws FileNotFoundException {
		Date dateBegin = new Date();
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd[H-m-s]Z");
		StringBuilder now = new StringBuilder(dateformat.format(dateBegin));
		PrintWriter log = null;
		if (!isUseCustomOutputDir())
			outputDirectory = "TestVectors" + now;
		else
			outputDirectory = parseCustomDirectoryString(customOutputDirectoryString);
		new File(outputDirectory).mkdirs();
		log = new PrintWriter(new File(outputDirectory + "/Experiment.log"));
		PrintWriter pw = null;
		if (data != null) {

			log.println("TestVector Process Begun At: " + now);
			System.out.println("TestVector Process Begun At: " + now);
			log.println("Modules Run:");
			for (ExtractionModule em : moduleList)
				log.println(em.getName());
			log.println("Data Selection Query: " + query);
			log.println("Slice Unit: " + unit);
			log.println("Slice Value: " + value);
			log.println("Scan Window Wraps Answer Boundaries: False");
			log.println("Allow Partials: " + allowPartials);
			log.println("Total Users: " + data.size());
			log.println("Begin User List:");

			StringBuilder nameVector = new StringBuilder();
			// Put all Feature Names in a List.
			for (ExtractionModule em : moduleList) {
				if (suppressSystemOut)
					systemOutToNull(true);
				Collection<Feature> features = em.extract(data.getFirst());
				if (suppressSystemOut)
					systemOutToNull(false);
				if (features != null) {
					for (Feature f : features) {
						nameVector.append(f.getFeatureName() + "|");
					}
				} else {
					continue;
				}
			}
			if (nameVector.length() > 0) {
				nameVector.deleteCharAt(nameVector.length() - 1);
			}

			double percent = Double.valueOf(partialPercent) / 100;
			int scanlength = 300000; // default 5minutes
			if (unit.equalsIgnoreCase("seconds")) {
				scanlength = value * 1000;
			} else if (unit.equalsIgnoreCase("minutes")) {
				scanlength = value * 1000 * 60;
			}

			for (DataNode dn : data) {
				System.out.println("Processing User: " + dn.getUserID());
				log.println(dn.getUserID());
				try {
					pw = new PrintWriter(new File(outputDirectory
							+ "/TestVectorsUser" + dn.getUserID() + ".txt"));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				pw.println(nameVector);
				for (Answer ans : dn) {
					LinkedList<GenericEvent> slice = new LinkedList<GenericEvent>();
					GenericEvent storeLast = null;
					slice.clear();
					for (GenericEvent ge : ans.getEventList()) {
						slice.add(ge);
						if (slice.getLast().getWhen()
								- slice.getFirst().getWhen() > scanlength) {
							storeLast = slice.pollLast();
							Answer newAns = (Answer) ans.clone();
							newAns.setEventList(slice);
							slice.clear();
							slice.add(storeLast);
							DataNode newDn = new DataNode(dn.getUserID());
							newDn.add(newAns);
							StringBuilder featureVector = new StringBuilder();
							for (ExtractionModule em : moduleList) {
								if (suppressSystemOut)
									systemOutToNull(true);
								Collection<Feature> features = em
										.extract(newDn);
								if (suppressSystemOut)
									systemOutToNull(false);
								if (features != null) {
									for (Feature f : features) {
										featureVector
												.append(f.toVector() + "|");
									}
								}
							}
							if (featureVector.length() > 0) {
								featureVector.deleteCharAt(featureVector
										.length() - 1);
							}
							pw.println(featureVector);
//							 System.out.println("<1" + featureVector + ">");
						}
					}

					if (allowPartials && slice.size() > 0) {
						if ((slice.getLast().getWhen() - slice.getFirst()
								.getWhen()) > (scanlength * percent)) {
							Answer newAns = (Answer) ans.clone();
							newAns.setEventList(slice);
							DataNode newDn = new DataNode(dn.getUserID());
							newDn.add(newAns);
							StringBuilder featureVector = new StringBuilder();
							for (ExtractionModule em : moduleList) {
								Collection<Feature> features = em
										.extract(newDn);
								if (features != null) {
									for (Feature f : features) {
										featureVector
												.append(f.toVector() + "|");
									}
								} else {
									continue;
								}
							}
							if (featureVector.length() > 0) //added by AG, 3/2/15
								featureVector.deleteCharAt(featureVector.length() - 1);
							pw.println(featureVector);
//							 System.out.println("<2" + featureVector + ">");
						}
					}
				}
				pw.close();
			} // close dataNode loop
			// After all extraction is complete, shut down modules
			for (TestVectorShutdownModule module : shutdownList) {
				module.shutdown();
				System.out.println("Shutting down: "+module.getClass().getName());
			}
			
			Date dateFinish = new Date();
			now = new StringBuilder(dateformat.format(dateFinish));
			log.println("TestVector Process Finished At: " + now);
			System.out.println("TestVector Process Finished At: " + now);
			log.println("Total Extraction Time: "
					+ ((dateFinish.getTime() - dateBegin.getTime()) / 1000)
					+ "seconds");
			log.close();
		}

	}

	/**
	 * Runs an Answer Wrapping test vector creation session.
	 */
	public void windowsWrapAnswers(String unit, int value,
			boolean allowPartials, int partialPercent)
			throws FileNotFoundException {
		Date dateBegin = new Date();
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd[H-m-s]Z");
		StringBuilder now = new StringBuilder(dateformat.format(dateBegin));
		PrintWriter log = null;
		if (!isUseCustomOutputDir())
			outputDirectory = "TestVectors" + now;
		else
			outputDirectory = parseCustomDirectoryString(customOutputDirectoryString);
		new File(outputDirectory).mkdirs();

		log = new PrintWriter(new File(outputDirectory + "/Experiment.log"));
		PrintWriter pw = null;
		if (data != null) {

			log.println("TestVector Process Begun At: " + now);
			System.out.println("TestVector Process Begun At: " + now);
			log.println("Modules Run:");
			for (ExtractionModule em : moduleList)
				log.println(em.getName());
			log.println("Data Selection Query: " + query);
			log.println("Slice Unit: " + unit);
			log.println("Slice Value: " + value);
			log.println("Scan Window Wraps Answer Boundaries: True");
			log.println("Allow Partials: " + allowPartials);
			log.println("Total Users: " + data.size());
			log.println("Begin User List:");

			StringBuilder nameVector = new StringBuilder();
			// Put all Feature Names in a List.
			for (ExtractionModule em : moduleList) {
				if (suppressSystemOut)
					systemOutToNull(true);
				Collection<Feature> features = em.extract(data.getFirst());
				if (suppressSystemOut)
					systemOutToNull(false);
				if (features != null) {
					for (Feature f : features) {
						nameVector.append(f.getFeatureName() + "|");
					}
				} else {
					continue;
				}
			}
			nameVector.deleteCharAt(nameVector.length() - 1);

			double percent = Double.valueOf(partialPercent) / 100;
			int scanlength = 300000; // default 5minutes
			if (unit.equalsIgnoreCase("seconds")) {
				scanlength = value * 1000;
			} else if (unit.equalsIgnoreCase("minutes")) {
				scanlength = value * 1000 * 60;
			}

			for (DataNode dn : data) {
				System.out.println("Processing User: " + dn.getUserID());
				log.println(dn.getUserID());
				try {
					pw = new PrintWriter(new File(outputDirectory
							+ "/TestVectorsUser" + dn.getUserID() + ".txt"));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				pw.println(nameVector);

				LinkedList<GenericEvent> slice = new LinkedList<GenericEvent>();
				DataNode newDN = new DataNode(dn.getUserID());
				long sliceTime = scanlength;
				long sliceTimeRemaining = sliceTime;

				for (Answer ans : dn) {

					GenericEvent storeLast = null;
					GenericEvent storeSecondLast = null;

					if (ans.getLength() < sliceTimeRemaining) {
						newDN.add(ans);
						sliceTimeRemaining -= ans.getLength();
					} else {
						for (GenericEvent ge : ans.getEventList()) {

							slice.add(ge);

							if (slice.getLast().getWhen()
									- slice.getFirst().getWhen() > sliceTimeRemaining) {
								storeLast = slice.pollLast();
								storeSecondLast = slice.peekLast();
								Answer newAns = (Answer) ans.clone();
								newAns.setEventList(slice);
								newDN.add(newAns);
								slice.clear();
								slice.add(storeSecondLast);
								slice.add(storeLast);
								sliceTimeRemaining = sliceTime;
								StringBuilder featureVector = new StringBuilder();
								for (ExtractionModule em : moduleList) {
									if (suppressSystemOut)
										systemOutToNull(true);
									Collection<Feature> features = em
											.extract(newDN);
									if (suppressSystemOut)
										systemOutToNull(false);
									if (features != null) {
										for (Feature f : features) {
											featureVector.append(f.toVector()
													+ "|");
										}
									} else {
										continue;
									}
								}
								featureVector.deleteCharAt(featureVector
										.length() - 1);
								pw.println(featureVector);
								// System.out.println("<" + featureVector +
								// ">");
								newDN.clear();
							}
						}

						Answer leftOver = (Answer) ans.clone();
						leftOver.setEventList(slice);
						sliceTimeRemaining -= leftOver.getLength();
						slice.clear();
						newDN.add(leftOver);

					}

				}

				if (allowPartials && (newDN.size() > 0)) {
					long length = 0;
					for (Answer a : newDN) {
						length += a.getLength();
					}
					if (length > (scanlength * percent)) {
						StringBuilder featureVector = new StringBuilder();
						for (ExtractionModule em : moduleList) {
							if (suppressSystemOut)
								systemOutToNull(true);
							Collection<Feature> features = em.extract(newDN);
							if (suppressSystemOut)
								systemOutToNull(false);
							if (features != null) {
								for (Feature f : features) {
									featureVector.append(f.toVector() + "|");
								}
							} else {
							}
						}
						featureVector.deleteCharAt(featureVector.length() - 1);
						pw.println(featureVector);
						newDN.clear();
					}
				}

				pw.close();
			}
			Date dateFinish = new Date();
			now = new StringBuilder(dateformat.format(dateFinish));
			log.println("TestVector Process Finished At: " + now);
			System.out.println("TestVector Process Finished At: " + now);
			log.println("Total Extraction Time: "
					+ ((dateFinish.getTime() - dateBegin.getTime()) / 1000)
					+ "seconds");
			log.close();
		}

	}

	public boolean isUseCustomOutputDir() {
		return useCustomOutputDir;
	}

	public void setUseCustomOutputDir(boolean useCustomOutputDir) {
		this.useCustomOutputDir = useCustomOutputDir;
		if (!useCustomOutputDir) {
			this.setOutputDirectory(defaultOutputDir);
		}
	}

}
