import java.io.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.List;

public class Main {
    //Lê arquivo de config, registra no RMI e inicializa o nodo
    public static void main(String args[]) throws IOException, InterruptedException {
        if (args.length < 2) {
            System.out.println("Usage: java Main <arquivo> <linha>");
            System.exit(1);
        }

        File file = new File(args[0]);

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            System.out.println("Erro ao processar aquivo de configuração");
            e.printStackTrace();
        }

        List<Nodo> nodos = new ArrayList<>();

        String[] linhaNodo = br.lines().skip(Integer.parseInt(args[1]) - 1).findFirst().get().split(" ");
        Nodo nodo = new Nodo(linhaNodo[0], linhaNodo[1], linhaNodo[2]);

        try {
            br = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            System.out.println("Erro ao processar aquivo de configuração");
            e.printStackTrace();
        }

        br.lines().forEach(linha -> {
            String[] linhaAux = linha.split(" ");
            if (linhaAux[0].equalsIgnoreCase(linhaNodo[0])) return;
            try {
                nodos.add(new Nodo(linhaAux[0], linhaAux[1], linhaAux[2]));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });

        try {
            //Deve se cuidar para definir o hostlocal, senão acaba ocorrendo erro de conexão ao tentar interagir com o arquivo
            System.setProperty("java.rmi.server.hostname", nodo.address);
            LocateRegistry.createRegistry(1099);
            System.out.println("java RMI registry created.");
        } catch (RemoteException e) {
            System.out.println("java RMI registry already exists.");
        }

        Nodo.inicia(nodo.ID, nodo.address, nodo.port, nodos);

    }
}
