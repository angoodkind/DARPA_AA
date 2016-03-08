/**
 * 
 */
package features.lexical;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import extractors.lexical.Tokenize;
import features.pause.PauseBursts;

/**
 * @author agoodkind
 *
 */
public class LexEventsBtwPause extends Tokenize implements ExtractionModule {

	ArrayList<Integer> wordsBtwPauseCount;		// count of words between pause
	ArrayList<Integer> sentStartCharToPause;	// count between sentence start and first pause
	ArrayList<Double> sentStartWordsToPause;	// count between sentence start and first pause
	PauseBursts pb;  										// for use in extracting pause list
	LinkedList<Feature> output;		
	
	public LexEventsBtwPause() {
		wordsBtwPauseCount = new ArrayList<Integer>();
		sentStartCharToPause = new ArrayList<Integer>();
		sentStartWordsToPause = new ArrayList<Double>();
		output = new LinkedList<Feature>();
	}
	
	public void clearLists() {
		wordsBtwPauseCount.clear();
		sentStartCharToPause.clear();
		sentStartWordsToPause.clear();
		pb = null;
		output.clear();
	}
	
	@Override
	public Collection<Feature> extract(DataNode data) {
//		System.out.println("Creating instance of "+this.getClass().toString());
		clearLists();

		for (Answer a : data) {
			try {
				pb = new PauseBursts();
				// get List of word Tokens
				String[] tokens = runTokenizer(a.getCharStream());
//				System.out.println(Arrays.toString(tokens));
				// get starting indices (of charStream) for all tokens
				ArrayList<Integer> startPositions = getStartIndex(a.getCharStream());
//				System.out.println("Start Pos: "+startPositions);
				
				ArrayList<Integer> endPositions = getEndIndex(a.getCharStream());
//				System.out.println("End Pos: "+endPositions);
				
				// get list of Pauses from charStream
				pb.generatePauseDownList(a.getKeyStrokes(),1000);
				List<Integer> pauseList = pb.getPauseDownList();
//				System.out.println("Pause Pos: "+pauseList);
				
				// get count of words between pauses
				wordsBtwPauseCount.addAll(getWordsBtwPause(startPositions,pauseList));
//				System.out.println("# of Pauses: "+pauseList.size());
//				System.out.println("# of WordCounts: "+getWordsBtwPause(startPositions,pauseList).size());
				
				// get number of chars from sentence start to first pause
				sentStartCharToPause.addAll(getCharsToPause(".",tokens,startPositions,pauseList));
				// get number of words from sentence start to first pause
				sentStartWordsToPause.addAll(getWordsToPause(".",tokens,startPositions,pauseList));
						
			} catch (IOException e) {e.printStackTrace();}
		}
		
		
		output.add(new Feature("WordsBetweenPause_Count",wordsBtwPauseCount));
//		output.add(new Feature("CharsFromSentStartToPause", sentStartCharToPause));
//		output.add(new Feature("WordsFromSentStartToPause", sentStartWordsToPause));
		
//		for (Feature f : output) System.out.println(f.toTemplate());
		
		return output;
	}
	
	/* counts number of words between pauses
	 * uses start indices of words, checks whether they fall between pauses
	 */
	private ArrayList<Integer> getWordsBtwPause(ArrayList<Integer> starts, List<Integer> pauses) {
		ArrayList<Integer> allWordCounts = new ArrayList<Integer>(); // to hold word count
		
		for (int i = 0; i < pauses.size()-1; i++) {
			int wordCount = 0;
			int startIdx = pauses.get(i);
			int endIdx = pauses.get(i+1);
			for (int wordStartIdx : starts) {
				
				if ((wordStartIdx >= startIdx) && (wordStartIdx < endIdx)) {
//					System.out.println("Start: "+startIdx+" End: "+endIdx);
//					System.out.println("Word Start: "+wordStartIdx);
					wordCount++;
				}
			}
			allWordCounts.add(wordCount);
//			System.out.println("Word Count: "+wordCount);
		}
		return allWordCounts;
	}
	/* 
	 * finds number of chars after a punctuation mark to first pause
	 */
	private ArrayList<Integer> getCharsToPause(String punct, String[] tokenArray, ArrayList<Integer> starts, List<Integer> pauses) {
		ArrayList<Integer> charsToPauseCounts = new ArrayList<Integer>();
		
		for (int i = 0; i < tokenArray.length; i++) {
			if (tokenArray[i].contains(punct)) { //found punctuation mark
				int sentenceStart = starts.get(i); // find start index of punctuation mark
				for (int j = 0; j < pauses.size(); j++) {
					if (pauses.get(j) >= sentenceStart) { //find next pause after punctuation mark
						int periodToPause = pauses.get(j) - sentenceStart;
						charsToPauseCounts.add(periodToPause);
//						System.out.println("SentStart: "+sentenceStart+" PauseIdx: "+pauses.get(j));
						break;
					}
				}
			}
		}
		
		return charsToPauseCounts;
	}
	
	
	/* finds the number of words between sentence start and first pause
	 * 1) Matches punctuation entered by user
	 * 2) finds the next pause in the array, after the sentence start
	 * 3) Counts number of whole words before pause
	 * 4) counts fraction of word, if pause occurs in the middle of a word
	 */
	private ArrayList<Double> getWordsToPause(String punct, String[] tokenArray, ArrayList<Integer> starts, List<Integer> pauses) {
		ArrayList<Double> wordsToPauseCounts = new ArrayList<Double>();
		
		for (int i = 0; i < tokenArray.length; i++) {
			if (tokenArray[i].contains(punct)) { //found punctuation mark
				
				int sentenceStart = starts.get(i)+1; // find index after punctuation mark, i.e. start of next sentence
				int sentenceStartIdx = i+1;
//				System.out.println("SentStartWord: "+sentenceStart+" Idx: "+sentenceStartIdx);
				int pauseIdx = 0;
				
				for (int j = 0; j < pauses.size(); j++) {
					if (pauses.get(j) >= sentenceStart) { //find next pause after punctuation mark
						pauseIdx = pauses.get(j);
//						System.out.println("SentStartWord: "+sentenceStart+" PauseIdx: "+pauses.get(j));
						break;
					}
				}
				
				if (pauseIdx == 0) {
					wordsToPauseCounts.add(0.0);
//					System.out.println("WordsToPause0: "+pauseIdx);
				} else {
					double wordsToPause = 0;
					for (int s = 0; s < starts.size(); s++) { // find number of words between period and pause
						if (starts.get(s) > pauseIdx) {
//							System.out.println("NextStartWord: "+starts.get(s)+" Idx: "+s);
							wordsToPause += ((s) - sentenceStartIdx); // add number of whole words
//							System.out.println("WordsToPause1: "+wordsToPause);
							wordsToPause += ((double)(starts.get(s)-pauseIdx))/tokenArray[s].length();
//							System.out.println("WordsToPause2: "+wordsToPause);
							wordsToPauseCounts.add(wordsToPause);
							break;
						}
					}
				} // close else
			} // close if (tokenArray[i].contains(punct))
		} // close for (int i = 0; i < tokenArray.length; i++)
		
		return wordsToPauseCounts;
	}
	
	/* 
	 * @see extractors.nyit.ExtractionModule#getName()
	 */
	@Override
	public String getName() {
		return "Between Pause Lex Events";
	}
}
