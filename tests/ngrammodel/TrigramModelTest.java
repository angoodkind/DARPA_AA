package ngrammodel;

import static org.junit.Assert.*;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;

import events.EventList;
import events.GenericEvent;
import extractors.data.Answer;
import keystroke.KeyStroke;
import ngrammodel.Bigram;
import ngrammodel.ModelGeneratorException;
import ngrammodel.Trigram;
import ngrammodel.TrigramModel;

public class TrigramModelTest {
	
	@Test
	public void setUnigramCountsTest() throws ModelGeneratorException {
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
	    
/*---------------------------------------*/
	    ArrayList<String> unigramsList = new ArrayList<String>();
	    for (KeyStroke k : answer.getKeyStrokeList()) {
	    		if (k.isKeyPress())
	    			unigramsList.add(KeyStroke.vkCodetoString(k.getKeyCode()));
	    }
	    
	    TrigramModel model = new TrigramModel();
	    model.setUnigrams(unigramsList);
	    model.setUnigramCounts();
	    
	    assertEquals(1,model.getUnigramCount("Period"));
    		assertEquals(3,model.getUnigramCount("D"));
    		assertEquals(5,model.getUnigramCount("E"));
    		assertEquals(1,model.getUnigramCount("C"));
    		assertEquals(2,model.getUnigramCount("L"));
    		assertEquals(4,model.getUnigramCount("O"));
    		assertEquals(3,model.getUnigramCount("H"));
    		assertEquals(2,model.getUnigramCount("Backspace"));
    		assertEquals(1,model.getUnigramCount("K"));
    		assertEquals(1,model.getUnigramCount("U"));
    		assertEquals(1,model.getUnigramCount("T"));
    		assertEquals(1,model.getUnigramCount("W"));
    		assertEquals(1,model.getUnigramCount("P"));
    		assertEquals(1,model.getUnigramCount("S"));
    		assertEquals(3,model.getUnigramCount("R"));
    		assertEquals(5,model.getUnigramCount("Spacebar"));
	}

	@Test
	public void settrigramCountsTest() throws ModelGeneratorException {
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
	    
/*---------------------------------------*/
	    ArrayList<String> unigramsList = new ArrayList<String>();
	    for (KeyStroke k : answer.getKeyStrokeList()) {
	    		if (k.isKeyPress())
	    			unigramsList.add(KeyStroke.vkCodetoString(k.getKeyCode()));
	    }
	    
	    TrigramModel model = new TrigramModel();
	    model.setUnigrams(unigramsList);
	    model.setUnigramCounts();
	    model.setBigramTrigramCounts();
	    assertEquals(1,(int)model.getBigramCountsMap().get(new Bigram("E","D")));
	    assertEquals(1,(int)model.getBigramCountsMap().get(new Bigram("E","C")));
	    assertEquals(1,(int)model.getBigramCountsMap().get(new Bigram("P","Spacebar")));
	    assertEquals(1,(int)model.getBigramCountsMap().get(new Bigram("Spacebar","R")));
	    assertEquals(2,(int)model.getBigramCountsMap().get(new Bigram("H","E")));
	    assertEquals(1,(int)model.getBigramCountsMap().get(new Bigram("E","H")));
	    assertEquals(1,(int)model.getBigramCountsMap().get(new Bigram("Spacebar","L")));
	    assertEquals(2,(int)model.getBigramCountsMap().get(new Bigram("D","Spacebar")));
	    assertEquals(2,(int)model.getBigramCountsMap().get(new Bigram("E","Spacebar")));
	    assertEquals(1,(int)model.getBigramCountsMap().get(new Bigram("T","E")));
	    assertEquals(1,(int)model.getBigramCountsMap().get(new Bigram("O","K")));
	    assertEquals(1,(int)model.getBigramCountsMap().get(new Bigram("L","O")));
	    assertEquals(1,(int)model.getBigramCountsMap().get(new Bigram("S","H")));
	    assertEquals(1,(int)model.getBigramCountsMap().get(new Bigram("O","O")));
	    assertEquals(1,(int)model.getBigramCountsMap().get(new Bigram("R","L")));
	    assertEquals(1,(int)model.getBigramCountsMap().get(new Bigram("K","E")));
	    assertEquals(1,(int)model.getBigramCountsMap().get(new Bigram("H","Backspace")));
	    assertEquals(1,(int)model.getBigramCountsMap().get(new Bigram("Backspace","H")));
	    assertEquals(1,(int)model.getBigramCountsMap().get(new Bigram("L","D")));
	    assertEquals(1,(int)model.getBigramCountsMap().get(new Bigram("C","O")));
	    assertEquals(1,(int)model.getBigramCountsMap().get(new Bigram("R","D")));
	    assertEquals(1,(int)model.getBigramCountsMap().get(new Bigram("R","E")));
	    assertEquals(2,(int)model.getBigramCountsMap().get(new Bigram("O","R")));
	    assertEquals(1,(int)model.getBigramCountsMap().get(new Bigram("W","O")));
	    assertEquals(1,(int)model.getBigramCountsMap().get(new Bigram("U","P")));
	    assertEquals(1,(int)model.getBigramCountsMap().get(new Bigram("Spacebar","U")));
	    assertEquals(1,(int)model.getBigramCountsMap().get(new Bigram("Spacebar","T")));
	    assertEquals(1,(int)model.getBigramCountsMap().get(new Bigram("Backspace","Backspace")));
	    assertEquals(1,(int)model.getBigramCountsMap().get(new Bigram("D","Period")));
	    assertEquals(1,(int)model.getBigramCountsMap().get(new Bigram("Spacebar","W")));
	    
	    assertEquals(2,model.getTrigramCount(new Trigram("H","E","Spacebar")));
	    assertEquals(1,model.getTrigramCount(new Trigram("D","Spacebar","R")));
	    assertEquals(1,model.getTrigramCount(new Trigram("Spacebar","R","E")));
	    assertEquals(1,model.getTrigramCount(new Trigram("L","D","Spacebar")));
	    assertEquals(1,model.getTrigramCount(new Trigram("D","Spacebar","U")));
	    assertEquals(1,model.getTrigramCount(new Trigram("E","Spacebar","L")));
	    assertEquals(1,model.getTrigramCount(new Trigram("Spacebar","T","E")));
	    assertEquals(1,model.getTrigramCount(new Trigram("Spacebar","L","O")));
	    assertEquals(1,model.getTrigramCount(new Trigram("E","Spacebar","W")));
	    assertEquals(1,model.getTrigramCount(new Trigram("E","D","Spacebar")));
	    assertEquals(1,model.getTrigramCount(new Trigram("P","Spacebar","T")));
	    assertEquals(1,model.getTrigramCount(new Trigram("Spacebar","W","O")));
	    assertEquals(1,model.getTrigramCount(new Trigram("U","P","Spacebar")));
	    assertEquals(1,model.getTrigramCount(new Trigram("Spacebar","U","P")));
	    assertEquals(1,model.getTrigramCount(new Trigram("O","K","E")));
	    assertEquals(1,model.getTrigramCount(new Trigram("R","E","C")));
	    assertEquals(1,model.getTrigramCount(new Trigram("E","H","Backspace")));
	    assertEquals(1,model.getTrigramCount(new Trigram("Backspace","H","E")));
	    assertEquals(1,model.getTrigramCount(new Trigram("K","E","D")));
	    assertEquals(1,model.getTrigramCount(new Trigram("E","C","O")));
	    assertEquals(1,model.getTrigramCount(new Trigram("H","Backspace","Backspace")));
	    assertEquals(1,model.getTrigramCount(new Trigram("Backspace","Backspace","H")));
	    assertEquals(1,model.getTrigramCount(new Trigram("O","R","L")));
	    assertEquals(1,model.getTrigramCount(new Trigram("L","O","O")));
	    assertEquals(1,model.getTrigramCount(new Trigram("O","O","K")));
	    assertEquals(1,model.getTrigramCount(new Trigram("R","D","Period")));
	    assertEquals(1,model.getTrigramCount(new Trigram("O","R","D")));
	    assertEquals(1,model.getTrigramCount(new Trigram("C","O","R")));
	    assertEquals(1,model.getTrigramCount(new Trigram("R","L","D")));
	    assertEquals(1,model.getTrigramCount(new Trigram("T","E","H")));
	    assertEquals(1,model.getTrigramCount(new Trigram("S","H","E")));
	    assertEquals(1,model.getTrigramCount(new Trigram("W","O","R")));
	}
	
	@Test
	public void setTrigramProbabilitiesTest() throws ModelGeneratorException {
	    EventList<KeyStroke> ks = new EventList<>();
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 0, KeyEvent.VK_A, 'a', 0));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 1, KeyEvent.VK_A, 'a', 1));
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 2, KeyEvent.VK_B, 'b', 1));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 3, KeyEvent.VK_B, 'b', 2));
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 4, KeyEvent.VK_C, 'c', 2));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 5, KeyEvent.VK_C, 'c', 3));
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 6, KeyEvent.VK_A, 'a', 3));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 7, KeyEvent.VK_A, 'a', 4));
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 8, KeyEvent.VK_B, 'b', 4));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 9, KeyEvent.VK_B, 'b', 5));
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 10, KeyEvent.VK_D, 'd', 5));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 11, KeyEvent.VK_D, 'd', 6));
	    Collection<GenericEvent> ge = new EventList<>();
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 0, KeyEvent.VK_A, 'a', 0));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 1, KeyEvent.VK_A, 'a', 1));
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 2, KeyEvent.VK_B, 'b', 1));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 3, KeyEvent.VK_B, 'b', 2));
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 4, KeyEvent.VK_C, 'c', 2));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 5, KeyEvent.VK_C, 'c', 3));
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 6, KeyEvent.VK_A, 'a', 3));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 7, KeyEvent.VK_A, 'a', 4));
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 8, KeyEvent.VK_B, 'b', 4));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 9, KeyEvent.VK_B, 'b', 5));
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 10, KeyEvent.VK_D, 'd', 5));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 11, KeyEvent.VK_D, 'd', 6));
	    
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
	    
/*---------------------------------------*/
	    ArrayList<String> unigramsList = new ArrayList<String>();
	    unigramsList.add(Trigram.START);
	    for (KeyStroke k : answer.getKeyStrokeList()) {
	    		if (k.isKeyPress())
	    			unigramsList.add(KeyStroke.vkCodetoString(k.getKeyCode()));
	    }
	    unigramsList.add(Trigram.STOP);
	    
	    TrigramModel model = new TrigramModel();
	    model.setUnigrams(unigramsList);
	    model.setUnigramCounts();
	    model.setVocabularySize();
	    model.setBigramTrigramCounts();
	    model.setTrigramProbabilities();
	    
	    assertEquals(1,(double)model.getTrigramProbability(new Trigram("_START_","A","B")),0.001);
	    assertEquals(0.545454545454545,(double)model.getTrigramProbability(new Trigram("A","B","C")),0.001);
	    assertEquals(1,(double)model.getTrigramProbability(new Trigram("B","C","A")),0.001);
	    assertEquals(1,(double)model.getTrigramProbability(new Trigram("C","A","B")),0.001);
	    assertEquals(0.545454545454545,(double)model.getTrigramProbability(new Trigram("A","B","_STOP_")),0.001);

	}
	

	
	@Test
	public void setVocabularySizeTest() throws ModelGeneratorException {
		EventList<KeyStroke> ks = new EventList<>();
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 0, KeyEvent.VK_A, 'a', 0));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 1, KeyEvent.VK_A, 'a', 1));
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 2, KeyEvent.VK_B, 'b', 1));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 3, KeyEvent.VK_B, 'b', 2));
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 4, KeyEvent.VK_C, 'c', 2));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 5, KeyEvent.VK_C, 'c', 3));
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 6, KeyEvent.VK_A, 'a', 3));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 7, KeyEvent.VK_A, 'a', 4));
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 8, KeyEvent.VK_B, 'b', 4));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 9, KeyEvent.VK_B, 'b', 5));
	    Collection<GenericEvent> ge = new EventList<>();
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 0, KeyEvent.VK_A, 'a', 0));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 1, KeyEvent.VK_A, 'a', 1));
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 2, KeyEvent.VK_B, 'b', 1));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 3, KeyEvent.VK_B, 'b', 2));
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 4, KeyEvent.VK_C, 'c', 2));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 5, KeyEvent.VK_C, 'c', 3));
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 6, KeyEvent.VK_A, 'a', 3));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 7, KeyEvent.VK_A, 'a', 4));
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 8, KeyEvent.VK_B, 'b', 4));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 9, KeyEvent.VK_B, 'b', 5));
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
	    
/*---------------------------------------*/
	    ArrayList<String> unigramsList = new ArrayList<String>();
//	    unigramsList.add(trigram.START);
	    for (KeyStroke k : answer.getKeyStrokeList()) {
	    		if (k.isKeyPress())
	    			unigramsList.add(KeyStroke.vkCodetoString(k.getKeyCode()));
	    }
//	    unigramsList.add(trigram.STOP);
	    
	    TrigramModel model = new TrigramModel();
	    model.setUnigrams(unigramsList);
	    model.setVocabularySize();
	    assertEquals(3,model.getVocabularySize());
	}
}
