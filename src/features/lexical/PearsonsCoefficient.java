package features.lexical;

/**
 * Calculates Pearson's correlation coefficient between two variables.
 */
public class PearsonsCoefficient {

  /**
   * Calculates Pearson's Coefficient.
   *
   * Pearson's Coefficient is the ratio of the covariance of x and y to the product of the
   * variance of x times the variance of y.
   *
   * @param x the first variable
   * @param y the second variable
   * @return peason's coefficient.
   */
  public static double calculatePearsonsCoefficient(double[] x, double[] y) {
    if (x.length != y.length) {
      return Double.NaN;
    }

    int n = x.length;
    double sy = 0.;
    double sx = 0.;
    double sxy = 0.;
    double sxx = 0.;
    double syy = 0.;

    for (int i = 0; i < n; ++i) {
      sy += y[i];
      sx += x[i];
      sxx += x[i] * x[i];
      syy += y[i] * y[i];
      sxy += x[i] * y[i];
    }

    return (sxy - sx * sy / n) / (Math.sqrt(sxx - sx * sx / n) * Math.sqrt(syy - sy * sy / n));
  }
}
