package serveur;
import javax.swing.*;
import config.Configuration;
import serveur.ServerHttp;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HTTPServerGUI {

    private JFrame frame;
    private JTextField urlField;
    private JButton startButton, stopButton;
    private JTextArea logArea;
    private JLabel statusLabel;
    private ServerHttp serverHttp;

    public HTTPServerGUI() {
        frame = new JFrame("Serveur HTTP");
        frame.setSize(700, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        urlField = new JTextField(30);
        urlField.setText("localhost:"+Configuration.getPort());

        startButton = new JButton("Démarrer");
        stopButton = new JButton("Arrêter");
        stopButton.setEnabled(false);

        panel.add(new JLabel("URL:"));
        panel.add(urlField);
        panel.add(startButton);
        panel.add(stopButton);

        logArea = new JTextArea(10, 40);
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);

        statusLabel = new JLabel("Serveur hors ligne", JLabel.CENTER);
        statusLabel.setForeground(Color.RED);

        frame.add(panel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(statusLabel, BorderLayout.SOUTH);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                serverHttp = new ServerHttp();
                startServer();
            }
        });

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopServer();
            }
        });

        frame.setVisible(true);
    }

    private void startServer() {
        serverHttp = new ServerHttp();
        serverHttp.startServer(); // Démarrer le serveur dans un thread
        logArea.append("Serveur démarré sur " + urlField.getText() + "\n");
        statusLabel.setText("Serveur en ligne");
        statusLabel.setForeground(Color.GREEN);
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
    }

    private void stopServer() {
        if (serverHttp != null) {
            serverHttp.stopServer();
        }
        logArea.append("Serveur arrêté.\n");
        statusLabel.setText("Serveur hors ligne");
        statusLabel.setForeground(Color.RED);
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
    }
}

