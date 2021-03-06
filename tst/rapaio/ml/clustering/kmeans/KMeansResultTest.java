package rapaio.ml.clustering.kmeans;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rapaio.core.RandomSource;
import rapaio.data.Frame;
import rapaio.data.Var;
import rapaio.datasets.Datasets;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by <a href="mailto:padreati@yahoo.com">Aurelian Tutuianu</a> on 9/4/20.
 */
public class KMeansResultTest {

    @BeforeEach
    void beforeEach() {
        RandomSource.setSeed(42);
    }

    @Test
    void basicTest() {

        Frame df = Datasets.loadOldFaithful();
        KMeans model = KMeans.newModel().k.set(3).fit(df);
        KMeansResult result = model.predict(df);


        Var distances = result.getDistances();
        Frame clusterSummary = result.getClusterSummary();

        assertEquals(df.rowCount(), distances.rowCount());
        assertEquals(model.getCentroids().rowCount(), clusterSummary.rowCount());
    }

    @Test
    void printTest() {
        Frame df = Datasets.loadOldFaithful();
        KMeans model = KMeans.newModel().k.set(3).fit(df);
        KMeansResult result = model.predict(df);

        assertEquals("KMeansResult{}", result.toString());
        assertEquals("Overall: \n" +
                "> count: 272\n" +
                "> mean: 19.224481\n" +
                "> var: 693.0516358\n" +
                "> sd: 26.3258739\n" +
                "> inertia:5,229.05884\n" +
                "> iterations:4\n" +
                "\n" +
                "Per cluster: \n" +
                "    ID count    mean         var      var/total    sd     \n" +
                "[0]  1    97 31.3079183 1,116.6942881 1.6112714 33.416976 \n" +
                "[1]  2    91 12.2831184   312.4622575 0.4508499 17.676602 \n" +
                "[2]  3    84 12.7907975   367.087476  0.5296683 19.159527 \n", result.toSummary());
        assertEquals(result.toSummary(), result.toContent());
        assertEquals(result.toSummary(), result.toFullContent());
    }
}
