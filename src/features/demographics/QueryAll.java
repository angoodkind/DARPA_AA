package features.demographics;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import output.util.AncillaryDataInterface;

public class QueryAll implements ExtractionModule {
	
	static final String csv_file = "user-features.demographics.csv";
	static final String DELIMITER = ";";
	AncillaryDataInterface adi;
	
	public QueryAll() {
		try {
			adi = new AncillaryDataInterface();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	@Override
	public Collection<Feature> extract(DataNode data) {
		
		BufferedWriter outf = null;
		Object subj_age = null;
		Object subj_ethnicity = null;
		Object subj_first_lang = null;
		Object subj_gender = null;
		Object subj_hand = null;
		Object subj_major = null;
		Object subj_mobile = null; 
		Object subj_prim_lang = null;
		Object subj_training = null;
		Object subj_typing = null;
		
		try {
			outf = new BufferedWriter(new FileWriter(csv_file, true));
			subj_age = adi.getData(data.getUserID(), AncillaryDataInterface.UD_AGE);
			subj_ethnicity = adi.getData(data.getUserID(), AncillaryDataInterface.UD_ETHINICITY);
			subj_first_lang = adi.getData(data.getUserID(), AncillaryDataInterface.UD_FIRST_LANGUAGE);
			subj_gender = adi.getData(data.getUserID(), AncillaryDataInterface.UD_GENDER);
			subj_hand = adi.getData(data.getUserID(), AncillaryDataInterface.UD_DOMINANT_HAND);
			subj_major = adi.getData(data.getUserID(), AncillaryDataInterface.UD_COLLEGE_MAJOR);
			subj_mobile = adi.getData(data.getUserID(), AncillaryDataInterface.UD_MOBILE_KEYBOARD_USE);
			subj_prim_lang = adi.getData(data.getUserID(), AncillaryDataInterface.UD_PRIMARY_LANGUAGE);
			subj_training = adi.getData(data.getUserID(), AncillaryDataInterface.UD_FORMAL_TRAINING);
			subj_typing = adi.getData(data.getUserID(), AncillaryDataInterface.UD_HOURS_TYPING_PER_DAY);
			
			if (data.getUserID() != 0)
				outf.write(Integer.toString(data.getUserID()));
			outf.write(DELIMITER);
			if (subj_age != null)
				outf.write(subj_age.toString());
			outf.write(DELIMITER);
			if (subj_ethnicity != null)
				outf.write(subj_ethnicity.toString());
			outf.write(DELIMITER);
			if (subj_first_lang != null)
				outf.write(subj_first_lang.toString());
			outf.write(DELIMITER);
			if (subj_gender != null)
				outf.write(subj_gender.toString());
			outf.write(DELIMITER);
			if (subj_hand != null)
				outf.write(subj_hand.toString());
			outf.write(DELIMITER);
			if (subj_major != null)
				outf.write(subj_major.toString());
			outf.write(DELIMITER);
			if (subj_mobile != null)
				outf.write(subj_mobile.toString());
			outf.write(DELIMITER);
			if (subj_prim_lang != null)
				outf.write(subj_prim_lang.toString());
			outf.write(DELIMITER);
			if (subj_training != null)
				outf.write(subj_training.toString());
			outf.write(DELIMITER);
			if (subj_typing != null)
				outf.write("'"+subj_typing.toString());
			outf.write('\n');
			
		
		} 
		catch (IOException | SQLException e) {
			subj_age =null;
			subj_ethnicity = null;
			subj_first_lang = null;
			subj_gender = null;
			subj_hand = null;
			subj_major = null;
			subj_mobile = null;
			subj_prim_lang = null;
			subj_training = null;
			subj_typing = null;
			e.printStackTrace();
		} 
		finally {
			if (outf != null) {
				try {
					outf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
