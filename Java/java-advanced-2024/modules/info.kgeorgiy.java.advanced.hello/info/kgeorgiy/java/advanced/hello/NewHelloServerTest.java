package info.kgeorgiy.java.advanced.hello;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Tests for {@link NewHelloServer}.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class NewHelloServerTest extends HelloServerTest {
    private static final InetSocketAddress SOCKET_ADDRESS = new InetSocketAddress("localhost", 28800);

    @Test
    public void test10_noPorts() throws IOException {
        testServer(List.of());
    }

    @Test
    public void test11_onePort() throws IOException {
        testServer(List.of("Bye $"));
    }

    @Test
    public void test12_twoPorts() throws IOException {
        testServer(List.of("Hello $", "Bye $"));
    }

    @Test
    public void test13_weirdFormat() throws IOException {
        testServer(List.of("$", "qqq$", "$qqq", "%1$$", "$qqq$"));
    }

    @Test
    public void test14_manyPorts() throws IOException {
        testServer(new Random(432785023487502348L)
                .ints(10)
                .boxed()
                .flatMap(i -> Stream.of(i + "$", "$" + i, i + "$" + i, "$" + i + "$"))
                .toList());
    }

    private static void testServer(final List<String> formats) throws IOException {
        final List<Endpoint> endpoints = IntStream.range(0, formats.size())
                .mapToObj(i -> new Endpoint(new InetSocketAddress(SOCKET_ADDRESS.getHostName(), SOCKET_ADDRESS.getPort() + i), formats.get(i)))
                .toList();
        final Map<Integer, String> ports = endpoints.stream()
                .collect(Collectors.toMap(e -> e.address.getPort(), Endpoint::format));
        try (final NewHelloServer server = createCUT()) {
            server.start(1, ports);
            try (final DatagramSocket socket = new DatagramSocket()) {
                for (final Endpoint endpoint : endpoints) {
                    endpoint.check(socket);
                }
            }
        }
    }

    private record Endpoint(InetSocketAddress address, String format) {
        private void check(final DatagramSocket socket) throws IOException {
            final String request = HelloServerTest.REQUEST + ":" + address.getPort();
            final String response = Util.request(request, socket, address);
            Assertions.assertEquals(Util.response(request, format), response, "Invalid response");
        }
    }
}
