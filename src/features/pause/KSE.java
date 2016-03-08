package features.pause;

//import edu.nyit.mock.KeyStroke;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import keystroke.KeyStroke;
import keystroke.KeyStrokeParser;

// The name stands for KeyStroke-Extended.
//  KSE class represents the keystroke fields of the keystroked.txt files.
public class KSE extends KeyStroke {
  protected long m_pauseMs;
  protected boolean inRevision;

  /**
   * Default constructor
   */
  public KSE() {
    super(0, 0, 0, ' ', 0);
    m_pauseMs = 0;
    inRevision = false;
  }

  /**
   * Partial Copy constructor extending an existing KeyStroke
   */
  public KSE(KeyStroke ks) {
    super(ks.getID(), ks.getWhen(), ks.getKeyCode(), ks.getKeyChar(), ks.getCursorPosition());
    m_pauseMs = 0;
    inRevision = false;
  }

  /**
   * Keystroke constructor based directly on KeyStroke.
   *
   * @param id             the keydown/key up id
   * @param when           ms when the stroke happened
   * @param keyCode        the vk id
   * @param keyChar        the ascii char
   * @param cursorPosition the position in the textbox
   */
  public KSE(int id, long when, int keyCode, char keyChar, int cursorPosition) {
    super(id, when, keyCode, keyChar, cursorPosition);
    m_pauseMs = 0;
    inRevision = false;
  }

  /**
   * Full KSE constructor using KeyStroke and pause fields.
   *
   * @param id             the keydown/key up id
   * @param when           ms when the stroke happened
   * @param keyCode        the vk id
   * @param keyChar        the ascii char
   * @param cursorPosition the position in the textbox
   * @param pauseMs        the pause since the last keystroke
   */
  public KSE(int id, long when, int keyCode, char keyChar, int cursorPosition, int pauseMs, boolean inRevision) {
    super(id, when, keyCode, keyChar, cursorPosition);
    m_pauseMs = pauseMs;
    this.inRevision = inRevision;
  }


  protected void setPause(long p) {
    m_pauseMs = p;
  }

  public long getM_pauseMs() {
    return this.m_pauseMs;
  }

  public boolean isInRevision() {
    return this.inRevision;
  }

  /**
   * Factory method that parses a entire typing session consisting of whitespace
   * separated keystroke event Strings into KSEs.
   *
   * KSEs maintain information about the length of pause before the stroke, and whether it takes place within
   * a revision or not.
   *
   * @param typingSession a string representation of keystrokes
   * @return A Collection of KSEs
   * @see java.util.Collection
   */
  public static Collection<KSE> parseSessionToKSE(String typingSession) {
    String[] keyStrokes = typingSession.trim().split("\\s+");

    // Maintains the current state of the final text to know if we're in a revision or not.
    StringBuilder sb = new StringBuilder();
    boolean shiftDown = false;
    SelectionBounds selection = new SelectionBounds();
    int startIndex = -1;
    LinkedList<KSE> keyStrokeList = new LinkedList<>();
    KSE prev_down_kse = null;
    for (String ks : keyStrokes) {
      KSE kse = new KSE(KeyStrokeParser.parse(ks));

      if (keyStrokeList.size() == 175) {
        int i = 0;
      }

      if (startIndex == -1) {
        startIndex = kse.getCursorPosition();
      }
      if (prev_down_kse != null && kse.isKeyPress()) {
        // Only compare the pause between down strokes.
        kse.setPause(kse.getWhen() - prev_down_kse.getWhen());
      } else {
        // The first kse has no preceding pause.
        kse.setPause(0);
      }
      keyStrokeList.add(kse);
      // if the cursor position is less than the max cursor position, the keystroke is in a revision.
      if (kse.getCursorPosition() < startIndex + sb.length()) {
        kse.inRevision = true;
      }

      shiftDown = KeyStroke.processKeyStroke(sb, startIndex, shiftDown, selection, kse);

      if (kse.isKeyPress()) {
        prev_down_kse = kse;
      }
    }
    return keyStrokeList;
  }
  
  
  
  public static ArrayList<KSE> parseToVisibleTextKSEs(Collection<KSE> kseCollection) {
	  ArrayList<KSE> kseList = new ArrayList<KSE>();
	  for (KSE kse : kseCollection)
		  if (kse.isKeyPress() && kse.isVisible())
			  kseList.add(kse);
	  return kseList;
  }
  
  public static ArrayList<KSE> parseToKeyPressKSEs(String keystrokeString) {
	  Collection<KSE> kseList = KSE.parseSessionToKSE(keystrokeString);
	  ArrayList<KSE> ksePressList = new ArrayList<KSE>();
	  for (KSE kse : kseList)
		  if (kse.isKeyPress())
			  kseList.add(kse);
	  return ksePressList;
  }
  
  // returns the canonical hand used to type the KeyStroke
  // TODO optimize with static map structure
  public String kseGetHand() {
	  Set<Integer> leftHandKeys = new HashSet<Integer>(Arrays.asList(9,17,18,20,49,50,51,52,53,65,66,67,68,69,70,
				71,81,82,83,84,86,87,88,90,192));
	  Set<Integer> rightHandKeys = new HashSet<Integer>(Arrays.asList(8,10,27,37,38,39,40,44,45,46,47,48,54,55,56,
				57,59,61,72,73,74,75,76,77,78,79,80,85,89,91,92,93,222));
	  
	  if (leftHandKeys.contains(this.getKeyCode())) 
		  return "Left_Hand";
	  else if (rightHandKeys.contains(this.getKeyCode()))
		  return "Right_Hand";
	  else
		  return "None";
  }
  
  // returns the canonical finger used to type the KeyStroke
  // TODO optimize with static map structure
  public String kseGetFinger() {
		Set<Integer> pinkyFingerKeys = new HashSet<Integer>(Arrays.asList(8,9,10,16,17,18,20,27,37,38,39,40,45,47,48,49,59,
				61,65,80,81,90,91,92,93,192,222));
		Set<Integer> ringFingerKeys = new HashSet<Integer>(Arrays.asList(46,50,57,70,76,79,83,87,88));
		Set<Integer> middleFingerKeys = new HashSet<Integer>(Arrays.asList(44,51,56,67,68,69,73,75));
		Set<Integer> indexFingerKeys = new HashSet<Integer>(Arrays.asList(52,53,54,55,66,71,72,74,77,78,82,84,85,86,89));
		Set<Integer> thumbKeys = new HashSet<Integer>(Arrays.asList(32));
		
		if (pinkyFingerKeys.contains(this.getKeyCode()))
			return "Pinky_Finger";
		else if (ringFingerKeys.contains(this.getKeyCode()))
			return "Ring_Finger";
		else if (middleFingerKeys.contains(this.getKeyCode()))
			return "Middle_Finger";
		else if (indexFingerKeys.contains(this.getKeyCode()))
			return "Index_Finger";
		else if (thumbKeys.contains(this.getKeyCode()))
			return "Thumb_Finger";
		else
			return "None";
  }
  
  // returns the row of the key used in the KeyStroke
  // TODO optimize with static map structure
  public String kseGetRow() {
		Set<Integer> numberRowKeys = new HashSet<Integer>(Arrays.asList(8,27,45,48,49,50,51,52,53,54,55,56,57,61,192));
		Set<Integer> topRowKeys = new HashSet<Integer>(Arrays.asList(9,69,73,79,80,81,82,84,85,87,89,91,92,93));
		Set<Integer> homeRowKeys = new HashSet<Integer>(Arrays.asList(10,20,59,65,68,70,71,72,74,75,76,83,222));
		Set<Integer> bottomRowKeys = new HashSet<Integer>(Arrays.asList(16,44,46,47,66,67,77,78,86,88,90));
		Set<Integer> spaceRowKeys = new HashSet<Integer>(Arrays.asList(17,18,32,37,38,39,40));
		
		if (numberRowKeys.contains(this.getKeyCode()))
			return "Number_Row";
		else if (topRowKeys.contains(this.getKeyCode()))
			return "Top_Row";
		else if (homeRowKeys.contains(this.getKeyCode()))
			return "Home_Row";
		else if (bottomRowKeys.contains(this.getKeyCode()))
			return "Bottom_Row";
		else if (spaceRowKeys.contains(this.getKeyCode()))
			return "Space_Row";
		else
			return "None";
  }


  public String toString() {
    return isKeyPress() + "," + getKeyChar() + "," + getWhen() + "," + getM_pauseMs();
  }
}
