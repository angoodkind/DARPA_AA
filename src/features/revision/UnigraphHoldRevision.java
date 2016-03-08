package features.revision;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeSet;

import events.EventList;
import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import features.pause.KSE;
import keystroke.KeyStroke;
import keytouch.KeyTouch;

/**
 * Note: 7/30/15 - This is a pretty poorly written class. Using something
 * like RevisionNgramFusion will probably always be easier.
 * @author Adam Goodkind
 *
 */
public class UnigraphHoldRevision implements ExtractionModule   {

	private static TreeSet<Integer> keySet = new TreeSet<Integer>(KeyStroke.UsefulVKCodes());
	private HashMap<String, LinkedList<Double>> featureMap = new HashMap<String, LinkedList<Double>>(10000);
	private static String[] rev_str = {"trans_rev_del_", "trans_rev_repl_", "rev_del_","rev_repl_","trans_back_","back_","non_rev_"};
	private static final int NGRAM_SIZE = 2;
	
	@Override
	public Collection<Feature> extract(DataNode data) {
		createSearchSpace();
		for (Answer a : data) {
			LinkedList<KeyTouch> keyTouchList = KeyTouch.parseSessionToKeyTouches(a.getKeyStrokes());
			ArrayList<Revision> revList = Revision.parseKeystrokesToRevision(keyTouchList);
			ArrayList<Integer> revStartingIdxList = Revision.getRevStartingIdxList(revList);

			for (int i = 0; i < (keyTouchList.size() - NGRAM_SIZE) + 1; i++) {
				KeyTouch k = keyTouchList.get(i);
				String kStr = KeyStroke.vkCodetoString(k.getKeyCode());
//				System.out.println(i+" "+kStr/**+","+kt.getHoldTime()**/);
				
				if (revStartingIdxList.contains(i)) {
					Revision r = revList.get(revStartingIdxList.indexOf(i));
//					System.out.println(i+" "+r);
					String rStr = r.isTransposition() ? "trans_" : "";
					for (KeyTouch delK : r.deletedKeyTouches) {
						featureMap.get(rStr+"rev_del_"+KeyStroke.vkCodetoString(delK.getKeyCode()))
						.add((double) delK.getPrecedingPause());
					}
					for (KeyTouch revK : r.revisedKeyTouches) {
						featureMap.get(rStr+"rev_repl_"+KeyStroke.vkCodetoString(revK.getKeyCode()))
						.add((double) revK.getPrecedingPause());
					}
					for (KeyTouch backK : r.backspaceKeyTouches) {
						featureMap.get(rStr+"back_"+KeyStroke.vkCodetoString(backK.getKeyCode()))
						.add((double) backK.getPrecedingPause());
					}
					i = (i + r.size()) - 1; 
				}
				else {
					System.out.println(i+" non_rev_"+kStr);
					featureMap.get("non_rev_"+kStr).add((double) k.getPrecedingPause());
				}
			}
		}
		LinkedList<Feature> output = new LinkedList<Feature>();
//		for (String r : rev_str) {
//			for (Integer key : keySet) {
//				output.add(new Feature(r+KeyStroke.vkCodetoString(key),featureMap.get(r+KeyStroke.vkCodetoString(key))));
//			}
//		}

		return output;
	}

	private void createSearchSpace() {
		
		featureMap.clear();
		for (String r : rev_str) {
			for (Integer key : keySet) {
				featureMap.put(r+KeyStroke.vkCodetoString(key), new LinkedList<Double>());
			}
		}
	}

	@Override
	public String getName() {
		return "UnigraphHoldRevision";
	}

}
