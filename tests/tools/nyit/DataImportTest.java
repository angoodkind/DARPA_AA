package tools.nyit;

import static org.junit.Assert.assertArrayEquals;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import output.util.TextDataImport;

public class DataImportTest {

	@Test
	public void dirTreeToStringArrayTest() {
		String str = "Day 9\\Tolliver\\Machine 4\\data\\yma004_10187053\\Session2\\Session12Oct2012 1603\\FreeText\\q8_8_O_AllKeys.txt";
		String[] split = str.split("\\\\");
		Path path = Paths.get(str);
		String[] test = TextDataImport.dirTreeToStringArray(path);
		assertArrayEquals(test, split);
	}

	
}
