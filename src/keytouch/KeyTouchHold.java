package keytouch;

import keystroke.KeyStroke;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;

public class KeyTouchHold implements ExtractionModule {
	public static final boolean DEBUG = false;

	private static TreeSet<Integer> keySet = new TreeSet<Integer>(KeyStroke.TopVKCodes());
	private HashMap<String, LinkedList<Double>> featureMap = new HashMap<String, LinkedList<Double>>(10000);
	private static final String[] metric = {"pause_","hold_"};
	static final boolean WRITE_FILE = false;
	static final String csv_file = "unigraph-instance.csv";

	/**
	 * Initialize the module.
	 */
	public KeyTouchHold() {
		featureMap.clear();
	}

	/**
	 * Generates full permutation of two UpperCase alpha combinations and places
	 * them into a TreeSet which ensures no duplicates and automatically sorts them!
	 */
	private void generateSearchSpace() {
		featureMap.clear();
		
		for (Integer key0 : keySet)
			for (String m : metric)
				featureMap.put(m+key0, new LinkedList<Double>());
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
				for (int i = 1; i < ktList.size(); i++) {
					KeyTouch k = ktList.get(i);
					int kCode = k.getKeyCode();
					long kHold = k.getHoldTime();
					long kPause = k.getPrecedingPause();
					
					if (WRITE_FILE)
						outf.write(KeyStroke.vkCodetoString(kCode)+"\n");
					
					if (featureMap.containsKey("pause_"+kCode)) {
						featureMap.get("pause_"+kCode).add((double)kPause);
					}
					
					if (featureMap.containsKey("hold_"+kCode)) {
						featureMap.get("hold_"+kCode).add((double)kHold);
					}
				}
			}

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
			for (String m : metric)
				output.add(new Feature("KT_"+m+KeyStroke.vkCodetoString(vkCode0),featureMap.get(m+vkCode0)));
		}

		return output;
	}


	@Override
	public String getName() {
		return "KeyTouch Hold";
	}

}
