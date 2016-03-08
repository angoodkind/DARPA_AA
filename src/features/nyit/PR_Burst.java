package features.nyit;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import features.bursts.BurstType_PR;
import features.bursts.PauseTimeDelimiter;
import keystroke.KeyStroke;
import output.util.SegmentAnswer;

public class PR_Burst implements ExtractionModule {	

	private PauseTimeDelimiter pauseDelimiter = new PauseTimeDelimiter(2000);
	private ArrayList<KeyStroke> key_events;
	private ArrayList<BurstType_PR> prBurstArray;
	private int startIndex;
	private int endIndex;
	private DataNode CurrentDataNode;
	
    // 2 (Each burst contains all keys including shift, backspace, delete, alpha numeric, space, etc..)
	// 1 (Each burst contains replayed characters, i.e.., the product that comes out on a text editor after executing deletes, backspaces, etc... in line revised text)
	// 1 (Should not be confused with the final text in the database, which we do not use at all!!!!)
	private int mode = 1; //
	private boolean write_to_file = false; 
	private static boolean write_header = true;
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public DataNode getIntermediateData() {	
		return CurrentDataNode;
	}
	
	public ArrayList<BurstType_PR> getBurstArray() {	
		return prBurstArray;
	}

	@Override
	public Collection<Feature> extract(DataNode data) {
		
				CurrentDataNode = new DataNode();
				CurrentDataNode.setUserID(data.getUserID());
				System.out.println("User id: " + data.getUserID());
				prBurstArray = new ArrayList<BurstType_PR>();
				
				int bursts_per_answer = 0;
				
				// Collect all bursts across all answers
				for (Answer a: data) {
					
					key_events = new ArrayList<KeyStroke>(a.getKeyStrokeList());
					//for (int p = 0; p < key_events.size(); p++)
					//	System.out.println("Beginning VK code of answer: " + key_events.get(p).getKeyCode());
					
					//System.out.println("For question: "+ a.getQuestionID() + ", Length: " + key_events.size() + ", Beginning VK code: " + key_events.get(0).getKeyCode() + ", Last VK code: " + key_events.get(key_events.size()-1).getKeyCode() + ", Beginning Cur Pos: " + key_events.get(0).getCursorPosition() + ", End Cur Pos: " + key_events.get(key_events.size()-1).getCursorPosition());

					startIndex = 0;
					int pause_index = 0;
					int pause_before = 0;
					String revision_type = null;
					
					bursts_per_answer = 0;
					
					// Get the index of the first key press. First few keys might be key releases.
					while (startIndex < key_events.size() && key_events.get(startIndex).isKeyRelease()) { startIndex++; }
					
					for(endIndex = startIndex + 1; endIndex < key_events.size()-1; endIndex++) {
						KeyStroke previous_key_event =  key_events.get(endIndex - 1);
						KeyStroke current_key_event =  key_events.get(endIndex);
						
						//String key_text = KeyStroke.getKeyText(key_after_delimiter.getID());
						//System.out.println("key: " + key_after_delimiter.getKeyCode());
						
						if (current_key_event.isKeyPress()) {
							
							// Find the index where the pause occurs.
							if (previous_key_event.isKeyRelease() && pauseDelimiter.isDelimiter(previous_key_event, current_key_event) ) {
								
								pause_before = (int)(current_key_event.getWhen() - previous_key_event.getWhen());
								pause_index = endIndex;
							}
							
							// Find the key that either 'DEL' (VK code 46 (2e in hex) in decimal) or 'BACKSPACE' (VK code 8 in decimal)
							if (pause_index > -1 && (current_key_event.getKeyCode() == 8 || current_key_event.getKeyCode() == 127)) {
								
								switch (current_key_event.getKeyCode()) {
									case 8: {
										revision_type = "Backspace";
										break;
									}
									case 127: {
										revision_type = "Delete";
										break;
									}
									default: {
										revision_type = "";
									}
								}
								
								// To make sure that the revision occurred after a pause, check if the revision index
								// is greater than the pause index.
								if (pause_index < endIndex) {
									
									System.out.println("pause_index: " + pause_index);
									System.out.println("end_index: " + endIndex);
																
									List<KeyStroke> burst_keys = key_events.subList(pause_index, endIndex);
									//System.out.println("For question: "+ a.getQuestionID() + ", Star index: "+ startIndex + ", End index: " + endIndex + ", Length: " + burst_keys.size() + ", Beginning VK code: " + burst_keys.get(0).getKeyCode() + ", Last VK code: " + burst_keys.get(burst_keys.size()-1).getKeyCode());
									BurstType_PR burst = new BurstType_PR(new ArrayList<KeyStroke>(burst_keys));
									
									//The current pause is the 'pause before' for current burst
									burst.SetPauseBefore(pause_before);
									
									burst.SetRevisionTypeAfter(revision_type);
									
									System.out.println(revision_type);
									
									// Add only the bursts that are valid for lexical analysis.
									if (burst.isValid()) {
										prBurstArray.add(burst);
										CurrentDataNode.add(SegmentAnswer.BetweenKeyStrokes(a, pause_index, endIndex-1, mode));
										bursts_per_answer++;
									}
									
									// Reset the pause index
									pause_index = -1;
								}
								else {
									if (pause_index != endIndex) 
										pause_index = 0;
									System.out.println("pause_index: " + pause_index);
									System.out.println("end_index: " + endIndex);
								}
							}
						}
					}
					
					if (write_to_file)
					{
						try {
						    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("C:\\Users\\Sathya\\Desktop\\PR_BurstsPerAnswer.csv", true)));
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
				
				LinkedList<Feature> output = new LinkedList<Feature>();
				
				// Extract features from the bursts collection
				LinkedList<Integer> ksCount1 = new LinkedList<Integer>();
				LinkedList<Double> ratio1 = new LinkedList<Double>();
				LinkedList<Double> ratio2 = new LinkedList<Double>();
				LinkedList<Long> burstTimes1 = new LinkedList<Long>();
				LinkedList<Integer> beforeBurstPause1 = new LinkedList<Integer>();
				LinkedList<Double> burstKeyDensity1 = new LinkedList<Double>();
				LinkedList<Double> postBurstKeyDensity1 = new LinkedList<Double>();
				LinkedList<Double> postBurstKeyDensity2 = new LinkedList<Double>();
				
				for (BurstType_PR b : prBurstArray) {
					if (b.GetRevisionTypeAfter().equals("Backspace")) {
						long burst_time = b.burstTime();
						//This gets the burst times
						burstTimes1.add(burst_time/1000);
						ksCount1.add(b.burstChars());
						beforeBurstPause1.add(b.GetPauseBefore());
						burstKeyDensity1.add((double)b.burstChars()/b.burstTime());
						postBurstKeyDensity1.add((double)b.burstChars()/b.GetPauseBefore());
						postBurstKeyDensity2.add((double)b.burstAlphaChars()/b.GetPauseBefore());
						//This computes the typing speed
						if (burst_time >= 0) {
							double r = (double)(b.burstChars() * 1000)/(double)burst_time;
							ratio1.add(r);
							double r2 = (double)(b.burstAlphaChars() * 1000)/(double)burst_time;
							ratio2.add(r2);
						}
					}
				}
				
				output.add(new Feature("PR_Burst_Type_Speed", ratio1));
				output.add(new Feature("PR_Burst_Type_Speed_Alpha", ratio2));
				output.add(new Feature("PR_PostBurst", postBurstKeyDensity1));
				output.add(new Feature("PR_PostBurst_Alpha", postBurstKeyDensity2));
				//output.add(new Feature("PR_Char_Count",ksCount1));
				
				return output;
	}
	
	@Override
	public String getName() {
		return "PR Burst Metrics";
	}

}
