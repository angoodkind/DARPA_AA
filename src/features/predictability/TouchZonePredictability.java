package features.predictability;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import features.pause.KSE;
import keystroke.KeyStroke;

/**
 * Touch-typing zone is specific to each finger, e.g. left thumb is L1, while
 * right pinkie is R5. 
 * 
 * This feature measures the predictabilized latency for each typing zone, both
 * for inter-zone transitions, e.g. R5 -> L1, and intra-zone, e.g. R5 -> R5.
 *  
 * @author Adam Goodkind
 *
 */
public class TouchZonePredictability extends Predictability implements ExtractionModule {

	private static final String modelName = "SESSION1";
	private static final String gramType = "keystroke";
	private static final String[] zoneList = {"L1","L2","L3","L4","L5","R1","R2","R3","R4","R5"};
	private HashMap<String,ArrayList<Double>> zoneTransitionPauseMap = new HashMap<String,ArrayList<Double>>();
	private HashMap<String,ArrayList<Double>> handPauseMap = new HashMap<String,ArrayList<Double>>();
	
	public TouchZonePredictability() {
		super(modelName, gramType);
	}
	
	public void generateZoneMap() {
		zoneTransitionPauseMap.clear();
		for (String s1 : zoneList)
			for (String s2 : zoneList) {
				String transition = s1+"_"+s2;
				zoneTransitionPauseMap.put(transition, new ArrayList<Double>());
			}
	}
	
	public void generateHandMap() {
		handPauseMap.clear();
		handPauseMap.put("L", new ArrayList<Double>());
		handPauseMap.put("R", new ArrayList<Double>());
		handPauseMap.put(null, new ArrayList<Double>());
	}

	@Override
	public Collection<Feature> extract(DataNode data) {
		for (Answer a : data) {
			int userID = data.getUserID();
			generateZoneMap();
			generateHandMap();
			ArrayList<KSE> allKseList = new ArrayList<KSE>(KSE.parseSessionToKSE(a.getKeyStrokes()));
			ArrayList<KSE> kseKeypressList = new ArrayList<KSE>();
			for (KSE kse : allKseList)
				if (kse.isKeyPress())
					kseKeypressList.add(kse);
			
			for (int i=0; i < kseKeypressList.size()-1; i++) {
				KSE kse1 = kseKeypressList.get(i);
				KSE kse2 = kseKeypressList.get(i+1);
				KSE[] bigram = {kse1,kse2};
				String kse1Zone = kse1.getHand()+kse1.getFinger();
				String kse2Zone = kse2.getHand()+kse2.getFinger();
				String transition = kse1Zone+"_"+kse2Zone;
				double predictabilizedPause;
				if (bigram[bigram.length-2].isSpace())
					predictabilizedPause = getKeystrokeNgramPredictability(bigram,"preWord",userID);
				else
					predictabilizedPause = getKeystrokeNgramPredictability(bigram,"intraWord",userID);
				//hand-ambiguous transition, e.g. Shift key, not recorded
				if (zoneTransitionPauseMap.containsKey(transition))
					zoneTransitionPauseMap.get(transition).add(predictabilizedPause);
				handPauseMap.get(kse2.getHand()).add(predictabilizedPause);
			}
		}
		LinkedList<Feature> output = new LinkedList<Feature>();
		for (String s : zoneTransitionPauseMap.keySet()) {
			output.add(new Feature("Predict_"+s,zoneTransitionPauseMap.get(s)));
		}
		for (String s : handPauseMap.keySet()) {
			output.add(new Feature("Predict_"+s,handPauseMap.get(s)));
		}
//		for (Feature f : output) System.out.println(f.toTemplate());
		return output;
	}

	@Override
	public String getName() {
		return "Typing Zone Predictability";
	}

}
