package features.revision;

import java.io.*;
import java.util.*;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import features.revision.Revision.KeystrokeRevision;
import keytouch.KeyTouch;

/**
 * This class produces a csv of the features of eack keystroke as they
 * relate to revisions, specifically predicting whether the next keystroke
 * will be revised.
 * 
 * @author Adam Goodkind
 *
 */
public class RevisionPredictionCSV implements ExtractionModule {

	static final String csv_file = "revisions.csv";
	static boolean printHeaders = true;
	static final String[] headers = {"key",
									 "pause"};
	
	@Override
	public Collection<Feature> extract(DataNode data) {
		
		BufferedWriter outf = null;
		try {
			outf = new BufferedWriter(new FileWriter(csv_file, true));

			for (Answer a: data) {
				LinkedList<KeyTouch> ktList = KeyTouch.parseSessionToKeyTouches(a.getKeyStrokes());
				ArrayList<KeystrokeRevision> revisionKeystrokes = Revision.extendKeystrokesToRevisions(ktList);
				
			}
		} catch (IOException e) {
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
