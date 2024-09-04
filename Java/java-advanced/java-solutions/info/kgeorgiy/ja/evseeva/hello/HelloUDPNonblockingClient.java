package info.kgeorgiy.ja.evseeva.hello;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.stream.IntStream;


public class HelloUDPNonblockingClient extends AbstractHelloUDPClient {
    Selector selector;
    DatagramChannel channel;
    @Override
    public void run(String host, int port, String prefix, int threads, int requests) {
        try {
            selector = Selector.open();
            channel = DatagramChannel.open();
            SocketAddress address = new InetSocketAddress(host, port);
            channel.configureBlocking(false).register(selector, SelectionKey.OP_WRITE);

            IntStream.range(0, threads).forEach(i -> IntStream.range(0, requests)
                            .forEach(j -> processRequests(address, prefix, i, j)));

        } catch (IOException e) {
            System.err.println("The hostname could not be resolved: " + host);
        }
    }

    private void processRequests(SocketAddress address, String prefix, int thread, int request)  {
        while (!selector.keys().isEmpty()) {
            try {
                if (selector.select(TIME) == 0) {
                    selector.keys().forEach(key -> key.interestOps(SelectionKey.OP_WRITE));
                }
            } catch (IOException e) {
                System.err.println("Error with selector");
            }

            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
            while (keys.hasNext()) {
                SelectionKey key = keys.next();
                String s = createNewString(prefix, thread, request);

                try {
                    if (key.isWritable()) {
                        channel.send(ByteBuffer.wrap(s.getBytes(StandardCharsets.UTF_8)), address);
                        key.interestOps(SelectionKey.OP_READ);
                    } else if (key.isReadable()) {
                        ByteBuffer buffer = ByteBuffer.allocate(channel.socket().getReceiveBufferSize());
                        channel.receive(buffer);
                        String receivedString = new String(buffer.array(), buffer.arrayOffset(), buffer.position(), StandardCharsets.UTF_8);

                        key.interestOps(SelectionKey.OP_WRITE);

                        if (receivedString.equals(HELLO + s)) {
                            System.out.println(receivedString);
                            return;
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Error during sending or receiving data");
                } finally {
                    keys.remove();
                }
            }
        }
    }


}
