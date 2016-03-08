package extractors.nyit;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;

import org.junit.Test;

import extractors.data.DataNode;
import extractors.data.FeatureExtractor;

public class FeatureExtractorTest {

	@Test
	public void testParseCustomDirectoryString() {
		
		FeatureExtractor e = new FeatureExtractor(new LinkedList<DataNode>());
		e.setCurSlice(3);
		e.setCustomOutputDirectory("%slice%min");
		
		assertEquals("03min", e.getOutputDirectory() );
	}

}
