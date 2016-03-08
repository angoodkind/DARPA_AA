package keystroke;

import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.LinkedList;

import events.EventParser;

public class KeyStrokeParser implements EventParser<KeyStroke> {

	/**
	 * Factory method that parses a single keystroke event String into a
	 * KeyStroke Object.
	 * 
	 * @param keyStrokeString
	 *            String in the format:
	 *            <p>
	 *            [Key Press(0)/Key
	 *            Release(1)]:[VK_Code]:[ASCII_Code]:[Time_Stamp
	 *            ]:[Cursor_Position]
	 * @return A KeyStroke Object.
	 * @see java.awt.event.KeyEvent
	 */
	public static KeyStroke parse(String keyStrokeString) {
		int id = 0, keyCode, cursorPosition;
		long when;
		char keyChar;
		String[] array = keyStrokeString.trim().split(":");
		switch (array[0]) {
		case "0":
			id = KeyEvent.KEY_PRESSED;
			break;
		case "1":
			id = KeyEvent.KEY_RELEASED;
			break;
		}
		keyCode = Integer.parseInt(array[1], 16);
		keyChar = (char) Integer.parseInt(array[2]);
		when = Long.parseLong(array[3]);
		cursorPosition = Integer.parseInt(array[4]);
		return new KeyStroke(id, when, keyCode, keyChar, cursorPosition);
	}
	
	@Override
	/**
	 * Factory method that parses a entire typing session consisting of
	 * whitespace separated keystroke event Strings.
	 * 
	 * @param typingSession
	 *            a string of white space separated keystroke events.
	 * @return A Collection of KeyStrokes
	 * @see Collection
	 */
	public Collection<KeyStroke> parseSession(String typingSession) {
		String[] keyStrokes = typingSession.trim().split("\\s+");
		Collection<KeyStroke> keyStrokeList = new LinkedList<>();
		for (String ks : keyStrokes)
			keyStrokeList.add(parse(ks));
		return keyStrokeList;
	}

}
