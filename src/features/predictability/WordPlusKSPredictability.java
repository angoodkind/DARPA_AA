package features.predictability;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Set;
import java.util.TreeSet;

import events.EventList;
import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import extractors.data.TestVectorShutdownModule;
import extractors.lexical.TokenExtender;
import features.pause.KSE;
import keystroke.KeyStroke;
import keytouch.KeyTouch;
import mwe.TokenExtended;

/**
 * Templates and TestVectors must be pruned after creation. Each Template/TestVector
 * will contain at least one feature for every word. If that word is not included in 
 * every answer, then the test/train will fail.
 * </p></p>
 * <code>vectorFileMap</code> accumulates scan Feature:Value pairs for every scan,
 * and writes it to an external file when testVectorFlush() is called upon completion
 * of all scans
 * </p></p>
 * A seperate utility, output.util.MakeSymmetricTestVectorFiles, is used to convert the external map file into a
 * symetric TestVector
 * @author Adam Goodkind
 *
 */
public class WordPlusKSPredictability extends Predictability implements ExtractionModule, TestVectorShutdownModule {

	/**
	 *  For intermediate TestVector file processing<p><p>
	 *  < User : < LinkedList<HashMap < Feature : Value > > > >
	 */
	private static HashMap<Integer,LinkedList<HashMap<String,String>>> vectorFileMap = new HashMap<Integer,LinkedList<HashMap<String,String>>>();;
	private final String vectorMapFileName = "vectorFile.map";
	private static boolean preliminaryScan = true;

	private static final String modelName = "SESSION1"; 
	private static final String gramType = "hybrid";
	private static TokenExtender extender = new TokenExtender();
	static final int NGRAM = 2;
	static final boolean CSV = false;
	static final boolean LIN_REGRESS_FILE = false;
	static final String csv_file = "kt.wordKsProb.bigram";
	private static final boolean LOG_TIME = true;
	private static final boolean IND_TIMES = true;
	private static final boolean INTRAWORD = true;
	private static final boolean DEBUG = false;

	private static TreeSet<Integer> keySet = new TreeSet<Integer>(KeyStroke.TopVKCodes());
	private HashMap<String, LinkedList<Double>> featureMap = new HashMap<String, LinkedList<Double>>(100000);

	public WordPlusKSPredictability() {
		super(modelName, gramType);
		featureMap.clear();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Feature> extract(DataNode data) {
		featureMap.clear();
		int userID = data.getUserID();

		//create user's hashmap
		if (!vectorFileMap.containsKey(userID)) {
			vectorFileMap.put(userID, new LinkedList<HashMap<String,String>>());
		}
		//create HashMap to be placed in LinkedList, only if this is not a 
		//pre-processing scan
		HashMap<String,String> sliceMap = null;
		if (!preliminaryScan)
			sliceMap = new HashMap<String,String>();

		BufferedWriter outf = null;
		try {
			outf = new BufferedWriter(new FileWriter(csv_file, true));

			for (Answer a : data) {

				EventList<KeyStroke> keyStrokeList = a.getKeyStrokeList();
				String visibleText = keyStrokeList.toVisibleTextString();			
				ArrayList<TokenExtended> tokens = extender.generateExtendedTokens(visibleText);

				LinkedList<KeyTouch> ktList = new LinkedList<KeyTouch>(KeyTouch.parseSessionToKeyTouches(a.getKeyStrokes()));
				LinkedList<KeyTouch> visibleKTs = new LinkedList<KeyTouch>(KeyTouch.parseToVisibleKeyTouches(ktList));

				for (TokenExtended t : tokens) {
					int tokenSize = t.size();
					//need 2 characters for at least 1 bigram
					if (tokenSize >= NGRAM) {
						System.out.println(t.token);
						double wordProb = wordBigramModel.getUnigramProbability(t.token);
						//get individual char probabilities
						for (int i = t.tokenSpan.getBegin()+1; i <= t.tokenSpan.getEnd()-(NGRAM)+1; i++) {
	
							KeyTouch kt1 = visibleKTs.get(i-1); 
							KeyTouch kt2 = visibleKTs.get(i);
//							KeyTouch ktNext = visibleKTs.get(i+1);
							System.out.println(kt1.getKeyChar()+"|"+kt2.getKeyChar()/**+"|"+ktNext.getKeyChar()**/);

							String digraph = kt1.getKeyChar()+"_"+kt2.getKeyChar();
//							String nextDigraph = kt.getKeyChar()+"_"+ktNext.getKeyChar();

							double digraphProb = keyStrokeBigramModel.getBigramProbability(kt1.getKeystroke(),kt2.getKeystroke());
//							double ksNextProb = keyStrokeBigramModel.getBigramProbability(kt.getKeystroke(),ktNext.getKeystroke());
							//							double ksTriProb = keyStrokeTrigramModel.getTrigramProbability(ktPrev.getKeystroke(),kt.getKeystroke(),ktNext.getKeystroke());

							double digraphDuration;
//							double nextBigramDuration;
							digraphDuration = (double) (kt1.getHoldTime() + kt2.getHoldTime());
//							nextBigramDuration = (double) (kt.getHoldTime() + ktNext.getHoldTime());


							//int prevDigraph = (ktPrev.getKeyCode() * 1000) + kt.getKeyCode();
							//int nextDigraph = (kt.getKeyCode() * 1000) + ktNext.getKeyCode();

							//where in the word this bigram occurs
							double relativeIndex = (t.tokenSpan.getBegin()-i)/(t.size()*1.0);

							//System.out.println(t.token+" "+kt.getKeyChar()+" "+relativeIndex);

							double prev0, prev1, next0, next1;
							double expectedTime;  // time of the "Next Duration"

							if (LOG_TIME) {
								if (IND_TIMES) {
									//prev0 = 5.217780;
									//prev1 = -8.239744;
									prev0 = 4.5682;
									prev1 = -0.3650;
									// KTtwoindpred
									expectedTime = Math.exp(prev0 + prev1 * digraphProb/**ksPrevProb**/) + Math.exp(prev0 + prev1 * digraphProb/**ksNextProb**/);
									// KTfirstindpred
									// expectedTime = Math.exp(prev0 + prev1 * prevProb);
								}
							}

							if (CSV) {
//								outf.write(t.token);
//								outf.write(',');
//								outf.write(Integer.toString(tokenSize));
//								outf.write(',');
//								outf.write(Predictability.probabilityToString(wordProb));
//								outf.write(',');
//								outf.write(kt.getKeyChar());
//								outf.write(',');
//								outf.write(ktNext.getKeyChar());
//								outf.write(',');
//								outf.write(Long.toString(kt.getHoldTime()));
//								outf.write(',');
//								outf.write(Long.toString(ktNext.getHoldTime()));
//								outf.write(',');
//								outf.write(Predictability.probabilityToString(ksPrevProb));
//								outf.write(',');
//								outf.write(Double.toString(relativeIndex));
//								outf.write('\n');
							}
							if (LIN_REGRESS_FILE) {
//								outf.write(KeyStroke.vkCodetoString(ktPrev.getKeyCode())); // k_i-1
//								outf.write(',');
//								outf.write(KeyStroke.vkCodetoString(kt.getKeyCode())); // k_i.
//								outf.write(',');
//								outf.write(KeyStroke.vkCodetoString(ktNext.getKeyCode())); // k_i+1.
//								outf.write(',');
//								outf.write(Double.toString(nextBigramDuration));  // duration of ki and ki+1
//								outf.write(',');
//								outf.write(Double.toString(ksPrevProb/(relativeIndex*wordProb))); // p (k_i | k_i+1) / p(w)
//								outf.write(',');
//								outf.write(Double.toString(ksNextProb/(relativeIndex*wordProb)));  // p(k_i+1 | k_i) / p(w)
//								//outf.write(',');
//								//outf.write(Double.toString(ksTriProb/(relativeIndex*wordProb)));  // p(k_i+1 | k_i,k_i-1) / p(w)
//								outf.write('\n');
							}
							double predDigraphDuration = (digraphDuration - expectedTime);
//							double predPrevDuration = (prevBigramDuration - expectedTime);
//							double predNextDuration = (nextBigramDuration - expectedTime);

							if (featureMap.containsKey(t.token+"_"+digraph)) {
								featureMap.get(t.token+"_"+digraph).add(predDigraphDuration);
							} else {
								//System.out.println(t.token+"_"+nextDigraph);
								featureMap.put(t.token+"_"+digraph, 
										new LinkedList<Double>(Arrays.asList(predDigraphDuration)));
							}
						}
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (outf != null) {
				try {
					outf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}



		//add this slice's HashMap to the LinkedList, if this is not a
		//pre-processing scan
		if (sliceMap != null) {
			//add features from featureMap to sliceMap
			for (String feature : featureMap.keySet()) {
				sliceMap.put(feature, featureMap.get(feature).toString());
			}
			if (DEBUG) {
				for (Integer user : vectorFileMap.keySet()) {
					System.out.println("U: "+user+" Scans: "+vectorFileMap.get(user).size());
					ListIterator<HashMap<String,String>> it = vectorFileMap.get(user).listIterator();
					while (it.hasNext()) {
						System.out.println(it.nextIndex()+" "+it.next().size());
					}
				}
				System.out.println("SliceMap Size: "+sliceMap.size());
			}
			vectorFileMap.get(userID).add(sliceMap);
			
			if (DEBUG) {
				for (Integer user : vectorFileMap.keySet()) {
					System.out.println("U: "+user+" Scans: "+vectorFileMap.get(user).size());
					ListIterator<HashMap<String,String>> it = vectorFileMap.get(user).listIterator();
					while (it.hasNext()) {
						System.out.println(it.nextIndex()+" "+it.next().size());
					}
				}
				System.out.println();
			}
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
		return "Word Plus KS Pred";
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
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
