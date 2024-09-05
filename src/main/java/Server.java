import java.io.FileInputStream;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Properties;

public class Server {
    public static void main(String[] args) {
        // 加载配置文件
        Properties properties = new Properties();
        try {
            // 从文件中加载配置
            properties.load(new FileInputStream("server_config.properties"));

            // 获取配置文件中的参数
            int serverCount = Integer.parseInt(properties.getProperty("server_count"));
            String consistentHashingServiceURL = properties.getProperty("consistent_hashing_service_url");
            System.out.println("Consistent Hashing Service URL: " + consistentHashingServiceURL);
            int basePort = Integer.parseInt(properties.getProperty("base_port"));

            // 获取一致性哈希服务对象
            ServerInterface consistentHashing = (ServerInterface) Naming.lookup(consistentHashingServiceURL);
            String serviceName = "RemoteServer"; // 动态分配服务名称

            // 循环创建和绑定多个远程对象实例
            for (int i = 0; i < serverCount; i++) {
                int port = basePort + i; // 动态分配端口

                // 创建远程对象实例
                RemoteServerImpl server = new RemoteServerImpl();

                // 启动 RMI 注册表并绑定实例
                try {
                    LocateRegistry.createRegistry(port);
                    System.out.println("RMI Registry started on port: " + port);
                } catch (RemoteException e) {
                    System.out.println("RMI Registry already running on port: " + port);
                }

                // 绑定远程对象到 RMI 注册表
                Naming.rebind("rmi://localhost:" + port + "/" + serviceName, server);
                System.out.println("RemoteServer instance " + serviceName + " is ready on port: " + port);

                // 调用 ConsistentHashingImpl 的 addNode 方法，将当前服务添加到一致性哈希环中
                consistentHashing.addNode("localhost", port);
                System.out.println("Node added to Consistent Hashing at port: " + port);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
