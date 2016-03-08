package features.predictability;

import keystroke.KeyStroke;
import ngrammodel.Bigram;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;


/**
 * Calculates Digraph Durations normalized by predictability.
 * <p/>
 * Based on Digraph Duration by Patrick Kock
 *
 * @see features.nyit.DigraphDuration
 */
public class DigraphExpandedDurationPred extends Predictability implements ExtractionModule {

  private static final String modelName = "SESSION1";
  private static final String gramType = "keystroke";

  public static final boolean DEBUG = false;

  static final boolean WORD_BOUNDARY = false;

  static final boolean JOINT = false;

  static final boolean WRITE_FILE = false;
  static final String csv_file = "digraph.expdur.tri.csv";

  private static TreeSet<Integer> keySet = new TreeSet<Integer>(LessUsefulVKCodes());
  private HashMap<Integer, LinkedList<Double>> featureMap = new HashMap<Integer, LinkedList<Double>>(1800);

  /**
   * Initialize the module.
   */
  public DigraphExpandedDurationPred() {
    super(modelName, gramType);
    featureMap.clear();
  }

  /**
   * Generates full permutation of two UpperCase alpha combinations and places
   * them into a TreeSet which ensures no duplicates and automatically sorts them!
   */
  private void generateSearchSpace() {
    featureMap.clear();
    for (Integer key0 : keySet)
      for (Integer key1 : keySet) {
        featureMap.put(key0 * 1000 + key1, new LinkedList<Double>());
      }
  }

  @Override
  public Collection<Feature> extract(DataNode data) {
    //clear feature map and repopulate it's keys for a new extraction
    generateSearchSpace();

    //create a buffer to hold the keystrokes during processing
    LinkedList<KeyStroke> buffer = new LinkedList<KeyStroke>();
    //Holds numerical value of digraph (k0*1000 + k1)
    int digraph;
    BufferedWriter outf = null;
    try {
      outf = new BufferedWriter(new FileWriter(csv_file, true));

      for (Answer a : data) {
        for (KeyStroke k : a.getKeyStrokeList()) {
          /////////////////Main Logic///////////////////////

          // AR: modified press-to-press timing to use p(k1, k2 | k0) probabilities.
          if (k.isKeyPress()) {                  //only look at keypresses
            buffer.add(k);                    //add a keypress to the buffer
            if (buffer.size() > 3)                //if the buffer contains more than 2 keypresses
            {
              buffer.poll();                  //remove the first keystroke in the buffer
            }
            if (buffer.size() == 3) {              //if buffer has 2 keystrokes in it
              digraph = (buffer.get(1).getKeyCode() * 1000 //calculate int equivalent
                  + buffer.get(2).getKeyCode());
              if (featureMap.containsKey(digraph)) {
                //if the digraph is in the searchSpace Set
                KeyStroke kt0 = buffer.get(0);
                KeyStroke kt1 = buffer.get(1);
                KeyStroke kt2 = buffer.get(2);

                double p = keyStrokeTrigramModel.getTrigramProbability(kt0, kt1, kt2);
                double p0 = keyStrokeBigramModel.getBigramProbability(kt0, kt1);

//                double prob = keyStrokeBigramModel.getBigramProbability(kt1, kt2);
                double prob = p * p0;
                double bigramInterval = kt2.getWhen() - kt1.getWhen();  // Press to press duration.

                if (DEBUG) {
                  System.out.println(
                      KeyStroke.vkCodetoString(kt1.getKeyCode()) + "," + KeyStroke.vkCodetoString(kt2.getKeyCode()) +
                          ":" +
                          prob);
                }

                if (WRITE_FILE) {
                  outf.write(KeyStroke.vkCodetoString(kt1.getKeyCode())); // k_i.
                  outf.write(',');
                  outf.write(KeyStroke.vkCodetoString(kt2.getKeyCode())); // k_+1.
                  outf.write(',');
                  outf.write(Double.toString(bigramInterval));  // time
                  outf.write(',');
                  if (kt1.isSpace()) {
                    outf.write("SPACE");
                  } else {
                    outf.write("NONSPACE");
                  }
                  outf.write(',');
                  outf.write(Double.toString(prob)); // bigram probability p(kt2 , kt1 | kt0);
                  outf.write(',');
                  outf.write(Double.toString(p)); // bigram probability p(kt2  | k1, kt0);
                  outf.write(',');
                  outf.write(Double.toString(p0)); // bigram probability p(kt1 | kt0);
                  outf.write('\n');
                }

                double expectedTime;
                if (WORD_BOUNDARY) {
                  //-- remove outliers over 2000ms and under 0 --
//                  mean pause SPACE: 362.441228136
//                  mean pause NONSPACE: 202.512634498
//                  mean log pause SPACE: 5.59405428907
//                  mean log pause NONSPACE: 5.08216142819
                  if (kt1.isSpace()) {
                    expectedTime = Math.exp(5.59405428907) - 1;
                    expectedTime = 362.441228136;
                  } else {
                    expectedTime = Math.exp(5.08216142819) - 1;
                    expectedTime = 202.512634498;
                  }
                } else {
                  if (JOINT) { // p (k1, k2 | k0)
                    double w0 = 5.2029;
                    double w1 = -2.4911;
                    expectedTime = Math.exp(w0 + w1 * prob);
                    //pearson raw -0.116448991316
//                    pearson log -0.168355986169
//                    spearman raw -0.20200329142
                  } else {  // trigram p(k2 | k1, k0)
                    double w0 = 5.3055;
                    double w1 = -0.7223;

                    // pearson raw -0.204757235103
//                    pearson log -0.251814422138
//                    spearman raw -0.313597115718
                    expectedTime = Math.exp(w0 + w1 * p);
                  }
                }

                // Omit intervals over 2 seconds
                if (bigramInterval < 2000 && bigramInterval > 0) {
                  double intervalPred = bigramInterval - expectedTime;

                  featureMap.get(digraph).add(intervalPred);
                }
              }
            }
          }
          /////////////////////////////////////////////////
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (outf != null) {
        try {
          outf.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    //create output feature list.
    LinkedList<Feature> output = new LinkedList<Feature>();

    //iterate over the featureMap
    for (Integer vkCode0 : keySet)
      for (Integer vkCode1 : keySet) {
        output.add(
            new Feature("DN_Exp_Predict_" + KeyStroke.vkCodetoString(vkCode0) + "_" + KeyStroke.vkCodetoString(vkCode1),
                featureMap.get(vkCode0 * 1000 + vkCode1)));
      }

    return output;
  }

  @Override
  public String getName() {
    return "Digraph_Expanded Duration";
  }

  /**
   * This returns a Set Object containing vkCodes that are useful to our
   * study.
   * <p/>
   * <p/>
   * This lowers the search space and eliminates erroneous key checks.
   *
   * @return Set of vkCodes that are useful in checking during keystroke
   * experiments.
   */
  public static Set<Integer> LessUsefulVKCodes() {
    TreeSet<Integer> vkSet = new TreeSet<>();
//    vkSet.add(8); //backspace
//		vkSet.add(9);
//		vkSet.add(10);
//		vkSet.add(16);
//		vkSet.add(17);
//		vkSet.add(18);
//		vkSet.add(19);
//		vkSet.add(20);
//		vkSet.add(27);
    vkSet.add(32); //space
    vkSet.add(44); //comma
    vkSet.add(46); //period
    for (int i = 65; i < 91; i++) //A-Z
      vkSet.add(i);
//		for (int i = 32; i < 41; i++)
//			vkSet.add(i);
//		for (int i = 44; i < 58; i++)
//			vkSet.add(i);
//		vkSet.add(59);
//		vkSet.add(61);
//		for (int i = 65; i < 94; i++)
//			vkSet.add(i);
//		for (int i = 96; i < 108; i++)
//			vkSet.add(i);
//		for (int i = 109; i < 124; i++)
//			vkSet.add(i);
//		vkSet.add(127);
//		vkSet.add(144);
//		vkSet.add(145);
//		vkSet.add(155);
//		vkSet.add(192);
    vkSet.add(222); //apostrophe
//		vkSet.add(524);
//		vkSet.add(525);
    return vkSet;
  }
}
