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

    private static volatile NodoInterface coordenador;

    protected Nodo(String ID, String address, String port) throws RemoteException {
        this.ID = ID;
        this.address = address;
        this.port = port;
    }

    //Vê se é o cara de maior ID da lista se for inicia modo primeiro coordenador se não envia confirmaNodo para o coordenador e então inicia modo nodo
    public static void inicia(String ID, String address, String port, List<Nodo> listaNodos) {
        nodos = listaNodos;
        Nodo maiorID = getNodoMaiorID();
        inEleicao = Boolean.FALSE;
        nodosProntos = new AtomicInteger(1);
        Nodo nodo = null;
        try {
            nodo = new Nodo(ID, address, port);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        try {
            //Registra no RMI Registry o objeto
            //Talvez tenha que mudar para usar ID ao invez de "Nodo" para caso no mesmo ip não se confunda
            Naming.rebind(nodo.ID, nodo);
            System.out.println("Nodo is ready.");
        } catch (Exception e) {
            System.out.println("Nodo failed: " + e);
        }

        if (Integer.parseInt(maiorID.ID) < Integer.parseInt(nodo.ID)) {
            nodo.primeiroCoordenador();
        } else {
            //Pega o objeto do coordenador no registro RMI
            String remoteHostName = maiorID.address;
            String connectLocation = "//" + remoteHostName + "/" + maiorID.ID;

            coordenador = null;
            try {
                //Conecta no host e busca seu objeto remoto no Registro RMI do Servidor
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

    //Espera receber "CHEGAY" de todos os outros nodos da lista nodosProntos = nodos.size() e então inicia modo coordenador
    public void primeiroCoordenador() {
        while (nodosProntos.intValue() < nodos.size()) {

        }
        this.coordenador();
    }

    //Recebe "CHEGAY" dos demais nodos, retorna ok e soma numero de nodos prontos
    public void mensagemConfirmaNodo() {
        System.out.println("nod confirmado");
        nodosProntos.getAndIncrement();
    }

    //Envia "CHEGAY" ao coordenador e inicia modo nodo
    public void confirmaNodo(NodoInterface coordenador) {
        try {
            System.out.println("env confirmanod");
            coordenador.mensagemConfirmaNodo();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        nodo();
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
        System.out.println("recebendoMSG");
        return true;
    }

    //Envia mensagem ao coordenador a cada 3 segundos, se coordenador não responder inicia eleição
    public void nodo() {
        //Enviar mensagemCoordenador ao atual coordenador da rede

        try {
            while (true) {
                coordenador.mensagemCoordenador();
                System.out.println("emvaindoMSG");
            }
        } catch (RemoteException e) {
            iniciaEleicao();
            e.printStackTrace();
        }
    }

    //Inicia eleição mandando mensagem de eleição pra todos nodos de ID maior que ele, se alguem responder desiste e espera mensagem de novo coordenador, se ninguem responder se declara o "MANDACHUVA avisa" geral e inicia modo coordenador
    public void iniciaEleicao() {
        inEleicao = Boolean.TRUE;
        Boolean eleito = Boolean.TRUE;
        //Envia msg para IDs maiores (Possivelmente Thread nova tem que testar)
        for (Nodo nodo : nodos) {
            if (Integer.parseInt(nodo.ID) > Integer.parseInt(this.ID)) {
                if (nodo.mensagemEleicao()) {
                    eleito = Boolean.FALSE;
                    break;
                }
            }
        }
        if (eleito) {
            notificaNovoCoordenador();
        }
        inEleicao = Boolean.FALSE;
    }

    //Recebe mensagem de eleição, responde e inicia propria eleição
    public boolean mensagemEleicao() {
        if (!inEleicao) {
            iniciaEleicao();
        }
        return true;
    }

    //Avisa geral que é o novo "MANDACHUVA" e inicia modo coordenador
    public void notificaNovoCoordenador() {
        //Envia mensagemNovoCoordenador para todos nodos
        nodos.forEach(nodo -> nodo.mensagemNovoCoordenador(this));
        coordenador();
    }

    //Recebe aviso de que tem um novo "MANDACHUVA" no pedaço e retoma modo nodo
    public void mensagemNovoCoordenador(NodoInterface nodo) {
        //Recebe novo cordenador e seta em uma variavel?? para enviar msgs
        coordenador = nodo;
        nodo();
    }

    public String getID() {
        return ID;
    }
}
