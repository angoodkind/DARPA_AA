/**
 * 
 */
package features.lexical;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

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
public class ConsonantTiming implements ExtractionModule {
	
	Collection<Feature> output;
	Collection<Long> commonConsonantTiming;
	Collection<Double> commonConsonantTimingRatio;
	Collection<Long> rareConsonantTiming;
	Collection<Double> rareConsonantTimingRatio;
	Collection<Double> commonToRareConsonantTimingRatio;
	
	public ConsonantTiming() {
		output = new LinkedList<Feature>();
		commonConsonantTiming = new LinkedList<Long>();				// h,n,r,s,t
		commonConsonantTimingRatio = new LinkedList<Double>();
		rareConsonantTiming = new LinkedList<Long>();				// j,kt,q,v,x,z
		rareConsonantTimingRatio = new LinkedList<Double>();
		commonToRareConsonantTimingRatio = new LinkedList<Double>();
	}
	
	public void clearLists() {
		output.clear();
		commonConsonantTiming.clear();
		commonConsonantTimingRatio.clear();
		rareConsonantTiming.clear();
		rareConsonantTimingRatio.clear();
		commonToRareConsonantTimingRatio.clear();
	}

	@Override
	public Collection<Feature> extract(DataNode data) {
		
		clearLists();
		
		for (Answer a: data) {
			//create and populate keyPress array
			LinkedList<KSE> keyPressKSEs = new LinkedList<KSE>();
			Collection<KSE> allKSEs = KSE.parseSessionToKSE(a.getKeyStrokes());
			for (KSE kse : allKSEs)
				if (kse.isKeyPress())
					keyPressKSEs.add(kse);
			
			/////////////////////////////////////////
			long cct = getCommonConsonantTiming(keyPressKSEs);
			if (cct != -1) {
				commonConsonantTiming.add(cct);
			}
			////////////////////////////////////////
			
			/////////////////////////////////////////
			long cct2 = getCommonConsonantTiming(keyPressKSEs);
			if (cct2 != -1) {
				commonConsonantTimingRatio.add(getCommonConsonantTimingRatio(keyPressKSEs));
			}
			////////////////////////////////////////
			
			long rareTime = getRareConsonantTiming(keyPressKSEs);
			if (rareTime != 0.0) {
				rareConsonantTiming.add(getRareConsonantTiming(keyPressKSEs));
				rareConsonantTimingRatio.add(getRareConsonantTimingRatio(keyPressKSEs));
				commonToRareConsonantTimingRatio.add(getCommonToRareConsonantTimingRatio(keyPressKSEs));
			}
		}
		output.add(new Feature("Common_Consonant_Timing",commonConsonantTiming));
		output.add(new Feature("Common_Consonant_Timing_Ratio",commonConsonantTimingRatio));
		output.add(new Feature("Rare_Consonant_Timing",rareConsonantTiming));
		output.add(new Feature("Rare_Consonant_Timing_Ratio",rareConsonantTimingRatio));
		output.add(new Feature("Common_To_Rare_Consonant_Timing_Ratio",commonToRareConsonantTimingRatio));
//	    for (Feature f : output) System.out.println(f.toTemplate());
		return output;
	}
	
	// mean time between pressing any key and then pressing a common consonant key
	public long getCommonConsonantTiming(LinkedList<KSE> kseArray) {
		List<Character> commonConsonants = Arrays.asList('h','n','r','s','t','H','N','R','S','T');
		long totalTimingIntervals = 0;
		int commonConsonantCount = 0;
		long meanTimingInterval;
		
		for (int i = 1; i < kseArray.size(); i++) {
			if (commonConsonants.contains(kseArray.get(i).getKeyChar())) {
				long prevKeyStrokeTime = kseArray.get(i-1).getM_pauseMs();			// timing of preceding keystroke
				totalTimingIntervals += prevKeyStrokeTime;
				commonConsonantCount++;
			}
		}
		
		///////////////////////////////////////////////
		if (commonConsonantCount > 0) {
			meanTimingInterval = totalTimingIntervals/commonConsonantCount;
			return meanTimingInterval;
		}
		else
			return -1;
		///////////////////////////////////////////////
	}
	
	// ratio of pause before common consonant to average pause
	public double getCommonConsonantTimingRatio(LinkedList<KSE> kseArray) {
		int keyStrokeCount = kseArray.size();
		long totalLagTime = 0;
		long averageLag;
		long averageCommonConsonantLag = getCommonConsonantTiming(kseArray);
		double commonConsonantTimingRatio;
		
		for (KSE kse : kseArray)
			totalLagTime += kse.getM_pauseMs();
		
		///////////////////////////////////////////////
		if (keyStrokeCount > 0) {
			averageLag = totalLagTime/keyStrokeCount;
			commonConsonantTimingRatio = (averageCommonConsonantLag * 1.)/averageLag;
			return commonConsonantTimingRatio;
		}
		else
		return -1;
		///////////////////////////////////////////////
	}
	
	// mean time between pressing any key and then pressing a rare consonant key
	public long getRareConsonantTiming(LinkedList<KSE> kseArray) {
		List<Character> rareConsonants = Arrays.asList('j','k','q','v','x','z','J','K','Q','V','X','Z');
		long totalTimingIntervals = 0;
		int rareConsonantCount = 0;
		long meanTimingInterval;
		
		for (int i = 1; i < kseArray.size(); i++) {
			if (rareConsonants.contains(kseArray.get(i).getKeyChar())) {
				long prevKeyStrokeTime = kseArray.get(i-1).getM_pauseMs();			// timing of preceding keystroke
				totalTimingIntervals += prevKeyStrokeTime;
				rareConsonantCount++;
			}
		}
		
		// to avoid divide by zero errors
		if (rareConsonantCount == 0)
				return 0;
		else {
			meanTimingInterval = totalTimingIntervals/rareConsonantCount;
			return meanTimingInterval;
		}
	}
	
	// ratio of pause before rare consonant to average pause
	public double getRareConsonantTimingRatio(LinkedList<KSE> kseArray) {
		int keyStrokeCount = kseArray.size();
		long totalLagTime = 0;
		long averageLag;
		long averageRareConsonantLag = getRareConsonantTiming(kseArray);
		double rareConsonantTimingRatio;
		
		for (KSE kse : kseArray)
			totalLagTime += kse.getM_pauseMs();
		
		// if rare consonants are zero, this function will not be called from above, 
		// but this keeps it safer 
		averageLag = totalLagTime/keyStrokeCount;
		if (averageRareConsonantLag == 0) {
			rareConsonantTimingRatio = 0;
		}
		else
			rareConsonantTimingRatio = (averageRareConsonantLag * 1.)/averageLag;
		
		return rareConsonantTimingRatio;
	}
	
	//get ratio of common consonant timing to rare consonant timing
	public double getCommonToRareConsonantTimingRatio(LinkedList<KSE> kseArray) {
		long commonConsonantTime = getCommonConsonantTiming(kseArray);
		long rareConsonantTime = getRareConsonantTiming(kseArray);
		double commonToRareRatio = (commonConsonantTime * 1.) / rareConsonantTime;
		return commonToRareRatio;
	}

	@Override
	public String getName() {
		return "Consonant_Timing";
	}
	
	

}
