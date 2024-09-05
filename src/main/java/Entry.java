import java.io.Serializable;

public class Entry implements Serializable {
    private static final long serialVersionUID = 1L;

    public String key;
    public String value;

    public Entry(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Entry{key='" + key + "', value='" + value + "'}";
    }
}
