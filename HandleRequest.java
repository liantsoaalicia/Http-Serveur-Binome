package requete;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import config.Configuration;
import function_php.PhpExecutor;

public class HandleRequest 
{

    public static String getPostData(BufferedReader in) 
    {
        String line;
        int contentLength = 0;
        String postData ="";

        try {
            while (!(line = in.readLine()).isEmpty()) {
                // System.out.println("En-tête : " + line);
                if (line.startsWith("Content-Length")) {
                    contentLength = Integer.parseInt(line.split(":")[1].trim());
                }
            }
    
            if (contentLength > 0) {
                char[] buffer = new char[contentLength];
                int readBytes = in.read(buffer, 0, contentLength);
                if (readBytes == contentLength) {
                    postData = new String(buffer);
                    System.out.println("Données POST reçues : " + postData);
                } else {
                    System.out.println("Erreur : Données POST incomplètes.");
                }
                return postData;
            } else {
                System.out.println("Erreur : Pas de contenu POST détecté.");
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return postData;
    }

    private static void serveDirectory(PrintWriter out) throws IOException {
        String path = Configuration.getPath();  // Chemin du répertoire à servir
        File directory = new File(path);
        
        if (directory.exists() && directory.isDirectory()) {
            out.println(("HTTP/1.1 200 OK\r\n").getBytes());
            out.println(("Content-Type: text/html\r\n").getBytes());
            out.println(("\r\n").getBytes());
        
            out.println(("<html><body><h1>Répertoire: " + directory.getName() + "</h1><ul>").getBytes());
        
            for (File file : directory.listFiles()) {
                if (file.isFile()) {
                    String fileName = file.getName();
                    out.println(("<li><a href=\"" + fileName + "\">" + fileName + "</a></li>").getBytes());
                }
            }
        
            out.println("</ul></body></html>".getBytes());
            out.flush();
        } else {
            out.println(("HTTP/1.1 404 Not Found\r\n").getBytes());
            out.println(("Content-Type: text/html\r\n").getBytes());
            out.println(("\r\n").getBytes());
            out.println("<html><body><h1>Erreur 404 : Répertoire non trouvé</h1></body></html>\n".getBytes());
        }
    }
    
    public static void handleRequest(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream())) {

            //getInputStream: mamaky anle requete du client par ex: localhost:1234/test.html -> GET /test.html HTTP/1.1
            //new InputStreamReader: mamadika chaine de caractere
            //BufferedReader: on met dedans pour lire ligne par ligne

            String requestLine = in.readLine();
            if (requestLine == null) {
                System.out.println("ERROR 1");
                PhpExecutor.sendError(out, 400, "Bad Request");
                return;
            }

            //satria tsy maintsy <MÉTHODE> <URL> <VERSION_HTTP> ny structure
            String[] requestParts = requestLine.split(" ");
            if (requestParts.length < 2) {
                System.out.println("ERROR 2");
                PhpExecutor.sendError(out, 400, "Bad Request");
                return;
            }

            
            String postData = "";
            if(requestParts[0].equals("POST")) {
                postData = getPostData(in);
            }
            String requestedFile = requestParts[1];

            if (requestedFile.contains("?")) {
                String[] parts = requestParts[1].split("\\?");
                requestedFile = parts[0]; //le fichier
            }
             else if (requestedFile.equals("/")) {
                requestedFile ="/index.php"; 
            }
             else {
                requestedFile = requestParts[1];
            }
            System.out.println("requested File: " +requestedFile);

            String path = Configuration.getPath();
            // File file = new File("htdocs" + requestedFile);
            File file = new File(path + requestedFile);
            if (!file.exists() || file.isDirectory()) { //na tsy miexiste na dossier pcq tsy maintsy fichier
                System.out.println("ERROR 3");
                PhpExecutor.sendError(out, 404, "Not Found");
                return;
            }

            OutputStream outImage = clientSocket.getOutputStream();

            // Vérifie si c'est un fichier PHP
            if (requestedFile.endsWith(".php")) {
                System.out.println("PHP");
                PhpExecutor.sendPhpOutput(out, file, requestParts[1], requestParts[0], postData);
            } else if (requestParts[1].endsWith(".jpg") || requestParts[1].endsWith(".jpeg")) {
                PhpExecutor.serveImage(requestParts[1], "image/jpeg", outImage);
            } else if (requestParts[1].endsWith(".png")) {
                PhpExecutor.serveImage(requestParts[1], "image/png", outImage);
            } 
            else {
                System.out.println("TSY PHP");
                PhpExecutor.sendFile(out, file);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
