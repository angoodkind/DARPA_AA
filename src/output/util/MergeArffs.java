package output.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import weka.core.Instances;
import weka.core.converters.ArffLoader;

/**
 * Process for merging two arffs:
 * 	1) Convert each arff file into an Instances
 * 	2) Check that the size (# of Instances) matches
 * 	3) Iterate through User+AnswerIDs, and confirm that they match
 * 	4) Delete class attribute from original data (replaced by class attribute in additional data)
 * 	5) Delete user info in additional data, since it is now redundant
 * 	6) merge Instances's
 * 	7) Write merged Instances to arff file
 * 
 * Format of arff files:
 * 	1) first attribute must be user+answer info, from features.demographics.SubjectID
 * 	2) if original arff has a class attribute, which is noted by the -C flag, it will be deleted
 * 
 * @author agoodkind
 * 
 * @param origArffLocation		arff file of previous data
 * @param addlArffLocation		arff file of newly cultivated data
 * @param mergedArffLocation	location for newly created arff file
 * @param origArffClass			add "-C" if original arff has a class attribute, which will be deleted
 * 
 * @return void, but writes arff file at location specified by mergedArffLocatiom
 */
public class MergeArffs {
	
	public static void main(String[] args) {
		try {
			if (args.length == 3)
				mergeData(args[0],args[1],args[2],"");
			else if (args.length == 4)
				mergeData(args[0],args[1],args[2],args[3]);
		} catch (IOException | WekaPipelineException e) {e.printStackTrace();}
	}
	
	public static void mergeData(
			String origArffLocation, String addlArffLocation, String mergedArffLocation, String origArffClass)
			 throws WekaPipelineException, IOException {
		
		try {
			
			//create an ArffLoader
			ArffLoader arffLoader = new ArffLoader();
		
			//create new Instances's for original data and additional data 
			arffLoader.setFile(new File(origArffLocation));
			Instances originalData = arffLoader.getDataSet();
			arffLoader.setFile(new File(addlArffLocation));
			Instances additionalData = arffLoader.getDataSet();
			
			//compare sizes of Instances's
			if (originalData.numInstances() != additionalData.numInstances()) {
				throw new WekaPipelineException("Unequal number of instances");
			}
			
			//compare users+answers
			for (int i = 0; i < originalData.numInstances(); i++) {
				if (!originalData.instance(i).stringValue(0).equals(additionalData.instance(i).stringValue(0)))
						throw new WekaPipelineException("Non-equivalent user: Orig: "+originalData.instance(i).stringValue(0)+
								" Addl: "+additionalData.instance(i).stringValue(0));
			}
			
			//delete class attribute from original data, if -C flag
			if (origArffClass.equals("-C")) {
				originalData.deleteAttributeAt(originalData.numAttributes()-1);
			}
			
			//delete user IDs from Additional Data
			additionalData.deleteAttributeAt(0);
			
			//Merge data
			Instances mergedData = Instances.mergeInstances(originalData,additionalData);
//			System.out.println(mergedData);
			
			//write merged arff
			BufferedWriter writer = new BufferedWriter(new FileWriter(mergedArffLocation));
			writer.write(mergedData.toString());
			writer.flush();
			writer.close();
			
		} catch (IOException | WekaPipelineException e) {e.printStackTrace();}		
	}
}
