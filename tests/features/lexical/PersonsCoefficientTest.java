package features.lexical;

import static features.lexical.PearsonsCoefficient.calculatePearsonsCoefficient;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Created with IntelliJ IDEA. User: andrew Date: 10/29/12 Time: 4:40 PM To change this template use File | Settings |
 * File Templates.
 */
public class PersonsCoefficientTest {

  @Test
  public void testCalculatePersonsCoefficientReturnsNaNWithInequalInput() {
    assertEquals(Double.NaN, calculatePearsonsCoefficient(new double[]{0.}, new double[]{0., 1.}), 0.0001);
  }

  @Test
  public void testCalculatePersonsCoefficientWorksCorrectly() {
    double[] x = new double[]{0., 1., 2.};
    double[] y = new double[]{0., 2., 4.};

    double r = calculatePearsonsCoefficient(x, y);
    assertEquals(1., r, 0.0001);
  }

  @Test
  public void testFit2DLinearRegressionWorksCorrectlyWithOffset() {
    double[] x = new double[]{0., 1., 2.};
    double[] y = new double[]{1., 3., 5.};

    double r = calculatePearsonsCoefficient(x, y);
    assertEquals(1., r, 0.0001);
  }
}
