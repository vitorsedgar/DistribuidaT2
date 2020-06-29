import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NodoInterface extends Remote {

    public void mensagemNovoCoordenador(NodoInterface nodo) throws RemoteException, InterruptedException;

    public boolean mensagemEleicao() throws RemoteException;

    public boolean mensagemCoordenador() throws RemoteException;

    public void mensagemConfirmaNodo() throws RemoteException;

    public String getID() throws RemoteException;
}
