/**
 * 
 */
package features.demographics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import extractors.data.TestVectorShutdownModule;
import keytouch.KeyTouchContextNgram;
import output.util.AncillaryDataInterface;

public class Gender implements ExtractionModule, TestVectorShutdownModule {

	private static HashMap<Integer,String> demogMap = new HashMap<Integer,String>();
	private static final boolean TV_SHUTDOWN = false;
	private LinkedHashMap<Integer,LinkedList<LinkedHashMap<String,String>>> vectorMap;
	private File vectorFile = new File(KeyTouchContextNgram.vectorMapFileName);

	AncillaryDataInterface adi;
	LinkedList<Feature> output;

	public Gender() {
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
		Object subj_gender = null;

		try {

			subj_gender = adi.getData(data.getUserID(), AncillaryDataInterface.UD_GENDER);
//						System.out.println(subj_gender);
		} catch (SQLException e) { e.printStackTrace();
		subj_gender = null;
		}

		//		for (Answer a : data)
		//			if (subj_gender.equals("m") || subj_gender.equals("f"))
		//				System.out.println(a.getAnswerID()+"|"+subj_gender);

		if (subj_gender.equals("m")) {
			output.add(new Feature("Gender", "MALE"));
			demogMap.put(data.getUserID(), "MALE");
		}
		else if (subj_gender.equals("f")) {
			output.add(new Feature("Gender", "FEMALE"));
			demogMap.put(data.getUserID(), "FEMALE");
		}
		return output;
	}

	@Override
	public String getName() {
		return "Gender";
	}

	@Override
	public void shutdown() {
		//only appends to an existing file, rather than creating a new one
		if (TV_SHUTDOWN && vectorFile.exists()) {
			// read in file
			ObjectInputStream in;
			try {
				in = new ObjectInputStream(new FileInputStream(new File(KeyTouchContextNgram.vectorMapFileName)));
				this.vectorMap = (LinkedHashMap<Integer, LinkedList<LinkedHashMap<String, String>>>) in.readObject();
			// append features.demographics to each slice (answer) of each user
			for (Integer user : vectorMap.keySet()) {
				for (LinkedHashMap<String, String> map : vectorMap.get(user)) {
					map.put("Gender", demogMap.get(user));
				}
			}
			//override the file and write the new, appended map
			File f = new File(KeyTouchContextNgram.vectorMapFileName);
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f,false));
			out.writeObject(vectorMap);
			out.close();
			
			} catch (IOException | ClassNotFoundException e) {e.printStackTrace();}
		}
		else {
			System.out.println("Vector File does not exist");
		}
	}
}
