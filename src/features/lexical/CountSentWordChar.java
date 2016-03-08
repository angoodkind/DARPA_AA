/**
 * 
 */
package features.lexical;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import extractors.lexical.SentenceDetector;
import extractors.lexical.Tokenize;
import keystroke.*;

/**
 * @author agoodkind
 *
 */
public class CountSentWordChar implements ExtractionModule {
	
	SentenceDetector sentDetector;
	Tokenize tokens;
	Collection<Feature> output;
	Collection<Integer> sentenceCount;
	LinkedList<Integer> wordsPerSentenceCount;	// number of words per sentence
	LinkedList<Integer> charsPerSentenceCount;	// number of characters per sentence
	LinkedList<Double> charsPerWordCount;		// characters/word per sentence
	
	public CountSentWordChar() {
		sentDetector = new SentenceDetector();
		tokens = new Tokenize();
		output = new LinkedList<Feature>();
		sentenceCount = new LinkedList<Integer>();
		wordsPerSentenceCount = new LinkedList<Integer>();
		charsPerSentenceCount = new LinkedList<Integer>();
		charsPerWordCount = new LinkedList<Double>();
	}
	
	public void clearLists() {
		output.clear();
		sentenceCount.clear();
		wordsPerSentenceCount.clear();
		charsPerSentenceCount.clear();
		charsPerWordCount.clear();
	}

	@Override
	public Collection<Feature> extract(DataNode data) {
		clearLists();
//		System.out.println("Creating instance of "+this.getClass().toString());
		for (Answer a : data) {
			try {
				String finalText = KeyStroke.keyStrokesToFinalText(a.getKeyStrokeList());
				String[] sentenceArray = sentDetector.getSentenceList(finalText);
				sentenceCount.add(sentenceArray.length);
				wordsPerSentenceCount.addAll(getWordsPerSentenceCount(sentenceArray));
				charsPerSentenceCount.addAll(getCharsPerSentenceCount(sentenceArray));
				charsPerWordCount.addAll(getCharsPerWordCount(sentenceArray));
			
			} catch (IOException e) {e.printStackTrace();}
		}
		
		output.add(new Feature("Sentence_Count",sentenceCount));
		output.add(new Feature("Words_Per_Sentence_Count",wordsPerSentenceCount));
		output.add(new Feature("Charss_Per_Sentence_Count",charsPerSentenceCount));
		output.add(new Feature("Chars_Per_Word_Count",charsPerWordCount));
//	    for (Feature f : output) System.out.println(f.toTemplate());
		return output;
	}

	public LinkedList<Integer> getWordsPerSentenceCount(String[] sentenceArray) {
	    LinkedList<Integer> wordCounts = new LinkedList<Integer>();
	    for (String sentenceStr : sentenceArray) {
	    	try {
	    		String[] tokenArr = tokens.runTokenizer(sentenceStr);
	    		wordCounts.add(tokenArr.length);
	    	} catch (IOException e) {e.printStackTrace();}
	    }
	    return wordCounts;
	}
	
	public LinkedList<Integer> getCharsPerSentenceCount(String[] sentenceArray) {
	    LinkedList<Integer> charCounts = new LinkedList<Integer>();
	    for (String sentenceStr : sentenceArray)
	    		charCounts.add(sentenceStr.length());
	    return charCounts;
	}
	
	public LinkedList<Double> getCharsPerWordCount(String[] sentenceArray) {
		LinkedList<Double> charsPerWordCounts = new LinkedList<Double>();
		for (String sentenceStr : sentenceArray) {
			try {
				String[] tokenArray = tokens.runTokenizer(sentenceStr);
				charsPerWordCounts.add(sentenceStr.length()/(tokenArray.length * 1.));
			} catch ( IOException e) {e.printStackTrace();}
		}
		return charsPerWordCounts;
	}
	
	@Override
	public String getName() {
		return "Count_Sent_Word_Char";
	}

}
