package features.nyit;

import java.util.Collection;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.TreeSet;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import keystroke.KeyStroke;

/**
 * Reports which keys have been pressed and how many times they have been pressed.
 * <p><p>
 * Useful in determining what OS environment and keyboard the user is typing in.
 * 
 * @author Patrick Koch
 */
public class KeyStrokeSurvey implements ExtractionModule {

	Hashtable<Integer,Integer> vkFrequencies = new Hashtable<Integer,Integer>();
	
	@Override
	public Collection<Feature> extract(DataNode data) {
		for (Answer a : data) {
			Collection<KeyStroke> keys = a.getKeyStrokeList();
			for (KeyStroke k : keys) {
				int keyCode = k.getKeyCode();
				if (k.isKeyPress()) {
					if (vkFrequencies.containsKey(keyCode))
						vkFrequencies.put(keyCode, vkFrequencies.get(keyCode) + 1 );
					else
						vkFrequencies.put(keyCode, 1);
				}
			}
		}
		
		TreeSet<Integer> keySet = new TreeSet<Integer>(vkFrequencies.keySet());
		LinkedList<Feature> output = new LinkedList<Feature>();
		for (Integer i : keySet) {
			Collection<Integer> value = new LinkedList<Integer>();
			value.add(vkFrequencies.get(i));
			output.add(new Feature("C_" + KeyStroke.vkCodetoString(i), value));
		}
		
		return output;
	}

	@Override
	public String getName() {
		return "Key Stroke Survey";
	}


}
