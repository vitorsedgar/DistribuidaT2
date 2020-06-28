import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class Nodo extends UnicastRemoteObject implements NodoInterface{
    private int ID;
    private int port;
    private String address;

    private List<NodoInterface> nodos;
    private int nodosProntos;

    private NodoInterface coordenador;

    public Nodo(int ID, int port, String address) throws RemoteException{
        this.ID = ID;
        this.port = port;
        this.address = address;
    }

    //Vê se é o cara de maior ID da lista se for inicia modo primeiro coordenador se não envia mensagemConfirmaNodo para o coordenador e então inicia modo nodo
    public void inicia(){

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

    }

    //Inicia eleição mandando mensagem de eleição pra todos nodos de ID maior que ele, se alguem responder desiste e espera mensagem de novo coordenador, se ninguem responder se declara o "MANDACHUVA avisa" geral e inicia modo coordenador
    public void iniciaEleicao(){

    }

    //Recebe mensagem de eleição, responde e inicia propria eleição
    public boolean mensagemEleicao(){

        return true;
    }

    //Avisa geral que é o novo "MANDACHUVA" e inicia modo coordenador
    public void notificaNovoCoordenador(){

    }

    //Recebe aviso de que tem um novo "MANDACHUVA" no pedaço e retoma modo nodo
    public void mensagemNovoCoordenador(){

    }

}
