package info.kgeorgiy.ja.evseeva.hello;
import info.kgeorgiy.java.advanced.hello.HelloClient;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
/**
 * Class implements {@link HelloClient} interface.
 */
public class HelloUDPClient extends AbstractHelloUDPClient {
    /**
     * {@inheritDoc}
     */
    @Override
    public void run(String host, int port, String prefix, int threads, int requests) {
        try {
            InetAddress address = InetAddress.getByName(host);
            try (ExecutorService service = Executors.newFixedThreadPool(threads)) {
                IntStream.range(0, threads)
                        .mapToObj(i -> take(requests, prefix, i, address, port))
                        .forEach(service::submit);
            }
        } catch (UnknownHostException e) {
            System.err.println("The hostname could not be resolved: " + host);
        }
    }
    private Runnable take(int requests, String prefix, final int finalI, InetAddress address, int port) {
        return () -> {
            try (DatagramSocket socket = new DatagramSocket()) {
                socket.setSoTimeout(TIME);
                for (int i = 0; i < requests; i++) {
                    String s = createNewString(prefix, finalI, i);
                    DatagramPacket packet = new DatagramPacket(s.getBytes(), s.getBytes().length, address, port);
                    sendAndReceive(socket, packet, s);
                }
            } catch (SocketException e) {
                System.err.println("Couldn't create or bind socket");
            }

        };
    }

    private void sendAndReceive(DatagramSocket socket, DatagramPacket packet, String s) {
        while (!socket.isClosed()) {
            try {
                socket.send(packet);
                byte[] receiveBuffer = new byte[socket.getReceiveBufferSize()];
                DatagramPacket receivedPacket = new DatagramPacket(receiveBuffer, socket.getReceiveBufferSize());
                socket.receive(receivedPacket);

                String message = new String(receivedPacket.getData(), receivedPacket.getOffset(), receivedPacket.getLength(), StandardCharsets.UTF_8);

                if (message.contains(s)) {
                    System.out.println(message);
                    break;
                }
            } catch (IOException e) {
                System.err.println("Error with sending/receiving data");
            }
        }
    }

}
