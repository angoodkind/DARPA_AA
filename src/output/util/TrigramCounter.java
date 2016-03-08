package output.util;

import features.predictability.Predictability;
import keystroke.KeyStroke;
import keytouch.KeyTouch;
import ngrammodel.Bigram;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.util.*;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;


/**
 * Counts trigram occurrences
 * Prints trigrams to a list, if trigram count is greater
 * than the threshold
 * 
 */
public class TrigramCounter extends Predictability implements ExtractionModule {

	private static final String modelName = "SESSION1";
	private static final String gramType = "keystroke";

	private static TreeSet<Integer> keySet = new TreeSet<Integer>(KeyStroke.UsefulVKCodes());
	private static HashMap<String, Integer> countMap = new HashMap<String,Integer>();

	static final int TOTAL_USERS = 486;
	static int current_user = 0;
	
	static final String FILENAME = "AvailableTrigrams.list";
	static final int THRESHOLD = 5;

	/**
	 * Initialize the module.
	 */
	public TrigramCounter() {
		super(modelName, gramType);
	}

	/**
	 * Generates full permutation of two UpperCase alpha combinations and places
	 * them into a TreeSet which ensures no duplicates and automatically sorts them!
	 */
	private static void generateSearchSpace() {
		if (countMap.isEmpty())
			for (Integer key0 : keySet)
				for (Integer key1 : keySet)
					for (Integer key2 : keySet)
						countMap.put((KeyStroke.vkCodetoString(key0)+"_"+KeyStroke.vkCodetoString(key1)
								+"_"+KeyStroke.vkCodetoString(key2)),0);
	}

	@Override
	public Collection<Feature> extract(DataNode data) {

		generateSearchSpace();

			for (Answer a : data) {

				LinkedList<KeyTouch> ktList = KeyTouch.parseSessionToKeyTouches(a.getKeyStrokes());
				for (int i = 1; i< ktList.size()-1; i++) {
					KeyTouch k0 = ktList.get(i-1);
					int k0Code = k0.getKeyCode();
					String k0Str = KeyStroke.vkCodetoString(k0Code);

					KeyTouch k1 = ktList.get(i);
					int k1Code = k1.getKeyCode();
					String k1Str = KeyStroke.vkCodetoString(k1Code);

					KeyTouch k2 = ktList.get(i+1);
					int k2Code = k2.getKeyCode();
					String k2Str = KeyStroke.vkCodetoString(k2Code);

					//filter timing outliers
					if (k1.getPrecedingPause() <= 0 || k1.getPrecedingPause() > 2000 ||
							k1.getHoldTime() <= 0 || k1.getHoldTime() > 2000) {
						continue;
					}

					//filter trigraphs spanning pre-word boundaries
					if (k0.getKeystroke().isSpace() || k1.getKeystroke().isSpace()) {
						continue;
					}

					String digraph = k0Str+"_"+k1Str;
					String trigraph = k0Str+"_"+k1Str+"_"+k2Str;;

					if (countMap.containsKey(trigraph))
						countMap.put(trigraph, countMap.get(trigraph)+1);
				}
			}
			current_user++;
			
			// When full query is complete, write to file if count
			// is above threshold
			if (current_user == TOTAL_USERS) {
				
				ArrayList<String> availableTrigrams = new ArrayList<String>();
				for (String trigram : countMap.keySet()) {
					int TrigramCount = countMap.get(trigram);
					if (TrigramCount >= THRESHOLD)
						availableTrigrams.add(trigram);
				}
				
				File f = new File(FILENAME);
				try {
					FileOutputStream fOut = new FileOutputStream(f);
					ObjectOutputStream out = new ObjectOutputStream(fOut);
					out.writeObject(availableTrigrams);
					out.close();
				} catch (IOException e) {e.printStackTrace();}
			}
	
		return null;
	}

	@Override
	public String getName() {
		return "Trigraph Counter";
	}

}
