package output.util;

import extractors.data.Feature;

/**
 * A utility interface for calculating aggregations of lists of numbers
 */
public interface Aggregator {
  /**
   * Performs an aggregation of a list of values.
   *
   * @param list the list of values
   * @return the output
   */
  public double aggregate(Object[] list);

  /**
   * Returns a label for the aggregator.
   *
   * @return the label
   */
  public String getLabel();

  /**
   * Performs an aggregation of a list of values, taken
   * from the Feature.toVector()
   *
   * @param feature	a <code>Feature</code>, which includes a list of values
   * @return the output
   */  
public double aggregate(Feature feature);
}
