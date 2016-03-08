package features.nyit;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import keystroke.KeyStroke;

/**
 * Test module to be used to generate a visual map of latencies. 
 * @author Patrick
 *
 */
public class LatencyStats implements ExtractionModule  {

	HashMap<Integer,Collection<Integer>> barf;
	LinkedList<Feature> output;
	int maxLatency = 60000;
	int subdivision = 1000;
	
	public LatencyStats() {
		barf = new HashMap<Integer,Collection<Integer>>();
		output = new LinkedList<Feature>();
		maxLatency = maxLatency / subdivision;
		for (int i = 1; i < maxLatency + 1; i++) {
			barf.put(i, new LinkedList<Integer>());
		}
	}
	
	private void clearMap() {
		
		for (Integer i: barf.keySet()) {
			barf.get(i).clear();
		}
	}
	
	@Override
	public Collection<Feature> extract(DataNode data) {
		clearMap();
		for (Answer a: data) {
			processAnswer(a);
		}
		formatOutput();
		return output;
	}

	private void processAnswer(Answer a) {
		KeyStroke last = null;
		int latency;
		for (KeyStroke k: a.getKeyStrokeList()) {
			if (k.isKeyRelease())
				continue;
			if (last != null) {
				latency = (int) Math.floor((k.getWhen() - last.getWhen()) / (double)subdivision) + 1;
				if (latency > maxLatency-1) {
					latency = maxLatency;
				} else if (latency <= 0) {
					latency = 1;
				}
				try {
				barf.get(latency).add(last.getKeyCode());
				} catch (NullPointerException e) {
					System.out.println(latency);
					System.out.println(k);
					System.out.println(last);
					e.printStackTrace();
					System.exit(1);
				}
			}
			last = k;
		}
	}
	
	private void formatOutput() {
		output.clear();
		for (Integer i : barf.keySet()) {
			output.add(new Feature(Integer.toString(i*subdivision),barf.get(i)));
		}
	}

	@Override
	public String getName() {
		return "Latency Statistics";
	}

}
