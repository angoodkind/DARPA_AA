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
 * Contextualizes each unigraph hold in digraph context
 * @author Adam Goodkind
 *
 */

public class KeyTouchFusionSepKHKIDigraph implements ExtractionModule, TestVectorShutdownModule {

	/**
	 *  For intermediate TestVector file processing<p><p>
	 *  < User : < LinkedList<HashMap < Feature : Value > > > >
	 */
	private static LinkedHashMap<Integer,LinkedList<LinkedHashMap<String,String>>> vectorFileMap =
			new LinkedHashMap<Integer,LinkedList<LinkedHashMap<String,String>>>();;
			public static final String vectorMapFileName = "vectorFile.map";
			private static boolean preliminaryScan = false;
			private HashMap<String, LinkedList<Double>> featureMap = new HashMap<String, LinkedList<Double>>(10000);

			private static final boolean LOG_TIME = true;
			
			public KeyTouchFusionSepKHKIDigraph() {
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
					LinkedList<KeyTouch> ktList = KeyTouch.parseSessionToKeyTouches(a.getKeyStrokes());
					
					for (int i=0; i < ktList.size()-1; i++) {
						KeyTouch k1 = ktList.get(i);
						KeyTouch k2 = ktList.get(i+1);
						
						String diRawStr = KeyStroke.vkCodetoString(k1.getKeyCode())+"_"+
								KeyStroke.vkCodetoString(k2.getKeyCode());
						String uni1Str = "H__"+KeyStroke.vkCodetoString(k1.getKeyCode())
										+"."+diRawStr;
						String uni2Str = "H__"+KeyStroke.vkCodetoString(k2.getKeyCode())
						+"."+diRawStr;
						String diStr = "I__"+diRawStr;
						
						double uni1HoldTime = k1.getHoldTime();
						double uni2HoldTime = k2.getHoldTime();
						double diIntervalTime = k2.getPrecedingPause();
						
						if (LOG_TIME) {
							uni1HoldTime = k1.getHoldTime()<=0.0? 0:Math.log(k1.getHoldTime());
							uni2HoldTime = k2.getHoldTime()<=0.0? 0:Math.log(k2.getHoldTime());
							diIntervalTime = k2.getPrecedingPause()<=0.0? 0:Math.log(k2.getPrecedingPause());
						}
						
						if (featureMap.containsKey(uni1Str)) {
							featureMap.get(uni1Str).add(uni1HoldTime);
						}
						else {
							featureMap.put(uni1Str,
									new LinkedList<Double>(Arrays.asList(uni1HoldTime)));
						}
						
						if (featureMap.containsKey(uni2Str)) {
							featureMap.get(uni2Str).add(uni2HoldTime);
						}
						else {
							featureMap.put(uni2Str,
									new LinkedList<Double>(Arrays.asList(uni2HoldTime)));
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
				return "2KH in Di Context 2KI";
			}
			
}