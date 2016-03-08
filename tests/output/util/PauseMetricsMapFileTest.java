package output.util;

import static org.junit.Assert.*;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collection;
import java.util.HashMap;

import org.junit.Test;

import events.EventList;
import events.GenericEvent;
import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.lexical.CreateUserPauseMetricsMap;
import keystroke.KeyStroke;

public class PauseMetricsMapFileTest {

//	@Test
//	public void loadDebugPauseMapTest() {
//		HashMap<Integer,HashMap<String,Double>> debugMap = null;
//		try {
//			String fileName = "DebugPauseMetrics.map"; //"DebugPauseMetrics.map"; 
//			File file = new File(fileName);
//			FileInputStream fileIStream;
//			fileIStream = new FileInputStream(file);
//			ObjectInputStream objectIStream = new ObjectInputStream(fileIStream);
//			debugMap = (HashMap<Integer,HashMap<String,Double>>) objectIStream.readObject();
//			objectIStream.close();
//		} catch (IOException | ClassNotFoundException e) {e.printStackTrace();}
//		
//		for (Integer i : debugMap.keySet()) {
//			HashMap<String,Double> testMap = debugMap.get(i);
//			for (String s : testMap.keySet()) {
//				System.out.println("User"+i+" "+s+": "+testMap.get(s));
//			}
//		}
//	}
	
	@Test
	public void CheckPauseMetrics() {
		EventList<KeyStroke> ks = new EventList<>();
		ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 1, 83, 's', 0));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 2, 83, 's', 1));
	    
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 3, 72, 'h', 1));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 4, 72, 'h', 2));
	    
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 5, 32, (char)32, 2));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 6, 32, (char)32, 3));
	    
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 7, 76, 'l', 3));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 8, 76, 'l', 4));
	    
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 9, 79, 'o', 4));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 10, 79, 'o', 5));
	    
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 11, 46,(char)46, 5));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 12, 46,(char)46, 6));
	    
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 13, 32, (char)32, 6));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 14, 32, (char)32, 7));
	    
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 15, 85, 'u', 7));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 16, 85, 'u', 8));
	    
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 17, 80, 'p', 8));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 18, 80, 'p', 9));
	    
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 19, 46,(char)46, 9));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 20, 46,(char)46, 10));
	    
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 21, 32, (char)32, 10));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 22, 32, (char)32, 11));
	    
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 23, 32, (char)32, 11));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 24, 32, (char)32, 12));
	    
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 25, 84, 't', 12));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 26, 84, 't', 13));
	    
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 27, 69, 'e', 13));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 28, 69, 'e', 14));
	    
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 29, 72, 'h', 14));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 30, 72, 'h', 15));
	    
	    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 31, 46,(char)46, 15));
	    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 32, 46,(char)46, 16));
	    
	    Collection<GenericEvent> ge = new EventList<>();
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 1, 83, 's', 0));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 2, 83, 's', 1));
	    
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 3, 72, 'h', 1));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 4, 72, 'h', 2));
	    
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 5, 32, (char)32, 2));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 6, 32, (char)32, 3));
	    
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 7, 76, 'l', 3));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 8, 76, 'l', 4));
	    
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 9, 79, 'o', 4));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 10, 79, 'o', 5));
	    
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 11, 46,(char)46, 5));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 12, 46,(char)46, 6));
	    
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 13, 32, (char)32, 6));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 14, 32, (char)32, 7));
	    
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 15, 85, 'u', 7));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 16, 85, 'u', 8));
	    
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 17, 80, 'p', 8));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 18, 80, 'p', 9));
	    
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 19, 46,(char)46, 9));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 20, 46,(char)46, 10));
	    
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 21, 32, (char)32, 10));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 22, 32, (char)32, 11));
	    
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 23, 32, (char)32, 11));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 24, 32, (char)32, 12));
	    
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 25, 84, 't', 12));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 26, 84, 't', 13));
	    
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 27, 69, 'e', 13));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 28, 69, 'e', 14));
	    
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 29, 72, 'h', 14));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 30, 72, 'h', 15));
	    
	    ge.add(new KeyStroke(KeyEvent.KEY_PRESSED, 31, 46,(char)46, 15));
	    ge.add(new KeyStroke(KeyEvent.KEY_RELEASED, 32, 46,(char)46, 16));
	    	    	    
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
	    /**--------------------------------------------**/
	    CreateUserPauseMetricsMap module = new CreateUserPauseMetricsMap();
	    module.extract(dn);
//	    for (Integer i : module.getPauseMap().keySet()) {
//	    	HashMap<String,Double> testMap = module.getPauseMap().get(i);
//	    	for (String s : testMap.keySet()) {
//	    		System.out.println("User"+i+" "+s+": "+testMap.get(s));
//	    	}
//	    }
	}
	

}
