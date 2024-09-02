import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Servr extends Remote{
    String addEntry(Entry entry) throws RemoteException;
    String removeEntryByKey(String key) throws RemoteException;
    Entry getEntryByKey(String key) throws RemoteException;
    List<Entry> getEntriesByHash(int hash) throws RemoteException;
    List<Entry> getAllEntries() throws RemoteException;
}
