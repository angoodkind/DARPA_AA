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
import features.bursts.BurstType_RR;
import keystroke.KeyStroke;
import output.util.SegmentAnswer;

public class RR_Burst implements ExtractionModule {
	
	private ArrayList<KeyStroke> key_events;
	private ArrayList<BurstType_RR> rrBurstArray;
	private int startIndex;
	private int endIndex;
	private DataNode CurrentDataNode;
	
    // 2 (Each burst contains all keys including shift, backspace, delete, alpha numeric, space, etc..)
	// 1 (Each burst contains replayed characters, ie.., the product that comes out on a text editor after executing deletes, backspaces, etc... in line revised text)
	// 1 (Should not be confused with the final text in the database, which we do not use at all!!!!)
	private int mode = 1;
	private boolean write_to_file = false;
	private static boolean write_header = true;
	
	public DataNode getIntermediateData() {	
		return CurrentDataNode;
	}
	
	public ArrayList<BurstType_RR> getBurstArray() {	
		return rrBurstArray;
	}

	@Override
	public Collection<Feature> extract(DataNode data) {
		//CurrentDataNode = data;
				//BurstBuilder bb = new BurstBuilder(new PPBurst());
				//LinkedList<Long> burstTimes = new LinkedList<Long>();
				CurrentDataNode = new DataNode();
				CurrentDataNode.setUserID(data.getUserID());
				System.out.println("User id: " + data.getUserID());
				rrBurstArray = new ArrayList<BurstType_RR>();
				
				int bursts_per_answer = 0;
				
				// Collect all bursts across all answers
				for (Answer a: data) {
					
					key_events = new ArrayList<KeyStroke>(a.getKeyStrokeList());
					//for (int p = 0; p < key_events.size(); p++)
					//	System.out.println("Beginning VK code of answer: " + key_events.get(p).getKeyCode());
					
					//System.out.println("For question: "+ a.getQuestionID() + ", Length: " + key_events.size() + ", Beginning VK code: " + key_events.get(0).getKeyCode() + ", Last VK code: " + key_events.get(key_events.size()-1).getKeyCode() + ", Beginning Cur Pos: " + key_events.get(0).getCursorPosition() + ", End Cur Pos: " + key_events.get(key_events.size()-1).getCursorPosition());

					startIndex = 0;
					int start_revision_index = 0;
					int end_revision_index = 0;
					String revision_type = null;
					
					int bks_counter = 0;
					int del_counter = 0;
					
					bursts_per_answer = 0;
					
					while (startIndex < key_events.size() && key_events.get(startIndex).isKeyRelease()) { startIndex++; }
					
					for(endIndex = startIndex + 1; endIndex < key_events.size()-1; endIndex++) {
						
						KeyStroke current_key_event =  key_events.get(endIndex);
						
						//String key_text = KeyStroke.getKeyText(key_after_delimiter.getID());
						//System.out.println("key: " + key_after_delimiter.getKeyCode());
						
						// Find the index where the pause occurs.
						// Revision key is either 'DEL' (VK code 46 (2e in hex) in decimal) or 'BACKSPACE' (VK code 8 in decimal)
						if (current_key_event.isKeyPress()) {
							
							switch (current_key_event.getKeyCode()) {
								case 8: {
									bks_counter++;
									break;
								}
								case 127: {
									del_counter++;
									break;
								}
								default: {
									if (bks_counter >= 2 || del_counter >= 2) {
										if (start_revision_index == 0 && end_revision_index == 0)
											start_revision_index = endIndex;
										else {
											if (start_revision_index > 0)
												end_revision_index = endIndex;
										}
									}
									bks_counter = 0;
									del_counter = 0;
								}
							}
						}
						
						if (start_revision_index > 0 && end_revision_index > 0
								&& end_revision_index > start_revision_index	) {
							
								System.out.println("start_revision_index: " + start_revision_index);
								System.out.println("end_revision_index: " + end_revision_index);
								
								List<KeyStroke> burst_keys = key_events.subList(start_revision_index, end_revision_index);
								
								BurstType_RR burst = new BurstType_RR(new ArrayList<KeyStroke>(burst_keys));
								
								if (burst.isValid()) {
									rrBurstArray.add(burst);
									CurrentDataNode.add(SegmentAnswer.BetweenKeyStrokes(a, start_revision_index, end_revision_index-1, mode));
									bursts_per_answer++;
								}
								
								start_revision_index = end_revision_index;
								end_revision_index = 0;
						}
						
						/*if (current_key_event.isKeyRelease() &&
							(current_key_event.getKeyCode() == 8 || current_key_event.getKeyCode() == 127)) {
							
							if (start_revision_index > 0 && end_revision_index > 0
									&& end_revision_index > start_revision_index	) {
									System.out.println("start_revision_index: " + start_revision_index);
									System.out.println("end_revision_index: " + end_revision_index);
									
									List<KeyStroke> burst_keys = key_events.subList(start_revision_index, end_revision_index + 1);
									
									BurstType_RR burst = new BurstType_RR(new ArrayList<KeyStroke>(burst_keys));
									
									if (burst.isValid()) {
										rrBurstArray.add(burst);
										CurrentDataNode.add(SegmentAnswer.BetweenKeyStrokes(a, start_revision_index, end_revision_index, mode));
										bursts_per_answer++;
									}
									
									start_revision_index = 0;
									end_revision_index = 0;
								}
						}*/
						
					}
					
					if (write_to_file) {
						try {
						    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("C:\\Users\\Sathya\\Desktop\\RR_BurstsPerAnswer.csv", true)));
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
				LinkedList<Double> burstKeyDensity1 = new LinkedList<Double>();
				
				for (BurstType_RR b : rrBurstArray) {
						long burst_time = b.burstTime();
						//This gets the burst times
						burstTimes1.add(burst_time/1000);
						ksCount1.add(b.burstChars());
						burstKeyDensity1.add((double)b.burstChars()/b.burstTime());
						
						//This computes the typing speed
						if (burst_time >= 0) {
							double r = (double)(b.burstChars() * 1000)/(double)burst_time;
							ratio1.add(r);
							double r2 = (double)(b.burstChars() * 1000)/(double)burst_time;
							ratio2.add(r2);
						}
				}
				
				output.add(new Feature("RR_Burst_Type_Speed", ratio1));
				output.add(new Feature("RR_Burst_Type_Speed_Alpha", ratio2));
				//output.add(new Feature("RP1_Char_Count",ksCount1));
				
				return output;
	}
	
	@Override
	public String getName() {
		return "RR Burst Metrics";
	}

}
