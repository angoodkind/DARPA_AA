package features.lexical;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;

public class Familiarity implements ExtractionModule {

	private LinkedList<Integer> contraction_possession_count;
	private LinkedList<Integer> phonetic_irregularity_count;
	
	@Override
	public Collection<Feature> extract(DataNode data) {
		
		contraction_possession_count = new LinkedList<Integer>();
		phonetic_irregularity_count = new LinkedList<Integer>();
		
		for (Answer a : data) {
			
			String final_text = a.getFinalText();
			
			String p1 = "\\w+'s|\\w+'t|\\w+'d|\\w+'ve";
			Pattern pattern1 = Pattern.compile(p1);
			Matcher matcher1 = pattern1.matcher(final_text);
			int counter1 = 0;
			while (matcher1.find()) { counter1 += 1; }
			contraction_possession_count.add(counter1);
			
			String p2 = "(\\w+)(ou|ough|ew)(\\w+)?";
			Pattern pattern2 = Pattern.compile(p2);
			Matcher matcher2 = pattern2.matcher(final_text);
			int counter2 = 0;
			while (matcher2.find()) { counter2 += 1; }
			phonetic_irregularity_count.add(counter2);
		}
		
		LinkedList<Feature> output = new LinkedList<Feature>();
		
		output.add(new Feature("contraction_possession_count", contraction_possession_count));
		output.add(new Feature("phonetic_irregularity_count", phonetic_irregularity_count));
		
		return output;
	}

	@Override
	public String getName() {
		return "Familiarity";
	}
}
