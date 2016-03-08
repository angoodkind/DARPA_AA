package features.nyit;

import java.io.BufferedWriter;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import appwindow.AppWindowScript;

import java.util.Arrays;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import features.bursts.BurstType_PP;
import features.bursts.PauseTimeDelimiter;
import keystroke.KeyStroke;
import output.util.SegmentAnswer;

public class P_P_BURST implements ExtractionModule {
	
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
	
	
	// mode = 2 (Each burst contains all keys including shift, backspace, delete, alpha numeric, space, etc..)
	// mode = 1 (Each burst contains replayed characters, i.e.., the product that comes out on a text editor after executing deletes, backspaces, etc... in line revised text)
	// mode = 1 (Should not be confused with the final text in the database, which we do not use at all!!!!)
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
	///////////////////////////////////////////////////////////////////////////////////////////////////
	
	private state delim_state;	
	private LinkedList<StartEnd> clippings = new LinkedList<StartEnd>();
	private static PauseTimeDelimiter pauseDelimiter = new PauseTimeDelimiter(pause_time_in_ms);//Define a pauseDelimiter
	private ArrayList<KeyStroke> key_events;
	private ArrayList<BurstType_PP> ppBurstArray;
	private int startIndex;
	private int endIndex;
	private DataNode CurrentDataNode;	
	
	public P_P_BURST() { }
	
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
		System.out.println("User id:>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>santhosh koyyada >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> " + data.getUserID());
		ppBurstArray = new ArrayList<BurstType_PP>();
		
		// Collect all bursts across all answers
		for (Answer a: data) {
			
			int bursts_per_answer = 0;
			
			key_events = new ArrayList<KeyStroke>(a.getKeyStrokeList());
			
			System.out.println("Question id: " + a.getQuestionID());
			
			clippings.clear();
			boolean no_burst = true;
			
			boolean add_clip = false;
			
			startIndex = 0;
			
			int before_pause = 0;
			int after_pause = 0;
			
			int start_pause_index = 0; //Start of the pause inside a burst
			int end_pause_index = 0;   //End of the pause inside a burst
			
			int revision_count = 0; // Counts the number of backspaces within a revision
			int PR_char_count = 0;  // Counts the number of characters between a pause and a revision
			
			boolean reset_revision_count = false;
			boolean reset_PR_char_count = false;
			
			int pause_index = 0;    
			int revision_start_index = 0; //Start of the revision index....
			int revision_end_index = 0;   //End of the revision index......
			
			int current_pause = 0;
			
			/// Set initial state as 'first pause' because before the start of an answer, a pause is present by default.
			delim_state = state.INITIAL; //Change to state.INITIAL_P and recheck...
			
			//delim_state = state.FIRST_P; // Use if you assume pause at the start
			
			//CHECK POINT: If the delim_state is initialized to FIRST_P, then every test vector would have a pause in the beginning, 
			//even though a test vector is cut out of a scan that does not have a pause in the beginning!!!! 
			
			//boolean add_at_end = true;//What is the purpose of this variable....depricated variable 
			
			// Get the index of the first key press. First few keys MIGHT be key releases. You may never know
			while (startIndex < key_events.size() && key_events.get(startIndex).isKeyRelease()) { startIndex++; }
			
			//Get the index of the last key press......
			int end_key_press_marker = key_events.size()-1;
			while (end_key_press_marker >= 0 && key_events.get(end_key_press_marker).isKeyRelease()) { end_key_press_marker--;}
			
			
			//System.out.println("END: " + end_key_press_marker + " END KEY MARKER: " + end_key_press_marker);//for debug purposes only
			
			// From the index of the first key press, iterate through the event list and extract the bursts.
			for(endIndex = startIndex + 1; endIndex < key_events.size()-1; endIndex++) {
				
				int rev_depth = 0; //Is the number of "consecutive" backspaces
				
				KeyStroke previous_key_event =  key_events.get(endIndex - 1); // Initialize to startIndex position, same as previous_key_event = startIndex
				KeyStroke current_key_event =  key_events.get(endIndex);      // Initialize to endIndex position, same as previous_key_event = endIndex
				
				if (current_key_event.isKeyPress()) {//Check for a key press to identify a possible 'pause' occurrence
					
					if (previous_key_event.isKeyRelease()) {//Check for a preceding key release to identify a possible 'pause' occurrence
						
						if (pauseDelimiter.isDelimiter(previous_key_event, current_key_event)) {//If a pause is detected
							
							reset_revision_count = true; //flag to reset revision count to 0.
							reset_PR_char_count = true;  //flag to reset number of characters between a pause and a revision  
							pause_index = endIndex;      //this is current key, index of key press after a pause has occurred. This is the start of a potential burst
							
							current_pause = (int)(current_key_event.getWhen() - previous_key_event.getWhen()); //Measures the duration of the pause
							
							switch (delim_state) {
								case INITIAL: {
									delim_state = state.FIRST_P;// Change the state variable to Second P state.
									start_pause_index = endIndex;// endIndex is a key press event, which marks the end of a pause. It is also the first key of the burst.
									before_pause = current_pause;// record the duration of the pause...
									break;
								}
								case FIRST_P: {
									end_pause_index = endIndex;   // endIndex is a key press event, which marks the end of a pause. It is also the first key of the burst.   
									after_pause = current_pause;  // record the duration of the pause...
									delim_state = state.SECOND_P; // Change the state variable to Second P state. 
									break;
								}
								case SECOND_P: {
									//currentPair = new StartEnd(start_pause_index, end_pause_index);
									//Check Point: We will not be able to detect a pause between BACKSPACES because this code is assuming that at any iteration either pause happens or revision happens, but not both....  
									//Is their a way to cut only at one place
									System.out.println("PAUSE AT: " + endIndex);
										add_clip = true; //True when a clip (burst) has to be extracted...
										
									break;
								}
							}

						}
					}
					
					switch (current_key_event.getKeyCode()) {//Does the key press event belong to a Revision Count
						//Note that a pause and revision can occur with the same current_event_key
						case 8: {
							revision_count++;
							System.out.println("AT: " + endIndex + " REVISION COUNTER: " + revision_count);//Debug print statement...
							if (revision_count == 1) {
								revision_start_index = endIndex; //endIndex is a key press event, which indicates the press of a backspace key
							}
							revision_end_index = endIndex;
							break;
						}
						default: {							
							if (revision_count >= min_revision_count) { //PR_char_count <= pr_dist
								revision_end_index = endIndex;
								//Warrants a change of state, but the change of stare is not confirmed here because we don't know if PR_char_count < =  pr_dist
								//Record revision_end_index
								if (delim_state == state.FIRST_P)
									delim_state = state.FIRST_R;
								if (delim_state == state.SECOND_P)
									delim_state = state.SECOND_R;
							}
							else
								PR_char_count++;
							
							reset_revision_count = true; //if there is a character, reset revision count to zero....
						}
					
					}//End of state change to revision, either R1 or R2......
					System.out.println("THIS STATE IS: " + delim_state);
					rev_depth = revision_count;
										
						switch (delim_state) {
							case FIRST_R: {
								if (PR_char_count <= pr_dist) {//Confirm that this is a "Pause followed By a Revision" and not a lone "Pause"
									
									if (revision_end_index == pause_index) {//Determine if the pause and revision have happened the same time (i.e., with the press of the same key)
										//Here, with the same key press, two states have changed P1 when into P2 and the same time R1 is confirmed...
										//System.out.println("")
										//Here, the agenda is to forget P1 because it is followed by R1 and then, make P2 be R1
										start_pause_index = pause_index; //start_pause_index should be changed to the P2's pause index...
										before_pause = current_pause; // before pause should be reinitialized to current pause
										delim_state = state.FIRST_P; //Disregard P1, because it had an R1 and then make P2 as P1.....
									}
									else
										delim_state = state.INITIAL; //A P1 and R1 have been confirmed, so change the state to initial and move on.... 
								}
								else
									delim_state = state.FIRST_P; // Switch back to Pause 1. You could also make revision_count = 0;
								break;
							}
							case SECOND_R: {
								if (PR_char_count <= pr_dist) {
									if (revision_end_index == pause_index) {//Determine if the pause and revision have happened the same time (i.e., with the press of the same key)
										start_pause_index = pause_index;
										before_pause = current_pause;
										delim_state = state.FIRST_P; //Disregard P2, because it had an R2 and then make new P2 as P1.....
									}
									else
										delim_state = state.INITIAL; //A P2 and R2 have been confirmed, so change the state to initial and move on.... 
								}
								else {
									delim_state = state.SECOND_P;
									//Reverting back to P2 state, because  I understand  at this point you can confirm it is not a PR so you are clipping it as PP, but if you don't
									//clip it here, it will clip anyway, after it encounters a new pause or reaches the end of the array, right!
									/*StartEnd clip = new StartEnd(start_pause_index, end_pause_index);
									
									clip.SetPrePause(before_pause);
									clip.SetPostPause(after_pause);
									clippings.add(clip);
									delim_state = state.FIRST_P;
									
									start_pause_index = end_pause_index;
									end_pause_index = 0;
									before_pause = after_pause;
									after_pause = 0;*/
								}
								break;
							}
						}//End of state change to initial (if PR occurs), first P (if first R does not occur), or perform a PP clip (if second R does not occur)					
				}
				
				//Clip if you reached the end of the keystroke array and also is in P2 state... 
				if (endIndex == end_key_press_marker && delim_state == state.SECOND_P) {
					/*StartEnd clip = new StartEnd(start_pause_index, end_pause_index);
					clip.SetPrePause(before_pause);
					clip.SetPostPause(after_pause);
					clippings.add(clip);*/
					add_clip = true;
				}
				
				//System.out.println(">>>>>>>>AT: " + endIndex + " REVISION_DEPTH: " + rev_depth);
				
				// This block adds new burst clip
				if (add_clip) {
					StartEnd clip = new StartEnd(start_pause_index, end_pause_index);
					//System.out.println("HAPPENNING AT: " + endIndex);
					System.out.println("AT: " + endIndex + " REVISION_DEPTH: " + rev_depth + " MIN_REV_COUNT: " + min_revision_count);
					System.out.println("FROM: " + start_pause_index + " TO: " + end_pause_index);
					clip.SetPrePause(before_pause);
					clip.SetPostPause(after_pause);
					clippings.add(clip);
					
					// Check if this clip was added because of another pause (P3)
					if (delim_state == state.SECOND_P && pause_index > end_pause_index) {
						// The previous pause becomes P1 and the current pause (P3) becomes P2
						start_pause_index = end_pause_index;
						end_pause_index = pause_index;
						before_pause = after_pause;
						after_pause = current_pause;
						delim_state = state.SECOND_P;
					}
					add_clip = false;
				}
				
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
				BurstType_PP burst = new BurstType_PP(new ArrayList<KeyStroke>(burst_keys));
				
				burst.SetPauseAfter(clip.GetPostPause());
				burst.SetPauseBefore(clip.GetPrePause());
					
				// Add only the bursts that are valid for lexical analysis.
				if (burst.isValid()) {
					ppBurstArray.add(burst);
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
		
		/// Computing new features here.
		
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
			System.out.println("Burst time>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>________________: " + burst_time + "\n");
			//This gets the burst times
			burstTimes.add(burst_time / 1000);
			
			ksCount.add(b.burstChars());
			beforeBurstPause.add(b.GetPauseBefore());
			afterBurstPause.add(b.GetPauseAfter());
			//burstKeyDensity.add((double)b.burstChars()/b.burstTime());
			System.out.println("burst-chars: loaded  " + b.burstChars());
			System.out.println("burst-chars: loaded  " + b.GetPauseBefore());
			System.out.println("burst-chars: loaded  " + b.GetPauseBefore());
			preBurstKeyDensity.add((double)(b.burstChars() * 1000)/b.GetPauseBefore());
			preBurstKeyDensity2.add((double)(b.burstAlphaChars() * 1000)/b.GetPauseBefore());
			postBurstKeyDensity.add((double)(b.burstChars() * 1000)/b.GetPauseAfter());
			postBurstKeyDensity2.add((double)(b.burstAlphaChars() * 1000)/b.GetPauseAfter());
			//This computes the typing speed
			if (burst_time >= 0) {
				System.out.println("burst-chars: loaded  " + b.burstChars());
				System.out.println("burst-alpha-chars: " + b.burstAlphaChars());
				System.out.println("burst-words: " + b.burstWords());
				System.out.println("burst-time: " + b.burstTime());
				System.out.println("burst-pre: " + b.GetPauseBefore());
				System.out.println("burst-post: " + b.GetPauseAfter()+ "\n");
				
				double r = (double)(b.burstChars() * 1000)/(double)burst_time;
				System.out.println("ratio>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>________________________________: " + r + "\n");
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
	
	private static class CreateFileOnce {
		private static boolean write_switch = true;
		private static String output_file_path = "";
		
		public static String GetAbsoluteFilePath() {
			if (write_switch) {
				write_switch = false;
				String workingDir = System.getProperty("user.dir");
				java.io.File output_dir = new java.io.File(workingDir,"BurstStats/P-P");
				output_dir.mkdirs();
				Date dateBegin = new Date();
				SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd[H-m-s]Z");
				StringBuilder now = new StringBuilder(dateformat.format(dateBegin));
				output_file_path = new java.io.File(output_dir.getAbsolutePath(), "PP_BurstsPerAnswer" + now + ".csv").toString();
				
			}
			
			return output_file_path;
		}
	}
	

	
	@Override
	public String getName() {
		return "PP Burst Metrics";
	}
	

}
