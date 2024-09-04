package info.kgeorgiy.ja.evseeva.hello;

import info.kgeorgiy.java.advanced.hello.NewHelloServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
/**
 * Class implements {@link NewHelloServer} interface.
 */
public class HelloUDPServer extends AbstractHelloUDPServer  {

    private final List<DatagramSocket> sockets = new ArrayList<>();
    /**
     * {@inheritDoc}
     */
    @Override
    public void start(int threads, Map<Integer, String> ports) {
        int nThreads = threads + ports.size();
        service = Executors.newFixedThreadPool(nThreads);
        ports.keySet().forEach(port -> {
            try {
                DatagramSocket socket = new DatagramSocket(port);
                socket.setSoTimeout(TIME);
                sockets.add(socket);
                Runnable task = take(socket.getReceiveBufferSize(), ports.get(socket.getLocalPort()), socket);
                IntStream.range(0, threads).forEach(i -> service.submit(task));
            } catch (SocketException e) {
                System.err.println("Error with creating socket on the port: " + port);
            }
        });
    }


    private Runnable take(int size, String type, DatagramSocket socket)  {
        return () -> {
            byte[] buffer = new byte[size];
            DatagramPacket packet = new DatagramPacket(buffer, size);

            while (!socket.isClosed()) {
                try {
                    socket.receive(packet);
                    String message = new String(packet.getData(), packet.getOffset(), packet.getLength(), StandardCharsets.UTF_8);
                    packet.setData(type.replaceAll("\\$", message).getBytes(StandardCharsets.UTF_8));
                    socket.send(packet);
                } catch (IOException e) {
                    System.err.println("Error with receiving/sending package");
                    throw new RuntimeException();
                }
            }
        };
    }

    @Override
    public void close() {
        sockets.forEach(DatagramSocket::close);
        if (service != null) {
            service.close();
        }
    }
}

