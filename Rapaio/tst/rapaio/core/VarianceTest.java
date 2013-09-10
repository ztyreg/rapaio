/*
 * Copyright 2013 Aurelian Tutuianu <padreati@yahoo.com>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package rapaio.core;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import static rapaio.core.BaseMath.sqrt;
import rapaio.core.stat.Variance;
import rapaio.data.Frame;
import rapaio.datasets.Datasets;

import java.io.IOException;

/**
 * @author <a href="mailto:padreati@yahoo.com">Aurelian Tutuianu</a>
 */
public class VarianceTest extends CoreStatTestUtil {

    public VarianceTest() throws IOException {
    }

    @Test
    public void testRReferenceVariance() {
        Frame df = getDataFrame();
        assertEquals(Double.valueOf("1.0012615815492349469"), sqrt(new Variance(df.getCol(0)).getValue()), 1e-12);
    }

    @Test
    public void testPearsonDSVariance() throws IOException {
        Frame df = Datasets.loadPearsonHeightDataset();
        assertEquals(Double.valueOf("1").doubleValue(), new Variance(df.getCol("Son")).getValue(), 1e-12);
    }
}