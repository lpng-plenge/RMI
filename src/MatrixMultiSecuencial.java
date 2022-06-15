
public class MatrixMultiSecuencial {

    private double[][] answ;

    public MatrixMultiSecuencial(double[][] A, double[][] B) {
        this.answ = new double[A.length][A[0].length];
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < A[0].length; j++) {
                for (int k = 0; k < A.length; k++) {
                    answ[i][j] += A[i][k] * B[k][j];
                }
            }
        }
    }

    public double[][] getResultados() {
        return this.answ;
    }
}
