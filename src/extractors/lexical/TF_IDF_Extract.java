/**
 * 
 */
package extractors.lexical;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

import opennlp.tools.util.InvalidFormatException;

/*
 * tf*idf, or Inverse Document Frequency, measures the frequency of a word intra-document, and divides it by the total number
 * of documents using it. This tells us the uniqueness of each word in the document
 */
public abstract class TF_IDF_Extract extends Tokenize {
	
	//create tf*idf map from an individual answer's final text string
	public HashMap<String,Double> createTfidfMap(String finalText) {
		
		//tf*idf score map for an individual answer
		HashMap<String,Double> tfidfMap = new HashMap<String,Double>();
		
		try {
			
			FileInputStream fileIn = new FileInputStream("IDF_DataFile.data");
			ObjectInputStream ois = new ObjectInputStream(fileIn);
			
			HashMap<String,Double> idfMap = (HashMap<String,Double>) ois.readObject();
			HashMap<String,Double> tfMap = createTfMap(finalText);
			
			for (Entry<String,Double> entry : tfMap.entrySet()) {
				double tf = entry.getValue();
				if (idfMap.get(entry.getKey()) != null) {
					double idf = idfMap.get(entry.getKey());
					tfidfMap.put(entry.getKey(), tf*idf);
				}
			}
		
		} catch (IOException | ClassNotFoundException e) { e.printStackTrace();}
		
		return tfidfMap;
	}
	
	//creates term frequency map from an individual answer's final text string
	public HashMap<String,Double> createTfMap(String finalText) throws InvalidFormatException, IOException {
		
		//stores initial counts of terms
		HashMap<String,Double> termCountMap = new HashMap<String,Double>();

		//stores term frequencies
		HashMap<String,Double> tfMap = new HashMap<String,Double>();
		
		String[] tokenArray = runTokenizer(finalText);
		
		int wordCount = tokenArray.length;
		
		//create term counts
		for (String token : Arrays.asList(tokenArray)) {
			Double tokenCount = termCountMap.get(token);
			if (tokenCount != null) 
				termCountMap.put(token, tokenCount+1.0);
			else
				termCountMap.put(token, 1.0);
		}
		
		//create term frequencies map
		for (Entry<String,Double> entry : termCountMap.entrySet()) 
			tfMap.put(entry.getKey(), entry.getValue()/wordCount);
		
		return tfMap;
	}
}