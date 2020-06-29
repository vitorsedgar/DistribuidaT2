import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class RMIStart {

    public static void main(String args[]) {
        try {
            //Deve se cuidar para definir o hostlocal, senão acaba ocorrendo erro de conexão ao tentar interagir com o arquivo
            System.setProperty("java.rmi.server.hostname", "192.168.100.38");
            LocateRegistry.createRegistry(1099);
            System.out.println("java RMI registry created.");
        } catch (RemoteException e) {
            System.out.println("java RMI registry already exists.");
        }
        while (true) {

        }
    }

}
