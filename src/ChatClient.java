


import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatClient extends Remote {

    //Obtener el cliente
    String getName() throws RemoteException;
    
    //el que se une
    void joined(String name) throws RemoteException;
    
    //los que se va
    void left(String name) throws RemoteException;
    
    //mostrar mensaje
    void showMessage(String from, String message) throws RemoteException;
    
    //mostrar matriz resultante
    void showMatrix(double[][] result) throws RemoteException;
            
}
