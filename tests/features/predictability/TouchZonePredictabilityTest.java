package features.predictability;

import static org.junit.Assert.*;

import java.awt.event.KeyEvent;
import java.util.Collection;

import org.junit.Test;

import events.EventList;
import events.GenericEvent;
import extractors.data.Answer;
import extractors.data.DataNode;
import features.predictability.TouchZonePredictability;
import keystroke.KeyStroke;

public class TouchZonePredictabilityTest {

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
	    
	    Collection<GenericEvent> ge = new EventList<>();
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 0, 83, 's', 0));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 10, 83, 's', 1));
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 20, 72, 'h', 1));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 30, 72, 'h', 2));
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 40, 69, 'e', 2));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 50, 69, 'e', 3));
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 60, 32, (char)32, 3));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 70, 32, (char)32, 4));
	    	    
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
	    
	    /*---------------------------------------*/
	    
	    TouchZonePredictability tzp = new TouchZonePredictability();
	    tzp.extract(dn);
	}

}
