package AlgoritmoConcurrencia;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class MatrixAddForkJoin {

    //No Modificar
    static int THRESHOLD;
    static double[][] A, B;
    static double[][] answ;

    public void matrixAdd(double[][] A, double[][] B, int T) {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        forkJoinPool.invoke(new MatrixSuma(A, B, T));
    }

    public double [][] getResultados() {
        return this.answ;
    }

    private class MatrixSuma extends RecursiveAction {

        private final int i1, i2;
        private final int j1, j2;

        MatrixSuma(double[][] A, double[][] B, int T) {
            this(0, A.length, 0, A[0].length);

            MatrixAddForkJoin.THRESHOLD = T;
            MatrixAddForkJoin.A = A;
            MatrixAddForkJoin.B = B;
            MatrixAddForkJoin.answ = new double[A.length][A[0].length];
        }

        MatrixSuma(int i1, int i2, int j1, int j2) {
            this.i1 = i1;
            this.i2 = i2;
            this.j1 = j1;
            this.j2 = j2;
        }

        @Override
        protected void compute() {
            if (i1 == i2) {
                return;
            } else if (i2 - i1 < THRESHOLD && j2 - j1 < THRESHOLD) {
                for (int i = i1; i < i2; i++) {
                    for (int j = j1; j < j2; j++) {
                        MatrixAddForkJoin.answ[i][j] = A[i][j] + B[i][j];
                    }
                }
            } else {//Dividir La Suma De Matriz En Tareas Mas Pequeñas
                int ihalf = (i2 + i1) / 2;
                int jhalf = (j2 + j1) / 2;
                invokeAll(
                        new MatrixSuma(i1, ihalf, j1, jhalf),
                        new MatrixSuma(i1, ihalf, jhalf, j2),
                        new MatrixSuma(ihalf, i2, j1, jhalf),
                        new MatrixSuma(ihalf, i2, jhalf, j2)
                );
            }
        }

    }
}
