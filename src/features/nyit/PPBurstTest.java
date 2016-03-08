package features.nyit;

import java.util.Collection;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import features.bursts.BurstBuilder;
import features.bursts.PPBurst;

public class PPBurstTest implements ExtractionModule{

	@Override
	public Collection<Feature> extract(DataNode data) {
		BurstBuilder bb = new BurstBuilder(new PPBurst());
		for (Answer a: data) {
			bb.setEventStream(a.getKeyStrokeList());
			while (bb.hasNextBurst()) {
				PPBurst ppb = (PPBurst)bb.nextBurst(); 
				System.out.println("-------------------------");
				System.out.println(ppb.get(0));
				System.out.println(ppb.get(1));
				System.out.println(ppb.get(2));
				System.out.println(ppb.get(3));
				System.out.println("----" + ppb.getFirstKeyPress());
				System.out.println(ppb.barf());
				System.out.println("----" + ppb.getLastKeyPress());
				System.out.println(ppb.get(ppb.NumberOfEvents() - 3));
				System.out.println(ppb.get(ppb.NumberOfEvents() - 2));
				System.out.println(ppb.get(ppb.NumberOfEvents() - 1));
				System.out.println("BurstTime: " + ppb.burstTime());
			}
				
		}
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
