
import java.net.*;
import java.io.*;
import java.util.Random;

public class Servidor_O {

    public static void main(String[] args) throws Exception {

        String[] facil = {"oso", "casa", "auto", "perro", "pluma", "agua", "gato", "fuego", "planta", "libro"};
        String[] medio = {"geografia", "electrostatica", "automovil", "amistad", "fantastico", "rinoceronte", "mexicano", "escultura", "plantacion", "caligrafia"};
        String[] dificil = {"la vida empieza cada cinco minutos ", "el poder de la imaginacion nos hace infinitos", "la libertad muere si no se usa",
            "no se puede encontrar la paz evitando la vida", "un buen viajante no tiene planes", "vivimos en un arcoiris de caos", "donde hay amor hay vida",
            "lo que no nos mata nos hace mas fuerte", "siempre parece imposible hasta que se hace", "la verdadera fuerza es delicada"};

        try {
            ServerSocket ss = new ServerSocket(3000);
            System.out.println("Servidor iniciado");
            for (;;) {
                Socket cl = ss.accept();
                ObjectOutputStream oos = new ObjectOutputStream(cl.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(cl.getInputStream());

                Objeto ob = (Objeto) ois.readObject();
                int nivel = Integer.parseInt(ob.getPalabras());
                System.out.println(nivel);
                String palabra = "";
                Random ran = new Random();
                int numr = 0 + ran.nextInt((9 - 0) + 1);
                if (nivel == 1) {
                    palabra = facil[numr];
                } else if (nivel == 2) {
                    palabra = medio[numr];
                } else if (nivel == 3) {
                    palabra = dificil[numr];
                }
                Objeto ob2 = new Objeto(palabra);
                oos.writeObject(ob2);
                oos.flush();
                System.out.println("Palabra enviada: " + palabra + "\n");
                //recibiendo
                Objeto ob3 = (Objeto) ois.readObject();
                System.out.println("Objeto recibido desde" + cl.getInetAddress() + ":" + cl.getPort() + " con los datos");
                System.out.println("Tiempo obtenido en el juego: " + ob3.getPalabras());
                ois.close();
                oos.close();
                cl.close();
                //Archivo
                if (!ob3.getPalabras().equals("Ha perdido")) {
                    BufferedWriter bw = null;
                    FileWriter fw = null;
                    File file = new File("Puntaciones.txt");
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    fw = new FileWriter(file.getAbsoluteFile(), true);
                    bw = new BufferedWriter(fw);
                    System.out.println(ob3.getPalabras());
                    int pun = Integer.parseInt(ob3.getPalabras()) / 1000;
                    String data = "--------\n" + "Direccion: " + cl.getInetAddress() + "   nivel: " + nivel + "    puntuacion:    " + pun + "\n";
                    bw.write(data);
                    System.out.println("información agregada!");
                    bw.close();
                    fw.close();
                }
            }//for   
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//main

}
