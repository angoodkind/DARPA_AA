package keytouch;

import keystroke.KeyStroke;

import java.util.*;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;

public class KeyTouchTrigraph implements ExtractionModule {
	
	private static final boolean INCLUDE_INTERVAL = false;
	private static TreeSet<Integer> keySet = new TreeSet<Integer>(KeyStroke.UsefulVKCodes());
	private HashMap<Integer, LinkedList<Double>> featureMap = new HashMap<Integer, LinkedList<Double>>(10000);

	/**
	 * Initialize the module.
	 */
	public KeyTouchTrigraph() {
		featureMap.clear();
	}

	/**
	 * Generates full permutation of two UpperCase alpha combinations and places
	 * them into a TreeSet which ensures no duplicates and automatically sorts them!
	 */
	private void generateSearchSpace() {
		featureMap.clear();
		for (Integer key0 : keySet)
			for (Integer key1 : keySet)
				for (Integer key2 : keySet) 
					featureMap.put(key0*1000000 + key1*1000 + key2, new LinkedList<Double>());
	}

	@Override
	public Collection<Feature> extract(DataNode data) {

		//clear feature map and repopulate it's keys for a new extraction
		generateSearchSpace();

		for (Answer a : data) {

			LinkedList<KeyTouch> ktList = KeyTouch.parseSessionToKeyTouches(a.getKeyStrokes());
			for (int i = 2; i < ktList.size(); i++) {
				KeyTouch k1 = ktList.get(i-2);
				int k1Code = k1.getKeyCode();
				
				KeyTouch k2 = ktList.get(i-1);
				int k2Code = k2.getKeyCode();
				long k2Pause = k2.getPrecedingPause();
				
				KeyTouch k3 = ktList.get(i);
				int k3Code = k3.getKeyCode();
				long k3Pause = k3.getPrecedingPause();
				
				int trigraphCode = k1Code*1000000 + k2Code*1000 + k3Code;
				
				double trigraphDuration = (double)(k1.getHoldTime()+k2.getHoldTime()+k3.getHoldTime());
				if (INCLUDE_INTERVAL)
					trigraphDuration += (double)(k2Pause+k3Pause);
					
				
				if (featureMap.containsKey(trigraphCode)) {
					featureMap.get(trigraphCode).add(trigraphDuration);
				}
			}
		}
		//create output feature list.
		LinkedList<Feature> output = new LinkedList<Feature>();

		//iterate over the featureMap
		for (Integer vkCode0 : keySet) {
			for (Integer vkCode1 : keySet)
				for (Integer vkCode2 : keySet)
					output.add(new Feature(
							"TR_D_" + KeyStroke.vkCodetoString(vkCode0) + "_" + KeyStroke.vkCodetoString(vkCode1)
							+ "_" + KeyStroke.vkCodetoString(vkCode2),
							featureMap.get(vkCode0*1000000 + vkCode1*1000 + vkCode2)));
		}

		return output;
	}


	@Override
	public String getName() {
		return "KeyTouch Preceding Pause";
	}

	/**
	 * This returns a Set Object containing vkCodes that are useful to our
	 * study.
	 * <p/>
	 * <p/>
	 * This lowers the search space and eliminates erroneous key checks.
	 *
	 * @return Set of vkCodes that are useful in checking during keystroke
	 * experiments.
	 */
	public static Set<Integer> LessUsefulVKCodes() {
		TreeSet<Integer> vkSet = new TreeSet<>();
		//		vkSet.add(8); //backspace
		//		vkSet.add(9);
		//		vkSet.add(10);
		//		vkSet.add(16);
		//		vkSet.add(17);
		//		vkSet.add(18);
		//		vkSet.add(19);
		//		vkSet.add(20);
		//		vkSet.add(27);
		vkSet.add(32); //space
		vkSet.add(44); //comma
		vkSet.add(46); //period
		for (int i = 65; i < 91; i++) //A-Z (65-91)
			vkSet.add(i);
		//		for (int i = 32; i < 41; i++)
		//			vkSet.add(i);
		//		for (int i = 44; i < 58; i++)
		//			vkSet.add(i);
		//		vkSet.add(59);
		//		vkSet.add(61);
		//		for (int i = 65; i < 94; i++)
		//			vkSet.add(i);
		//		for (int i = 96; i < 108; i++)
		//			vkSet.add(i);
		//		for (int i = 109; i < 124; i++)
		//			vkSet.add(i);
		//		vkSet.add(127);
		//		vkSet.add(144);
		//		vkSet.add(145);
		//		vkSet.add(155);
		//		vkSet.add(192);
		vkSet.add(222); //apostrophe
		//		vkSet.add(524);
		//		vkSet.add(525);
		return vkSet;
	}

}
