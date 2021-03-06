package rapaio.data.filter;

import org.junit.jupiter.api.Test;
import rapaio.core.RandomSource;
import rapaio.core.distributions.ChiSquare;
import rapaio.core.stat.Mean;
import rapaio.core.stat.Variance;
import rapaio.data.Var;
import rapaio.data.VarDouble;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by <a href="mailto:padreati@yahoo.com">Aurelian Tutuianu</a> on 9/28/18.
 */
public class VJitterTest {

    @Test
    void testJitterStandard() {
        RandomSource.setSeed(1);
        Var a = VarDouble.fill(100_000, 1).fapply(VJitter.standard());
        Mean mean = Mean.of(a);
        Variance var = Variance.of(a);
        mean.printSummary();
        var.printSummary();

        assertTrue(mean.value() > 0.9);
        assertTrue(mean.value() < 1.1);
        assertTrue(var.sdValue() > 0.095);
        assertTrue(var.sdValue() < 1.005);
    }

    @Test
    void testJitterStandardSd() {
        RandomSource.setSeed(1);
        Var a = VarDouble.fill(100_000, 1).fapply(VJitter.gaussian(0, 2));
        Mean mean = Mean.of(a);
        Variance var = Variance.of(a);
        mean.printSummary();
        var.printSummary();

        assertTrue(mean.value() > 0.9);
        assertTrue(mean.value() < 1.1);
        assertTrue(var.sdValue() > 1.995);
        assertTrue(var.sdValue() < 2.005);
    }

    @Test
    void testJitterDistributed() {
        RandomSource.setSeed(1);
        Var a = VarDouble.fill(100_000, 1).fapply(VJitter.with(ChiSquare.of(5)));
        Mean mean = Mean.of(a);
        Variance var = Variance.of(a);
        mean.printSummary();
        var.printSummary();

        assertTrue(mean.value() > 5.0);
        assertTrue(mean.value() < 7.0);
        assertTrue(var.sdValue() > 3.1);
        assertTrue(var.sdValue() < 3.2);
    }

}
