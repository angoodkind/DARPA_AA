package extractors.lexical;

import java.util.*;
import java.io.*;
import java.math.BigDecimal;

import events.EventList;
import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import features.pause.KSE;
import keystroke.KeyStroke;
import keytouch.KeyTouch;

/**
 * Creates a HashMap, mapping userID to user's mean pauses 
 * and standard deviation of all pauses
 * 		Pause categories are further broken down into pauses that
 * 		occur before a word, and pauses that occur intra-word
 * 
 * This class does not return a Feature
 * 
 * The structure of the nested HashMap pauseMap is:
 * <UserID: <metric, Double> >
 * 
 * Metrics are: LinearMean, LinearStdDev, LogMean, LogStdDev
 * Categories are: "all","preWord","intraWord"
 * 
 * Each metric is calculated for each category of keystrokes
 * 
 * @author Adam Goodkind
 *
 */
public class CreateUserPauseMetricsMap implements ExtractionModule {

	//see class comments for structure of HashMap
	private String[] categories = {"all","preWord","intraWord"};
	private static HashMap<Integer,HashMap<String,Double>> pauseMap = new HashMap<Integer,HashMap<String,Double>>();
	private FileOutputStream fileOut;
	private ObjectOutputStream objectOut;
	private static final int totalSubjects = 486; //CHANGE! Session1: 838; Session2: 491; Training/Testing: 486; alldata: 
	private static int currentSubject = 0;
	private final String filename = "Testing_3CategoryDurationMetrics.map";

	@Override
	public Collection<Feature> extract(DataNode data) {


		//add userID key to pauseMaps
		int userID = data.getUserID();
		pauseMap.put(userID, new HashMap<String,Double>());

		//initiate map to collect all pause data
		HashMap<String,ArrayList<Double>> pauseListMap = new HashMap<String,ArrayList<Double>>();
		initiatePauseListMap(pauseListMap);

		for (Answer a: data) {

			//build map for individual user, based on categories
			//add user categories to allUser map

			LinkedList<KeyTouch> ktList = KeyTouch.parseSessionToKeyTouches(a.getKeyStrokes());
			for (int i=1; i<ktList.size(); i++) {
				KeyTouch prevK = ktList.get(i-1);
				KeyTouch k = ktList.get(i);
				double duration = (double) (prevK.getHoldTime()+k.getHoldTime());
				
				pauseListMap.get("all").add(duration);
				
				if (KeyStroke.vkCodetoString(prevK.getKeyCode()).equals("Spacebar"))
					pauseListMap.get("preWord").add(duration);
				else
					pauseListMap.get("intraWord").add(duration);
			}

//			ArrayList<KSE> kseList = new ArrayList<KSE>(KSE.parseSessionToKSE(a.getKeyStrokes()));
//			ArrayList<KSE> ksePressList = new ArrayList<KSE>();
//			for (KSE kse : kseList)
//				if (kse.isKeyPress())
//					ksePressList.add(kse);
//			//			boolean punctuationEncountered = false;
//			for (int i = 1; i < ksePressList.size()-1; i++) {
//				KSE previousKSE = ksePressList.get(i-1);
//				KSE kse = ksePressList.get(i);
//				double pause = (double) kse.getM_pauseMs();
//
//				//automatically add to 'all pauses' list
//				pauseListMap.get("allKeystrokes").add(pause);
//
//				//				if (kse.isPunctuation())
//				//					punctuationEncountered = true;
//				//check last keystroke, to determine pre- or intra-word
//				//				if (ksePressList.get(i).isSpace())
//				//					pauseListMap.get("space").add(pause);
//
//				if (ksePressList.get(i-1).isSpace()) { //pre-word
//					pauseListMap.get("preWord").add(pause);
//					//					if (punctuationEncountered) {
//					//						pauseListMap.get("clauseInitial").add(pause);
//					//						punctuationEncountered = false;
//					//					}
//				}
//				else  //intra-word
//					pauseListMap.get("intraWord").add(pause);
//			}

		}

		for (String category : categories) {
			pauseMap.get(userID).put(category+"LinearMean",linearMean(pauseListMap.get(category)));
			pauseMap.get(userID).put(category+"LinearStdDev",linearStdDev(pauseListMap.get(category)));
			pauseMap.get(userID).put(category+"LogMean",logMean(pauseListMap.get(category)));
			pauseMap.get(userID).put(category+"LogStdDev",logStdDev(pauseListMap.get(category)));
		}

		currentSubject++;

		if (totalSubjects == currentSubject) {
			try {
				fileOut = new FileOutputStream(filename);
				objectOut = new ObjectOutputStream(fileOut);

				objectOut.writeObject(pauseMap);
				objectOut.close();
			} catch (IOException e) {e.printStackTrace();}
			finally {
				File f = new File(filename);
				if (f.exists() && totalSubjects == currentSubject)
					System.out.println(filename+" successfully created with "+currentSubject+" users");
				else 
					System.err.println("File creation error!");
			}
		}
		return null;
	}

	private void initiatePauseListMap(HashMap<String, ArrayList<Double>> map) {
		for (String category : categories)
			map.put(category, new ArrayList<Double>());
	}

	private double linearMean(ArrayList<Double> list) {
		double sum = 0.0;
		int count = 0;
		for (Double value : list) {
			if (value > 0)
				sum += value;
			count ++;
		}
		return sum / count;
	}

	/**
	 * Returns the mean of the logs of each value
	 * @param list		list of values
	 * @return		mean
	 */
	private double logMean(ArrayList<Double> list) {
		double sum = 0.0;
		int count = 0;
		for (Double value : list) {
			if (value > 0) {
				sum += Math.log(value);
				count++;
			}
		}
		return sum / count;
	}

	/**
	 * Although we could pass in mean, want to avoid passing in 
	 * log mean, when should be linear
	 * @param list
	 * @return
	 */
	private double linearStdDev(ArrayList<Double> list) {
		double linearMean = linearMean(list);
		double sum = 0.0;
		int count = 0;
		for (Double value : list) {
			if (value > 0) {
				sum += ((value-linearMean) * (value-linearMean));
				count++;
			}
		}
		return Math.sqrt(sum / count);
	}

	/**
	 * Although we could pass in mean, want to avoid passing in 
	 * log mean, when should be linear
	 * @param list
	 * @return
	 */
	private double logStdDev(ArrayList<Double> list) {
		double logMean = logMean(list);
		double sum = 0.0;
		int count = 0;
		for (Double value : list) {
			if (value > 0) {
				double logValue = Math.log(value);
				sum += ((logValue-logMean) * (logValue-logMean));
				count++;
			}
		}
		return Math.sqrt(sum / count);
	}

	public HashMap<Integer,HashMap<String,Double>> getPauseMap() {
		return pauseMap;
	}



	@Override
	public String getName() {
		return "Create Typing Rate Map";
	}

}
