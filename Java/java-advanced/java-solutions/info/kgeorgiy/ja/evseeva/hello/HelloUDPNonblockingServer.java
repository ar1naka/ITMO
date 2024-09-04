package info.kgeorgiy.ja.evseeva.hello;

import info.kgeorgiy.java.advanced.hello.HelloServer;
import info.kgeorgiy.java.advanced.hello.NewHelloServer;


import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

import java.nio.channels.Channel;
import java.nio.charset.StandardCharsets;
import java.util.*;

import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class HelloUDPNonblockingServer  extends AbstractHelloUDPServer {
    Selector selector;
    private ExecutorService single;

    private record Response(SocketAddress address, String mes) { }

    private final List<DatagramChannel> channels = new ArrayList<>();


    @Override
    public void start(int threads, Map<Integer, String> ports) {
        try {
            selector = Selector.open();
            single = Executors.newSingleThreadExecutor();
            service = Executors.newFixedThreadPool(threads);

        } catch (IOException e){
            System.err.println("Error with creating");
        }
        ports.keySet().forEach(port -> {
            try {
                Queue<Response> queue = new ConcurrentLinkedDeque<>();
                DatagramChannel channel = DatagramChannel.open();
                SocketAddress address = new InetSocketAddress(port);
                channel.bind(address).configureBlocking(false).register(selector, SelectionKey.OP_READ);

                channels.add(channel);

                single.submit(take(ports.get(channel.socket().getLocalPort()), channel, queue));
            } catch (IOException e) {
                System.err.println("Error with creating socket on the port: " + port);
            }
        });

    }


    private Runnable take(String s, DatagramChannel channel, Queue<Response> queue) {
        return () -> {
            while (selector.isOpen()) {
                try {
                    selector.select();
                    Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                    while (keys.hasNext()) {
                        SelectionKey key = keys.next();
                        try {
                            if (key.isReadable()) {
                                ByteBuffer buffer = ByteBuffer.allocate(channel.socket().getReceiveBufferSize());
                                SocketAddress address = channel.receive(buffer);

                                String receivedString = new String(buffer.array(), buffer.arrayOffset(), buffer.position(), StandardCharsets.UTF_8);

                                service.submit(() -> {
                                    queue.add(new Response(address,  s.replaceAll("\\$", receivedString)));
                                    key.interestOps(SelectionKey.OP_WRITE);
                                    selector.wakeup();
                                });
                            } else if (key.isWritable()) {
                                Response response = queue.poll();

                                if (response == null) {
                                    key.interestOps(SelectionKey.OP_READ);
                                } else {
                                    channel.send(ByteBuffer.wrap(response.mes.getBytes(StandardCharsets.UTF_8)), response.address);
                                }

                            }
                        } catch (IOException e) {
                            System.err.println("Error with receiving/sending package");
                            throw new RuntimeException();
                        } finally {
                            keys.remove();
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }


    @Override
    public void close() {
        try {
            for (DatagramChannel c : channels) {
                c.close();
            }

            selector.close();
        } catch (IOException e) {
            System.err.println("Error with closing.");
        }
        if (single != null) {
            single.close();
        }
        if (service != null) {
            service.close();
        }

    }
}
