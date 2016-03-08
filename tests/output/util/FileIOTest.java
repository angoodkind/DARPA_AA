package output.util;

import static org.junit.Assert.*;

import java.io.*;
import java.util.*;

import org.junit.Test;

public class FileIOTest {

	@Test
	public void test() {
		String[] arr = {"blah","seven","tiger2"};
		ArrayList<String> list = new ArrayList<String>();
		for (String s : arr)
			list.add(s);
		
		File f = new File("test.list");
		try {
			FileOutputStream fOut = new FileOutputStream(f);
			ObjectOutputStream out = new ObjectOutputStream(fOut);
			out.writeObject(list);
			out.close();
			
//			File in = new File("test.list");
			FileInputStream fIn = new FileInputStream(f);
			ObjectInputStream in = new ObjectInputStream(fIn);
			ArrayList<String> fileList = (ArrayList<String>) in.readObject();
			in.close();
			
			for (String s : fileList)
				System.out.println(s);
			
		} catch (IOException | ClassNotFoundException e) {e.printStackTrace();}
		
	}

}
