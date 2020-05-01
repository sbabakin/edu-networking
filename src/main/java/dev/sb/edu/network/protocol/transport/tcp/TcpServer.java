package dev.sb.edu.network.protocol.transport.tcp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Single-threaded TCP server.
 * The code intentionally has straight forward logic for demonstration.
 */
public class TcpServer {

    private final int listenPort;

    public TcpServer(int listenPort) {
        this.listenPort = listenPort;
    }

    public static void main(String[] args) {
        TcpServer server = new TcpServer(12345);
        server.start();
    }

    public void start() {
        try {
            // here we bind server process to listening port
            ServerSocket serverSocket = new ServerSocket(listenPort);
            System.out.println("listening %s" + serverSocket.getLocalPort());

            while (true) {
                System.out.println("waiting...");
                // at this stage we passed TCP handshake and obtain connection socket
                Socket connectionSocket = serverSocket.accept();
                System.out.println("accept connection...");
                // below we process request and respond to client
                InputStream inputStream = connectionSocket.getInputStream();
                BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream));
                String request = inputReader.readLine();

                System.out.println("<-- " + request);
                DataOutputStream outputStream = new DataOutputStream(connectionSocket.getOutputStream());

                // assume we do some work here
                Thread.sleep(1000);
                String response = prepareResponse(request) + "\n";

                System.out.print("--> " + response);
                outputStream.write(response.getBytes());

                // just stop the server on STOP command
                if ("STOP".equals(request)){
                    break;
                }
            }

            serverSocket.close();
            System.out.println("server shutdown");
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("server exception", e);
        }
    }

    private String prepareResponse(String incomingMessage) {
        return new StringBuilder(incomingMessage).reverse().toString();
    }
}
