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
public class DigraphDurationPred extends Predictability implements ExtractionModule {

	private static final String modelName = "SESSION1";
	private static final String gramType = "keystroke";

	public static final boolean DEBUG = false;

	private Set<String> searchSpace = new TreeSet<String>();
	private HashMap<String, LinkedList<Long>> featureMap = new HashMap<String, LinkedList<Long>>(1000);

	/**
	 * Initialize the module.
	 */
	public DigraphDurationPred() {
		super(modelName, gramType);
		featureMap.clear();
		searchSpace.clear();
		generateSearchSpace();
	}

	/**
	 * Generates full permutation of two UpperCase alpha combinations and places
	 * them into a TreeSet which ensures no duplicates and automatically sorts them!
	 */
	private void generateSearchSpace() {
		for (char a = 65; a < 91; a++)
			for (char b = 65; b < 91; b++)
				searchSpace.add("" + a + b);
	}


	@Override
	public Collection<Feature> extract(DataNode data) {
		//clear feature map and repopulate it's keys for a new extraction
		featureMap.clear();
		for (String s : searchSpace)
			featureMap.put(s, new LinkedList<Long>());

		//create a buffer to hold the keystrokes during processing
		LinkedList<KeyStroke> buffer = new LinkedList<KeyStroke>();
		//String digraph used to hold d
		String digraph;
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
						digraph = (KeyStroke.keyStrokesToString(buffer) //convert it to a textual representation
								.toUpperCase());            //and make it uppercase
						if (searchSpace.contains(digraph)) {
							//if the digraph is in the searchSpace Set
							KeyStroke kt1 = buffer.getFirst();
							KeyStroke kt2 = buffer.getLast();
							//              featureMap.get(digraph)          //get the digraph's entry in the Map
							//                  .add(kt2.getWhen() //add the latency between the keystrokes...
							//                      - kt1.getWhen()); //to the list contained in the Map.

							double prob = keyStrokeBigramModel.getBigramProbability(kt1,kt2);
							double bigramInterval = kt2.getWhen() - kt1.getWhen();
							if (DEBUG) {
								System.out.println(
										KeyStroke.vkCodetoString(kt1.getKeyCode()) + "," + KeyStroke.vkCodetoString(kt2.getKeyCode()) + ":" +
												prob);
							}
							long intervalPred = (long) (bigramInterval - prob);
							//              long bigramPredictability = (long) (bigramInterval / (-1.0 * bigramProbability));

							featureMap.get(digraph).add(intervalPred);
						}

					}
				}
				/////////////////////////////////////////////////
			}
		}

		//create output feature list.
		LinkedList<Feature> output = new LinkedList<Feature>();

		//iterate over the featureMap using searchSpace because it is auto-sorted by TreeSet class.
		for (String s : searchSpace)
			output.add(new Feature("DN_Predict" + s, featureMap.get(s)));
		return output;
	}

	@Override
	public String getName() {
		return "Digraph Duration";
	}

}
