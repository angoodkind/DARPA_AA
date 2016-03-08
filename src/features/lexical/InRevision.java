/**
 * 
 */
package features.lexical;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import features.pause.KSE;

/**
 * @author agoodkind
 *	TO DO: Ratio
 */
public class InRevision implements ExtractionModule {

	Collection<Feature> output;
	Collection<Integer> inRevisionChars;
	Collection<Long> inRevisionTime;
	Collection<Double> inRevisionCharRatio;
	
	public InRevision() {
		output = new LinkedList<Feature>();
		inRevisionChars = new LinkedList<Integer>();
		inRevisionTime = new LinkedList<Long>();
		inRevisionCharRatio = new LinkedList<Double>();
	}
	
	public void clearLists() {
		output.clear();
		inRevisionChars.clear();
		inRevisionTime.clear();
		inRevisionCharRatio.clear();
	}
	
	@Override
	public Collection<Feature> extract(DataNode data) {

		clearLists();
//		System.out.println("Creating instance of "+this.getClass().toString());
		for (Answer a : data) {
			Collection<KSE> kseArray = KSE.parseSessionToKSE(a.getKeyStrokes());
			LinkedList<KSE> kseDownStrokeArray = new LinkedList<KSE>();
			for (KSE kse : kseArray)
				if (kse.isKeyPress())
					kseDownStrokeArray.add(kse);
			inRevisionChars.addAll(getRevisionChars(kseDownStrokeArray));
			inRevisionTime.addAll(getRevisionTime(kseDownStrokeArray));
			inRevisionCharRatio.add(getRevisionCharRatio(kseDownStrokeArray));
		}
		output.add(new Feature("In_Revision_Chars",inRevisionChars));
		output.add(new Feature("In_Revision_Time",inRevisionTime));
		output.add(new Feature("In_Revision_Char_Ratio",inRevisionCharRatio));
//	    for (Feature f : output) System.out.println(f.toTemplate());
		return output;
	}

	@Override
	public String getName() {
		return "In_Revision";
	}
	
	// return the lengths, in chars, of revision bursts
	public ArrayList<Integer> getRevisionChars(LinkedList<KSE> kseArray) {
		ArrayList<Integer> revisionChars = new ArrayList<Integer>();
		int currentRevisionLength = 0;
		for (KSE kse : kseArray)
			if (kse.isInRevision())
				currentRevisionLength++;
			else {
				if (currentRevisionLength > 0) {
					revisionChars.add(currentRevisionLength);
					currentRevisionLength = 0;
				}
			}
		return revisionChars;
	}
	
	// return the ratio of number of chars produced while in revision
	public double getRevisionCharRatio(LinkedList<KSE> kseArray) {
		double revisionRatio = 0;
		int inRevisionKeyStrokes = 0;
		int totalKeyStrokes = 0;
		for (KSE kse : kseArray) {
			totalKeyStrokes++;
			if (kse.isInRevision())
				inRevisionKeyStrokes++;
		}
		revisionRatio = (inRevisionKeyStrokes * 1.) / totalKeyStrokes;
		
		return revisionRatio;
	}
	
	// return the ratio of amount of time spent in revision
	public double getRevisionTimeRatio(LinkedList<KSE> kseArray) {
		final long totalTime = kseArray.getLast().getWhen() - kseArray.getFirst().getWhen();
		double revisionRatio = 0;
		boolean inRevisionSpan = false;
		long totalRevisionTime = 0;
		long revisionStartTime = 0;
		for (KSE kse : kseArray) {
			if (kse.isInRevision()) { // if KSE is in revision
				if (!inRevisionSpan) {  // if not in a current revision span
					revisionStartTime = kse.getWhen();
					inRevisionSpan = true;
				}
			}
			else {	// KSE is not in a revision
				if (inRevisionSpan) { //a revision span is active
					totalRevisionTime += (kse.getWhen() - revisionStartTime);
					inRevisionSpan = false;
					revisionStartTime = 0;
				}
			}
			revisionRatio = totalRevisionTime/totalTime;
		}
		
		return revisionRatio;
	}
	
	// return the length, in times, spent in revision
	public ArrayList<Long> getRevisionTime(Collection<KSE> kseArray) {
		ArrayList<Long> revisionTime = new ArrayList<Long>();
		long currentRevisionTime = 0;
		for (KSE kse : kseArray)
			if (kse.isInRevision())
				currentRevisionTime += kse.getM_pauseMs();
			else {
				if (currentRevisionTime > 0) {
					revisionTime.add(currentRevisionTime);
					currentRevisionTime = 0;
				}
			}
		return revisionTime;
	}

}
