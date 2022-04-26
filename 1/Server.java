import java.rmi.*;

public class Server {
    public static void main(String args[]) {
        try {
            IntfChat server = new IntfChatRemote();
            Naming.rebind("rmi://localhost:3000/test", server);
        } catch (Exception e) {
            System.out.println("Fallo de servidor: "+e);
        }
    }
}