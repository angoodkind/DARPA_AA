package output.util;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.meta.LogitBoost;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Creates a classifier from a directory of TestVectors
 * 
 * @author Adam Goodkind
 *
 */
public class GenerateClassifierFile {

	public static void main(String[] args) {

		if (args.length < 3) {
			System.out.println("Bad args format:\n" +
					"/**" +
					"* Format of args\n"+
					"* 	[0] - location of saved TestVector directory\n"+
					"* 	[1] - nominal indices\n"+
					"* 	[2] - classifier type to use\n"+
					"*	(.cls extension and classifer name appended automatically to Classifier file)\n"+
					"**/");
			return;
		}
		
		VectorToWekaConverter converter = new VectorToWekaConverter();
		String classifierName = args[2];
		switch (classifierName) {
			case "NaiveBayes":
				converter.classifier = new NaiveBayes();
				break;
			case "AdaBoostM1":
				converter.classifier = new AdaBoostM1();
				break;
			case "LogitBoost":
				converter.classifier = new LogitBoost();
				break;
			case "J48":
				converter.classifier = new J48();
				break;
			case "IBk":
				converter.classifier = new IBk();
				break;
			case "SMO":
			case "SVM":  // PolyKernel -E = 1.0 -- i.e. linear?
				converter.classifier = new SMO();
				/*************FIX***************/
				//if (args.length > 3) {
				// Assume the next is the "C" value:
				//		converter.classifier.setOptions(weka.core.Utils.splitOptions("-C " + args[3]));
				//}
				break;
				/*
					// RBF and PolyKernel are incompatible with "pipeline"
					case "RBF":
						converter.classifier = new RBFKernel();
						break;
					case "Polynomial":
						converter.classifier = new PolyKernel();
						break;
				 */
			}
			if (converter.classifier == null) {
				System.out.println("Invalid classifier. Please enter valid classifier.");
				return;
			}
			
		converter.setNullTolerance(0.01);
		converter.setNominalIndices(args[1]);
		converter.vectorToClassifierFile(args[0],args[2]);
		System.out.println("\n**Created classifier file "+args[0]+args[2]+".cls**");
		System.exit(0);
	}
}
