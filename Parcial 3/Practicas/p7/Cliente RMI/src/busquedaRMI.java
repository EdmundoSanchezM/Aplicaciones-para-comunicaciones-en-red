

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface busquedaRMI extends Remote{
       searchResult buscar(String file) throws RemoteException;
}
