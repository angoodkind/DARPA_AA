package features.nyit;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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

public class PR_P_BURST implements ExtractionModule {
	
	private enum state {
		INITIAL,
		FIRST_P,
		FIRST_R,
		FIRST_PR,
		SECOND_P,
		SECOND_R,
		SECOND_PR,
		FINAL		
	}
	
	private class StartEnd {
		private int SIndex = 0;
		private int EIndex = 0;
		private int pause_before = 0;
		private int pause_after = 0;
		
		public StartEnd(int sIndex, int eIndex) {
			SIndex = sIndex;
			EIndex = eIndex;
		}
		
		public int StartIndex() {
			return SIndex;
		}
		
		public int EndIndex() {
			return EIndex;
		}
		
		public void SetPrePause(int pause) {
			pause_before = pause;
		}
		
		public void SetPostPause(int pause) {
			pause_after = pause;
		}
		
		public int GetPrePause() {
			return pause_before;
		}
		
		public int GetPostPause() {
			return pause_after;
		}
	}
	
		
    // 2 (Each burst contains all keys including shift, backspace, delete, alpha numeric, space, etc..)
	// 1 (Each burst contains replayed characters, ie.., the product that comes out on a text editor after executing deletes, backspaces, etc... in line revised text)
	// 1 (Should not be confused with the final text in the database, which we do not use at all!!!!)
	/*@author: Zdenka Sitova
	 * sets pause_time_in_ms variable whenever we want to TODO if I knew how to get instance of this class I wouldn't need to make this static
	 */
	
	public static void setPauseTimeInMs(int pauseLength) {
		pause_time_in_ms = pauseLength;
		pauseDelimiter = new PauseTimeDelimiter(pause_time_in_ms);
	}
	private static int pause_time_in_ms = 1000;
	private static final int pr_dist = 1;
	private static final int min_revision_count = 3;
	private static int mode = 1;
	
	
	
	// This is meant to write the burst counts for each user to a csv file.
	private boolean write_to_file = false;
	private static boolean write_header = true;
	/////////////////////////////////////////////////////////////////////////////////////////////////
	
	private state delim_state;
	
	private static PauseTimeDelimiter pauseDelimiter = new PauseTimeDelimiter(pause_time_in_ms);
	private LinkedList<StartEnd> clippings = new LinkedList<StartEnd>();
	private ArrayList<KeyStroke> key_events;
	private ArrayList<BurstType_RP> rpBurstArray;
	private int startIndex;
	private int endIndex;
	private DataNode CurrentDataNode;
	
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
				
				// Collect all bursts across all answers
				for (Answer a: data) {
					
					int bursts_per_answer = 0;
					
					key_events = new ArrayList<KeyStroke>(a.getKeyStrokeList());
					
					clippings.clear();
					boolean no_burst = true;
					
					boolean add_clip = false;
					
					bursts_per_answer = 0;
					
					System.out.println("Question id: " + a.getQuestionID());
					//for (int p = 0; p < key_events.size(); p++)
					//	System.out.println("Beginning VK code of answer: " + key_events.get(p).getKeyCode());
					
					//System.out.println("For question: "+ a.getQuestionID() + ", Length: " + key_events.size() + ", Beginning VK code: " + key_events.get(0).getKeyCode() + ", Last VK code: " + key_events.get(key_events.size()-1).getKeyCode() + ", Beginning Cur Pos: " + key_events.get(0).getCursorPosition() + ", End Cur Pos: " + key_events.get(key_events.size()-1).getCursorPosition());

					startIndex = 0;
					
					int prev_before_pause = 0;
					int before_pause = 0;
					int after_pause = 0;
					
					int prev_start_pause_index = 0;
					int start_pause_index = 0;
					int end_pause_index = 0;
					
					int revision_count = 0;
					int PR_char_count = 0;		

					boolean reset_revision_count = false;
					boolean reset_PR_char_count = false;
					
					int r1_start_index = 0;
					int r1_end_index = 0;
					
					int pause_index = 0;
					int revision_start_index = 0;
					int revision_end_index = 0;
					
					int current_pause = 0;
					
					delim_state = state.INITIAL;
										
					while (startIndex < key_events.size() && key_events.get(startIndex).isKeyRelease()) { startIndex++; }
					
					//Get the index of the last key press
					int end_key_press_marker = key_events.size()-1;
					while ( end_key_press_marker >= 0 && key_events.get(end_key_press_marker).isKeyRelease()) { end_key_press_marker--;}
					
					for(endIndex = startIndex + 1; endIndex < key_events.size()-1; endIndex++) {
						int rev_depth = 0;
						
						KeyStroke previous_key_event =  key_events.get(endIndex - 1); // Initialize to start Index position, same as previous_key_event = startIndex
						KeyStroke current_key_event =  key_events.get(endIndex);      // Initialize to end Index position, same as previous_key_event = endIndex
						
						if (current_key_event.isKeyPress()) {//Check for a key press to identify a possible 'pause' occurence
							
							if (previous_key_event.isKeyRelease()) {//Check for a preceding key release to identify a possible 'pause' occurence
								
								if (pauseDelimiter.isDelimiter(previous_key_event, current_key_event)) {//If a pause is detected
									
									reset_revision_count = true;
									reset_PR_char_count = true;
									pause_index = endIndex; // This is current key, index where key press has occurred. This is the start of a potential burst
									
									current_pause = (int)(current_key_event.getWhen() - previous_key_event.getWhen()); //Measure the duration of the pause
									
									switch (delim_state) {
										case INITIAL: {
											delim_state = state.FIRST_P;
											start_pause_index = endIndex;
											before_pause = current_pause;
											break;
										}
										case FIRST_P: {
											delim_state = state.FIRST_P;
											prev_start_pause_index = start_pause_index;
											prev_before_pause = before_pause;
											start_pause_index = endIndex;   // endIndex is a key press event, which marks the end of a pause. It is also the first key of the burst.   
											before_pause = current_pause;  // Measure the duration of the pause...
											break;
										}
										case FIRST_PR: {
											end_pause_index = endIndex;   // endIndex is a key press event, which marks the end of a pause. It is also the first key of the burst.   
											after_pause = current_pause;  // Measure the duration of the pause...
											delim_state = state.SECOND_P; // Change the state variable to Second P state. 
											break;
										}
										case SECOND_P: {
											//currentPair = new StartEnd(start_pause_index, end_pause_index);
											//Check Point: We will not be able to detect a pause between BACKSPACES because this code is assuming that at any iteration either pause happens or revision happens, but not both....  
											//Is their a way to cut only at one place
											System.out.println("PAUSE AT: " + endIndex);
											add_clip = true;
												
											break;
										}
									}

								}
							}
							
							switch (current_key_event.getKeyCode()) {//Does the key press event belong to a Revision Count
								case 8: {
									revision_count++;
									System.out.println("AT: " + endIndex + " REVISION COUNTER: " + revision_count);
									if (revision_count == 1) {
										revision_start_index = endIndex; //endIndex is a key press event, which marks the end of the pause and here, press of a backspace key
									}
									revision_end_index = endIndex;
									break;
								}
								default: {							
									if (revision_count >= min_revision_count) { //PR_char_count <= pr_dist
										revision_end_index = endIndex;
										//How does this warrant a change of state, because this does not ensure PR_char_count < =  pr_dist
										if (delim_state == state.FIRST_P)
											delim_state = state.FIRST_R;
										if (delim_state == state.SECOND_P)
											delim_state = state.SECOND_R;
									}
									else
										PR_char_count++;
									
									reset_revision_count = true;
								}
							
							}//End of state change to revision, either R1 or R2......
							System.out.println("THIS STATE IS: " + delim_state);
							rev_depth = revision_count;
												
								switch (delim_state) {
									case FIRST_R: {
										if (PR_char_count <= pr_dist) {//Confirm that this is a "Pause followed By a Revision" and not a lone "Pause"
											if (revision_end_index == pause_index) {
												start_pause_index = prev_start_pause_index;
												before_pause = prev_before_pause;
												end_pause_index = pause_index;
												after_pause = current_pause;
												delim_state = state.SECOND_P;
											}
											else
												delim_state = state.FIRST_PR;

											r1_start_index = revision_start_index;
											r1_end_index = revision_end_index;
										}
										else
											delim_state = state.INITIAL; // Switch back to Pause 1. You could also make revision_count = 0;
										break;
									}
									case SECOND_R: {
										if (PR_char_count <= pr_dist) {
											if (revision_end_index == pause_index) {
												start_pause_index = end_pause_index;
												end_pause_index = pause_index;
												before_pause = after_pause;
												after_pause = current_pause;
												delim_state = state.SECOND_P;
											}
											else {
												start_pause_index = end_pause_index;
												end_pause_index = 0;
												before_pause = after_pause;
												delim_state = state.FIRST_PR;
											}
											
											r1_start_index = revision_start_index;
											r1_end_index = revision_end_index;
										}
										else {
											delim_state = state.SECOND_P;
										}
										break;
									}
								}//End of state change to initial (if PR occurs), first P (if first R does not occur), or perform a PP clip (if second R does not occur)					
						}
						
						//Clip if you reached the end of the keystroke array and also is in P2 state... 
						if (endIndex == end_key_press_marker && delim_state == state.SECOND_P) {
							add_clip = true;
						}
						
						//System.out.println(">>>>>>>>AT: " + endIndex + " REVISION_DEPTH: " + rev_depth);
						
						// This block adds new burst clip
						if (add_clip && rev_depth < min_revision_count) {
							
							StartEnd clip = new StartEnd(start_pause_index, end_pause_index); //  Give the beginning and ending indices to be clipped. Here, we have pause indices start and end of pause, so the clipping is between pauses
							//StartEnd clip = new StartEnd(start_pause_index, revision_end_index);
							//If we want to do a clip between pause and a revision, use start_pause_index and revision_end_index, will include the revisions in the burst.....
							//System.out.println("HAPPENNING AT: " + endIndex);
							
							//StartEnd clip = new StartEnd(r1_end_index, end_pause_index); // Use this if you want to use R1 start or end indicies.
							
							System.out.println("AT: " + endIndex + " REVISION_DEPTH: " + rev_depth + " MIN_REV_COUNT: " + min_revision_count);
							System.out.println("FROM: " + start_pause_index + " TO: " + end_pause_index);
							clip.SetPrePause(before_pause);
							clip.SetPostPause(after_pause);
							clippings.add(clip);
							
							if (delim_state == state.SECOND_P && pause_index > end_pause_index) {
								start_pause_index = pause_index;
								end_pause_index = 0;
								before_pause = current_pause;
								after_pause = 0;
								delim_state = state.FIRST_P;
							}
							add_clip = false;
						}
						else
							add_clip = false;
						
						/// Implement counter resets here
						if (reset_revision_count) {
							if (current_key_event.getKeyCode() == 8)
								revision_count = 1;
							else
								revision_count = 0;
							
							reset_revision_count = false;
						}
						
						if (reset_PR_char_count) {
							if (current_key_event.getKeyCode() == 8)
									PR_char_count = 0;						
							else
								PR_char_count = 1;
							
							reset_PR_char_count = false;
						}
							
					}
					
					for (int se_index = 0; se_index < clippings.size(); se_index++) {
						
						StartEnd clip = clippings.get(se_index);
						
						if (clip.StartIndex() >= clip.EndIndex())
							continue;
						
						System.out.println("clip start_index: " + clip.StartIndex());
						System.out.println("before pause: " + clip.GetPrePause());
						System.out.println("clip end_index: " + clip.EndIndex());
						System.out.println("after pause: " + clip.GetPostPause());
						
						List<KeyStroke> burst_keys = key_events.subList(clip.StartIndex(), clip.EndIndex());
						BurstType_RP burst = new BurstType_RP(new ArrayList<KeyStroke>(burst_keys));
						
						burst.SetPauseAfter(clip.GetPostPause());
						burst.SetPauseBefore(clip.GetPrePause());
						

						if (burst.isValid()) {
							rpBurstArray.add(burst);
							CurrentDataNode.add(SegmentAnswer.BetweenKeyStrokes(a, clip.StartIndex(), clip.EndIndex()-1, mode));
							no_burst = false;
							bursts_per_answer++;
						}
					}
					
					
					if (write_to_file) {
						try {
							String output_file = CreateFileOnce.GetAbsoluteFilePath();
						    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(output_file, true)));
						    if (write_header) {
						    	out.println("UserId,QuestionId,BurstCount");
						    	write_header = false;
						    }
						    out.println(data.getUserID() + "," + a.getQuestionID() + "," + bursts_per_answer);
						    out.close();
						} catch (Exception e) {
						    System.out.println("Error writing burst count file");
						}
					}
				}
				
				LinkedList<Feature> output = new LinkedList<Feature>();
				
				/// Computing new features here.
				
				LinkedList<Integer> ksCount1 = new LinkedList<Integer>();
				LinkedList<Double> ratio1 = new LinkedList<Double>();
				LinkedList<Double> ratio2 = new LinkedList<Double>();
				LinkedList<Long> burstTimes1 = new LinkedList<Long>();
				LinkedList<Integer> afterBurstPause1 = new LinkedList<Integer>();
				LinkedList<Double> burstKeyDensity1 = new LinkedList<Double>();
				
				LinkedList<Double> preBurstKeyDensity1 = new LinkedList<Double>();
				LinkedList<Double> preBurstKeyDensity2 = new LinkedList<Double>();
				LinkedList<Double> postBurstKeyDensity1 = new LinkedList<Double>();
				LinkedList<Double> postBurstKeyDensity2 = new LinkedList<Double>();
				
				int counter = 0;
				for (BurstType_RP b : rpBurstArray) {
					System.out.println("Counter: " + (++counter));
					long burst_time = b.burstTime();
					//This gets the burst times
					burstTimes1.add(burst_time/1000);
					ksCount1.add(b.burstChars());
					afterBurstPause1.add(b.GetPauseAfter());
					burstKeyDensity1.add((double)b.burstChars()/b.burstTime());
					preBurstKeyDensity1.add((double)b.burstChars()/b.GetPauseBefore());
					preBurstKeyDensity2.add((double)b.burstAlphaChars()/b.GetPauseBefore());
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
				
				output.add(new Feature("RP_Burst_Type_Speed", ratio1));
				output.add(new Feature("RP_Burst_Type_Speed_Alpha", ratio2));
				
				output.add(new Feature("RP_PreBurst", preBurstKeyDensity1));
				output.add(new Feature("RP_PreBurst_Alpha", preBurstKeyDensity2));
				output.add(new Feature("RP_PostBurst", postBurstKeyDensity1));
				output.add(new Feature("RP_PostBurst_Alpha", postBurstKeyDensity2));
				//output.add(new Feature("RP1_Char_Count",ksCount1));
				
				return output;
	}
	
	private static class CreateFileOnce {
		private static boolean write_switch = true;
		private static String output_file_path = "";
		
		public static String GetAbsoluteFilePath() {
			if (write_switch) {
				write_switch = false;
				String workingDir = System.getProperty("user.dir");
				java.io.File output_dir = new java.io.File(workingDir,"BurstStats/R-P");
				output_dir.mkdirs();
				Date dateBegin = new Date();
				SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd[H-m-s]Z");
				StringBuilder now = new StringBuilder(dateformat.format(dateBegin));
				output_file_path = new java.io.File(output_dir.getAbsolutePath(), "RP_BurstsPerAnswer" + now + ".csv").toString();
				
			}
			
			return output_file_path;
		}
	}
	
	@Override
	public String getName() {
		return "RP Burst Metrics";
	}

}
