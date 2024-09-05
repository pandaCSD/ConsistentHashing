import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
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

    private int hash32(String data) {
        final byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        int length = bytes.length;
        int h1 = 0;

        final int c1 = 0xcc9e2d51;
        final int c2 = 0x1b873593;
        final int r1 = 15;
        final int r2 = 13;
        final int m = 5;
        final int n = 0xe6546b64;

        int i = 0;
        while (i + 4 <= length) {
            int k1 = (bytes[i] & 0xff) | ((bytes[i + 1] & 0xff) << 8) | ((bytes[i + 2] & 0xff) << 16) | (bytes[i + 3] << 24);
            i += 4;

            k1 *= c1;
            k1 = Integer.rotateLeft(k1, r1);
            k1 *= c2;

            h1 ^= k1;
            h1 = Integer.rotateLeft(h1, r2) * m + n;
        }

        int k1 = 0;
        switch (length & 3) {
            case 3:
                k1 ^= (bytes[i + 2] & 0xff) << 16;
            case 2:
                k1 ^= (bytes[i + 1] & 0xff) << 8;
            case 1:
                k1 ^= (bytes[i] & 0xff);
                k1 *= c1;
                k1 = Integer.rotateLeft(k1, r1);
                k1 *= c2;
                h1 ^= k1;
        }

        h1 ^= length;
        h1 ^= h1 >>> 16;
        h1 *= 0x85ebca6b;
        h1 ^= h1 >>> 13;
        h1 *= 0xc2b2ae35;
        h1 ^= h1 >>> 16;

        return h1;
    }

    private Integer hashNode(Node node) {
        String combined = node.IP + ":" + node.PORT;
        return hash32(combined);
    }

    // 增加结点
    @Override
    public void addNode(String IP ,Integer PORT) throws RemoteException {
        rwLock.writeLock().lock();
        Node node = null;
        try {
            node = new Node(IP, PORT);
            int hash = hashNode(node);
            nodeMap.put(hash, node);
            Node nextNode = getNode(hash + 1);
            if (nextNode != null) {
                List<Entry> entries = nextNode.remote.getEntriesByHash(hash);
                for (Entry entry : entries) {
                    node.remote.addEntry(entry);
                    nextNode.remote.removeEntryByKey(entry.key);
                }
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        }
         finally {
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
            Node nextNode = getNode(hash + 1);
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
