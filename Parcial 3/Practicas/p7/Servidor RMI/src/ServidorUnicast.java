
import static colors.colors.ANSI_GREEN;
import static colors.colors.ANSI_RESET;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServidorUnicast extends Thread{
    
    int puerto = 1234;
    ServerSocket s;
    Socket cl;
    
    public void run(){
       startService();
       waitForFile();   
    }
     
    
    public static void main(String[] args) {
        try{
	    ServidorUnicast servidorU = new ServidorUnicast();
	    servidorU.start();
	}catch(Exception e){
            e.printStackTrace();
        }
    }

    private void startService() {
        try{
            s = new ServerSocket(puerto);
            s.setReuseAddress(true);
            System.out.print( ANSI_GREEN + "[Ok] "+ANSI_RESET+" Servidor Unicast Iniciado. ");
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private void waitForFile() {
        for(;;){
            try {
                cl = s.accept();
                System.out.println("[ Ok ] Cliente conectado desde: "+ cl.getInetAddress()+":"+ cl.getPort());
                
                //Leemos la entrada
                DataInputStream dis = new DataInputStream(cl.getInputStream());
                String nombre = (String) dis.readUTF();
                
                System.out.println("[ Ok ] Transfieriendo archivo..."); 
                File f = new File(nombre);
                String name = f.getName();
                long tam = f.length();
                String path = f.getAbsolutePath();
                System.out.println("[ Ok ] Enviando archivo: " + name + " que mide " + tam + " bytes\n");
                DataOutputStream dos = new DataOutputStream (cl.getOutputStream());
                DataInputStream disFile = new DataInputStream(new FileInputStream(path));
                //Enviando el archvio
                dos.writeUTF(name); //Envia el nombre
                dos.flush();
                dos.writeLong(tam); //Envia el tamaño
                byte[] b =new byte[1500];
                long enviados = 0;
                int porciento = 0, n=0;
                while(enviados < tam){
                    n = disFile.read(b);
                    dos.write(b, 0, n);
                    dos.flush(); //se envian
                    enviados+=n;
                    porciento = (int)((enviados*100)/tam);
                    System.out.println("[ Enviado ] Trasmitido el " + porciento + "%");
                }
                disFile.close();
                dos.close();
                cl.close();
                System.out.println("[ Ok ] Archvio enviado con exito");

                dis.close();
                cl.close(); 
                
            } catch (IOException ex) {
                Logger.getLogger(ServidorUnicast.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
