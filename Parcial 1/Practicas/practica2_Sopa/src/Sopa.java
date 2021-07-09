
import java.io.Serializable;
import java.util.*;

public class Sopa implements Serializable {

    Scanner sc = new Scanner(System.in);
    private char[][] sopa;

    
    private String[] palabras;
    private int[] sitio = new int[]{-1, -1, -1};
    String[] animales = {"oso", "abeja", "ballena", "perro", "caballo", "camello", "gato", "cerdo", "elefante", "foca","leon","tigre","lagartija","mono","lobo"};
    String[] frutas = {"uva", "lima", "limon", "cereza", "arandano", "platano", "manzana", "sandia", "fresa", "naranja"};
    String[] colores = {"rojo", "naranja", "verde", "negro", "blanco ", "cafe ", "amaraillo", "rosa ", "gris ", "azul"};
    String[] prueba={"memo","jose","sui"};
    Sopa(int nivel) {
        this.sopa = new char[16][16];
        for (int i = 0; i < sopa.length; i++) {
            for (int j = 0; j < sopa[i].length; j++) {
                sopa[i][j] = '.';
            }
        }
        if (nivel == 1) {
            this.palabras = animales;
        } else if (nivel == 2) {
            this.palabras = frutas;
        } else if (nivel == 3) {
            this.palabras = colores;
        }else if (nivel == 4) {
            this.palabras = prueba;
        }
    }
    public char[][] getSopa() {
        return sopa;
    }

    public void setSopa(char[][] sopa) {
        this.sopa = sopa;
    }
    public void impPalabras(){
        System.out.println(Arrays.toString(palabras));
    }
    public void cargar() {
        Random r = new Random();
        int xran;
        int yran;
        int ban;

        for (int i = 0; i < palabras.length; i++) {
            ban = 0;
            String aux = palabras[i];

            while (ban == 0) {
                yran = r.nextInt(15) + 1;
                xran = r.nextInt(15) + 1;
                if (aux.length() < (sopa[0].length - xran)) {//derecha
                    if (cruza(yran, xran, aux, 1)) {
                        //System.out.println("aceptada1");
                        for (int h = 0; h < aux.length(); h++) {
                            sopa[yran][xran + h] = aux.charAt(h);
                        }
                        ban = 1;
                    }
                } else if (aux.length() < (sopa.length - yran)) {//abajo
                    if (cruza(yran, xran, aux, 2)) {
                        //System.out.println("aceptada2");
                        for (int h = 0; h < aux.length(); h++) {
                            sopa[yran + h][xran] = aux.charAt(h);
                        }
                        ban = 1;
                    }
                } else if ((sopa.length - yran) < aux.length()) {//izquierda
                    if (cruza(yran, xran, aux, 3)) {
                        //System.out.println("aceptada3");
                        for (int h = 0; h < aux.length(); h++) {
                            sopa[yran][xran - h] = aux.charAt(h);
                        }
                        ban = 1;
                    }
                } else if ((sopa.length - yran) < aux.length()) {//arriba
                    if (cruza(yran, xran, aux, 4)) {
                        //System.out.println("aceptada4");
                        for (int h = 0; h < aux.length(); h++) {
                            sopa[yran - h][xran] = aux.charAt(h);
                        }
                        ban = 1;
                    }
                }
            }
        }
    }

    public boolean cruza(int y, int x, String pal, int tipo) {
        boolean retorno = true;
        int aux = tipo;
        if (tipo == 1) {
            for (int i = 0; i < pal.length(); i++, x++) {
                if (sopa[y][x] == '.' || (sopa[y][x] == pal.charAt(i))) {
                    continue;
                } else {
                    retorno = false;
                    break;
                }
            }
        } else if (tipo == 2) {
            for (int i = 0; i < pal.length(); i++, y++) {
                if (sopa[y][x] == '.' || (sopa[y][x] == pal.charAt(i))) {
                    continue;
                } else {
                    retorno = false;
                    break;
                }
            }
        } else if (tipo == 3) {
            for (int i = 0; i < pal.length(); i++, x--) {
                if (sopa[y][x] == '.' || (sopa[y][x] == pal.charAt(i))) {
                    continue;
                } else {
                    retorno = false;
                    break;
                }
            }
        } else if (tipo == 4) {
            for (int i = 0; i < pal.length(); i++, y--) {
                if (sopa[y][x] == '.' || (sopa[y][x] == pal.charAt(i))) {
                    continue;
                } else {
                    retorno = false;
                    break;
                }
            }
        }
        return retorno;
    }

    public void mostrar() {
        for (int i = 0; i < sopa.length; i++) {
            if (i < 10) {
                System.out.print("0" + i + "|");
            } else {
                System.out.print(i + "|");
            }
            for (int j = 0; j < sopa[0].length; j++) {
                if (j != sopa[0].length) {
                    System.out.print(" " + sopa[i][j]);
                }
            }
            if (i < 10) {
                System.out.println("|" + "0" + i);
            } else {
                System.out.println("|" + i);
            }
        }
    }
}
