package config;
import java.io.FileInputStream;
import java.util.Properties;

public class Configuration 
{
    private static int port;
    private static String path;

    public static int getPort() {
        return port;
    }
    public static void setPort(int port) {
        port = port;
    }
    public static String getPath() {
        return path;
    }
    public static void setPath(String path) {
        path = path;
    }

    static {
        try {
            Properties properties = new Properties();
            FileInputStream in= new FileInputStream("C:\\PROGRAMMATION\\Http(Li)\\Config.txt");
            properties.load(in);
            
            port=Integer.parseInt(properties.getProperty("port"));
            System.out.println("port: "+ port);
            path=properties.getProperty("path");
            System.out.println("path" +path);

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

}
