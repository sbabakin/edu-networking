package dev.sb.edu.network.protocol.transport.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

/**
 * Single-threaded TCP server.
 * The code intentionally has straight forward logic for demonstration.
 */
public class UdpServer {

    private final int listenPort;

    public UdpServer(int listenPort) {
        this.listenPort = listenPort;
    }

    public static void main(String[] args) {
        UdpServer server = new UdpServer(12345);
        server.start();
    }

    public void start() {
        try {
            // here we bind server process to OS listening port
            DatagramSocket serverSocket = new DatagramSocket(listenPort);
            System.out.println("listening %s" + serverSocket.getLocalPort());

            byte[] receiveBuffer = new byte[32];
            while (true) {
                System.out.println("waiting...");
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                serverSocket.receive(receivePacket);

                // decode received buffer
                String receive = new String(receiveBuffer, StandardCharsets.UTF_8);
                System.out.println("<-- " + receive);

                // just stop the server on STOP command
                if (receive.startsWith("STOP")) {
                    break;
                }

                // start sending UDP datagrams in response
                for (int i = 0; i < 10; i++) {
                    Thread.sleep(500);
                    System.out.println(i + " |--> " + receive);

                    // just echo incoming message
                    byte[] sendBuffer = receive.getBytes();
                    InetAddress fromAddress = receivePacket.getAddress();
                    int fromPort = receivePacket.getPort();
                    DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, fromAddress, fromPort);
                    serverSocket.send(sendPacket);
                }
            }

            serverSocket.close();
            System.out.println("server shutdown");
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("server exception", e);
        }
    }
}
