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
 *
 */
public class ConsonantFrequency implements ExtractionModule {

	Collection<Feature> output;
	Collection<Double> commonConsonantRatio;	//h,n,r,s,t
	Collection<Double> rareConsonantRatio;		//j,kt,q,v,x,z
	
	public ConsonantFrequency() {
		output = new LinkedList<Feature>();
		commonConsonantRatio = new LinkedList<Double>();
		rareConsonantRatio = new LinkedList<Double>();
	}
	
	public void clearLists() {
		output.clear();
		commonConsonantRatio.clear();
		rareConsonantRatio.clear();
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
			
			commonConsonantRatio.add(getCommonConsonantRatio(keyPressKSEs));
			rareConsonantRatio.add(getRareConsonantRatio(keyPressKSEs));
		}
		output.add(new Feature("Common_Consonant_Ratio",commonConsonantRatio));
		output.add(new Feature("Rare_Consonant_Ratio",rareConsonantRatio));
//	    for (Feature f : output) System.out.println(f.toTemplate());
		return output;
	}

	// extract the ratio of common consonants / alpha characters
	public double getCommonConsonantRatio(LinkedList<KSE> kseArray) {
		List<Character> commonConsonants = Arrays.asList('h','n','r','s','t','H','N','R','S','T');
		double commonConsonantRatio = 0.0;
		int commonConsonantCount = 0;
		int alphaCharCount = 0;
		for (KSE kse : kseArray) {
			if (kse.isAlpha()) {
				alphaCharCount++;
				if (commonConsonants.contains(kse.getKeyChar())) {
					commonConsonantCount++;
				}
			}
		}
		commonConsonantRatio = (commonConsonantCount * 1.) / alphaCharCount; 
		return commonConsonantRatio;
	}
	
	// extract the ratio of rare consonants / alpha characters
	public double getRareConsonantRatio(LinkedList<KSE> kseArray) {
		List<Character> rareConsonants = Arrays.asList('j','k','q','v','x','z','J','K','Q','V','X','Z');
		double rareConsonantRatio = 0.0;
		int rareConsonantCount = 0;
		int alphaCharCount = 0;
		for (KSE kse : kseArray) {
			if (kse.isAlpha()) {
				alphaCharCount++;
				if (rareConsonants.contains(kse.getKeyChar()))
					rareConsonantCount++;
			}
		}
		rareConsonantRatio = (rareConsonantCount * 1.) / alphaCharCount; 
		return rareConsonantRatio;
	}
	
	@Override
	public String getName() {
		return "Consonant_Frequency";
	}

}
