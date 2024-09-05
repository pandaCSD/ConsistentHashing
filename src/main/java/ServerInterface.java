import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote {
    // 将指定IP和端口的服务器加入集群
    void addNode(String IP, Integer PORT) throws RemoteException;

    // 将指定IP和端口的服务器从集群中移除
    void removeNode(String IP, Integer PORT) throws RemoteException;
}
