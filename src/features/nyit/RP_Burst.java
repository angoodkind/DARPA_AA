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
import features.bursts.BurstType_RP;
import features.bursts.PauseTimeDelimiter;
import keystroke.KeyStroke;
import output.util.SegmentAnswer;

public class RP_Burst implements ExtractionModule {
	
	private PauseTimeDelimiter pauseDelimiter = new PauseTimeDelimiter(2000);
	private ArrayList<KeyStroke> key_events;
	private ArrayList<BurstType_RP> rpBurstArray;
	private int startIndex;
	private int endIndex;
	private DataNode CurrentDataNode;
	
    // 2 (Each burst contains all keys including shift, backspace, delete, alpha numeric, space, etc..)
	// 1 (Each burst contains replayed characters, ie.., the product that comes out on a text editor after executing deletes, backspaces, etc... in line revised text)
	// 1 (Should not be confused with the final text in the database, which we do not use at all!!!!)
	private int mode = 1;
	private boolean write_to_file = false;
	private static boolean write_header = true;
	/////////////////////////////////////////////////////////////////////////////////////////////////
	
	public DataNode getIntermediateData() {	
		return CurrentDataNode;
	}
	
	public ArrayList<BurstType_RP> getBurstArray() {	
		return rpBurstArray;
	}

	@Override
	public Collection<Feature> extract(DataNode data) {
		//CurrentDataNode = data;
				//BurstBuilder bb = new BurstBuilder(new PPBurst());
				//LinkedList<Long> burstTimes = new LinkedList<Long>();
				CurrentDataNode = new DataNode();
				CurrentDataNode.setUserID(data.getUserID());
				System.out.println("User id: " + data.getUserID());
				rpBurstArray = new ArrayList<BurstType_RP>();
				
				int bursts_per_answer = 0;
				
				// Collect all bursts across all answers
				for (Answer a: data) {
					
					key_events = new ArrayList<KeyStroke>(a.getKeyStrokeList());
					//for (int p = 0; p < key_events.size(); p++)
					//	System.out.println("Beginning VK code of answer: " + key_events.get(p).getKeyCode());
					
					//System.out.println("For question: "+ a.getQuestionID() + ", Length: " + key_events.size() + ", Beginning VK code: " + key_events.get(0).getKeyCode() + ", Last VK code: " + key_events.get(key_events.size()-1).getKeyCode() + ", Beginning Cur Pos: " + key_events.get(0).getCursorPosition() + ", End Cur Pos: " + key_events.get(key_events.size()-1).getCursorPosition());

					startIndex = 0;
					int revision_index = 0;
					int pause_after = 0;
					String revision_type = null;
					
					bursts_per_answer = 0;
					
					while (startIndex < key_events.size() && key_events.get(startIndex).isKeyRelease()) { startIndex++; }
					
					for(endIndex = startIndex + 1; endIndex < key_events.size()-1; endIndex++) {
						
						KeyStroke previous_key_event =  key_events.get(endIndex - 1);
						KeyStroke current_key_event =  key_events.get(endIndex);
						
						//String key_text = KeyStroke.getKeyText(key_after_delimiter.getID());
						//System.out.println("key: " + key_after_delimiter.getKeyCode());
						
						// Find the index where the pause occurs.
						// Revision key is either 'DEL' (VK code 46 (2e in hex) in decimal) or 'BACKSPACE' (VK code 8 in decimal)
						if (current_key_event.isKeyPress()) {
							
							if (current_key_event.getKeyCode() == 8 || current_key_event.getKeyCode() == 127) {
									switch (current_key_event.getKeyCode()) {
										case 8: {
											revision_type = "Backspace";
											break;
										}
										case 127: {
											revision_type = "Delete";
											break;
										}
									}
								revision_index = endIndex;
							}
							
							// Find the key where pause occurs.
							if (revision_index > 0 && previous_key_event.isKeyRelease() &&
								pauseDelimiter.isDelimiter(previous_key_event, current_key_event)) {
								
								// Make sure that the pause follows a revision.
								if (revision_index < endIndex) {
									
									System.out.println("revision_index: " + revision_index);
									System.out.println("end_index: " + endIndex);
									
									pause_after = (int)(current_key_event.getWhen() - previous_key_event.getWhen());
									
									List<KeyStroke> burst_keys = key_events.subList(revision_index, endIndex);
									//System.out.println("For question: "+ a.getQuestionID() + ", Star index: "+ startIndex + ", End index: " + endIndex + ", Length: " + burst_keys.size() + ", Beginning VK code: " + burst_keys.get(0).getKeyCode() + ", Last VK code: " + burst_keys.get(burst_keys.size()-1).getKeyCode());
									BurstType_RP burst = new BurstType_RP(new ArrayList<KeyStroke>(burst_keys));
									
									//The current pause is the 'pause after' for current burst
									burst.SetPauseAfter(pause_after);
									
									burst.SetRevisionTypeBefore(revision_type);
									System.out.println(revision_type);
									//The current pause is the 'pause after' for the previous burst 
									//if (ppBurstArray.size() > 1) {
										//BurstType_PP previous_burst = ppBurstArray.get(ppBurstArray.size() - 1);
										//burst.SetPauseBefore(before_pause);
									//}
									
									// Add only the bursts that are valid for lexical analysis.
									if (burst.isValid()) {
										rpBurstArray.add(burst);
										CurrentDataNode.add(SegmentAnswer.BetweenKeyStrokes(a, revision_index, endIndex-1, mode));
										bursts_per_answer++;
									}
									
									// Reset the revision index.
									revision_index = 0;
								}
								else {
									// Thus ensures that if the key that was not involved in creating a pause (> 2 sec) is not a revision key
									if (revision_index != endIndex)
										revision_index = 0;
									System.out.println("revision_index: " + revision_index);
									System.out.println("end_index: " + endIndex);
								}
							}
						}
					}
					
					if (write_to_file)
					{
						try {
						    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("C:\\Users\\Sathya\\Desktop\\RP_BurstsPerAnswer.csv", true)));
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
				
				LinkedList<Integer> ksCount1 = new LinkedList<Integer>();
				LinkedList<Double> ratio1 = new LinkedList<Double>();
				LinkedList<Double> ratio2 = new LinkedList<Double>();
				LinkedList<Long> burstTimes1 = new LinkedList<Long>();
				LinkedList<Integer> afterBurstPause1 = new LinkedList<Integer>();
				LinkedList<Double> burstKeyDensity1 = new LinkedList<Double>();
				LinkedList<Double> postBurstKeyDensity1 = new LinkedList<Double>();
				LinkedList<Double> postBurstKeyDensity2 = new LinkedList<Double>();
				
				int counter = 0;
				for (BurstType_RP b : rpBurstArray) {
					System.out.println("Counter: " + (++counter));
					if (b.GetRevisionTypeBefore().equals("Backspace")) {
						long burst_time = b.burstTime();
						//This gets the burst times
						burstTimes1.add(burst_time/1000);
						ksCount1.add(b.burstChars());
						afterBurstPause1.add(b.GetPauseAfter());
						burstKeyDensity1.add((double)b.burstChars()/b.burstTime());
						postBurstKeyDensity1.add((double)b.burstChars()/b.GetPauseAfter());
						postBurstKeyDensity2.add((double)b.burstAlphaChars()/b.GetPauseAfter());
						
						//This computes the typing speed
						if (burst_time >= 0) {
							double r = (double)(b.burstChars() * 1000)/(double)burst_time;
							ratio1.add(r);
							double r2 = (double)(b.burstChars() * 1000)/(double)burst_time;
							ratio2.add(r2);
						}
					}
				}
				
				output.add(new Feature("RP1_Burst_Type_Speed", ratio1));
				output.add(new Feature("RP1_Burst_Type_Speed_Alpha", ratio2));
				output.add(new Feature("RP1_PostBurst", postBurstKeyDensity1));
				output.add(new Feature("RP1_PostBurst_Alpha", postBurstKeyDensity2));
				//output.add(new Feature("RP1_Char_Count",ksCount1));
				
				return output;
	}
	
	@Override
	public String getName() {
		return "RP Burst Metrics";
	}

}
