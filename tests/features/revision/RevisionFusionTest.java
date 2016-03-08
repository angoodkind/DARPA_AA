package features.revision;

import static org.junit.Assert.*;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import org.junit.Test;

import events.EventList;
import events.GenericEvent;
import extractors.data.Answer;
import extractors.data.DataNode;
import features.revision.Revision;
import features.revision.RevisionNgramFusion;
import features.revision.Revision.KeystrokeRevision;
import keystroke.KeyStroke;
import keytouch.KeyTouch;

public class RevisionFusionTest {

	@Test
	public void test() {
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
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 256, 32, (char)32, 13));
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
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 256, 32, (char)32, 13));
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
	    DataNode dn = new DataNode(1);
	    dn.add(answer);
	    
/**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~**/
	    
	    LinkedList<KeyTouch> ktList = KeyTouch.parseSessionToKeyTouches(answer.getKeyStrokes());
	    ArrayList<KeystrokeRevision> krList = Revision.extendKeystrokesToRevisions(ktList);
	    
	    RevisionNgramFusion module = new RevisionNgramFusion();
	    module.extract(dn);
	}

}
