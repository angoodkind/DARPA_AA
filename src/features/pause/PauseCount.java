package features.pause;

import java.util.Collection;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;

public class PauseCount implements ExtractionModule {

	@Override
	public Collection<Feature> extract(DataNode data) {
    
		 int pause_threshold = 250;
		
		PauseBursts ksep = new PauseBursts();
		
		PauseCountStats pcs = null;
		int sum = 0;
		for (Answer a : data) {			
			KSE[] kses = KSE.parseSessionToKSE(a.getKeyStrokes()).toArray(new KSE[0]);
			pcs = ksep.getPauseCountStats(kses,pause_threshold);
			sum += pcs.m_numOfPauses;
		}
    return null;
	}

	@Override
	public String getName() {
		return "Pause Count";
	}

}
