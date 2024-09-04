package info.kgeorgiy.ja.evseeva.hello;

import info.kgeorgiy.java.advanced.hello.HelloServer;
import info.kgeorgiy.java.advanced.hello.NewHelloServer;

import java.util.concurrent.ExecutorService;

abstract class AbstractHelloUDPServer implements NewHelloServer {
    protected static final int TIME = 100;
    protected static final String HELLO = "Hello, ";
    /**
     * The main entry for HelloServer.
     * @param args args[0] - number of port on which requests will be accepted;
     *             args[1] - number of worker threads that will process requests.
     */
    public static void main(String[] args) {
        if (args == null || args.length != 2 || args[0] == null || args[1] == null){
            System.err.println("Incorrect input, expected: int threads, Map<Integer, String> ports");
            return;
        }
        try (NewHelloServer server = new HelloUDPServer()) {
            server.start(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        } catch (RuntimeException e) {
            System.err.println("Error with starting HelloUDPServer");
        }
    }
    protected ExecutorService service;

}
