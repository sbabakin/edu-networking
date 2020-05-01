package dev.sb.edu.network.protocol.transport.tcp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * TCP client.
 * The code intentionally has straight forward logic for demonstration.
 */
public class TcpClient {

    private final String host;
    private final int port;

    public TcpClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static void main(String[] args) {
        TcpClient tcpClient = new TcpClient("127.0.0.1", 12345);
        tcpClient.start();
    }

    public void start() {
        try {

            System.out.print("> ");
            Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8.name());
            while (scanner.hasNextLine()) {
                String userInput = scanner.nextLine();

                // we open client socket
                Socket socket = new Socket(host, port);
                System.out.println("open connection");

                // in our "protocol" \n defines end of message
                // write message to socket output stream
                String request = userInput + "\n";
                System.out.print("--> " + request);
                OutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                outputStream.write(request.getBytes());

                // read message from socket input stream
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                System.out.println("<-- " + reader.readLine());

                // end communication with the server
                socket.close();
                System.out.println("close connection");

                if ("STOP".equals(userInput)) {
                    break;
                }
                System.out.print("> ");
            }
            System.out.println("client shutdown");
        } catch (IOException e) {
            throw new RuntimeException("client exception", e);
        }
    }
}
