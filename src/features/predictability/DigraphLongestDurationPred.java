package features.predictability;

import keystroke.KeyStroke;
import ngrammodel.Bigram;

import java.util.*;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;


/**
 * Calculates Digraph Durations normalized by predictability.
 * <p/>
 * Based on Digraph Duration by Patrick Kock
 *
 * @see features.nyit.DigraphDuration
 */
public class DigraphLongestDurationPred extends Predictability implements ExtractionModule {

	private static final String modelName = "SESSION1";
	private static final String gramType = "keystroke";

	public static final boolean DEBUG = false;

	private static TreeSet<Integer> keySet = new TreeSet<Integer>(LessUsefulVKCodes());
	private HashMap<Integer, LinkedList<Double>> featureMap = new HashMap<Integer, LinkedList<Double>>(1800);

	/**
	 * Initialize the module.
	 */
	public DigraphLongestDurationPred() {
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
				featureMap.put(key0 * 1000 + key1, new LinkedList<Double>());
			}
	}


	@Override
	public Collection<Feature> extract(DataNode data) {
		//clear feature map and repopulate it's keys for a new extraction
		generateSearchSpace();

		//create a buffer to hold the keystrokes during processing
		LinkedList<KeyStroke> buffer = new LinkedList<KeyStroke>();
		//Holds numerical value of digraph (k0*1000 + k1)
		int digraph;
		int longestDigraph = 0;
		for (Answer a : data) {
			for (KeyStroke k : a.getKeyStrokeList()) {
				/////////////////Main Logic///////////////////////

				if (k.isKeyPress()) {                  //only look at keypresses
					buffer.add(k);                    //add a keypress to the buffer
					if (buffer.size() > 2)                //if the buffer contains more than 2 keypresses
					{
						buffer.poll();                  //remove the first keystroke in the buffer
					}
					if (buffer.size() == 2) {              //if buffer has 2 keystrokes in it
						digraph = (buffer.get(0).getKeyCode()*1000 //calculate int equivalent
								+ buffer.get(1).getKeyCode());           
						// Find the longest digraph
						if (digraph > longestDigraph) {
							longestDigraph = digraph;
						}
						if (featureMap.containsKey(digraph)) {
							//if the digraph is in the searchSpace Set
							KeyStroke kt1 = buffer.getFirst();
							KeyStroke kt2 = buffer.getLast();

							double prob = keyStrokeBigramModel.getBigramProbability(kt1,kt2);
							double bigramInterval = kt2.getWhen() - kt1.getWhen();
							if (DEBUG) {
								System.out.println(
										KeyStroke.vkCodetoString(kt1.getKeyCode()) + "," + KeyStroke.vkCodetoString(kt2.getKeyCode()) + ":" +
												prob);
							}
							double intervalPred = bigramInterval - prob;
							//              long bigramPredictability = (long) (bigramInterval / (-1.0 * bigramProbability));

							// featureMap.get(digraph).add(intervalPred);
							featureMap.get(digraph).add(intervalPred / longestDigraph);
						}

					}
				}
				/////////////////////////////////////////////////
			}
		}

		//create output feature list.
		LinkedList<Feature> output = new LinkedList<Feature>();

		//iterate over the featureMap
	    for (Integer vkCode0 : keySet)
	      for (Integer vkCode1 : keySet) {
	        output.add(
	            new Feature("DN_Exp_Predict_" + KeyStroke.vkCodetoString(vkCode0) + "_" + KeyStroke.vkCodetoString(vkCode1),
	                featureMap.get(vkCode0 * 1000 + vkCode1)));
	      }
		return output;
	}

	@Override
	public String getName() {
		// return "Digraph_Expanded Duration";
		return "Digraph_Longest_Duration Duration";
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
