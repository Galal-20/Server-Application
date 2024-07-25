package com.iti.serverapplication;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerController implements Initializable {
    @FXML
    private TextField portField;
    @FXML
    private Button startServerBtn;
    @FXML
    private Button stopServerBtn;
    @FXML
    private Label statusText;

    private ServerSocket serverSocket; // Added to manage server socket
    private List<ClientHandler> clients = new ArrayList<>(); // Added to manage connected clients
    private volatile boolean running = false; // Added to control server running state

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        stopServerBtn.setVisible(false); // Ensure stop button is hidden initially
    }

    public void loadTableData(){
        try {
            // Empty implementation
        } catch (Exception ex) {
            ex.printStackTrace();
            Alert a = new Alert(AlertType.ERROR);
            a.setTitle("Failed");
            a.setHeaderText("Connection Failed");
            a.setResizable(true);
            a.setContentText("Connection to Database Failed");
            a.showAndWait();
            System.exit(0);
        }
    }

    public void startServer(ActionEvent ae) {
        if (portField.getText().isEmpty() || !Pattern.matches(
                "^((6553[0-5])|(655[0-2][0-9])|(65[0-4][0-9]{2})|(6[0-4][0-9]{3})|([1-5][0-9]{4})|([0-5]{0,5})|([0-9]{1,4}))$",
                portField.getText())) {
            Alert a = new Alert(AlertType.ERROR);
            a.setTitle("Failed");
            a.setHeaderText("Server Failed to start");
            a.setResizable(true);
            a.setContentText("The port number is not valid");
            a.showAndWait();
            return;
        }

        int port = Integer.parseInt(portField.getText());
        startServerBtn.setVisible(false);
        stopServerBtn.setVisible(true);
        statusText.setTextFill(Color.GREEN);
        statusText.setText("Online");
        portField.setDisable(true);

        // Start the server in a separate thread
        running = true;
        new Thread(() -> startServerSocket(port)).start(); // Added to start server in a new thread
    }

    private void startServerSocket(int port) {
        try {
            serverSocket = new ServerSocket(port); // Create server socket
            while (running) {
                Socket clientSocket = serverSocket.accept(); // Accept client connections
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                new Thread(clientHandler).start(); // Handle each client in a new thread
            }
        } catch (IOException e) {
            if (running) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    Alert a = new Alert(AlertType.ERROR);
                    a.setTitle("Error");
                    a.setHeaderText("Server Error");
                    a.setContentText("An error occurred while starting the server.");
                    a.showAndWait();
                });
            }
        } finally {
            stopServerSocket(); // Ensure server socket is closed
        }
    }

    private void stopServerSocket() {
        running = false; // Stop accepting new connections
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close(); // Close the server socket
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setServerStopped() {
        stopServerBtn.setVisible(false);
        startServerBtn.setVisible(true);
        statusText.setTextFill(Color.RED);
        statusText.setText("Offline");
        portField.setDisable(false);
        stopServerSocket(); // Ensure server is stopped
    }

    public void stopServer(ActionEvent ae) {
        setServerStopped();
    }

    public void exit(ActionEvent ae) {
        setServerStopped();
        System.exit(0);
    }

    // Added class to handle client connections
    private class ClientHandler implements Runnable {
        private Socket socket;
        // Add necessary streams and methods for client handling

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            // Handle client communication here
        }

        private void handleClient() {
            // Add logic to handle client communication
        }
    }
}
