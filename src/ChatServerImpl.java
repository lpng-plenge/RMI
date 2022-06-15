
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class ChatServerImpl extends UnicastRemoteObject implements ChatServer {

    private Map<String, ChatClient> clientMap = new HashMap<>();
    private double[][] answ;
    
    public ChatServerImpl() throws RemoteException {
    }

    @Override
    public String[] login(ChatClient client) throws RemoteException {

        String name = client.getName();

        if (clientMap.containsKey(name)) {
            throw new RuntimeException("name already in use");
        }

        String[] clientNames = list();
        clientMap.put(name, client);

        //we need to add the new user in the list
        for (String clientName : clientNames) {
            clientMap.get(clientName).joined(name);
        }

        return clientNames;
    }

    @Override
    public String[] list() {
        return clientMap.keySet().toArray(new String[clientMap.size()]);
    }

    @Override
    public double[][] getMatrix() throws RemoteException {
        return this.answ;
    }

    @Override
    public void sendMessage(String from, String to, String message) throws RemoteException {
        clientMap.get(to).showMessage(from, message);
    }

    @Override
    public void sendMessage(String from, String message) throws RemoteException {
        String[] clientNames = list();
        for (String clientName : clientNames) {
            sendMessage(from, clientName, message);
        }
    }

    @Override
    public void sendMatrix(String from, String to, double[][] matrixA, double[][] matrixB, String type, int dividirArriba, int dividirAbajo) throws RemoteException {
        answ = new double[matrixA.length][matrixB[0].length]; 
        answ=clientMap.get(to).resolveMatrix(from, matrixA, matrixB, type, dividirArriba, dividirAbajo).clone();
    }

    @Override
    public void sendMatrix(String from, double[][] matrixA, double[][] matrixB, String type, int dividirArriba, int dividirAbajo) throws RemoteException {
        String[] clientNames = list();
        for (String clientName : clientNames) {
            sendMatrix(from, clientName, matrixA, matrixB, type, dividirArriba, dividirAbajo);
        }
    }

    @Override
    public void logout(String name) throws RemoteException {
        clientMap.remove(name);
        String[] clientNames = list();
        for (String clientName : clientNames) {
            clientMap.get(clientName).left(name);
        }
    }

}
