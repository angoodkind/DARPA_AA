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

public class KeyTouchContextKHKI implements ExtractionModule, TestVectorShutdownModule {

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

			private static final boolean INCLUDE_WORD = true;
			private static final boolean INCLUDE_LEMMA = false;
			private static final boolean INCLUDE_POS = false;
			private static final boolean INCLUDE_FCP = false;
			
			// whether to include the keystrokes surrounding a word token
			private static final boolean INCLUDE_PREV_KS = true;
			private static final boolean INCLUDE_NEXT_KS = true;
			private static final boolean EXCLUDE_PREWORD_PAUSE = false;
			private static final boolean EXCLUDE_PREPERIOD_PAUSE = false;

			public KeyTouchContextKHKI() {
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

					LinkedList<KeyTouch> ktList = KeyTouch.parseSessionToKeyTouches(a.getKeyStrokes());
					ArrayList<TokenExtended> tokens = extender.generateExtendedTokens(ktList);

					for (TokenExtended t : tokens) {

						String word = "__"+t.token;
						String lemma = "__LMA__"+t.getLemma().toLowerCase();
						String pos = "__"+t.getPartOfSpeech();
						String contFunc;
						String fcp;
						//set contFunct & pb
						if (POS_Extractor.isContentPOS(pos)) {
							contFunc = "__content";
							fcp = "__content";
						}
						else {
							contFunc = "function";
							if (pennebakers.contains(lemma))
								fcp = "__pennebaker";
							else
								fcp = "__non-pennebaker";
						}
						//set token scan starting/ending index; go back as far as possible, before
						//a space is encountered. this will pick up shift key strokes.
						int startScan = t.rawKeyTouchIndices.begin;
						while (startScan >= 0 && KeyStroke.vkCodetoString(ktList.get(startScan).getKeyCode()) != "Spacebar") { 
							startScan -= 1;
						}
						if (INCLUDE_PREV_KS && startScan > 0) {
							startScan -= 1;
						}
						
						int endScan = t.rawKeyTouchIndices.end;
						if (INCLUDE_NEXT_KS) {
							if (endScan < ktList.size()-1) {
								endScan += 1;
							}
						}
						
						for (int i = startScan+1; i < endScan; i++) {
//							System.out.print(KeyStroke.vkCodetoString(ktList.get(i).keyCode)+"_");
							KeyTouch kt1 = ktList.get(i);
							KeyTouch kt2 = ktList.get(i+1);
							
							String uni1Str = KeyStroke.vkCodetoString(kt1.getKeyCode());
							String uni2Str = KeyStroke.vkCodetoString(kt2.getKeyCode());
							String diStr = KeyStroke.vkCodetoString(kt1.getKeyCode())+"_"+
									KeyStroke.vkCodetoString(kt2.getKeyCode());
							
							double uni1HoldTime = kt1.getHoldTime();
							double uni2HoldTime = kt2.getHoldTime();
							double diHoldTime = kt1.getHoldTime()+kt2.getHoldTime();
							double diIntervalTime = kt2.getPrecedingPause();
							
							// add context modifier strings
							ArrayList<String> contextModifiers = new ArrayList<>(Arrays.asList(""));
							if (INCLUDE_WORD)
								contextModifiers.add(word);
							if (INCLUDE_LEMMA)
								contextModifiers.add(lemma);
							if (INCLUDE_POS)
								contextModifiers.add(pos);
							if (INCLUDE_FCP)
								contextModifiers.add(fcp);
							
							// add features for this ngram based on context modifiers
							HashMap<String,Double> ngramFeatureMap = new HashMap<String,Double>();
							for (String modifier : contextModifiers) {
								ngramFeatureMap.put("UH_D__"+diStr+modifier, uni2HoldTime);
								ngramFeatureMap.put("UH__"+uni2Str+modifier, uni2HoldTime);
//								ngramFeatureMap.put("DiH__"+diStr+modifier, diHoldTime);
								if ( !(uni1Str=="Spacebar" && EXCLUDE_PREWORD_PAUSE) &&
										!(uni2Str=="Period" && EXCLUDE_PREPERIOD_PAUSE)) {
//									ngramFeatureMap.put("P__"+uni2Str+modifier, diIntervalTime);
									ngramFeatureMap.put("I__"+diStr+modifier, diIntervalTime);
								}
							}
							
							// add this ngram's features to feature map
							for (String f : ngramFeatureMap.keySet()) {
								if (!featureMap.containsKey(f)) {
									featureMap.put(f, new LinkedList<Double>());
								}
								featureMap.get(f).add(ngramFeatureMap.get(f));
								
									
							}							
						} // close n-graph iteration loop
					} // close token loop 
				} // close answer loop


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
				return "KeyTouch KHKI In Context";
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
