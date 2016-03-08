package output.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import com.sun.xml.internal.bind.v2.runtime.reflect.ListIterator;

/**
 * Takes an external file of asymmmetric TestVector features
 * and creates symmetric user TestVector files
 * optional 3rd argument is the class attribute (to be put last)
 * <p><p>
 * Usage: 
 * 
 * 
 * @author Adam Goodkind
 *
 */
public class MakeSymmetricTestVectors {
	
	private LinkedHashMap<Integer,LinkedList<LinkedHashMap<String,String>>> vectorMap;
	private File outputDir;
	private static String CLASS_ATTRIB = null;
	
	@SuppressWarnings("unchecked")
	public MakeSymmetricTestVectors(String vectorMapFile, String outputDir) {
		ObjectInputStream in;
		try {
			in = new ObjectInputStream(new FileInputStream(new File(vectorMapFile)));
			this.vectorMap = (LinkedHashMap<Integer, LinkedList<LinkedHashMap<String, String>>>) in.readObject();
			this.outputDir = new File(outputDir);
		} catch (IOException | ClassNotFoundException e) {e.printStackTrace();}
		
	}
	
	/**
	 * For each user in <code>vectorMap</code>: <br>
	 * 	- create a master set of features <br>
	 * 	- loop through each scan, populating values, and inserting
	 * 		nulls where necessary <br>
	 * 	- create file
	 */
	private void createFiles() {
		if (!outputDir.exists())
			outputDir.mkdir();
		//create a new file for each user
		for (Integer user : vectorMap.keySet()) {
			try {
				LinkedHashSet<String> allFeaturesSet = new LinkedHashSet<String>();
				Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputDir.getPath() + File.separatorChar + "TestVectorsUser"+user+".txt")));
				LinkedList<LinkedHashMap<String,String>> userScanList = vectorMap.get(user);
				//create master feature set for this user
				for (LinkedHashMap<String,String> scan : userScanList) {
					allFeaturesSet.addAll(scan.keySet());
				}
				
				if (CLASS_ATTRIB != null) {
					allFeaturesSet.remove(CLASS_ATTRIB);
					allFeaturesSet.add(CLASS_ATTRIB);
				}
				
				//print headers
				StringBuilder headers = new StringBuilder();
				for (String featureName : allFeaturesSet) {
						headers.append(featureName+"|");
				}
				
				writer.write(headers.toString().substring(0,headers.length()-1)+"\n");
				
				for (LinkedHashMap<String,String> scan : userScanList) {
					if (scan.size() > 0) {
					StringBuilder currentScan = new StringBuilder();
					for (String feature : allFeaturesSet) {
						if (scan.containsKey(feature))
							currentScan.append((scan.get(feature)));
						currentScan.append(("|"));
					}
					writer.write(currentScan.toString().substring(0,currentScan.length()-1)+"\n");
				}
				}
				writer.close();
			} catch (IOException e) {e.printStackTrace();}
		}
	}
	
	public static void main(String[] args) {
		MakeSymmetricTestVectors maker = new MakeSymmetricTestVectors(args[0],args[1]);
		if (args.length == 3) {
			CLASS_ATTRIB = args[2];
		}
		maker.createFiles();
		System.out.println("Symmetric TestVectors Created Successfully");
	}

}
