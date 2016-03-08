package features.predictability;

import edu.stanford.nlp.util.Pair;
import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import features.lexical.LinearRegression;
import keystroke.KeyStroke;
import keytouch.KeyTouch;
import ngrammodel.Bigram;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


/**
 * Calculates Digraph Durations normalized by predictability.
 * <p/>
 * Based on Digraph Duration by Patrick Kock
 *
 * @see features.nyit.DigraphDuration
 */
public class KtDurationPred extends Predictability implements ExtractionModule {

	private static final String modelName = "SESSION1";
	private static final String gramType = "keystroke";


	private static final boolean IND_TIMES = false;

	private static final boolean INTRAWORD = false;
	private static final boolean NOPRED = true;

	private static final boolean TRIGRAM = false;
	private static final boolean INCLUDE_INTERVAL = true;
	private static final boolean LOG_TIME = false;
	public static final boolean DEBUG = false;

	private static TreeSet<Integer> keySet = new TreeSet<Integer>(KeyStroke.UsefulVKCodes()/**LessUsefulVKCodes()**/);
	private HashMap<Integer, LinkedList<Double>> featureMap = new HashMap<Integer, LinkedList<Double>>(10000);

	static final boolean GET_LR = false;
	static ArrayList<Double> x = new ArrayList<Double>();
	static ArrayList<Double> y = new ArrayList<Double>();
	final int TOTAL_USERS = 486;
	static int currentUser = 0;

	static final boolean WRITE_FILE = false;
	static final String csv_file = "digraph-count.csv";


	/**
	 * Initialize the module.
	 */
	public KtDurationPred() {
		super(modelName, gramType);
		featureMap.clear();
	}

	/**
	 * Generates full permutation of two UpperCase alpha combinations and places
	 * them into a TreeSet which ensures no duplicates and automatically sorts them!
	 */
	private void generateSearchSpace() {
		featureMap.clear();
		for (Integer key0 : keySet)
			for (Integer key1 : keySet) {
				//				if (INTRAWORD && key0 != 32 /* SPACE */) 
				featureMap.put(key0 * 1000 + key1, new LinkedList<Double>());

			}
	}

	@Override
	public Collection<Feature> extract(DataNode data) {

		//clear feature map and repopulate it's keys for a new extraction
		generateSearchSpace();

		BufferedWriter outf = null;
		try {
			outf = new BufferedWriter(new FileWriter(csv_file, true));

			for (Answer a : data) {

				LinkedList<KeyTouch> ktList = KeyTouch.parseSessionToKeyTouches(a.getKeyStrokes());
				for (int i = 1; i < ktList.size() - 1; i++) {
					KeyTouch prevK = ktList.get(i - 1);
					int prevKCode = prevK.getKeyCode();
					KeyTouch k = ktList.get(i);
					int kCode = k.getKeyCode();
					KeyTouch nextK = ktList.get(i + 1);
					int nextKCode = nextK.getKeyCode();

					int nextDigraph = k.getKeyCode() * 1000 + nextK.getKeyCode();

					String prevKStr = KeyStroke.vkCodetoString(prevKCode);
					String kStr = KeyStroke.vkCodetoString(kCode);
					String nextKStr = KeyStroke.vkCodetoString(nextKCode);

					double prevProb = keyStrokeBigramModel.getBigramProbability(prevKStr, kStr);
					double nextProb = keyStrokeBigramModel.getBigramProbability(kStr, nextKStr);
					double triProb = keyStrokeTrigramModel.getTrigramProbability(prevKStr, kStr, nextKStr);


					double nextBigramDuration;
					
					if (INCLUDE_INTERVAL)
						nextBigramDuration = (double) (k.getHoldTime() + nextK.getPrecedingPause() + nextK.getHoldTime());
					else
						nextBigramDuration = (double) (k.getHoldTime() + nextK.getHoldTime());

					double prev0, prev1, next0, next1;
					double expectedTime;  // time of the "Next Duration"
					if (LOG_TIME) {
						//            prev0 = 5.219156;
						//            prev1 = -0.488329;
						//            expectedTime = Math.exp(prev0 + prev1 * prevProb);
						//						next0 = 5.211374;
						//						next1 = -0.444915;
						//						expectedNextTime = Math.exp(next0 + next1 * nextProb) - 2;

						if (IND_TIMES) {
							// AR: taken from KeyHold. Just subtract out the expected hold time for each key independent of each
							// other.
							prev0 = 4.5682;
							prev1 = -0.3650;
							// KTtwoindpred
							expectedTime = Math.exp(prev0 + prev1 * prevProb) + Math.exp(prev0 + prev1 * nextProb);
							// KTfirstindpred
							//              expectedTime = Math.exp(prev0 + prev1 * prevProb);
						} else {
							// AR: predict next time from prev prob
							prev0 = 5.196292;
							prev1 = -0.369197;
							expectedTime = Math.exp(prev0 + prev1 * prevProb);
						}

						if (TRIGRAM) {
							// AR: Trigram probabilities and correlation.
							prev0 = 5.2147;
							prev1 = -0.3168;
							expectedTime = Math.exp(prev0 + prev1 * triProb) + 15;
						}
					} else {
						prev0 = 208.738662;
						prev1 = -63.977224;
						expectedTime = (prev0 + prev1 * prevProb);
						//						next0 = 206.540032;
						//						next1 = -51.695657;
						//						expectedNextTime = (next0 + next1 * nextProb);
					}

					if (WRITE_FILE) {
						outf.write(prevKStr); // k_i-1
						outf.write(',');
						outf.write(kStr); // k_i.
						outf.write(',');
						outf.write(nextKStr); // k_i+1.
						outf.write(',');
						outf.write(Double.toString(nextBigramDuration));  // duration of ki and ki+1
						outf.write(',');
						outf.write(Double.toString(prevProb)); // p (ki | ki+1)
						outf.write(',');
						outf.write(Double.toString(nextProb));  // p(ki+1 | ki)
						outf.write(',');
						outf.write(Double.toString(triProb));  // p(ki+1 | ki)
						outf.write('\n');
					}

					double predNextDuration = (nextBigramDuration - expectedTime);

					if (NOPRED) {
						predNextDuration = nextBigramDuration;
					}

					if (featureMap.containsKey(nextDigraph)) {
						featureMap.get(nextDigraph).add(predNextDuration);
					}
				}
			} //close answer loop

//			//iterate over the featureMap
//			for (Integer vkCode0 : keySet) {
//				for (Integer vkCode1 : keySet) {
//					outf.write(KeyStroke.vkCodetoString(vkCode0) + "_" + KeyStroke.vkCodetoString(vkCode1));
//					outf.write(": "+featureMap.get(vkCode0 * 1000 + vkCode1).size()+"\n");
//
//				}
//			}

		}	 catch (IOException e) {
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
		//create output feature list.
		LinkedList<Feature> output = new LinkedList<Feature>();

		//iterate over the featureMap
		for (Integer vkCode0 : keySet) {
			for (Integer vkCode1 : keySet) {
				//				if (INTRAWORD && vkCode0 != 32 /* SPACE */) 
				output.add(new Feature("KT_DN_" + KeyStroke.vkCodetoString(vkCode0) + "_" + KeyStroke.vkCodetoString(vkCode1),
						featureMap.get(vkCode0 * 1000 + vkCode1)));
			}
		}
		return output;
	}
	/**
	 * Calculates and prints linear regression coefficients
	 */
	private void generateRegression(ArrayList<Double> x, ArrayList<Double> y) {
		System.out.println("Calculating Linear Regression...");
		if (x.size() != y.size()) {
			System.err.println("X!=Y");
		}

		double[] xArr = new double[x.size()];
		double[] yArr = new double[y.size()];

		for (int i = 0; i < x.size(); i++) {
			xArr[i] = x.get(i);
			yArr[i] = y.get(i);
		}
		Pair<Double, Double> d = LinearRegression.fit2DLinearModel(xArr, yArr);
		System.out.println(d.toString());
	}

	@Override
	public String getName() {
		return "KeyTouch Duration Pred";
	}

	/**
	 * This returns a Set Object containing vkCodes that are useful to our
	 * study.
	 * <p/>
	 * <p/>
	 * This lowers the search space and eliminates erroneous key checks.
	 *
	 * @return Set of vkCodes that are useful in checking during keystroke
	 * experiments.
	 */
	public static Set<Integer> LessUsefulVKCodes() {
		TreeSet<Integer> vkSet = new TreeSet<>();
		vkSet.add(8); //backspace
		//		vkSet.add(9);
		//		vkSet.add(10);
		vkSet.add(16); //shift
		//		vkSet.add(17);
		//		vkSet.add(18);
		//		vkSet.add(19);
		//		vkSet.add(20);
		//		vkSet.add(27);
		vkSet.add(32); //space
		vkSet.add(44); //comma
		vkSet.add(46); //period
		for (int i = 65; i < 91; i++) //A-Z (65-91)
			vkSet.add(i);
		//		for (int i = 32; i < 41; i++)
		//			vkSet.add(i);
		//		for (int i = 44; i < 58; i++)
		//			vkSet.add(i);
		vkSet.add(59); //semi-colon
		//		vkSet.add(61);
		//		for (int i = 65; i < 94; i++)
		//			vkSet.add(i);
		//		for (int i = 96; i < 108; i++)
		//			vkSet.add(i);
		//		for (int i = 109; i < 124; i++)
		//			vkSet.add(i);
		//		vkSet.add(127);
		//		vkSet.add(144);
		//		vkSet.add(145);
		//		vkSet.add(155);
		//		vkSet.add(192);
		vkSet.add(222); //apostrophe
		//		vkSet.add(524);
		//		vkSet.add(525);
		return vkSet;
	}

}
