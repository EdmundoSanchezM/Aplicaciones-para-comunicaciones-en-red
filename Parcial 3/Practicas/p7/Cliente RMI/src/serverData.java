


import java.io.Serializable;


public class serverData implements Serializable{
    String address;
    int port;
    int temp;
    
    public serverData(String address, int port, int temp) {
        this.address = address;
        this.port = port;
        this.temp = temp;
    }
    
    public serverData() {}

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    
}
