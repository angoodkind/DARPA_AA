package features.predictability;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import extractors.lexical.POS_Extractor;
import extractors.lexical.TokenExtender;
import extractors.lexical.Tokenize;
import features.pause.KSE;
import mwe.TokenExtended;
import ngrammodel.Bigram;

public class POS_Predictability extends Predictability implements ExtractionModule {

	private static final String modelName = "S1PCFG";
	private static final String gramType = "pos";
	private HashMap<Bigram,ArrayList<Double>> posPredictabilizedPauseMap;
	private HashMap<String,ArrayList<Double>> functionContentPauseMap;
	
	public POS_Predictability() {
		super(modelName,gramType);
	}
	
	public void generatePOSMap() {
		for (Bigram bigram : wordBigramModel.getBigramCountsMap().keySet())
			posPredictabilizedPauseMap.put(bigram, new ArrayList<Double>());
	}
	
	public void generateFunctionContentMap() {
		functionContentPauseMap.put("FuncWord", new ArrayList<Double>());
		functionContentPauseMap.put("ContWord", new ArrayList<Double>());
	}

	@Override
	public Collection<Feature> extract(DataNode data) {
		
		for (Answer a : data) {
			
			int userID = data.getUserID();
			posPredictabilizedPauseMap = new HashMap<Bigram,ArrayList<Double>>();
			functionContentPauseMap = new HashMap<String,ArrayList<Double>>();
			generatePOSMap();
			generateFunctionContentMap();
			//generate ArrayList of Timing Tokens
			ArrayList<TimingToken> tokenList = Predictability.createTimingTokenList(a);
			//iterate through list of tokens
			for (int i = 0; i < tokenList.size()-1; i++) {
				String pos1 = tokenList.get(i).POS;
				String pos2 = tokenList.get(i+1).POS;
				String[] bigramStr = {pos1,pos2};
				long pause = tokenList.get(i+1).pause;
				Bigram bigram = new Bigram(pos1,pos2);
				double predictabilizedPause = getWordNgramPredictability(bigramStr,pause,"preWord",userID);
				if (posPredictabilizedPauseMap.containsKey(bigram))
					posPredictabilizedPauseMap.get(bigram).add(predictabilizedPause);
				if (POS_Extractor.isContentPOS(pos2))
					functionContentPauseMap.get("ContWord").add(predictabilizedPause);
				if (POS_Extractor.isFunctionPOS(pos2))
					functionContentPauseMap.get("FuncWord").add(predictabilizedPause);
			}
		}
		
		LinkedList<Feature> output = new LinkedList<Feature>();
		for (Bigram bigram : posPredictabilizedPauseMap.keySet()) {
			String bigramStr = bigram.getGram1()+"-"+bigram.getGram2();
			output.add(new Feature("Predict_"+bigramStr,posPredictabilizedPauseMap.get(bigram)));
		}
		for (String s : functionContentPauseMap.keySet())
			output.add(new Feature("Predict_"+s,functionContentPauseMap.get(s)));
		
//		for (Feature f : output) System.out.println(f.toTemplate());		
		return output;
	}

	@Override
	public String getName() {
		return "POS Predictability";
	} 

}
