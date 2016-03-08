/**
 * 
 */
package extractors.lexical;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
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
public class CreateIDF_Map extends Tokenize implements ExtractionModule {

	// to hold count of documents in which each term appears
	private static HashMap<String,Integer> docCountMap = new HashMap<String,Integer>();
	
	// to hold inverse document frequencies
	private static HashMap<String,Double> idfMap = new HashMap<String,Double>();
	
	//needs to be updated based on the number of users being queried
	private static final int TOTAL_USERS = 441;
	
	//to hold incrementing count of users processed
	private static int userCount = 0;
	
	//keep track of total number of documents (answers)
	private static int docCount = 0;
	
	/* 
	 * @see extractors.nyit.ExtractionModule#extract(extractors.nyit.DataNode)
	 */
	@Override
	public Collection<Feature> extract(DataNode data) {
		
		for (Answer a : data) {
			
			try {
				String finalText = KeyStroke.keyStrokesToFinalText(a.getKeyStrokeList());
				addToDocCountMap(finalText);
				docCount++;
			
			} catch (IOException e) {e.printStackTrace();}
		}
		
		userCount++;
		
		if (userCount == TOTAL_USERS) {
			try {
				
				createIdfMap();
				
				// Print contents of idf hashmap (for testing purposes)
				PrintWriter out = new PrintWriter("TestIDF.txt");
				for (Entry<String,Double> entry : idfMap.entrySet())
					out.println("``"+entry.getKey()+"\t"+entry.getValue());
				out.close();
								
				FileOutputStream fileOut = new FileOutputStream("IDF_DataFile.data");
				ObjectOutputStream oos = new ObjectOutputStream(fileOut);
				oos.writeObject(idfMap);
				oos.close();
				fileOut.close();
				
			} catch (Exception e) {e.printStackTrace();}
			
		}
		return null;
	}
		
	/* 
	 * Create a set from all tokens
	 * 	- Use set to expand/update docFreqMap hashmap 
	 */
	private void addToDocCountMap(String str) throws InvalidFormatException, IOException {
		
		String[] tokenArray = runTokenizer(str);
		Set<String> tokenSet = new HashSet<String>(Arrays.asList(tokenArray));
		
		for (String token : tokenSet) {
			Integer tokenCount = docCountMap.get(token);
			if (tokenCount != null)
				docCountMap.put(token, tokenCount+1);
			else
				docCountMap.put(token,1);
		}
	}
	
	// after doc counts have been calculated, create inverse doc freq
	private void createIdfMap() {
		
		for (Entry<String,Integer> entry : docCountMap.entrySet()) {
			double df = (((double)docCount)/entry.getValue());
			double idf = Math.log(df);
			idfMap.put(entry.getKey(), idf);
		}
	}

	/* (non-Javadoc)
	 * @see extractors.nyit.ExtractionModule#getName()
	 */
	@Override
	public String getName() {
		return "Create_IDF_Map";
	}
	
	

}
