/**
 * 
 */
package features.demographics;

import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import output.util.AncillaryDataInterface;

public class PrimaryLanguage implements ExtractionModule {

	AncillaryDataInterface adi;
	LinkedList<Feature> output;
	
	public PrimaryLanguage() {
		output = new LinkedList<Feature>();		
		try {
			//Always instantiate in default module constructor.
			adi = new AncillaryDataInterface();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	@Override
	public Collection<Feature> extract(DataNode data) {
		
		output.clear();
		Object subj_lang = null;
		
		try {
			subj_lang = adi.getData(data.getUserID(), AncillaryDataInterface.UD_FIRST_LANGUAGE);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
//		for (Answer a : data) {
//			if (subj_lang.equals("English"))
//				System.out.println(a.getAnswerID()+"|L1");
//			else
//				System.out.println(a.getAnswerID()+"|L2");
//		}
		
		if (subj_lang.equals("English"))
			output.add(new Feature("Native_Language", "English"));
		else
			output.add(new Feature("Native_Language", "Non-English"));
		
		return output;
	}

	@Override
	public String getName() {
		return "Primary_Language";
	}

}
