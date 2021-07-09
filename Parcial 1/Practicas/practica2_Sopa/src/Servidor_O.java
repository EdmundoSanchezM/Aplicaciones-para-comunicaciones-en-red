
import java.net.*;
import java.io.*;
import java.util.Random;

public class Servidor_O {

    public static void main(String[] args) throws Exception {

        try {
            ServerSocket ss = new ServerSocket(3000);
            System.out.println("Servidor iniciado");
            for (;;) {
                String puntaje;
                String categoria="";
                Socket cl = ss.accept();
                ObjectOutputStream oos = new ObjectOutputStream(cl.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(cl.getInputStream());
                Objeto ob = (Objeto) ois.readObject();
                int nivel = Integer.parseInt(ob.getPalabras());
                if (nivel == 1) {
                    categoria = "animales";
                } else if (nivel == 2) {
                    categoria = "frutas";
                } else if (nivel == 3) {
                    categoria = "colores";
                } else if (nivel == 4) {
                    categoria = "prueba";
                }
                System.out.println(categoria);
                Sopa sopita = new Sopa(nivel);
                sopita.cargar();
                sopita.mostrar();
                Sopa copia = sopita;
                Objeto ob2 = new Objeto(sopita.getSopa());
                oos.writeObject(ob2);
                oos.flush();
                Objeto ob3 = (Objeto) ois.readObject();
                //recibiendo
                puntaje = ob3.getPalabras();
                ois.close();
                oos.close();
                cl.close();
                //Archivo

                BufferedWriter bw = null;
                FileWriter fw = null;
                File file = new File("Puntaciones.txt");
                if (!file.exists()) {
                    file.createNewFile();
                }
                fw = new FileWriter(file.getAbsoluteFile(), true);
                bw = new BufferedWriter(fw);
                String data = "--------\n" + "Direccion: " + cl.getInetAddress() + " En el puero"+cl.getPort()+"   categoria: " + categoria + "    puntuacion:    " + puntaje + "\n";
                bw.write(data);
                System.out.println("información agregada!");
                bw.close();
                fw.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
