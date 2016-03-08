package extractors.data;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * A Java Class struct used to hold answer data for a given user.
 * <p>
 * <p>This structure and it's methods are directly accessible to all ExtractionModule objects.
 * <p>DataNode objects hold a user's answers internally in a LinkedList. <p>There are multiple options
 * that can be exercised when retrieving the answer data including returning the data as:
 * <ul>
 * <li>Individual answer Strings.</li>
 * <li>Concatenated String of all answers.</li>
 * <li>Collection of answer Strings.</li>
 * <li>Iterator over raw Answer objects.</li>
 * </ul>
 * 
 * @author Patrick Koch
 * @see ExtractionModule
 * @see Answer
 */
public class DataNode implements Iterable<Answer> {

	private int userID;
	private LinkedList<Answer> answers;
		
	/**
	 * Creates a DataNode object for the specified userID.
	 * 
	 * @param userID
	 */
	public DataNode(int userID) {
		this.userID = userID;
		answers = new LinkedList<Answer>();
	}
	
	/**
	 * Creates a DataNode object with userID = -1.
	 * 
	 */
	public DataNode() {
		this.userID = -1;
		answers = new LinkedList<Answer>();
	}
	
	/**
	 * Returns the number of answers contained in this DataNode.
	 * 
	 * @return The number of answers contained in this DataNode.
	 */
	public int size() {
		return answers.size();
	}
	
	/**
	 * Returns the UserID Associated with the answers in this DataNode.
	 * 
	 * @return the UserID Associated with the answers in this DataNode.
	 */
	public int getUserID() {
		return userID;
	}
	
	/**
	 * Sets the UserID Associated with the answers in this DataNode.
	 * 
	 * @param userID the UserID Associated with the answers in this DataNode.
	 */
	public void setUserID(int userID) {
		this.userID = userID;
	}
	
	public String toString(){
		
		return "User:" + userID + ", Answers:" + answers.size();
	}

	/**
	 * Returns the FinalText of all answers contained in this DataNode as a concatenated String.
	 * 
	 * @return the FinalText of all answers contained in this DataNode as a concatenated String.
	 */
	public String getFinalTextString() {
		String sum = "";
		for (Answer a : answers)
			sum += a.getFinalText();
		return sum;
	}
	
	/**
	 * Returns the CharStream of all answers contained in this DataNode as a concatenated String.
	 * 
	 * @return the CharStream of all answers contained in this DataNode as a concatenated String.
	 */
	public String getCharStreamString() {
		String sum = "";
		for (Answer a : answers)
			sum += a.getCharStream();
		return sum;
	}
	
	/**
	 * Returns the KeyStrokes of all answers contained in this DataNode as a concatenated String.
	 * 
	 * @return the KeyStrokes of all answers contained in this DataNode as a concatenated String.
	 */
	public String getKeyStrokesString() {
		String sum = "";
		for (Answer a : answers)
			sum += a.getKeyStrokes();
		return sum;
	}
	
	/**
	 * Adds an Answer to the DataNode.
	 * 
	 * @param answer
	 */
	public void add(Answer answer) {
		answers.add(answer);
	}
	
	/**
	 * Removes all the Answers held in this DataNode.
	 */
	public void clear() {
		answers.clear();
	}
	
	/**
	 * Returns the finalText stored at the specified index within the DataNode.
	 * 
	 * @param index
	 * @return the finalText stored at the specified index within the DataNode.
	 */
	public String getFinalText(int index) {
		return answers.get(index).getFinalText();
	}
	
	/**
	 * Returns the charStream stored at the specified index within the DataNode.
	 * 
	 * @param index
	 * @return the charStream stored at the specified index within the DataNode.
	 */
	public String getCharStream(int index) {
		return answers.get(index).getCharStream();
	}
	
	/**
	 * Returns the keyStrokes stored at the specified index within the DataNode.
	 * 
	 * @param index
	 * @return the keyStrokes stored at the specified index within the DataNode.
	 */
	public String getKeyStrokes(int index) {
		return answers.get(index).getKeyStrokes();
	}
	
	/**
	 * Returns an iterable collection of the FinalText answers contained within this DataNode.
	 * 
	 * @return an iterable collection of the FinalText answers contained within this DataNode.
	 */
	public Collection<String> getFinalTextList() {
		Collection<String> list = new LinkedList<String>();
		for (Answer a : answers)
			list.add(keystroke.KeyStroke.keyStrokesToFinalText((a.getKeyStrokeList())));
		return list;
	}
	
	/**
	 * Returns an iterable collection of the CharStream answers contained within this DataNode.
	 * 
	 * @return an iterable collection of the CharStream answers contained within this DataNode.
	 */
	public Collection<String> getCharStreamList() {
		Collection<String> list = new LinkedList<String>();
		for (Answer a : answers)
			list.add(a.getCharStream());
		return list;
	}
	/**
	 * Returns an iterable collection of the KeyStrokes answer Strings contained within this DataNode.
	 * 
	 * @return an iterable collection of the KeyStrokes answer Strings contained within this DataNode.
	 */
	public Collection<String> getKeyStrokesList() {
		Collection<String> list = new LinkedList<String>();
		for (Answer a : answers)
			list.add(a.getKeyStrokes());
		return list;
	}
	

	/**
	 * Returns an Iterator over all the Answer objects contained within this DataNode.
	 * 
	 * @return an Iterator over all the Answer objects contained within this DataNode.
	 * @see Answer
	 */
	@Override
	public Iterator<Answer> iterator() {
		return answers.iterator();
	}
	
	public void sortByOrderID () {
		Collections.sort(answers,new ByOrderComparator() );
	}
	
	public Answer getFirst() {
		return answers.getFirst();
	}
	
	public Answer getLast() {
		return answers.getLast();
	}
}
