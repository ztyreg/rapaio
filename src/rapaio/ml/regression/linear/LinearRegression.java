/*
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 *    Copyright 2013 Aurelian Tutuianu
 *    Copyright 2014 Aurelian Tutuianu
 *    Copyright 2015 Aurelian Tutuianu
 *    Copyright 2016 Aurelian Tutuianu
 *    Copyright 2017 Aurelian Tutuianu
 *    Copyright 2018 Aurelian Tutuianu
 *    Copyright 2019 Aurelian Tutuianu
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

package rapaio.ml.regression.linear;

import rapaio.data.*;
import rapaio.data.filter.frame.*;
import rapaio.math.linear.*;
import rapaio.math.linear.dense.*;
import rapaio.ml.common.*;
import rapaio.ml.regression.*;
import rapaio.printer.*;
import rapaio.printer.format.*;

/**
 * User: Aurelian Tutuianu <padreati@yahoo.com>
 */
public class LinearRegression extends AbstractRegression implements DefaultPrintable {

    /**
     * Builds a linear regression model with intercept, no centering and no scaling.
     *
     * @return new instance of linear regression model
     */
    public static LinearRegression newLm() {
        return new LinearRegression()
                .withIntercept(true)
                .withCentering(false)
                .withScaling(false);
    }

    private static final long serialVersionUID = 8595413796946622895L;

    protected boolean intercept = true;
    protected boolean centering = false;
    protected boolean scaling = false;
    protected RM beta;

    @Override
    public LinearRegression newInstance() {
        return newInstanceDecoration(new LinearRegression())
                .withIntercept(intercept)
                .withCentering(centering)
                .withScaling(scaling);
    }

    @Override
    public String name() {
        return "LinearRegression";
    }

    @Override
    public String fullName() {
        StringBuilder sb = new StringBuilder();
        sb.append(name()).append("(");
        sb.append("intercept=").append(intercept).append(",");
        sb.append("centering=").append(centering).append(",");
        sb.append("scaling=").append(scaling);
        sb.append(")");
        return sb.toString();
    }

    @Override
    public Capabilities capabilities() {
        return new Capabilities()
                .withInputTypes(VType.DOUBLE, VType.INT, VType.BINARY)
                .withTargetTypes(VType.DOUBLE)
                .withInputCount(1, 1_000_000)
                .withTargetCount(1, 1_000_000)
                .withAllowMissingInputValues(false)
                .withAllowMissingTargetValues(false);
    }

    /**
     * @return true if the linear model adds an intercept
     */
    public boolean hasIntercept() {
        return intercept;
    }

    /**
     * Configure the model to introduce an intercept or not.
     *
     * @param intercept if true an intercept variable will be generated, false otherwise
     * @return linear model instance
     */
    public LinearRegression withIntercept(boolean intercept) {
        this.intercept = intercept;
        return this;
    }

    public boolean hasCentering() {
        return centering;
    }

    public LinearRegression withCentering(boolean centering) {
        this.centering = centering;
        return this;
    }

    public boolean hasScaling() {
        return scaling;
    }

    public LinearRegression withScaling(boolean scaling) {
        this.scaling = scaling;
        return this;
    }

    public RV firstCoefficients() {
        return beta.mapCol(0);
    }

    public RV getCoefficients(int targetIndex) {
        return beta.mapCol(targetIndex);
    }

    public RM allCoefficients() {
        return beta;
    }

    @Override
    protected FitSetup prepareFit(Frame df, Var weights, String... targetVarNames) {
        if (intercept) {
            return super.prepareFit(FIntercept.filter().apply(df), weights, targetVarNames);
        }
        return super.prepareFit(df, weights, targetVarNames);
    }

    @Override
    protected boolean coreFit(Frame df, Var weights) {
        RM X = SolidRM.copy(df.mapVars(inputNames()));
        RM Y = SolidRM.copy(df.mapVars(targetNames()));
        beta = QRDecomposition.from(X).solve(Y);
        return true;
    }

    @Override
    public LinearRegression fit(Frame df, String... targetVarNames) {
        return (LinearRegression) super.fit(df, targetVarNames);
    }

    @Override
    public LinearRegression fit(Frame df, Var weights, String... targetVarNames) {
        return (LinearRegression) super.fit(df, weights, targetVarNames);
    }

    @Override
    protected PredSetup preparePredict(Frame df, boolean withResiduals) {
        if (intercept) {
            return super.preparePredict(FIntercept.filter().apply(df), withResiduals);
        }
        return super.preparePredict(df, withResiduals);
    }

    @Override
    protected LinearRPrediction corePredict(Frame df, boolean withResiduals) {
        LinearRPrediction rp = new LinearRPrediction(this, df, withResiduals);
        for (int i = 0; i < targetNames().length; i++) {
            String target = targetName(i);
            for (int j = 0; j < rp.prediction(target).rowCount(); j++) {
                double fit = 0.0;
                for (int k = 0; k < inputNames().length; k++) {
                    fit += beta.get(k, i) * df.getDouble(j, inputName(k));
                }
                rp.prediction(target).setDouble(j, fit);
            }
        }

        rp.buildComplete();
        return rp;
    }

    @Override
    public LinearRPrediction predict(Frame df) {
        return predict(df, false);
    }

    @Override
    public LinearRPrediction predict(Frame df, boolean withResiduals) {
        return (LinearRPrediction) super.predict(df, withResiduals);
    }

    @Override
    public String summary() {
        StringBuilder sb = new StringBuilder();
        sb.append(headerSummary());
        sb.append("\n");

        if (!hasLearned) {
            return sb.toString();
        }

        for (int i = 0; i < targetNames.length; i++) {
            String targetName = targetNames[i];
            sb.append("Target <<< ").append(targetName).append(" >>>\n\n");
            sb.append("> Coefficients: \n");
            RV coeff = beta.mapCol(i);

            TextTable tt = TextTable.empty(coeff.count() + 1, 2, 1, 0);
            tt.textCenter(0, 0, "Name");
            tt.textCenter(0, 1, "Estimate");
            for (int j = 0; j < coeff.count(); j++) {
                tt.textLeft(j + 1, 0, inputNames[j]);
                tt.floatMedium(j + 1, 1, coeff.get(j));
            }
            sb.append(tt.getDefaultText());
            sb.append("\n");
        }
        return sb.toString();
    }
}
