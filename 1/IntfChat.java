import java.rmi.*;

public interface IntfChat extends Remote{
    public boolean getVerified(String user, String ID, String contra) throws RemoteException;
    public int deposito(String user,int cantidad) throws RemoteException;
    public int retiro(String user,int cantidad) throws RemoteException;
    public String tipo(String user) throws RemoteException;
    public int getBalance(String user) throws RemoteException;
    public String getUltimoMovType(String user) throws RemoteException;
    public int getUltimoMovMont(String user) throws RemoteException;
}