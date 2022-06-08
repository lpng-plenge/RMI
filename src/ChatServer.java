


import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatServer extends Remote {

    //Login
    String[] login(ChatClient client) throws RemoteException;

    //List Client
    String[] list() throws RemoteException;

    //Messages
    void sendMessage(String from, String to, String message) throws RemoteException;
    
    void sendMessage(String from, String message) throws RemoteException;

    //LogOut
    void logout(String name) throws RemoteException;
}
