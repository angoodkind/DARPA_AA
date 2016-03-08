package features.pause;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import features.pause.PauseBursts;

/**
 * Test class for BetKSEPause
 */
public class PauseBurstsTest1 {
	
  @Test
  public void testGeneratePauseDownListPropagateKSEArray() {
	  
	  int pause_threshold = 250;
	  
    PauseBursts pb = new PauseBursts();
    String s = " 0:10:65535:1336160308375:0 0:54:84:1336160308500:0 1:10:65535:1336160308562:1 1:54:84:1336160308625:1 0:48:104:1336160308640:1 1:48:104:1336160308718:2 0:45:101:1336160308781:2 0:20:32:1336160308843:3 1:45:101:1336160308875:4 1:20:32:1336160308953:4 0:4c:108:1336160309468:4 ";
    pb.generatePauseDownList(s,pause_threshold);
    assertNotNull(pb.getKseArray());
  }
}
