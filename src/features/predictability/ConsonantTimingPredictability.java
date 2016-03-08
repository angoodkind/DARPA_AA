/**
 * 
 */
package features.predictability;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import features.pause.KSE;

/**
 * @author agoodkind
 * Analyzes the rate at which a user types common consonants and rare consonants
 *
 */
public class ConsonantTimingPredictability extends Predictability implements ExtractionModule {
	
	private static final String modelName = "SESSION1";
	private static final String gramType = "keystroke";
	Collection<Feature> output;
	Collection<Double> commonConsonantTimingBigram;
	Collection<Double> commonConsonantTimingTrigram;
	Collection<Double> commonConsonantTimingBigramNormed;
	Collection<Double> commonConsonantTimingTrigramNormed;
	Collection<Double> rareConsonantTimingBigram;
	Collection<Double> rareConsonantTimingTrigram;
	Collection<Double> rareConsonantTimingBigramNormed;
	Collection<Double> rareConsonantTimingTrigramNormed;
	double commonToRareConsonantTimingRatioBigram;
	double commonToRareConsonantTimingRatioTrigram;
	double commonToRareConsonantTimingRatioBigramNormed;
	double commonToRareConsonantTimingRatioTrigramNormed;
	
	public ConsonantTimingPredictability() {
		super(modelName,gramType);
		output = new LinkedList<Feature>();
		commonConsonantTimingBigram = new ArrayList<Double>();
		commonConsonantTimingTrigram = new ArrayList<Double>();
		commonConsonantTimingBigramNormed = new ArrayList<Double>();
		commonConsonantTimingTrigramNormed = new ArrayList<Double>();
		rareConsonantTimingBigram = new ArrayList<Double>();
		rareConsonantTimingTrigram = new ArrayList<Double>();
		rareConsonantTimingBigramNormed = new ArrayList<Double>();
		rareConsonantTimingTrigramNormed = new ArrayList<Double>();
		commonToRareConsonantTimingRatioBigram = 0.0;
		commonToRareConsonantTimingRatioTrigram = 0.0;
		commonToRareConsonantTimingRatioBigramNormed = 0.0;
		commonToRareConsonantTimingRatioTrigramNormed = 0.0;
	}
	
	public void clearLists() {
		output.clear();
		commonConsonantTimingBigram.clear();
		commonConsonantTimingTrigram.clear();
		commonConsonantTimingBigramNormed.clear();
		commonConsonantTimingTrigramNormed.clear();
		rareConsonantTimingBigram.clear();
		rareConsonantTimingTrigram.clear();
		rareConsonantTimingBigramNormed.clear();
		rareConsonantTimingTrigramNormed.clear();
	}

	@Override
	public Collection<Feature> extract(DataNode data) {
		
		clearLists();
		
		for (Answer a: data) {
			ArrayList<KSE> keyPresses = generateKSEKeyPresses(a.getKeyStrokes());
			consonantIntervalTiming(keyPresses,data.getUserID());
		}
		output.add(new Feature("Common Consonant Timing Bigram",commonConsonantTimingBigram));
		output.add(new Feature("Common Consonant Timing Trigram",commonConsonantTimingTrigram));
		output.add(new Feature("Rare Consonant Timing Bigram",rareConsonantTimingBigram));
		output.add(new Feature("Rare Consonant Timing Trigram",rareConsonantTimingTrigram));
		output.add(new Feature("Common Consonant Timing Bigram Normed",commonConsonantTimingBigramNormed));
		output.add(new Feature("Common Consonant Timing Trigram Normed",commonConsonantTimingTrigramNormed));
		output.add(new Feature("Rare Consonant Timing Bigram Normed",rareConsonantTimingBigramNormed));
		output.add(new Feature("Rare Consonant Timing Trigram Normed",rareConsonantTimingTrigramNormed));
		output.add(new Feature("Common/Rare Timing Ratio Bigram",commonToRareConsonantTimingRatioBigram));
		output.add(new Feature("Common/Rare Timing Ratio Trigram",commonToRareConsonantTimingRatioTrigram));
		output.add(new Feature("Common/Rare Timing Ratio Bigram Normed",commonToRareConsonantTimingRatioBigramNormed));
		output.add(new Feature("Common/Rare Timing Ratio Trigram Normed",commonToRareConsonantTimingRatioTrigramNormed));
//		for (Feature f : output) System.out.println(f.toTemplate());
		return output;
	}
	
	/**
	 * Timing interval normalized for predictability
	 * @param keyPresses
	 * @return
	 */
	public void consonantIntervalTiming(ArrayList<KSE> keyPresses, int userID) {
		Set<Character> commonConsonants = new TreeSet<Character>(Arrays.asList('h','n','r','s','t','H','N','R','S','T'));
		Set<Character> rareConsonants = new TreeSet<Character>(Arrays.asList('j','k','q','v','x','z','J','K','Q','V','X','Z'));
		for (int i = 2; i < keyPresses.size(); i++) {
			
			KSE kse1 = keyPresses.get(i-2);
			KSE kse2 = keyPresses.get(i-1);
			KSE kse3 = keyPresses.get(i);
			if (commonConsonants.contains(keyPresses.get(i).getKeyChar())) {
				// 1st bigram, which is not a trigram
				if (i==2) {
					KSE[] bigram = {kse1,kse2};
					double bigramPredictabilized;
					if (bigram[bigram.length-2].isSpace())
						bigramPredictabilized = getKeystrokeNgramPredictability(bigram,"preWord",userID);
					else
						bigramPredictabilized = getKeystrokeNgramPredictability(bigram,"intraWord",userID);
					commonConsonantTimingBigram.add(bigramPredictabilized);
				}
				KSE[] bigram = {kse2,kse3};
				KSE[] trigram = {kse1,kse2,kse3};
				double bigramPredictabilized;
				if (bigram[bigram.length-2].isSpace())
					bigramPredictabilized = getKeystrokeNgramPredictability(bigram,"preWord",userID);
				else
					bigramPredictabilized = getKeystrokeNgramPredictability(bigram,"intraWord",userID);
				commonConsonantTimingBigram.add(bigramPredictabilized);
				double trigramPredictabilized;
				if (trigram[trigram.length-2].isSpace())
					trigramPredictabilized = getKeystrokeNgramPredictability(trigram,"preWord",userID);
				else
					trigramPredictabilized = getKeystrokeNgramPredictability(trigram,"intraWord",userID);
				commonConsonantTimingTrigram.add(trigramPredictabilized);
			}
			else if (rareConsonants.contains(keyPresses.get(i).getKeyChar())) {
				if (i==2) {
					KSE[] bigram = {kse1,kse2};
					double bigramPredictabilized;
					if (bigram[bigram.length-2].isSpace())
						bigramPredictabilized = getKeystrokeNgramPredictability(bigram,"preWord",userID);
					else
						bigramPredictabilized = getKeystrokeNgramPredictability(bigram,"intraWord",userID);
					
					rareConsonantTimingBigram.add(bigramPredictabilized);
				}
				KSE[] bigram = {kse2,kse3};
				KSE[] trigram = {kse1,kse2,kse3};
				double bigramPredictabilized;
				if (bigram[bigram.length-2].isSpace())
					bigramPredictabilized = getKeystrokeNgramPredictability(bigram,"preWord",userID);
				else
					bigramPredictabilized = getKeystrokeNgramPredictability(bigram,"intraWord",userID);
				rareConsonantTimingBigram.add(bigramPredictabilized);
				
				double trigramPredictabilized;
				if (trigram[trigram.length-2].isSpace())
					trigramPredictabilized = getKeystrokeNgramPredictability(trigram,"preWord",userID);
				else
					trigramPredictabilized = getKeystrokeNgramPredictability(trigram,"intraWord",userID);
				
				rareConsonantTimingTrigram.add(trigramPredictabilized);
			}
		}
		consonantNormalizedTiming(keyPresses);
	}
	
	/**
	 * Called from within consonantTiming, to guarantee that
	 * timing arrays are already populated
	 * @param kseList
	 */
	public void consonantNormalizedTiming(ArrayList<KSE> kseList) {
		double mean = meanPause(kseList);
		for (double pause : commonConsonantTimingBigram)
			commonConsonantTimingBigramNormed.add(pause/mean);
		for (double pause : commonConsonantTimingTrigram)
			commonConsonantTimingTrigramNormed.add(pause/mean);
		for (double pause : rareConsonantTimingBigram)
			rareConsonantTimingBigramNormed.add(pause/mean);
		for (double pause : rareConsonantTimingTrigram)
			rareConsonantTimingTrigramNormed.add(pause/mean);
		commonToRareConsonantTimingRatio();
	}
	//get ratio of common consonant timing to rare consonant timing
	public void commonToRareConsonantTimingRatio() {
		commonToRareConsonantTimingRatioBigram = mean(commonConsonantTimingBigram)/mean(rareConsonantTimingBigram);
		commonToRareConsonantTimingRatioTrigram = mean(commonConsonantTimingTrigram)/mean(rareConsonantTimingTrigram);;
		commonToRareConsonantTimingRatioBigramNormed = mean(commonConsonantTimingBigramNormed)/mean(rareConsonantTimingBigramNormed);;
		commonToRareConsonantTimingRatioTrigramNormed = mean(commonConsonantTimingTrigramNormed)/mean(rareConsonantTimingTrigramNormed);;
	}

	@Override
	public String getName() {
		return "Consonant Timing Predictabilized";
	}
	
	

}
