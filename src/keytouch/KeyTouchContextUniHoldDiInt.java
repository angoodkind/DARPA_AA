package keytouch;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.TreeSet;

import events.EventList;
import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import extractors.data.TestVectorShutdownModule;
import extractors.lexical.POS_Extractor;
import extractors.lexical.TokenExtender;
import features.lexical.FunctionWordMetrics;
import keystroke.KeyStroke;
import mwe.TokenExtended;

public class KeyTouchContextUniHoldDiInt implements ExtractionModule, TestVectorShutdownModule {

	/**
	 *  For intermediate TestVector file processing<p><p>
	 *  < User : < LinkedList<HashMap < Feature : Value > > > >
	 */
	private static LinkedHashMap<Integer,LinkedList<LinkedHashMap<String,String>>> vectorFileMap =
			new LinkedHashMap<Integer,LinkedList<LinkedHashMap<String,String>>>();;
			public static final String vectorMapFileName = "vectorFile.map";
			private static boolean preliminaryScan = false;

			private static TreeSet<Integer> keySet = new TreeSet<Integer>(KeyStroke.TopVKCodes());
			private HashMap<String, LinkedList<Double>> featureMap = new HashMap<String, LinkedList<Double>>(100000);
			private static TokenExtender extender = new TokenExtender();
			private static final ArrayList<String> pennebakers = FunctionWordMetrics.PennebakerWords();

			private static final boolean INCLUDE_WORD = false;
			private static final boolean INCLUDE_LEMMA = false;
			private static final boolean INCLUDE_POS = false;
			private static final boolean INCLUDE_PB = true;
			

			public KeyTouchContextUniHoldDiInt() {
				featureMap.clear();
			}

			@Override
			public Collection<Feature> extract(DataNode data) {
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

					EventList<KeyStroke> keyStrokeList = a.getKeyStrokeList();
					String visibleText = keyStrokeList.toVisibleTextString();
					ArrayList<TokenExtended> tokens = extender.generateExtendedTokens(visibleText);

					LinkedList<KeyTouch> ktList = 
							new LinkedList<KeyTouch>(KeyTouch.parseSessionToKeyTouches(a.getKeyStrokes()));
					LinkedList<KeyTouch> visibleKTs = 
							new LinkedList<KeyTouch>(KeyTouch.parseToVisibleKeyTouches(ktList));

					for (TokenExtended t : tokens) {

						String word = t.token;
						String lemma = t.getLemma().toLowerCase();
						String pos = t.getPartOfSpeech();
						String contFunc;
						String pb;
						//set contFunct & pb
						if (POS_Extractor.isContentPOS(pos)) {
							contFunc = "content";
							pb = "content";
						}
						else {
							contFunc = "function";
							if (pennebakers.contains(lemma))
								pb = "pennebaker";
							else
								pb = "non-pennebaker";
						}

						int tokenSize = t.size();
						if (tokenSize >= 2 &&
								t.tokenSpan.getBegin() > 0 && 
								t.tokenSpan.getEnd() < visibleKTs.size()) {
							// for each word, get this keystroke and the following keystroke,
							// starting with the trailing space. This will capture each interval,
							// but not double-count the hold on the leading nor trailing space
							for (int i = t.tokenSpan.getBegin()-1; i < t.tokenSpan.getEnd(); i++) {
//								System.out.println(word+" "+visibleKTs.get(i).getKeyChar());	
								KeyTouch k1 = visibleKTs.get(i);
								KeyTouch k2 = visibleKTs.get(i+1);
								String uniStr = KeyStroke.vkCodetoString(k1.getKeyCode());
								String diStr = KeyStroke.vkCodetoString(k1.getKeyCode())+"_"+
										KeyStroke.vkCodetoString(k2.getKeyCode());
								double uniHoldTime = k1.getHoldTime();
								double diIntervalTime = k2.getPrecedingPause();

								//add unigraph hold
								if (featureMap.containsKey("H__"+uniStr)) {
									featureMap.get("H__"+uniStr).add(uniHoldTime);
								} else {
									featureMap.put("H__"+uniStr, 
											new LinkedList<Double>(Arrays.asList(uniHoldTime)));
								}
								if (INCLUDE_POS) {
									if (featureMap.containsKey("H__"+pos+"__"+uniStr)) {
										featureMap.get("H__"+pos+"__"+uniStr).add(uniHoldTime);
									} else {
										featureMap.put("H__"+pos+"__"+uniStr, 
												new LinkedList<Double>(Arrays.asList(uniHoldTime)));
									}
								}
								if (INCLUDE_LEMMA) {
									if (featureMap.containsKey("H__"+lemma+"__"+uniStr)) {
										featureMap.get("H__"+lemma+"__"+uniStr).add(uniHoldTime);
									} else {
										featureMap.put("H__"+lemma+"__"+uniStr, 
												new LinkedList<Double>(Arrays.asList(uniHoldTime)));
									}
								}
								if (INCLUDE_WORD) {
									if (featureMap.containsKey("H__"+word+"__"+uniStr)) {
										featureMap.get("H__"+word+"__"+uniStr).add(uniHoldTime);
									} else {
										featureMap.put("H__"+word+"__"+uniStr, 
												new LinkedList<Double>(Arrays.asList(uniHoldTime)));
									}
								}
								if (INCLUDE_PB) {
									if (featureMap.containsKey("H__"+pb+"__"+uniStr)) {
										featureMap.get("H__"+pb+"__"+uniStr).add(uniHoldTime);
									} else {
										featureMap.put("H__"+pb+"__"+uniStr, 
												new LinkedList<Double>(Arrays.asList(uniHoldTime)));
									}
								}

								//add digraph interval
								if (featureMap.containsKey("I__"+diStr)) {
									featureMap.get("I__"+diStr).add(diIntervalTime);
								} else {
									featureMap.put("I__"+diStr, 
											new LinkedList<Double>(Arrays.asList(diIntervalTime)));
								}
								if (INCLUDE_POS) {
									if (featureMap.containsKey("I__"+pos+"__"+diStr)) {
										featureMap.get("I__"+pos+"__"+diStr).add(diIntervalTime);
									} else {
										featureMap.put("I__"+pos+"__"+diStr, 
												new LinkedList<Double>(Arrays.asList(diIntervalTime)));
									}
								}
								if (INCLUDE_LEMMA) {
									if (featureMap.containsKey("I__"+lemma+"__"+diStr)) {
										featureMap.get("I__"+lemma+"__"+diStr).add(diIntervalTime);
									} else {
										featureMap.put("I__"+lemma+"__"+diStr, 
												new LinkedList<Double>(Arrays.asList(diIntervalTime)));
									}
								}
								if (INCLUDE_WORD) {
									if (featureMap.containsKey("I__"+word+"__"+diStr)) {
										featureMap.get("I__"+word+"__"+diStr).add(diIntervalTime);
									} else {
										featureMap.put("I__"+word+"__"+diStr, 
												new LinkedList<Double>(Arrays.asList(diIntervalTime)));
									}
								}
								if (INCLUDE_PB) {
									if (featureMap.containsKey("I__"+pb+"__"+diStr)) {
										featureMap.get("I__"+pb+"__"+diStr).add(diIntervalTime);
									} else {
										featureMap.put("I__"+pb+"__"+diStr, 
												new LinkedList<Double>(Arrays.asList(diIntervalTime)));
									}
								}
								
							}
						}
					}
				}


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

				//create output feature list.
				LinkedList<Feature> output = new LinkedList<Feature>();	
				for (String feature : featureMap.keySet()) {
					output.add(new Feature(feature,featureMap.get(feature)));
				}

				//changed to false after initial, pre-user, Scan
				preliminaryScan=false;

				return output;
			}

			@Override
			public String getName() {
				return "KeyTouch 1KH2KI In Word";
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

}
