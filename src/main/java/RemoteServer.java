import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RemoteServer extends Remote{
    // 在服务器中增加一个键值对
    String addEntry(Entry entry) throws RemoteException;

    // 通过key在服务器中删除一个键值对
    String removeEntryByKey(String key) throws RemoteException;

    // 通过key从服务器获取一个键值对
    Entry getEntryByKey(String key) throws RemoteException;

    // 获取所有key的哈希值小于等于hash的键值对
    List<Entry> getEntriesByHash(int hash) throws RemoteException;

    // 获取服务器上所有的键值对
    List<Entry> getAllEntries() throws RemoteException;
}
