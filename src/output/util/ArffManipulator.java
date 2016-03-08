/**
 * 
 */
package output.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

/**
 * @author dbrizan
 *
 */

// TODO: Check whether the following classes should be consolidated:
// 1) CalculateExpectedValues.java
// 2) ExpectedValueCalculator.java

// TODO: Should this extend TestVectorProcessor.java

// TODO: Also check if ProcessWekaPipeline.java should have some additional features (such as the classifier)

/**
 * ArffManipulator
 * Used to manipulate all sorts of ARFF files.
 * @author dbrizan
 *
 */
public class ArffManipulator {
	
	protected String inputFileName;
	protected String outputFileName;
	protected boolean safe;
	
	protected Instances data;
	
	
	/**
	 * Constructor reads the input ARFF file immediately and saves the name of the output ARFF file for
	 * later writing.
	 * @param input name of input ARFF file.
	 * @param output name of output ARFF file.
	 * @throws java.lang.Exception
	 */
	public ArffManipulator(String input, String output) throws java.lang.Exception {
		inputFileName = input;
		outputFileName = output;
		
		// Flag other methods that it's safe to proceed
		safe = true;
		
		// Read the file into memory:
		try {
			DataSource source = new DataSource(inputFileName);
			data = source.getDataSet();
			
			// Set the class attribute -- assuming the data format does not provide this information
			if (data.classIndex() == -1)
				data.setClassIndex(data.numAttributes() - 1);
		}
		catch (Exception e) {
			safe = false;  // No other methods should be called.
			throw new Exception ("Error opening input file " + input);
		}
	}
	
	
	/**
	 * Closes the instance of this class and writes the output file.
	 * @return true when file is successfully written; false otherwise.
	 */
	public boolean close () {
		if (! safe) {
			return false;
		}
		
		safe = false;  // No longer needed
		
		ArffSaver saver = new ArffSaver();
		saver.setInstances(data);
		try {
			saver.setFile(new File(outputFileName));
			saver.writeBatch();
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}
	
	
	/**
	 * Placeholder ... in case this gets called impolitely from the garbage collector rather than
	 * properly through the "close" function. 
	 */
	public void finalize () {
		close();
	}
	
	
	/**
	 * Given the dataset (file) imported by the constructor, removes the attribute (column) indicated
	 * by the parameter. When column is "1", the first attribute is removed.
	 * @param column = the column to be removed; may be specified as a negative number (offset from end).
	 * @return true when successfully removed; false otherwise.
	 */
	public boolean removeAttribute (int column) {
		if (! safe) {
			System.out.println("Not safe... not proceeding.");
			return false;
		}
		
		String[] options = new String[2];
		options[0] = "-R";
		options[1] = String.valueOf(column);
		
		Remove remove = new Remove();
		try {
			remove.setOptions(options);
			remove.setInputFormat(data);
			Instances newData = Filter.useFilter(data, remove);  // apply filter
			
			// Re-assign to original data
			data = newData;
			newData = null;
		}
		catch (Exception e) {
			safe = false;
			return false;
		}
		return true;
	}
	
	
	// Stolen from http://weka.wikispaces.com/Adding+attributes+to+a+dataset
	// TODO: Get clarity from Adam on this.
	// TODO: Create "addNominalAttribute"
	public boolean addNumericAttribute (String name, String filename, int column) {
		/*
		if (! safe) {
			System.out.println("Not safe... not proceeding.");
			return false;
		}
		
		Instances newData = new Instances(data);
		Add filter = new Add();
		filter.setAttributeIndex("last");
		filter.setAttributeName(name);
		try {
			filter.setInputFormat(newData);
			newData = Filter.useFilter(newData, filter);
			
			// TODO: Read the data from the file(?) provided
			for (int i = 0; i < newData.numInstances(); i++) {
				// Places new attribute as second-to-last item. (Last item is reserved for class attribute.)
		        newData.instance(i).setValue(newData.numAttributes() - 1, -25.353);
			}
			
			// Re-assign to original data
			data = newData;
			newData = null;
		}
		catch (Exception e) {
			safe = false;
			return false;
		}
		return true;
		*/
		return false;
	}
	
	
	// TODO: Check with Adam; this is stolen from ConvertTestVectors.java
	public void convertTestVectors (String nominalIndices, String resourceName, boolean resourceIsFile, String target) {
	    VectorToWekaConverter converter = new VectorToWekaConverter();
	    converter.setNominalIndices(nominalIndices);
	    try {

	      if (resourceIsFile) {
	        converter.loadFile(resourceName);
	      } else {
//	        converter.loadAllFilesInDirectory(resourceName);
	    	  converter.vectorDirToArff(resourceName, target);
	      }

	    } catch (IOException | TestVectorException e) {
	      e.printStackTrace();
	    }
	  }
	
	
	/**
	 * Determines the count of minority (and non-minority) classes; removes instances of non-minority classes until
	 * the counts of all classes are equal to the minority class. 
	 * @return true when successful; false otherwise
	 */
	public boolean downsample () {
		if (! safe) {
			return false;  // Not safe; not proceeding.
		}
		
		System.out.println("*** Starting downsample function; expecting " + data.numInstances() + " instances ***");

		HashMap<String, Integer> targets = new HashMap<String, Integer>();
		
		// Iterate through the Instances and count the number of each of the final attribute ...
		// HashMap<String, Integer> targets = new HashMap<String, Integer> ();
		for (int i = 0; i < data.numInstances(); i++) {			
			Integer count = targets.get(data.instance(i).stringValue(data.numAttributes()-1));
			
			if (count == null || count.intValue() <= 0) {
				targets.put(data.instance(i).stringValue(data.numAttributes()-1), 1);
			}
			else {
				int intcount = count.intValue();
				targets.put(data.instance(i).stringValue(data.numAttributes()-1), ++intcount);
			}
		}
		
		// Given the counts above, it's probably easy to determine the minority ... 
		String majority = "";
		String minority = "";
		String key = "";
		
		Iterator<String> keys = targets.keySet().iterator();
		// Set the first element to both minority and majority targets
		if (keys.hasNext()) {
			key = keys.next();
			majority = key;
			minority = key;
		}
		
		// Check the rest of the targets to see whether the counts are higher or lower 
		while (keys.hasNext()) {
			key = keys.next();

			Integer count = targets.get(key);
			if (count > targets.get(majority)) {
				majority = key;
			}
			if (count < targets.get(minority)) {
				minority = key;
			}
		}
		
		String targetMinority = minority;

		// Next, determine which items in the data set need to be removed (one or more non-minority instances).
		Vector<Integer> toBeRemoved = new Vector<Integer>();
		
		HashMap<String, Integer> written = new HashMap<String, Integer>();
		HashMap<String, Integer> toSkip  = new HashMap<String, Integer>();
		
		int minorityCount = targets.get(targetMinority);
		
		keys = targets.keySet().iterator();
		while (keys.hasNext()) {
			key = keys.next();
			written.put(key, 0);
			toSkip.put(key, 0);
		}
		
		for (int i = 0; i < data.numInstances(); i++) {
			String attribute = data.instance(i).stringValue(data.numAttributes()-1);
			if (toSkip.get(attribute) == 0 && written.get(attribute) < minorityCount) {
				// Don't skip, but update counts:
				written.put(attribute, 1 + written.get(attribute));
				toSkip.put(attribute, (targets.get(attribute) - minorityCount) / minorityCount);
			}
			else {
				toBeRemoved.add(i);
			}
		}
		
		// Last step: for each item in the "toBeRemoved" vector, remove it from the final data set:
		System.out.println("Removing Total: "+toBeRemoved.size());
		System.out.println("Data Num Instances: "+data.numInstances());
		for (int i = 0; i < toBeRemoved.size(); i++) {
			System.out.println("Removing: "+data.instance(i));
			data.delete(i);
		}

		return true;
	}

}
