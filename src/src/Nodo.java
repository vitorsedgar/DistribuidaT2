import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Nodo extends UnicastRemoteObject implements NodoInterface{
    public String ID;
    public String port;
    public String address;

    private List<Nodo> nodos;
    private int nodosProntos;
    private Boolean inEleicao;

    private NodoInterface coordenador;

    public Nodo(String ID, String address, String port) throws RemoteException{
        this.ID = ID;
        this.address = address;
        this.port = port;
    }

    //Vê se é o cara de maior ID da lista se for inicia modo primeiro coordenador se não envia confirmaNodo para o coordenador e então inicia modo nodo
    public void inicia(List<Nodo> nodos){
        this.nodos = nodos;
        Nodo maiorID = getNodoMaiorID();
        inEleicao = Boolean.FALSE;

        try {
            //Registra no RMI Registry o objeto
            Naming.rebind("Nodo", this);
            System.out.println("Nodo is ready.");
        } catch (Exception e) {
            System.out.println("Nodo failed: " + e);
        }

        if(Integer.parseInt(maiorID.ID) < Integer.parseInt(this.ID)){
            primeiroCoordenador();
        }
    }

    private Nodo getNodoMaiorID() {
        List<Nodo> lista = this.nodos.stream().sorted(Comparator.comparing(Nodo::getID)).collect(Collectors.toList());
        Collections.reverse(lista);
        return lista.get(0);
    }

    //Espera receber "CHEGAY" de todos os outros nodos da lista nodosProntos = nodos.size() e então inicia modo coordenador
    public void primeiroCoordenador(){

    }

    //Recebe "CHEGAY" dos demais nodos, retorna ok e soma numero de nodos prontos
    public void mensagemConfirmaNodo(){

    }

    //Envia "CHEGAY" ao coordenador e inicia modo nodo
    public void confirmaNodo(){

    }

    //Conta 10 segundos e encerra programa
    public void coordenador(){

    }

    //Confirma menssagem dos nodos
    public boolean mensagemCoordenador(){

        return true;
    }

    //Envia mensagem ao coordenador a cada 3 segundos, se coordenador não responder inicia eleição
    public void nodo(){
        //Enviar mensagemCoordenador ao atual coordenador da rede
    }

    //Inicia eleição mandando mensagem de eleição pra todos nodos de ID maior que ele, se alguem responder desiste e espera mensagem de novo coordenador, se ninguem responder se declara o "MANDACHUVA avisa" geral e inicia modo coordenador
    public void iniciaEleicao(){
        inEleicao = Boolean.TRUE;
        //Envia msg para IDs maiores (Possivelmente Thread nova tem que testar)
        inEleicao = Boolean.FALSE;
    }

    //Recebe mensagem de eleição, responde e inicia propria eleição
    public boolean mensagemEleicao(){
        if(!inEleicao){
            iniciaEleicao();
        }
        return true;
    }

    //Avisa geral que é o novo "MANDACHUVA" e inicia modo coordenador
    public void notificaNovoCoordenador(){
        //Envia mensagemNovoCoordenador para todos nodos
    }

    //Recebe aviso de que tem um novo "MANDACHUVA" no pedaço e retoma modo nodo
    public void mensagemNovoCoordenador(NodoInterface nodo){
        //Recebe novo cordenador e seta em uma variavel?? para enviar msgs
        coordenador = nodo;
    }

    public String getID() {
        return ID;
    }
}
