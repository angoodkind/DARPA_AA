package features.revision;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import keytouch.KeyTouch;

public class TestParseRevision implements ExtractionModule {

	@Override
	public Collection<Feature> extract(DataNode data) {
		for (Answer a : data) {
//			LinkedList<KeyTouch> kList = KeyTouch.parseSessionToKeyTouches(a.getKeyStrokes());
//			for (KeyTouch kt : kList)
//				System.out.println(kt);
			ArrayList<Revision> rList = Revision.parseKeystrokesToRevision(
										KeyTouch.parseSessionToKeyTouches(a.getKeyStrokes()));
			
			for (Revision r : rList) {
//				System.out.print(data.getUserID());
//				System.out.print("|");
//				System.out.print(a.getQuestionID());
//				System.out.print("|");
				System.out.print(r);
				System.out.print("|");
//				System.out.print(r.isEarlyFinger());
//				System.out.print("|");
//				System.out.print(r.getEditDistance());
//				System.out.print("|");
//				System.out.print(r.isTransposition());
//				System.out.print("|");
//				System.out.print(r.isUnchanged());
//				System.out.print("|");
//				System.out.print(r.isFatFinger());
//				System.out.print("|");
//				System.out.print(r.isWordChange());
//				System.out.print("|");
//				System.out.print(r.isDoublingError());
//				System.out.print("|");
//				System.out.print(r.getPauseBeforeBackspaces());
//				System.out.print("|");
//				System.out.print(r.getPauseBeforeRevisedText());
//				System.out.print("|");
//				System.out.print(r.getMeanBackspacingHold());
//				System.out.print("|");
//				System.out.print(r.getMeanBackspacingPause());
				System.out.print("|");
				System.out.print(KeyTouch.getElapsedTime(r.deletedKeyTouches));
				System.out.print("|");
				System.out.print(KeyTouch.getElapsedTime(r.revisedKeyTouches));
				System.out.print("|");
				System.out.print(KeyTouch.getElapsedTimeWithoutLeadingPause(r.deletedKeyTouches));
				System.out.print("|");
				System.out.print(KeyTouch.getElapsedTimeWithoutLeadingPause(r.revisedKeyTouches));
				System.out.print("\n");
			}
//			System.out.println(String.format("%d, %d, %d, %d, %d", 
//											data.getUserID(),
//											a.getCogLoad(),
//											a.getAnswerID(),
//											rList.size(),
//											a.getCharStream().length()));
			
		}
		return null;
	}

	@Override
	public String getName() {
		return "Revision Test Utility";
	}

}
