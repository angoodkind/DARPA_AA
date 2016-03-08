package features.nyit;

import java.util.Collection;
import java.util.LinkedList;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import features.bursts.BurstBuilder;
import features.bursts.PPBurst;

public class PPBurstMetrics implements ExtractionModule{

	@Override
	public Collection<Feature> extract(DataNode data) {
		BurstBuilder bb = new BurstBuilder(new PPBurst());
		
		LinkedList<Long> burstTimes = new LinkedList<Long>();
		
		LinkedList<Integer> burstKeyStrokes = new LinkedList<Integer>();
		LinkedList<Integer> burstChars = new LinkedList<Integer>();
		LinkedList<Integer> burstAlphaChars = new LinkedList<Integer>();
		LinkedList<Integer> burstSpaces = new LinkedList<Integer>();
		LinkedList<Integer> burstPunctuation = new LinkedList<Integer>();
		
		LinkedList<Double> burstKeyStrokesPerMinute = new LinkedList<Double>();
		LinkedList<Double> burstCharsPerMinute = new LinkedList<Double>();
		LinkedList<Double> burstAlphaCharsPerMinute = new LinkedList<Double>();
		LinkedList<Double> burstSpacesPerMinute = new LinkedList<Double>();
		LinkedList<Double> burstPunctuationPerMinute = new LinkedList<Double>();
		
		
		for (Answer a: data) {
			bb.setEventStream(a.getKeyStrokeList());
			while (bb.hasNextBurst()) {
				PPBurst ppb = (PPBurst) bb.nextBurst(); 
				burstTimes.add(ppb.burstTime());
				
				burstKeyStrokes.add(ppb.burstKeyStrokes());
				burstChars.add(ppb.burstChars());
				burstAlphaChars.add(ppb.burstAlphaChars());
				burstSpaces.add(ppb.burstSpaces());
				burstPunctuation.add(ppb.burstPunctuation());
				
				double timeInMinutes = ppb.burstTime() / 60000.0; 
				
				burstKeyStrokesPerMinute.add((double)ppb.burstKeyStrokes() / timeInMinutes);
				burstCharsPerMinute.add(ppb.burstChars() / timeInMinutes);
				burstAlphaCharsPerMinute.add(ppb.burstAlphaChars() / timeInMinutes);
				burstSpacesPerMinute.add(ppb.burstSpaces() / timeInMinutes);
				burstPunctuationPerMinute.add(ppb.burstPunctuation() / timeInMinutes);
			}
				
		}
		
		LinkedList<Feature> output = new LinkedList<Feature>();
		
		output.add(new Feature("PP_Burst_Length",burstTimes));
		
		output.add(new Feature("PP_Burst_KeyStrokes",burstKeyStrokes));
		output.add(new Feature("PP_Burst_Chars",burstChars));
		output.add(new Feature("PP_Burst_AlphaChars",burstAlphaChars));
		output.add(new Feature("PP_Burst_Spaces",burstSpaces));
		output.add(new Feature("PP_Burst_Punctuation",burstPunctuation));
		
		output.add(new Feature("PP_Burst_KeyStrokesPerMinute",burstKeyStrokesPerMinute));
		output.add(new Feature("PP_Burst_CharsPerMinute",burstCharsPerMinute));
		output.add(new Feature("PP_Burst_AlphaCharsPerMinute",burstAlphaCharsPerMinute));
		output.add(new Feature("PP_Burst_SpacesPerMinute",burstSpacesPerMinute));
		output.add(new Feature("PP_Burst_PunctuationPerMinute",burstPunctuationPerMinute));
		
		
		return output;
	}

	@Override
	public String getName() {
		return "PP Burst Metrics";
	}
	
}
