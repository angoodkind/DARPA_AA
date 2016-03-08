package features.nyit;

import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;

import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import output.util.AncillaryDataInterface;

public class AncillaryDataTest implements ExtractionModule {

	AncillaryDataInterface adi;
	LinkedList<Feature> output; 
	
	public AncillaryDataTest() {
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
		
		Object o = null;
		try {
			o = adi.getData(data.getUserID(), AncillaryDataInterface.UD_GENDER);
		} catch (SQLException e) {
			e.printStackTrace();
			o = null;
		}
		
		output.add(new Feature("Subject_Age", o));
		return output;
	}

	@Override
	public String getName() {
		return "Ancillary Data Test Module";
	}

}
