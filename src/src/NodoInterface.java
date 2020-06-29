import java.rmi.Remote;

public interface NodoInterface extends Remote {

    public void mensagemNovoCoordenador(NodoInterface nodo);

    public boolean mensagemEleicao();

    public boolean mensagemCoordenador();

    public void mensagemConfirmaNodo();
}
