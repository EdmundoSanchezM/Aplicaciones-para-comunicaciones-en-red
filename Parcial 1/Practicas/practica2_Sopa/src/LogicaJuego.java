
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import sun.net.idn.Punycode;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author memo0p2
 */
public class LogicaJuego implements Serializable{
    private char[][] sopa;
    private String[] palabras;
    private List<String> encontradas = new ArrayList<String>();
    LogicaJuego(char[][] sopa,String[] palabras) {
        Random r = new Random();
        String alfabeto="abcdefghijklmnopqrstuvwxyz";
        int num;
        this.sopa=sopa;
        this.palabras=palabras;
        for (int i = 0; i < this.sopa.length; i++) {
            for (int j = 0; j < this.sopa[i].length; j++) {
                if(this.sopa[i][j]=='.'){
                    num = r.nextInt(25) + 1;
                    this.sopa[i][j]=alfabeto.charAt(num);
                }
            }
        }
    }
    public double  jugar(){
        long inicio = System.currentTimeMillis();
        long fin;
        double tiempo;
        int[] cordenadas={0,0,0,0};
        String aux1,aux2;
        String[] aux;
        String enc;
        Scanner escaneo = new Scanner(System.in);
        while(palabras.length!=encontradas.size()){
            mostrar();
            System.out.println("Ingrese las cordenada 1");
            aux1=escaneo.nextLine();
            System.out.println("Ingrese las cordenada 2");
            aux2=escaneo.nextLine();
            aux=aux1.split(",");
            cordenadas[0]=Integer.parseInt(aux[0]);
            cordenadas[1]=Integer.parseInt(aux[1]);
            aux=aux2.split(",");
            cordenadas[2]=Integer.parseInt(aux[0]);
            cordenadas[3]=Integer.parseInt(aux[1]);
            enc=encontrar(cordenadas);
            System.out.println(enc);
            for(int i=0;i<palabras.length;i++){
                if(palabras[i].equals(enc)){
                    marcar(cordenadas);
                    encontradas.add(enc);
                }
            }
        }
        System.out.println("Palabras por encontrar: "+Arrays.toString(palabras));
        System.out.println("Palabras encontradas: "+encontradas);
        System.out.println("Felicidades, has ganado");
        fin = System.currentTimeMillis();
        tiempo = (double) ((fin - inicio)/1000);
        System.out.println("Su puntaje es: "+ tiempo);
        return tiempo;
    }
    public void marcar(int[] cordenadas){
        char letra;
        if(cordenadas[0]==cordenadas[2]&&cordenadas[1]<cordenadas[3]){//derecha
            for(int i=0;i<cordenadas[3]-cordenadas[1]+1;i++){
                letra=sopa[cordenadas[0]][cordenadas[1]+i];
                if(letra>96&&letra<123){
                    sopa[cordenadas[0]][cordenadas[1]+i]=(char) (letra-32);
                }
            }
        }else if(cordenadas[1]==cordenadas[3]&&cordenadas[0]<cordenadas[2]){//abajo
            for(int i=0;i<cordenadas[2]-cordenadas[0]+1;i++){
                letra=sopa[cordenadas[0]+i][cordenadas[1]];
                if(letra>96&&letra<123){
                    sopa[cordenadas[0]+i][cordenadas[1]]=(char) (letra-32);
                }
            }
        }else if(cordenadas[0]==cordenadas[2]&&cordenadas[3]<cordenadas[1]){//izquierda
            for(int i=0;i<cordenadas[1]-cordenadas[3]+1;i++){
                letra=sopa[cordenadas[0]][cordenadas[1]-i];
                if(letra>96&&letra<123){
                    sopa[cordenadas[0]][cordenadas[1]-i]=letra=(char) (letra-32);
                }
            }
        }else if(cordenadas[1]==cordenadas[3]&&cordenadas[2]<cordenadas[0]){//arriba
            for(int i=0;i<cordenadas[0]-cordenadas[2]+1;i++){
                letra=sopa[cordenadas[0]-i][cordenadas[1]];
                if(letra>96&&letra<123){
                    sopa[cordenadas[0]-i][cordenadas[1]]=(char) (letra-32);
                }
            }
        }
    }
    public String encontrar(int[] cordenadas){
        String aux;
        char letra;
        List<String> letters = new ArrayList<String>();
        if(cordenadas[0]==cordenadas[2]&&cordenadas[1]<cordenadas[3]){//derecha
            for(int i=0;i<cordenadas[3]-cordenadas[1]+1;i++){
                letra=sopa[cordenadas[0]][cordenadas[1]+i];
                letters.add(letra+"");
            }
        }else if(cordenadas[1]==cordenadas[3]&&cordenadas[0]<cordenadas[2]){//abajo
            for(int i=0;i<cordenadas[2]-cordenadas[0]+1;i++){
                letra=sopa[cordenadas[0]+i][cordenadas[1]];
                letters.add(letra+"");
            }
        }else if(cordenadas[0]==cordenadas[2]&&cordenadas[3]<cordenadas[1]){//izquierda
            for(int i=0;i<cordenadas[1]-cordenadas[3]+1;i++){
                letra=sopa[cordenadas[0]][cordenadas[1]-i];
                letters.add(letra+"");
            }
        }else if(cordenadas[1]==cordenadas[3]&&cordenadas[2]<cordenadas[0]){//arriba
            for(int i=0;i<cordenadas[0]-cordenadas[2]+1;i++){
                letra=sopa[cordenadas[0]-i][cordenadas[1]];
                letters.add(letra+"");
            }
        }
        aux=Arrays.toString(letters.toArray());
        aux=aux.toLowerCase();
        aux=aux.replace("[","");
        aux=aux.replace("]","");
        aux=aux.replace(",","");
        aux=aux.replace(" ","");
        return aux;
    }
    public void mostrar() {
        System.out.println("Palabras por encontrar: "+Arrays.toString(palabras));
        System.out.println("Palabras encontradas: "+encontradas);
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
