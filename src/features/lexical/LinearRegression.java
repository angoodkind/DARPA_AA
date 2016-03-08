package features.lexical;


import java.util.LinkedList;

import edu.stanford.nlp.util.Pair;

/**
 * A support class for linear regression calculations.
 */
public class LinearRegression {

  /**
   * Calculates linear regression coefficients, c0 and c1, minimizing the least squared error of c0 + c1*x = y
   * <p/>
   * Returns a pair of c0 and c1.
   *
   * @param x the input observation data
   * @param y the target
   * @return linear regression coefficients.
   */
  public static Pair<Double, Double> fit2DLinearModel(double[] x, double[] y) {
    if (x.length != y.length) {
      return null;
    }

    int n = x.length;
    double sxy = 0.;
    double sy = 0.;
    double sx = 0.;
    double sxx = 0.;

    for (int i = 0; i < n; ++i) {
      sxy += x[i] * y[i];
      sy += y[i];
      sx += x[i];
      sxx += x[i] * x[i];
    }

    double c1 = (sxy - sy * sx / n) / (sxx - sx * sx / n);
    double c0 = (sy / n - c1 * sx / n);

    return new Pair<>(c0, c1);
  }
  
  /**
   * Calculates linear regression coefficients, c0 and c1, minimizing the least squared error of c0 + c1*x = y
   * <p/>
   * Returns a pair of c0 and c1.
   *
   * @param x the input observation data
   * @param y the target
   * @return linear regression coefficients.
   */
  public static Pair<Double, Double> fit2DLinearModel(LinkedList<Double> x, LinkedList<Double> y) {
    if (x.size() != y.size()) {
      return null;
    }

    int n = x.size();
    double sxy = 0.;
    double sy = 0.;
    double sx = 0.;
    double sxx = 0.;

    for (int i = 0; i < n; ++i) {
      sxy += x.get(i) * y.get(i);
      sy += y.get(i);
      sx += x.get(i);
      sxx += x.get(i) * x.get(i);
    }

    double c1 = (sxy - sy * sx / n) / (sxx - sx * sx / n);
    double c0 = (sy / n - c1 * sx / n);

    return new Pair<>(c0, c1);
  }

}
