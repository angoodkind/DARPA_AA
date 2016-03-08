/**
 * 
 */
package features.demographics;

import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;

import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import output.util.AncillaryDataInterface;

public class Handedness implements ExtractionModule {

	AncillaryDataInterface adi;
	LinkedList<Feature> output;
	
	public Handedness() {
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
		Object subj_hand = null;
		
		try {
			
			subj_hand = adi.getData(data.getUserID(), AncillaryDataInterface.UD_DOMINANT_HAND);
//			System.out.println(data.getUserID()+" "+subj_hand);
			
		} catch (SQLException e) { e.printStackTrace();}
		
		output.add(new Feature("Handedness",subj_hand));
		
		return output;
	}

	@Override
	public String getName() {
		return "Handedness";
	}

}
