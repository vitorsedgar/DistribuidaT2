import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Nodo extends UnicastRemoteObject implements NodoInterface{
    private int ID;
    private int port;
    private String address;

    public Nodo(int ID, int port, String address) throws RemoteException{
        this.ID = ID;
        this.port = port;
        this.address = address;
    }
}
