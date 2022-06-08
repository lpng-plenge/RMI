package AlgoritmoConcurrencia;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class MatrixAddForkJoin2 {

    static double[][] answ;

    public void matrixAdd(double[][] A, double[][] B) {
        double[][] helper = new double[A.length][B[0].length];
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        forkJoinPool.invoke(new AddRow(A, B, A.length, A[0].length, B.length, B[0].length, 0, A.length - 1, helper));
        setResultados(helper);
    }

    public void setResultados(double[][] helper) {
        answ = new double[helper.length][helper[0].length];
        answ = helper.clone();
    }

    public double[][] getResultados() {
        return this.answ;
    }

    public class AddRow extends RecursiveAction {

        public double[][] A, B, help;
        private final int i1, i2;
        private final int j1, j2;
        private final int arriba;
        private final int abajo;

        AddRow(double[][] A, double[][] B, int i1, int j1, int i2, int j2, int arriba, int abajo, double[][] help) {
            this.A = A;
            this.B = B;
            this.help = help;
            this.i1 = i1;
            this.j1 = j1;
            this.i2 = i2;
            this.j2 = j2;
            this.arriba = arriba;
            this.abajo = abajo;
        }

        @Override
        protected void compute() {
            if (arriba == abajo) {
                AddCols op = new AddCols(A, B, i1, j1, i2, j2, 0, j2 - 1, arriba, help);
                invokeAll(op);
            } else {
                int mid = (int) (this.arriba + this.abajo) / 2;
                AddRow upMatrix = new AddRow(A, B, i1, j1, i2, j2, arriba, mid, help);
                AddRow downMatrix = new AddRow(A, B, i1, j1, i2, j2, mid + 1, abajo, help);
                invokeAll(upMatrix, downMatrix);
            }
        }

        class AddCols extends RecursiveAction {

            private final double[][] A, B, help;
            private final int i1, i2;
            private final int j1, j2;
            private final int izq, der;
            private final int current;

            AddCols(double[][] A, double[][] B, int i1, int j1, int i2, int j2, int izq, int der, int current, double[][] help) {
                this.A = A;
                this.B = B;
                this.help = help;
                this.i1 = i1;
                this.j1 = j1;
                this.i2 = i2;
                this.j2 = j2;
                this.izq = izq;
                this.der = der;
                this.current = current;
            }

            @Override
            protected void compute() {
                if (izq == der) {
                    help[izq][current] = A[current][current] + B[current][current];
                } else {
                    int mid = (this.izq + this.der) / 2;
                    AddCols leftMatrix = new AddCols(A, B, i1, j1, i2, j2, izq, mid, current, help);
                    AddCols rightMatrix = new AddCols(A, B, i1, j1, i2, j2, mid + 1, der, current, help);
                    invokeAll(leftMatrix, rightMatrix);
                }
            }
        }

    }
}
