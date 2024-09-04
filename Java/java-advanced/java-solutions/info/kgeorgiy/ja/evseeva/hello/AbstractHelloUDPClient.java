package info.kgeorgiy.ja.evseeva.hello;

import info.kgeorgiy.java.advanced.hello.HelloClient;

abstract class AbstractHelloUDPClient implements HelloClient {
    protected static final int TIME = 100;
    protected static final String HELLO = "Hello, ";


    /**
     * The main entry for HelloClient.
     * @param args args[0] - the name or IP address of the computer running the server;
     *             args[1] - the number of the port to send requests to;
     *             args[2] - request prefix (string);
     *             args[3] - number of parallel request streams;
     *             args[4] - number of requests in each thread.
     */
    public static void main(String[] args) {
        if (args == null || args.length != 5 || args[0] == null || args[1] == null || args[2] == null
                || args[3] == null || args[4] == null) {
            System.err.println("Incorrect input, expected: String host, int port, String prefix, int threads, int requests");
            return;
        }
        HelloClient client = new HelloUDPClient();
        client.run(args[0], Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3]),  Integer.parseInt(args[4]));
    }
    protected String createNewString (String prefix, int thread, int request) {
        return String.format("%s%d_%d", prefix, thread + 1, request + 1);
    }
}
