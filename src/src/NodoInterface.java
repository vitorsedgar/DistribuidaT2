import java.rmi.Remote;

public interface NodoInterface extends Remote {

    public void mensagemNovoCoordenador();

    public boolean mensagemEleicao();

    public boolean mensagemCoordenador();

    public void mensagemConfirmaNodo();
}
