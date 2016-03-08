package features.predictability;

import edu.stanford.nlp.util.Pair;
import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import features.lexical.LinearRegression;
import features.predictability.Predictability;
import keystroke.KeyStroke;
import keytouch.KeyTouch;
import ngrammodel.Bigram;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;


/**
 * Calculates Digraph Durations normalized by predictability, and 
 * separated by pre- and intra-word 
 * <p/>
 * Based on Digraph Duration by Patrick Kock
 *
 * @see features.nyit.DigraphDuration
 */
public class DigraphZ extends Predictability implements ExtractionModule {

	private static final String modelName = "SESSION1";
	private static final String gramType = "keystroke";

	private static TreeSet<Integer> keySet = new TreeSet<Integer>(LessUsefulVKCodes());
	private HashMap<String, LinkedList<Double>> featureMap = new HashMap<String, LinkedList<Double>>(10000);

	private static final boolean LOG_TIME = false;
	public static final boolean DEBUG = false;
	static final boolean WRITE_FILE = false;
	static final String csv_file = "KtDurIntraAlpha.bigram.csv";

	/**
	 * Initialize the module.
	 */
	public DigraphZ() {
		super(modelName, gramType);
		featureMap.clear();
	}

	/**
	 * Generates full permutation of two UpperCase alpha combinations and places
	 * them into a TreeSet which ensures no duplicates and automatically sorts them!
	 */
	private void generateSearchSpace() {
		//		String[] categories = {"preWord","intraWord"};
		featureMap.clear();
		//		for (String category : categories)
		for (Integer key0 : keySet)
			for (Integer key1 : keySet)
				featureMap.put((KeyStroke.vkCodetoString(key0)+"_"+KeyStroke.vkCodetoString(key1)),
						new LinkedList<Double>());
	}

	@Override
	public Collection<Feature> extract(DataNode data) {

		//clear feature map and repopulate its keys for a new extraction
		generateSearchSpace();
		BufferedWriter outf = null;

		try {
			outf = new BufferedWriter(new FileWriter(csv_file, true));

			for (Answer a : data) {
				LinkedList<KeyTouch> ktList = KeyTouch.parseSessionToKeyTouches(a.getKeyStrokes());
				for (int i = 1; i< ktList.size()-1; i++) {
					KeyTouch prevK = ktList.get(i-1);
					int prevKCode = prevK.getKeyCode();
					String prevKStr = KeyStroke.vkCodetoString(prevKCode);

					KeyTouch k = ktList.get(i);
					int kCode = k.getKeyCode();
					String kStr = KeyStroke.vkCodetoString(kCode);

					KeyTouch nextK = ktList.get(i+1);
					int nextKCode = nextK.getKeyCode();
					String nextKStr = KeyStroke.vkCodetoString(nextKCode);

					//filter outliers and digraphs spanning word boundaries
					if (k.getPrecedingPause() <= 0 || k.getPrecedingPause() > 2000 ||
							k.getHoldTime() <= 0 || k.getHoldTime() > 2000 ||
							prevK.getKeystroke().isSpace() || k.getKeystroke().isSpace()) {
						continue;
					}

					//					String category;
					//					if (prevKStr.equals("Spacebar"))
					//						category = "preWord";
					//					else
					//						category = "intraWord";



					String digraph = prevKStr+"_"+kStr;
					double prob = keyStrokeBigramModel.getBigramProbability(prevKStr,kStr);
					double duration;

					if (LOG_TIME) 
						duration = (Math.log((double) (prevK.getHoldTime()+k.getPrecedingPause()+k.getHoldTime())));
					else 
						duration = (double) (prevK.getHoldTime()+k.getPrecedingPause()+k.getHoldTime());

					double c0, c1;
					double expectedTime;
					if (LOG_TIME) {
						c0 = 5.931336;
						c1 = -0.759717;
						expectedTime = Math.exp(c0 + c1 * prob) - 2;
					} else {
						c0 = 434.154633;
						c1 = -209.598271;
						expectedTime = (c0 + c1 * prob);						
					}
					//					if (category.equals("preWord")) {
					if (WRITE_FILE) {
						outf.write(prevKStr); // k_i-1
						outf.write(',');
						outf.write(kStr); // k_i.
						outf.write(',');
						outf.write(nextKStr); // k_i+1.
						outf.write(',');
						outf.write(Double.toString(duration));  // previous hold time.
						outf.write(',');
						outf.write(Double.toString(prob)); // backward probability
						outf.write(',');
						outf.write(Double.toString(-1.0));  // forward probability
						outf.write('\n');
					}
					//					}

					double predDuration = (duration - expectedTime);

					if (featureMap.containsKey(digraph))
						featureMap.get(digraph).add(duration);
				}
			}			
		} catch (IOException e) {e.printStackTrace();} 
		finally {
			if (outf != null) {
				try {
					outf.close();
				} catch (IOException e) {e.printStackTrace();}
			}
		}
		//create output feature list.
		LinkedList<Feature> output = new LinkedList<Feature>();

		//iterate over the featureMap
		for (String s : featureMap.keySet()) {
			LinkedList<Double> originalList = featureMap.get(s);
			LinkedList<String> newList = new LinkedList<String>();
			double mean = -1.0;
			double stdDev = -1.0;
			if (originalList.size() > 1) {
				mean = Predictability.mean(originalList);
				stdDev = Predictability.StdDev(originalList);
				//get z score
				for (double d : originalList) {
					double z = (d-mean)/stdDev;
					if (!Double.isNaN(z)) {
						DecimalFormat df = new DecimalFormat("#.#");
						df.setMaximumFractionDigits(8);
						newList.add(df.format(z));
					}
				}
			}
			output.add(new Feature("Di_Pred_Z_"+s, newList));
		}
		return output;
	}

	/**
	 * Calculates and prints linear regression coefficients
	 */

	@Override
	public String getName() {
		return "KeyTouch Duration Categorized Pred";
	}

	/**
	 * This returns a Set Object containing vkCodes that are useful to our
	 * study.
	 * <p>
	 * <p>
	 * This lowers the search space and eliminates erroneous key checks.
	 * 
	 * @return Set of vkCodes that are useful in checking during keystroke
	 *         experiments.
	 */
	public static Set<Integer> LessUsefulVKCodes() {
		TreeSet<Integer> vkSet = new TreeSet<>();
		//		vkSet.add(8); //backspace
		//		vkSet.add(9);
		//		vkSet.add(10);
		//		vkSet.add(16);
		//		vkSet.add(17);
		//		vkSet.add(18);
		//		vkSet.add(19);
		//		vkSet.add(20);
		//		vkSet.add(27);
		vkSet.add(32); //space
		vkSet.add(44); //comma
		vkSet.add(46); //period
		for (int i = 65; i < 91; i++) //A-Z
			vkSet.add(i);
		//		for (int i = 32; i < 41; i++)
		//			vkSet.add(i);
		//		for (int i = 44; i < 58; i++)
		//			vkSet.add(i);
		//		vkSet.add(59);
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
