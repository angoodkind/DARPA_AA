/**
 * 
 */
package extractors.lexical;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.util.InvalidFormatException;

/**
 * @author agoodkind
 *
 */
public class BigramExtractor extends Tokenize {
	
	// takes a string and creates an array of bigrams
	public String[] createBigramTokens(String rawStr) throws InvalidFormatException, IOException {
		
		String[] unigramTokens = runTokenizer(rawStr);
		
		List<String> tempBigramArrayList = new ArrayList<String>();
		
		for (int i = 0; i < unigramTokens.length - 1; i++) {	
			String tempBigram = unigramTokens[i]+" "+unigramTokens[i+1];
			tempBigramArrayList.add(tempBigram);
		}
		
		String[] BigramArray = new String[tempBigramArrayList.size()];
		
		BigramArray = tempBigramArrayList.toArray(BigramArray);
		
		return BigramArray;
		
	}

}
