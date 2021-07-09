/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author josue
 */
public class LogicaJuego {

    String pilaUsado = "";

    public String getPilaUsado() {
        return pilaUsado;
    }

    public void setPilaUsado(String pilaUsado) {
        this.pilaUsado = pilaUsado;
    }

    public String palabratoGuion(String palabra) {
        String palabraGuion = "";
        for (int x = 0; x < palabra.length(); x++) {
            palabraGuion = palabraGuion + "_";
        }
        return palabraGuion;
    }

    public void escribirPalabraEspaciada(char[] palabra) {
        for (int x = 0; x < palabra.length; x++) {
            System.out.print(palabra[x] + " ");
        }
    }

}
