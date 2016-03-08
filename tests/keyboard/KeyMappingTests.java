package keyboard;

import static org.junit.Assert.*;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

import events.EventList;
import keystroke.KeyStroke;

public class KeyMappingTests {

	@Test
	public void KeyFingerMappingTest() {
	    EventList<KeyStroke> ks = new EventList<>();
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 0, KeyEvent.VK_A, 'a', 0));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 1, KeyEvent.VK_A, 'a', 1));

	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 1, KeyEvent.VK_B, 'b', 1));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 2, KeyEvent.VK_B, 'b', 2));

	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 3, KeyEvent.VK_SHIFT, (char)0, 2));
	    
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 4, KeyEvent.VK_C, 'C', 2));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 5, KeyEvent.VK_C, 'C', 3));

	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 6, KeyEvent.VK_SHIFT, (char)0, 3));
	    
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 7, KeyEvent.VK_D, 'd', 3));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 8, KeyEvent.VK_D, 'd', 4));

	    ArrayList<String> expectedFingers = new ArrayList<String>(Arrays.asList("5","5","2","2","5","3","3","5","4","4"));
	    ArrayList<String> actualFingers = new ArrayList<String>();
	    for (KeyStroke k : ks)
	    	actualFingers.add(k.getFinger());
	    assertEquals(expectedFingers,actualFingers);
	}

	@Test
	public void KeyHandMappingTest() {
	    EventList<KeyStroke> ks = new EventList<>();
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 0, KeyEvent.VK_A, 'a', 0));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 1, KeyEvent.VK_A, 'a', 1));

	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 1, KeyEvent.VK_B, 'b', 1));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 2, KeyEvent.VK_B, 'b', 2));

	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 3, KeyEvent.VK_SHIFT, (char)0, 2));
	    
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 4, KeyEvent.VK_C, 'C', 2));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 5, KeyEvent.VK_C, 'C', 3));

	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 6, KeyEvent.VK_SHIFT, (char)0, 3));
	    
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 7, KeyEvent.VK_M, 'M', 3));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 8, KeyEvent.VK_M, 'M', 4));

	    ArrayList<String> expectedHand = new ArrayList<String>(Arrays.asList("L","L","L","L",null,"L","L",null,"R","R"));
	    ArrayList<String> actualHand = new ArrayList<String>();
	    for (KeyStroke k : ks)
	    	actualHand.add(k.getHand());
	    assertEquals(expectedHand,actualHand);
	}

	@Test
	public void KeyRowMappingTest() {
	    EventList<KeyStroke> ks = new EventList<>();
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 0, KeyEvent.VK_A, 'a', 0));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 1, KeyEvent.VK_A, 'a', 1));

	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 1, KeyEvent.VK_B, 'b', 1));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 2, KeyEvent.VK_B, 'b', 2));

	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 3, KeyEvent.VK_SHIFT, (char)0, 2));
	    
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 4, KeyEvent.VK_T, 't', 2));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 5, KeyEvent.VK_T, 't', 3));

	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 6, KeyEvent.VK_SHIFT, (char)0, 3));
	    
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 7, KeyEvent.VK_1, '1', 3));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 8, KeyEvent.VK_1, '1', 4));

	    ArrayList<String> expectedFingers = new ArrayList<String>
	    								(Arrays.asList("hme","hme","btm","btm","btm","top","top","btm","num","num"));
	    ArrayList<String> actualFingers = new ArrayList<String>();
	    for (KeyStroke k : ks) {
	    	actualFingers.add(k.getRow());
	    	System.out.println(k.getKeyChar()+" "+k.getWhen());
	    }
	    assertEquals(expectedFingers,actualFingers);
	}

	
}
