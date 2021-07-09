
import static colors.colors.ANSI_GREEN;
import static colors.colors.ANSI_RESET;
import static colors.colors.ANSI_YELLOW;


public class initServidores extends Thread{

    ServidorMulticast ServidorMulticast = new ServidorMulticast();
    ServidorRMI ServidorRMI = new ServidorRMI();
    ServidorUnicast ServidorUnicast = new ServidorUnicast();
    
    public initServidores() {
        System.out.println( ANSI_GREEN + "[ Inicia ] "+ANSI_RESET+" initServidores Iniciando...");
        System.out.print( ANSI_YELLOW + "[ Info ] "+ANSI_RESET+" Iniciando Servidor Multicast. ");
        System.out.print( ANSI_YELLOW + "[ Info ] "+ANSI_RESET+" Iniciando Servidor RMI. ");
        System.out.println( ANSI_YELLOW + "[ Info ] "+ANSI_RESET+" Iniciando Servidor Unicast. ");
        ServidorMulticast.start();
        ServidorRMI.start();
        ServidorUnicast.start();
    }
    
    public static void main(String[] args) {
        try{
	    initServidores servidores = new initServidores();
	    servidores.start();
	}catch(Exception e){
            e.printStackTrace();
        }
    }
    
    
}
