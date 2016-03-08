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
import features.predictability.Predictability;

/**
 * Counts the number of words between a pause of a
 * predetermined threshold
 * <p><p>
 * Spaces signify a new word
 * 
 * @author Adam Goodkind
 *
 */
public class TypingBursts implements ExtractionModule {

	private static final long PAUSE = 2000;		//pause threshold that constitutes the end of a burst
	private LinkedList<Double> bursts = new LinkedList<Double>();
	
	@Override
	public Collection<Feature> extract(DataNode data) {
		bursts.clear();
		
		for (Answer a : data) {
			ArrayList<KSE> ksePresses = Predictability.generateKSEKeyPresses(a.getKeyStrokes());
			
			LinkedList<Double> typingBurstList = new LinkedList<Double>();
			double currentBurstWordCount = 0.0;
			for (KSE kse : ksePresses) {
				//space signifies a new word; increment word count
				if (kse.isSpace())
					currentBurstWordCount += 1.0;
				//end of burst
				if (kse.getM_pauseMs() > PAUSE) {
					typingBurstList.add(currentBurstWordCount);
					currentBurstWordCount = 0.0;
				}
			}
			typingBurstList.add(currentBurstWordCount);		//last burst
			
			double avgBurst = Predictability.mean(typingBurstList);
//			System.out.println(data.getUserID()+","+a.getCogLoad()+","+avgBurst);
			
			bursts.addAll(typingBurstList);
		}
		
		LinkedList<Feature> output = new LinkedList<Feature>();
		output.add(new Feature("TypingBursts"+PAUSE,bursts));
		return output;
	}

	@Override
	public String getName() {
		return "Typing Bursts";
	}

}
