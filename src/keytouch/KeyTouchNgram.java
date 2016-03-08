package keytouch;

import keystroke.KeyStroke;

import java.util.*;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
/**
 * Highly modular code, to allow for selection of ngram length,
 * as well as whether to include interval pauses, keyholds, or both
 * 
 * @author Adam Goodkind
 */
public class KeyTouchNgram implements ExtractionModule {
	private static final boolean INCLUDE_HOLD = true;
	private static final boolean INCLUDE_INTERVAL = false	;
	private static final int GRAM_LENGTH = 2; //2-4 (use unigram class for 1)
	
	private static TreeSet<Integer> keySet = new TreeSet<Integer>(KeyStroke.TopVKCodes());
	private HashMap<String, LinkedList<Double>> featureMap = new HashMap<String, LinkedList<Double>>(100000);

	/**
	 * Initialize the module.
	 */
	public KeyTouchNgram() {
		featureMap.clear();
	}



	@Override
	public Collection<Feature> extract(DataNode data) {

		//clear feature map and repopulate it's keys for a new extraction
		generateSearchSpace();

		for (Answer a : data) {

			LinkedList<KeyTouch> ktList = KeyTouch.parseSessionToKeyTouches(a.getKeyStrokes());
			for (int i = 0; i < ktList.size()-GRAM_LENGTH+1; i++) {
				LinkedList<KeyTouch> ngram = new LinkedList<KeyTouch>();
				for (int n = 0; n < GRAM_LENGTH; n++) {
					ngram.add(ktList.get(i+n));
				}
				
				double ngramDuration = 0.0;
				String ngramStr = "";
				for (int k = 0; k < ngram.size(); k++) {
					if (INCLUDE_HOLD) {
						ngramDuration += (double)ngram.get(k).getHoldTime();
					}
					if (k != 0) {
						if (INCLUDE_INTERVAL) {
							//do not include preceding pause of first gram
							ngramDuration += (double)ngram.get(k).getPrecedingPause();
						}
						ngramStr += "_";
					}
					ngramStr += KeyStroke.vkCodetoString(ngram.get(k).getKeyCode());
				}
				
				if (featureMap.containsKey(ngramStr)) {
					featureMap.get(ngramStr).add(ngramDuration);
				}
			}
		}
		//create output feature list.
		LinkedList<Feature> output = new LinkedList<Feature>();
		for (String ngram : featureMap.keySet()) {
			output.add(new Feature ("KT_DUR_"+ngram,featureMap.get(ngram)));
		}

		return output;
	}


	@Override
	public String getName() {
		return "KeyTouch Ngram";
	}

	/**
	 * Generates full permutation of two UpperCase alpha combinations and places
	 * them into a TreeSet which ensures no duplicates and automatically sorts them!
	 */
	private void generateSearchSpace() {
		featureMap.clear();
		switch (GRAM_LENGTH) {
			case 1:
				for (Integer key0 : keySet)
					featureMap.put(KeyStroke.vkCodetoString(key0), new LinkedList<Double>());
				break;
			case 2:
				for (Integer key0 : keySet)
					for (Integer key1 : keySet) 
						featureMap.put(KeyStroke.vkCodetoString(key0) + "_"
								+ KeyStroke.vkCodetoString(key1), new LinkedList<Double>());
				break;
			case 3:
				for (Integer key0 : keySet)
					for (Integer key1 : keySet)
						for (Integer key2 : keySet) 
							featureMap.put(KeyStroke.vkCodetoString(key0) + "_"
									+ KeyStroke.vkCodetoString(key1) + "_"
									+ KeyStroke.vkCodetoString(key2), new LinkedList<Double>());
				break;
			case 4:
				for (Integer key0 : keySet)
					for (Integer key1 : keySet)
						for (Integer key2 : keySet) 
							for (Integer key3 : keySet) 
								featureMap.put(KeyStroke.vkCodetoString(key0) + "_"
										+ KeyStroke.vkCodetoString(key1) + "_"
										+ KeyStroke.vkCodetoString(key2) + "_"
										+ KeyStroke.vkCodetoString(key3), new LinkedList<Double>());
				break;
		}
	}
}
