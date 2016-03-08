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
 * <p><p>
 * Looks at an ngram within an ngram, e.g. T | T E R
 * 
 * @author Adam Goodkind
 */
public class KeyTouchSubNgram implements ExtractionModule {
	private static final boolean INCLUDE_HOLD = true;
	private static final boolean INCLUDE_INTERVAL = false	;
	private static final int NGRAM_LENGTH = 2; //2-4 
	private static final int SUBGRAM_LENGTH = 1; //1-3
	
	private static TreeSet<Integer> keySet = new TreeSet<Integer>(KeyStroke.TopVKCodes());
	private HashMap<String, LinkedList<Double>> featureMap = new HashMap<String, LinkedList<Double>>(100000);

	/**
	 * Initialize the module.
	 */
	public KeyTouchSubNgram() {
		featureMap.clear();
	}



	@Override
	public Collection<Feature> extract(DataNode data) {

		//clear feature map and repopulate it's keys for a new extraction
		generateSearchSpace();
		
		for (Answer a : data) {

			LinkedList<KeyTouch> ktList = KeyTouch.parseSessionToKeyTouches(a.getKeyStrokes());
			for (int i = 0; i < ktList.size()-NGRAM_LENGTH+1; i++) {
				//construct ngram
				LinkedList<KeyTouch> ngram = new LinkedList<KeyTouch>();
				String ngramStr = "";
				for (int n = 0; n < NGRAM_LENGTH; n++) {
					if (n != 0) {
						ngramStr += "_";
					}
					ngramStr += KeyStroke.vkCodetoString(ktList.get(i+n).getKeyCode());
					ngram.add(ktList.get(i+n));	
				}
				
				//for each subgram, create an instance of the feature
				for (int k = 0; k < ngram.size(); k++) {
					KeyTouch subgram = ngram.get(k);
					String subgramStr = KeyStroke.vkCodetoString(subgram.getKeyCode());
					double subgramDur = 0.0;
					if (INCLUDE_HOLD)
						subgramDur += (double)subgram.getHoldTime();
					if (INCLUDE_INTERVAL)
						subgramDur += (double)subgram.getPrecedingPause();
					String feature = subgramStr + "--" + ngramStr;
					
					if (featureMap.containsKey(feature)) {
						featureMap.get(feature).add(subgramDur);
					}
				}
			}
		}
		//create output feature list.
		LinkedList<Feature> output = new LinkedList<Feature>();
		for (String ngram : featureMap.keySet()) {
			output.add(new Feature ("KT_SUBN_"+ngram,featureMap.get(ngram)));
		}

		return output;
	}


	@Override
	public String getName() {
		return "KeyTouch SubNgram";
	}

	/**
	 * Generates full permutation of two UpperCase alpha combinations and places
	 * them into a TreeSet which ensures no duplicates and automatically sorts them!
	 */
	private void generateSearchSpace() {
		featureMap.clear();
		switch (NGRAM_LENGTH) {
			case 2:
				for (Integer key0 : keySet)
					for (Integer key1 : keySet)
						for (Integer key2 : keySet) 
							featureMap.put(KeyStroke.vkCodetoString(key2) + "--"
									+ KeyStroke.vkCodetoString(key0) + "_"
									+ KeyStroke.vkCodetoString(key1), new LinkedList<Double>());
				break;
			case 3:
				for (Integer key0 : keySet)
					for (Integer key1 : keySet)
						for (Integer key2 : keySet) 
							for (Integer key3 : keySet) 
								featureMap.put(KeyStroke.vkCodetoString(key3) + "--"
										+ KeyStroke.vkCodetoString(key0) + "_"
										+ KeyStroke.vkCodetoString(key1) + "_"
										+ KeyStroke.vkCodetoString(key2), new LinkedList<Double>());
				break;
			case 4:
				for (Integer key0 : keySet)
					for (Integer key1 : keySet)
						for (Integer key2 : keySet) 
							for (Integer key3 : keySet)
								for (Integer key4 : keySet)
									featureMap.put(KeyStroke.vkCodetoString(key4) + "--"
											+ KeyStroke.vkCodetoString(key0) + "_"
											+ KeyStroke.vkCodetoString(key1) + "_"
											+ KeyStroke.vkCodetoString(key2) + "_"
											+ KeyStroke.vkCodetoString(key3), new LinkedList<Double>());
				break;
		}
	}
}
