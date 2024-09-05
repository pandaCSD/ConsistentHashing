import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RemoteServerImpl extends UnicastRemoteObject implements RemoteServer {
    private static final long serialVersionUID = 1L;

    // 存储键值对的内存数据结构
    private Map<String, Entry> entryMap;

    public RemoteServerImpl() throws RemoteException {
        // 初始化键值对存储
        entryMap = new HashMap<>();
    }

    @Override
    public String addEntry(Entry entry) throws RemoteException {
        entryMap.put(entry.key, entry);
        return "Entry added: " + entry.toString();
    }

    @Override
    public String removeEntryByKey(String key) throws RemoteException {
        Entry removedEntry = entryMap.remove(key);
        if (removedEntry != null) {
            return "Entry removed: " + removedEntry.toString();
        } else {
            return "No entry found with key: " + key;
        }
    }

    @Override
    public Entry getEntryByKey(String key) throws RemoteException {
        return entryMap.get(key);
    }

    @Override
    public List<Entry> getEntriesByHash(int hash) throws RemoteException {
        List<Entry> result = new ArrayList<>();
        for (Entry entry : entryMap.values()) {
            if (entry.key.hashCode() <= hash) {
                result.add(entry);
            }
        }
        return result;
    }

    @Override
    public List<Entry> getAllEntries() throws RemoteException {
        return new ArrayList<>(entryMap.values());
    }
}
