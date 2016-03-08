package features.lexical;

import static features.lexical.LinearRegression.fit2DLinearModel;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import edu.stanford.nlp.util.Pair;

/**
 * Created with IntelliJ IDEA. User: andrew Date: 10/29/12 Time: 4:40 PM To change this template use File | Settings |
 * File Templates.
 */
public class LinearRegressionTest {

  @Test
  public void testFit2DLinearRegressionReturnsNullWithInequalInput() {
    assertNull(fit2DLinearModel(new double[]{0.0}, new double[]{0.0, 1.0}));
  }

  @Test
  public void testFit2DLinearRegressionWorksCorrectly() {
    double[] x = new double[]{0., 1., 2.};
    double[] y = new double[]{0., 2., 4.};

    Pair<Double, Double> p = fit2DLinearModel(x, y);
    assertEquals(p.first, 0., 0.0001);
    assertEquals(p.second, 2., 0.0001);
  }

  @Test
    public void testFit2DLinearRegressionWorksCorrectlyWithOffset() {
      double[] x = new double[]{0., 1., 2.};
      double[] y = new double[]{1., 3., 5.};

      Pair<Double, Double> p = fit2DLinearModel(x, y);
      assertEquals(p.first, 1., 0.0001);
      assertEquals(p.second, 2., 0.0001);
    }
}
