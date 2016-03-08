package output.util;

import java.util.*;
import java.io.*;

public class AvailabilityPrune {

	protected LinkedHashMap<String, ArrayList<Integer>> templateValueCounts;
	protected LinkedHashMap<Integer, LinkedHashMap<String, ArrayList<Double>>> userTemplateValues; // <userID <Feature : values>>
	static LinkedHashMap<String,int[]> availableValues;
	private final String method; 
	static ArrayList<String> ignoredFeatures;
	static String classAttrib = null;
	final static int VAR_SIZE_THRESHOLD = 5;
	final static boolean TEST = false;

	// Constructor
	public AvailabilityPrune (String method) {
		templateValueCounts  = new LinkedHashMap<String, ArrayList<Integer>>();
		availableValues = new LinkedHashMap<String,int[]>();
		userTemplateValues = new LinkedHashMap<Integer, LinkedHashMap<String,ArrayList<Double>>>();
		switch(method) {
		case "-mean":
			this.method = "mean";
			break;
		case "-max":
			this.method = "max";
			break;
		case "-min":
			this.method = "min";
			break;
		case "-top":
			this.method = "top";
			break;
		case "-var":
			this.method = "var";
			break;
		case "-zscore":
			this.method = "zscore";
			break;
		case "-med":
			this.method = "med";
			break;
		default:
			throw new IllegalArgumentException("Invalid method arguement: " + method);
		}
	}

	/**
	 * usage:
	 * Explains how to use this (standalone) class
	 */
	public void usage () {
		System.out.println("Run with the following five arguments:");
		System.out.println("\tsourceTemplateDir");
		System.out.println("\ttargetTemplateDir");
		System.out.println("\tsourceTestVectorDir");
		System.out.println("\ttargetTestVectorDir");
		System.out.println("\tthreshold");
		System.out.println("\tPruningMethod [-max/-mean]");
		System.out.println("\t[optional] comma-seperated list of features to ignore");
		System.out.println("\t[optional] class attribute");
	}

	/*
	 * printCounts -- Only for debugging this class
	 */
	protected void printCounts () {
		Iterator<String> mapIterator = templateValueCounts.keySet().iterator();
		while (mapIterator.hasNext()) {
			String key = mapIterator.next();
			System.out.println(key + " :\t" + templateValueCounts.get(key));
		}
	}

	public static void main(String[] args) {
		AvailabilityPrune prune = new AvailabilityPrune(args[5]);
		//		if (args.length > 6) {
		//			ignoredFeatures = new ArrayList<String>(Arrays.asList(args[6].split("\\s*,\\s*")));
		//			classAttrib = args[7];
		//		}
		prune.pruneFeatures(args);
	}

	/**
	 * pruneFeatures
	 * @param args
	 * @return
	 */
	public boolean pruneFeatures(String [] args) {
		if (args.length >= 6) {
			return pruneFeatures (new File(args[0]), new File(args[1]), new File(args[2]), new File(args[3]), Double.parseDouble(args[4]));
		} else {
			usage();
			return false;
		}
	}

	// Read Template files to get max count across all files
	// Prune (remove from HashMap?) anything with less than the max count
	// For each file in the Template directory:
	// * Write a nearly identical Template file in a target directory
	// For each file in the TestVectors directory:
	// * Write a nearly identical TestVector file in a target directory
	public boolean pruneFeatures (File sourceTemplateDir, File targetTemplateDir,
			File sourceTestVectorDir, File targetTestVectorDir, double threshold) {
		boolean okay = true;
		//		System.out.println("Reading template files from " + sourceTemplateDir.getAbsolutePath());
		System.out.println("Reading template files from " + sourceTemplateDir.getName());
		// Read the template files from the sourceTemplateDir
		File[] fileList = sourceTemplateDir.listFiles();
		for (int i = 0; i < fileList.length; i++) {
			// Only take the files named "TemplateUser..."-something
			// (Do not, for example, take Experiment.log
			if (fileList[i].getName().startsWith("TemplateUser")) {
				okay = okay && getHeadersAndCounts(fileList[i]);
			}
		}
		// Remove any items less than the threshold from the "templateValues" HashMap
		eliminateCountsLessThan(threshold);
		if (!TEST) {
			// Re-read the (source) template directory and create an almost identical set of files
			System.out.println("Producing trimmed template files to " + targetTemplateDir.getName());
			for (int userID : userTemplateValues.keySet()) {
				okay = okay && outputTemplateFile(userID,targetTemplateDir);
			}
			// Read the (source) test vectors and create an almost identical set of files
			//		System.out.println("Producing trimmed test vector files from " + sourceTestVectorDir.getAbsolutePath() + " to " + targetTestVectorDir.getAbsolutePath());
			System.out.println("Producing trimmed test vector files from " + 
					sourceTestVectorDir.getName() + " to " + targetTestVectorDir.getName());
			fileList = sourceTestVectorDir.listFiles();
			for (int i = 0; i < fileList.length; i++) {
				if (fileList[i].getName().startsWith("TestVectorsUser")) {
					okay = okay && outputTestVectorFile(fileList[i], targetTestVectorDir);
				}
			}
		}
		else {
			runTests();
		}
		if (okay) {
			System.out.println("Happiness");
			printAvailabilityAnalysis();
		}
		else {
			System.out.println("Failed somewhere...");
		}
		return okay;
	}

	protected boolean getHeadersAndCounts (File file) {
		int userID = Integer.parseInt(file.getName().split("TemplateUser")[1].replaceFirst(".txt",""));
		if (! userTemplateValues.containsKey(userID))
			userTemplateValues.put(userID, new LinkedHashMap<String,ArrayList<Double>>());

		String line;
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			while ((line = br.readLine()) != null) {
				// Each line has a format of LABEL: [value[,value]*]
				// Pick up the label -- everything before the ":" character
				String [] keyVal = line.split(":");
				// Pick up the COUNT of the values following the label and ":" character
				int valCount = 0;
//				String[] values = null;
				ArrayList<Double> dblValues = new ArrayList<Double>();
				try {
					String[] tempStrValues = keyVal[1].split(",");
					dblValues = strArrayToDblArray(tempStrValues);
//					System.out.println(Arrays.toString(values)+" "+values.length);
					valCount = dblValues.size();
				} catch (Exception e) {
					valCount = 0;
				}

				//add new key to templateValues, if it does not exist
				if (! templateValueCounts.containsKey(keyVal[0]))
					templateValueCounts.put(keyVal[0], new ArrayList<Integer>());
				//add new value count to maps
				templateValueCounts.get(keyVal[0]).add(valCount);

				//add new feature mapping to userTemplateValues.get(userID)
				userTemplateValues.get(userID).put(keyVal[0],dblValues);

				//place SubjAnswID in 1st position
				//necessary for merging arffs
				if (templateValueCounts.containsKey("SubjAnswID")) {
					LinkedHashMap<String, ArrayList<Integer>> templateValueCountsCopy = new LinkedHashMap<>(templateValueCounts);
					templateValueCounts.keySet().retainAll(Collections.singleton("SubjAnswID"));
					templateValueCounts.putAll(templateValueCountsCopy);

					LinkedHashMap<Integer, LinkedHashMap<String, ArrayList<Double>>> userTemplateValuesCopy = new LinkedHashMap(userTemplateValues);
					userTemplateValues.keySet().retainAll(Collections.singleton("SubjAnswID"));
					userTemplateValues.putAll(userTemplateValuesCopy);
				}
			}
			br.close();
			return true;
		} catch (IOException ex) {
			return false;
		}
	}

	/*
	 * eliminateCountsLessThan
	 * @param threshold -- any integer, but only positive numbers (greater than 1) are useful.
	 */
	protected void eliminateCountsLessThan(double threshold) {
		// Now that all the keys are read, eliminate anything less than the threshold
		Iterator<String> templateValCountsIterator = templateValueCounts.keySet().iterator();
		Map<Integer,ArrayList<String>> valuesMap = new TreeMap<Integer,ArrayList<String>>();
		Vector<String> keysToEliminate = new Vector<String>();

		while (templateValCountsIterator.hasNext()) {
			String key = templateValCountsIterator.next();
			//if (ignoredFeatures != null && !ignoredFeatures.contains(key)) {
			ArrayList<Integer> values = templateValueCounts.get(key);

			double comparisonValue; //either mean or max
			switch (method) {
			case "top":
				int sum = 0;
				for (int i : values) {
					sum += i;
				}
				if (valuesMap.containsKey(sum))
					valuesMap.get(sum).add(key);
				else {
					ArrayList<String> keyList = new ArrayList<String>();
					keyList.add(key);
					valuesMap.put(sum, keyList);
				}
				break;
			case "mean":
				comparisonValue = mean(values);
				if (comparisonValue <= threshold) {
					// The key has a count less than the threshold, so mark it for elimination
					// Can't eliminate directory while iterating over the same container, so store it in for later
					keysToEliminate.add(key);
				}
				break;
			case "med":
				comparisonValue = median(values);
				if (comparisonValue <= threshold) {
					// The key has a count less than the threshold, so mark it for elimination
					// Can't eliminate directory while iterating over the same container, so store it in for later
					keysToEliminate.add(key);
				}
				break;
			case "max":
				comparisonValue = Collections.max(values);
//				System.out.println("key: "+key+" compVal: "+comparisonValue);
				if (comparisonValue <= threshold) {
					// The key has a count less than the threshold, so mark it for elimination
					// Can't eliminate directory while iterating over the same container, so store it in for later
					keysToEliminate.add(key);
				}
				break;
			case "min":
				comparisonValue = Collections.min(values);
				int totalUsers = userTemplateValues.size();
//				System.out.println("key: "+key+" compVal: "+comparisonValue);
				// if there are less values than total users, this means that at least one
				// user had no observations of this feature. Therefore, eliminate it.
				if (comparisonValue <= threshold || values.size() < totalUsers) {
					// The key has a count less than the threshold, so mark it for elimination
					// Can't eliminate directory while iterating over the same container, so store it in for later
					keysToEliminate.add(key);
				}
				break;
			case "var":
				double meanVariance = meanVarianceAcrossUsers(key);
				if (Double.isNaN(meanVariance) || meanVariance > threshold) {
					keysToEliminate.add(key);
				}
				break;
			case "zscore":
				double meanZScore = meanZScoreOfFeature(key);
				if (Double.isNaN(meanZScore) || meanZScore > threshold) {
					keysToEliminate.add(key);
				}
				break;
			default:
				comparisonValue = Double.NaN;
				throw new IllegalArgumentException("Invalid list for key: " + key);
			}
			//				} //ignored features loop
		}
		// Remove from the templateValues anything living in keysToEliminate
		/**
		 * Iterate through map valuesMap, which stores the sum of all observations as
		 * the key, and a list of key names as the value, e.g. <3, <"I_Hold","T_Hold",...>>
		 * 
		 * Loop through this map and eliminate any keys whose ranking is below the
		 * user-defined threshold
		 */
		if (method == "top") {
			int numKeysToEliminate = templateValueCounts.size() - (int)threshold;
			int keysEliminated = 0;
			outerloop:
			for (Map.Entry<Integer,ArrayList<String>> entry : valuesMap.entrySet()) {
				for (String valueName : entry.getValue()) {
					templateValueCounts.remove(valueName);
					keysEliminated++;
					if (keysEliminated == numKeysToEliminate)
						break outerloop;
				}
			}
		}
		else {
			for (int i = 0; i < keysToEliminate.size(); i++) {
				// System.out.println("Eliminating: " + keysToEliminate.get(i) + " = " + templateValues.get(keysToEliminate.get(i)));
				templateValueCounts.remove(keysToEliminate.get(i));
			}
		}
	}

	/*
	 * outputTemplateFile
	 * Reads one template file and writes a possibly trimmed version to the target directory
	 * @param sourceFile
	 * @param targetTemplateDir
	 * @return true when successful; false if unsuccessful
	 */
	protected boolean outputTemplateFile(int userID, File targetTemplateDir) {
//		System.out.println("Processing Template for: "+userID);
		BufferedWriter writer = null;
		try {
			LinkedHashMap<String,ArrayList<Double>> userMap = userTemplateValues.get(userID);
			// Create the directory if it doesn't already exist
			if (! targetTemplateDir.exists()) {
				targetTemplateDir.mkdir();
			}
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetTemplateDir.getPath() + 
					File.separatorChar + "TemplateUser"+userID+".txt")));

			for (String featureName : templateValueCounts.keySet()){
				
				if (userMap.containsKey(featureName)) {
					String values = dblArrayToString(userMap.get(featureName));
//							Arrays.toString(userMap.get(featureName)).
//							replace("[","").replace("]","").replace(" ","").replace("null", "");
					writer.write(featureName+": "+values+"\n");
//					System.out.println(featureName+": "+values);
					//TODO
				}
				else {
					writer.write(featureName+": \n");
				}
			}
			writer.close();
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	protected boolean outputTestVectorFile(File sourceFile, File targetTestVectorDir) {
//				System.out.println("Processing "+sourceFile.getPath());
		try {
			if (! targetTestVectorDir.exists()) {
				targetTestVectorDir.mkdir();
			}
			BufferedReader reader = new BufferedReader(new FileReader(sourceFile));
			Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetTestVectorDir.getPath() + File.separatorChar + sourceFile.getName())));

			//read source file --> create mapping HashMap<FeatureName,LinkedList<ValueStrings>>
			LinkedHashMap<String,LinkedList<String>> userVectorMap = new LinkedHashMap<String,LinkedList<String>>();
			String fieldNamesLine = reader.readLine();
			if (fieldNamesLine == null) {
				reader.close();
				writer.close();
				return false;
			}
			String [] fields = fieldNamesLine.split("\\|");
			LinkedHashMap<String,Integer> fieldIndex = new LinkedHashMap<String,Integer>();
			//populate userVectorMap headers
			for (int i = 0; i < fields.length; i++) {
				//System.out.println("Adding to vectorMap: "+fields[1]);
				userVectorMap.put(fields[i],new LinkedList<String>());
				fieldIndex.put(fields[i],i);
			}
			String singleScanValues = "";
			int userScans = 0;
			while ((singleScanValues = reader.readLine()) != null) {
//				System.out.println("Reading scan "+userScans+" "+sourceFile.getName());
				String [] values = singleScanValues.split("\\|",fields.length*-1);
//				System.out.println("Values size: "+values.length+" vectorMap size: "+userVectorMap.size());
				for (String feature : userVectorMap.keySet()) {
//					System.out.println(fieldIndex.get(feature)+" "+values[fieldIndex.get(feature)]);
					//TODO: Change to accomodate doubles
					userVectorMap.get(feature).add(values[fieldIndex.get(feature)].
							replace("[","").replace("]","").replace(" ","").replace("null", ""));
				}
				userScans++;
			}
			//loop through templateValueCounts (eliminated columns have already been removed)
			//print headers
			String headerString = "";
			for (String feature : templateValueCounts.keySet()) {
				if (!feature.equals(classAttrib))
					headerString += (feature+"|");
			}
			if (classAttrib != null)
				headerString += (classAttrib+"|");
			//			System.out.println("header string: "+headerString);
			writer.write(headerString.substring(0,headerString.length()-1)+"\n"); //don't print last "|"
			//for each scan, print row
			for (int i = 0; i < userScans; i++) {
				String fieldValueString = "";
				for (String feature : templateValueCounts.keySet()) {
					if (!feature.equals(classAttrib)) {
						//System.out.println("Looking for feature: "+feature);
						if (userVectorMap.containsKey(feature)) { 
							//System.out.println("Found it! "+userVectorMap.get(feature).get(i));
							fieldValueString += (userVectorMap.get(feature).get(i));
						}
						fieldValueString += "|";
					}
				}
				if (classAttrib != null) {
					fieldValueString += (userVectorMap.get(classAttrib).get(i)+"|");
				}

				writer.write(fieldValueString.substring(0, fieldValueString.length()-1)+"\n"); //don't print final "|"
			}

			writer.close();
			reader.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public void runTests() {
		for (String fName : templateValueCounts.keySet()) {
			System.out.println(fName+","+templateValueCounts.get(fName).size()+","+
								meanVarianceAcrossUsers(fName));
		}
//		String csvFile = "availabilityAnalysis.csv";
//		BufferedWriter csv = null;
//		try {
//			csv = new BufferedWriter(new FileWriter(csvFile,true));
//			for (Map.Entry<Integer, LinkedHashMap<String, ArrayList<Double>>> userEntry : userTemplateValues.entrySet()) {
//				int userID = userEntry.getKey();
//				for (Map.Entry<String, ArrayList<Double>> featureEntry : userTemplateValues.get(userID).entrySet()) {
//					int size = featureEntry.getValue().size();
//					double var = variance(featureEntry.getValue());
//					if (size != 0 && var != 0.0) {
//						csv.write(Integer.toString(userID));
//						csv.write(",");
//						csv.write(featureEntry.getKey());
//						csv.write(",");
//						csv.write(Integer.toString(size));
//						csv.write(",");
//						csv.write(Double.toString(mean(featureEntry.getValue())));
//						csv.write(",");
//						csv.write(Double.toString(var));
//						csv.write("\n");
//					}
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (csv != null) {
//				try {
//					csv.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
	}
	
	public void printAvailabilityAnalysis() {
		Set<String> availableFeatureNames = templateValueCounts.keySet();
		int uniqueFeatureCount = availableFeatureNames.size();
		int totalObservations = 0;
		//		double emptyFeatureTotalCount = 0.0;

		for (String feature : availableFeatureNames) {

			//			String[] ngram = feature.split("_");
			//			System.out.println("p("+feature+"): "+featureProbability);

			ArrayList<Integer> featureCounts = templateValueCounts.get(feature);

			for (Integer featureCount : featureCounts)
				totalObservations += featureCount;
			//			double emptyFeatures = values[1];
			//			emptyFeatureTotalCount += emptyFeatures;
		}

		System.out.format("Available Features: \t %d %n",uniqueFeatureCount);
		System.out.format("Observations: \t %d %n",totalObservations);
		//		System.out.format("Empty Features: \t %f %n",emptyFeatureTotalCount);
		System.out.println();

	}

	private double mean(Collection<? extends Number> values) {
		int length = values.size();
		double sum = 0.0;
		for (Number value : values) {
			if (value instanceof Double)
				sum += (double) value;
			if (value instanceof Integer)
				sum += (int) value;
			if (value instanceof Long)
				sum += value.doubleValue();
		}
		return (sum*1.0)/length;
	}
	
	private double median(ArrayList<Integer> values) {
		Collections.sort(values);
		int middle = values.size()/2;
		if (values.size()%2 == 1) {
			return values.get(middle);
		}
		else {
			return (values.get(middle) + values.get(middle-1)) / 2.0;
		}
	}
	
	private double variance(ArrayList<? extends Number> values) {
		double mean = mean(values);
		double temp = 0.0;
		for (Number value : values)
			temp += (mean-value.doubleValue())*(mean-value.doubleValue());
		return temp/values.size();
	}

	private static class EntryComparator implements Comparator<Map.Entry<String, ArrayList<Integer>>> {
		public int compare(Map.Entry<String, ArrayList<Integer>> left,
				Map.Entry<String, ArrayList<Integer>> right) {     
			// Right then left to get a descending order
			return Integer.compare(right.getValue().size(), left.getValue().size());
		}
	}
	
	/**
	 * Converts an array of Strings to an equal length array
	 * of doubles
	 * @param strArray
	 * @return
	 */
	private ArrayList<Double> strArrayToDblArray(String[] strArray) {
		ArrayList<Double> returnArray = new ArrayList<Double>();
		for (String s : strArray)
			returnArray.add(Double.parseDouble(s));
		return returnArray;
	}
	
	private String dblArrayToString(ArrayList<Double> dblArr) {
		StringBuilder retStr = new StringBuilder();
		for (Double d : dblArr) {
			retStr.append(Double.toString(d));
			retStr.append(",");
		}
		return dblArr.size()>0? retStr.toString().substring(0, retStr.length()-1) : retStr.toString();
	}
	
	private double meanVarianceAcrossUsers(String featureName) {
		int totalValueSets = 0;
		double totalVariance = 0.0;
		for (LinkedHashMap<String, ArrayList<Double>> userMap : userTemplateValues.values()) {
			if (userMap.containsKey(featureName)) {
				ArrayList<Double> values = userMap.get(featureName);
				if (values.size() > VAR_SIZE_THRESHOLD) {
					totalVariance += variance(values);
					totalValueSets++;				
				}
			}
		}
		System.out.println(featureName+","+totalValueSets+","+totalVariance/totalValueSets);
		return totalVariance/totalValueSets;
	}
	
	private double meanZScoreOfFeature(String featureName) {
		double overallCumulativeMeanZScore = 0.0;
		int totalUsers = 0;
		for (LinkedHashMap<String, ArrayList<Double>> userMap : userTemplateValues.values()) {
			double cumulativeUserZScore = 0.0;
			int totalUserObservations = 0;
			if (userMap.containsKey(featureName)) {
				ArrayList<Double> values = userMap.get(featureName);
				if (values.size() > VAR_SIZE_THRESHOLD) {
					double mean = mean(values);
					double sd = Math.sqrt(variance(values));
					for (double d : values) {
						double absZScore = (Math.abs(d-mean))/sd;
						cumulativeUserZScore += absZScore;
						totalUserObservations++;
					}
					overallCumulativeMeanZScore += (cumulativeUserZScore/totalUserObservations);
					totalUsers++;
				}
			}
		}
//		System.out.println(featureName+","+totalUsers+","+overallCumulativeMeanZScore/totalUsers);
		return overallCumulativeMeanZScore/totalUsers;
	}

}
