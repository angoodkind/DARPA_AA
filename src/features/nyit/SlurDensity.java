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

public class SlurDensity implements ExtractionModule {
	
	
	private HashMap<String, Integer> featureMap = new HashMap<String, Integer>(1800);
	private static TreeSet<Integer> keySet = new TreeSet<Integer>(KeyStroke.UsefulVKCodes());
	
	private void CreateCombinations() {
		for (Integer key0 : keySet)
			for (Integer key1 : keySet) {
				featureMap.put(KeyStroke.vkCodetoString(key0)+"_"+KeyStroke.vkCodetoString(key1), 0);
			}
	}

	@Override
	public Collection<Feature> extract(DataNode data) {
		CreateCombinations();
		
		LinkedList<KeyStroke> buffer = new LinkedList<KeyStroke>();
		for (Answer a : data) {
			buffer.clear();
			KeyStroke previousKey = null;
			for (KeyStroke currentKey : a.getKeyStrokeList()) {
				if (previousKey != null) {
					if (currentKey.isVisible() && previousKey.isVisible() && currentKey.isKeyPress() && previousKey.isKeyPress() ) {
						String key_combo = KeyStroke.vkCodetoString(previousKey.getKeyCode())
											+ "_"
											+ KeyStroke.vkCodetoString(currentKey.getKeyCode());
						if (featureMap.containsKey(key_combo)) 	featureMap.put(key_combo, featureMap.get(key_combo) + 1);
					}
				}
				previousKey = currentKey;
			}
		}
		LinkedList<Feature> output = new LinkedList<Feature>();
		for (String key : featureMap.keySet()) {
			output.add(new Feature(key, new LinkedList<Integer>().add(featureMap.get(key))));
		}
		
		return output;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
