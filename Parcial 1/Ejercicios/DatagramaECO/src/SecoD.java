import java.net.*;
import java.io.*;
/**
 *
 * @author axele
 */
public class SecoD {
    public static void main(String[] args){
      try{  
          int pto=1234;
          String msj="";
          DatagramSocket s = new DatagramSocket(pto);
          s.setReuseAddress(true);
         // s.setBroadcast(true);
          System.out.println("Servidor iniciado... espedando datagramas..");
          for(;;){
              byte[] b = new byte[65535];
              DatagramPacket p = new DatagramPacket(b,b.length);
              s.receive(p);
              msj = new String(p.getData(),0,p.getLength());
              System.out.println("Se ha recibido datagrama desde "+p.getAddress()+":"+p.getPort()+" con el mensaje:"+msj.substring(1, msj.length())+ 
                      "\nNumero de paquete: "+msj.substring(0,1)+
                      "\nTamaño con el numero de paquete: "+msj.length() + ", sin el numero de paquete: "+(msj.length()-1));
              DatagramPacket nuevoP = new DatagramPacket(msj.substring(1, msj.length()).getBytes(),msj.length()-1,p.getAddress(),p.getPort());
              s.send(nuevoP);
          }//for
          
      }catch(Exception e){
          e.printStackTrace();
      }//catch
        
    }//main
}
