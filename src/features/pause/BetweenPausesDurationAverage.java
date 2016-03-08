package features.pause;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;

public class BetweenPausesDurationAverage implements ExtractionModule {
	
	int pause_threshold = 250;
	
	@Override
	public Collection<Feature> extract(DataNode data) {
		PauseBursts ksep = new PauseBursts();			
		
		double avgSum = 0.0;
		for (Answer a : data) {			
			ksep.generatePauseList(a.getKeyStrokes(),pause_threshold);
			KSE[] kseArray = ksep.getKseArray();
			List<Integer> pauseList = ksep.getPauseList();
			
			Iterator<Integer> it = pauseList.iterator();
			int lastIdx = 0;
			long allDur = 0;
			while (it.hasNext()) {
				int pauseIdx = it.next();
				long durBetPauses = kseArray[pauseIdx].getWhen() -
									kseArray[lastIdx].getWhen();
				lastIdx = pauseIdx;
				allDur += durBetPauses;
				System.out.println(durBetPauses);
			}
			double avg = ((double) allDur) / pauseList.size();
			avgSum = avgSum + avg;
			System.out.println(pauseList.size());
			System.out.println(avg);
		}		
		double theAvg = avgSum / data.size();
		System.out.println(data.size());
		String sFeat = Double.toString(theAvg);	
		System.out.println(sFeat);
		
		return null;
	}

	@Override
	public String getName() {
		return "Between Pauses Duration Average";
	}

}
