import java.util.concurrent.ConcurrentSkipListSet;

public class ConsistentHashing {
    ConcurrentSkipListSet<Node> skipListSet = new ConcurrentSkipListSet<>();

    // 写
    public void addNode(Node<T> node) {
        rwLock.writeLock().lock();
        try {
            skipListSet.add(node);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    // 写
    public void removeNode(int key) {
        rwLock.writeLock().lock();
        try {
            skipListSet.remove(key);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    // 读
    public V getNode(int key) {
        rwLock.readLock().lock();
        try {
            if (skipListSet.isEmpty()) {
                return null;
            }
            Integer higherValue = skipListSet.higher(key);
            if (higherValue == null) {
                higherValue = skipListSet.first();
            }
            return map.get(higherValue);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    // 增加元素
    public addElement()

    // 删除元素


    // 获取元素
}
