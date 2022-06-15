
public class MatrixAddSecuencial {

    private double[][] answ;

    public MatrixAddSecuencial(double[][] A, double[][] B) {
        this.answ = new double[A.length][A[0].length];
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < A[0].length; j++) {
                answ[i][j] = A[i][j] + B[i][j];
            }
        }
    }

    public double[][] getResultados() {
        return this.answ;
    }
}
