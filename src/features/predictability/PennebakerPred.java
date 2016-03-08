package features.predictability;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import events.EventList;
import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import features.lexical.FunctionWordMetrics;
import keystroke.KeyStroke;
import keytouch.KeyTouch;

public class PennebakerPred implements ExtractionModule {

	private static ArrayList<String> pennebakerWords = FunctionWordMetrics.PennebakerWords();
	private static TreeSet<Integer> keySet = new TreeSet<Integer>(LessUsefulVKCodes());
	private HashMap<String, LinkedList<Double>> featureMap = new HashMap<String, LinkedList<Double>>(10000);

	public PennebakerPred() {
		featureMap.clear();
	}

	private void generateSearchSpace() {
		featureMap.clear();
		for (Integer key0 : keySet)
			for (Integer key1 : keySet) {
				featureMap.put(("PB_"+KeyStroke.vkCodetoString(key0)+"_"+KeyStroke.vkCodetoString(key1)),new LinkedList<Double>());
				featureMap.put(("Non_PB_"+KeyStroke.vkCodetoString(key0)+"_"+KeyStroke.vkCodetoString(key1)),new LinkedList<Double>());
			}
	}


	@Override
	public Collection<Feature> extract(DataNode data) {

		generateSearchSpace();

		for (Answer a : data) {

			LinkedList<KeyTouch> ktList = KeyTouch.parseSessionToKeyTouches(a.getKeyStrokes());
			StringBuilder s = new StringBuilder();
			ArrayList<KeyTouch> wordKeyTouches = new ArrayList<KeyTouch>();
			for (int i = 1; i < ktList.size() - 1; i++) {
				KeyTouch k = ktList.get(i);

				if (k.getKeystroke().isAlpha()) {
					s.append(k.getKeyChar());
					wordKeyTouches.add(k);
				}

				if (k.getKeystroke().isBackspace() && s.length() > 0) {
					s.deleteCharAt(s.length()-1);
				}

				if (k.getKeystroke().isSpace()) {
					if (s.length() > 1) {
						String word = s.toString().toLowerCase();
						for (int j = 1; j < wordKeyTouches.size(); j++) {
							KeyTouch prevKT = wordKeyTouches.get(j-1);
							KeyTouch KT = wordKeyTouches.get(j);
							String digraph = KeyStroke.vkCodetoString(prevKT.getKeyCode()) +"_"+ KeyStroke.vkCodetoString(KT.getKeyCode());
							double duration = prevKT.getHoldTime() + KT.getHoldTime();

							if (pennebakerWords.contains(word))
								featureMap.get("PB_"+digraph).add(duration);
							else
								featureMap.get("Non_PB_"+digraph).add(duration);
						} //END WORD LOOP
					} //END S LOOP
					s.setLength(0);
					wordKeyTouches.clear();
				} //END K.ISSPACE()
			} //END KTLIST LOOP
		} //END ANSWER LOOP

		LinkedList<Feature> output = new LinkedList<Feature>();
		for (String s : featureMap.keySet())
			output.add(new Feature(s,featureMap.get(s)));
		//		for (Feature f : output) System.out.println(f.toTemplate());
		return output;
	}

	@Override
	public String getName() {return "Pennebaker Pred";}

	/**
	 * This returns a Set Object containing vkCodes that are useful to our
	 * study.
	 * <p>
	 * <p>
	 * This lowers the search space and eliminates erroneous key checks.
	 * 
	 * @return Set of vkCodes that are useful in checking during keystroke
	 *         experiments.
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
		//		vkSet.add(32); //space
		//		vkSet.add(44); //comma
		//		vkSet.add(46); //period
		for (int i = 65; i < 91; i++) //A-Z
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
		//		vkSet.add(222); //apostrophe
		//		vkSet.add(524);
		//		vkSet.add(525);
		return vkSet;
	}

}
