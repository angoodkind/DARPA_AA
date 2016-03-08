/**
 * 
 */
package output.util;

import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.meta.LogitBoost;
import weka.classifiers.trees.J48;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.supportVector.RBFKernel;
import weka.classifiers.functions.supportVector.PolyKernel;

/**
 * @author agoodkind
 * similar to ProcessWekaPipeline, but uses previously created arffs instead of TestVetor directories
 */
public class ProcessArffExperiment {
	
	/**
	 * See argument structure below
	 * Process:
	 * 	1) Creates new pipeline
	 * 	2) sets pipeline's classifier
	 * 	3) creates Instanceses from arffs
	 * 		**Note: The last attribute in the training set becomes the class index
	 * 	4) prune testing Instances based on training Instances
	 * 	5) output results of evaluation
	 */
	
	public static void main(String[] args) {
		
		try {
		if (args.length < 3) {
			System.out.println("Bad args format:\n" +
					"/**" +
					 "* Format of args\n"+
					 "* 	[0] - location of training arff\n"+
					 "* 	[1] - location of testing arff\n"+
					 "* 	[2] - classifier choice\n"+ 
					 "**/");
			return;
		}
		
		//create new pipeline
		WekaPipeline pipeline = new WekaPipeline(args[0],args[1]);
		
		//initialize pipeline's classifier
		String classifierName = args[2];
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
				if (args.length > 3) {
					// Assume the next is the "C" value:
					((SMO) pipeline.classifier).setOptions(weka.core.Utils.splitOptions("-C " + args[3]));
				}
				break;
/**
			// RBF and PolyKernel are incompatible with "pipeline"
			case "RBF":
				pipeline.classifier = new RBFKernel();
				break;
			case "Polynomial":
				pipeline.classifier = new PolyKernel();
				break;
**/
		}
		if (pipeline.classifier == null) {
			System.out.println("Invalid classifier. Please enter valid classifier.");
			return;
		}
				
		pipeline.convertArffsToInstanceses();	//create Instances objects, set class index
//		pipeline.pruneInstanceses();			//prune testing Instances based on training Instances
		pipeline.classifyAndEvaluate();			//output results
		
		} catch (Exception | WekaPipelineException e) {e.printStackTrace();}		
	}
}
