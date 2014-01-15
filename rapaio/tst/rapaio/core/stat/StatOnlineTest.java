package rapaio.core.stat;

import org.junit.Test;
import rapaio.data.NumVector;
import rapaio.data.Vector;
import rapaio.data.Vectors;
import rapaio.distributions.Normal;
import rapaio.graphics.Plot;
import rapaio.graphics.plot.Points;
import rapaio.printer.LocalPrinter;

import static rapaio.workspace.Workspace.draw;
import static rapaio.workspace.Workspace.setPrinter;

/**
 * User: Aurelian Tutuianu <paderati@yahoo.com>
 */
public class StatOnlineTest {

    @Test
    public void testVariance() {

//        RandomSource.setSeed(1223);
        setPrinter(new LocalPrinter());

        int LEN = 1_000;
        Vector v = new Normal(0, 1).sample(LEN);

        StatOnline statOnline = new StatOnline();

        Vector index = Vectors.newSeq(LEN);
        Vector varLeft = new NumVector(new double[LEN]);
        Vector varRight = new NumVector(new double[LEN]);
        Vector varSum = new NumVector(new double[LEN]);

        for (int i = 0; i < LEN; i++) {
            statOnline.update(v.getValue(i));
            if (i > 0) {
                varLeft.setValue(i, statOnline.getVariance());
            }
        }
        statOnline.clean();
        for (int i = LEN - 1; i >= 0; i--) {
            statOnline.update(v.getValue(i));
            if (i < LEN - 1) {
                varRight.setValue(i, statOnline.getVariance());
            }
        }
        for (int i = 0; i < LEN; i++) {
            varSum.setValue(i, (varLeft.getValue(i) + varRight.getValue(i)) / 2);
        }

        draw(new Plot()
                .add(new Points(index, varLeft).setColorIndex(1))
                .add(new Points(index, varRight).setColorIndex(2))
                .add(new Points(index, varSum).setColorIndex(3))
                .setSizeIndex(0.4)
                .setYRange(0.5, 1.5)
        );
    }
}