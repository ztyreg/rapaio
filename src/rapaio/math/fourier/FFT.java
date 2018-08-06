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


package rapaio.math.fourier;

import rapaio.data.VarDouble;
import rapaio.data.Var;
import rapaio.util.Pair;

/**
 * Fast Fourier Transform
 * <p>
 * Created by <a href="mailto:padreati@yahoo.com">Aurelian Tutuianu</a> on 6/28/16.
 */
public class FFT {

    // compute the FFT of x[], assuming its length is a power of 2
    public static Pair<Var, Var> fft(Pair<Var, Var> x) {

        int N = x._1.rowCount();

        // base case
        if (N == 1) return x;

        // radix 2 Cooley-Tukey FFT
        if (N % 2 != 0) {
            throw new RuntimeException("N is not a power of 2");
        }

        // fft of even terms
        int[] evenMap = new int[N / 2];
        for (int k = 0; k < N / 2; k++) {
            evenMap[k] = 2 * k;
        }
        Pair<Var, Var> even = Pair.from(x._1.mapRows(evenMap), x._2.mapRows(evenMap));
        Pair<Var, Var> q = fft(even);

        // fft of odd terms
        int[] oddMap = new int[N / 2];
        for (int k = 0; k < N / 2; k++) {
            oddMap[k] = 2 * k + 1;
        }
        Pair<Var, Var> r = fft(Pair.from(x._1.mapRows(oddMap), x._2.mapRows(oddMap)));

        // combine
        Var rey = VarDouble.fill(N, 0.0);
        Var imy = VarDouble.fill(N, 0.0);
        for (int k = 0; k < N / 2; k++) {
            double kth = -2 * k * Math.PI / N;
            double coskth = Math.cos(kth);
            double sinkth = Math.sin(kth);

            rey.setDouble(k, q._1.getDouble(k) + coskth * r._1.getDouble(k) - sinkth * r._2.getDouble(k));
            imy.setDouble(k, q._2.getDouble(k) + coskth * r._2.getDouble(k) + sinkth * r._1.getDouble(k));

            rey.setDouble(k + N / 2, q._1.getDouble(k) - coskth * r._1.getDouble(k) + sinkth * r._2.getDouble(k));
            imy.setDouble(k + N / 2, q._2.getDouble(k) - coskth * r._2.getDouble(k) - sinkth * r._1.getDouble(k));
        }
        return Pair.from(rey, imy);
    }


    // compute the inverse FFT of x[], assuming its length is a power of 2
    public static Pair<Var, Var> ifft(Pair<Var, Var> x) {
        int N = x._1.rowCount();

        Var im2 = VarDouble.from(N, row -> -x._2.getDouble(row));

        // compute forward FFT
        Pair<Var, Var> y = fft(Pair.from(x._1, im2));

        // take conjugate again and divide by N
        Var re3 = VarDouble.from(N, row -> y._1.getDouble(row) / N);
        Var im3 = VarDouble.from(N, row -> -y._2.getDouble(row) / N);
        return Pair.from(re3, im3);
    }

    // compute the circular convolution of x and y
    public static Pair<Var, Var> cconvolve(Pair<Var, Var> x, Pair<Var, Var> y) {

        int len = x._1.rowCount();

        // should probably pad x and y with 0s so that they have same length
        // and are powers of 2
        if ((x._2.rowCount() != len)) {
            throw new RuntimeException("Dimensions don't agree");
        }

        int N = x._1.rowCount();

        // compute FFT of each sequence
        Pair<Var, Var> a = fft(x);
        Pair<Var, Var> b = fft(y);

        // point-wise multiply
        Pair<Var, Var> c = Pair.from(VarDouble.fill(len, 0.0), VarDouble.fill(len, 0.0));
        for (int i = 0; i < N; i++) {
            c._1.setDouble(i, a._1.getDouble(i) * b._1.getDouble(i) - a._2.getDouble(i) * b._2.getDouble(i));
            c._2.setDouble(i, a._1.getDouble(i) * b._2.getDouble(i) + a._1.getDouble(i) * b._2.getDouble(i));
        }

        // compute inverse FFT
        return ifft(c);
    }


    // compute the linear convolution of x and y
    public static Pair<Var, Var> convolve(Pair<Var, Var> x, Pair<Var, Var> y) {
        Pair<Var, Var> a = Pair.from(x._1.solidCopy(), x._2.solidCopy());
        Pair<Var, Var> b = Pair.from(y._1.solidCopy(), y._2.solidCopy());

        for (int i = 0; i < x._1.rowCount(); i++) {
            a._1.addDouble(0.0);
            a._2.addDouble(0.0);
            b._1.addDouble(0.0);
            b._2.addDouble(0.0);
        }
        return cconvolve(a, b);
    }
}
