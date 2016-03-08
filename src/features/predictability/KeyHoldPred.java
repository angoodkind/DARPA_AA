package features.predictability;

import events.EventList;
import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import keystroke.KeyStroke;
import keytouch.KeyTouch;
import ngrammodel.Bigram;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.TreeSet;

/**
 * Extracts key hold times from the selected data.
 * Key hold time is defined as the time taken between a keypress and keyrelease event for the same key.
 * KEYHOLD CLASS IS AN EXTRACTION MODULE
 * RECEIVES INPUT FROM DATNODE CLASS WHICH REPRESENTS ALL 12 ANSWERS OF A SINGLE USER
 * PROCESSES DATANODE AND RETURNS OUTPUT AS FEATURE
 * THIS CLASS IS DESIGNED TO BUILD A CONTAINER OF A USER'S KEYHOLD TIMES ASSOCIATED WHICH EACH KEY PRESSED BY THE USER
 *
 * @author Patrick Koch
 * @see extractors.nyit.ExtractionModule
 */
public class KeyHoldPred extends Predictability implements ExtractionModule {

  private static final String modelName = "SESSION1";
  private static final String gramType = "keystroke";

  private static final boolean LOG_TIME = true;
  private static final boolean FORWARD = false;

  private static final boolean RATIO = true;

  public KeyHoldPred() {
    super(modelName, gramType);
  }

  static final boolean DEBUG = false;

  static final boolean WRITE_FILE = false;
  static final String csv_file = "keyhold.bigram.csv";

  public LinkedList<Feature> extract(
      DataNode data) {//REQUIRES DATA NODE-INDIVIDUAL USER WITH KEYSTROKE STREAM-ALL ANSWERS

    Hashtable<Integer, LinkedList<Integer>> keyTable =
        constructKeyTable();//CONSTRUCT EMPTY CONTAINER WHICH STORES USER KEYHOLD VALUES FOR EACH KEY

    TreeSet<Integer> keySet = new TreeSet<Integer>(keyTable.keySet());//CONTAINS A LIST OF KEYS

    //----------------------FEATURE EXTRACTION STARTS
    // HERE------------------------------------------------------------------------
    BufferedWriter outf = null;
    try {
      outf = new BufferedWriter(new FileWriter(csv_file, true));

      for (Answer a : data) {//ITERATION THROUGH TO GET LIST OF KEYSTROKES
        EventList<KeyStroke> keys = a.getKeyStrokeList();//STORE KEYSTROKES

        for (Integer vkCode : keySet) { //ITERATE THROUGH THE LIST OF USEFUL VK CODES.
          long pressTime = 0; //INITIALIZE PRESS TIME TO 0
          boolean firstKeyPress = true;
          // THIS LOOP FINDS THE KEY HOLD TIME BY ITERATING THROUGH THE LIST OF KEYSTROKES OF THE CURRENT ANSWER FOR
          // THE SPECIFIED VK CODE
          for (int i = 0; i < keys.size(); i++) {
            KeyStroke k = keys.get(i);
            if (k.getKeyCode() == vkCode) {  // Found the key we're looking for.
              if (k.isKeyPress() && firstKeyPress) {
                pressTime = k.getWhen();//ASSIGN FIRST TIMESTAMP TO FIRST KEY PRESS
                firstKeyPress = false;
              } else if (k.isKeyRelease() &&
                  pressTime != 0) {//FIND KEY RELEASE AND SUBTRACT PRESS TIME FROM KEY RELEASE TIME TO GET KEYHOLD

                long holdTime = (k.getWhen() - pressTime);

                int j = i + 1;
                KeyStroke next_press = null;
                while (j < keys.size() && !(next_press = keys.get(j)).isKeyPress()) {
                  j++;
                }

                KeyStroke prev_press = null;
                j = i - 1;
                while (j > -1 && !(prev_press = keys.get(j)).isKeyPress()) {
                  j--;
                }
                double prob = -1.0;
                double forward_prob = -1.0;
                if (next_press != null && prev_press != null) {
                  forward_prob = keyStrokeBigramModel.getBigramProbability(k, next_press);
                  prob = keyStrokeBigramModel.getBigramProbability(prev_press, k);

                  if (WRITE_FILE) {
                    outf.write(KeyStroke.vkCodetoString(prev_press.getKeyCode())); // k_i-1
                    outf.write(',');
                    outf.write(KeyStroke.vkCodetoString(k.getKeyCode())); // k_i.
                    outf.write(',');
                    outf.write(KeyStroke.vkCodetoString(next_press.getKeyCode())); // k_+1.
                    outf.write(',');
                    outf.write(Long.toString(holdTime));  // previous hold time.
                    outf.write(',');
                    outf.write(Double.toString(prob)); // backward probability
                    outf.write(',');
                    outf.write(Double.toString(forward_prob));  // forward probability
                    outf.write('\n');
                  }
                }


                double w0, w1;
                double expectedTime;

                if (FORWARD) {
                  if (next_press == null) continue;
                  if (!LOG_TIME) {
                    w0 = 107.5641;
                    w1 = -29.4067;
                    expectedTime = (w0 + w1 * forward_prob);
                  } else {
                    w0 = 4.5962;
                    w1 = -0.3785;
                    expectedTime =  Math.exp(w0 + w1 * forward_prob);
                  }
                } else {
                  if (prev_press == null) continue;
                  if (!LOG_TIME) {
                    // AR: 07/16/2014 -- these numbers seem like they might be wrong.  I'm finding different stats for
                    // keyholds.
                    // AG's weights
//                  w0 = 104.492571;
//                  w1 = -33.669445;
                    w0 = 103.9355;
                    w1 = -7.5380;
                    expectedTime = (w0 + w1 * prob);
                  } else {
                    // AG's weights
//                  w0 = 5.2975;
//                  w1 = -0.1730;
                    w0 = 4.5682;
                    w1 = -0.3650;
                    expectedTime = Math.exp(w0 + w1 * prob);
                  }
                }


//                Intercept     103.9355      0.109    952.966      0.000       103.722   104.149
//                probability    -7.5380      0.633    -11.901      0.000        -8.779    -6.297

//                LOG -0.140745488543
//                Intercept       4.5682      0.000    1.7e+04      0.000         4.568     4.569
//                probability    -0.3650      0.002   -234.561      0.000        -0.368    -0.362


                // Forward bigram
//                Intercept    107.5641      0.128    837.425      0.000       107.312   107.816
//                next_prob    -29.4067      0.573    -51.279      0.000       -30.531   -28.283

//                LOG  -0.161121764481
//                Intercept      4.5962      0.000   1.46e+04      0.000         4.596     4.597
//                next_prob     -0.3785      0.001   -269.366      0.000        -0.381    -0.376


                // Expected KEY HOLD from bigram probability.
                // Spearman's: -0.0693053554753   (spearman is a rank test so there's no diff between raw and log space.
                // Pearsons
                // Raw time : -0.0636452063378
                // Log time : -0.193641075299

                // Intercept      104.492571
                // probability    -33.669445
//              Intercept     104.4926      0.084   1237.164      0.000       104.327   104.658
//              probability   -33.6694      0.317   -106.160      0.000       -34.291   -33.048

                // Log space
                // Min: -1
//              Intercept       5.2975      0.000   3.23e+04      0.000         5.297     5.298
//              probability    -0.1730      0.001   -281.137      0.000        -0.174    -0.172


                int predHoldTime;
                if (RATIO) {
                  predHoldTime = (int) (100 * (1 - holdTime / expectedTime));
                  // AR: This makes the ratio feature a percentage increase or decrease.  it should keep the numbers more
                  // consistent when smashing down to an integer
                } else {
                  predHoldTime = (int) (holdTime - expectedTime);
                }
                keyTable.get(vkCode).add(predHoldTime); //DETERMINE KEYHOLD TIME

                if (DEBUG) {
                  System.out.println(
                      Character.toString(prev_press.getKeyChar()) + "," + Character.toString(k.getKeyChar()) + ":" +
                          prob + " -- " + expectedTime);
                }

                firstKeyPress = true;
                //THIS LOGIC CANNOT PROCESS SLURS BECAUSE THEY ARE TWO CONSECUTIVE KEYSTROKE...SLURS ARE IGNORED
              }
            }
          }
        }

        /***
         * TODO: possibly replace with the following KeyTouch methodology.    I never really liked the idea of
         * iterating over valid
         vk codes.


         LinkedList<KeyTouch> touches = KeyTouch.parseSessionToKeyTouches(a.getKeyStrokes());
         for (int i = 0; i < touches.size(); i++) {
         KeyTouch t = touches.get(i);
         int vkCode = t.getKeyCode();

         if (WRITE_FILE && i > 0 && i < touches.size() - 1) {
         KeyTouch t_prev = touches.get(i - 1);
         KeyTouch t_next = touches.get(i + 1);
         outf.write(KeyStroke.vkCodetoString(touches.get(i - 1).getKeyCode())); // k_i-1
         outf.write(',');
         outf.write(KeyStroke.vkCodetoString(t.getKeyCode())); // k_i.
         outf.write(',');
         outf.write(KeyStroke.vkCodetoString(touches.get(i + 1).getKeyCode())); // k_+1.
         outf.write(',');
         outf.write(Long.toString(t.getHoldTime()));  // previous hold time.
         outf.write(',');
         outf.write(Double.toString(keyStrokeBigramModel
         .getBigramProbability(KeyStroke.vkCodetoString(t_prev.getKeyCode()),
         KeyStroke.vkCodetoString(t.getKeyCode())))); // backward probability
         outf.write(',');
         outf.write(Double.toString(keyStrokeBigramModel
         .getBigramProbability(KeyStroke.vkCodetoString(t.getKeyCode()),
         KeyStroke.vkCodetoString(t_next.getKeyCode())))); // forward probability
         outf.write('\n');
         }


         //          if we want to go this way....
         if (touchKeyTable.keySet().contains(vkCode)) {
         touchKeyTable.get(vkCode).add((int) t.getHoldTime());
         }
         }
         */
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

    LinkedList<Feature> output = new LinkedList<Feature>(); //CONTAINER TO HOLD KEYHOLD VALUES

    //----------------------------FEATURE EXTRACTION ENDS
    // HERE------------------------------------------------------------------------------------

    for (Integer i : keySet) {
      output.add(new Feature("H_Predict" + KeyStroke.vkCodetoString(i), keyTable.get(i)));//APPEND H TO KEY
    }
    return output;
  }

  //INITIALIZE THE CONTAINER TO STORE A USERS ANSWERS KEYHOLD VALUES...BUILD THE KEY NAMES IN THE CONTAINER BEFORE
  // USING IT TO STORE THE KEYHOLD VALUE TIMES
  private Hashtable<Integer, LinkedList<Integer>> constructKeyTable() {
    Hashtable<Integer, LinkedList<Integer>> keyTable = new Hashtable<Integer, LinkedList<Integer>>();
    for (Integer key : KeyStroke.UsefulVKCodes())
      keyTable.put(key, new LinkedList<Integer>());
    return keyTable;
  }

  public String getName() {
    return "Key Hold";
  }

}
