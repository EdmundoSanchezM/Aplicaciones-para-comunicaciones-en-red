
import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Cliente_O {

    public static void main(String[] args) {
        int vidas = 4;
        String[] animales = {"oso", "abeja", "ballena", "perro", "caballo", "camello", "gato", "cerdo", "elefante", "foca","leon","tigre","lagartija","mono","lobo"};
        String[] frutas = {"uva", "lima", "limon", "cereza", "arandano", "platano", "manzana", "sandia", "fresa", "naranja"};
        String[] colores = {"rojo", "naranja", "verde", "negro", "blanco ", "cafe ", "amaraillo", "rosa ", "gris ", "azul"};
        String[] prueba={"memo","jose","sui"};
        String[] palabras={};
        try {
            Scanner escaneo = new Scanner(System.in);
            Socket cl = new Socket("localhost", 3000);
            System.out.println("Conexion con servidor exitosa..");
            ObjectOutputStream oos = new ObjectOutputStream(cl.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(cl.getInputStream());
            System.out.println("Seleccione la categoria \n 1)animales\n 2)frutas\n 3)colores\n 4)prueba");
            int nivel = escaneo.nextInt();
            Objeto ob = new Objeto(nivel + "");
            oos.writeObject(ob);
            oos.flush();
            Objeto ob2 = (Objeto) ois.readObject();
            char[][] sopa = ob2.getSopa();
            double puntaje;
            if (nivel == 1) {
                palabras = animales;
            } else if (nivel == 2) {
                palabras = frutas;
            } else if (nivel == 3) {
                palabras = colores;
            }  else if (nivel == 4) {
                palabras = prueba;
            }
            LogicaJuego juego = new LogicaJuego(sopa,palabras);
            puntaje=juego.jugar();
            Objeto ob3 = new Objeto(puntaje+"");
            oos.writeObject(ob3);
            oos.flush();
            ois.close();
            oos.close();
            cl.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
