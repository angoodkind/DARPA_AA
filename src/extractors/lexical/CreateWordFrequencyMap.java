package extractors.lexical;

import java.util.*;

import com.google.common.base.Functions;
import com.google.common.collect.Ordering;

import java.io.*;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import extractors.data.TestVectorShutdownModule;
import extractors.lexical.Tokenize;
import keystroke.KeyStroke;

/**
 * To print results at the completion of collection, use TestVector creation. This
 * will call the shutdown() method at the end.
 * 
 * @author Adam Goodkind
 *
 */
public class CreateWordFrequencyMap implements ExtractionModule, TestVectorShutdownModule {

	protected static HashMap<String,Integer> wordMap = new HashMap<String,Integer>();
	protected static Tokenize tokenizer = new Tokenize();
	protected static final int NUM_PRINTED_TERMS = 1100; //number of terms to print
	
	@Override
	public Collection<Feature> extract(DataNode data) {
		for (Answer a : data) {
			// tokenize text, and add lowercase tokens to frequency dictionary
			try {
				String finalText = KeyStroke.keyStrokesToFinalText(a.getKeyStrokeList());
				String[] tokenList = tokenizer.runTokenizer(finalText);
				for (String token : tokenList) {
					if (wordMap.containsKey(token.toLowerCase()))
						wordMap.put(token.toLowerCase(), wordMap.get(token.toLowerCase())+1);
					else
						wordMap.put(token.toLowerCase(), 1);
				}				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public String getName() {
		return "Create Word Frequency Map";
	}

	/**
	 * Print word frequencies, using Guava's natural Ordering to sort wordMap by value. The
	 * map is returned in reverse order, up to the number of desired terms specified in
	 * NUM_PRINTED_TERMS
	 */
	@Override
	public void shutdown() {
		List<String> sortedKeys = Ordering.natural().onResultOf(Functions.forMap(wordMap)).
				immutableSortedCopy(wordMap.keySet());
		for (int i = sortedKeys.size()-1; i > sortedKeys.size() - NUM_PRINTED_TERMS; i--) {
			String key = sortedKeys.get(i);
			int keyFrequency = wordMap.get(key);
			System.out.println(key+": "+keyFrequency);
		}		
	}

}
