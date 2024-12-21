package serveur;
import java.io.IOException;
import java.io.ObjectInputFilter.Config;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import requete.HandleRequest;
import config.*;

public class ServerHttp implements Runnable
{
    private int port;
    private ServerSocket serverSocket;
    private boolean running = false;
    
    //GETTERS & SETTERS
    public void setPort(int port) {
        this.port = port;
    }
    public int getPort() {
        return port;
    }
    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }
    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    //CONSTRUCTOR
    public ServerHttp(int port) {
        this.port = port;
    } public ServerHttp() {
        
    }

    public void startServer() {
        new Thread(this).start();  // Démarre le serveur dans un thread séparé
    }

    public void run() {
        try {
            running = true;
            this.serverSocket = new ServerSocket(Configuration.getPort(), 50, InetAddress.getByName("0.0.0.0"));
            System.out.println("Serveur connecte au port "+Configuration.getPort());

            while (running) {
                Socket clientSocket = serverSocket.accept();
                HandleRequest.handleRequest(clientSocket);
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    public void stopServer() {
        try {
            if(this.serverSocket != null && !this.serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
