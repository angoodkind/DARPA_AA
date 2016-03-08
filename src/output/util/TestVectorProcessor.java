package output.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * TestVectorProcessor is a class that allows for a set of extracted features to be processed.
 *
 * This is a base class for arff conversion, and expected value calculation.
 *
 * Subclasses are required to define what they want to do with each line of observations.
 */
public abstract class TestVectorProcessor {

	protected String[] attr_names;              // a hash of attribute names to their storage location.

	/**
	 * Loads every file in a directory.
	 *
	 * @param path the path of the directory
	 * @throws TestVectorException if the path is not a directory.
	 */
	public void loadAllFilesInDirectory(String path) throws TestVectorException {
		File dir = new File(path);
		if (!dir.isDirectory()) {
			throw new TestVectorException("Path, " + path + ", is not a directory");
		}
		for (File f : dir.listFiles()) {
			if (f.isFile() && !f.isHidden() && !f.getAbsolutePath().endsWith(".log")) {
				try {
//					System.out.println("Loading "+f.getAbsolutePath());
					loadFile(f.getAbsolutePath());
				} catch (IOException | TestVectorException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Reads data from a file into the data_matrix.
	 *
	 * @param filename the filename
	 * @throws IOException        if the file cant be read
	 * @throws TestVectorException if there is a problem with the data format.
	 */
	public void loadFile(String filename) throws IOException, TestVectorException {
		BufferedReader reader = new BufferedReader(new FileReader(filename));

		String line = reader.readLine();
		String delim = "[|]";
		//    System.out.println("line: "+line);

		if (attr_names == null) {
			// Assign the attribute names based on the first line.
			attr_names = line.trim().split(delim, -1);
//			System.out.println("Created attr_names, length: "+attr_names.length+"...");
//		    System.out.println("Created attr_names as "+Arrays.toString(attr_names));
		} else {
			// Confirm the order of the attribute names
			String[] names = line.trim().split(delim, -1);
//			      System.out.println("data string array: "+Arrays.toString(names));
			if (names.length != attr_names.length) {
				//    	  System.out.println("names.length="+names.length+" "+"attr_names.length="+attr_names.length);
				throw new TestVectorException("File, " + filename + ", has an incompatible list of attributes");
			}
			for (int i = 0; i < names.length; ++i) {
				if (!names[i].equals(attr_names[i])) {
					throw new TestVectorException(
							"In file, " + filename + ", attribute[" + i + "] is unexpected. Expected:" + attr_names[i] +
							"; Actual: " + names[i]);
				}
			}
		}
		int lineCount = 0;
		while ((line = reader.readLine()) != null) {
			lineCount++;
			String[] data = line.trim().split("\\|", -1);
//			System.out.println("data[]: "+Arrays.toString(data));
			if (data.length != attr_names.length) {
				throw new TestVectorException(
						"In file " + filename + ", line "+lineCount+", Data line does not have the correct number of variables\n>" /**+ line**/ +
						"\nExpected:" + attr_names.length + "; Actual: " + data.length);
			}

			processData(data);
		}
	}

	/**
	 * Processes a single line of data.
	 *
	 * This is an abstract method that needs to be defined by subclasses.
	 *
	 * @param data a single line separated into attributes.
	 */
	protected abstract void processData(String[] data);

	public String[] getAttrNames() {
		return attr_names;
	}

	public void setAttrNames(String[] attrNames) {
		this.attr_names = attrNames;
	}
}
