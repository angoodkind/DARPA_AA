/**
 * The package name does not matter but can be used to organize
 * like modules together or to separate teams.
 * 	ex:
 * 		edu.qccuny.module
 * 		edu.latech.module
 * 
 * Package name and class name must be added to 'modules.conf' for
 * them to be loaded.
 */
package features.nyit;

// These are imported in order to interact with the extractor.
import java.util.Collection;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import keystroke.KeyStroke;

/**
 * @author Patrick Koch
 *
 * Example Module 
 */
public class ExampleModule2 implements ExtractionModule {
	
	/**
	 * Runs When the Create Template Method is Clicked.
	 * 
	 * @see Answer
	 */
	public Collection<Feature> extract(DataNode data) {
		
		//Iterates over all answers for a user.
		for (Answer a : data) {
			//Iterate over all the KeyStrokes in an Answer
			for (KeyStroke k : a.getKeyStrokeList()){
				//get Time
				k.getWhen();
				//return true for a keypress
				k.isKeyPress();
				//return true for a keyrelease
				k.isKeyRelease();
				//return the integer keycode
				k.getKeyCode();
				//return the name of this key
				KeyStroke.vkCodetoString(k.getKeyCode());
				//return the Character this keyevent produced.
					//can be UPPER or lower case.
				k.getKeyChar();
				//return the cursor position
				k.getCursorPosition();
				
			}
		}		
		//Returns nothing to the extractor.
		return null;
	}
	
	/**
	 * Returns the Module's Name.
	 */
	public String getName() {
		// A nicely formated String with the module's name.
		return "Example Module";
	}

}
