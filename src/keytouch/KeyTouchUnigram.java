package keytouch;

import keystroke.KeyStroke;

import java.util.*;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;

public class KeyTouchUnigram implements ExtractionModule {
	public static final boolean INCLUDE_HOLD = true;
	public static final boolean INCLUDE_PAUSE = false;
	private static TreeSet<Integer> keySet = new TreeSet<Integer>(KeyStroke.TopVKCodes());
	private HashMap<Integer, LinkedList<Double>> featureMap = new HashMap<Integer, LinkedList<Double>>(10000);

	/**
	 * Initialize the module.
	 */
	public KeyTouchUnigram() {
		featureMap.clear();
	}

	/**
	 * Generates full permutation of two UpperCase alpha combinations and places
	 * them into a TreeSet which ensures no duplicates and automatically sorts them!
	 */
	private void generateSearchSpace() {
		featureMap.clear();
		for (Integer key0 : keySet)
			featureMap.put(key0, new LinkedList<Double>());
	}

	@Override
	public Collection<Feature> extract(DataNode data) {

		//clear feature map and repopulate it's keys for a new extraction
		generateSearchSpace();

		for (Answer a : data) {

			LinkedList<KeyTouch> ktList = KeyTouch.parseSessionToKeyTouches(a.getKeyStrokes());
			for (int i = 1; i < ktList.size(); i++) {
				KeyTouch k = ktList.get(i);
				int kCode = k.getKeyCode();
				long kPause = k.getPrecedingPause();
				double kHold = k.getHoldTime();
				
				double uniDuration = 0.0;
				if (INCLUDE_PAUSE)
					uniDuration += (double)kPause;
				if (INCLUDE_HOLD)
					uniDuration += (double)kHold;
					
				if (featureMap.containsKey(kCode)) {
					featureMap.get(kCode).add(uniDuration);
				}
			}
		}
		//create output feature list.
		LinkedList<Feature> output = new LinkedList<Feature>();

		//iterate over the featureMap
		for (Integer vkCode0 : keySet) {
			output.add(new Feature("UNI_H_" + KeyStroke.vkCodetoString(vkCode0),featureMap.get(vkCode0)));
		}

		return output;
	}


	@Override
	public String getName() {
		return "KeyTouch Unigram";
	}

}
