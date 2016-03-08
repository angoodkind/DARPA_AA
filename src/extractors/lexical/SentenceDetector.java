/**
 * 
 */
package extractors.lexical;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.InvalidFormatException;

/**
 * @author agoodkind
 *
 */
public class SentenceDetector {
	
	SentenceDetectorME sdetector;
	
	public Integer getSentenceCount(String answer) throws InvalidFormatException, IOException {
		
		String[] sentenceArray = getSentenceList(answer);
		return sentenceArray.length;
	}
	
	public String[] getSentenceList(String answer) throws InvalidFormatException, IOException {
		
		String sentenceArray[] = sdetector.sentDetect(answer);
		return sentenceArray;
	}
	
	public SentenceDetector() {
		try {
			InputStream isSent = new FileInputStream("en-sent.bin");
			SentenceModel sModel = new SentenceModel(isSent);
			sdetector = new SentenceDetectorME(sModel);
		} catch (IOException e) {e.printStackTrace();}	
	}
	
}
