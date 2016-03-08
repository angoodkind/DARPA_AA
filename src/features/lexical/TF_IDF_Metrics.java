/**
 *
 */
package features.lexical;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import extractors.lexical.TF_IDF_Extract;
import extractors.lexical.Tokenize;
import features.pause.KSE;
import features.pause.PauseBursts;

/**
 * @author agoodkind
 */
public class TF_IDF_Metrics extends TF_IDF_Extract implements ExtractionModule {

//  ArrayList<Integer> userIdArray = new ArrayList<>();
  int pause_threshold = 250;
  final double rareThreshold = 0.075; //tf*idf cutoff
  
  Collection<Double> avgTfidf;			// average tf*idf score (of all tokens)
  Collection<Double> rareWordPause;		// average pause length for rare words
  Collection<Double> tfidfAfterPause;	// average tf*idf after a pause
  LinkedList<Feature> output;

  public TF_IDF_Metrics() {
	  avgTfidf = new LinkedList<>();
	  rareWordPause = new LinkedList<>();
	  tfidfAfterPause = new LinkedList<>();
	  output = new LinkedList<>();
  }
  
  public void clearLists() {
	  avgTfidf.clear();
	  rareWordPause.clear();
	  tfidfAfterPause.clear();
	  output.clear();
  }

  /*
    * gets tf*idf map from TF_IDF_Extract
    * calculates average tf*idf across an entire answer
    */
  @Override
  public Collection<Feature> extract(DataNode data) {
	  
	clearLists();
    for (Answer a : data) {

      String finalText = a.getCharStream();
//			String finalText = a.getFinalText();
//			String finalText = KeyStroke.keyStrokesToFinalText(a.getKeyStrokeList());

      String keystrokeStr = a.getKeyStrokes();

      HashMap<String, Double> tfidfMap = createTfidfMap(finalText);

      //add average tf*idf scores to collection
      avgTfidf.add(getAvgTfidf(tfidfMap, finalText));

      //add average rare word pause to collection
      rareWordPause.add(getRareWordPause(tfidfMap, finalText, keystrokeStr, rareThreshold, pause_threshold));

      //add average tf*idf after a pause
      tfidfAfterPause.add(getAvgTfidfAfterPause(tfidfMap, finalText, keystrokeStr, pause_threshold));
    }

    output.add(new Feature("Average_TF*IDF", avgTfidf));
    output.add(new Feature("Avg_Rare_Word_Pause", rareWordPause));
    output.add(new Feature("Avg_tf*idf_After_Pause", tfidfAfterPause));

//    for (Feature f : output)
//      System.out.println(f.toTemplate());

    return output;
  }

  // calculates the average tf*idf over all tokens in an answer
  private double getAvgTfidf(HashMap<String, Double> map, String tokenStr) {

    double totalTfidf = 0;
    int tokenCount = 0;

    try {

      //create token array
      Tokenize t = new Tokenize();
      String[] tokenArray = t.runTokenizer(tokenStr);

      //for each token, get tf*idf

      for (String token : tokenArray) {
        if (map.get(token) != null) {
          totalTfidf += map.get(token);
          ++tokenCount;
        }
      }

    } catch (IOException e) {
      e.printStackTrace();
    }

    return totalTfidf / tokenCount;
  }

  private double getRareWordPause(HashMap<String, Double> map, String tokenStr, String keystrokes, double threshold,
		  int pause_threshold) {

    double totalPauseLength = 0;
    int pauseCount = 0;

    try {
      //create token array
      Tokenize t = new Tokenize();
      String[] tokenArray = t.runTokenizer(tokenStr);
      // get starting indices (of KStoFinalText) for all tokens
      ArrayList<Integer> startPositions = getStartIndex(tokenStr);

      // new instance of Pause class, for use in extracting pause list
      PauseBursts ksep = new PauseBursts();
      // get list of Pauses from charStream
      List<Integer> pauseList = ksep.generatePauseDownList(keystrokes,pause_threshold);
      KSE[] kseArr = ksep.getKseArray();

      //build set of rare words
      Set<String> rareWordSet = getRareWordSet(map, threshold);

      for (int i = 0; i < tokenArray.length; i++) {
        if (rareWordSet.contains(tokenArray[i])) { // found a match
          for (Integer pause_idx : pauseList) {

            if (pause_idx == (startPositions.get(i) - 1)) {
              //find KSE Array index that pauseList.get(j) points to
              // and get the pause duration of this index of the KSE Array

              totalPauseLength += kseArr[pause_idx].getM_pauseMs();
              ++pauseCount;
              break;
            }
          }
        }
      }

    } catch (IOException e) {
      e.printStackTrace();
    }

    return totalPauseLength / pauseCount;
  }

  //called from getRareWordPause
  //returns set of rare words
  private Set<String> getRareWordSet(HashMap<String, Double> map, double threshold) {

    Set<String> rareWordSet = new HashSet<>();

    for (Entry<String, Double> entry : map.entrySet()) {
      if (entry.getValue() > threshold) {
        rareWordSet.add(entry.getKey());
      }
    }

    return rareWordSet;
  }

  private double getAvgTfidfAfterPause(HashMap<String, Double> map, String tokenStr, String keystrokes, int pause_threshold) {

    double totalTfidf = 0;
    int pauseCount = 0;

    try {
      //create token array
      Tokenize t = new Tokenize();
      String[] tokenArray = t.runTokenizer(tokenStr);
      // get starting indices (of KStoFinalText) for all tokens
      ArrayList<Integer> startPositions = getStartIndex(tokenStr);

      // new instance of Pause class, for use in extracting pause list
      PauseBursts ksep = new PauseBursts();
      // get list of Pauses from charStream
      ksep.generatePauseDownList(keystrokes, pause_threshold);
      List<Integer> pauseList = ksep.getPauseDownList();

      for (Integer pause_idx : pauseList) {
        for (int j = 0; j < startPositions.size(); j++) {
          if (pause_idx + 1 == startPositions.get(j)) { //if the next index is the start of a token

            String word = tokenArray[j];
            if (map.get(word) != null) {
              double tfidf = map.get(word);
              totalTfidf += tfidf;
              ++pauseCount;
            }
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return totalTfidf / pauseCount;
  }

  /*
    * @see extractors.nyit.ExtractionModule#getName()
    */
  @Override
  public String getName() {
    return "TF_IDF Metrics";
  }

}
