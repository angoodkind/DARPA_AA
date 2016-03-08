package output.bursts;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import extractors.data.Feature;
import features.bursts.Burst;
import features.bursts.BurstType_PR;
import output.util.ConvertGenerics;

public class pr_burst_postprocess {
	
public static LinkedList<Feature>  extract (List<Feature> output_list, ArrayList<Burst> BurstArray) {
		
		LinkedList<Feature> output = new LinkedList<Feature>();
		
		for (int f = 0; f < output_list.size(); f++) {
			Feature feature = output_list.get(f);
			
			//get feature with the name "Lexical_Density"
			switch (feature.getFeatureName()) {
			
				case "Lexical_Density": {
					LinkedList<Double> normalized = new LinkedList<Double>();
					LinkedList<Double> normalized2 = new LinkedList<Double>();
					LinkedList<Double> normalized3 = new LinkedList<Double>();
					
					int counter = -1;
					Object[] lexDensities = feature.getFeatureValues().toArray();
														
					for (Burst b: BurstArray) {
						
						BurstType_PR prb = (BurstType_PR)b;
						
						Double val = (Double)lexDensities[++counter];
						Double result = (val.doubleValue() * 1000) / (double) prb.burstTime();
						normalized.add(result);
						Double result2 = (val.doubleValue() * 1000) / (double) prb.GetPauseBefore();
						normalized2.add(result2);
						Double result3 = (val.doubleValue() * 1000) / (double) prb.GetPauseAfter();
						normalized3.add(result3);
					}
					output.add(new Feature("normalized_Lexical_Density", normalized));
					output.add(new Feature("normalized_Lexical_Density_Before_Pause", normalized2));
					output.add(new Feature("normalized_Lexical_Density_After_Pause", normalized3));
					
					break;
				}
				
				case "Type-Token_Ratio": {
					LinkedList<Double> token_normalized = new LinkedList<Double>();
					LinkedList<Double> token_normalized2 = new LinkedList<Double>();
					LinkedList<Double> token_normalized3 = new LinkedList<Double>();
					
					int counter = -1;
					Object[] type_token = feature.getFeatureValues().toArray();
					
					
					for (Burst b: BurstArray) {
						
						BurstType_PR prb = (BurstType_PR)b;
						
						Double val = (Double)type_token[++counter];
						Double result = (val.doubleValue() * 1000) / (double) prb.burstTime();
						token_normalized.add(result);
						Double result2 = (val.doubleValue() * 1000) / (double) prb.GetPauseBefore();
						token_normalized2.add(result2);
						Double result3 = (val.doubleValue() * 1000) / (double) prb.GetPauseAfter();
						token_normalized3.add(result3);
					}
					output.add(new Feature("type_token_burst_duration_ratio", token_normalized));
					output.add(new Feature("type_token_burst_duration_ratio_Before_Pause", token_normalized2));
					output.add(new Feature("type_token_burst_duration_ratio_After_Pause", token_normalized3));
					
					break;
				}
				
				case "Noun_Counts": {
					LinkedList<Double> nouns_normalized = new LinkedList<Double>();
					LinkedList<Double> nouns_normalized2 = new LinkedList<Double>();
					LinkedList<Double> nouns_normalized3 = new LinkedList<Double>();
					
					int counter = -1;
					
					if (!feature.isEmpty()) {
						ConvertGenerics cg = new ConvertGenerics(feature.getFeatureValues());
						Double[] double_values = cg.ToDoubleArray();
						
						for (Burst b: BurstArray) {
							
							BurstType_PR prb = (BurstType_PR)b;
							
							Double val = double_values[++counter];
							Double result = (val.doubleValue() * 1000) / (double) prb.burstTime();
							nouns_normalized.add(result);
							Double result2 = (val.doubleValue() * 1000) / (double) prb.GetPauseBefore();
							nouns_normalized2.add(result2);
							Double result3 = (val.doubleValue() * 1000) / (double) prb.GetPauseAfter();
							nouns_normalized3.add(result3);
						}
					}
					
					output.add(new Feature("nouns_burst_duration_ratio", nouns_normalized));
					output.add(new Feature("nouns_burst_duration_ratio_Before_Pause", nouns_normalized2));
					output.add(new Feature("nouns_burst_duration_ratio_After_Pause", nouns_normalized3));
					
					break;
				}
				
				case "Verb_Counts": {
					LinkedList<Double> verbs_normalized = new LinkedList<Double>();
					LinkedList<Double> verbs_normalized2 = new LinkedList<Double>();
					LinkedList<Double> verbs_normalized3 = new LinkedList<Double>();
					
					int counter = -1;

					if (!feature.isEmpty()) {
						ConvertGenerics cg = new ConvertGenerics(feature.getFeatureValues());
						Double[] double_values = cg.ToDoubleArray();
						
						
						for (Burst b: BurstArray) {
							
							BurstType_PR prb = (BurstType_PR)b;
							
							Double val = double_values[++counter];
							Double result = (val.doubleValue() * 1000) / (double) prb.burstTime();
							verbs_normalized.add(result);
							Double result2 = (val.doubleValue() * 1000) / (double) prb.GetPauseBefore();
							verbs_normalized2.add(result2);
							Double result3 = (val.doubleValue() * 1000) / (double) prb.GetPauseAfter();
							verbs_normalized3.add(result3);
						}
					}
					
					output.add(new Feature("verbs_burst_duration_ratio", verbs_normalized));
					output.add(new Feature("verbs_burst_duration_ratio_Before_Pause", verbs_normalized2));
					output.add(new Feature("verbs_burst_duration_ratio_After_Pause", verbs_normalized3));
					
					break;
				}
				
				
				case "Modifier_Counts": {
					LinkedList<Double> modifiers_normalized = new LinkedList<Double>();
					LinkedList<Double> modifiers_normalized2 = new LinkedList<Double>();
					LinkedList<Double> modifiers_normalized3 = new LinkedList<Double>();
					
					int counter = -1;
					
					if (!feature.isEmpty()) {
						ConvertGenerics cg = new ConvertGenerics(feature.getFeatureValues());
						Double[] double_values = cg.ToDoubleArray();
						
						
						for (Burst b: BurstArray) {
							
							BurstType_PR prb = (BurstType_PR)b;
							
							Double val = double_values[++counter];
							Double result = (val.doubleValue() * 1000) / (double) prb.burstTime();
							modifiers_normalized.add(result);
							Double result2 = (val.doubleValue() * 1000) / (double) prb.GetPauseBefore();
							modifiers_normalized2.add(result2);
							Double result3 = (val.doubleValue() * 1000) / (double) prb.GetPauseAfter();
							modifiers_normalized3.add(result3);
						}
					}
					
					output.add(new Feature("modifiers_burst_duration_ratio", modifiers_normalized));
					output.add(new Feature("modifiers_burst_duration_ratio_Before_Pause", modifiers_normalized2));
					output.add(new Feature("modifiers_burst_duration_ratio_After_Pause", modifiers_normalized3));
					
					break;
				}
				
				case "Modal_Counts": {
					LinkedList<Double> modal_normalized = new LinkedList<Double>();
					LinkedList<Double> modal_normalized2 = new LinkedList<Double>();
					LinkedList<Double> modal_normalized3 = new LinkedList<Double>();
					
					int counter = -1;

					if (!feature.isEmpty()) {
						ConvertGenerics cg = new ConvertGenerics(feature.getFeatureValues());
						Double[] double_values = cg.ToDoubleArray();
						
						
						for (Burst b: BurstArray) {
							
							BurstType_PR prb = (BurstType_PR)b;
							
							Double val = double_values[++counter];
							Double result = (val.doubleValue() * 1000) / (double) prb.burstTime();
							modal_normalized.add(result);
							Double result2 = (val.doubleValue() * 1000) / (double) prb.GetPauseBefore();
							modal_normalized2.add(result2);
							Double result3 = (val.doubleValue() * 1000) / (double) prb.GetPauseAfter();
							modal_normalized3.add(result3);
						}
					}
					
					output.add(new Feature("modals_burst_duration_ratio", modal_normalized));
					output.add(new Feature("modals_burst_duration_ratio_Before_Pause", modal_normalized2));
					output.add(new Feature("modals_burst_duration_ratio_After_Pause", modal_normalized3));
					
					break;
				}
				
				
				default : {
						
				}
			}
		}
		
		//////// Compute new features ///////////////////
		LinkedList<Double> word_count_ratio = new LinkedList<Double>();
		LinkedList<Double> word_count_ratio_pause_before = new LinkedList<Double>();
		LinkedList<Double> word_count_ratio_pause_after = new LinkedList<Double>();
		
		for (Burst b: BurstArray) {
			
			BurstType_PR prb = (BurstType_PR)b;
			
			word_count_ratio.add((double)prb.burstWords() * 1000 / (double) prb.burstTime());
			word_count_ratio_pause_before.add((double)prb.burstWords() * 1000 / (double) prb.GetPauseBefore());
			word_count_ratio_pause_after.add((double)prb.burstWords() * 1000 / (double) prb.GetPauseAfter());
		}
		
		output.add(new Feature("pr_burst_word_count_ratio", word_count_ratio));
		output.add(new Feature("pr_burst_word_count_ratio_Before_Pause", word_count_ratio_pause_before));
		output.add(new Feature("pr_burst_word_count_ratio_After_Pause", word_count_ratio_pause_after));
		
		return output;
	}

}
