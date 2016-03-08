/**
 * 
 */
package extractors.lexical;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import opennlp.tools.util.InvalidFormatException;

/*
 * tf*idf, or Inverse Document Frequency, measures the frequency of a word intra-document, and divides it by the total number
 * of documents using it. This tells us the uniqueness of each word in the document
 */
public abstract class TF_IDF extends Tokenize {
	
	//holds term frequency map for each user
	private static ArrayList<HashMap<String,Integer>> termCounts = new ArrayList<HashMap<String,Integer>>();
	
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
	
}
//
//	private HashMap<String,Integer> termFreqMap; //term frequency intra-document, instantiated for each document
//	private HashMap<String,Double> TF_IDF_Map; //map of document's tf*idf values, instantiated for each document	
//	private ArrayList<String[]> tokenArray; // = new ArrayList<String[]>(); //arraylist of token arrays
//	private HashMap<String,Integer> documentFreqMap = new HashMap<String,Integer>(); //term frequency across all documents
//	private ArrayList<HashMap<String,Integer>> termFreqMapArray = new ArrayList<HashMap<String,Integer>>(); //array of termFreqMaps
//	private ArrayList<HashMap<String,Double>> TF_IDF_MapArray = new ArrayList<HashMap<String,Double>>(); //array of TF*IDF Maps
//	private List<Double> averageTF_IDFArray = new ArrayList<Double>(); 
//	
//	public TF_IDF(ArrayList<String[]> array) {
//		tokenArray = array;
//		runTF_IDF();
//	}
//	
//	// default constructor
//	public TF_IDF() {}
//	
//	/*
//	 * Takes an arraylist of token arrays
//	 * 1) creates intra-document frequency distributions
//	 * 2) creates frequency distribution a cross all documents
//	 * 3) calculates tf*idf based on above 
//	 * 4) calculates average of top n tf*idf's
//	 */
//	public void runTF_IDF() {
//    	createTermFreqDist();
//    	createDocFreqDist();
//    	createTF_IDF_Map();
//    	avgTF_IDF(10);
////    	System.out.println(averageTF_IDFArray);
//	}
//	
//	public void createTermFreqDist() {
//			
//		for (String[] tokens : tokenArray) {
//			
//			termFreqMap = new HashMap<String,Integer>();
//			for (String w: Arrays.asList(tokens)) {
//				Integer num = termFreqMap.get(w);
//				if (num != null)
//					termFreqMap.put(w,num+1);
//				else
//					termFreqMap.put(w,1);
//			}
//			termFreqMapArray.add(termFreqMap);
////			System.out.println("TermFreqDist:\n"+termFreqMap);
//		}
//	} //close createTermFreqDist
//	
//	public void createDocFreqDist() {
//		
//		for (HashMap<String,Integer> termFD : termFreqMapArray) {
//		
//			for (String term : termFD.keySet()) {
//				Integer num = documentFreqMap.get(term);
//				if (num != null)
//					documentFreqMap.put(term, num+1);
//				else
//					documentFreqMap.put(term, 1);
//			}
//		}
////		System.out.println("DocFreqDist:\n"+documentFreqMap);
//	}
//	
//	public void createTF_IDF_Map() {
//		
//		int index = 0; //used for tf calculation below
//		
//		for (String[] tArray : tokenArray) {
//			
//			TF_IDF_Map = new HashMap<String,Double>();
//		
//			for (String token : tArray) {
//
//				int tf = termFreqMapArray.get(index).get(token);
//				double idf = Math.log(documentFreqMap.size()) / documentFreqMap.get(token);
//				double tf_idf = tf * idf;
//				
//				TF_IDF_Map.put(token, tf_idf);
//			} //close token loop
//			
//			TF_IDF_MapArray.add(TF_IDF_Map);
//			index++;
////			System.out.println("TF_IDF_Map:\n"+TF_IDF_Map);
//		} // close String[] loop
//		
//	}	
//	
//	// averages the top "numToAvg" occurrences 
//	public void avgTF_IDF(int numToAvg) {
//		
//		for (HashMap<String,Double> tfArray : TF_IDF_MapArray) {
//			List<Double> tfVals = new ArrayList<Double>(tfArray.values());
//			Collections.sort(tfVals);
//			
//			double averageTF_IDF = 0;
//			for (int i = tfVals.size() - numToAvg; i < tfVals.size(); i++) {
//				averageTF_IDF += tfVals.get(i);
//			}
//			averageTF_IDF /= numToAvg;
//			averageTF_IDFArray.add(averageTF_IDF);
//		}		
//	}	
//	
//	public double getAvgTF_IDF(int index) {
//		return averageTF_IDFArray.get(index);
//	}
//	
//} //close TF_IDF class