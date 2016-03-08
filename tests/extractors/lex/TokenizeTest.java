/**
 * 
 */
package extractors.lex;

import static org.junit.Assert.*;

import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.Span;

import org.junit.Test;

import events.EventList;
import events.GenericEvent;
import extractors.data.Answer;
import extractors.lexical.Tokenize;
import keystroke.KeyStroke;

/**
 * @author agoodkind
 *
 */
public class TokenizeTest {

	@Test
	public void testRunTokenizer() throws InvalidFormatException, IOException {
		
		String rawStr = "The last book i read was The Hunger Games series. I also went to see it in the movies.";
		String[] tokens = {"The", "last", "book", "i", "read", "was", "The", "Hunger", "Games", "series", ".", 
				"I", "also", "went", "to", "see", "it", "in", "the", "movies", "."};
		
		InputStream isToken = new FileInputStream("en-token.bin");
		TokenizerModel tModel = new TokenizerModel(isToken);
		Tokenizer tokenizer = new TokenizerME(tModel);
		String[] unigramTokens = tokenizer.tokenize(rawStr);
		isToken.close();
		
		assertArrayEquals(tokens,unigramTokens);
		
	}
	
	@Test
	// retrieves the Spans (OpenNLP class) of the token; returns the starting index of each token
	public void testGetStartIndex() throws InvalidFormatException, IOException {
		
		String rawStr = "The last book i read was The Hunger Games series. I also went to see it in the movies.";
		int[] starts = {0, 4, 9, 14, 16, 21, 25, 29, 36, 42, 48, 50, 52, 57, 62, 65, 69, 72, 75, 79, 85};
		
		ArrayList<Integer> startIndexes = new ArrayList<Integer>();
		Span[] tokenSpans;
		
		InputStream isToken = new FileInputStream("en-token.bin");
		TokenizerModel tModel = new TokenizerModel(isToken);
		Tokenizer tokenizer = new TokenizerME(tModel);
		tokenSpans = tokenizer.tokenizePos(rawStr);
		isToken.close();
		
		for (Span s : tokenSpans) {
			startIndexes.add(s.getStart());
		}
		int i = 0;
		int[] s = new int[startIndexes.size()];
		for (Integer integer : startIndexes)
			s[i++] = integer;
		
		assertArrayEquals(starts,s);
	}

	@Test
	public void testGetEndIndex() throws InvalidFormatException, IOException {
		
		String rawStr = "The last book i read was The Hunger Games series. I also went to see it in the movies.";
		int[] ends = {3, 8, 13, 15, 20, 24, 28, 35, 41, 48, 49, 51, 56, 61, 64, 68, 71, 74, 78, 85, 86};
		
		ArrayList<Integer> endIndexes = new ArrayList<Integer>();
		Span[] tokenSpans;
		
		InputStream isToken = new FileInputStream("en-token.bin");
		TokenizerModel tModel = new TokenizerModel(isToken);
		Tokenizer tokenizer = new TokenizerME(tModel);
		tokenSpans = tokenizer.tokenizePos(rawStr);
		isToken.close();
		
		for (Span s : tokenSpans) {
			endIndexes.add(s.getEnd());
		}
		int i = 0;
		int[] e = new int[endIndexes.size()];
		for (Integer integer : endIndexes)
			e[i++] = integer;
		
		assertArrayEquals(ends,e);
	}
	
	@Test
	public void testCreateWordSet() throws InvalidFormatException, IOException {
		
		String rawStr = "I I am am not not sure sure sure sure.";
		Set<String> testSet = new HashSet<String>();
		Collections.addAll(testSet, "I","am","not","sure",".");
		Tokenize token = new Tokenize();
		
		String[] tokens = token.runTokenizer(rawStr);
		Set<String> wordSet = new HashSet<String>();
		
		for (String t : tokens) 
			wordSet.add(t);
		
		for (String s : wordSet) 
			if (!testSet.contains(s))
				fail("TestSet does not contain \""+s+"\"");
	}
	
	@Test
	public void TypeTokenRatioTest() {
		EventList<KeyStroke> ks = new EventList<>();
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 0, 83, 's', 0));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 10, 83, 's', 1));
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 20, 72, 'h', 1));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 30, 72, 'h', 2));
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 40, 69, 'e', 2));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 50, 69, 'e', 3));
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 60, 32, (char)32, 3));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 70, 32, (char)32, 4));
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 80, 76, 'l', 4));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 90, 76, 'l', 5));
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 100, 79, 'o', 5));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 110, 79, 'o', 6));
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 120, 79, 'o', 6));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 130, 79, 'o', 7));
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 140, 75, 'k', 7));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 150, 75, 'k', 8));
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 160, 69, 'e', 8));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 170, 69, 'e', 9));
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 180, 68, 'd', 9));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 190, 68, 'd', 10));
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 200, 32, (char)32, 10));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 210, 32, (char)32, 11));
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 215, 85, 'u', 11));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 225, 85, 'u', 12));
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 235, 80, 'p', 12));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 245, 80, 'p', 13));
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 255, 32, (char)32, 13));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 265, 32, (char)32, 14));
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 275, 84, 't', 14));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 285, 84, 't', 15));
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 295, 69, 'e', 15));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 305, 69, 'e', 16));
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 315, 72, 'h', 16));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 325, 72, 'h', 17));
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 335, 8, (char)8, 17));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 345, 8, (char)8, 16));
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 355, 8, (char)8, 16));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 365, 8, (char)8, 15));
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 375, 72, 'h', 15));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 385, 72, 'h', 16));
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 395, 69, 'e', 16));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 405, 69, 'e', 17));
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 415, 32, (char)32, 17));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 425, 32, (char)32, 18));
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 435, 87, 'w', 18));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 445, 87, 'w', 19));
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 455, 79, 'o', 19));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 465, 79, 'o', 20));
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 475, 82, 'r', 20));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 485, 82, 'r', 21));
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 495, 76, 'l', 21));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 505, 76, 'l', 22));
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 515, 68, 'd', 22));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 525, 68, 'd', 23));
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 535, 32, (char)32, 23));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 545, 32, (char)32, 24));
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 550, 82, 'r', 24));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 560, 82, 'r', 25));
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 570, 69, 'e', 25));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 580, 69, 'e', 26));
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 590, 67, 'c', 26));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 600, 67, 'c', 27));
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 610, 79, 'o', 27));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 620, 79, 'o', 28));
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 630, 82, 'r', 28));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 640, 82, 'r', 29));
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 650, 68, 'd', 29));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 660, 68, 'd', 30));
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 670, 46, (char)46, 30));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 680, 46, (char)46, 31));
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 690, 46, (char)46, 31));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 700, 46, (char)46, 32));
	    
	    Collection<GenericEvent> ge = new EventList<>();
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 0, 83, 's', 0));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 10, 83, 's', 1));
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 20, 72, 'h', 1));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 30, 72, 'h', 2));
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 40, 69, 'e', 2));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 50, 69, 'e', 3));
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 60, 32, (char)32, 3));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 70, 32, (char)32, 4));
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 80, 76, 'l', 4));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 90, 76, 'l', 5));
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 100, 79, 'o', 5));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 110, 79, 'o', 6));
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 120, 79, 'o', 6));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 130, 79, 'o', 7));
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 140, 75, 'k', 7));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 150, 75, 'k', 8));
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 160, 69, 'e', 8));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 170, 69, 'e', 9));
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 180, 68, 'd', 9));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 190, 68, 'd', 10));
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 200, 32, (char)32, 10));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 210, 32, (char)32, 11));
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 215, 85, 'u', 11));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 225, 85, 'u', 12));
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 235, 80, 'p', 12));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 245, 80, 'p', 13));
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 255, 32, (char)32, 13));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 265, 32, (char)32, 14));
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 275, 84, 't', 14));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 285, 84, 't', 15));
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 295, 69, 'e', 15));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 305, 69, 'e', 16));
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 315, 72, 'h', 16));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 325, 72, 'h', 17));
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 335, 8, (char)8, 17));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 345, 8, (char)8, 16));
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 355, 8, (char)8, 16));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 365, 8, (char)8, 15));
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 375, 72, 'h', 15));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 385, 72, 'h', 16));
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 395, 69, 'e', 16));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 405, 69, 'e', 17));
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 415, 32, (char)32, 17));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 425, 32, (char)32, 18));
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 435, 87, 'w', 18));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 445, 87, 'w', 19));
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 455, 79, 'o', 19));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 465, 79, 'o', 20));
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 475, 82, 'r', 20));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 485, 82, 'r', 21));
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 495, 76, 'l', 21));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 505, 76, 'l', 22));
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 515, 68, 'd', 22));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 525, 68, 'd', 23));
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 535, 32, (char)32, 23));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 545, 32, (char)32, 24));
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 550, 82, 'r', 24));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 560, 82, 'r', 25));
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 570, 69, 'e', 25));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 580, 69, 'e', 26));
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 590, 67, 'c', 26));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 600, 67, 'c', 27));
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 610, 79, 'o', 27));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 620, 79, 'o', 28));
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 630, 82, 'r', 28));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 640, 82, 'r', 29));
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 650, 68, 'd', 29));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 660, 68, 'd', 30));
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 670, 46, (char)46, 30));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 680, 46, (char)46, 31));
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 690, 46, (char)46, 31));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 700, 46, (char)46, 32));
	    
	    String charStream = ks.toVisibleTextString();
	    String finalText = KeyStroke.keyStrokesToFinalTextEventList(ks).toVisibleTextString();
	    StringBuilder keyStrokeListBuilder = new StringBuilder();
	    
	    for (KeyStroke k : ks) {
	    	StringBuilder keyStrokeBuilder = new StringBuilder();
	    	keyStrokeBuilder.append(" "+(k.isKeyRelease()?1:0)+":");
	    	keyStrokeBuilder.append(Integer.toHexString(k.getKeyCode())+":");
	    	keyStrokeBuilder.append((int)k.getKeyChar()+":");
	    	keyStrokeBuilder.append(k.getWhen()+":");
	    	keyStrokeBuilder.append(k.getCursorPosition());
	    	keyStrokeListBuilder.append(keyStrokeBuilder.toString());
	    }
	    
	    Answer answer = new Answer(charStream,finalText,keyStrokeListBuilder.toString(),1,1,1,1,"O",ge);
	    Tokenize tokenize = new Tokenize(finalText);
	    double ttr = Double.NaN;
	    double mattr = Double.NaN;
	    try {
			ttr = tokenize.calculateTTR(finalText);
			mattr = tokenize.calculateMATTR(finalText, 2);
		} catch (IOException e) {e.printStackTrace();}
	    assertEquals(0.875,ttr,0.001);
	    assertEquals(0.9285714285714286,mattr,0.001);
	}
}
