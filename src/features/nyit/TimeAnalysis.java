package features.nyit;

import java.util.Collection;
import java.util.LinkedList;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;

public class TimeAnalysis implements ExtractionModule {

  @Override
  public Collection<Feature> extract(DataNode data) {
    //data.sortByOrderID();
    LinkedList<Feature> output = new LinkedList<Feature>();
    Long length = 0L;
    for (Answer a : data) {
      length += a.getLength();
    }
    output.add(new Feature("Question_Duration", length / 1000));

    for (Answer a : data) {
      if (output.size() == 1) {
        output.add(new Feature("Question_ID", "QID_" + a.getQuestionID()));
        output.add(new Feature("Answer_ID", "AID_" + a.getAnswerID()));
        output.add(new Feature("Cog_Load", "COGLOAD_" + a.getCogLoad()));
      }
    }
    return output;
  }


  @Override
  public String getName() {
    // TODO Auto-generated method stub
    return null;
  }

}
