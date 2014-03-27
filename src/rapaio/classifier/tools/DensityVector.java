/*
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 *    Copyright 2013 Aurelian Tutuianu
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

package rapaio.classifier.tools;

import rapaio.core.RandomSource;
import rapaio.data.Numeric;
import rapaio.data.Vector;

/**
 * @author <a href="mailto:padreati@yahoo.com>Aurelian Tutuianu</a>
 */
public class DensityVector {

    private final String[] labels;
    private final double[] values;

    public DensityVector(String[] labels) {
        if (!labels[0].equals("?")) {
            this.labels = new String[labels.length + 1];
            this.labels[0] = "?";
            System.arraycopy(labels, 0, this.labels, 1, this.labels.length);
        } else {
            this.labels = labels;
        }
        this.values = new double[labels.length];
    }

    public DensityVector(Vector vector, Numeric weights) {
        this.labels = vector.getDictionary();
        this.values = new double[labels.length];
        vector.stream().forEach(spot -> {
            values[spot.getIndex()] += weights.getValue(spot.row());
        });
    }

    public void out(int pos, double value) {
        values[pos] += value;
    }

    public double get(int pos) {
        return values[pos];
    }

    public int findBestIndex() {
        double n = 0;
        int bestIndex = -1;
        double best = Double.NEGATIVE_INFINITY;
        for (int i = 1; i < values.length; i++) {
            if (values[i] > best) {
                best = values[i];
                bestIndex = i;
                n = 1;
                continue;
            }
            if (values[i] == best) {
                if (RandomSource.nextDouble() > n / (n + 1)) {
                    best = values[i];
                    bestIndex = i;
                }
                n++;
            }
        }
        return bestIndex;
    }

    public void normalize() {
        double total = 0.0;
        for (double value : values) {
            total += value;
        }
        for (int i = 0; i < values.length; i++) {
            values[i] /= total;
        }
    }
}
