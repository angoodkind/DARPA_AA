package features.revision;

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
import features.revision.Revision.KeystrokeRevision;
import features.revision.Revision.KeystrokeRevision.RevisionStatus;
import keystroke.KeyStroke;
import keytouch.KeyTouch;

/**
 * An extension of the verification algorithms that also incorporates revision
 * status into the ngraph information
 * 
 * @author Adam Goodkind
 *
 */
public class RevisionNgram implements ExtractionModule, TestVectorShutdownModule {

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
			private static final int NGRAM_SIZE = 2;
			private static final boolean INCLUDE_HOLD = true;
			private static final boolean INCLUDE_INTERVAL = false;
			private static final boolean EXCLUDE_DELETIONS = false;
			private static final boolean EXCLUDE_REPLACEMENTS = false;		

			public RevisionNgram() {
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
					ArrayList<KeystrokeRevision> krList = Revision.extendKeystrokesToRevisions(ktList);
					for (int i=0; i < krList.size()-NGRAM_SIZE; i++) {
						// construct ngram
						String ngramStr = "";
						double ngramTiming = 0.0;
						int nSize = NGRAM_SIZE;
						int actualGramSize = 0;

						for (int n=0; n < nSize; n++) {
							// since nSize could be expanded below, check that we are still
							// within the list's range
							if ((i+n) < krList.size()) {
								KeystrokeRevision k = krList.get(i+n);
								// if excluding certain types of keystrokes, increment temporary
								// n size and skip this keystroke
								if ((k.getRevisionStatus().equals(RevisionStatus.Deletion) && EXCLUDE_DELETIONS)
										|| (k.getRevisionStatus().equals(RevisionStatus.Replacement) && EXCLUDE_REPLACEMENTS)) {
									nSize++;
									continue;
								}
								// construct ngram name
								ngramStr += (k.toString()+"_");
								actualGramSize++;
								// construct timing metric
								if (INCLUDE_HOLD)
									ngramTiming += ((double) krList.get(i+n).kt.getHoldTime());
								// for n > 1, do not include first "interval". This is the 
								// preceding pause, which is not an "interval", that is, "within"
								// the ngram
								if ((INCLUDE_INTERVAL && NGRAM_SIZE > 1 & n != 0) || 
										(INCLUDE_INTERVAL && NGRAM_SIZE == 1)) 
									ngramTiming += ((double) krList.get(i+n).kt.getPrecedingPause());
								
							}
							// only add a complete n-gram
							if (actualGramSize == NGRAM_SIZE) {
								//trim trailing _ from ngram string
								ngramStr = ngramStr.substring(0, ngramStr.length()-1);

								// add to existing entry or create new entry
								if (featureMap.keySet().contains(ngramStr))
									featureMap.get(ngramStr).add(ngramTiming);
								else
									featureMap.put(ngramStr, 
											new LinkedList<Double>(Arrays.asList(ngramTiming)));
							}
						}
					}
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

				//changed to false after initial, pre-user, Scan
				preliminaryScan=false;

				return output;
			}

			//			private void createSearchSpace() {	
			//				featureMap.clear();
			//				// create feature name strings (keystroke + rev status)
			//				for (RevisionStatus r : revStatusList) {
			//					for (Integer key : keySet) {
			//						keystrokeRevisionStrList.add(KeyStroke.vkCodetoString(key)+"_"+r.toString());
			//					}
			//				}
			//				// create keystroke-revStatus combinations
			//				for (String s0 : keystrokeRevisionStrList) {
			//					for (String s1 : keystrokeRevisionStrList) {
			//						featureMap.put(s0+"_"+s1, new LinkedList<Double>());
			//					}
			//				}
			//			}

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
				return "Revision Ngram";
			}

}
