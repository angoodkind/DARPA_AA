package features.predictability;

import java.util.Collection;

import events.EventList;
import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import keystroke.KeyStroke;
import ngrammodel.Bigram;

public class Test_Predictability extends Predictability implements ExtractionModule {
	private static final String modelName = "TRAINING";
	private static final String gramType = "keystroke";

	public Test_Predictability() {
		super(modelName,gramType);
	}
	@Override
	public Collection<Feature> extract(DataNode data) {
		
		for (Answer a : data) {
			EventList<KeyStroke> finals = KeyStroke.keyStrokesToFinalTextEventList(a.getKeyStrokeList());
			for (int i = 0; i < finals.size()-1; i++) {
				String ks1 = KeyStroke.vkCodetoString(finals.get(i).getKeyCode());
				String ks2 = KeyStroke.vkCodetoString(finals.get(i+1).getKeyCode());
				Bigram bigram = new Bigram(ks1,ks2);
				double predictability = keyStrokeBigramModel.getBigramProbability(bigram);
				System.out.println(bigram.toCharString()+"\t"+predictability);
			}
			System.out.println();
		}
		
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	} 

}
