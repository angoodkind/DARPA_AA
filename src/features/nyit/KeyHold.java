package features.nyit;

import java.util.Collection;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.TreeSet;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import keystroke.KeyStroke;

/**
 * Extracts key hold times from the selected data.
 * Key hold time is defined as the time taken between a keypress and keyrelease event for the same key. 
 * KEYHOLD CLASS IS AN EXTRACTION MODULE
 * RECEIVES INPUT FROM DATNODE CLASS WHICH REPRESENTS ALL 12 ANSWERS OF A SINGLE USER
 * PROCESSES DATANODE AND RETURNS OUTPUT AS FEATURE 
 * THIS CLASS IS DESIGNED TO BUILD A CONTAINER OF A USER'S KEYHOLD TIMES ASSOCIATED WHICH EACH KEY PRESSED BY THE USER
 * @author Patrick Koch
 * @see ExtractionModule
 */
public class KeyHold implements ExtractionModule {

	public LinkedList<Feature> extract(DataNode data) {//REQUIRES DATA NODE-INDIVIDUAL USER WITH KEYSTROKE STREAM-ALL ANSWERS
		
		Hashtable<Integer, LinkedList<Integer>> keyTable = constructKeyTable();//CONSTRUCT EMPTY CONTAINER WHICH STORES USER KEYHOLD VALUES FOR EACH KEY
		
		TreeSet<Integer> keySet = new TreeSet<Integer>(keyTable.keySet());//CONTAINS A LIST OF KEYS
		
		//----------------------FEATURE EXTRACTION STARTS HERE------------------------------------------------------------------------
		
		for (Answer a : data){//ITERATION THROUGH TO GET LIST OF KEYSTROKES
			Collection<KeyStroke> keys = a.getKeyStrokeList();//STORE KEYSTROKES
			
			for (Integer vkCode : keySet) { //ITERATE THROUGH THE LIST OF USEFUL VK CODES.
				long pressTime = 0; //INITIALIZE PRESS TIME TO 0
				boolean firstKeyPress = true;
				// THIS LOOP FINDS THE KEY HOLD TIME BY ITERATING THROUGH THE LIST OF KEYSTROKES OF THE CURRENT ANSWER FOR THE SPECIFIED VK CODE
				for (KeyStroke k : keys) { 
					if (k.getKeyCode() == vkCode) {
						if (k.isKeyPress() && firstKeyPress) {
							pressTime = k.getWhen();//ASSIGN FIRST TIMESTAMP TO FIRST KEY PRESS
							firstKeyPress = false;
						}
						else if (k.isKeyRelease() && pressTime != 0) {//FIND KEY RELEASE AND SUBTRACT PRESS TIME FROM KEY RELEASE TIME TO GET KEYHOLD
							keyTable.get(vkCode).add((int)(k.getWhen() - pressTime));//DETERMINE KEYHOLD TIME
							firstKeyPress = true;
							//THIS LOGIC CANNOT PROCESS SLURS BECAUSE THEY ARE TWO CONSECUTIVE KEYSTROKE...SLURS ARE IGNORED
						}
					}
				}
			}	
		}
		
		LinkedList<Feature> output = new LinkedList<Feature>(); //CONTAINER TO HOLD KEYHOLD VALUES
		
		//----------------------------FEATURE EXTRACTION ENDS HERE------------------------------------------------------------------------------------
		
		for (Integer i : keySet) { 
			output.add(new Feature("H_" + KeyStroke.vkCodetoString(i),keyTable.get(i)));//APPEND H TO KEY
		}		
		return output;
	}	
	//INITIALIZE THE CONTAINER TO STORE A USERS ANSWERS KEYHOLD VALUES...BUILD THE KEY NAMES IN THE CONTAINER BEFORE USING IT TO STORE THE KEYHOLD VALUE TIMES
	private Hashtable<Integer, LinkedList<Integer>> constructKeyTable(){
		Hashtable<Integer, LinkedList<Integer>> keyTable = new Hashtable<Integer, LinkedList<Integer>>();
		for (Integer key : KeyStroke.UsefulVKCodes())
			keyTable.put(key, new LinkedList<Integer>());
		return keyTable;
	}
	
	public String getName() { 
		return "Key Hold";
	}
	
}
