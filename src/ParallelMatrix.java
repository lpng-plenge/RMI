
import java.util.Arrays;

public class ParallelMatrix {

    private double[][] answ;
    private double[][] A, B;
    private int dividirArriba, dividirAbajo;
    
    public ParallelMatrix(double[][] matrixA, double[][] matrixB, int dividirArriba, int dividirAbajo) {
        this.A = new double[matrixA.length][matrixA[0].length];
        this.B = new double[matrixB.length][matrixB[0].length];
        this.answ = new double[matrixA.length][matrixB[0].length];
        this.A = matrixA.clone();
        this.B = matrixB.clone();
        this.dividirArriba = dividirArriba;
        this.dividirAbajo = dividirAbajo;
        
    }

    public void setMultiConcurrencia() {
        MatrixMultiConcurrency forkMulti = new MatrixMultiConcurrency();
        forkMulti.matrixMulti(this.A, this.B, this.dividirArriba, this.dividirAbajo);
        this.answ = forkMulti.getResultados().clone();
    }
    
    public void printParalelo() {
        for (double[] matrixAnsw : this.answ) {
            System.out.println(Arrays.toString(matrixAnsw));
        }
    }
    public void  setAddMatrixConcurrencia(){
        MatrixAddConcurrency forkJoin = new MatrixAddConcurrency();
        forkJoin.matrixAdd(this.A, this.B, 100, dividirArriba, dividirAbajo);
        this.answ = forkJoin.getResultados();
    }
    
    public double[][] getResultado() {
        return this.answ;
    }

}
