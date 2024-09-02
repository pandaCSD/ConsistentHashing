import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class Main {
    public static void main(String[] args) {
        // 启动RMI注册表
        try {
            LocateRegistry.createRegistry(1099);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

        // 创建远程对象
        ConsistentHashingImpl remoteObject = null;
        try {
            remoteObject = new ConsistentHashingImpl();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

        // 将远程对象绑定到RMI注册表
        try {
            Naming.rebind("rmi://localhost:1099/ConsistentHashing", remoteObject);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
