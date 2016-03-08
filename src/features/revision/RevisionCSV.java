package features.revision;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import extractors.lexical.VisualCharStream;
import features.revision.Revision.KeystrokeRevision;
import keytouch.KeyTouch;

public class RevisionCSV implements ExtractionModule {

	static final String csv_file = "revisions.csv";
	static boolean printHeaders = true;

	@Override
	public Collection<Feature> extract(DataNode data) {

		BufferedWriter outf = null;
		try {
			outf = new BufferedWriter(new FileWriter(csv_file, true));

			for (Answer a: data) {
				LinkedList<KeyTouch> ktList = KeyTouch.parseSessionToKeyTouches(a.getKeyStrokes());
				ArrayList<Revision> rList = Revision.parseKeystrokesToRevision(ktList);
				
				String[] headers = {"UserID",
									"AnswerID",
									"CogLoad",
									"AnswerLen",
									"RevStartIdx",
									"RevSize",
									"EditDist",
									"DelKeys",
									"ReplKeys",
									"Transposition",
									"Unchanged",
									"FatFinger",
									"WordChange",
									"DoublingError",
									"EarlyFinger",
									"PauseBeforeBackspace",
									"PauseBeforeRevised",
									"MeanBackspaceHold",
									"MeanBackspacePause",
									"ElapsedDeletedTime",
									"ElapsedReplacedTime",
									"ElapsedDeletedTimeWoPause",
									"ElapsedReplacedTimeWoPause"};
				if (printHeaders) {
					for (int i = 0; i < headers.length; i++) {
						outf.write(headers[i]);
						if (i != headers.length-1)
							outf.write("|");
						else
							outf.write("\n");
					}
					printHeaders = false;
				}
				
				for (Revision r: rList) {
					outf.write(Integer.toString(data.getUserID()));
					outf.write("|");
					outf.write(Integer.toString(a.getAnswerID()));
					outf.write("|");
					outf.write(Integer.toString(a.getCogLoad()));
					outf.write("|");
					outf.write(Integer.toString(ktList.size()));
					outf.write("|");
					outf.write(Integer.toString(r.startingIndex));
					outf.write("|");
					outf.write(Integer.toString(r.deletedKeyTouches.size()));
					outf.write("|");
					outf.write(Integer.toString(r.getEditDistance()));
					outf.write("|");
					outf.write(VisualCharStream.ppVkCode(r.deletedKeyTouches));
					outf.write("|");
					outf.write(VisualCharStream.ppVkCode(r.revisedKeyTouches));
					outf.write("|");
					outf.write(Boolean.toString(r.isTransposition()));
					outf.write("|");
					outf.write(Boolean.toString(r.isUnchanged()));
					outf.write("|");
					outf.write(Boolean.toString(r.isFatFinger()));
					outf.write("|");
					outf.write(Boolean.toString(r.isWordChange()));
					outf.write("|");
					outf.write(Boolean.toString(r.isDoublingError()));
					outf.write("|");
					outf.write(Boolean.toString(r.isEarlyFinger()));
					outf.write("|");
					outf.write(Double.toString(r.getPauseBeforeBackspaces()));
					outf.write("|");
					outf.write(Double.toString(r.getPauseBeforeRevisedText()));
					outf.write("|");
					outf.write(Double.toString(r.getMeanBackspacingHold()));
					outf.write("|");
					outf.write(Double.toString(r.getMeanBackspacingPause()));
					outf.write("|");
					outf.write(Double.toString(KeyTouch.getElapsedTime(r.deletedKeyTouches)));
					outf.write("|");
					outf.write(Double.toString(KeyTouch.getElapsedTime(r.revisedKeyTouches)));
					outf.write("|");
					outf.write(Double.toString(KeyTouch.getElapsedTimeWithoutLeadingPause(r.deletedKeyTouches)));
					outf.write("|");
					outf.write(Double.toString(
							KeyTouch.getElapsedTimeWithoutLeadingPause( 
									r.revisedKeyTouches.subList(0, 
									r.size()>r.revisedKeyTouches.size()? r.revisedKeyTouches.size():r.size()))));
					outf.write("\n");
				}
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
		return "Revision CSV";
	}

}
