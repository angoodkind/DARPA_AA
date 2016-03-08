package output.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

/**
 * ExpectedValueCalculator calculates expected values based on development features.
 * <p/>
 * These expected values are used for normalization of features on live data.
 */
public class ExpectedValueCalculator extends TestVectorProcessor {

  HashMap<String, FeatureStats> stats;  // a hash to accumulate Feature statistics.

  public ExpectedValueCalculator() {
    super();
    stats = new HashMap<>();
  }

  @Override
  /**
   * Accumulates feature statistics from a single data point for multiple features
   *
   * Currently assumes that all features are numbers.
   *
   * @param data an array of string representations of data.
   */
  public void processData(String[] data) {
    for (int i = 0; i < data.length; ++i) {
      if (!stats.containsKey(attr_names[i])) {
        stats.put(attr_names[i], new FeatureStats());
      }
      for (String s : data[i].split(",")) {
        if (s.length() > 0) {
          try {
          stats.get(attr_names[i]).add(Double.parseDouble(s));
          } catch (NumberFormatException ignored) {
            // Don't process string attributes.
          }
        }
      }
    }
  }

  /**
   * Writes the mean and standard deviation of each feature to a file.
   * <p/>
   * Features are written one per line with feature name, mean and standard deviation separated by commas, and
   * in that order
   * <p/>
   * Format:
   * <name>,<mean>,<stdev>
   *
   * @param filename the output file name
   * @throws IOException if there is a file writing problem
   */
  public void writeExpectedValues(String filename) throws IOException {
    BufferedWriter writer = new BufferedWriter(new FileWriter(filename));

    for (String attr_name : attr_names) {
      writer.write(attr_name + "," + getMean(attr_name) + "," + getStdev(attr_name) + "\n");
    }
    writer.close();
  }

  /**
   * Retrieves the mean for a specific feature
   *
   * @param feature the feature name
   * @return the mean value
   */
  public double getMean(String feature) {
    return stats.get(feature).getMean();
  }

  /**
   * Retrieves the standard deviation for a specific feature
   *
   * @param feature the feature name
   * @return the standard deviation
   */
  public double getStdev(String feature) {
    return stats.get(feature).getStdev();
  }

  private class FeatureStats {
    double sum;
    double ssq;
    int n;

    /**
     * Constructs a new FeatureStats object.
     */
    public FeatureStats() {
      sum = 0.;
      ssq = 0.;
      n = 0;
    }

    /**
     * Prints the mean and standard deviation of the feature.
     *
     * @return a "mean,std.dev"
     */
    public String toString() {
      return getMean() + "" + getStdev();
    }

    /**
     * Calculates the mean.
     *
     * @return mean value
     */
    private Double getMean() {
      return sum / n;
    }

    /**
     * Calculates the standard deviation.
     *
     * @return standard deviation value
     */
    private Double getStdev() {
      Double mean = sum / n;
      return Math.sqrt((ssq - (n * mean * mean)) / (n - 1));
    }

    /**
     * Add a value to the stats.
     *
     * @param v the value
     */
    public void add(double v) {
      n++;
      sum += v;
      ssq += (v * v);
    }
  }
}
