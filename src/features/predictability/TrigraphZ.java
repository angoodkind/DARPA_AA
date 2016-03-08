package features.predictability;

import features.predictability.Predictability;
import keystroke.KeyStroke;
import keytouch.KeyTouch;
import ngrammodel.Bigram;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;


/**
 * This modules calculates both straightforward trigraph duration,
 * as well as digraph within trigraph, e.g. P(K1, K2 | K0)
 * This is calculated as P(K1, K2 | K0) = p(K2 | K1, K0) * P( K1 | K0)
 * 
 */
public class TrigraphZ extends Predictability implements ExtractionModule {

	private static final String modelName = "SESSION1";
	private static final String gramType = "keystroke";

	private static TreeSet<Integer> keySet = new TreeSet<Integer>(LessUsefulVKCodes());
	private HashMap<String, LinkedList<Double>> featureMap = new HashMap<String, LinkedList<Double>>(10000);

	private static final boolean LOG_TIME = true;
	public static final boolean DEBUG = false;
	public static final boolean WRITE_FILE = false;
	public static final String csv_file = "DebugErr.data";

	/**
	 * Initialize the module.
	 */
	public TrigraphZ() {
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
			for (Integer key1 : keySet)
				for (Integer key2 : keySet)
					featureMap.put((KeyStroke.vkCodetoString(key0)+"_"+KeyStroke.vkCodetoString(key1)
							+"_"+KeyStroke.vkCodetoString(key2)),new LinkedList<Double>());
	}

	@Override
	public Collection<Feature> extract(DataNode data) {

		//clear feature map and repopulate its keys for a new extraction
		generateSearchSpace();
		BufferedWriter outf = null;

		try {
			if (WRITE_FILE)
				outf = new BufferedWriter(new FileWriter(csv_file, true));

			for (Answer a : data) {

				LinkedList<KeyTouch> ktList = KeyTouch.parseSessionToKeyTouches(a.getKeyStrokes());
				for (int i = 1; i< ktList.size()-1; i++) {
					KeyTouch k0 = ktList.get(i-1);
					int k0Code = k0.getKeyCode();
					String k0Str = KeyStroke.vkCodetoString(k0Code);

					KeyTouch k1 = ktList.get(i);
					int k1Code = k1.getKeyCode();
					String k1Str = KeyStroke.vkCodetoString(k1Code);

					KeyTouch k2 = ktList.get(i+1);
					int k2Code = k2.getKeyCode();
					String k2Str = KeyStroke.vkCodetoString(k2Code);

					//filter timing outliers
					if (k1.getPrecedingPause() <= 0 || k1.getPrecedingPause() > 2000 ||
							k1.getHoldTime() <= 0 || k1.getHoldTime() > 2000) {
						continue;
					}

					//filter trigraphs spanning pre-word boundaries
					if (k0.getKeystroke().isSpace() || k1.getKeystroke().isSpace()) {
						continue;
					}

					String digraph = k0Str+"_"+k1Str;
					String trigraph = k0Str+"_"+k1Str+"_"+k2Str;;
					double diProb = keyStrokeBigramModel.getBigramProbability(k0Str,k1Str);
					double triProb = keyStrokeTrigramModel.getTrigramProbability(k0Str,k1Str,k2Str);
					double conditionalProb = diProb * triProb;
					double diDuration = (double) (k0.getHoldTime()+k1.getPrecedingPause()+k1.getHoldTime());
					double triDuration = (double) (diDuration+k2.getPrecedingPause()+k2.getHoldTime());

					double diHoldDuration = (double) (k0.getHoldTime()+k1.getHoldTime());
					double triHoldDuration = (double) (diHoldDuration+k2.getHoldTime());

					if (LOG_TIME) {
						diDuration = Math.log((double)k0.getHoldTime())
								//									+Math.log((double)k1.getPrecedingPause())
								+Math.log((double)k1.getHoldTime());
						triDuration = Math.log((double) diDuration 
								//									+Math.log((double)k2.getPrecedingPause())
								+Math.log((double)k2.getHoldTime()));
						//						diDuration = Math.log(diDuration);
						//						triDuration = Math.log(triDuration);
					}

					double c0, c1;
					double expectedTime;
					if (LOG_TIME) {
						c0 = 6.480041;
						c1 = -1.933191;
						expectedTime = Math.exp(c0 + c1 * conditionalProb) - 2;
					} else {
						c0 = 772.827484;
						c1 = -811.220902;
						expectedTime = (c0 + c1 * conditionalProb);						
					}

					if (WRITE_FILE) {
						outf.write(k0Str); // k_i-1
						outf.write(',');
						outf.write(k1Str); // k_i.
						outf.write(',');
						outf.write(k2Str); // k_i+1.
						outf.write(',');
						outf.write(Double.toString(triHoldDuration));  // time measure
						outf.write(',');
						outf.write(Double.toString(triProb)); // backward probability to correlate 
						outf.write(',');							  // above to
						outf.write(Double.toString(-1.0));  // forward probability
						outf.write('\n');
					}



					double predDiDuration = (diDuration - expectedTime);
					double predTriDuration = (triDuration - expectedTime);

					if (featureMap.containsKey(trigraph))
						featureMap.get(trigraph).add(predTriDuration);
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
			//divide predicted time by standard deviation for that trigram
			double stdDev = -1.0;
			if (originalList.size() > 1) {
				stdDev = Predictability.StdDev(originalList);

				for (double d : originalList) {
					double predZ = d/stdDev;
					if (!(Double.isNaN(predZ) || Double.isInfinite(predZ))) {
						DecimalFormat df = new DecimalFormat("#.#");
						df.setMaximumFractionDigits(8);
						newList.add(df.format(predZ));
					}
				}
			}
			output.add(new Feature("Tri_PredZ_"+s, newList));
		}

		return output;
	}

	/**
	 * Calculates and prints linear regression coefficients
	 */

	@Override
	public String getName() {
		return "Trigraph Duration Pred";
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
