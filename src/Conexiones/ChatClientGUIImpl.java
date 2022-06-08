package Conexiones;


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
        this.chatArea.append("Message from: "+from+ ": "+message+"\r\n");
    }

    @Override
    public void showMatrix(double[][] result) throws RemoteException {
        
    }

}
