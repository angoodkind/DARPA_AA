package features.nyit;

import java.util.Collection;
import java.util.LinkedList;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import keystroke.KeyStroke;

public class KeyToTextTest implements ExtractionModule {

	@Override
	public Collection<Feature> extract(DataNode data) {
		boolean hahaha;
		String lol = new String();
		for (Answer a : data) {
			lol = KeyStroke.keyStrokesToFinalText(a.getKeyStrokeList());
			hahaha = a.getFinalText().equals(lol);
			System.out.println(hahaha);
			if (hahaha)
				continue;
			System.out.println(lol);
			System.out.println(a.getFinalText());
		}
		
		LinkedList<Feature> output = new LinkedList<Feature>();
		output.add(new Feature("text", lol.toString()));
		return output;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
