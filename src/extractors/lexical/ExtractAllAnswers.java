/**
 * 
 */
package extractors.lexical;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import opennlp.tools.util.InvalidFormatException;
import keystroke.*;

/**
 * @author agoodkind
 * Goes through entire database, and extracts all answers.
 * 	Relevant query:
 * 	
 */
public class ExtractAllAnswers extends Tokenize implements ExtractionModule {

	// to hold term counts for each user, and for overall document frequency
	public ArrayList<HashMap<String,Integer>> termCounts = new ArrayList<HashMap<String,Integer>>();
	
	/* 
	 * @see extractors.nyit.ExtractionModule#extract(extractors.nyit.DataNode)
	 */
	@Override
	public Collection<Feature> extract(DataNode data) {
		
		for (Answer a : data) {
			HashMap<String, Integer> userData = new HashMap<String,Integer>();
			try {
				String finalText = KeyStroke.keyStrokesToFinalText(a.getKeyStrokeList());
				userData = createUserMap(finalText);
			
			} catch (IOException e) {e.printStackTrace();}
			
			termCounts.add(userData);
		}
		
		// add document frequency hashmap
		HashMap<String, Integer> docData = new HashMap<String,Integer>();
		System.out.println("Creating doc data");
		docData = createDocMap(termCounts);
		
		//insert doc data at beginning of termCounts
		termCounts.add(0,docData);
		
		//write termCounts to file
		try {
		
			FileOutputStream fileOut = new FileOutputStream("TFIDF_DataFile.data");
			ObjectOutputStream oos = new ObjectOutputStream(fileOut);
			oos.writeObject(termCounts);
			oos.close();
			fileOut.close();
			
		} catch (Exception e) {e.printStackTrace();}
		
		
		//for testing purposes, read file
//		try {
//			ArrayList<HashMap<String,Integer>> testTermCounts = new ArrayList<HashMap<String,Integer>>();
//			
//			FileInputStream fileIn = new FileInputStream("TFIDF_DataFile.data");
//			ObjectInputStream ois = new ObjectInputStream(fileIn);
//			testTermCounts = (ArrayList<HashMap<String,Integer>>) ois.readObject();
//		
//			for (HashMap<String,Integer> map : testTermCounts) {
//				for (Entry<String,Integer> entry : map.entrySet()) {
//					System.out.println(entry.getKey() + ":" + entry.getValue());
//				}
//			}
//			
//		} catch (IOException|ClassNotFoundException e) {e.printStackTrace();} 
		
		return null;
	}
	
	// create term map from string
	// tokenize string, then create map from tokens
	private HashMap<String,Integer> createUserMap(String str) throws InvalidFormatException, IOException {
		
		HashMap<String,Integer> termMap = new HashMap<String,Integer>();
		
		String[] tokenArray = runTokenizer(str);
		
		for (String token : Arrays.asList(tokenArray)) {
			Integer tokenCount = termMap.get(token);
			if (tokenCount != null) 
				termMap.put(token, tokenCount+1);
			else
				termMap.put(token, 1);
		}
		
		return termMap;
	}
	
	//after user maps are created, create overall document map
	private HashMap<String,Integer> createDocMap(ArrayList<HashMap<String,Integer>> userMaps) {
		
		HashMap<String,Integer> termMap = new HashMap<String,Integer>();
		
		for (HashMap<String,Integer> map : userMaps) {
			for (String key : map.keySet()) {
				Integer tokenCount = termMap.get(key);
				if (tokenCount != null)
					termMap.put(key, tokenCount+1);
				else
					termMap.put(key, 1);
			}
		}
		return termMap;
	}
	
//	public ArrayList<ArrayList<String>> getAllAnswersArray() {
//		return allFinalAnswers;
//	}

	/* (non-Javadoc)
	 * @see extractors.nyit.ExtractionModule#getName()
	 */
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
