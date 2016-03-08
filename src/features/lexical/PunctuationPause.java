/**
 * Measures the pauses surrounding punctuation marks
 */
package features.lexical;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import extractors.lexical.POS_Extractor;
import features.pause.KSE;

/**
 * @author agoodkind
 */
public class PunctuationPause extends POS_Extractor implements ExtractionModule {

    // to hold list of ints, counting pauses before periods
    Collection<Long> beforePeriodPauses;
    // to hold list of ints, counting pauses before periods
    Collection<Long> afterPeriodPauses;
    // to hold list of ints, counting pauses before commas
    Collection<Long> beforeCommaPauses;
    // to hold list of ints, counting pauses before commas
    Collection<Long> afterCommaPauses;

    // to hold all of the POS_Pause features
    LinkedList<Feature> output;
    
    int pause_threshold = 250;

    public PunctuationPause() {
	    beforePeriodPauses = new LinkedList<>();
	    afterPeriodPauses = new LinkedList<>();
	    beforeCommaPauses = new LinkedList<>();
	    afterCommaPauses = new LinkedList<>();
	    output = new LinkedList<Feature>();
    }
    
    public void clearLists() {
	    beforePeriodPauses.clear();
	    afterPeriodPauses.clear();
	    beforeCommaPauses.clear();
	    afterCommaPauses.clear();
	    output.clear();
    }

  @Override
  public Collection<Feature> extract(DataNode data) {
    
	  	clearLists();
//		System.out.println("Creating instance of "+this.getClass().toString());
    for (Answer a : data) {
    	       
      KSE[] kses = KSE.parseSessionToKSE(a.getKeyStrokes()).toArray(new KSE[0]);
      ArrayList<KSE> keypressKseArray = new ArrayList<KSE>();
		for (KSE kse : kses) 
			if (kse.isKeyPress())
				keypressKseArray.add(kse);
	
//        System.out.println(pauseList.toString());
	
	// get pauses before period
	beforePeriodPauses.addAll(getPrePunctPauses('.',keypressKseArray));
	// get pauses after period
	afterPeriodPauses.addAll(getPostPunctPauses('.',keypressKseArray));
	// get pauses before comma
	beforeCommaPauses.addAll(getPrePunctPauses(',',keypressKseArray));
	// get pauses after comma
	afterCommaPauses.addAll(getPostPunctPauses(',',keypressKseArray));
    }

    output.add(new Feature("BeforePeriodPauses", beforePeriodPauses));
    output.add(new Feature("AfterPeriodPauses", afterPeriodPauses));
    output.add(new Feature("BeforeCommaPauses", beforeCommaPauses));
    output.add(new Feature("AfterCommaPauses", afterCommaPauses));

//    for (Feature f : output) System.out.println(f.toTemplate());

    return output;
  }

  /*searches for a pause directly preceding the given punctuation mark
    * 1) search kse array for given punctuation mark
    * 2) When punctuation mark found
    * 		a) Find the starting time of the punct and subtracts it from preceding starting time
    * 3) returns pauseCount
    */
  private List<Long> getPrePunctPauses(char punct, ArrayList<KSE> kses) {
    List<Long> pauses = new ArrayList<>();
    pauses.clear();

    for (int i = 1; i < kses.size(); i++) {
    	if (kses.get(i).getKeyChar() == punct)
    		pauses.add(kses.get(i).getWhen()-kses.get(i-1).getWhen());
    }
    
    return pauses;
  }

  /*searches for a pause directly after the given punctuation mark
    * 1) search kse array for given punctuation mark
    * 2) When punctuation mark found
    * 		a) Find the starting time of the punct, and subtract from next kse starting time
    * 3) returns pauseCount
    */
  private List<Long> getPostPunctPauses(char punct, ArrayList<KSE> kses) {
    
	List<Long> pauses = new ArrayList<>();
    pauses.clear();

    for (int i = 0; i < kses.size() - 1; i++) {
    	if (kses.get(i).getKeyChar() == punct)
    		pauses.add(kses.get(i+1).getWhen()-kses.get(i).getWhen());
    }
    return pauses;
  }

  /*
    * @see extractors.nyit.ExtractionModule#getName()
    */
  @Override
  public String getName() {
    return "Punctuation_Pause";
  }
}
