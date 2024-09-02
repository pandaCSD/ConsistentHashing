import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ConsistentHashingImpl extends UnicastRemoteObject implements ServerInterface, ClientInterface {
    ConcurrentSkipListMap<Integer, Node> nodeMap = new ConcurrentSkipListMap<>();
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    protected ConsistentHashingImpl() throws RemoteException {
        super();
    }

    private Integer hashNode(Node node) {
        String combined = node.IP + ":" + node.PORT;
        return combined.hashCode();
    }

    // 增加结点
    @Override
    public void addNode(String IP ,Integer PORT) throws RemoteException {
        rwLock.writeLock().lock();
        Node node = null;
        try {
            node = new Node(IP, PORT);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        }
        try {
            int hash = hashNode(node);
            nodeMap.put(hash, node);
            Node nextNode = getNode(hash);
            if (nextNode != null) {
                List<Entry> entries = nextNode.remote.getEntriesByHash(hash);
                for (Entry entry : entries) {
                    nextNode.remote.removeEntryByKey(entry.key);
                    node.remote.addEntry(entry);
                }
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    // 移除结点
    @Override
    public void removeNode(String IP, Integer PORT) throws RemoteException {
        rwLock.writeLock().lock();
        Node node = null;
        try {
            node = new Node(IP, PORT);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        }
        try {
            int hash = hashNode(node);
            Node nextNode = getNode(hash);
            if (nextNode != null) {
                List<Entry> entries = node.remote.getAllEntries();
                for (Entry entry : entries) {
                    nextNode.remote.addEntry(entry);
                }
            }
            nodeMap.remove(hash);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    // 获取结点
    private Node getNode(int hash) {
        if (nodeMap.isEmpty()) {
            return null;
        }
        Map.Entry<Integer, Node> entry = nodeMap.ceilingEntry(hash);
        if (entry != null) {
            return entry.getValue();
        } else {
            return nodeMap.firstEntry().getValue();
        }
    }

    // 增加键值对
    @Override
    public void addEntry(Entry entry) throws RemoteException {
        rwLock.readLock().lock();
        try {
            Node node = getNode(entry.key.hashCode());
            if (node != null) {
                node.remote.addEntry(entry);
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    // 删除键值对
    @Override
    public void removeEntryByKey(String key) {
        rwLock.readLock().lock();
        try {
            Node node = getNode(key.hashCode());
            if (node != null) {
                node.remote.removeEntryByKey(key);
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    // 获取键值对
    @Override
    public Entry getEntryByKey(String key) {
        rwLock.readLock().lock();
        try {
            Node node = getNode(key.hashCode());
            if (node != null) {
                return node.remote.getEntryByKey(key);
            } else {
                return null;
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        } finally {
            rwLock.readLock().unlock();
        }
    }
}
