/*
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 *    Copyright 2013 Aurelian Tutuianu
 *    Copyright 2014 Aurelian Tutuianu
 *    Copyright 2015 Aurelian Tutuianu
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
 *
 */

package rapaio.ml.regression.tree;

import java.io.Serializable;

/**
 * Created by <a href="mailto:padreati@yahoo.com>Aurelian Tutuianu</a> on 11/24/14.
 */
public interface RTreeTestFunction extends Serializable {

    RTreeTestFunction WeightedVarGain = new RTreeTestFunction() {

        private static final long serialVersionUID = 5119966657929147020L;

        @Override
        public String name() {
            return "WeightedVarGain";
        }

        @Override
        public double computeTestValue(RTreeTestPayload p) {
            double down = 0.0;
            double up = 0.0;
            for (int i = 0; i < p.splits; i++) {
                down += p.splitWeight[i];
                up += p.splitWeight[i] * p.splitVar[i];
            }
            return (down == 0) ? 0.0 : p.totalVar - up / down;
        }
    };
    RTreeTestFunction WeightedSdGain = new RTreeTestFunction() {

        private static final long serialVersionUID = 5119966657929147020L;

        @Override
        public String name() {
            return "WeightedSdGain";
        }

        @Override
        public double computeTestValue(RTreeTestPayload p) {
            double down = 0.0;
            double up = 0.0;
            for (int i = 0; i < p.splits; i++) {
                down += p.splitWeight[i];
                up += p.splitWeight[i] * Math.sqrt(p.splitVar[i]);
            }
            return (down == 0) ? Double.MIN_VALUE : Math.sqrt(p.totalVar) - up / down;
        }
    };

    ////////////////////
    // implementations

    String name();

    double computeTestValue(RTreeTestPayload payload);

}
