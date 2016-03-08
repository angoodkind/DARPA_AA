package features.lexical;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;

public class RareCharacterType implements ExtractionModule {

	private LinkedList<Integer> rare_punctuation_count;
	private LinkedList<Integer> rare_character_count;
	
	@Override
	public Collection<Feature> extract(DataNode data) {
		
		rare_punctuation_count = new LinkedList<Integer>();
		rare_character_count = new LinkedList<Integer>();
		
		for (Answer a : data) {
			
			String final_text = a.getFinalText();
			
			String p1 = "(\\:|\\;|\\?|\\!|\\\"(\\w+?)\\\")";
			Pattern pattern1 = Pattern.compile(p1);
			Matcher matcher1 = pattern1.matcher(final_text);
			int counter1 = 0;
			while (matcher1.find()) { counter1 += 1; }
			rare_punctuation_count .add(counter1);
			
			String p2 = "\\[(\\w+?)\\]|\\((\\w+?)\\)|\\{(\\w+?)\\}|\\&\\*\\~\\/";
			Pattern pattern2 = Pattern.compile(p2);
			Matcher matcher2 = pattern2.matcher(final_text);
			int counter2 = 0;
			while (matcher2.find()) { counter2 += 1; }
			rare_character_count.add(counter2);
		}
		
		LinkedList<Feature> output = new LinkedList<Feature>();
		
		output.add(new Feature("rare_punctuation_count", rare_punctuation_count));
		output.add(new Feature("rare_character_count", rare_character_count));		
		
		// 
		return output;
	}

	@Override
	public String getName() {
		return "Character Type";
	}

}
