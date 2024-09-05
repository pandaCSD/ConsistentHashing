import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.Naming;

public class ClientGUI {

    private JFrame frame;
    private JTextField keyField;
    private JTextField valueField;
    private JTextArea outputArea;
    private ClientInterface server;

    public ClientGUI(ClientInterface server) {
        this.server = server;
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setTitle("Client GUI");
        frame.setBounds(100, 100, 450, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        frame.getContentPane().add(panel, BorderLayout.NORTH);
        panel.setLayout(new GridLayout(2, 2));

        JLabel keyLabel = new JLabel("Key:");
        panel.add(keyLabel);

        keyField = new JTextField();
        panel.add(keyField);
        keyField.setColumns(10);

        JLabel valueLabel = new JLabel("Value:");
        panel.add(valueLabel);

        valueField = new JTextField();
        panel.add(valueField);
        valueField.setColumns(10);

        JPanel buttonPanel = new JPanel();
        frame.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        JButton addButton = new JButton("Add Entry");
        buttonPanel.add(addButton);
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addEntry();
            }
        });

        JButton removeButton = new JButton("Remove Entry");
        buttonPanel.add(removeButton);
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeEntry();
            }
        });

        JButton getButton = new JButton("Get Entry");
        buttonPanel.add(getButton);
        getButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getEntry();
            }
        });

        JButton exitButton = new JButton("Exit");
        buttonPanel.add(exitButton);
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        outputArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(outputArea);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    // 添加条目
    private void addEntry() {
        String key = keyField.getText();
        String value = valueField.getText();
        try {
            Entry entry = new Entry(key, value);
            server.addEntry(entry);
            outputArea.append("Entry added: " + entry + "\n");
        } catch (Exception e) {
            e.printStackTrace();
            outputArea.append("Error adding entry: " + e.getMessage() + "\n");
        }
    }

    // 删除条目
    private void removeEntry() {
        String key = keyField.getText();
        try {
            server.removeEntryByKey(key);
            outputArea.append("Entry with key " + key + " removed.\n");
        } catch (Exception e) {
            e.printStackTrace();
            outputArea.append("Error removing entry: " + e.getMessage() + "\n");
        }
    }

    // 获取条目
    private void getEntry() {
        String key = keyField.getText();
        try {
            Entry entry = server.getEntryByKey(key);
            if (entry != null) {
                outputArea.append("Fetched entry: " + entry + "\n");
            } else {
                outputArea.append("No entry found with key: " + key + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
            outputArea.append("Error fetching entry: " + e.getMessage() + "\n");
        }
    }

    // 启动客户端
    public static void startClient() {
        SwingUtilities.invokeLater(() -> {
            try {
                ClientInterface server = (ClientInterface) Naming.lookup("rmi://localhost:9999/ConsistentHashing");
                new ClientGUI(server);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}

