package features.lexical;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import keystroke.KeyStroke;

public class ErasePattern implements ExtractionModule {

	private LinkedList<Integer> bks_pattern_count;
	private LinkedList<Integer> del_pattern_count;
	private ArrayList<KeyStroke> key_events; 
	
	@Override
	public Collection<Feature> extract(DataNode data) {
		
		bks_pattern_count = new LinkedList<Integer>();
		del_pattern_count = new LinkedList<Integer>();
		
		
		for (Answer a : data) {
			key_events = new ArrayList<KeyStroke>(a.getKeyStrokeList());
			
			int bks_counter = 0;
			int del_counter = 0;
			
			int bks_revision_count = 0;
			int del_revision_count = 0;
			
			for (KeyStroke key : key_events) {
				if (key.isKeyPress()) {
					switch (key.getKeyCode()) {
						case 8: {
							bks_counter++;
							break;
						}
						case 127: {
							del_counter++;
							break;
						}
						default: {
							if (bks_counter >= 2) 
								bks_revision_count++;
							if (del_counter >= 2) 
								del_revision_count++;
							bks_counter = 0;
							del_counter = 0;
						}
					}
				}
			}
			
			// For trailing back spaces.
			if (bks_counter >= 2) 
				bks_revision_count++;
			if (del_counter >= 2) 
				del_revision_count++;
			
			bks_pattern_count.add(bks_revision_count);
			del_pattern_count.add(del_revision_count);
			
		}
		
		LinkedList<Feature> output = new LinkedList<Feature>();
		
		output.add(new Feature("bks_pattern_count", bks_pattern_count));
		output.add(new Feature("del_pattern_count", del_pattern_count));
		
		return output;
	}

	@Override
	public String getName() {
		return "Erase Pattern";
	}
}
