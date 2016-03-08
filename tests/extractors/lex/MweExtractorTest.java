package extractors.lex;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import edu.mit.jmwe.data.IMWE;
import edu.mit.jmwe.data.IToken;
import edu.mit.jmwe.data.Token;
import edu.mit.jmwe.detect.Consecutive;
import edu.mit.jmwe.detect.IMWEDetector;
import edu.mit.jmwe.index.IMWEIndex;
import edu.mit.jmwe.index.MWEIndex;

public class MweExtractorTest {

	private final String mweString = "[look_up_V={looked_VBD,up_RP}, world_record_N={world_NN,record_NN}]";
	private List<IMWE<IToken>> mwes;
	
	@Test
	public void test() throws IOException {
		
		// get handle to file containing MWE index data, // e.g., mweindex_wordnet3.0_Semcor1.6.data
		File idxData = new File("mweindex_wordnet3.0_semcor1.6.data");
		// construct an MWE index and open it
		IMWEIndex index = new MWEIndex(idxData); index.open();
		// make a basic detector
		IMWEDetector detector = new Consecutive(index);
		// construct a test sentence:
		// "She looked up the world record."
		List <IToken > sentence = new ArrayList <IToken >();
		sentence.add(new Token("She", "DT"));
		sentence.add(new Token("looked", "VBD", "look"));
		sentence.add(new Token("up", "RP"));
		sentence.add(new Token("the", "DT"));
		sentence.add(new Token("world", "NN"));
		sentence.add(new Token("record", "NN")); 
		sentence.add(new Token(".","."));
		
		// run detector and print out results
		mwes = detector.detect(sentence); 
		
		assertEquals(mweString,mwes.toString());
	}

}
