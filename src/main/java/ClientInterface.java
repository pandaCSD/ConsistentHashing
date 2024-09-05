import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientInterface extends Remote {
    // 在服务器增加一个键值对
    void addEntry(Entry entry) throws RemoteException;

    // 通过key在服务器中删除一个键值对
    void removeEntryByKey(String key) throws RemoteException;

    // 通过key从服务器获取一个键值对
    Entry getEntryByKey(String key) throws RemoteException;
}