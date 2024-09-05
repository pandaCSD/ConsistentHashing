import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Node {
    public final String IP;
    public final int PORT;
    public final RemoteServer remote;

    public Node(String IP, int PORT) throws MalformedURLException, NotBoundException, RemoteException {
        this.IP = IP;
        this.PORT = PORT;
        this.remote = (RemoteServer) Naming.lookup("rmi://"+IP+":"+PORT+"/RemoteServer");
    }
}