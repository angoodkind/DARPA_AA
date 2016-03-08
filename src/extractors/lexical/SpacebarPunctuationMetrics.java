package extractors.lexical;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import keystroke.KeyStroke;
import keytouch.KeyTouch;

public class SpacebarPunctuationMetrics implements ExtractionModule {

	static final boolean WRITE_FILE = false;
	static final String csv_file = "alphanum-timing.csv";
	static final String SEP = ",";

	@Override
	public Collection<Feature> extract(DataNode data) {

		BufferedWriter outf = null;
		try {
			outf = new BufferedWriter(new FileWriter(csv_file, true));
			for (Answer a : data) {
				int userID = data.getUserID();
				int qID = a.getQuestionID();
				int aID = a.getAnswerID();
				
				LinkedList<KeyTouch> ktList = KeyTouch.parseSessionToKeyTouches(a.getKeyStrokes());
				for (int i = 0; i < ktList.size()-1; i++) {
					if (ktList.get(i).getKeystroke().isAlphaNumeric() &&
							 ktList.get(i+1).getKeystroke().isAlphaNumeric()
							/**ktList.get(i).getKeystroke().isClauseFinalPunctuation() ||
						ktList.get(i).getKeystroke().isSpace() ||
						ktList.get(i).getKeystroke().isSentenceFinalPunctuation()**/) {
							KeyTouch k1 = ktList.get(i);
							KeyTouch k2 = ktList.get(i+1);
							String k1Name = KeyStroke.vkCodetoString(k1.getKeyCode());
							String k2Name = KeyStroke.vkCodetoString(k2.getKeyCode());
//							double precedingPause = i==0? Double.NaN : k.getPrecedingPause();
							double trailingPause = i==ktList.size()-1? Double.NaN : ktList.get(i+1).getPrecedingPause();
							outf.write(Integer.toString(userID));
							outf.write(SEP);
							outf.write(Integer.toString(aID));
							outf.write(SEP);
							outf.write(k1Name);
							outf.write(SEP);
							outf.write(k2Name);
							outf.write(SEP);
							outf.write(Double.toString(k2.getPrecedingPause()));
//							outf.write(Double.toString(precedingPause));
//							outf.write(SEP);
//							outf.write(Double.toString(trailingPause));
							outf.write("\n");
					}
				}
			}
		} 	 catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (outf != null) {
				try {
					outf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
