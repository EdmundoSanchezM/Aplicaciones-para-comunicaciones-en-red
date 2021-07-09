
import java.io.*;

class Objeto implements Serializable {

    private String palabras;
    private char[][] sopa;

   
    public Objeto(String palabras) {
        this.palabras = palabras;
    }
    
    public Objeto(char[][] sopa) {
        this.sopa = sopa;
    }  
    public char[][] getSopa() {
        return sopa;
    }

    public void setSopa(char[][] sopa) {
        this.sopa = sopa;
    }
    public String getPalabras() {
        return palabras;
    }

    public void setPalabras(String palabras) {
        this.palabras = palabras;
    }
}
