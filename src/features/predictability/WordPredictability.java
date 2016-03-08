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
public class WordPredictability extends Predictability implements ExtractionModule {
	private static final String modelName = "SESSION1";
	private static final String gramType = "word";
	private static TokenExtender extender = new TokenExtender();
	
	private Collection<Double> wordBigramPredictability;
	private Collection<Double> wordTrigramPredictability;
		
	public WordPredictability() {
		super(modelName,gramType);
		wordBigramPredictability = new ArrayList<Double>();
		wordTrigramPredictability = new ArrayList<Double>();
	}
	
	public void clearLists() {
		wordBigramPredictability.clear();
		wordTrigramPredictability.clear();
	}
	
	@Override
	public Collection<Feature> extract(DataNode data) {
		clearLists();
		for (Answer a : data) {
			getWordPredictability(a);
		}
		
		LinkedList<Feature> output = new LinkedList<Feature>();
		output.add(new Feature("Word Bigram Predictability",wordBigramPredictability));
		output.add(new Feature("Word Trigram Predictability",wordTrigramPredictability));
//		for (Feature f : output) System.out.println(f.toTemplate());
		
		return output;
	}
		
	public void getWordPredictability(Answer answer) {
		ArrayList<KSE> kseList = new ArrayList<KSE>(KSE.parseSessionToKSE(answer.getKeyStrokes()));
		ArrayList<KSE> visibleKSEs = KSE.parseToVisibleTextKSEs(kseList);
		
		EventList<KeyStroke> keyStrokeList = answer.getKeyStrokeList();
		String visibleText = keyStrokeList.toVisibleTextString();
	
		
		ArrayList<TokenExtended> tokens = extender.generateExtendedTokens(visibleText);

		for (int i = 0; i < tokens.size()-2; i++) {
			String gram1 = tokens.get(i).token;
			String gram2 = tokens.get(i+1).token;
			String gram3 = tokens.get(i+2).token;
			
			double bigramProbability = wordBigramModel.getBigramProbability(gram1,gram2);
			KSE gram2Begin = visibleKSEs.get(tokens.get(i+1).getTokenSpan().begin);
			double bigramPause = gram2Begin.getM_pauseMs()/1000.0;
			double bigramPredictability = bigramPause/(-1.0*Math.log(bigramProbability));
			wordBigramPredictability.add(bigramPredictability);
				
			double trigramProbability = wordTrigramModel.getTrigramProbability(gram1,gram2,gram3);
			KSE gram3Begin = visibleKSEs.get(tokens.get(i+2).getTokenSpan().begin);
			double trigramPause = gram3Begin.getM_pauseMs()/1000.0;
			double trigramPredictability = trigramPause/(-1.0*Math.log(trigramProbability));
			wordTrigramPredictability.add(trigramPredictability);
		}
		
		//one more for last bigram
		int length = tokens.size();
		String gram1 = tokens.get(length-2).token;
		String gram2 = tokens.get(length-1).token;
		double bigramProbability = wordBigramModel.getBigramProbability(gram1,gram2);
		KSE gram2Begin = visibleKSEs.get(tokens.get(length-1).getTokenSpan().begin);
		double bigramPause = gram2Begin.getM_pauseMs()/1000.0;
		double bigramPredictability = bigramPause/bigramProbability;
		wordBigramPredictability.add(bigramPredictability);
	}
		
	public Collection<Double> getWordBigramPredictabilityList() {
		return wordBigramPredictability;
	}
	
	public Collection<Double> getWordTrigramPredictabilityList() {
		return wordTrigramPredictability;
	}
	
	@Override
	public String getName() {
		return "Word Predictability";
	}

}
