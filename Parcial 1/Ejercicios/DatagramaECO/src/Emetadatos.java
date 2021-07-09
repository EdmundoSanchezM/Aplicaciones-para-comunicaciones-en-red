import java.net.*;
import java.io.*;

/**
 *
 * @author axele
 */
public class Emetadatos {
    public static void main(String[] args){
        try{
            String mensaje="Un breve mensaje..";
            byte[] tmp = mensaje.getBytes();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            dos.writeInt(1);
            dos.writeInt(tmp.length);
            dos.write(tmp);
            dos.flush();
            byte[] b = baos.toByteArray();
            DatagramSocket cl = new DatagramSocket();
            InetAddress dir = InetAddress.getByName("127.0.0.1");
            DatagramPacket p = new DatagramPacket(b,b.length,dir,5555);
            cl.send(p);
            System.out.println("mensaje enviado..");
            dos.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
}
