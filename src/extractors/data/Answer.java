package extractors.data;

import java.util.Collection;

import events.EventList;
import events.GenericEvent;
import keystroke.KeyStroke;

/**
 * An Answer object contains all the data about a single test answer.
 * 
 * It contains various self-described getters and setters.
 * 
 * @author Patrick
 * @see DataNode
 */
public class Answer implements Cloneable{
	
	private String charStream;
	private String finalText;
	private String keyStrokes;
	private int    answerID;
	private int	   questionID;
	private int    cogLoad;
	private String type;
	private EventList<GenericEvent> keyStrokeList;
	private int    orderID;
	
	public Answer(String charStream, String finalText, String keyStrokes,
			int answerID, int questionID, int orderID, int cogLoad, String type, Collection<GenericEvent> keyStrokeList) {
		this.charStream = charStream;
		this.finalText = finalText;
		this.keyStrokes = keyStrokes;
		this.answerID = answerID;
		this.questionID = questionID;
		this.cogLoad = cogLoad;
		this.type = type;
		this.keyStrokeList = new EventList<GenericEvent>(keyStrokeList);
		this.setOrderID(orderID);
	}
	
	public String getCharStream() {
		return charStream;
	}
	public void setCharStream(String charStream) {
		this.charStream = charStream;
	}
	
	/**
	 * Use KeyStroke.keyStrokesToFinalText(Answer.getKeyStrokeList()) instead
	 * @return
	 */
	@Deprecated
	public String getFinalText() {
		return finalText;
	}
	@Deprecated
	public void setFinalText(String finalText) {
		this.finalText = finalText;
	}
	public String getKeyStrokes() {
		return keyStrokes;
	}
	public void setKeyStrokes(String keyStrokes) {
		this.keyStrokes = keyStrokes;
	}
	
	/**
	 * Returns unique identiier for this one instance
	 * @return
	 */
	public int getAnswerID() {
		return answerID;
	}
	public void setAnswerID(int answerID) {
		this.answerID = answerID;
	}
	/**
	 * Returns 1-36, for which question was asked
	 * @return
	 */
	public int getQuestionID() {
		return questionID;
	}
	public void setQuestionID(int questionID) {
		this.questionID = questionID;
	}
	public int getOrderID() {
		return orderID;
	}

	public void setOrderID(int orderID) {
		this.orderID = orderID;
	}

	public int getCogLoad() {
		return cogLoad;
	}
	public void setCogLoad(int cogLoad) {
		this.cogLoad = cogLoad;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public EventList<KeyStroke> getKeyStrokeList() {
		EventList<KeyStroke> keyList = new EventList<KeyStroke>();
		for (GenericEvent ge: keyStrokeList) {
			keyList.add((KeyStroke)ge);
		}
		return keyList;
	}
	
	public EventList<GenericEvent> getEventList() {
		return keyStrokeList;
	}

	@SuppressWarnings("unchecked")
	public <T extends GenericEvent> EventList<T> toEventList(EventList<T> eventList){
		eventList.clear();
		for (GenericEvent ge: keyStrokeList) {
			eventList.add((T)ge);
		}
		return eventList;
	}
	
	public void setEventList(Collection<GenericEvent> keyStrokeList) {
		this.keyStrokeList = new EventList<GenericEvent>(keyStrokeList);
	}
	
	@Override
	public String toString() {
		return "Answer_ID:" + answerID;
	}

	public Object clone()
    {
        try
    {
            return super.clone();
        }
    catch( CloneNotSupportedException e )
    {
            return null;
        }
    }
	
	public long getStartTime() {
		return keyStrokeList.getFirst().getWhen();
	}
	
	public long getEndTime() {
		return keyStrokeList.getLast().getWhen();
	}
	
	public long getLength() {
		return getEndTime() - getStartTime(); 
	}

}
