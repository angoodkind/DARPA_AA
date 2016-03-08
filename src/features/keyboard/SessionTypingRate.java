/**
 * 
 */
package features.keyboard;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

import events.EventList;
import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import extractors.lexical.SentenceDetector;
import extractors.lexical.Tokenize;
import keystroke.KeyStroke;


/**
 * @author agoodkind
 *
 */
public class SessionTypingRate implements ExtractionModule {

	SentenceDetector sentDetect;
	Tokenize tokens;
	Collection<Feature> output;
	Collection<Double> wordRate;
	Collection<Double> keyStrokeRate;
	
	public SessionTypingRate() {
		sentDetect = new SentenceDetector();
		tokens = new Tokenize();
		output = new LinkedList<Feature>();
		wordRate = new LinkedList<Double>();
		keyStrokeRate = new LinkedList<Double>();
	}
	
	public void clearLists() {
		output.clear();
		wordRate.clear();
		keyStrokeRate.clear();
	}
	
	@Override
	public Collection<Feature> extract(DataNode data) {
		clearLists();
		for (Answer a : data) {
			try {
			
				EventList<KeyStroke> typingSession = a.getKeyStrokeList();
				String finalText = KeyStroke.keyStrokesToFinalText(typingSession);
				int keyStrokeCount = a.getKeyStrokeList().size();
				int wordCount = tokens.runTokenizer(finalText).length;
				
				keyStrokeRate.add(keyStrokesPerSecond(typingSession,keyStrokeCount));
				wordRate.add(wordsPerSecond(typingSession,wordCount));
			
			} catch (IOException e) {e.printStackTrace();}
		}
		output.add(new Feature("KeyStrokes_Per_Second",keyStrokeRate));
		output.add(new Feature("Words_Per_Second",wordRate));
//	    for (Feature f : output) System.out.println(f.toTemplate());
		return output;
	}

	// measures over entire string
	public double wordsPerSecond(EventList<KeyStroke> keyStrokeList, int wordCount) {
		long sessionStart = keyStrokeList.getFirst().getWhen();
		long sessionEnd = keyStrokeList.getLast().getWhen();
		long sessionLength = sessionEnd - sessionStart;
		
		return (sessionLength / 1000.) / wordCount;
	}
	
	public double keyStrokesPerSecond(EventList<KeyStroke> keyStrokeList, int keyStrokeCount) {
		long sessionStart = keyStrokeList.getFirst().getWhen();
		long sessionEnd = keyStrokeList.getLast().getWhen();
		long sessionLength = sessionEnd - sessionStart;
		
		return (sessionLength / 1000.) / keyStrokeCount;
	}
	
	@Override
	public String getName() {
		return "Typing_Rate";
	}

}
