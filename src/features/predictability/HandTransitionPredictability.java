package features.predictability;

import java.util.*;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import features.pause.KSE;
import keystroke.KeyStroke;
import ngrammodel.Bigram;

/**
 * 
 * This feature measures the predictabilized latency for each hand
 * 
 * @author Adam Goodkind
 *
 */
public class HandTransitionPredictability extends Predictability implements ExtractionModule {

	private static final String modelName = "SESSION1";
	private static final String gramType = "keystroke";
	private static final String[] handList = {"L","R",null};
	private HashMap<String,ArrayList<Double>> handTransitionPauseMap = new HashMap<String,ArrayList<Double>>();
	
	public HandTransitionPredictability() {
		super(modelName, gramType);
	}
	
	public void generateTransitionMap() {
		handTransitionPauseMap.clear();
		for (String s1 : handList)
			for (String s2 : handList) {
				String transition = s1+"_"+s2;
				handTransitionPauseMap.put(transition, new ArrayList<Double>());
			}
	}
	
	
	@Override
	public Collection<Feature> extract(DataNode data) {
		for (Answer a : data) {
			int userID = data.getUserID();
			generateTransitionMap();
			ArrayList<KSE> kseList = new ArrayList<KSE>(KSE.parseSessionToKSE(a.getKeyStrokes()));
			
			//iterate through KSEs, not including first keypress
			for (int i=1; i < kseList.size(); i++) {
				if (kseList.get(i).isKeyPress()) {
					KSE kse2 = kseList.get(i);
					String hand2 = kse2.getHand();
					KSE kse1;
					//look backwards for preceding keystroke keypress; set to kse1 
					if (kseList.get(i-1).isKeyPress())
						kse1 = kseList.get(i-1);
					else //previous keystroke was a key release
						kse1 = kseList.get(i-2);
					String hand1 = kse1.getHand();
					
					//get transition type
					String transitionType = hand1+"_"+hand2;
					
					//get pause
					long transitionPause = kse2.getM_pauseMs();
					
					//get hold time, by looking forward for i's key release
					long kse2Release;
					if (kseList.get(i+1).getKeyCode() == kse2.getKeyCode())
						kse2Release = kseList.get(i+1).getWhen();
					else //following keystroke was another key press (of another key)
						kse2Release = kseList.get(i+2).getWhen();
					long keyHold = kse2Release - kse2.getWhen();
		
					
					//predictabilize pause time
					KSE[] bigram = {kse1,kse2};
					double predictabilizedPause;
					if (bigram[bigram.length-2].isSpace())
						predictabilizedPause = getKeystrokeNgramPredictability(bigram,"preWord",userID);
					else
						predictabilizedPause = getKeystrokeNgramPredictability(bigram,"intraWord",userID);
					
					
					//add pause to pauseTransitionMap
					if (handTransitionPauseMap.containsKey(transitionType))
						handTransitionPauseMap.get(transitionType).add(predictabilizedPause);
					
					//TODO predictabilize hold time
					//TODO add hold to holdHandMap
					//TODO add hold to holdTransitionMap
				}
			}
		}
		
		//after collecting all transition data,  determine ratios
		double LL_Mean = getMean(handTransitionPauseMap.get("L_L"));
		double RR_Mean = getMean(handTransitionPauseMap.get("R_R"));
		double LLtoRRratio = LL_Mean/RR_Mean;
		
		double LR_Mean = getMean(handTransitionPauseMap.get("L_R"));
		double RL_Mean = getMean(handTransitionPauseMap.get("R_L"));
		double LRtoRLratio = LR_Mean/RL_Mean;
		
		double null_L_Mean = getMean(handTransitionPauseMap.get("null_L"));
		double null_R_Mean = getMean(handTransitionPauseMap.get("null_R"));
		double nullLtoNullRratio = null_L_Mean/null_R_Mean;
		
		double L_null_Mean = getMean(handTransitionPauseMap.get("L_null"));
		double R_null_Mean = getMean(handTransitionPauseMap.get("R_null"));
		double LnulltoRnullRatio = L_null_Mean/R_null_Mean;
		
		LinkedList<Feature> output = new LinkedList<Feature>();
		output.add(new Feature("Predict_LL_RR",LLtoRRratio));
		output.add(new Feature("Predict_LR_RL",LRtoRLratio));
		output.add(new Feature("Predict_nullL_nullR",nullLtoNullRratio));
		output.add(new Feature("Predict_Lnull_Rnull",LnulltoRnullRatio));
		
//		for (String s : handTransitionPauseMap.keySet()) {
//			output.add(new Feature("Predict_"+s,handTransitionPauseMap.get(s)));
//		}
//		for (String s : handPauseMap.keySet()) {
//			output.add(new Feature("Predict_"+s,handPauseMap.get(s)));
//		}
//		for (Feature f : output) System.out.println(f.toTemplate());
		return output;
	}
	
	/**
	 * calculates the mean, for a List of doubles
	 * @param values	List of doubles
	 * @return	mean value
	 */
	public double getMean(List<Double> values) {
		int size = values.size();
		double sum = 0.0;
		for (double value : values)
			sum += value;
		return sum/size;
	}

	@Override
	public String getName() {
		return "Hand Predictability";
	}

}
