package extractors.lexical;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.Span;

/**
 * @author agoodkind
 *	Uses the OpenNLP API to tokenize a string
 */
public class Tokenize {
	
	private Tokenizer tokenizer;
	private String[] unigramTokens;
	
	public Tokenize(){
		InputStream isToken = null;
		try {
		isToken = new FileInputStream("en-token.bin");
		TokenizerModel tModel = new TokenizerModel(isToken);
		tokenizer = new TokenizerME(tModel);
		} catch (IOException e) {e.printStackTrace();}
		finally {
			if (isToken != null) {
				try {
					isToken.close();
				} catch (IOException e) {e.printStackTrace();}
			}
		}
	}
	
	public Tokenize(String rawString) {
		InputStream isToken = null;
		try {
		isToken = new FileInputStream("en-token.bin");
		TokenizerModel tModel = new TokenizerModel(isToken);
		tokenizer = new TokenizerME(tModel);
		runTokenizer(rawString);
		} catch (IOException e) {e.printStackTrace();}
		finally {
			if (isToken != null) {
				try {
					isToken.close();
				} catch (IOException e) {e.printStackTrace();}
			}
		}

	}
	
	public String[] runTokenizer(String rawStr) throws InvalidFormatException, IOException {
		unigramTokens = tokenizer.tokenize(rawStr);
		return unigramTokens;
	}
	/**
	 * Returns the spans of the tokens
	 * @param rawString		String of text to be tokenized
	 * @return
	 * @throws InvalidFormatException
	 * @throws IOException
	 */
	public Span[] getSpan(String rawString) throws InvalidFormatException, IOException {
		Span[] tokenSpans = tokenizer.tokenizePos(rawString);
		return tokenSpans;
	}
	// retrieves the Spans (OpenNLP class) of the token; returns the starting index of each token
	public ArrayList<Integer> getStartIndex(String rawStr) throws InvalidFormatException, IOException {
		
		ArrayList<Integer> startIndexes = new ArrayList<Integer>();
		Span[] tokenSpans = tokenizer.tokenizePos(rawStr);;
		for (Span s : tokenSpans) {
			startIndexes.add(s.getStart());
		}
		
		return startIndexes;
	}

  public ArrayList<Integer> getEndIndex(String rawStr) throws InvalidFormatException, IOException {

    ArrayList<Integer> endIndices = new ArrayList<Integer>();
    Span[] tokenSpans = tokenizer.tokenizePos(rawStr);;

//    InputStream isToken = new FileInputStream("en-token.bin");
//    TokenizerModel tModel = new TokenizerModel(isToken);
//    Tokenizer tokenizer = new TokenizerME(tModel);
//    tokenSpans = tokenizer.tokenizePos(rawStr);
//    isToken.close();

    for (Span s : tokenSpans) {
      endIndices.add(s.getEnd());
    }

    return endIndices;
  }
	
	public Set<String> createWordSet(String rawStr) throws InvalidFormatException, IOException {
		
		String[] tokens = runTokenizer(rawStr);
		Set<String> wordSet = new HashSet<String>();
		
		for (String t : tokens) 
			wordSet.add(t);
		
		return wordSet;
	}
	
	public double calculateTTR(String rawStr) throws InvalidFormatException, IOException {
		
		int wordListSize = runTokenizer(rawStr).length;
		int wordSetSize = createWordSet(rawStr).size();
		
		double typeTokenRatio = (wordSetSize * 1.)/wordListSize;
		
		return typeTokenRatio;
	}
	
	public double calculateMATTR(String rawStr, int windowSize) throws InvalidFormatException, IOException {
		
		List<String> tokens = Arrays.asList(runTokenizer(rawStr));
		
		int windowStart = 0; //starting index
		int listLength = tokens.size(); // length of entire list
		double MATTR = 1; //initialize MATTR
		
		while (windowStart < (listLength - windowSize + 1)) {
			
			List<String> window = tokens.subList(windowStart, windowSize+windowStart);
			Set<String> windowSet = new HashSet<String>(window);
			double window_lex_div = ((double)windowSet.size())/windowSize;
			MATTR = ((MATTR * windowStart) + window_lex_div)/(windowStart + 1);
			windowStart++;
		}
		
		return MATTR;
	}
	
	public String[] getUnigramTokens() {
		return unigramTokens;
	}

}
