
import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Cliente_O {

    public static void main(String[] args) {
        int vidas = 4;
        Scanner escaneo = new Scanner(System.in);
        try {
            Socket cl = new Socket("localhost", 3000);
            System.out.println("Conexion con servidor exitosa..");
            ObjectOutputStream oos = new ObjectOutputStream(cl.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(cl.getInputStream());
            LogicaJuego logicaJuego = new LogicaJuego();

            System.out.println("Seleccione la dificultad \n 1)Facil\n 2)Medio\n 3)Dificil");
            int nivel = escaneo.nextInt();
            Objeto ob = new Objeto(nivel + "");
            oos.writeObject(ob);
            oos.flush();
            Objeto ob2 = (Objeto) ois.readObject();
            System.out.println("Palabra recibida desde " + cl.getInetAddress() + ":" + cl.getPort());

            String palabraObjetivo = ob2.getPalabras();
            String lineas = logicaJuego.palabratoGuion(palabraObjetivo);
            char[] temp = lineas.toCharArray();
            char[] temp1;
            char caracter = ' ';
            int gano = 0;
            int aux = 0;
            logicaJuego.escribirPalabraEspaciada(temp);
            System.out.println();
            escaneo.nextLine();
            long startTime = System.currentTimeMillis();
            while (vidas > 0) {
                if (lineas.equals(palabraObjetivo)) {
                    gano = 1;
                    break;
                }

                String leerCaracter;
                System.out.println("Ingrese su char");
                leerCaracter = escaneo.nextLine();
                caracter = leerCaracter.charAt(0);
                String pilaCaracterUsado = logicaJuego.getPilaUsado();
                if (pilaCaracterUsado.length() > 0) {
                    while (pilaCaracterUsado.indexOf(caracter) != -1) {
                        System.out.println("A ingresado un caracter que ya se ha usado, ingrese otro diferente");
                        System.out.print("Letras usadas: ");
                        logicaJuego.escribirPalabraEspaciada(pilaCaracterUsado.toCharArray());
                        System.out.println("Ingrese su char");
                        leerCaracter = escaneo.nextLine();
                        caracter = leerCaracter.charAt(0);
                    }
                }
                logicaJuego.setPilaUsado(pilaCaracterUsado + caracter);
                aux = 0;
                temp = lineas.toCharArray();
                temp1 = ob2.getPalabras().toCharArray();
                for (int x = 0; x < ob2.getPalabras().length(); x++) {
                    if (temp1[x] == caracter) {
                        temp[x] = caracter;
                        aux++;
                    }
                }
                if (aux == 0) {
                    vidas--;
                }
                lineas = String.valueOf(temp);
                logicaJuego.escribirPalabraEspaciada(temp);
                System.out.println("\n******* Vidas restantes: " + vidas + " *******");
            }

            if (gano == 1) {
                System.out.println("Felicidades ha ganado");
                long endTime = System.currentTimeMillis() - startTime;
                ob = new Objeto(endTime + "");
                oos.writeObject(ob);
                oos.flush();
                System.out.println("Tiempo enviado..");
            } else {
                System.out.println("Usted ha perdido");
                 ob = new Objeto("Ha perdido");
                oos.writeObject(ob);
                oos.flush();
                System.out.println("Tiempo enviado..");
            }
            ois.close();
            oos.close();
            cl.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//main

}
