package features.pause;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// The class name is an abbreviation for Between-KeyStrokes-Extended-Pause.
//  This class marks the input where a pause between keystrokes has occurred.
public class PauseBursts {
  // We set the threshold for what constitutes a pause here.
//  public final static int PAUSE = 300; // mSec.

  // The array that will hold the array of keystrokes-extended, KSE, entried
  //  of a given input.
  private KSE[] m_kseArray;

  // This is the list of the indices in the kseArray where a pause occurs.
  private List<Integer> m_pauseList;

  // This is the list of the indices in the kseArray down-strokes where a
  //  pause occurs. This is generated to be used by code that used text data
  //  where up-strokes are irrelevant.
  private List<Integer> m_pauseDownList;

  public PauseBursts() {
    m_kseArray = new KSE[0];
  }

  public KSE[] getKseArray() {
    return m_kseArray;
  }

  public List<Integer> getPauseList() {
    return m_pauseList;
  }

  public List<Integer> getPauseDownList() {
	  return m_pauseDownList;
  }

  public List<Integer> generatePauseList(String s, int pause_threshold) {
    Collection<KSE> kses = KSE.parseSessionToKSE(s);
    m_kseArray = kses.toArray(new KSE[0]);

    m_pauseList = new ArrayList<>();

    int i = 0;
    for (KSE kse : kses) {
      if (kse.m_pauseMs > pause_threshold) {
        m_pauseList.add(i);
      }
      i++;
    }
    return m_pauseList;
  }

  public List<Integer> generatePauseDownList(String s, int pause_threshold) {
    Collection<KSE> kses = KSE.parseSessionToKSE(s);
    m_kseArray = kses.toArray(new KSE[0]);

    m_pauseDownList = new ArrayList<>();

    int i = 0;
    for (KSE kse : kses) {
      if (kse.isKeyPress()) {
        if (kse.m_pauseMs > pause_threshold) {
          m_pauseDownList.add(i);
        }
        i++;
      }
    }
    return m_pauseDownList;
  }

  /**
   * Calculates the number of keystrokes since the idx'th pause.
   *
   * @param kseArray the KSE events
   * @param idx      the idx of the pause of interest.
   * @return the number of KSEs since the idx'th pause.
   */
  public long kseSinceLastPause(KSE[] kseArray, long idx, int pause_threshold) {
    // Check bounds.
    if (idx < 0 || idx > kseArray.length - 1) {
      // This could also throw an exception.
      return -1;
    }

    int cnt = 0;
    long pauseIdx = 0;
    for (int i = 0; i < kseArray.length && kseArray[i].getCursorPosition() <= idx; i++) {
      if (kseArray[i].m_pauseMs > pause_threshold) {
        pauseIdx = cnt;
      }
      cnt++;
    }

    // AR: I have no idea why this used half of the keystrokes.  Probably because it counts all events
    // rather than only "downs"
    // DP: long dist = (idx - pauseIdx) / 2; // Close enough for now. <========

    return idx - pauseIdx;
  }

  /**
   * Calculates the number of keystrokes since the idx'th pause.
   *
   * @param sText a keystroke string.
   * @param idx   the idx of the pause of interest.
   * @return the number of KSEs since the idx'th pause.
   */
  public long kseSinceLastPause(String sText, int idx, int pause_threshold) {
    Collection<KSE> kseArray = KSE.parseSessionToKSE(sText);

    return kseSinceLastPause((KSE[]) kseArray.toArray(), idx, pause_threshold);
  }

  /**
   * Calculates the duration in ms since the idx'th pause in the KSE array
   *
   * @param kseArray the kse array
   * @param idx      the index of interest
   * @return the duration since the idxth pause
   */
  public long durSinceLastPause(KSE[] kseArray, int idx, int pause_threshold) {
    // Check bounds.
    if (idx < 0 || idx > kseArray.length - 1) {
      return -1;
    }

    int cnt = 0;
    int pauseIdx = 0;
    for (int i = 0; i < kseArray.length && kseArray[i].getCursorPosition() <= idx; i++) {
      if (kseArray[i].m_pauseMs > pause_threshold) {
        pauseIdx = cnt;
      }
      cnt++;
    }

    return kseArray[idx].getWhen() - kseArray[pauseIdx].getWhen();
  }

  /**
   * Accumulates pause count stats: the number of pauses, non pauses and the rate of pausing.
   *
   * @param kseArray an array of KSE
   * @return a PauseCountStats object that holds these stats.
   */
  public PauseCountStats getPauseCountStats(KSE[] kseArray, int pause_threshold) {
    // Calculate the features below.
    int cntPause = 0;
    int cntNonPause = 0;
    double cntRateOfPauses;

    for (KSE aKseArray : kseArray) {
      // We only count down keys.
      if (aKseArray.isKeyRelease()) continue;

      if (aKseArray.m_pauseMs > pause_threshold) {
        cntPause++;
      } else {
        cntNonPause++;
      }
    }

    // Calculate rate of keystrokes with pauses to total keystrokes.
    cntRateOfPauses = (double) cntPause / (double) (cntPause + cntNonPause);

    return new PauseCountStats(cntPause, cntNonPause, cntRateOfPauses);
  }

  /**
   * Calculates the pause durations stats, total pause duration, duration of session and their ratio.
   *
   * @param kseArray an array of KSE
   * @return a PauseDurationStats object.
   */
  public PauseDurationStats getPauseDurationStats(KSE[] kseArray, int pause_threshold) {
    // Calculate the features below.
    long durOfPauses = 0;
    long durOfWholeText;
    double timeRateOfPauses;

    for (KSE kse : kseArray) {
      // We only count down keys.
      if (kse.isKeyRelease()) continue;

      if (kse.m_pauseMs > pause_threshold) {
        durOfPauses += kse.m_pauseMs;
      }
    }

    // Calculate the duration of the text.
    durOfWholeText = kseArray[kseArray.length - 1].getWhen() -
        kseArray[0].getWhen();

    // Calculate rate of the sum of the duration of the pauses to the
    //  total duration of the text.
    timeRateOfPauses = (double) durOfPauses / (double) durOfWholeText;

    return new PauseDurationStats(durOfPauses, durOfWholeText, timeRateOfPauses);
  }


  /**
   * Calculates the number of key presses and releases.
   *
   * @param kseArray an array of KSE
   * @return a KeyUpDownStats object to hold these features
   */
  public KeyUpDownStats getKeyUpDownStats(KSE[] kseArray) {
    // Calculate the features below.
    int cntUpKey = 0;
    int cntDownKey = 0;
    int upDownDiff;
    double upDownDiffRatio;

    for (KSE kse : kseArray) {
      if (kse.isKeyRelease()) {
        cntUpKey++;
      } else {
        cntDownKey++;
      }
    }
    upDownDiff = cntDownKey - cntUpKey;
    upDownDiffRatio = ((double) upDownDiff) / (cntDownKey + cntUpKey);

    System.out.println("Number of down keys: " + cntDownKey);
    System.out.println("Number of up keys: " + cntUpKey);
    System.out.println("Difference in number between up and down strokes: " + upDownDiff);
    System.out.println("Ratio of Difference to total: " + upDownDiffRatio);

    return new KeyUpDownStats(cntDownKey, cntUpKey, upDownDiff, upDownDiffRatio);
  }
} 

