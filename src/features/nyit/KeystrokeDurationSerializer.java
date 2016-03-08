package features.nyit;

import java.util.Collection;
import java.util.LinkedList;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import keystroke.KeyStroke;


/**
 * Serializes Keystroke durations into the format...
 * 
 * A 30 B 40 C 50 D
 * 
 * ...where the the key names and durations between them are whitespace separated. 
 * 
 * @author Patrick Koch
 */
public class KeystrokeDurationSerializer implements ExtractionModule {

	@Override
	public Collection<Feature> extract(DataNode data) {
		StringBuilder outputString = new StringBuilder();
		StringBuilder featureName = new StringBuilder();
		KeyStroke last;
		LinkedList<Feature> output = new LinkedList<Feature>();
		for (Answer a : data) {
			outputString.setLength(0);
			featureName.setLength(0);
			last = null;
			for (KeyStroke k : a.getKeyStrokeList()) {
				if (k.isKeyPress()) {
					if (last != null)
						outputString.append(k.getWhen() - last.getWhen() + " ");
					outputString.append(KeyStroke.vkCodetoString(k.getKeyCode()) + " ");
					last = k;
				}
			}
			outputString.setLength(outputString.length()-1);
			featureName.append(a.getQuestionID());
			featureName.append('_');
			featureName.append(a.getOrderID());
			featureName.append('_');
			featureName.append(a.getCogLoad());
			featureName.append('_');
			featureName.append(a.getAnswerID());
			output.add(new Feature(featureName.toString(),outputString.toString()));
		}
		
		return output;
	}

	@Override
	public String getName() {
		return "Keystroke Duration Serializer";
	}

}
