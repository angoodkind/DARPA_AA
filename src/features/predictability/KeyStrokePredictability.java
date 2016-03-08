package features.predictability;

import java.util.*;
import java.io.*;

import events.EventList;
import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import extractors.lexical.TokenExtender;
import features.pause.KSE;
import keystroke.KeyStroke;
import mwe.TokenExtended;
import ngrammodel.*;

/**
 * Calculated from pre-existing language model
 * @author Adam Goodkind
 */
public class KeyStrokePredictability extends Predictability implements ExtractionModule {
	private static final String modelName = "DEBUG";
	private static final String gramType = "keystroke";
	private static TokenExtender extender = new TokenExtender();
	
	private Collection<Double> keyStrokeBigramPredictability;
	private Collection<Double> keyStrokeTrigramPredictability;
		
	public KeyStrokePredictability() {
		super(modelName,gramType);
		keyStrokeBigramPredictability = new ArrayList<Double>();
		keyStrokeTrigramPredictability = new ArrayList<Double>();
	}
	
	public void clearLists() {
		keyStrokeBigramPredictability.clear();
		keyStrokeTrigramPredictability.clear();
	}
	
	@Override
	public Collection<Feature> extract(DataNode data) {
		clearLists();
		for (Answer a : data) {
			getKeyStrokePredictability(a.getKeyStrokes());
		}
		
		LinkedList<Feature> output = new LinkedList<Feature>();
		output.add(new Feature("KeyStroke Bigram Predictability",keyStrokeBigramPredictability));
		output.add(new Feature("KeyStroke Trigram Predictability",keyStrokeTrigramPredictability));
//		for (Feature f : output) System.out.println(f.toTemplate());
		
		return output;
	}
	
	/**
	 * Generate keyStrokeBigramPredictability and keyStrokeTrigramPredictability lists
	 * by iterating through KeyStroke String
	 * 
	 * @param keyStrokes	A String of KeyStrokes (Answer.getKeyStrokes())
	 */
	public void getKeyStrokePredictability(String keyStrokes) {
		
		ArrayList<KSE> kseList = new ArrayList<KSE>(KSE.parseSessionToKSE(keyStrokes));
		ArrayList<KSE> keyPresses = new ArrayList<KSE>();
		for (KSE kse : kseList)
			if (kse.isKeyPress()) 
				keyPresses.add(kse);
		
		for (int i = 0; i < keyPresses.size()-2; i++) {
			String gram1 = KeyStroke.vkCodetoString(keyPresses.get(i).getKeyCode());
			String gram2 = KeyStroke.vkCodetoString(keyPresses.get(i+1).getKeyCode());
			String gram3 = KeyStroke.vkCodetoString(keyPresses.get(i+2).getKeyCode());
			
			double bigramProbability = keyStrokeBigramModel.getBigramProbability(gram1,gram2);
			double bigramPause = keyPresses.get(i+1).getM_pauseMs()/1000.0;
			double bigramPredictability = bigramPause/(-1.0*Math.log(bigramProbability));
			keyStrokeBigramPredictability.add(bigramPredictability);
			
			
			double trigramProbability = keyStrokeTrigramModel.getTrigramProbability(gram1,gram2,gram3);
			double trigramPause = keyPresses.get(i+2).getM_pauseMs()/1000.0;
			double trigramPredictability = trigramPause/(-1.0*Math.log(trigramProbability));
			keyStrokeTrigramPredictability.add(trigramPredictability);
		}
		
		//one more for last bigram
		int length = keyPresses.size();
		String gram1 = KeyStroke.vkCodetoString(keyPresses.get(length-2).getKeyCode());
		String gram2 = KeyStroke.vkCodetoString(keyPresses.get(length-1).getKeyCode());
		double bigramProbability = keyStrokeBigramModel.getBigramProbability(gram1,gram2);
		double bigramPause = keyPresses.get(length-1).getM_pauseMs()/1000.0;
		double bigramPredictability = bigramPause/bigramProbability;
		keyStrokeBigramPredictability.add(bigramPredictability);
		
	}
		
	public Collection<Double> getKeyStrokeBigramPredictabilityList() {
		return keyStrokeBigramPredictability;
	}
	
	public Collection<Double> getKeyStrokeTrigramPredictabilityList() {
		return keyStrokeTrigramPredictability;
	}
		
	@Override
	public String getName() {
		return "KeyStroke Predictability";
	}

}
