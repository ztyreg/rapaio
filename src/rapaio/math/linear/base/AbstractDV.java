package rapaio.math.linear.base;

import rapaio.math.linear.DM;
import rapaio.math.linear.DV;
import rapaio.math.linear.dense.DMStripe;
import rapaio.printer.Format;
import rapaio.printer.Printer;
import rapaio.printer.TextTable;
import rapaio.printer.opt.POption;
import rapaio.util.function.Double2DoubleFunction;

import java.util.function.BiFunction;

/**
 * Created by <a href="mailto:padreati@yahoo.com">Aurelian Tutuianu</a> on 1/8/20.
 */
public abstract class AbstractDV implements DV {

    private static final long serialVersionUID = 4164614372206348682L;

    protected void checkConformance(DV vector) {
        if (size() != vector.size()) {
            throw new IllegalArgumentException(
                    String.format("Vectors are not conform for operation: [%d] vs [%d]", size(), vector.size()));
        }
    }

    @Override
    public DV add(double x) {
        for (int i = 0; i < size(); i++) {
            set(i, get(i) + x);
        }
        return this;
    }

    @Override
    public DV add(DV b) {
        checkConformance(b);
        for (int i = 0; i < size(); i++) {
            set(i, get(i) + b.get(i));
        }
        return this;
    }

    @Override
    public DV sub(double x) {
        for (int i = 0; i < size(); i++) {
            set(i, get(i) - x);
        }
        return this;
    }

    @Override
    public DV sub(DV b) {
        checkConformance(b);
        for (int i = 0; i < size(); i++) {
            set(i, get(i) - b.get(i));
        }
        return this;
    }

    @Override
    public DV mult(double scalar) {
        for (int i = 0; i < size(); i++) {
            set(i, get(i) * scalar);
        }
        return this;
    }

    @Override
    public DV mult(DV b) {
        checkConformance(b);
        for (int i = 0; i < size(); i++) {
            set(i, get(i) * b.get(i));
        }
        return this;
    }

    @Override
    public DV div(double scalar) {
        for (int i = 0; i < size(); i++) {
            set(i, get(i) / scalar);
        }
        return this;
    }

    @Override
    public DV div(DV b) {
        checkConformance(b);
        for (int i = 0; i < size(); i++) {
            set(i, get(i) / b.get(i));
        }
        return this;
    }

    @Override
    public double dot(DV b) {
        checkConformance(b);
        double s = 0;
        for (int i = 0; i < size(); i++) {
            s = Math.fma(get(i), b.get(i), s);
        }
        return s;
    }

    public double norm(double p) {
        if (p <= 0) {
            return size();
        }
        if (p == Double.POSITIVE_INFINITY) {
            double max = Double.NaN;
            for (int i = 0; i < size(); i++) {
                double value = get(i);
                if (Double.isNaN(max)) {
                    max = value;
                } else {
                    max = Math.max(max, value);
                }
            }
            return max;
        }

        double s = 0.0;
        for (int i = 0; i < size(); i++) {
            s += Math.pow(Math.abs(get(i)), p);
        }
        return Math.pow(s, 1.0 / p);
    }

    public DV normalize(double p) {
        double norm = norm(p);
        if (norm != 0.0)
            mult(1.0 / norm);
        return this;
    }

    @Override
    public double sum() {
        double sum = 0;
        for (int i = 0; i < size(); i++) {
            sum += get(i);
        }
        return sum;
    }

    @Override
    public double nansum() {
        double nansum = 0;
        for (int i = 0; i < size(); i++) {
            double value = get(i);
            if (Double.isNaN(value)) {
                continue;
            }
            nansum += value;
        }
        return nansum;
    }

    @Override
    public DV cumsum() {
        for (int i = 1; i < size(); i++) {
            inc(i, get(i - 1));
        }
        return this;
    }

    @Override
    public double prod() {
        double prod = 0;
        for (int i = 0; i < size(); i++) {
            prod *= get(i);
        }
        return prod;
    }

    @Override
    public double nanprod() {
        double nanprod = 0;
        for (int i = 0; i < size(); i++) {
            double value = get(i);
            if (Double.isNaN(value)) {
                continue;
            }
            nanprod += value;
        }
        return nanprod;
    }

    @Override
    public DV cumprod() {
        for (int i = 1; i < size(); i++) {
            set(i, get(i - 1) * get(i));
        }
        return this;
    }

    @Override
    public int nancount() {
        int nancount = 0;
        for (int i = 0; i < size(); i++) {
            double value = get(i);
            if (Double.isNaN(value)) {
                continue;
            }
            nancount++;
        }
        return nancount;
    }

    @Override
    public double mean() {
        return sum() / size();
    }

    @Override
    public double nanmean() {
        return nansum() / nancount();
    }

    @Override
    public double variance() {
        if (size() == 0) {
            return Double.NaN;
        }
        double mean = mean();
        double sum2 = 0;
        double sum3 = 0;
        for (int i = 0; i < size(); i++) {
            sum2 += Math.pow(get(i) - mean, 2);
            sum3 += get(i) - mean;
        }
        return (sum2 - Math.pow(sum3, 2) / size()) / (size() - 1.0);
    }

    @Override
    public double nanvariance() {
        double mean = nanmean();
        int missingCount = 0;
        int completeCount = 0;
        for (int i = 0; i < size(); i++) {
            if (Double.isNaN(get(i))) {
                missingCount++;
            } else {
                completeCount++;
            }
        }
        if (completeCount == 0) {
            return Double.NaN;
        }
        double sum2 = 0;
        double sum3 = 0;
        for (int i = 0; i < size(); i++) {
            if (Double.isNaN(get(i))) {
                continue;
            }
            sum2 += Math.pow(get(i) - mean, 2);
            sum3 += get(i) - mean;
        }
        return (sum2 - Math.pow(sum3, 2) / completeCount) / (completeCount - 1.0);
    }

    @Override
    public DV apply(Double2DoubleFunction f) {
        for (int i = 0; i < size(); i++) {
            set(i, f.applyAsDouble(get(i)));
        }
        return this;
    }

    @Override
    public AbstractDV apply(BiFunction<Integer, Double, Double> f) {
        for (int i = 0; i < size(); i++) {
            set(i, f.apply(i, get(i)));
        }
        return this;
    }

    @Override
    public boolean deepEquals(DV v, double eps) {
        if (size() != v.size()) {
            return false;
        }
        for (int i = 0; i < size(); i++) {
            if (Math.abs(get(i) - v.get(i)) > eps) {
                return false;
            }
        }
        return true;
    }

    @Override
    public DM asMatrix() {
        DMStripe res = rapaio.math.linear.dense.DMStripe.empty(size(), 1);
        for (int i = 0; i < size(); i++) {
            res.set(i, 0, get(i));
        }
        return res;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName()).append("{");
        sb.append("size:").append(size()).append(", values:");
        sb.append("[");
        for (int i = 0; i < Math.min(20, size()); i++) {
            sb.append(Format.floatFlex(get(i)));
            if (i != size() - 1) {
                sb.append(",");
            }
        }
        if (size() > 20) {
            sb.append("...");
        }
        sb.append("]}");
        return sb.toString();
    }

    @Override
    public String toSummary(Printer printer, POption<?>... options) {
        return toContent(printer, options);
    }

    @Override
    public String toContent(Printer printer, POption<?>... options) {
        int head = 20;
        int tail = 2;

        boolean full = head + tail >= size();

        if (full) {
            return toFullContent(printer, options);
        }

        int[] rows = new int[Math.min(head + tail + 1, size())];
        for (int i = 0; i < head; i++) {
            rows[i] = i;
        }
        rows[head] = -1;
        for (int i = 0; i < tail; i++) {
            rows[i + head + 1] = i + size() - tail;
        }
        TextTable tt = TextTable.empty(rows.length, 2, 0, 1);
        for (int i = 0; i < rows.length; i++) {
            if (rows[i] == -1) {
                tt.textCenter(i, 0, "...");
                tt.textCenter(i, 1, "...");
            } else {
                tt.intRow(i, 0, rows[i]);
                tt.floatFlexLong(i, 1, get(rows[i]));
            }
        }
        return tt.getDynamicText(printer, options);
    }

    @Override
    public String toFullContent(Printer printer, POption<?>... options) {

        TextTable tt = TextTable.empty(size(), 2, 0, 1);
        for (int i = 0; i < size(); i++) {
            tt.intRow(i, 0, i);
            tt.floatFlexLong(i, 1, get(i));
        }
        return tt.getDynamicText(printer, options);
    }
}
