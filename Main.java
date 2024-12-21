package main;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.SwingUtilities;
import serveur.*;
import requete.*;

public class Main 
{
    public static void main(String[] args) {
        // ServerHttp serverHttp = new ServerHttp();
        // serverHttp.startServer();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new HTTPServerGUI();
            }
        });
    }    
}