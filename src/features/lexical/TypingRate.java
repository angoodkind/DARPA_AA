package features.lexical;

import java.io.IOException;
import java.util.*;

import opennlp.tools.util.InvalidFormatException;
import events.EventList;
import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import extractors.lexical.Tokenize;
import features.pause.KSE;
import keystroke.KeyStroke;

public class TypingRate implements ExtractionModule{

	private EventList<KeyStroke> keyStrokeList;
	private ArrayList<Double> wordsPerMinute;
	private ArrayList<Double> keyStrokesPerMinute;
	private ArrayList<Double> intraWordTypingRate;
	
	public TypingRate() {
		keyStrokeList = new EventList<KeyStroke>();
		wordsPerMinute = new ArrayList<Double>();
		keyStrokesPerMinute = new ArrayList<Double>();
		intraWordTypingRate = new ArrayList<Double>();
	}
	
	public void clearLists() {
		keyStrokeList.clear();
		wordsPerMinute.clear();
		keyStrokesPerMinute.clear();
		intraWordTypingRate.clear();
	}
	
	@Override
	public Collection<Feature> extract(DataNode data) {
		clearLists();
		for (Answer a : data) {

			try {
				keyStrokeList = a.getKeyStrokeList();
				String keystrokes = a.getKeyStrokes();
				wordsPerMinute.add(wordsPerMinute());
				keyStrokesPerMinute.add(keyStrokesPerMinute());
				intraWordTypingRate.add(intraWordTypingRate(keystrokes));
//				System.out.println(data.getUserID()+","+a.getCogLoad()+","+intraWordTypingRate(keystrokes));
			} catch (InvalidFormatException e) { e.printStackTrace();
			} catch (IOException e) { e.printStackTrace();}
		}
		
		LinkedList<Feature> output = new LinkedList<Feature>();
		output.add(new Feature("wordsPerMinute",wordsPerMinute));
		output.add(new Feature("keyStrokesPerMinute",keyStrokesPerMinute));
		output.add(new Feature("intraWordTypingRate",intraWordTypingRate));
		
//		for (Feature f : output) System.out.println(f.toTemplate());
		
		return output;
	}
	
	public double wordsPerMinute() throws InvalidFormatException, IOException {
		
		String visibleText = keyStrokeList.toVisibleTextString();
		Tokenize t = new Tokenize();
		String[] unigramTokens = t.runTokenizer(visibleText);
		double wordCount = unigramTokens.length*1.0;
		long startTime = keyStrokeList.get(0).getWhen();
		long endTime = keyStrokeList.get(keyStrokeList.size()-1).getWhen();
		double sessionDuration = (endTime-startTime)/60000.0;
		
		return wordCount/sessionDuration;
	}

	public double keyStrokesPerMinute() {
		double keyStrokeCount = 0;
		for (KeyStroke k : keyStrokeList)
			if (k.isKeyPress())
				keyStrokeCount++;
		long startTime = keyStrokeList.get(0).getWhen();
		long endTime = keyStrokeList.get(keyStrokeList.size()-1).getWhen();
		double sessionDuration = (endTime-startTime)/60000.0;
		
		return keyStrokeCount/sessionDuration;
	}
	
	public double intraWordTypingRate(String ks) {
		
		ArrayList<KSE> kseList = new ArrayList<KSE>(KSE.parseSessionToKSE(ks));
		ArrayList<KSE> ksePresses = new ArrayList<KSE>();
		for (KSE kse : kseList)
			if (kse.isKeyPress())
				ksePresses.add(kse);
		
		int kseCount = 0;
		double elapsedTime = 0.0;
		for (int i = 1; i < ksePresses.size(); i++) {
			KSE prevKSE = ksePresses.get(i-1);
			KSE kse = ksePresses.get(i);
			if (!(prevKSE.isSpace() || kse.isSpace())) {
				kseCount++;
				elapsedTime += (double) kse.getM_pauseMs();
			}	
		}
		
		return (kseCount/elapsedTime)*1000.0;
	}
	
	@Override
	public String getName() {
		return "Typing Rate";
	}

}
