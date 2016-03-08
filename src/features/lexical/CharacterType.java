/**
 * 
 */
package features.lexical;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import features.pause.KSE;
import keystroke.*;

/**
 * @author agoodkind
 * Frequency of different types of characters
 */
public class CharacterType implements ExtractionModule {

	Collection<Feature> output;
	Collection<Double> alphaCharRatio;
	Collection<Double> numberCharRatio;
	Collection<Double> uppercaseRatio;
	Collection<Double> spacebarRatio;
	Collection<Double> vowelRatio;
	
	public CharacterType() {
		output = new LinkedList<Feature>();
		alphaCharRatio = new LinkedList<Double>();
		numberCharRatio = new LinkedList<Double>();
		uppercaseRatio = new LinkedList<Double>();
		spacebarRatio = new LinkedList<Double>();
		vowelRatio = new LinkedList<Double>();
	}
	
	public void clearLists() {
		output.clear();
		alphaCharRatio.clear();
		numberCharRatio.clear();
		uppercaseRatio.clear();
		spacebarRatio.clear();
		vowelRatio.clear();
	}
	
	@Override
	public Collection<Feature> extract(DataNode data) {
		clearLists();
		
		for (Answer a: data) {
			//create and populate keyPress array
			LinkedList<KSE> keyPressKSEs = new LinkedList<KSE>();
			Collection<KSE> allKSEs = KSE.parseSessionToKSE(a.getKeyStrokes());
			for (KSE kse : allKSEs) {
				if (kse.isKeyPress())
					keyPressKSEs.add(kse);
			}
			//compute specific ratios
			String finalText = KeyStroke.keyStrokesToFinalText(a.getKeyStrokeList());
			alphaCharRatio.add(getAlphaCharRatio(keyPressKSEs));
			numberCharRatio.add(getNumberCharRatio(keyPressKSEs));
			uppercaseRatio.add(getUppercaseRatio(finalText));
			spacebarRatio.add(getSpacebarRatio(keyPressKSEs));
			vowelRatio.add(getVowelRatio(finalText));
		}
		output.add(new Feature("Alpha_Ratio",alphaCharRatio));
		output.add(new Feature("Number_Ratio",numberCharRatio));
		output.add(new Feature("Uppercase_Ratio",uppercaseRatio));
		output.add(new Feature("Spacebar_Ratio",spacebarRatio));
		output.add(new Feature("Vowel_Ratio",vowelRatio));
	    // for (Feature f : output) System.out.println(f.toTemplate());
		return output;
	}
	
	// ratio of alphabetical characters to total kestrokes
	public double getAlphaCharRatio(LinkedList<KSE> kseArray) {
		double alphaRatio = 0.0;
		int totalKSECount = 0;
		int alphaCharCount = 0;
		for (KSE kse : kseArray) {
			totalKSECount++;
			if (kse.isAlpha())
				alphaCharCount++;
		}
		alphaRatio = (alphaCharCount * 1.) / totalKSECount; 
		return alphaRatio;
	}
	
	// ratio of numeric characters to total keystrokes
	public double getNumberCharRatio(LinkedList<KSE> kseArray) {
		double numberRatio = 0.0;
		int totalKSECount = 0;
		int numberCharCount = 0;
		for (KSE kse : kseArray) {
			totalKSECount++;
			if (kse.isNumeric())
				numberCharCount++;
		}
		numberRatio = (numberCharCount * 1.) / totalKSECount; 
		return numberRatio;
	}
	
	// ratio of uppercase characters to total letterss
	public double getUppercaseRatio(String finalText) {
		char[] textArray = finalText.toCharArray();
		double uppercaseRatio = 0.0;
		int alphaChars = 0;
		int uppercaseChars = 0;
		for (char c : textArray) {
			if (Character.isLetter(c)) {
				alphaChars++;
				if (Character.isUpperCase(c))
					uppercaseChars++;
			}
		}
		uppercaseRatio = (uppercaseChars *1.) / alphaChars;
		
		return uppercaseRatio;
	}
	
	// ratio of space bar depressions to total keystrokes
	public double getSpacebarRatio(LinkedList<KSE> kseArray) {
		double spacebarRatio = 0.0;
		int totalKSECount = 0;
		int spacebarCount = 0;
		for (KSE kse : kseArray) {
			totalKSECount++;
			if (kse.isSpace())
				spacebarCount++;
		}
		spacebarRatio = (spacebarCount * 1.) / totalKSECount; 
		return spacebarRatio;
	}
	
	// ratio of vowels to alphabetical characters
	public double getVowelRatio(String finalText) {
		char[] textArray = finalText.toCharArray();
		double vowelRatio = 0.0;
		ArrayList<Character> vowelArray = new ArrayList<Character>(Arrays.asList('a','e','i','o','u','A','E','I','O','U'));
		int alphaChars = 0;
		int vowelChars = 0;
		for (char c : textArray) {
			if (Character.isLetter(c)) {
				alphaChars++;
				if (vowelArray.contains(c))
					vowelChars++;
			}
		}
		vowelRatio = (vowelChars *1.) / alphaChars;
		
		return vowelRatio;
	}

	@Override
	public String getName() {
		return "Char_Type";
	}

}
