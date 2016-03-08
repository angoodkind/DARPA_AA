package extractors.nyit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.event.KeyEvent;

import org.junit.Before;
import org.junit.Test;

import events.EventList;
import keystroke.KeyStroke;
import keystroke.KeyStrokeIterator;

public class TestKeyStrokeIterator {

	EventList<KeyStroke> events = new EventList<KeyStroke>();
	
	@Before
	public void setUp() throws Exception {
	    events.add(new KeyStroke(KeyEvent.KEY_PRESSED, 0, KeyEvent.VK_A, 'a', 0));			//0
	    events.add(new KeyStroke(KeyEvent.KEY_RELEASED, 1, KeyEvent.VK_A, 'a', 1));			//1

	    events.add(new KeyStroke(KeyEvent.KEY_PRESSED, 1, KeyEvent.VK_B, 'b', 1));			//2
	    events.add(new KeyStroke(KeyEvent.KEY_RELEASED, 2, KeyEvent.VK_B, 'b', 2));			//3

	    events.add(new KeyStroke(KeyEvent.KEY_PRESSED, 3, KeyEvent.VK_C, 'c', 2));			//4
	    events.add(new KeyStroke(KeyEvent.KEY_RELEASED, 4, KeyEvent.VK_C, 'c', 3));			//5

	    events.add(new KeyStroke(KeyEvent.KEY_PRESSED, 5, KeyEvent.VK_D, 'd', 3));			//6
	    events.add(new KeyStroke(KeyEvent.KEY_RELEASED, 6, KeyEvent.VK_D, 'd', 4));			//7

	    events.add(new KeyStroke(KeyEvent.KEY_PRESSED, 7, KeyEvent.VK_SHIFT, (char)0, 4));	//8
	    events.add(new KeyStroke(KeyEvent.KEY_PRESSED, 8, KeyEvent.VK_LEFT, (char)0, 4));	//9
	    events.add(new KeyStroke(KeyEvent.KEY_RELEASED, 9, KeyEvent.VK_LEFT, (char)0, 3));	//10
	    events.add(new KeyStroke(KeyEvent.KEY_PRESSED, 10, KeyEvent.VK_LEFT, (char)0, 3));	//11
	    events.add(new KeyStroke(KeyEvent.KEY_RELEASED, 11, KeyEvent.VK_LEFT, (char)0, 2));	//12
	    events.add(new KeyStroke(KeyEvent.KEY_RELEASED, 12, KeyEvent.VK_SHIFT, (char)0, 2));//13

	    events.add(new KeyStroke(KeyEvent.KEY_PRESSED, 13, KeyEvent.VK_BACK_SPACE, (char)8, 2));//14
	    events.add(new KeyStroke(KeyEvent.KEY_RELEASED, 14, KeyEvent.VK_BACK_SPACE, (char)8, 2));//15
	}
	
	@Test
	public void testNext() {
		KeyStrokeIterator ei = new KeyStrokeIterator(events);
		assertEquals(ei.next().getWhen(), 0);
		assertEquals(ei.next().getWhen(), 1);
		assertEquals(ei.next().getWhen(), 1);
		assertEquals(ei.next().getWhen(), 2);
		assertEquals(ei.next().getWhen(), 3);
		assertEquals(ei.next().getWhen(), 4);
		assertEquals(ei.next().getWhen(), 5);
		assertEquals(ei.next().getWhen(), 6);
		assertEquals(ei.next().getWhen(), 7);
		assertEquals(ei.next().getWhen(), 8);
		assertEquals(ei.next().getWhen(), 9);
		assertEquals(ei.next().getWhen(), 10);
		assertEquals(ei.next().getWhen(), 11);
		assertEquals(ei.next().getWhen(), 12);
		assertEquals(ei.next().getWhen(), 13);
		assertEquals(ei.next().getWhen(), 14);
	}
	
	@Test
	public void testHasNext() {
		KeyStrokeIterator ei = new KeyStrokeIterator(events);
		assertTrue(ei.hasNext());
		for (int i=0; events.size() > i; i++)
			ei.next();
		assertFalse(ei.hasNext());
	}

	@Test
	public void testHasNextKeyPress() {
		KeyStrokeIterator ei = new KeyStrokeIterator(events);
		
		assertTrue(ei.hasNextKeyPress());
		ei.next(); //0
		assertTrue(ei.hasNextKeyPress());
		ei.next(); //1
		ei.next(); //2
		ei.next(); //3
		ei.next(); //4 
		ei.next(); //5 
		ei.next(); //6
		ei.next(); //7
		ei.next(); //8
		ei.next(); //9
		ei.next(); //10
		ei.next(); //11
		ei.next(); //12
		ei.next(); //13
		ei.next(); //14
		assertFalse(ei.hasNextKeyPress());
	}

	@Test
	public void testNextKeyPress() {
		KeyStrokeIterator ei = new KeyStrokeIterator(events);
		
		assertEquals(ei.nextKeyPress().getWhen(), 0);
		assertEquals(ei.nextKeyPress().getWhen(), 1);
		assertEquals(ei.nextKeyPress().getWhen(), 3);
		assertEquals(ei.nextKeyPress().getWhen(), 5);
		assertEquals(ei.nextKeyPress().getWhen(), 7);
		assertEquals(ei.nextKeyPress().getWhen(), 8);
		assertEquals(ei.nextKeyPress().getWhen(), 10);
		assertEquals(ei.nextKeyPress().getWhen(), 13);
	}

	@Test
	public void testHasNextKeyRelease() {
		EventList<KeyStroke> test = new EventList<KeyStroke>(events);
		test.remove(15);
		test.remove(13);
		test.remove(12);
		test.remove(10);
		KeyStrokeIterator ei = new KeyStrokeIterator(test);
		
		assertTrue(ei.hasNextKeyRelease());
		ei.next(); //1
		ei.next(); //2
		assertTrue(ei.hasNextKeyRelease());
		ei.next(); //3
		ei.next(); //4
		ei.next(); //5 
		ei.next(); //6 
		ei.next(); //7
		ei.next(); //8
		ei.next(); //9
		ei.next(); //10
		ei.next(); //11
		assertFalse(ei.hasNextKeyRelease());
	}

	@Test
	public void testNextKeyRelease() {
		KeyStrokeIterator ei = new KeyStrokeIterator(events);
		
		assertEquals(ei.nextKeyRelease().getWhen(), 1);
		assertEquals(ei.nextKeyRelease().getWhen(), 2);
		assertEquals(ei.nextKeyRelease().getWhen(), 4);
		assertEquals(ei.nextKeyRelease().getWhen(), 6);
		assertEquals(ei.nextKeyRelease().getWhen(), 9);
		assertEquals(ei.nextKeyRelease().getWhen(), 11);
		assertEquals(ei.nextKeyRelease().getWhen(), 12);
		assertEquals(ei.nextKeyRelease().getWhen(), 14);
	}

}
