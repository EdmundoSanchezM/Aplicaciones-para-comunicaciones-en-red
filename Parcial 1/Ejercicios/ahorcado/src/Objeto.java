
import java.io.*;

class Objeto implements Serializable {

    private String palabras;

    public Objeto(String palabras) {
        this.palabras = palabras;
    }
    
    public String getPalabras() {
        return palabras;
    }

    public void setPalabras(String palabras) {
        this.palabras = palabras;
    }
}
