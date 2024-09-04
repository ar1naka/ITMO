package info.kgeorgiy.java.advanced.hello;

import info.kgeorgiy.java.advanced.base.BaseTester;

import java.util.List;

/**
 * Tester for <a href="https://www.kgeorgiy.info/courses/java-advanced/homeworks.html#homework-hello-udp">Hello UDP</a> homework
 * of <a href="https://www.kgeorgiy.info/courses/java-advanced/">Java Advanced</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class Tester {
    private Tester() {
    }

    public static void main(final String... args) {
        if (args.length > 0) {
            Util.setMode(args[0]);
        }

        final BaseTester tester = new BaseTester();
        for (final String suffix : List.of("", "-i18n", "-evil")) {
            tester.add("server" + suffix, HelloServerTest.class);
            tester.add("client" + suffix, HelloClientTest.class);
            tester.add("new-server" + suffix, NewHelloServerTest.class);
            tester.add("new-client" + suffix, HelloClientTest.class);
        }

        TesterHelper.test(tester, args);
    }
}
