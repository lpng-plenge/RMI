package AlgoritmosSecuenciales;

import java.util.Arrays;

public class MatrixMulti {

    private double[][] answ;

    public MatrixMulti(double[][] A, double[][] B) {
        this.answ = new double[A.length][A[0].length];
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < A[0].length; j++) {
                for (int k = 0;  k < A.length; k++) {
                    answ[i][j] += A[i][k] * B[k][j];
                }
            }
        }
    }

    public double [][] getResultados() {
        return this.answ;
    }
}
