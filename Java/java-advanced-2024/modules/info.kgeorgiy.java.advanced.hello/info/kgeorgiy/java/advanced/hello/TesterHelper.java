package info.kgeorgiy.java.advanced.hello;

import info.kgeorgiy.java.advanced.base.BaseTester;

/**
 * Utility class for {@link Tester}.
 */
public class TesterHelper {
    private TesterHelper() {
    }

    public static void test(final BaseTester tester, final String[] args) {
        if (args.length > 1) {
            final String prefix = args[1].replaceAll("Server$", "").replaceAll("Client$", "");
            final String test = args[0];
            if (test.contains("both")) {
                test(tester, prefix, test.replaceAll("(?<=^|-)both", "client"), args);
                test(tester, prefix, test.replaceAll("(?<=^|-)both", "server"), args);
            } else {
                test(tester, prefix, test, args);
            }
        } else {
            tester.run(args);
        }
    }

    public static void test(final BaseTester tester, final String prefix, final String test, final String[] args) {
        args[0] = test.toLowerCase();
        final String baseTest = test.replaceAll("^new-", "");
        args[1] = prefix + Character.toUpperCase(baseTest.charAt(0)) + baseTest.substring(1, (baseTest + "-").indexOf("-"));
        tester.run(args);
    }
}
