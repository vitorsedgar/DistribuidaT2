import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Nodo extends UnicastRemoteObject implements NodoInterface {
    public String ID;
    public String port;
    public String address;

    private static volatile List<Nodo> nodos;
    private static volatile AtomicInteger nodosProntos;
    private static volatile Boolean inEleicao;
    private static volatile Boolean eleito;

    private static volatile NodoInterface coordenador;

    protected Nodo(String ID, String address, String port) throws RemoteException {
        this.ID = ID;
        this.address = address;
        this.port = port;
    }

    //Se registra no RMI, verifica se é o nodo de maior ID, se for inicia modo primeiro coordenador se não envia confirmaNodo para o coordenador e então inicia modo nodo
    public static void inicia(String ID, String address, String port, List<Nodo> listaNodos) throws RemoteException, InterruptedException {
        nodos = listaNodos;
        Nodo maiorID = getNodoMaiorID();
        inEleicao = Boolean.FALSE;
        eleito = Boolean.FALSE;
        nodosProntos = new AtomicInteger(0);
        Nodo nodo = null;
        try {
            nodo = new Nodo(ID, address, port);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        try {
            Naming.rebind(nodo.ID, nodo);
            System.out.println("Nodo is ready.");
        } catch (Exception e) {
            System.out.println("Nodo failed: " + e);
        }

        if (Integer.parseInt(maiorID.ID) < Integer.parseInt(nodo.ID)) {
            nodo.primeiroCoordenador();
        } else {
            String remoteHostName = maiorID.address;
            String connectLocation = "//" + remoteHostName + "/" + maiorID.ID;

            coordenador = null;
            try {
                System.out.println("Conectando ao coordenador em : " + connectLocation);
                coordenador = (NodoInterface) Naming.lookup(connectLocation);
            } catch (Exception e) {
                System.out.println("Coordenador falhou: ");
                e.printStackTrace();
            }

            nodo.confirmaNodo(coordenador);
        }
    }

    private static Nodo getNodoMaiorID() {
        List<Nodo> lista = nodos.stream().sorted(Comparator.comparing(Nodo::getID)).collect(Collectors.toList());
        Collections.reverse(lista);
        return lista.get(0);
    }

    //Espera receber confirmação de todos os outros nodos para iniciar algoritmo
    public void primeiroCoordenador() {
        while (nodosProntos.intValue() < nodos.size()) {

        }
        System.out.println("Nodos confirmados, iniciando algoritmo...");
        this.coordenador();
    }

    //Recebe confirmação de nodos para iniciar algoritmo
    public void mensagemConfirmaNodo() {
        nodosProntos.getAndIncrement();
    }

    //Envia mensagemConfirmaNodo para primeiro coordenador, para iniciar algoritmo
    public void confirmaNodo(NodoInterface coordenador) throws RemoteException, InterruptedException {
        try {
            System.out.println("Confirmando nodo ao coordenador");
            coordenador.mensagemConfirmaNodo();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        this.nodo();
    }

    //Conta 10 segundos e encerra programa
    public void coordenador() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    //Confirma menssagem dos nodos
    public boolean mensagemCoordenador() {
        return true;
    }

    //Envia mensagem ao coordenador a cada 3 segundos, se coordenador não responder inicia eleição. Para quando eleito coordenador
    public void nodo() throws InterruptedException {
        while (!coordenador.equals(this)) {
            while (!inEleicao) {
                if (coordenador.equals(this)) break;
                try {
                    System.out.println("t " + coordenador.getID());
                    coordenador.mensagemCoordenador();
                    Thread.sleep(3000);
                } catch (RemoteException e) {
                    if (!inEleicao) {
                        this.iniciaEleicao();
                    }
                }
            }
        }
    }

    //Inicia eleição se não for coordenador e não tiver uma eleição ativa, envia mensagemEleicao para IDs maiores e notifica demais nodos caso eleito
    public void iniciaEleicao() {
        if (!inEleicao && !coordenador.equals(this)) {
            inEleicao = Boolean.TRUE;

            List<Nodo> nodosAux = nodos.stream().filter(nodo -> Integer.parseInt(nodo.ID) > Integer.parseInt(this.ID)).collect(Collectors.toList());
            StringBuilder sb = new StringBuilder();
            sb.append("e [ ");
            nodosAux.forEach(n -> sb.append(n.ID).append(" "));
            sb.append("]");

            System.out.println(sb.toString());

            eleito = Boolean.TRUE;
            //Envia mensagemEleicao para IDs maiores
            for (Nodo nodo : nodosAux) {
                String remoteHostName = nodo.address;
                String connectLocation = "//" + remoteHostName + "/" + nodo.ID;

                NodoInterface nodoInterface = null;
                try {
                    nodoInterface = (NodoInterface) Naming.lookup(connectLocation);
                    if (nodoInterface.mensagemEleicao()) {
                        eleito = Boolean.FALSE;
                        break;
                    }
                } catch (Exception e) {

                }
            }
            if (eleito) {
                coordenador = this;
                notificaNovoCoordenador();
            }
        }
    }

    //Recebe mensagem de eleição, responde e inicia propria eleição
    public boolean mensagemEleicao() throws RemoteException {
        new Thread(this::iniciaEleicao).start();
        return true;
    }

    //Envia mensagemNovoCoordenador para todos nodos e inicia modo coordenador
    public void notificaNovoCoordenador() {
        System.out.println("c " + this.ID);
        nodos.forEach(nodo -> {
            if (!nodo.ID.equals(this.ID)) {
                String remoteHostName = nodo.address;
                String connectLocation = "//" + remoteHostName + "/" + nodo.ID;
                NodoInterface nodoInterface = null;
                try {
                    nodoInterface = (NodoInterface) Naming.lookup(connectLocation);
                    nodoInterface.mensagemNovoCoordenador(this);
                } catch (Exception e) {

                }
            }
        });
        this.coordenador();
    }

    //Recebe novo coordenador e segue modo nodo
    public void mensagemNovoCoordenador(NodoInterface nodo) throws RemoteException {
        inEleicao = Boolean.TRUE;
        System.out.println("c " + nodo.getID());
        coordenador = nodo;
        inEleicao = Boolean.FALSE;
    }

    public String getID() {
        return ID;
    }
}
