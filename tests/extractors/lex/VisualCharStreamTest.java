/**
 * 
 */
package extractors.lex;

import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.junit.Test;

import extractors.lexical.VisualCharStream;
import features.pause.KSE;

/**
 * @author agoodkind
 *
 */
public class VisualCharStreamTest {

	@Test
	public void testVCSToString() {
		
		int pause_threshold = 250;
		
		String ksStr = " 0:10:65535:1336160308375:0 0:54:84:1336160308500:0 1:10:65535:1336160308562:1 1:54:84:1336160308625:1 0:48:104:1336160308640:1 1:48:104:1336160308718:2 0:45:101:1336160308781:2 0:20:32:1336160308843:3 1:45:101:1336160308875:4 1:20:32:1336160308953:4 0:4c:108:1336160309468:4 1:4c:108:1336160309562:5 0:41:97:1336160309609:5 1:41:97:1336160309734:6 0:53:115:1336160309921:6 1:53:115:1336160310062:7 0:54:116:1336160310203:7 1:54:116:1336160310312:8 0:20:32:1336160310375:8 1:20:32:1336160310468:9 0:42:98:1336160310531:9 1:42:98:1336160310609:10 0:4f:111:1336160310671:10 1:4f:111:1336160310781:11 0:4f:111:1336160310875:11 1:4f:111:1336160310968:12 0:4b:107:1336160311062:12 1:4b:107:1336160311171:13 0:20:32:1336160311562:13 1:20:32:1336160311718:14 0:49:105:1336160312796:14 1:49:105:1336160312859:15 0:20:32:1336160313203:15 1:20:32:1336160313312:16 0:52:114:1336160313390:16 0:45:101:1336160313453:17 1:52:114:1336160313484:18 0:41:97:1336160313578:18 1:45:101:1336160313609:19 1:41:97:1336160313734:19 0:44:100:1336160313750:19 1:44:100:1336160313890:20 0:20:32:1336160313921:20 1:20:32:1336160314031:21 0:57:119:1336160314109:21 1:57:119:1336160314250:22 0:41:97:1336160314281:22 1:41:97:1336160314406:23 0:53:115:1336160314453:23 1:53:115:1336160314578:24 0:20:32:1336160314687:24 1:20:32:1336160314828:25 0:10:65535:1336160315562:25 0:54:84:1336160315718:25 1:10:65535:1336160315781:26 1:54:84:1336160315843:26 0:48:104:1336160315859:26 1:48:104:1336160315937:27 0:45:101:1336160315953:27 1:45:101:1336160316109:28 0:20:32:1336160316343:28 1:20:32:1336160316484:29 0:10:65535:1336160316671:29 0:48:72:1336160316796:29 1:10:65535:1336160316875:30 0:55:117:1336160316875:30 1:48:72:1336160316890:31 1:55:117:1336160316968:31 0:4e:110:1336160317031:31 1:4e:110:1336160317140:32 0:47:103:1336160317171:32 1:47:103:1336160317218:33";
		String vcsTestStr = "[Shift]THE __LA_S_T BOOK__ ____I_ READ WAS ___[Shift]THE_ _[Shift]HUNG";
		Collection<KSE> kseArray;
		VisualCharStream vcs = new VisualCharStream(ksStr);
		String vcsStr = vcs.toString(pause_threshold);
		
		assertEquals(vcsTestStr,vcsStr);
	}

}