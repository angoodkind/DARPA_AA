package output.util;
import java.util.LinkedList;
import java.util.Collection;

import events.GenericEvent;
import extractors.data.Answer;
import keystroke.KeyStroke;

public class SegmentAnswer {

	public static Answer BetweenKeyStrokes (Answer a, int FromKeyIndex, int ToKeyIndex, int mode) {
		
		// The following properties remain the same across segments
		// of the same answer.
		int answerID = a.getAnswerID();
		int	questionID = a.getQuestionID();
		int cogLoad = a.getCogLoad();
		int orderID = a.getOrderID();
		String type = a.getType();
		
		String[] keyStrokes = a.getKeyStrokes().trim().split("\\s+");
		
		// Declare variables and data structures to hold extracted data
		LinkedList<KeyStroke> keyStrokeSubList = new LinkedList<KeyStroke>();
		Collection<GenericEvent> keyStrokeSubList2 = new LinkedList<GenericEvent>();
		StringBuilder charSubStream = new StringBuilder();
		String finalSubText = null;
		StringBuilder keyStrokesSubString = new StringBuilder();
		
		/// Logic to extract final text for the corresponding sub-list of keystroke
		for (int i = FromKeyIndex; i <= ToKeyIndex; i++) {
			KeyStroke ks = a.getKeyStrokeList().get(i);
			
			if (ks.isKeyPress()) charSubStream.append(ks.getKeyChar());
			
			keyStrokeSubList.add(ks); // Add KeyStroke to Keystroke Linked List
			
			keyStrokeSubList2.add(ks); // Add the keystroke to the GenericEvent Array
			
			keyStrokesSubString.append(keyStrokes[i]).append(" "); // Create new character stream 
		}
		
		// Extract final text from given sub-set of keystrokes
		//finalSubText = KeyStroke.keyStrokesToFinalText((Collection<KeyStroke>)keyStrokeSubList);
		
		//System.out.println("FINAL TEXT: " + charSubStream.toString());
		switch (mode) {
			case 1: {
				finalSubText = KeyStroke.keyStrokesToFinalText((Collection<KeyStroke>)keyStrokeSubList);
				break;
			}
			case 2: {
				finalSubText = charSubStream.toString();
				break;
			}
		}
		
		return new Answer(charSubStream.toString(), finalSubText, keyStrokesSubString.toString(),
			answerID, questionID, orderID, cogLoad, type, keyStrokeSubList2);
		
		//return new Answer(charSubStream.toString(), charSubStream.toString(), keyStrokesSubString.toString(),
		//		answerID, questionID, orderID, cogLoad, type, keyStrokeSubList2);
		
	}
	
	
	
}
