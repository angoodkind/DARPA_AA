package features.pause;

import java.util.Collection;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;

public class KeyUpDown implements ExtractionModule {
	
	@Override
	public Collection<Feature> extract(DataNode data) {
		PauseBursts ksep = new PauseBursts();

		KeyUpDownStats ks = null;
		int sum = 0;
		for (Answer a : data) {			
			KSE[] kses = (KSE[]) KSE.parseSessionToKSE(a.getKeyStrokes()).toArray(new KSE[0]);
			ks = ksep.getKeyUpDownStats(kses);
			sum += ks.m_numOfDownKey;
		}		
		String sFeat = Integer.toString(sum);		
		return null;
	}
	
	@Override
	public String getName() {
		return "Key UpDown";
	}

}
