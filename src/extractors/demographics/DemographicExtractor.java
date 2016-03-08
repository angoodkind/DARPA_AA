package extractors.demographics;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import weka.classifiers.Classifier;
import weka.classifiers.misc.InputMappedClassifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import events.EventList;
import events.GenericEvent;
import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import keystroke.KeyStroke;
import output.util.Aggregator;
import output.util.VectorToWekaConverter.CountAggregator;
import output.util.VectorToWekaConverter.MeanAggregator;
import output.util.VectorToWekaConverter.MedianAggregator;
import output.util.VectorToWekaConverter.MseAggregator;
import output.util.VectorToWekaConverter.StdDevAggregator;

/**
 * - modules.conf must be set to run this extractor, other modules
 * highlighted in this file will not affect the current extractor.
 * - attributes.conf must be set to use all of the features
 * that should be extracted by this extractor. Modules set in this
 * file will not affect AppWindow or any other extractors
 * 
 * The InputMappedClassifier will map incompatible data, i.e. missing
 * attributes, either in the training or testing data.
 * 
 * The clsName variable is a serialized Object[], written using weka's
 * SerializationHelper.writeAll() function. Object[0] is the serialized
 * Instances. Object[1] is the serialized Classifier
 * 
 * In order to extract a demographic feature, this extractor must run the
 * extract() method of other Extraction Modules. If those modules are also
 * run, the extract() method will be run twice. This is a known inefficiency.
 * 
 * @author Adam Goodkind
 *
 */
public class DemographicExtractor implements ExtractionModule {

	protected static Classifier cls;
	protected static Instances clsInstances;
	protected static String clsName = "HandednessAda.cls";
	protected static InputMappedClassifier mappedCls = new InputMappedClassifier();;
	protected static LinkedList<ExtractionModule> moduleList = new LinkedList<ExtractionModule>();
	protected static String[] aggregatorNames = new String[]{"mean", "median", "count", "mse", "sd"};
	protected static ArrayList<Aggregator> aggregatorList = new ArrayList<Aggregator>();
	protected static ArrayList<Attribute> attributeList = new ArrayList<Attribute>();
	protected static boolean attributesSet = false;
	

	static {
		try {
			Object[] clsFile = weka.core.SerializationHelper.readAll(clsName);
			cls = (Classifier) clsFile[1];
			clsInstances = (Instances) clsFile[0];
			mappedCls.setClassifier(cls);
			mappedCls.setModelHeader(clsInstances);
			loadModules();
			loadAggregators();
		} catch (Exception e) {e.printStackTrace();}
	}

	public Collection<Feature> extract(DataNode allAnswers) {
		
		LinkedList<Feature> output = new LinkedList<Feature>();
		LinkedList<Double> predictedClassIndices = new LinkedList<Double>();
		
		for (Answer ans : allAnswers) {
			
//			System.out.println("Attributes set?: "+attributesSet);
			
			//wrap an Answer in a DataNode
			DataNode newNode = new DataNode(allAnswers.getUserID());
			newNode.add(ans);

			//create initial features from extraction modules
			LinkedList<Feature> allModuleFeatures = new LinkedList<Feature>();
			
//			long millis = System.currentTimeMillis();
//			String now = String.format("%d min, %d sec", 
//				    TimeUnit.MILLISECONDS.toMinutes(millis) % 1000,
//				    TimeUnit.MILLISECONDS.toSeconds(millis) - 
//				    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
//			System.out.println("Entering moduleFeatures Addition "+now);
			
			for (ExtractionModule module : moduleList) {
				Collection<Feature> moduleFeatures = module.extract(newNode);
				allModuleFeatures.addAll(moduleFeatures);
			}
			
			//create list of Attributes, using list of features/aggregators
			if (!attributesSet) {
//				millis = System.currentTimeMillis();
//				now = String.format("%d min, %d sec", 
//					    TimeUnit.MILLISECONDS.toMinutes(millis) % 1000,
//					    TimeUnit.MILLISECONDS.toSeconds(millis) - 
//					    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
//				System.out.println("Setting Attributes "+now);

				setAttributes(allModuleFeatures);
				
//				millis = System.currentTimeMillis();
//				now = String.format("%d min, %d sec", 
//					    TimeUnit.MILLISECONDS.toMinutes(millis) % 1000,
//					    TimeUnit.MILLISECONDS.toSeconds(millis) - 
//					    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
//				System.out.println("AttributeList Set "+now);
			}
			
			//create Instances and Instance
			Instances wrapperInstances = new Instances("testRelation",attributeList,0);
			wrapperInstances.setClassIndex(wrapperInstances.numAttributes()-1);
			Instance nodeInstance = new DenseInstance(wrapperInstances.numAttributes());
			nodeInstance.setDataset(wrapperInstances);
			
//			millis = System.currentTimeMillis();
//			now = String.format("%d min, %d sec", 
//				    TimeUnit.MILLISECONDS.toMinutes(millis) % 1000,
//				    TimeUnit.MILLISECONDS.toSeconds(millis) - 
//				    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
//			System.out.println("Entering Attribute Creation "+now);
			
			//add Attributes to Instance
			for (Feature f : allModuleFeatures) {
				if (f.getFeatureValues().size() > 0) {
					for (Aggregator agg : aggregatorList) {
						double value = agg.aggregate(f);		
						String featureName = f.getFeatureName().replaceAll(" ", "_").replaceAll("\\*", "_");
						nodeInstance.setValue(wrapperInstances.attribute(featureName+"_"+agg.getLabel()), value);
	//					System.out.println(fName+"_"+agg.getLabel()+" "+value);
					}
				}
			}
			
//			millis = System.currentTimeMillis();
//			now = String.format("%d min, %d sec", 
//				    TimeUnit.MILLISECONDS.toMinutes(millis) % 1000,
//				    TimeUnit.MILLISECONDS.toSeconds(millis) - 
//				    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
//			System.out.println("Attributes Set "+now);
			
			nodeInstance.setValue(wrapperInstances.classAttribute(), -1);	//add null, -1, class attribute
			wrapperInstances.add(nodeInstance);								//add Instance to Instances
			
//			mappedCls.setModelHeader(wrapperInstances);
			
			//get results
			try {
				
//				millis = System.currentTimeMillis();
//				now = String.format("%d min, %d sec", 
//					    TimeUnit.MILLISECONDS.toMinutes(millis) % 1000,
//					    TimeUnit.MILLISECONDS.toSeconds(millis) - 
//					    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
//				System.out.println("Classifying "+now);
				
				double predictedClassIndex = mappedCls.classifyInstance(wrapperInstances.firstInstance());
				
//				millis = System.currentTimeMillis();
//				now = String.format("%d min, %d sec", 
//					    TimeUnit.MILLISECONDS.toMinutes(millis) % 1000,
//					    TimeUnit.MILLISECONDS.toSeconds(millis) - 
//					    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
//				System.out.println("Classified "+now);
				
				predictedClassIndices.add(predictedClassIndex);
				double[] distrib = mappedCls.distributionForInstance(wrapperInstances.firstInstance());
//				System.out.println(wrapperInstances.firstInstance());
				System.out.print("Instance classification: "+predictedClassIndex);
				System.out.print(" Distrib: ");
				for (double d : distrib)
//					System.out.print(d+" ");
				System.out.println();
			
			} catch (Exception e) {e.printStackTrace();	} 
		}
		output.add(new Feature("Class Index",predictedClassIndices));
		return output;
	}


	/**
	 * Loads modules into the FeatureExtractor to be used during test
	 * processes. The modules.conf file should only call this module.
	 * This module, in turn, will call the necessary modules. 
	 * <p>
	 * <p>
	 * Modules are loaded from modules.conf by the fully qualified name of the
	 * module's Java Class (i.e. features.nyit.KeyHold)
	 * 
	 * @throws Exception
	 * @author Adam Goodkind
	 */
	public static void loadModules() throws Exception {

		Scanner scanner = null;
		try {
			scanner = new Scanner(new File("attributes.conf"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		while (scanner.hasNextLine()) {
			String token = scanner.nextLine().trim();
			if (!token.isEmpty()) {
				if (token.charAt(0) != '#') {
					try {
						moduleList.add((ExtractionModule) Class.forName(token).newInstance());
					} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}
		scanner.close();
		if (moduleList.isEmpty()) {
			throw new Exception("No Modules Selected.");
		}
	}


	public static void loadAggregators() {
		aggregatorList = new ArrayList<>();
		for (String s : aggregatorNames) {
			switch (s) {
			case "mean":
				aggregatorList.add(new MeanAggregator());
				break;
			case "median":
				aggregatorList.add(new MedianAggregator());
				break;
			case "count":
				aggregatorList.add(new CountAggregator());
				break;
			case "mse":
				aggregatorList.add(new MseAggregator());
				break;
			case "sd":
				aggregatorList.add(new StdDevAggregator());
				break;
			}
		}
	}

	/**
	 * Sets the global list of attributes, derived from the inputted
	 * feature list, and the global list of aggregators
	 * 
	 * @param featureList
	 */
	public void setAttributes(LinkedList<Feature> featureList) {
		for (Feature f : featureList) {
			for (Aggregator aggr : aggregatorList) {
				String fName = f.getFeatureName().replaceAll(" ", "_").replaceAll("\\*", "_");
				String attributeName = fName + "_" + aggr.getLabel();
				Attribute attribute = new Attribute(attributeName);
				attributeList.add(attribute);
			}
		}
//		attributeList.add(new Attribute ("Class"));
		attributesSet = true;
	}

	@Override
	public String getName() {
		return "Demographics Extractor";
	}
}
