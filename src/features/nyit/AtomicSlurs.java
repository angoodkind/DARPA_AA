package features.nyit;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import keystroke.KeyStroke;

public class AtomicSlurs implements ExtractionModule {

		private HashMap<Integer, LinkedList<Long>> featureMap = new HashMap<Integer, LinkedList<Long>>(1800);
		private int[] alphaNumeric = {48, 49, 50, 51, 52, 53, 54, 55, 56, 57,
									  65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 
									  78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90};
		private int[] punctuation = {44, 45, 46, 59, 222, 47, 92, 91, 93, 192};
		
		private void createSearchSpace() {
			featureMap.clear();
			for (Integer i : alphaNumeric)
				for (Integer j : alphaNumeric)
					featureMap.put(i * 1000 + j, new LinkedList<Long>());
			for (Integer i : alphaNumeric)
				for (Integer j : punctuation)
					featureMap.put(i * 1000 + j, new LinkedList<Long>());
			for (Integer i : alphaNumeric)
				featureMap.put(i * 1000 + 32, new LinkedList<Long>());
		}
		
		@Override
		public Collection<Feature> extract(DataNode data) {
			createSearchSpace();
			LinkedList<KeyStroke> buffer = new LinkedList<KeyStroke>();
			for (Answer a : data) {
				buffer.clear();
				for (KeyStroke k : a.getKeyStrokeList()) {
				/////////////////Main Logic///////////////////////
					buffer.add(k);
					if (buffer.size() > 3) {
						if (isSlur(buffer))
							featureMap.get(buffer.get(0).getKeyCode() * 1000 + buffer.get(1).getKeyCode()).add(buffer.get(2).getWhen() - buffer.get(1).getWhen());
						buffer.poll();
					}
				//////////////////////////////////////////////////
				}
			}
			LinkedList<Feature> output = new LinkedList<Feature>();
			for (Integer i : alphaNumeric)
				for (Integer j : alphaNumeric)
				output.add(new Feature("S_" + KeyStroke.vkCodetoString(i) + "_" + KeyStroke.vkCodetoString(j), featureMap.get(i*1000 + j)));
			for (Integer i : alphaNumeric)
				for (Integer j : punctuation)
					output.add(new Feature("S_" + KeyStroke.vkCodetoString(i) + "_" + KeyStroke.vkCodetoString(j), featureMap.get(i*1000 + j)));
			for (Integer i : alphaNumeric)
				output.add(new Feature("S_" + KeyStroke.vkCodetoString(i) + "_" + KeyStroke.vkCodetoString(32), featureMap.get(i*1000 + 32)));
			
			return output;
		}
		
		private boolean isSlur(LinkedList<KeyStroke> buffer) {
			// if the featureMap contains the hash value of the first two keys
			if (featureMap.keySet().contains(buffer.get(0).getKeyCode() * 1000 + buffer.get(1).getKeyCode())) {
				// if the keys follow the pattern press, press, release, release
				if (buffer.get(0).isKeyPress() && buffer.get(1).isKeyPress()
						&& buffer.get(2).isKeyRelease() && buffer.get(3).isKeyRelease()) {
					// if the keys follow pattern A, B, A, B
					if (buffer.get(0).getKeyCode() == buffer.get(2).getKeyCode() 
							&& buffer.get(1).getKeyCode() == buffer.get(3).getKeyCode()) {
						return true;
					}
				}
			}
			return false;
		}
		
		
		@Override
		public String getName() {
			return "Atomic Slurs";
		}
		
}
