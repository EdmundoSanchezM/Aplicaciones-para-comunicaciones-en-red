
import static colors.colors.ANSI_BLUE;
import static colors.colors.ANSI_GREEN;
import static colors.colors.ANSI_RESET;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import static javax.swing.JOptionPane.showMessageDialog;


public class ClienteUnicast extends Thread{
    
    String local = "src\\descargas";
    
    softwareDownload frameSoftware;
    private final database db;
    Socket cl;
    
    public ClienteUnicast(database db, softwareDownload frameSoftware){
        this.frameSoftware = frameSoftware;
        this.db = db;
        System.out.print( ANSI_BLUE + "[ Creado ] "+ANSI_RESET+" Cliente Unicast Creado. ");
    }
    
    public void run(){
        System.out.println( ANSI_GREEN + "[ Iniciado ] "+ANSI_RESET+" Cliente Unicast Iniciado");
    }
    
    public void connectWithServer() {
        //Nos conectamos con els ervidor local.
        try{
            cl = new Socket (db.getServerFileFound(), 1234); //socket bloquante
            System.out.println("[ Ok ] Conectado!");
        }catch(Exception e){
            showMessageDialog(null, "Ocurrio un problema con el servidor");
        }
            
    }
    
    public void downloadFile(){
        if(cl != null){
        try {
            System.out.println("[ Ok ] Pidiendo archivo: " + db.getFileFound().getPath().toString());
            DataOutputStream dos = new DataOutputStream (cl.getOutputStream());
            //Enviando el archvio
            dos.writeUTF(db.getFileFound().getPath().toString()); //Enviamos el file que queremos
            dos.flush();
            //Ya enviado, esperamos la respuesta
            DataInputStream dis = new DataInputStream(cl.getInputStream());
            //Recibimos el file.
            String nombre = (String) dis.readUTF();
            long tam = (long) dis.readLong();
            DataOutputStream dosFile = new DataOutputStream (new FileOutputStream(local+"/"+nombre));
            byte [] b = new byte [1500];
            long recibidos = 0;
            int porciento_recibido = 0, n=0;
            while (recibidos < tam){
                n = dis.read(b);
                dosFile.write(b, 0, n);
                dosFile.flush();
                recibidos +=n;
                porciento_recibido = (int)((recibidos*100)/tam);
                System.out.println("[ Ok ] Recibido el " + porciento_recibido + "%");
                frameSoftware.setProgressBar(porciento_recibido);
            }
            dos.close();
            dosFile.close();
            dis.close();
            cl.close();  
            System.out.println("[ Ok ] Archivo recibido");
            showMessageDialog(null, "Archivo recibido con exito");

        } catch (IOException ex) {
            showMessageDialog(null, "Ocurrio un problema al subir el archvio");
        }
    }
    }
    
}
