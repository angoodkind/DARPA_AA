package features.pause;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;

public class BetweenPausesDuration implements ExtractionModule {
	
	int pause_threshold = 250;
	
	@Override
	public Collection<Feature> extract(DataNode data) {
		PauseBursts ksep = new PauseBursts();
    LinkedList<Feature> output = new LinkedList<>();
    ArrayList<Long> times = new ArrayList<>();
    ArrayList<Double> strokes = new ArrayList<>();

		for (Answer a : data) {
			ksep.generatePauseDownList(a.getKeyStrokes(),pause_threshold);
			KSE[] kseArray = KSE.parseSessionToKSE(a.getKeyStrokes()).toArray(new KSE[0]);
			List<Integer> pauseList = ksep.getPauseDownList();
			
			Iterator<Integer> it = pauseList.iterator();
			int lastIdx = 0;
			while (it.hasNext()) {
				int pauseIdx = it.next();
				long durBetPauses = kseArray[pauseIdx].getWhen() -
									kseArray[lastIdx].getWhen();
        times.add(durBetPauses);
        // The idx's include up and down strokes, to closer approximate only the downs, we divide the distance by 2
        strokes.add((pauseIdx - lastIdx)/2.);
        lastIdx = pauseIdx;
      }
		}
    output.add(new Feature("between_pause_duration_ms", times));
    output.add(new Feature("between_pause_duration_ks", strokes));

		return output;
	}

	@Override
	public String getName() {
		return "Between Pauses Duration Average";
	}
}
