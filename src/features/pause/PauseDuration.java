package features.pause;

import java.util.Collection;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;

public class PauseDuration implements ExtractionModule {
	
	int pause_threshold = 250;

	@Override
	public Collection<Feature> extract(DataNode data) {
		PauseBursts ksep = new PauseBursts();
		
		PauseDurationStats pds = null;
		int sum = 0;
		for (Answer a : data) {			
			KSE[] kses = KSE.parseSessionToKSE(a.getKeyStrokes()).toArray(new KSE[0]);
			pds = ksep.getPauseDurationStats(kses,pause_threshold);
			sum += pds.m_durOfPauses;
		}		
		String sFeat = Long.toString(sum);	
		return null;
	}

	@Override
	public String getName() {
		return "Pause Duration";
	}
}
