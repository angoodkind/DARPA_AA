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

public class KeyTouchContextNgram implements ExtractionModule, TestVectorShutdownModule {

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

			private static final int GRAM_LENGTH = 2;
			private static final boolean INCLUDE_HOLD = true;
			private static final boolean INCLUDE_INTERVAL = false;
			private static final boolean INCLUDE_WORD = true;
			private static final boolean INCLUDE_LEMMA = false;
			private static final boolean INCLUDE_POS = false;
			private static final boolean INCLUDE_PB = true;
			private static final boolean ADD_SUBJ_ANS_ID = false;
			private static final ArrayList<String> pennebakers = FunctionWordMetrics.PennebakerWords();

			public KeyTouchContextNgram() {
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

					if (!preliminaryScan && ADD_SUBJ_ANS_ID) {
						sliceMap.put("SubjAnswID", "S"+data.getUserID()+"A"+a.getAnswerID());
						//					if (!featureMap.containsKey("SubjAnswID"))
						featureMap.put("SubjAnswID", new LinkedList<Double>());
						//					else
						//						featureMap.get("SubjAnswID").add
					}

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
						if (tokenSize >= GRAM_LENGTH 
								&& t.tokenSpan.getBegin() > 0 && t.tokenSpan.getEnd() < visibleKTs.size()) {
							//for each word
							for (int i = t.tokenSpan.getBegin()-1; i < t.tokenSpan.getEnd(); i++) {
								//System.out.println(word+" "+visibleKTs.get(i).getKeyChar());	
								//for each ngram
								LinkedList<KeyTouch> ngraph = new LinkedList<KeyTouch>();
								String ngraphStr = "";
								for (int n = 0; n < GRAM_LENGTH; n++) {
									ngraph.add(visibleKTs.get(i+n));
									ngraphStr += (KeyStroke.vkCodetoString(visibleKTs.get(i+n).getKeyCode())
											+"_");
								}
								ngraphStr = ngraphStr.substring(0,ngraphStr.length()-1); //delete trailing _
								//							System.out.println(word+" "+ngraphStr);

								double ngraphDur = 0.0;
								for (int k = 0; k < ngraph.size(); k++) {
									if (INCLUDE_HOLD)
										ngraphDur += (double)ngraph.get(k).getHoldTime();
									if (INCLUDE_INTERVAL)
										ngraphDur += (double)ngraph.get(k).getPrecedingPause();
								}

								if (featureMap.containsKey("__"+ngraphStr)) {
									featureMap.get("__"+ngraphStr).add(ngraphDur);
								} else {
									featureMap.put("__"+ngraphStr, 
											new LinkedList<Double>(Arrays.asList(ngraphDur)));
								}


								if (INCLUDE_POS) {
									if (featureMap.containsKey(pos+"__"+ngraphStr)) {
										featureMap.get(pos+"__"+ngraphStr).add(ngraphDur);
									} else {
										featureMap.put(pos+"__"+ngraphStr, 
												new LinkedList<Double>(Arrays.asList(ngraphDur)));
									}
								}

								if (INCLUDE_LEMMA) {
									if (featureMap.containsKey(lemma+"__"+ngraphStr)) {
										featureMap.get(lemma+"__"+ngraphStr).add(ngraphDur);
									} else {
										featureMap.put(lemma+"__"+ngraphStr, 
												new LinkedList<Double>(Arrays.asList(ngraphDur)));
									}
								}

								if (INCLUDE_WORD) {
									if (featureMap.containsKey(word+"__"+ngraphStr)) {
										featureMap.get(word+"__"+ngraphStr).add(ngraphDur);
									} else {
										featureMap.put(word+"__"+ngraphStr, 
												new LinkedList<Double>(Arrays.asList(ngraphDur)));
									}
								}
								if (INCLUDE_PB) {
									if (featureMap.containsKey(pb+"__"+ngraphStr)) {
										featureMap.get(pb+"__"+ngraphStr).add(ngraphDur);
									} else {
										featureMap.put(pb+"__"+ngraphStr, 
												new LinkedList<Double>(Arrays.asList(ngraphDur)));
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
				return "KeyTouch In Word";
			}

			/**
			 * Write vector map file
			 */
			@Override
			public void shutdown() {

				//		for (int i : vectorFileMap.keySet()) {
				//			System.out.println("User: "+i);
				//			int slice = 1;
				//			for (HashMap<String,String> map : vectorFileMap.get(i)) {
				//				System.out.println("Slice: "+slice++);
				//				for (String key : map.keySet()) {
				//					System.out.println(key);
				//				}
				//			}
				//		}

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
