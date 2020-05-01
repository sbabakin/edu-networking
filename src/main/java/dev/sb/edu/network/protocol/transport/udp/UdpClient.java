package dev.sb.edu.network.protocol.transport.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Simple UDP client.
 * The code intentionally has straight forward logic for demonstration.
 */
public class UdpClient {

    private final String host;
    private final int port;

    public UdpClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static void main(String[] args) {
        UdpClient tcpClient = new UdpClient("127.0.0.1", 12345);
        tcpClient.start();
    }

    public void start() {
        try {
            System.out.print("> ");
            Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8.name());
            while (scanner.hasNextLine()) {
                String userInput = scanner.nextLine();
                System.out.println("--> " + userInput);

                byte[] sendBuffer = userInput.getBytes();
                InetAddress inetAddress = InetAddress.getByName(host);
                DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, inetAddress, port);

                // we open client socket and send datagram
                DatagramSocket socket = new DatagramSocket();
                System.out.println("send UPP datagram");
                socket.send(sendPacket);

                while (true) {
                    try {
                        // protocol matters, basically here we assume that received packet has same length...
                        // note that we accept only 3 datagrams while server send much more.
                        byte[] receiveBuffer = new byte[sendBuffer.length];
                        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                        socket.setSoTimeout(1000); // wait 1 second for incoming data
                        socket.receive(receivePacket);
                        String receive = new String(receiveBuffer);
                        System.out.println("<-- " + receive);
                    } catch (SocketTimeoutException e) {
                        // basically in real life a client must
                        // retry to reconnect or send control command to UDP server
                        System.out.println("got timeout, take some actions...");
                        break;
                    }
                }

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
