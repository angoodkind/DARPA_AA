package output.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.supportVector.RBFKernel;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericToNominal;
import weka.filters.unsupervised.attribute.Standardize;

/**
 * A utility class to process:
 * 	- TestVector directories to arffs
 * 	- arffs to Instanceses
 * 	- run Weka experiments
 */

public class WekaPipeline {
	
	public String trainingDirStr; //from args
	public String testingDirStr; //from args
	public Classifier classifier;
	public double null_tolerance = 0.0; //will need to be changed
	public String trainingArffLocation = ""; //args
	public String testingArffLocation = ""; //args
	public Instances training;
	public Instances testing;
	Instances rawTraining;
	Instances rawTesting;
	
	//create training and testing arffs
	public void createArffs (String nominalIndices) {
		//create training arff with null tolerance
		VectorToWekaConverter converterTrain = new VectorToWekaConverter();
		converterTrain.setNullTolerance(null_tolerance);
		converterTrain.setNominalIndices(nominalIndices);
//			converterTrain.loadAllFilesInDirectory(trainingDirStr);
//			converterTrain.writeArff(trainingArffLocation);
		converterTrain.vectorDirToArff(trainingDirStr, trainingArffLocation);
		
		//create testing arff without null tolerance
		VectorToWekaConverter converterTest = new VectorToWekaConverter();
		converterTest.setNullTolerance(null_tolerance);
		converterTest.setNominalIndices(nominalIndices);
//			converterTest.loadAllFilesInDirectory(testingDirStr);
//			converterTest.writeArff(testingArffLocation);
		converterTest.vectorDirToArff(testingDirStr, testingArffLocation);
	}
	
	/**
	 *	1) Creates arff files for training and testing
	 *	2) sets class index for training based on passed-in argument
	 *		a) sets testing class attribute based on (2)
	 *	3) makes class attributes nominal 
	 */
	public void convertArffsToInstanceses() throws WekaPipelineException, Exception {
		
		Instances tempTraining = arffToInstances(this.trainingArffLocation);
		System.out.println("tempTraining, # of Attributes: "+tempTraining.numAttributes());
		Instances tempTesting = arffToInstances(this.testingArffLocation);
		System.out.println("tempTesting, # of Attributes: "+tempTesting.numAttributes());
		pruneInstanceses(tempTraining,tempTesting);
		rawTraining.setClassIndex(rawTraining.numAttributes()-1);
		NumericToNominal filter = new NumericToNominal();
//		Standardize filter = new Standardize();
		filter.setAttributeIndicesArray(new int[] {rawTraining.classAttribute().index()});
		filter.setInputFormat(rawTraining);
		this.training = Filter.useFilter(rawTraining, filter);
		
//		this.testing.setClass(testing.attribute(this.training.classAttribute().name()));
//		NumericToNominal testingNumToNom = new NumericToNominal();
//		testingNumToNom.setAttributeIndicesArray(new int[] {this.testing.classAttribute().index()});
//		testingNumToNom.setInputFormat(testing);
		this.testing = Filter.useFilter(rawTesting, filter);
		
		System.out.println("Training Class Attribs: "+training.classAttribute().toString());
		System.out.println("Testing Class Attribs: "+testing.classAttribute().toString());
	}
	
	/**
	 * convert an arff file to a weka.Instances <code>Instances</code> object
	 * @param filename
	 * @return
	 * @throws WekaPipelineException
	 */
	public Instances arffToInstances(String filename) throws WekaPipelineException {
		
		Instances tempInstances = null;
		
		try {
			BufferedReader arffReader = new BufferedReader(new FileReader(filename));
			System.out.println("Loading: "+filename);
			tempInstances = new Instances(arffReader);
			arffReader.close();
		} catch (IOException e) {e.printStackTrace();}
		
		return tempInstances;
	}
	
	//Prune testing arff, based on training attributes
	public void pruneInstanceses(Instances train, Instances test) {
		for (int i = 0; i < test.numAttributes(); i++) {
			if (train.attribute(test.attribute(i).name()) == null) {
				test.deleteAttributeAt(i);
				i--;
			}
		}
		
		for (int i = 0; i < train.numAttributes(); i++) {
			if (test.attribute(train.attribute(i).name()) == null) {
				train.deleteAttributeAt(i);
				i--;
			}
		}
		rawTraining = train;
		rawTesting = test;
		
		//insert writearffs()
		writeArff(rawTraining,trainingArffLocation);
		writeArff(rawTesting,testingArffLocation);
		
//		for (int i = 0; i < rawTraining.numAttributes(); i++)
//			System.out.println(rawTraining.attribute(i).name());
//		System.out.println();
//		for (int i = 0; i < rawTesting.numAttributes(); i++)
//			System.out.println(rawTesting.attribute(i).name());
		System.out.println("Raw Training Attributes: "+rawTraining.numAttributes());
		System.out.println("Raw Testing Attributes: "+rawTesting.numAttributes());
	}
	
	private void writeArff(Instances instances, String arffFileName) {
		try {
			ArffSaver saver = new ArffSaver();
			saver.setInstances(instances);
			saver.setFile(new File(arffFileName));
			saver.writeBatch();
		} catch (IOException e) {e.printStackTrace();}
	}
	
	//build classifier based on training and evaluate
	public void classifyAndEvaluate() {
		try {
			this.classifier.buildClassifier(this.training);
			// evaluate classifier and print some statistics
			Evaluation eval = new Evaluation(this.training);
			eval.evaluateModel(classifier, this.testing);
			System.out.println(eval.toSummaryString("\n==SUMMARY==\n", true));
			System.out.println(eval.toClassDetailsString("==CLASS_DETAILS==\n"));
			System.out.println(eval.toMatrixString("\n==CONFUSION_MATRIX==\n"));
//			System.out.println(this.classifier.toString());
//			for (Instance i : this.testing) {
//				System.out.print(i.stringValue(0)+","); //should be the SubjAnsID
//				System.out.print(i.classValue()+",");
//				System.out.println(eval.evaluateModelOnce(classifier, i));
//			}
		} catch (Exception e) {e.printStackTrace(); }
	}
	
	//create balanced training arff
	public void createBalancedArff() throws Exception {
		ArffManipulator am = new ArffManipulator(trainingArffLocation,"Balanced-"+trainingArffLocation);
		am.downsample();
	}
	
	//not used in default processing; primarily for testing purposes
	public void printInstances() {
//		System.out.println(this.training.relationName());
//		for (int i = 0; i < training.numAttributes(); i++)
//			System.out.print(training.attribute(i).toString()+"\n");
//		System.out.println();
//		System.out.println(this.testing.relationName());
//		for (int i = 0; i < testing.numAttributes(); i++)
//			System.out.print(testing.attribute(i).toString()+"\n");
		
		System.out.println(this.training.toString());
		System.out.println();
		System.out.println(this.testing.toString());
	}
	
	public WekaPipeline() {}
	
	//constructor for ProcessWekaPipeline
	public WekaPipeline(String trainDir,String testDir,String trainArff,String testArff) {
		this.trainingDirStr = trainDir;
		this.testingDirStr = testDir;
		this.trainingArffLocation = trainArff;
		this.testingArffLocation = testArff;
	}
	
	//constructor for ProcessArffExperiment
	public WekaPipeline(String trainArff,String testArff) {
		this.trainingArffLocation = trainArff;
		this.testingArffLocation = testArff;
	}
}
