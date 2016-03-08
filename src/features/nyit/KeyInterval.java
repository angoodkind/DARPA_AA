package features.nyit;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeSet;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import keystroke.KeyStroke;

public class KeyInterval implements ExtractionModule {
	
	private static TreeSet<Integer> keySet = new TreeSet<Integer>(KeyStroke.UsefulVKCodes());
	private HashMap<Integer, LinkedList<Long>> featureMap = new HashMap<Integer, LinkedList<Long>>(1800);

	public Collection<Feature> extract(DataNode data) {
		createSearchSpace();
		
		LinkedList<KeyStroke> buffer = new LinkedList<KeyStroke>();
		for (Answer a : data) {
			buffer.clear();
			for (KeyStroke k : a.getKeyStrokeList()) {
				buffer.add(k);
				if (buffer.size() > 3) {
					if (isInterval(buffer) && buffer.get(2).getWhen() - buffer.get(1).getWhen() > -1)
						featureMap.get(buffer.get(1).getKeyCode() * 1000 + buffer.get(2).getKeyCode()).add(buffer.get(2).getWhen() - buffer.get(1).getWhen());
					buffer.poll();
				}
			}
		}
		LinkedList<Feature> output = new LinkedList<Feature>(); 
		for (Integer vkCode0 : keySet)
			for (Integer vkCode1: keySet) {
				output.add(new Feature( "I_" + KeyStroke.vkCodetoString(vkCode0) + "_" + KeyStroke.vkCodetoString(vkCode1),  featureMap.get(vkCode0 * 1000 + vkCode1)));
			}
		return output;
	}

	private void createSearchSpace() {
		featureMap.clear();
		for (Integer key0 : keySet)
			for (Integer key1 : keySet) {
				featureMap.put(key0 * 1000 + key1, new LinkedList<Long>());
			}
	}

	
	/**
	 * Determines if the buffer contains a key Interval.
	 * 
	 * @return Hashtable to be used during extraction.
	 */
	private boolean isInterval(LinkedList<KeyStroke> buffer) {
		// if the featureMap contains the hash value of the first two keys
		if (featureMap.keySet().contains(buffer.get(1).getKeyCode() * 1000 + buffer.get(2).getKeyCode())) {
			// if the keys follow the pattern press, press, release, release
			if (buffer.get(0).isKeyPress() && buffer.get(1).isKeyRelease()
					&& buffer.get(2).isKeyPress() && buffer.get(3).isKeyRelease()) {
				// if the keys follow pattern A, A, B, B
				if (buffer.get(0).getKeyCode() == buffer.get(1).getKeyCode() 
						&& buffer.get(2).getKeyCode() == buffer.get(3).getKeyCode()) { 
					return true;
				}
			}
		}
		return false;
	}
	
	public String getName() {
		return "Key Interval";
	}

}
