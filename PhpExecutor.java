package function_php;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.Vector;
import config.*;

public class PhpExecutor 
{
    public static void sendError(PrintWriter out, int statusCode, String message) {
        out.println("HTTP/1.1 " + statusCode + " " + message);
        out.println("Content-Type: text/html");
        out.println();
        out.println("<h1>" + statusCode + " " + message + "</h1>");
        out.flush();
    }

    public static void sendFile(PrintWriter out, File file) throws IOException {
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: text/html");
        out.println();

        try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = fileReader.readLine()) != null) {
                out.println(line);
            }
        }
        out.flush(); //envoi de la reponse rapide
    }

    public static String phpOutput(String requestedFile, String method, String postData) throws IOException {
        Vector<String> commands = new Vector<>();
        commands.add("php");
        commands.add("-r");
        ProcessBuilder processBuilder;
        StringBuilder output = new StringBuilder();

        //Pour la methode GET
        if (method.equals("GET")) {
            System.out.println("methode GET");
            String[] parts = requestedFile.split("\\?");
            System.out.println("requested File:"+requestedFile);
            String filePath = parts[0]; //c'est le fichier (ex: /form.php)
            String params = parts.length > 1 ? parts[1] : ""; //raha inf a 1 tsisy parametre
            System.out.println("size :"+parts.length);
            if(parts.length > 1) {
                System.out.println("parts[1]:" +parts[1]);
            } 

            if(filePath.equals("/")) {
                filePath = "/index.php";
            }
            System.out.println("File path: "+filePath);
            System.out.println("Params : "+params);

            // commande pour executer PHP en mode GET
            commands.add("parse_str('" + params + "', $_GET); include('" +"./htdocs" + filePath + "');");
            processBuilder = new ProcessBuilder(commands);
            processBuilder.redirectErrorStream(true);

            // Lancer le processus PHP
            Process process = processBuilder.start();

            // Lire la sortie du processus PHP
            try (BufferedReader phpOutput = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = phpOutput.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
        }

        //Pour la methode POST
        else if (method.equals("POST")) {
            // commande pour executer PHP en mode POST
            commands.add("parse_str('" + postData + "', $_POST); include('" +"./htdocs" + requestedFile + "');");

            processBuilder = new ProcessBuilder(commands);
            processBuilder.redirectErrorStream(true);

            // Lancer le processus PHP
            Process process = processBuilder.start();

            // Écrire les données POST dans l'entrée standard du processus PHP
            try (OutputStream outputStream = process.getOutputStream()) {
                outputStream.write(postData.getBytes());
                outputStream.flush();
            }

            try (BufferedReader phpOutput = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = phpOutput.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
        }

        System.out.println(output.toString());
        return output.toString();
    }

    public static void sendPhpOutput(PrintWriter out, File file, String requestedFile, String method, String postData) throws IOException {
        //Hiexecutena anle fichier PHP
        //file.getAbsolutePath(): chemin complet du fichier php
        ProcessBuilder processBuilder = new ProcessBuilder("php", file.getAbsolutePath());
        Process process = processBuilder.start();

        BufferedReader phpError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: text/html");
        out.println();

        String line;
        out.println(phpOutput(requestedFile, method, postData));

        //En cas d'erreur dans le script PHP
        // while ((line = phpError.readLine()) != null) {
        //     out.println("<p style='color:red;'>" + line + "</p>");
        // }

        phpError.close();
        out.flush();
    }

    public static void serveImage(String filePath, String contentType, OutputStream out) throws IOException {
        String basePath = Configuration.getPath(); // Chemin de base
        File file = new File(basePath + filePath); // Ajoute le chemin de base
    
        if (file.exists() && !file.isDirectory()) {
            System.out.println("ATOOO");
            byte[] fileBytes = Files.readAllBytes(file.toPath());
            out.write(("HTTP/1.1 200 OK\r\n").getBytes());
            out.write(("Content-Type: " + contentType + "\r\n").getBytes());
            out.write(("Content-Length: " + fileBytes.length + "\r\n").getBytes());
            out.write(("\r\n").getBytes());
            out.write(fileBytes);
            out.flush();
        } else {
            out.write(("HTTP/1.1 404 Not Found\r\n").getBytes());
            out.write(("Content-Type: text/html\r\n").getBytes());
            out.write(("\r\n").getBytes());
            out.write("<html><body><h1>Erreur 404 : Fichier non trouvé</h1></body></html>\n".getBytes());
        }
    }

}
