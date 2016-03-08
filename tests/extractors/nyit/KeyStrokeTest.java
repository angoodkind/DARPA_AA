package extractors.nyit;

import static junit.framework.Assert.assertEquals;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import keystroke.KeyStroke;
import keystroke.KeyStrokeParser;

/**
 * JUnit tests for the KeyStroke class
 */
public class KeyStrokeTest {
	
@Test
  public void testKeyStrokeToFinalTextCorrectlyDeletesSelectionOnBackspaceUpdate() {

    ArrayList<KeyStroke> ks = new ArrayList<>();
    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 0, KeyEvent.VK_A, 'a', 0));
    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 1, KeyEvent.VK_A, 'a', 1));

    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 1, KeyEvent.VK_B, 'b', 1));
    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 2, KeyEvent.VK_B, 'b', 2));

    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 3, KeyEvent.VK_C, 'c', 2));
    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 4, KeyEvent.VK_C, 'c', 3));

    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 5, KeyEvent.VK_D, 'd', 3));
    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 6, KeyEvent.VK_D, 'd', 4));

    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 7, KeyEvent.VK_SHIFT, (char)0, 4));
    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 8, KeyEvent.VK_LEFT, (char)0, 4));
    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 9, KeyEvent.VK_LEFT, (char)0, 3));
    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 10, KeyEvent.VK_LEFT, (char)0, 3));
    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 11, KeyEvent.VK_LEFT, (char)0, 2));
    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 12, KeyEvent.VK_SHIFT, (char)0, 2));

    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 13, KeyEvent.VK_BACK_SPACE, (char)8, 2));
    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 14, KeyEvent.VK_BACK_SPACE, (char)8, 2));

    String final_text = KeyStroke.keyStrokesToFinalText(ks);
    assertEquals("ab", final_text);
  }
  
  @Test
  public void testKeyStrokeToFinalTextCorrectlyDeletesSelectionOnOverwriteUpdate() {

    ArrayList<KeyStroke> ks = new ArrayList<>();
    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 0, KeyEvent.VK_A, 'a', 0));
    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 1, KeyEvent.VK_A, 'a', 1));

    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 1, KeyEvent.VK_B, 'b', 1));
    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 2, KeyEvent.VK_B, 'b', 2));

    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 3, KeyEvent.VK_C, 'c', 2));
    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 4, KeyEvent.VK_C, 'c', 3));

    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 5, KeyEvent.VK_D, 'd', 3));
    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 6, KeyEvent.VK_D, 'd', 4));

    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 7, KeyEvent.VK_SHIFT, (char)0, 4));
    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 8, KeyEvent.VK_LEFT, (char)0, 4));
    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 9, KeyEvent.VK_LEFT, (char)0, 3));
    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 10, KeyEvent.VK_SHIFT, (char)0, 3));

    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 11, KeyEvent.VK_E, 'e', 3));
    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 12, KeyEvent.VK_E, 'e', 4));

    String final_text = KeyStroke.keyStrokesToFinalText(ks);
    assertEquals("abce", final_text);
  }
  
  @Test
  public void testKeyStrokeToFinalTextCorrectlyDeletesSelectionOnDeleteUpdate() {

    ArrayList<KeyStroke> ks = new ArrayList<>();
    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 0, KeyEvent.VK_A, 'a', 0));
    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 1, KeyEvent.VK_A, 'a', 1));

    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 1, KeyEvent.VK_B, 'b', 1));
    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 2, KeyEvent.VK_B, 'b', 2));

    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 3, KeyEvent.VK_C, 'c', 2));
    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 4, KeyEvent.VK_C, 'c', 3));

    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 5, KeyEvent.VK_D, 'd', 3));
    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 6, KeyEvent.VK_D, 'd', 4));
    
    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 7, KeyEvent.VK_SHIFT, (char)0, 4));
    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 8, KeyEvent.VK_LEFT, (char)0, 4));
    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 9, KeyEvent.VK_LEFT, (char)0, 3));
    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 10, KeyEvent.VK_LEFT, (char)0, 3));
    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 11, KeyEvent.VK_LEFT, (char)0, 2));
    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 12, KeyEvent.VK_SHIFT, (char)0, 2));

    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 13, KeyEvent.VK_DELETE, (char)127, 2));
    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 14, KeyEvent.VK_DELETE, (char)127, 2));

    String final_text = KeyStroke.keyStrokesToFinalText(ks);
    assertEquals("ab", final_text);
  }

  @Test
  public void testKeyStrokeToFinalTextCorrectlyDeletesSelectionOnDelete() {

    ArrayList<KeyStroke> ks = new ArrayList<>();
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

    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 13, KeyEvent.VK_DELETE, ' ', 2));
    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 14, KeyEvent.VK_DELETE, ' ', 2));

    String final_text = KeyStroke.keyStrokesToFinalText(ks);
    assertEquals("ab", final_text);
 }


  @Test
  public void testKeyStrokeToFinalTextCorrectlyDeletesSelectionOnOverwrite() {

    ArrayList<KeyStroke> ks = new ArrayList<>();
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
    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 10, KeyEvent.VK_SHIFT, ' ', 3));

    ks.add(new KeyStroke(KeyEvent.KEY_PRESSED, 11, KeyEvent.VK_E, 'e', 3));
    ks.add(new KeyStroke(KeyEvent.KEY_RELEASED, 12, KeyEvent.VK_E, 'e', 4));

    String final_text = KeyStroke.keyStrokesToFinalText(ks);
    assertEquals("abce", final_text);
  }

  @Test
  public void testKeyStrokeToFinalTextCorrectlyDeletesSelectionOnBackspace() {

    ArrayList<KeyStroke> ks = new ArrayList<>();
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

    String final_text = KeyStroke.keyStrokesToFinalText(ks);
    assertEquals("ab", final_text);
  }
  
  @Test
  public void testKeystrokesToFinalTextCorrectlyHandlesBackspaceAndShift() {

    // parse string of keystrokes, and add to keyStrokeList
    String keyStrokeString = " 0:2e:46:1336160382812:258 1:2e:46:1336160382906:259 0:20:32:1336160383000:259 0:10:65535:1336160383109:260 1:20:32:1336160383125:260 0:49:73:1336160383265:260 1:10:65535:1336160383375:261 1:49:73:1336160383390:261 0:8:8:1336160383828:261 1:8:8:1336160383875:260 0:10:65535:1336160384046:260 0:54:84:1336160384218:260 1:10:65535:1336160384265:261 0:48:104:1336160384328:261 1:54:84:1336160384343:262 1:48:104:1336160384437:262 0:45:101:1336160384453:262";
    LinkedList<KeyStroke> keyStrokeList = new LinkedList<>();
    keyStrokeList.addAll(new KeyStrokeParser().parseSession(keyStrokeString));

    ArrayList<String> finalTextActual = new ArrayList<>();
    ArrayList<String> finalTextExpected = new ArrayList<>();

    // add incremented segments of expected outputs to finalTextExpected
    List<String> expectedStrings = Arrays
        .asList(".", ".", ". ", ". ", ". ", ". I", ". I", ". I", ". ", ". ", ". ", ". T", ". T", ". Th", ". Th", ". Th",
            ". The");
    //this version of "expectedStrings" contains the current, incorrect output
//  List<String> expectedStrings = Arrays.asList(".",".",". ",". ",". ",". I",". I",". I",". I",". I",". I",". TI",". TI",". Th",". Th",". Th",". The");
    finalTextExpected.addAll(expectedStrings);

    // generate and add incremented segments of keyStrokeList, processed by keyStrokeToFinalText, to FinalTextActual
    // uses size()+1 because sublist(x,y) is inclusive x, exclusive y
    for (int i = 1; i < keyStrokeList.size()+1; i++)
      finalTextActual.add(KeyStroke.keyStrokesToFinalText(keyStrokeList.subList(0, i)));

    Assert.assertEquals(finalTextExpected, finalTextActual);
  }
}
