/**
 * 
 */
package extractors.lexical;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;

import java.util.Set;

import opennlp.tools.util.InvalidFormatException;
import keystroke.*;

/**
 * @author agoodkind
 * Goes through entire database, and extracts all answers.
 * 	Relevant query:
 * 	
 */
public class CreateTFIDF_Map extends Tokenize implements ExtractionModule {

	// to hold total term counts
	private static HashMap<String,Double> termFreqMap = new HashMap<String,Double>();

	// to hold count of documents in which each term appears
	private static HashMap<String,Integer> docFreqMap = new HashMap<String,Integer>();
	
	// to hold final tf*idf metrics
	private static HashMap<String,Integer> tfidfMap = new HashMap<String,Integer>();
	
	//needs to be updated based on the number of users being queried
	private static final int TOTAL_USERS = 100;
	
	//to hold incrementing count of users processed
	private static int userCount = 0;
	
	//keep track of total number of documents (answers)
	private static int docCount = 0;
	
	//keeps track of total number of words (tokens)
	private static int wordCount = 0;
	
	/* 
	 * @see extractors.nyit.ExtractionModule#extract(extractors.nyit.DataNode)
	 */
	@Override
	public Collection<Feature> extract(DataNode data) {
		
		for (Answer a : data) {
			
//			System.out.println(a.getFinalText()+"\n");
			
			
			try {
				String finalText = KeyStroke.keyStrokesToFinalText(a.getKeyStrokeList());
				addToTermFreq(finalText);
				docCount++;
			
			} catch (IOException e) {e.printStackTrace();}
			
			
		}
		
		userCount++;
		
		if (userCount == TOTAL_USERS) {
			// add document frequency hashmap
//			HashMap<String, Integer> docData = new HashMap<String,Integer>();
//			System.out.println("Creating doc data");
//			docData = createDocMap(termCounts);
			
			//insert doc data at beginning of termCounts
//			termCounts.add(0,docData);
			
			//write termCounts to file
			try {
				
				reviseTermFreq();
				createTFIDF_Map();
				
//				System.out.println("Doc Count: "+docCount);				

				System.out.println("tf*idf Map");
				for (Entry<String,Integer> entry : tfidfMap.entrySet())
					System.out.println(entry.getKey() + "\t" + entry.getValue());
				
//				System.out.println("Term Freq Map");
//				for (Entry<String,Integer> entry : termFreqMap.entrySet())
//					System.out.println(entry.getKey() + ":" + entry.getValue());
//
//				System.out.println("\n\n\nDoc Freq Map");
//				for (Entry<String,Integer> entry : docFreqMap.entrySet())
//					System.out.println(entry.getKey() + ":" + entry.getValue());
				
//				FileOutputStream fileOut = new FileOutputStream("TFIDF_DataFile.data");
//				ObjectOutputStream oos = new ObjectOutputStream(fileOut);
//				oos.writeObject(termFreqMap);
//				oos.close();
//				fileOut.close();
				
			} catch (Exception e) {e.printStackTrace();}
			
			
			//for testing purposes, read file
//			try {
//				HashMap<String,Integer> testTermCounts = new HashMap<String,Integer>();
//				
//				FileInputStream fileIn = new FileInputStream("TFIDF_DataFile.data");
//				ObjectInputStream ois = new ObjectInputStream(fileIn);
//				testTermCounts = (HashMap<String,Integer>) ois.readObject();
//			
//				for (Entry<String,Integer> entry : testTermCounts.entrySet()) {
//					System.out.println(entry.getKey() + ":" + entry.getValue());
//				}
//				
//			} catch (IOException|ClassNotFoundException e) {e.printStackTrace();} 
		}
		return null;
	}
	
	// tokenize string, then add to overall term frequency map
	private void addToTermFreq(String str) throws InvalidFormatException, IOException {
		
		String[] tokenArray = runTokenizer(str);
		wordCount += tokenArray.length;
		
		for (String token : Arrays.asList(tokenArray)) {
			Double tokenCount = termFreqMap.get(token);
			if (tokenCount != null) 
				termFreqMap.put(token, tokenCount+1.0);
			else
				termFreqMap.put(token, 1.0);
		}
		createDocMap(tokenArray);
	}
	
	/* called from addToTermFreq
	 * 	- After term frequency map is updated:
	 * 		- Update document frequency map 
	 */
	private void createDocMap(String[] tokens) {
		
		Set<String> tokenSet = new HashSet<String>(Arrays.asList(tokens));
		
		for (String token : tokenSet) {
			Integer tokenCount = docFreqMap.get(token);
			if (tokenCount != null)
				docFreqMap.put(token, tokenCount+1);
			else
				docFreqMap.put(token,1);
		}
	}
	
	// update term frequency based on total word count
	private void reviseTermFreq() {
		
		for (Entry<String,Double> entry : termFreqMap.entrySet()) {
			double temp = entry.getValue();
			entry.setValue(temp/wordCount);
		}
		
	}
	
	// calculate tf*idf for each term -> add to map
	private void createTFIDF_Map() {
		for (Entry<String,Double> entry : termFreqMap.entrySet()) {
			double tf = entry.getValue();
			double idf = Math.log(((double)docCount)/docFreqMap.get(entry.getKey()));
			tfidfMap.put(entry.getKey(), (int)Math.round((tf*idf)*100000));
		}
	}
	
	/* (non-Javadoc)
	 * @see extractors.nyit.ExtractionModule#getName()
	 */
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
