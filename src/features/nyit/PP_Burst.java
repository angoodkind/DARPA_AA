package features.nyit;

import java.util.*;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.FileWriter;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import features.bursts.BurstType_PP;
import features.bursts.PauseTimeDelimiter;
import keystroke.KeyStroke;
import output.util.SegmentAnswer;

public class PP_Burst implements ExtractionModule {
	
	private PauseTimeDelimiter pauseDelimiter = new PauseTimeDelimiter(2000);
	private ArrayList<KeyStroke> key_events;
	private ArrayList<BurstType_PP> ppBurstArray;
	private int startIndex;
	private int startIndex2;
	private int endIndex;
	private DataNode CurrentDataNode;
	
	// 2 (Each burst contains all keys including shift, backspace, delete, alpha numeric, space, etc..)
	// 1 (Each burst contains replayed characters, ie.., the product that comes out on a text editor after executing deletes, backspaces, etc... in line revised text)
	// 1 (Should not be confused with the final text in the database, which we do not use at all!!!!)
	private int mode = 1;
	private boolean write_to_file = false;
	private static boolean write_header = true;
	///////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	public PP_Burst() { }
	
	public DataNode getIntermediateData() {	
		return CurrentDataNode;
	}
	
	public ArrayList<BurstType_PP> getBurstArray() {	
		return ppBurstArray;
	}
	
	@Override
	public Collection<Feature> extract(DataNode data) {
		//CurrentDataNode = data;
		//BurstBuilder bb = new BurstBuilder(new PPBurst());
		//LinkedList<Long> burstTimes = new LinkedList<Long>();
		CurrentDataNode = new DataNode();
		CurrentDataNode.setUserID(data.getUserID());
		System.out.println("User id: " + data.getUserID());
		ppBurstArray = new ArrayList<BurstType_PP>();
		
		int bursts_per_answer = 0;
		// Collect all bursts across all answers
		for (Answer a: data) {
			
			key_events = new ArrayList<KeyStroke>(a.getKeyStrokeList());
			
			System.out.println("Question id: " + a.getQuestionID());
			
			//for (int p = 0; p < key_events.size(); p++)
			//	System.out.println("Beginning VK code of answer: " + key_events.get(p).getKeyCode());
			
			//System.out.println("For question: "+ a.getQuestionID() + ", Length: " + key_events.size() + ", Beginning VK code: " + key_events.get(0).getKeyCode() + ", Last VK code: " + key_events.get(key_events.size()-1).getKeyCode() + ", Beginning Cur Pos: " + key_events.get(0).getCursorPosition() + ", End Cur Pos: " + key_events.get(key_events.size()-1).getCursorPosition());

			boolean no_burst = true;
			startIndex = 0;
			startIndex2 = 0;
			int before_pause = 0;
			int after_pause = 0;
			
			bursts_per_answer = 0;
			
			// Get the index of the first key press. First few keys might be key releases.
			while (startIndex < key_events.size() && key_events.get(startIndex).isKeyRelease()) { startIndex++; }
			
			// This is to retain the original start index, which is the start index of the entire answer.
			startIndex2 = startIndex;
			
			// From the index of the first key press, iterate through the event list and extract the bursts.
			for(endIndex = startIndex + 1; endIndex < key_events.size()-1; endIndex++) {
				
				KeyStroke previous_key_event =  key_events.get(endIndex - 1);
				KeyStroke current_key_event =  key_events.get(endIndex);
				
				if ((previous_key_event.isKeyRelease() && current_key_event.isKeyPress())) {
					//&&	key_before_delimiter.getKeyCode() != key_after_delimiter.getKeyCode()) {
					
					if (pauseDelimiter.isDelimiter(previous_key_event, current_key_event)) {
						
						if (startIndex < endIndex) {
							
							System.out.println("start_index: " + startIndex);
							System.out.println("end_index: " + endIndex);
								
							//The current pause is the 'pause after' for current burst.
							after_pause = (int)(current_key_event.getWhen() - previous_key_event.getWhen());
							
							List<KeyStroke> burst_keys = key_events.subList(startIndex, endIndex);
							//System.out.println("For question: "+ a.getQuestionID() + ", Star index: "+ startIndex + ", End index: " + endIndex + ", Length: " + burst_keys.size() + ", Beginning VK code: " + burst_keys.get(0).getKeyCode() + ", Last VK code: " + burst_keys.get(burst_keys.size()-1).getKeyCode());
							BurstType_PP burst = new BurstType_PP(new ArrayList<KeyStroke>(burst_keys));
							
							//The current pause is the 'pause after' for current burst and 'pause before' for the next burst
							burst.SetPauseAfter(after_pause);
							burst.SetPauseBefore(before_pause);
								
							// Add only the bursts that are valid for lexical analysis.
							if (burst.isValid()) {
								ppBurstArray.add(burst);
								CurrentDataNode.add(SegmentAnswer.BetweenKeyStrokes(a, startIndex, endIndex-1, mode));
								no_burst = false;
								bursts_per_answer++;
							}
							
							//The current pause is the 'pause before' for the next burst
							before_pause = after_pause;
						}
						startIndex = endIndex;
					}
				}
			}
			// If no burst is found, then the entire answer qualifies as a PP_burst
			if ((no_burst) && (endIndex > startIndex + 1)) {
				
				System.out.println("start_index: " + startIndex);
				System.out.println("end_index: " + endIndex);
				
				List<KeyStroke> burst_keys = key_events.subList(startIndex2, endIndex);
				BurstType_PP burst = new BurstType_PP(new ArrayList<KeyStroke>(burst_keys));
				burst.SetPauseAfter(after_pause);
				burst.SetPauseBefore(before_pause);
				ppBurstArray.add(burst);
				CurrentDataNode.add(SegmentAnswer.BetweenKeyStrokes(a, startIndex, endIndex-1, mode));
				bursts_per_answer++;
			}
			
			if (write_to_file) {
				try {
				    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("C:\\Users\\Sathya\\Desktop\\PP_BurstsPerAnswer.csv", true)));
				    if (write_header) {
				    	out.println("UserId,QuestionId,BurstCount");
				    	write_header = false;
				    }
				    out.println(data.getUserID() + "," + a.getQuestionID() + "," + bursts_per_answer);
				    out.close();
				} catch (Exception e) {
				    System.out.println("Error writing file");
				}
			}
		}
			
		// Extract features from the bursts collection
		//LinkedList<Long> burstTimes = new LinkedList<Long>();
		//LinkedList<Integer> charCount = new LinkedList<Integer>();
		LinkedList<Integer> ksCount = new LinkedList<Integer>();
		LinkedList<Double> ratio = new LinkedList<Double>();
		LinkedList<Double> ratio2 = new LinkedList<Double>();
		LinkedList<Long> burstTimes = new LinkedList<Long>();
		LinkedList<Integer> beforeBurstPause = new LinkedList<Integer>();
		LinkedList<Integer> afterBurstPause = new LinkedList<Integer>();
		LinkedList<Double> burstKeyDensity = new LinkedList<Double>();
		LinkedList<Double> preBurstKeyDensity = new LinkedList<Double>();
		LinkedList<Double> preBurstKeyDensity2 = new LinkedList<Double>();
		LinkedList<Double> postBurstKeyDensity = new LinkedList<Double>();
		LinkedList<Double> postBurstKeyDensity2 = new LinkedList<Double>();
		
		//Integer[] pause_array = (Integer[])Burst_delim.toArray();
		
		for (BurstType_PP b : ppBurstArray) {
			long burst_time = b.burstTime();
			//This gets the burst times
			burstTimes.add(burst_time / 1000);
			ksCount.add(b.burstChars());
			beforeBurstPause.add(b.GetPauseBefore());
			afterBurstPause.add(b.GetPauseAfter());
			//burstKeyDensity.add((double)b.burstChars()/b.burstTime());
			preBurstKeyDensity.add((double)(b.burstChars() * 1000)/b.GetPauseBefore());
			preBurstKeyDensity2.add((double)(b.burstAlphaChars() * 1000)/b.GetPauseBefore());
			postBurstKeyDensity.add((double)(b.burstChars() * 1000)/b.GetPauseAfter());
			postBurstKeyDensity2.add((double)(b.burstAlphaChars() * 1000)/b.GetPauseAfter());
			//This computes the typing speed
			if (burst_time >= 0) {
				/*System.out.println("burst-chars: " + b.burstChars());
				System.out.println("burst-alpha-chars: " + b.burstAlphaChars());
				System.out.println("burst-words: " + b.burstWords());
				System.out.println("burst-time: " + b.burstTime());
				System.out.println("burst-pre: " + b.GetPauseBefore());
				System.out.println("burst-post: " + b.GetPauseAfter()+ "\n");*/
				double r = (double)(b.burstChars() * 1000)/(double)burst_time;
				double r2 = (double)(b.burstAlphaChars() * 1000)/(double)burst_time;
				ratio.add(r);
				ratio2.add(r2);
			}
		}
		
		LinkedList<Feature> output = new LinkedList<Feature>();
		//output.add(new Feature("PP_Burst_Time", burstTimes));
		output.add(new Feature("PP_Burst_Type_Speed", ratio));
		output.add(new Feature("PP_Burst_Type_Speed_Alpha", ratio2));
		//output.add(new Feature("PP_KeystrokeCount",ksCount));
		//output.add(new Feature("PP_Before",beforeBurstPause));
		//output.add(new Feature("PP_After", afterBurstPause));
		//output.add(new Feature("PP_BurstKeyDensity", burstKeyDensity));
		output.add(new Feature("PP_PreBurst", preBurstKeyDensity));
		output.add(new Feature("PP_PreBurst_Alpha", preBurstKeyDensity2));
		output.add(new Feature("PP_PostBurst", postBurstKeyDensity));
		output.add(new Feature("PP_PostBurst_Alpha", postBurstKeyDensity2));
		//output.add(new Feature("PP_Burst_Time",burstTimes));
		//output.add(new Feature("PP_Char_Count",charCount));
		return output;
	}
	
	@Override
	public String getName() {
		return "PP Burst Metrics";
	}
}
