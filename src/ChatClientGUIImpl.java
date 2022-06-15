
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.awt.List;
import java.awt.TextArea;

public class ChatClientGUIImpl extends UnicastRemoteObject implements ChatClient {

    private String name;
    private List clientList;
    private TextArea chatArea;

    public ChatClientGUIImpl(String n, List l, TextArea ta) throws RemoteException {
        this.name = n;
        this.clientList = l;
        this.chatArea = ta;

    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void joined(String name) {
        this.clientList.add(name);
    }

    @Override
    public void left(String name) {
        this.clientList.remove(name);
    }

    @Override
    public void showMessage(String from, String message) {
        this.chatArea.append("Message from: " + from + ": " + message + "\r\n");
    }

    @Override
    public double[][] resolveMatrix(String from, double[][] matrixA, double[][] matrixB, String type, int dividirArriba, int dividirAbajo) throws RemoteException {
        this.chatArea.append("Matrix from: " + from + "\r\n");
        //agregar que parte realizara
        ParallelMatrix matrix = new ParallelMatrix(matrixA, matrixB, dividirArriba, dividirAbajo);

        if (type.equals("Multi")) {
            matrix.setMultiConcurrencia();
            //matrix.printParalelo();
        } else if (type.equals("Add")) {
            matrix.setAddMatrixConcurrencia();
            //matrix.printParalelo();
        }

        return matrix.getResultado();
    }
}
