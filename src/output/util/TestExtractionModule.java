package output.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import events.EventList;
import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import extractors.data.TestVectorShutdownModule;
import features.pause.KSE;
import keystroke.KeyStroke;

/**
 * This Extraction Module does not produce any useful
 * metrics about answers. It is purely for testing purposes.
 * 
 * @author Adam Goodkind
 *
 */
public class TestExtractionModule implements ExtractionModule,TestVectorShutdownModule {

	/*
	 * Define class fields. These fields are persistent in memory
	 * throughout an extraction session.
	 */
	Collection<Feature> output;
	ArrayList<Integer> nums;
	ArrayList<Integer> squares;
	ArrayList<Integer> nulls;
	int i = 1;

	/*
	 * default constructor performs one-time setup.
	 */
	public TestExtractionModule() {
		output = new LinkedList<Feature>();
		nums = new ArrayList<Integer>();
		squares = new ArrayList<Integer>();
		nulls = new ArrayList<Integer>();
	}

	/*
	 * clears fields that one doesn't want to persist between
	 * users. Clearing the lists is more efficient than allocating
	 * new resources!
	 */
	private void clearFields() {
		output.clear();
		nums.clear();
		squares.clear();
		nulls.clear();
	}

	/*
	 * module entry point.
	 */
	@Override
	public Collection<Feature> extract(DataNode data) {
		clearFields();
		for (Answer a : data) {
//			System.out.println(a.getKeyStrokes());
			nums.add(i);
			nums.add(i*3);
			squares.add(i*i);
			if (a.getAnswerID()%2==0)
				squares.add(i*i*i);
			nulls.add(null);
			i++;
		}
			
		output.add(new Feature("nums",nums));
		output.add(new Feature("squares",squares));
		output.add(new Feature("nulls",nulls));

		//Return output to Extractor
		return output;
	}

	/*
	 * Obviously the "Descriptive Name"
	 * of the module.
	 */
	@Override
	public String getName() {
		return "Nonsense Ignore";
	}

	@Override
	public void shutdown() {
		System.out.println("Test Shutdown");	
	}
}