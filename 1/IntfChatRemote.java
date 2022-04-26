import java.rmi.*;
import java.rmi.server.*;
import java.io.*;

public class IntfChatRemote extends UnicastRemoteObject implements IntfChat {
    public String user;
    public String ID;
    public String contra;
    public String[] tabla;
    public String[] info;
    public int saldo;
    public String tipo;
    public int ultimoMov;
    public String IdUltimoMov;



    public IntfChatRemote(String user, String ID, String contra) throws RemoteException {
        this.user = user;
        this.ID = ID;
        this.contra = contra;
        String archCSV = "datos_banco.csv";
        //tabla tipo 0:nombre,1:tipo,2:ID,3:contra,4:saldo,5:monto ultimo mov,6:ultimo mov tipo
        tabla = lector(archCSV);
        info = getInfo(user);
        this.tipo = info[1];
        this.saldo = Integer.parseInt(info[4]);
        this.ultimoMov = Integer.parseInt(info[5]);
        this.IdUltimoMov = info[6];

    }
    
    public IntfChatRemote() throws RemoteException {
        this.user = "ADMIN";
    }

    public String[] lector(String archCSV) {
        String[] tabla = new String[6];
        try {
            BufferedReader br = new BufferedReader(new FileReader(archCSV));
            String linea;
            int i = 0;
            while ((linea = br.readLine()) != null) {
                tabla[i] = linea;
                i++;
            }
            
            br.close();
        } catch (Exception e) {
            System.out.println("Error al leer el archivo"+e);
        }
        return tabla;
    }

    public boolean getVerified(String user, String ID, String contra) throws RemoteException {
        boolean verified = false;
        String[] datos;
        for(int i = 0; i < tabla.length; i++){
            datos = tabla[i].split(",");
            if(datos[0].equals(user) && datos[2].equals(ID) && datos[3].equals(contra)){
                verified = true;
            }
                
        }
        return verified;
    }

    public String[] getInfo(String user) throws RemoteException{
        //devuelve la informacion del usuario en un arreglo
        //Funciona
        String[] info = new String[7];
        for(int i = 0; i < tabla.length; i++){
            if(tabla[i].split(",")[0].equals(user)){
                info = tabla[i].split(",");
            }
        }
        return info;

    }

    public int getBalance(String user) throws RemoteException{

        return this.saldo;
    }

    public String tipo(String user) throws RemoteException{
        return this.tipo;
    }
    public int retiro(String user, int monto) throws RemoteException{
        this.saldo -= monto;
        //se registra ultimo movimiento
        this.ultimoMov = monto;
        this.IdUltimoMov = "Cargo"; 
        return this.saldo;
    }

    public int deposito(String user, int monto) throws RemoteException{
        this.saldo += monto;
        //se registra ultimo movimiento
        this.ultimoMov = monto;
        this.IdUltimoMov = "Abono"; 
        return this.saldo;
    }

    public String getUltimoMovType(String user) throws RemoteException{
        return this.IdUltimoMov;
    }
    public int getUltimoMovMont(String user) throws RemoteException{
        return this.ultimoMov;
    }
}
