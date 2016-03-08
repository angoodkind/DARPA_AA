/**
 * 
 */
package output.util;

import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.supportVector.RBFKernel;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.meta.LogitBoost;
import weka.classifiers.trees.J48;

/**
 * @author agoodkind
 * 
 * Streamline processing of weka experiments:
 *	- see outline of processing steps below
 */

public class ProcessWekaPipeline {

	/**
	 * See argument structure below
	 * Process:
	 * 	1) Creates new pipeline
	 * 	2) sets pipeline's classifier
	 * 	3) creates arffs from TemplateVectors
	 * 	4) creates Instanceses from arffs
	 * 		**Note: The last attribute in the training set becomes the class index
	 * 	5) prune testing Instances based on training Instances
	 * 	6) output results of evaluation
	 * 	7) create balanced version of training arff
	 * 	Notes: optional flag -noexp will just create arffs, and not run experiments
	 */
	
	public static void main(String[] args) {
		
		try {
		if (args.length < 6) {
			System.out.println("Bad args format:\n" +
					"/**" +
					 "* Format of args\n"+
					 "* 	[0] - location of training TVs\n"+
					 "* 	[1] - location of testing TVs\n"+
					 "* 	[2] - nominal indices\n"+ 
					 "* 	[3] - training arff output location\n"+
					 "* 	[4] - testing arff output location\n"+
					 "* 	[5] - classifier choice\n"+ 
					 "*		[6] - OPTIONAL -noexp"+	
					 "**/");
			return;
		}
		
		//create new pipeline
		WekaPipeline pipeline = new WekaPipeline(args[0],args[1],args[3],args[4]);
		
		//initialize pipeline's classifier
		String classifierName = args[5];
		switch (classifierName) {
			case "NaiveBayes":
				pipeline.classifier = new NaiveBayes();
				break;
			case "AdaBoostM1":
				pipeline.classifier = new AdaBoostM1();
				break;
			case "LogitBoost":
				pipeline.classifier = new LogitBoost();
				break;
			case "J48":
				pipeline.classifier = new J48();
				break;
			case "IBk":
				pipeline.classifier = new IBk();
				break;
			case "SMO":
			case "SVM":  // PolyKernel -E = 1.0 -- i.e. linear?
				pipeline.classifier = new SMO();
				/*************FIX***************/
				if (args.length > 7) {
					// Assume the next is the "C" value:
					((SMO) pipeline.classifier).setOptions(weka.core.Utils.splitOptions("-C " + args[3]));
				}
				break;

			// RBF and PolyKernel are incompatible with "pipeline"
//			case "RBF":
//				pipeline.classifier = new RBFKernel();
//				break;
//			case "Polynomial":
//				pipeline.classifier = new PolyKernel();
//				break;

				
		}
		if (pipeline.classifier == null) {
			System.out.println("Invalid classifier. Please enter valid classifier.");
			return;
		}
				
		pipeline.createArffs(args[2]);					//create arff files
		
		// using -noexp will just create arffs, and not run experiments
		if (args.length > 6 && args[6].equals("-noexp")) {
			return;
		}
		
//		pipeline.createBalancedArff();					//creates a balanced version of the training arff		
		pipeline.convertArffsToInstanceses();			//create Instances objects, set class index
//		pipeline.pruneInstanceses();					//prune testing Instances based on training Instances
		pipeline.classifyAndEvaluate();					//output results


		
		} catch (Exception | WekaPipelineException e) {e.printStackTrace();}
	
		
	}
}
