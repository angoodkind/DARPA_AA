package output.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import extractors.data.Feature;
import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.ProtectedProperties;
import weka.core.converters.ArffSaver;

/**
 * VectorToArffConverter is a utility class that has the responsibility of converting a TestVector file
 * to a feature vector in arff format.
 */
public class VectorToWekaConverter extends TestVectorProcessor {
	int min_count;  // the minimum number of observations (per vector) to be used in calculations.
	double null_tolerance;
	// if there are fewer than this rate of valid attributes across the full data set, ignore the feature entirely.

	boolean[] null_variable = null;  // the number of null observations for each variable.
	int[] null_attr_count = null;
	// the number of null observations for each attribute (multiple variables can be generated per attribute).
	int count;  // the total number of processed data points.

	ArrayList<Object[]> data_matrix; // a N-x-kt matrix of data points by feature values.

	HashSet<Integer> nominal_indices;  // a set of indices of attributes which should be nominal.
	ArrayList<Aggregator> aggregators;  // a list of aggregators to run.
	public Classifier classifier; //classifier to use in constructing external file


	/**
	 * Calculates and stores aggregations of features.
	 * <p/>
	 * These aggregations will ultimately be written to the arff files.
	 *
	 * @param data an array of attribute values
	 */
	protected void processData(String[] data) {
		count++;
		//adding +1 for class attribute, which is not included in nominal_indices
		int size = nominal_indices.size()+1 + (data.length - (nominal_indices.size()+1)) * aggregators.size();
//		System.out.println("Data length: "+data.length+", size: "+size);
		if (null_attr_count == null) {
			null_attr_count = new int[attr_names.length];
		}
		Object[] vector = new Object[size];
		for (int i = 0, j = 0; i < data.length; ++i) {
			if (nominal_indices.contains(i) || i == data.length-1 /**i is class attrib**/) {
				vector[j++] = data[i];
			} else {
				Object[] observations = new Object[0];
				try {
					observations = convertToObservations(data[i]);
				} catch (TestVectorException e) {
					System.err.println("Uncaught nominal index:" + i + " - " + attr_names[i]);
					System.exit(0);
				}

				for (Aggregator a : aggregators) {
					Object x = "?";
					if (observations != null && observations.length > 0 /* min_count */) {
						x = a.aggregate(observations);
					} else {
						null_attr_count[i]++;
					}
					vector[j++] = x;
				}
			}
		}
		data_matrix.add(vector);
	}

	/**
	 * Constructs a converter aggregators, and thresholds on the number of observations
	 * necessary for aggregation and the acceptable rates of missing values.
	 * <p/>
	 * Acceptable aggregator labels are "mean", "median", "count", "mse" and "sd".
	 *
	 * @param aggregators    a list of aggregator labels
	 * @param min_count      the minimum number of observations to aggregate
	 * @param null_tolerance the rate of null values that is tolerated to include an attribute (CURRENTLY UNSUPPORTED)
	 */
	public VectorToWekaConverter(String[] aggregators, int min_count, double null_tolerance,
			String nominal_indices) {
		this.aggregators = new ArrayList<>();
		for (String s : aggregators) {
			switch (s) {
			case "mean":
				this.aggregators.add(new MeanAggregator());
				break;
			case "median":
				this.aggregators.add(new MedianAggregator());
				break;
			case "count":
				this.aggregators.add(new CountAggregator());
				break;
			case "mse":
				this.aggregators.add(new MseAggregator());
				break;
			case "sd":
				this.aggregators.add(new StdDevAggregator());
				break;
			}
		}

		this.min_count = min_count;
		this.null_tolerance = null_tolerance;

		this.attr_names = null;
		this.data_matrix = new ArrayList<>();
		this.nominal_indices = new HashSet<>();
		this.count = 0;

		setNominalIndices(nominal_indices);
	}

	/**
	 * Assigns the set of attributes that should be treated as nominal.
	 *
	 * @param index_string a comma separated list of indices.
	 */
	public void setNominalIndices(String index_string) {
		String[] indices = index_string.split(",");
		for (String s : indices) {
			if (s.length() > 0) {
				this.nominal_indices.add(Integer.parseInt(s));
			}
		}
	}

	/**
	 * sets the null_tolerance 
	 * @author agoodkind
	 * @param null_tolerance
	 */
	public void setNullTolerance(double null_tolerance) {
		this.null_tolerance = null_tolerance;
	}

	/**
	 * Constructs a default converter with all aggregations, no minimum counts, and no variable elimination due to null
	 * counts.
	 */
	public VectorToWekaConverter() {
		this(new String[]{"mean", "median", "count", "mse", "sd"}, 0, 0.0, "");
	}

	/**
	 * Write an <code>Instances</code> object from specified TestVector
	 * directory
	 * 
	 *  @param filename	Directory of TestVectors
	 *  @author Adam Goodkind
	 */
	public Instances vectorToInstances(String filename) {
		try {
			loadAllFilesInDirectory(filename);
		} catch (TestVectorException e) {e.printStackTrace();}
		System.out.println("All files loaded...");
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();

		// Assume that everything starts off null
		//adding +1 for class attribute, which is not included in nominal_indices
		int size = nominal_indices.size()+1 + (data_matrix.get(0).length - (nominal_indices.size()+1)) * aggregators.size();
		System.out.println("data_matrix.get(0).length: "+data_matrix.get(0).length+", size: "+size);
		null_variable = new boolean[size];
		for (int i = 0; i < size; ++i)
			null_variable[i] = true;

		int variable_idx = 0;
		// write attributes
		for (int i = 0; i < attr_names.length; ++i) {
//			System.out.println("Checking attr: "+attr_names[i]+" #: "+i);
//			System.out.println("null_attr_count[i] * 1. "+(null_attr_count[i] * 1.)+" count: "+count+" null tolerance: "+null_tolerance);
			if (null_attr_count[i] * 1. / count <= 1 - null_tolerance) {
//				System.out.println("Including attr: "+attr_names[i]);
				// only use attributes where the rate of null attributes is less than the null tolerance
				if (nominal_indices.contains(i) || i == attr_names.length-1 /** i is class attrib**/) {
					List<String> nominal_values = Arrays.asList(constructNominalValues(variable_idx)); 
					attributes.add(new Attribute(attr_names[i].replaceAll(" ", "_").replaceAll("\\*", "_"),nominal_values));

					null_variable[variable_idx] = false;
					variable_idx++;
				} else {
					for (Aggregator a : aggregators) {
						attributes.add(new Attribute(attr_names[i].replaceAll(" ", "_").
								replaceAll("\\*", "_") + "_" + a.getLabel()));
						null_variable[variable_idx] = false;
						variable_idx++;
					}
				}
			} else {
				if (nominal_indices.contains(i) || i == attr_names.length-1 /** i is class attrib**/) {
					variable_idx++;
				} else {
					for (Aggregator a : aggregators) {
						variable_idx++;
					}
				}
			}
		}
		System.out.println("Attributes loaded...");
		//TEST PRINT//
//		System.out.println("Printing attributes\n");
//		  for (Attribute a : attributes)
//			  System.out.println(a.name());
		Instances instancesData = new Instances("TestRelation",attributes,attributes.size()/**CHANGE ME!**/);
		instancesData.setClassIndex(attributes.size()-1);
		
		// write data
		int line_count = 1;
		System.out.println("Processing data matrix, size "+data_matrix.size());
		for (Object[] d : data_matrix) {
//			System.out.println("Processing line "+line_count+++" of data_matrix, size:"+data_matrix.size());
			Instance instance = new DenseInstance(attributes.size()); //d.length
			int attr_idx = 0; //keep track of non-nulls
			for (int i = 0; i < d.length; ++i) {
//				System.out.println("d[i] "+d[i]+" i: "+i+" d.len: "+d.length+" attr: "+attr_idx+" attr.len: "+attributes.size());
//				System.out.println("Instance length: "+instance.numAttributes());
				if (!null_variable[i]) {
//					System.out.println(d[i]+" "+d[i].getClass().getName()+" "+i+" "+attributes.size());
					if (d[i] instanceof Double)
						instance.setValue(attributes.get(attr_idx), (double) d[i]);
					else if (d[i] instanceof String)
						if (!d[i].equals("?"))
							instance.setValue(attributes.get(attr_idx), (String) d[i]);
						else
							instance.setValue(attributes.get(attr_idx), Double.NaN);
					else if (d[i] instanceof Integer)
						instance.setValue(attributes.get(attr_idx), (int) d[i]);
					else
						instance.setValue(attributes.get(attr_idx), (double) d[i]);
					attr_idx++;
				}
			}
//			System.out.println("Printing instance\n"+instance);
			instancesData.add(instance);
		}
//		System.out.println("Printing instanceData\n"+instancesData);
		System.out.println("Vector Dir loaded to Instances...");
		return instancesData;
	}

	/**
	 * Creates a serialized Weka Instances & Classifier file from an
	 * Instances object 
	 * @param instances
	 * @param filename
	 */
	public void createClassifierFile(Instances instances,String clsFilename) {
		try {
			System.out.println("Creating classifier file...");
			Object[] toFile = new Object[2];
			toFile[0] = instances;
			classifier.buildClassifier(instances);
			System.out.println("Classifier built...");
			toFile[1] = classifier;
			weka.core.SerializationHelper.writeAll(clsFilename+".cls", toFile);
//			weka.core.SerializationHelper.write(clsFilename+".cls", classifier);
		} catch (Exception e) {e.printStackTrace();}
	}

	/**
	 * Converts a preprocessed directory of TestVectors (run
	 * loadAllFilesInDirectory), and converts to a serialized
	 * file
	 * @param filename - where to save classifier
	 */
	public void vectorToClassifierFile(String testVectorFilename, String classifierName) {
		Instances instances = vectorToInstances(testVectorFilename);
		System.out.println("Instances object created...");
		createClassifierFile(instances,(testVectorFilename+classifierName));
	}

	/**
	 * Writes an arff representation of the data to the specified file.
	 * @param vectorDir
	 */
	public void vectorDirToArff(String vectorDir, String arffFileName) {
		try {
			Instances instances = vectorToInstances(vectorDir);
//			System.out.println(instances);
			ArffSaver saver = new ArffSaver();
			saver.setInstances(instances);
			saver.setFile(new File(arffFileName));
			saver.writeBatch();
		} catch (IOException e) {e.printStackTrace();}
	}

//	/**
//	 * Writes an arff representation of the data to the specified file.
//	 * @deprecated Use vectorDirToArff
//	 * @param filename the filename.
//	 */
//	@Deprecated
//	public void writeArff(String filename) throws IOException {
//		BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
//
//		writer.write("@relation VectorToArffConverter\n\n");
//
//		// Assume that everything starts off null
//		int size = nominal_indices.size() + (data_matrix.get(0).length - nominal_indices.size()) * aggregators.size();
//		null_variable = new boolean[size];
//		for (int i = 0; i < size; ++i)
//			null_variable[i] = true;
//
//		int variable_idx = 0;
//		// write attributes
//		for (int i = 0; i < attr_names.length; ++i) {
//			if (null_attr_count[i] * 1. / count <= 1 - null_tolerance) {
//				//    	  System.out.println("Including attr: "+attr_names[i]);
//				// only use attributes where the rate of null attributes is less than the null tolerance
//				if (nominal_indices.contains(i)) {
//					String[] nominal_values = constructNominalValues(variable_idx);
//					writer.write("@attribute " + attr_names[i].replaceAll(" ", "_").replaceAll("\\*", "_") + " {" +
//							join(nominal_values, ",") + "}\n");
//					null_variable[variable_idx] = false;
//					variable_idx++;
//				} else {
//					for (Aggregator a : aggregators) {
//						writer.write(
//								"@attribute " + attr_names[i].replaceAll(" ", "_").replaceAll("\\*", "_") + "_" + a.getLabel() +
//								" numeric\n");
//						null_variable[variable_idx] = false;
//						variable_idx++;
//					}
//				}
//			} else {
//				if (nominal_indices.contains(i)) {
//					variable_idx++;
//				} else {
//					for (Aggregator a : aggregators) {
//						variable_idx++;
//					}
//				}
//			}
//		}
//		writer.write("\n@data\n");
//
//		// write data
//		for (Object[] d : data_matrix) {
//			boolean first = true;
//			for (int i = 0; i < d.length; ++i) {
//				if (!null_variable[i]) {
//					// Support for null tolerance
//					if (!first) {
//						writer.write(",");
//					}
//					writer.write(d[i].toString());
//					first = false;
//				}
//			}
//			writer.write("\n");
//		}
//
//		writer.close();
//	}

	/**
	 * Construct nominal values for variable index i
	 *
	 * @param i the variable index
	 * @return a string representing the nominal values
	 */
	private String[] constructNominalValues(int i) {
		HashSet<String> values = new HashSet<>();
		for (Object[] data : data_matrix) {
			values.add(data[i].toString());
		}
		return values.toArray(new String[values.size()]);
	}

	/**
	 * Converts a comma separated set of numbers expressed in a string to an array of doubles.
	 *
	 * @param s the string
	 * @return an array of doubles.
	 */
	private Object[] convertToObservations(String s) throws TestVectorException {
		if (s == null || s.length() == 0) {
			return null;
		}
		String[] split = s.trim().replaceAll("\\[", "").replaceAll("\\]", "").split(",");
		Object[] ret = new Object[split.length];
		for (int i = 0; i < ret.length; ++i) {
			try {
				if (split[i].length() > 0) {
					ret[i] = Double.parseDouble(split[i]);
				}
			} catch (NumberFormatException e) {
				throw new TestVectorException("nominal values");
			}
		}
		return ret;
	}

	/**
	 * Joins an array into a string separated by a delimiter.
	 *
	 * @param data      an array of doubles.
	 * @param delimiter a delimiter.
	 * @return a delimited string of data.
	 */
	private String join(Object[] data, String delimiter) {
		StringBuilder sb = new StringBuilder();
		if (data == null || data.length == 0) {
			return "";
		}
		sb.append(data[0]);
		for (int i = 1; i < data.length; ++i) {
			sb.append(delimiter);
			if (data[i] instanceof Double && Double.isNaN((Double) data[i])) {
				sb.append("?");
			} else {
				sb.append(data[i].toString());
			}
		}
		return sb.toString();
	}

	public static class MeanAggregator implements Aggregator {
		@Override
		public double aggregate(Object[] list) {
			double sum = 0;
			for (Object d : list) {
				if (d instanceof Double) {
					sum += (double) d;
				}
			}
			return sum / list.length;
		}
		
		@Override
		public double aggregate(Feature feature) {
			double sum = 0;
			for (Object d : feature.getFeatureValues()) {
				if (d instanceof Double)
					sum += (double) d;
				else if (d instanceof Integer)
					sum += (int) d;
					
			}
			return sum / feature.getFeatureValues().size();
		}

		@Override
		public String getLabel() {
			return "mean";
		}
	}

	public static class MedianAggregator implements Aggregator {
		@Override
		public double aggregate(Object[] list) {
			if (list == null) {
				return Double.NaN;
			}
			if (!(list[0] instanceof Double)) {
				return -1;
			}
			Arrays.sort(list);

			if (list.length % 2 == 1) {  // odd length array.
				return (Double) list[list.length / 2];
			} else {
				return ((Double) list[list.length / 2] + (Double) list[list.length / 2 - 1]) / 2;
			}
		}
		
		@Override
		public double aggregate(Feature feature) {
			if (feature.getFeatureValues().size() == 0)
				return Double.NaN;
			
			if (!(feature.getFeatureValues().get(0) instanceof Double))
				return -1;
			
			LinkedList<Double> values = new LinkedList<Double>();
			for (Object d : feature.getFeatureValues()) {
				if (d instanceof Double)
					values.add((Double)d);
			}
			Collections.sort(values);
			
			if (values.size() % 2 == 1) { //odd length
				return (Double) values.get(values.size()/2);
			} else {
				return ((Double) values.get(values.size()/2) + (Double) values.get(values.size()/2-1))/2;
			}
		}

		@Override
		public String getLabel() {
			return "median";
		}
	}

	public static class CountAggregator implements Aggregator {
		@Override
		public double aggregate(Object[] list) {
			return list.length;
		}
		
		@Override
		public double aggregate(Feature feature) {
			return feature.getFeatureValues().size();
		}

		@Override
		public String getLabel() {
			return "count";
		}
	}

	public static class MseAggregator implements Aggregator {
		@Override
		public double aggregate(Object[] list) {
			if (list.length < 2)
				return Double.NaN;
			else {
				double mse = 0;
				double mean = aggMean(list);
				for (Object value : list)
					if (value instanceof Double)
						mse += Math.pow((Double)value-mean,2);
				mse /= list.length;
				return Math.sqrt(mse);
			}
		}
		
		@Override
		public double aggregate(Feature feature) {
			if (feature.getFeatureValues().size() < 2)
				return Double.NaN;
			else {
				LinkedList<Double> values = new LinkedList<Double>();
				for (Object d : feature.getFeatureValues()) {
					if (d instanceof Double)
						values.add((Double)d);
				}
				double mse = 0;
				double mean = aggMean(values);
				for (Double value : values) {
					mse += Math.pow(value-mean,2);
				}
				mse /= values.size();
				return Math.sqrt(mse);
			}
		}

		@Override
		public String getLabel() {
			return "mse";
		}
	}

	public static class StdDevAggregator implements Aggregator {
		@Override
		public double aggregate(Object[] list) {
			if (list.length < 2)
				return Double.NaN;
			else {
				return Math.sqrt(aggVariance(list));	
			}
		}
		
		@Override
		public double aggregate(Feature feature) {
			if (feature.getFeatureValues().size() < 2)
				return Double.NaN;
			else {
				LinkedList<Double> values = new LinkedList<Double>();
				for (Object d : feature.getFeatureValues())
					if (d instanceof Double)
						values.add((Double)d);
				return Math.sqrt(aggVariance(values));
			}
		}

		@Override
		public String getLabel() {
			return "sd";
		}
	}

	protected static double aggVariance(Object[] values) {
		double avg = aggMean(values);
		double sum = 0.0;
		for (Object value : values) {
			if (value instanceof Double)
				sum += ((Double) value - avg) * ((Double) value - avg);
		}
		return sum / (values.length - 1);
	}

	public static double aggVariance(LinkedList<Double> values) {
		double avg = aggMean(values);
		double sum = 0.0;
		for (Double d : values)
			if (d instanceof Double)
				sum += ((Double) d - avg) * ((Double) d - avg);
		return sum / (values.size()-1);
	}

	public static double aggMean(LinkedList<Double> values) {
		double sum = 0.0;
		for (Double value : values)
			if (value instanceof Double)
				sum += value;
		return sum / values.size();
	}   

	protected static double aggMean(Object[] values) {
		double sum = 0.0;
		for (Object value : values) {
			if (value instanceof Double)
				sum += (Double) value;
		}
		return sum / values.length;
	}

}
