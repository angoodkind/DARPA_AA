package features.predictability;

import java.util.*;

import events.EventList;
import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import features.pause.KSE;
import keystroke.KeyStroke;
import keytouch.KeyTouch;
import ngrammodel.Bigram;

public class KeyHoldPredictability extends Predictability implements ExtractionModule {

	private static final String modelName = "SESSION1";
	private static final String gramType = "keystroke";
//	private ArrayList<Double> bigramPredictabilityList;
	
	public KeyHoldPredictability() {
		super(modelName,gramType);
//		bigramPredictabilityList = new ArrayList<Double>();
	}
	
//	public void clearLists() {
//		bigramPredictabilityList.clear();
//	}

	@Override
	public Collection<Feature> extract(DataNode data) {
//		clearLists();
		for (Answer a : data) {
			
			Hashtable<Integer, LinkedList<Integer>> keyTable = constructKeyTable();//CONSTRUCT EMPTY CONTAINER WHICH STORES USER KEYHOLD VALUES FOR EACH KEY
			
			LinkedList<KeyTouch> keyTouchList = KeyTouch.parseSessionToKeyTouches(a.getKeyStrokes());
			for (int i = 1; i < keyTouchList.size(); i++) {
				KeyTouch kt1 = keyTouchList.get(i-1);
				KeyTouch kt2 = keyTouchList.get(i);
				Bigram bigram = new Bigram(KeyStroke.vkCodetoString(kt1.getKeyCode()),KeyStroke.vkCodetoString(kt2.getKeyCode()));
				
				double bigramProbability = keyStrokeBigramModel.getBigramProbability(bigram);
				double bigramHold = kt2.getHoldTime()/1000.0;
				double bigramPredictability = bigramHold/(-1.0*Math.log(bigramProbability));
//				bigramPredictabilityList.add(bigramPredictability);
			}
		}
		LinkedList<Feature> output = new LinkedList<Feature>();
//		output.add(new Feature("KH_Bigram_Predict",bigramPredictabilityList));
//		for (Feature f:output) System.out.println(f.toTemplate());
		return output;
	}
	
	//INITIALIZE THE CONTAINER TO STORE A USERS ANSWERS KEYHOLD VALUES...BUILD THE KEY NAMES IN THE CONTAINER BEFORE USING IT TO STORE THE KEYHOLD VALUE TIMES
	private Hashtable<Integer, LinkedList<Integer>> constructKeyTable(){
		Hashtable<Integer, LinkedList<Integer>> keyTable = new Hashtable<Integer, LinkedList<Integer>>();
		for (Integer key : KeyStroke.UsefulVKCodes())
			keyTable.put(key, new LinkedList<Integer>());
		return keyTable;
	}

	@Override
	public String getName() {
		return "Key Hold Predictability";
	}

}
