/**
 * 
 */
package extractors.lexical;

import java.util.*;

import edu.stanford.nlp.ling.CoreAnnotations.CharacterOffsetBeginAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.CharacterOffsetEndAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import events.EventList;
import keystroke.KeyStroke;
import keytouch.KeyTouch;
import keytouch.KeyTouch.VisibleKeystrokeInfo;
import mwe.TokenExtended;

/**
 * @author Adam Goodkind
 *
 */
public class TokenExtender {

	protected static StanfordCoreNLP pipeline;

    static {
        // Create StanfordCoreNLP object properties, with POS tagging
        // (required for lemmatization), and lemmatization
        Properties props;
        props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma");
        pipeline = new StanfordCoreNLP(props);
    }
    
    /**
     * Takes an EventList of keystrokes. First parses for visible text info,
     * and then performs annotation of visible text. 
     * 
     * This version of the method also includes the raw keystroke indices that the
     * visible keystrokes correspond to
     * 
     * @param ksList	EventList of KeyStrokes
     * @return			List of extended tokens
     */
    public ArrayList<TokenExtended> generateExtendedTokens(LinkedList<KeyTouch> ktList) {
    	ArrayList<TokenExtended> allTokenInfo = new ArrayList<TokenExtended>();
    	
    	VisibleKeystrokeInfo keystrokeInfo = KeyTouch.toVisibleKeystrokeInfo(ktList);
    	ArrayList<Integer> visibleKeystrokeIndices = keystrokeInfo.getVisibleKeystrokeIndices();
    	
		// create an empty Annotation with just the given text
        Annotation document = new Annotation(keystrokeInfo.getVisibleText());
//        System.out.println(keystrokeInfo.getVisibleText());

        // run all Annotators on this text
        this.pipeline.annotate(document);

     // Iterate over all of the sentences
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        int overallIndex = 0;
        int sentenceIndex = 0;
        for(CoreMap sentence: sentences) {
            // Iterate over all tokens in a sentence
        	int sentenceLength = sentence.get(TokensAnnotation.class).size();
        	int wordIndex = 0;
            for (CoreLabel fullToken: sentence.get(TokensAnnotation.class)) {
            	String token = fullToken.get(TextAnnotation.class);
                String partOfSpeech = fullToken.get(PartOfSpeechAnnotation.class);
                String lemma = fullToken.get(LemmaAnnotation.class);
                
                int beginToken = fullToken.get(CharacterOffsetBeginAnnotation.class);
		        int endToken = fullToken.get(CharacterOffsetEndAnnotation.class);
		        TokenSpan tokenSpan = new TokenSpan(beginToken,endToken,overallIndex);
		        
		        
		        int beginKeyTouches = visibleKeystrokeIndices.get(beginToken);
		        int endKeyTouches = visibleKeystrokeIndices.get(endToken-1);
		        TokenSpan keyTouchSpan = new TokenSpan(beginKeyTouches,endKeyTouches,overallIndex);
		        
		        ArrayList<KeyTouch> tokenKtList = new ArrayList<KeyTouch>();
		        for (int i = beginKeyTouches; i <= endKeyTouches; i++) {
		        	tokenKtList.add(ktList.get(i));
		        }

		        // stupid exception for stupid smiley-face
		        if (token.equals(":-RRB-")) {
	                	token = ":)";
	                	partOfSpeech = ".";
	                	lemma = ":)";
                }
		        if (token.equals("-RRB-")) {
	                	token = ")";
	                	partOfSpeech = ".";
	                	lemma = ")";
		        }
		        IndexInfo indexInfo = new IndexInfo(overallIndex,sentenceIndex,wordIndex,sentenceLength);
                TokenExtended te = new TokenExtended(token,
                									partOfSpeech,
                									lemma,
                									tokenSpan,
                									keyTouchSpan,
                									indexInfo,
                									tokenKtList);
                allTokenInfo.add(te);
                overallIndex++;
                wordIndex++;
            }
            sentenceIndex++;
        }
    	
    	return allTokenInfo;
    }
    
    /**
     * Extracts list of TokenExtendeds from text string
     * @deprecated			Pass in EventList
     * @param answerText	text String
     * @return				List of TokenExtendeds
     */
    @Deprecated
	public ArrayList<TokenExtended> generateExtendedTokens(String answerText) {
		ArrayList<TokenExtended> allTokenInfo = new ArrayList<TokenExtended>();
		
		// create an empty Annotation with just the given text
        Annotation document = new Annotation(answerText.toString());

        // run all Annotators on this text
        this.pipeline.annotate(document);

        // Iterate over all of the sentences
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        int index = 0;
        for(CoreMap sentence: sentences) {
            // Iterate over all tokens in a sentence
            for (CoreLabel fullToken: sentence.get(TokensAnnotation.class)) {
            	String token = fullToken.get(TextAnnotation.class);
                String partOfSpeech = fullToken.get(PartOfSpeechAnnotation.class);
                String lemma = fullToken.get(LemmaAnnotation.class);
                int begin = fullToken.get(CharacterOffsetBeginAnnotation.class);
		        int end = fullToken.get(CharacterOffsetEndAnnotation.class);
		        TokenSpan tokenSpan = new TokenSpan(begin,end,index);
//		        System.out.println(token+" "+tokenSpan.toString()+" "+answerText.length()+" "+document.size());
		        // stupid exception for stupid smiley-face
		        if (token.equals(":-RRB-")) {
	                	token = ":)";
	                	partOfSpeech = ".";
	                	lemma = ":)";
                }
		        if (token.equals("-RRB-")) {
	                	token = ")";
	                	partOfSpeech = ".";
	                	lemma = ")";
		        }
                TokenExtended te = new TokenExtended(token,partOfSpeech,lemma,tokenSpan);
                allTokenInfo.add(te);
                index++;
            }
        }
        return allTokenInfo;
    }
	
    /**
	 * Sentence index is the index of the sentence in the overall answer. Word index
	 * is the index of the individual token within that sentence.
	 * @author Adam Goodkind
	 *
	 */
    public static class IndexInfo {
    	public final int overallIndex;
    	public final int sentenceIndex;
    	public final int wordIndex;
    	public final int sentenceLength;
    	
    	public IndexInfo(int overallIndex, int sentenceIndex, int wordIndex, int sentenceLength) {
    		this.overallIndex = overallIndex;
    		this.sentenceIndex = sentenceIndex;
    		this.wordIndex = wordIndex;
    		this.sentenceLength = sentenceLength;
    	}
    	
    	public String toString() {
    		String retStr = String.format("Overall: %d, Sentence: %d, Word: %d, SentLen: %d"
    				, this.overallIndex, this.sentenceIndex, this.wordIndex, this.sentenceLength);
    		return retStr;
    	}
    }
    
	public static class TokenSpan {
		public final int begin;
		public final int end;
		public final int index;
		
		public TokenSpan(int begin, int end, int index) {
			this.begin = begin;
			this.end = end;
			this.index = index;
		}
		
		public int getBegin() {
			return begin;
		}

		public int getEnd() {
			return end;
		}
		
		public String toString() {
			return "("+begin+","+end+") ";
		}
	}
	
	/**
	 * Class to track a string of visible keystrokes as well as
	 * the visible keystroke's index in the original raw file. The raw
	 * index will be used to add all -- visible and non-visible -- keystrokes
	 * to an Extended Token
	 * @author Adam Goodkind
	 *
	 */
	public static class visibleKeystrokeInfo {
//		protected 
	}
}
