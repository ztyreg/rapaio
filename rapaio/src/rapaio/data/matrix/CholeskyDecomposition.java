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
package rapaio.data.matrix;


/**
 * Cholesky Decomposition.
 * <p/>
 * For a symmetric, positive definite rapaio.data.matrix A, the Cholesky decomposition is an
 * lower triangular rapaio.data.matrix L so that A = L*L'.
 * <p/>
 * If the rapaio.data.matrix is not symmetric or positive definite, the constructor returns
 * a partial decomposition and sets an internal flag that may be queried by the
 * isSPD() method.
 */


public class CholeskyDecomposition implements java.io.Serializable {

    /*
	 * ------------------------ Class variables ------------------------
     */
	/**
	 * Array for internal storage of decomposition.
	 *
	 * @serial internal array storage.
	 */
	private double[][] L;

	/**
	 * Row and column dimension (square rapaio.data.matrix).
	 *
	 * @serial rapaio.data.matrix dimension.
	 */
	private int n;

	/**
	 * Symmetric and positive definite flag.
	 *
	 * @serial is symmetric and positive definite flag.
	 */
	private boolean isspd;

    /*
     * ------------------------ Constructor ------------------------
     */

	/**
	 * Cholesky algorithm for symmetric and positive definite rapaio.data.matrix. Structure
	 * to access L and isspd flag.
	 *
	 * @param Arg Square, symmetric rapaio.data.matrix.
	 */
//    public CholeskyDecomposition(Matrix Arg) {
//
//        // Initialize.
//        double[][] A = Arg.getArray();
//        n = Arg.getRows();
//        L = new double[n][n];
//        isspd = (Arg.getCols() == n);
//        // Main loop.
//        for (int j = 0; j < n; j++) {
//            double[] Lrowj = L[j];
//            double d = 0.0;
//            for (int k = 0; k < j; k++) {
//                double[] Lrowk = L[k];
//                double s = 0.0;
//                for (int i = 0; i < k; i++) {
//                    s += Lrowk[i] * Lrowj[i];
//                }
//                Lrowj[k] = s = (A[j][k] - s) / L[k][k];
//                d = d + s * s;
//                isspd = isspd & (A[k][j] == A[j][k]);
//            }
//            d = A[j][j] - d;
//            isspd = isspd & (d > 0.0);
//            L[j][j] = Math.sqrt(Math.max(d, 0.0));
//            for (int k = j + 1; k < n; k++) {
//                L[j][k] = 0.0;
//            }
//        }
//    }

    /*
     * ------------------------ Temporary, experimental code.
     * ------------------------ *\
     *
     * \** Right Triangular Cholesky Decomposition. <P> For a symmetric,
     * positive definite rapaio.data.matrix A, the Right Cholesky decomposition is an upper
     * triangular rapaio.data.matrix R so that A = R'*R. This constructor computes R with
     * the Fortran inspired column oriented algorithm used in LINPACK and
     * MATLAB. In Java, we suspect a row oriented, lower triangular
     * decomposition is faster. We have temporarily included this constructor
     * here until timing experiments confirm this suspicion. \
     *
     * \** Array for internal storage of right triangular decomposition. **\
     * private transient double[][] R;
     *
     * \** Cholesky algorithm for symmetric and positive definite rapaio.data.matrix. @param
     * A Square, symmetric rapaio.data.matrix. @param rightflag Actual getValue ignored.
     * @return Structure to access R and isspd flag. \
     *
     * public CholeskyDecomposition (Matrix Arg, int rightflag) { // Initialize.
     * double[][] A = Arg.getArray(); n = Arg.getColumnDimension(); R = new
     * double[n][n]; isspd = (Arg.getColumnDimension() == n); // Main loop. for
     * (int j = 0; j < n; j++) { double d = 0.0; for (int k = 0; k < j; k++) {
     * double s = A[k][j]; for (int i = 0; i < k; i++) { s = s -
     * R[i][k]*R[i][j]; } R[k][j] = s = s/R[k][k]; d = d + s*s; isspd = isspd &
     * (A[k][j] == A[j][k]); } d = A[j][j] - d; isspd = isspd & (d > 0.0);
     * R[j][j] = Math.sqrt(Math.max(d,0.0)); for (int k = j+1; k < n; k++) {
     * R[k][j] = 0.0; } } }
     *
     * \** Return upper triangular factor. @return R \
     *
     * public Matrix getR () { return new Matrix(R,n,n); }
     *
     * \* ------------------------ End of temporary code.
     * ------------------------
     */

    /*
     * ------------------------ Public Methods ------------------------
     */

	/**
	 * Is the rapaio.data.matrix symmetric and positive definite?
	 *
	 * @return true if A is symmetric and positive definite.
	 */
	public boolean isSPD() {
		return isspd;
	}

	/**
	 * Return triangular factor.
	 *
	 * @return L
	 */
//    public Matrix getL() {
//        return new Matrix(L, n, n);
//    }

//    /**
//     * Solve A*X = B
//     *
//     * @param B A Matrix with as many getRowCount as A and any number of columns.
//     * @return X so that L*L'*X = B
//     * @throws IllegalArgumentException Matrix row dimensions must agree.
//     * @throws RuntimeException         Matrix is not symmetric positive definite.
//     */
//    public Matrix solve(Matrix B) {
//        if (B.getRows() != n) {
//            throw new IllegalArgumentException("Matrix row dimensions must agree.");
//        }
//        if (!isspd) {
//            throw new RuntimeException("Matrix is not symmetric positive definite.");
//        }
//
//        // Copy right hand side.
//        double[][] X = B.getArrayCopy();
//        int nx = B.getCols();
//
//        // Solve L*Y = B;
//        for (int k = 0; k < n; k++) {
//            for (int j = 0; j < nx; j++) {
//                for (int i = 0; i < k; i++) {
//                    X[k][j] -= X[i][j] * L[k][i];
//                }
//                X[k][j] /= L[k][k];
//            }
//        }
//
//        // Solve L'*X = Y;
//        for (int k = n - 1; k >= 0; k--) {
//            for (int j = 0; j < nx; j++) {
//                for (int i = k + 1; i < n; i++) {
//                    X[k][j] -= X[i][j] * L[i][k];
//                }
//                X[k][j] /= L[k][k];
//            }
//        }
//
//        return new Matrix(X, n, nx);
//    }
}