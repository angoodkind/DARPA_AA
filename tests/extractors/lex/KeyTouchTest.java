package extractors.lex;

import static org.junit.Assert.*;

import java.awt.event.KeyEvent;
import java.util.*;

import org.junit.Test;

import events.EventList;
import events.GenericEvent;
import extractors.data.Answer;
import keystroke.KeyStroke;
import keytouch.KeyTouch;

public class KeyTouchTest {

	@Test
	public void testParseSesstionToKeyTouchesCorrectlyMarksSlurs() {
		// Create an Answer object
		EventList<KeyStroke> ks = new EventList<>();
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 0, KeyEvent.VK_A, 'a', 0));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 1, KeyEvent.VK_A, 'a', 1));

	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 1, KeyEvent.VK_B, 'b', 1));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 2, KeyEvent.VK_B, 'b', 2));

	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 3, KeyEvent.VK_C, 'c', 2));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 4, KeyEvent.VK_C, 'c', 3));

	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 5, KeyEvent.VK_D, 'd', 3));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 6, KeyEvent.VK_D, 'd', 4));

	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 7, KeyEvent.VK_SHIFT, ' ', 4));
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 8, KeyEvent.VK_LEFT, ' ', 4));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 9, KeyEvent.VK_LEFT, ' ', 3));
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 10, KeyEvent.VK_LEFT, ' ', 3));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 11, KeyEvent.VK_LEFT, ' ', 2));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 12, KeyEvent.VK_SHIFT, ' ', 2));

	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 13, KeyEvent.VK_BACK_SPACE, ' ', 2));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 14, KeyEvent.VK_BACK_SPACE, ' ', 2));
		
		Collection<GenericEvent> ge = new EventList<>();
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 0, KeyEvent.VK_A, 'a', 0));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 1, KeyEvent.VK_A, 'a', 1));

	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 1, KeyEvent.VK_B, 'b', 1));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 2, KeyEvent.VK_B, 'b', 2));

	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 3, KeyEvent.VK_C, 'c', 2));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 4, KeyEvent.VK_C, 'c', 3));

	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 5, KeyEvent.VK_D, 'd', 3));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 6, KeyEvent.VK_D, 'd', 4));

	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 7, KeyEvent.VK_SHIFT, ' ', 4));
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 8, KeyEvent.VK_LEFT, ' ', 4));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 9, KeyEvent.VK_LEFT, ' ', 3));
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 10, KeyEvent.VK_LEFT, ' ', 3));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 11, KeyEvent.VK_LEFT, ' ', 2));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 12, KeyEvent.VK_SHIFT, ' ', 2));

	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 13, KeyEvent.VK_BACK_SPACE, ' ', 2));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 14, KeyEvent.VK_BACK_SPACE, ' ', 2));

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
	    LinkedList<KeyTouch> keyTouchList = KeyTouch.parseSessionToKeyTouches(answer.getKeyStrokes());
	   
	    assertEquals("B,1,1,1",keyTouchList.get(1).toString());
	    assertEquals("C,2,1,2",keyTouchList.get(2).toString());
	    assertEquals("D,2,1,3",keyTouchList.get(3).toString());
	    assertEquals("Shift,2,5,4",keyTouchList.get(4).toString());
	    assertEquals("LeftArrow,1,1,4",keyTouchList.get(5).toString());
	    assertEquals("LeftArrow,2,1,3",keyTouchList.get(6).toString());
	    assertEquals("Backspace,3,1,2",keyTouchList.get(7).toString());
	}

}
