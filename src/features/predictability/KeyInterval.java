package features.predictability;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeSet;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import keystroke.KeyStroke;

public class KeyInterval extends Predictability implements ExtractionModule {

  private static final String modelName = "SESSION1";
  private static final String gramType = "keystroke";
  private static final boolean LOG_TIME = false;

  private static TreeSet<Integer> keySet = new TreeSet<Integer>(KeyStroke.UsefulVKCodes());
  private HashMap<Integer, LinkedList<Double>> featureMap = new HashMap<Integer, LinkedList<Double>>(1800);

  public KeyInterval() {
    super(modelName, gramType);
  }

  @Override
  public Collection<Feature> extract(DataNode data) {
    createSearchSpace();

    LinkedList<KeyStroke> buffer = new LinkedList<KeyStroke>();
    for (Answer a : data) {
      buffer.clear();
      for (KeyStroke k : a.getKeyStrokeList()) {
        buffer.add(k);
        if (buffer.size() > 3) {
          if (isInterval(buffer) && buffer.get(2).getWhen() - buffer.get(1).getWhen() > -1) {
            KeyStroke ks1 = buffer.get(1);
            KeyStroke ks2 = buffer.get(2);
            long interval = (ks2.getWhen() - ks1.getWhen());

            double prob = keyStrokeBigramModel.getBigramProbability(ks1, ks2);

            double w0, w1;
            double expectedTime;
            if (!LOG_TIME) {
              w0 = 333.1600;
              w1 = -315.7240;
              expectedTime = (w0 + w1 * prob);
            } else {
              w0 = 5.4564;
              w1 = -1.0034;
              expectedTime = Math.exp(w0 + w1 * prob) - 2;
            }

//            double pred_interval = (interval / 1000.0) / keyStrokeBigramModel.getBigramProbability(ks1, ks2);

            // Expected interval time.
//            Intercept     333.1600      0.607    548.664      0.000       331.970   334.350
//            probability  -315.7240      2.279   -138.524      0.000      -320.191  -311.257

            // Expected log interval time
            // MIN: -16
//            Intercept       5.4564      0.001   9577.592      0.000         5.455     5.458
//            probability    -1.0034      0.002   -469.208      0.000        -1.008    -0.999


            featureMap.get(ks1.getKeyCode() * 1000 + ks2.getKeyCode()).add(interval / expectedTime);
          }
          buffer.poll();
        }
      }
    }
    LinkedList<Feature> output = new LinkedList<Feature>();
    for (Integer vkCode0 : keySet)
      for (Integer vkCode1 : keySet) {
        output.add(
            new Feature("I_Predict_" + KeyStroke.vkCodetoString(vkCode0) + "_" + KeyStroke.vkCodetoString(vkCode1),
                featureMap.get(vkCode0 * 1000 + vkCode1)));
      }
//		for (Feature f : output) System.out.println(f.toTemplate());
    return output;
  }

  private void createSearchSpace() {
    featureMap.clear();
    for (Integer key0 : keySet)
      for (Integer key1 : keySet) {
        featureMap.put(key0 * 1000 + key1, new LinkedList<Double>());
      }
  }


  /**
   * Determines if the buffer contains a key Interval.
   *
   * @return Hashtable to be used during extraction.
   */
  private boolean isInterval(LinkedList<KeyStroke> buffer) {
    // if the featureMap contains the hash value of the first two keys
    if (featureMap.keySet().contains(buffer.get(1).getKeyCode() * 1000 + buffer.get(2).getKeyCode())) {
      // if the keys follow the pattern press, press, release, release
      if (buffer.get(0).isKeyPress() && buffer.get(1).isKeyRelease()
          && buffer.get(2).isKeyPress() && buffer.get(3).isKeyRelease()) {
        // if the keys follow pattern A, A, B, B
        if (buffer.get(0).getKeyCode() == buffer.get(1).getKeyCode()
            && buffer.get(2).getKeyCode() == buffer.get(3).getKeyCode()) {
          return true;
        }
      }
    }
    return false;
  }

  public String getName() {
    return "Key Interval Predictability";
  }

}
