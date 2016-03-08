package extractors.lex;

import static org.junit.Assert.*;

import java.io.*;
import java.util.*;

import org.junit.Test;

public class CreateTypingRateMapTest {

	@Test
	public void test() {
		try {
			File file = new File("S1TypingRate.map");
		    FileInputStream f;
			f = new FileInputStream(file);
		    ObjectInputStream s = new ObjectInputStream(f);
		    HashMap<Integer, Double> fileObj = (HashMap<Integer, Double>) s.readObject();
		    s.close();
		    
		    for (Integer user : fileObj.keySet())
		    	System.out.println(user+" "+fileObj.get(user));
	    
		} catch (IOException | ClassNotFoundException e) {e.printStackTrace();}
	}

}
