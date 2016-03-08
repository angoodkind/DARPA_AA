package ngrammodel;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import ngrammodel.UnigramTokenListGenerator;

/**RUN USING TEST files**/
public class UnigramTokenTest {

	@Test
	public void GenerateKeyStrokeUnigramsTest() {
		ArrayList<String> keystrokeUnigrams = UnigramTokenListGenerator.unigramTokenFileToList("TESTKeyStrokeTokens.data");
		String expectedKeyStrokeUnigrams = "[_START_, S, H, E, Spacebar, L, O, O, K, E, D, Spacebar, U, P, Spacebar, T, E, H, Backspace, Backspace, H, E, Spacebar, W, O, R, L, D, Spacebar, R, E, C, O, R, D, Period, _STOP_]";
		assertEquals(expectedKeyStrokeUnigrams,keystrokeUnigrams.toString());
	}
	
	@Test
	public void GenerateWordUnigramsTest() {
		ArrayList<String> wordUnigrams = UnigramTokenListGenerator.unigramTokenFileToList("TESTWordTokens.data");
		String expectedWordUnigrams = "[_START_, she, looked, up, tehhe, world, record, ., _STOP_]";
		assertEquals(expectedWordUnigrams,wordUnigrams.toString());	}
}
