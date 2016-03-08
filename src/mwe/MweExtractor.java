/**
 * 
 */
package mwe;

import java.io.*;
import java.util.*;

import edu.mit.jmwe.data.IMWE;
import edu.mit.jmwe.data.IToken;
import edu.mit.jmwe.data.Token;
import edu.mit.jmwe.detect.Consecutive;
import edu.mit.jmwe.detect.IMWEDetector;
import edu.mit.jmwe.index.IMWEIndex;
import edu.mit.jmwe.index.MWEIndex;
import events.EventList;
import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import extractors.lexical.TokenExtender;
import extractors.lexical.TokenExtender.TokenSpan;
import keystroke.KeyStroke;

/**
 * @author Adam Goodkind
 *
 */
public class MweExtractor {

	private static IMWEDetector detector;
	private ArrayList<IMWE<IToken>> allAnswersMwes; 
	private HashMap<String,Integer> allAnswersMweCount;
	private TokenExtender tokenExtender = new TokenExtender();
	private HashMap<ArrayList<String>,Long> mwePosTagsMap;
	private ArrayList<ArrayList<TokenExtended>> mweTokens;
	private ArrayList<TokenExtended> allTokensExtended;
	
	static {
		File idxData = new File("mweindex_wordnet3.0_semcor1.6.data");
		// construct an MWE index and open it
		IMWEIndex index = new MWEIndex(idxData);
		try {
			index.open();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// make a basic detector
		detector = new Consecutive(index);
	}
	
	public MweExtractor() {
		allAnswersMwes = new ArrayList<IMWE<IToken>>();
		allAnswersMweCount = new HashMap<String,Integer>();
		tokenExtender = new TokenExtender();
		mweTokens = new ArrayList<ArrayList<TokenExtended>>();
		allTokensExtended = new ArrayList<TokenExtended>();
	}
	
	public void clearLists() {
		allAnswersMwes.clear();
		allAnswersMweCount.clear();
		mweTokens.clear();
		allTokensExtended.clear();
	}
	
	public void parseTextToExtendedTokens(String answerText) {
		clearLists();
		try {
			allTokensExtended = tokenExtender.generateExtendedTokens(answerText);
			allAnswersMwes.addAll(extractMweInfo(allTokensExtended));
		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public ArrayList<IMWE<IToken>> getAllAnswersMwes() {
		return allAnswersMwes;
	}

	public void setAllAnswersMwes(ArrayList<IMWE<IToken>> allAnswersMwes) {
		this.allAnswersMwes = allAnswersMwes;
	}

	public HashMap<String, Integer> getAllAnswersMweCount() {
		return allAnswersMweCount;
	}

	public void setAllAnswersMweCount(HashMap<String, Integer> allAnswersMweCount) {
		this.allAnswersMweCount = allAnswersMweCount;
	}

//	public TokenExtender getTokenExtender() {
//		return tokenExtender;
//	}

//	public void setTokenExtender(TokenExtender tokenExtender) {
//		this.tokenExtender = tokenExtender;
//	}

	public ArrayList<ArrayList<TokenExtended>> getMweTokens() {
		return mweTokens;
	}

	public void setMweTokens(ArrayList<ArrayList<TokenExtended>> mweTokens) {
		this.mweTokens = mweTokens;
	}

	public ArrayList<TokenExtended> getallTokensExtended() {
		return allTokensExtended;
	}
	
	/**
	 * Extracts tokens of MWEs, and extracts their spans
	 * @param tokensInfo	An ArrayList of tokenized text
	 * @return				MWE tokens
	 * @throws IOException
	 */
	public List<IMWE<IToken>> extractMweInfo(ArrayList<TokenExtended> tokensInfo) throws IOException {
//		File idxData = new File("mweindex_wordnet3.0_semcor1.6.data");
//		// construct an MWE index and open it
//		IMWEIndex index = new MWEIndex(idxData);
//		index.open();
//		// make a basic detector
//		IMWEDetector detector = new Consecutive(index);
		List<IToken> answerITokens = new ArrayList<IToken>();
		for (TokenExtended tokenInfo : tokensInfo) {
		//	System.out.println(tokenInfo.token+" "+tokenInfo.tokenSpan.toString());
			answerITokens.add(new Token(tokenInfo.token, tokenInfo.partOfSpeech, tokenInfo.lemma));
		}
		// run MWE detector
		List<IMWE<IToken>> mwes = detector.detect(answerITokens);
		// extract spans
		mweTokens = getMweTokens(mwes,tokensInfo);
		return mwes;
	}
	/**
	 * 
	 * @param mweList	MWE tokens
	 * @param allTokens	full text tokens
	 * @return			List of spans of MWEs
	 */
	public ArrayList<ArrayList<TokenExtended>> getMweTokens(List<IMWE<IToken>> mweList, ArrayList<TokenExtended> allTokens) {
		ArrayList<ArrayList<TokenExtended>> allMweTokens = new ArrayList<ArrayList<TokenExtended>>();
		int mweStartIndex = 0;
		boolean mweMatch = false;
		int mweListIndex = 0;
		int mwePartIndex = 0;
		int allTokensIndex = 0;
		
		while (mweListIndex < mweList.size()) {
			int mweWordCount = mweList.get(mweListIndex).getTokens().size();
			while (mwePartIndex < mweWordCount) {				
				while (allTokensIndex < allTokens.size()) {
					String mweWordString = mweList.get(mweListIndex).getTokens().get(mwePartIndex).getForm().toString();
					String allTokensWordString = allTokens.get(allTokensIndex).token;
					if (mweWordString.equals(allTokensWordString)) {	// tokens match
						if (mweMatch == false) {						// possible first word of MWE
							mweMatch = true;
							mweStartIndex = allTokensIndex;
							allTokensIndex++;
							mwePartIndex++;
						}
						else if (mweMatch == true) {					// already matched at least 1 word
							if (mwePartIndex + 1 == mweWordCount) {		// final word of MWE
								ArrayList<TokenExtended> singleMwe = getTokens(mweStartIndex,allTokensIndex);
								allMweTokens.add(singleMwe);
								mweMatch = false;						// reset
								mwePartIndex = 0;						// start at beginning of MWE
								mweListIndex++;							// next MWE
								allTokensIndex = mweStartIndex;			// for MWEs on same token
							}
							else {										// still in MWE
								allTokensIndex++;
								mwePartIndex++;							// search next word in MWE
							}
						}
					}
					else if (!mweWordString.equals(allTokensWordString)) {
						if (mweMatch)
							allTokensIndex = mweStartIndex + 1;
						else
							allTokensIndex++;
						mwePartIndex = 0;								// restart MWE string if partially through it
						mweMatch = false;
					}
					break;
				}
				break;
			}
		} 
		return allMweTokens;
	}
	
	/**
	 * Returns list of TokenSpans between two token indices
	 * @param firstIndex	Beginning index to pull
	 * @param lastIndex		Final index to pull
	 * @return				List of TokenSpans
	 */
	private ArrayList<TokenExtended> getTokens(int firstIndex, int lastIndex) {
		ArrayList<TokenExtended> tokens = new ArrayList<TokenExtended>();
		int spanListIndex = firstIndex;
		while (spanListIndex >= firstIndex && spanListIndex <= lastIndex) {
			tokens.add(allTokensExtended.get(spanListIndex));
			spanListIndex++;
		}
		return tokens;
	}
	
	/**
	 * Extract a List of POS tags of MWEs
	 * @return	List of POS tags of MWEs
	 */
	public ArrayList<String> generateMwePosTagsMap() {
		ArrayList<String> mwePosTags = new ArrayList<String>();
		for(IMWE<IToken> mwe : allAnswersMwes) {
			StringBuilder posTags = new StringBuilder();
			for (IToken i : mwe.getTokens()) 
				posTags.append(i.getTag().toString()+" ");
			mwePosTags.add(posTags.substring(0, posTags.length()-1).toString()); //remove trailing space
		}
		return mwePosTags;
	}
	
	/**
	 * add MWE tokens to hashmap
	 */
	public void generateMweHashMap() {	
		for(IMWE<IToken> mwe : allAnswersMwes) {
			if (allAnswersMweCount.containsKey(mwe.toString()))
				allAnswersMweCount.put(mwe.toString(), allAnswersMweCount.get(mwe.toString())+1);
			else
				allAnswersMweCount.put(mwe.toString(), 1);
		}
	}
	
	public void printMweHashMap() {
		for (String mwe : allAnswersMweCount.keySet()) {
			Integer count = allAnswersMweCount.get(mwe);
			System.out.println(mwe+"\t"+count);
		}
	}

}
