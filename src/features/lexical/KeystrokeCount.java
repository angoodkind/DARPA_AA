package features.lexical;

import java.util.Collection;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;

public class KeystrokeCount implements ExtractionModule {

	@Override
	public Collection<Feature> extract(DataNode data) {
		for (Answer a : data) {
			System.out.print(data.getUserID());
			System.out.print("|");
			System.out.print(a.getAnswerID());
			System.out.print("|");
			System.out.println(a.getKeyStrokeList().size());
		}
		return null;
	}

	@Override
	public String getName() {
		return "KeyStroke Count";
	}

}
