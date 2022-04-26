import java.rmi.*;
import java.util.Scanner;

public class Client {
    public static void main(String args[]) {
        try {
            int count = 0;
            String user = "";
            String ID = "";
            String contra = "";
            boolean verificado = false;
            IntfChat client = null;
            Scanner scan = new Scanner(System.in);
            IntfChat server = (IntfChat)Naming.lookup("rmi://localhost:3000/test");
            System.out.print("Ingrese su nombre de usuario: ");
            user = scan.nextLine().trim();
            System.out.print("Ingrese su ID del banco: ");
            ID = scan.nextLine().trim();
            System.out.print("Ingrese su contraseña: ");
            contra = scan.nextLine().trim();
            client = new IntfChatRemote(user,ID,contra);
            while(count<2){
                //verificar si el usuario existe
                if(user.toLowerCase().equals("salir") || ID.toLowerCase().equals("salir")|| contra.toLowerCase().equals("salir")){
                    System.out.println("Saliendo...");
                    count = 3;
                }
                verificado = client.getVerified(user,ID,contra);
                System.out.println("verificado: "+verificado);
                
                if(verificado){
                    count = 3;
                }else{
                    count++;
                    System.out.println("Usuario o contraseña incorrectos\nIntentos restantes: "+ (3-count));
                    System.out.print("Ingrese su nombre de usuario: ");
                    user = scan.nextLine().trim();
                    System.out.print("Ingrese su ID del banco: ");
                    ID = scan.nextLine().trim();
                    System.out.print("Ingrese su contraseña: ");
                    contra = scan.nextLine().trim();
                    client = new IntfChatRemote(user,ID,contra);  
                }
                            

            }

            if(!verificado){
                System.out.println("Usuario no existe o contraseña incorrecta\n saliendo del programa...");
            }
            else{
                //pregunta que tipo de operacion decea realizar en un bucle infinito hasta que el usuario ingrese "salir"
                while (true){
                    
                    System.out.println("Ingrese n° de la operacion que decea realizar: ");
                    System.out.println("Operaciones disponibles: 1.Consultar 2.Retirar 3.Depositar 4.Salir");
                    String operacion = scan.nextLine().trim();
                    System.out.println("Numero Ingresado: "+operacion);
                    if (operacion.toLowerCase() == "salir" || operacion.toLowerCase() == "4"){
                        System.out.println("Saliendo...");
                        break;
                    } else if (operacion.equals("1")){
                        System.out.println("Consulta\n"+
                                            "Nombre: "+ user +"\n"+
                                            "Fondo: "+ client.getBalance(user)+"\n"+
                                            "Ulitmo mov"+"\n"+
                                            "          -Monto: "+client.getUltimoMovMont(user)+"\n"+
                                            "          -ID: "+client.getUltimoMovType(user)+"\n");

                    } else if (operacion.equals("2")){
                        while(true){
                            System.out.println("Ingrese montos a retirar: \nIngrese Atras si decea volver al menu");
                            String monto = scan.nextLine().trim();
                            if (monto.equals("atras".toLowerCase())){
                                break;
                            } else {
                                int mont = Integer.parseInt(monto);
                                String type = client.tipo(user);
                                if (type.equals("Individual")){
                                    System.out.println("Usuario de tipo: " + client.tipo(user));
                                    if (mont > 50000){
                                        System.out.println("Usted no puede realizar una transaccion de\nmonto mayor a $50.000\nIntente nuevamente con un monto inferior");
                                    } else {
                                        if((client.getBalance(user) - mont) < -100000){
                                            System.out.println("Usted no puede tener una deuda mayor a $100.000\nIntente nuevamente con un monto menor");
                                        } else{
                                            System.out.println("Retiro realizado con exito\n"+
                                                            "Fondo actual: $"+ client.retiro(user,mont));
                                }}} else if(type.equals("Empresa")){
                                    if (mont > 500000){
                                        System.out.println("Usted no puede realizar una transaccion de\nmonto mayor a $500.000\nIntente nuevamente con un monto inferior");
                                    } else {
                                        if((client.getBalance(user) - mont) < -1000000){
                                            System.out.println("Usted no puede tener una deuda mayor a $1.000.000\nIntente nuevamente con un monto menor");
                                        }else{ 
                                            System.out.println("Retiro realizado con exito\n"+
                                                            "Fondo actual: $"+ client.retiro(user,mont));
                    }}}}}
                    }else if (operacion.equals("3")){
                        while(true){
                            System.out.println("Ingrese montos a depositar: \nIngrese Atras si decea volver al menu");
                            String monto = scan.nextLine().trim();
                            if (monto.equals("atras".toLowerCase())){
                                break;
                            } else {
                                int mont = Integer.parseInt(monto);
                                String type = client.tipo(user);
                                if (type.equals("Individual")){
                                    System.out.println("Usuario de tipo: " + client.tipo(user));
                                    if (mont > 50000){
                                        System.out.println("Usted no puede realizar una transaccion de\nmonto mayor a $50.000\nIntente nuevamente con un monto inferior");
                                    } else {
                                        if((client.getBalance(user) + mont) > 500000){
                                            System.out.println("Su cuenta no puede poseer mas de $500.000 en fondos\nIntente nuevamente con un monto menor");
                                        }else{
                                            System.out.println("Deposito realizado con exito\n"+
                                                            "Fondo actual: $"+ client.deposito(user,mont));
                                        }
                                    }
                                }else if(type.equals("Empresa")){
                                    if (mont > 500000){
                                        System.out.println("Usted no puede realizar una transaccion de\nmonto mayor a $500.000\nIntente nuevamente con un monto inferior");
                                    } else {
                                        if((client.getBalance(user) + mont) > 6500000){
                                            System.out.println("Su cuenta no puede poseer mas de $6.500.000 en fondos\nIntente nuevamente con un monto menor");
                                        }else{
                                            System.out.println("Deposito realizado con exito\n"+
                                                            "Fondo actual: $"+ client.deposito(user,mont));
                                        }
                                    }
                                }
                            }
                        }
                    } else if(operacion.equals("4") || operacion.toLowerCase().equals("salir")){
                        System.out.println("Saliendo...");
                        break;
                    } else {
                        System.out.println("Operacion no valida\nIntente nuevamente");
                    }
                }
            }
            scan.close();
            System.exit(0);
        } catch (Exception e) {
            System.out.println("Fallo de servidor: "+e);
        }
    }
}
