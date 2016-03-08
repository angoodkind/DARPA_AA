package keytouch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.TreeSet;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import extractors.data.TestVectorShutdownModule;
import keystroke.KeyStroke;

/**
 * This class implements a specific version of ngram fusion. It takes a
 * unigraph key hold and digraph key interval
 * @author Adam Goodkind
 *
 */

public class KeyTouchFusionUniHoldDiInt implements ExtractionModule, TestVectorShutdownModule {

	/**
	 *  For intermediate TestVector file processing<p><p>
	 *  < User : < LinkedList<HashMap < Feature : Value > > > >
	 */
	private static LinkedHashMap<Integer,LinkedList<LinkedHashMap<String,String>>> vectorFileMap =
			new LinkedHashMap<Integer,LinkedList<LinkedHashMap<String,String>>>();;
			public static final String vectorMapFileName = "vectorFile.map";
			private static boolean preliminaryScan = false;

			//			private static final RevisionStatus[] revStatusList = KeystrokeRevision.RevisionStatus.values();
			private HashMap<String, LinkedList<Double>> featureMap = new HashMap<String, LinkedList<Double>>(10000);
//			private static final int NGRAM_SIZE = 1;
			
			public KeyTouchFusionUniHoldDiInt() {
				featureMap.clear();
			}

			@Override
			public Collection<Feature> extract(DataNode data) {
				//		createSearchSpace();

				featureMap.clear();
				int userID = data.getUserID();
				//create user's hashmap
				if (!vectorFileMap.containsKey(userID)) {
					vectorFileMap.put(userID, new LinkedList<LinkedHashMap<String,String>>());
				}
				//create HashMap to be placed in LinkedList, only if this is not a 
				//pre-processing scan
				LinkedHashMap<String,String> sliceMap = null;
				if (!preliminaryScan) {
					sliceMap = new LinkedHashMap<String,String>();
				}

				for (Answer a : data) {
					//add Cognitive Load as a feature
//					featureMap.put("CogLoad", 
//							new LinkedList<Double>(Arrays.asList((double) a.getCogLoad())));
					
					LinkedList<KeyTouch> ktList = KeyTouch.parseSessionToKeyTouches(a.getKeyStrokes());
					
					for (int i=0; i < ktList.size()-1; i++) {
						KeyTouch k1 = ktList.get(i);
						KeyTouch k2 = ktList.get(i+1);
						String uniStr = "H__"+KeyStroke.vkCodetoString(k1.getKeyCode());
						String diStr = "I__"+KeyStroke.vkCodetoString(k1.getKeyCode())+"_"+
								KeyStroke.vkCodetoString(k2.getKeyCode());
						double uniHoldTime = k1.getHoldTime();
						double diIntervalTime = k2.getPrecedingPause();
						
						if (featureMap.containsKey(uniStr)) {
							featureMap.get(uniStr).add(uniHoldTime);
						}
						else {
							featureMap.put(uniStr,
									new LinkedList<Double>(Arrays.asList(uniHoldTime)));
						}
						
						if (featureMap.containsKey(diStr)) {
							featureMap.get(diStr).add(diIntervalTime);
						}
						else {
							featureMap.put(diStr,
									new LinkedList<Double>(Arrays.asList(diIntervalTime)));
						}
						
					} // end loop through keystroke list
				} //end answer loop

				//add this slice's HashMap to the LinkedList, if this is not a
				//pre-processing scan
				if (sliceMap != null) {
					//add features from featureMap to sliceMap
					for (String feature : featureMap.keySet()) {
						//ignore subjanswid because it is already added to slicemap
						//needed to add it to featuremap for Template creation, and
						//subsequent availability pruning, where it is necessary to
						//have all features
						if (!feature.equals("SubjAnswID"))
							sliceMap.put(feature, featureMap.get(feature).toString());
					}
					vectorFileMap.get(userID).add(sliceMap);
				}

				LinkedList<Feature> output = new LinkedList<Feature>();
				for (String featureName : featureMap.keySet()) {
					output.add(new Feature(featureName,featureMap.get(featureName)));
				}
//				for (Feature f : output) System.out.println(f.toTemplate());
				//changed to false after initial, pre-user, Scan
				preliminaryScan=false;

				return output;
			}

			/**
			 * Write vector map file
			 */
			@Override
			public void shutdown() {

				try {
					File f = new File(vectorMapFileName);
					ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f,false));
					out.writeObject(vectorFileMap);
					out.close();
					vectorFileMap.clear();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			@Override
			public String getName() {
				return "Ngram 1KH 2KI";
			}
			
}