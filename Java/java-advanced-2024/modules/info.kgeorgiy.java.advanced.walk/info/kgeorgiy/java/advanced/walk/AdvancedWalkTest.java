package info.kgeorgiy.java.advanced.walk;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Tests for advanced version
 * of <a href="https://www.kgeorgiy.info/courses/java-advanced/homeworks.html#homework-walk">Walk</a> homework
 * for <a href="https://www.kgeorgiy.info/courses/java-advanced/">Java Advanced</a> course.
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class AdvancedWalkTest extends RecursiveWalkTest {
    public AdvancedWalkTest() {
    }

    private final WalkUtil other = new WalkUtil() {
        @Override
        public String hash(final byte[] bytes) {
            if (bytes == ERROR_CONTENT) {
                return errorHash;
            }
            try {
                final MessageDigest digest = MessageDigest.getInstance("SHA-1");
                final byte[] hash = bytes == null ? new byte[digest.getDigestLength()] : digest.digest(bytes);
                return String.format("%0" + (hash.length << 1) + "x", new BigInteger(1, hash));
            } catch (final NoSuchAlgorithmException e) {
                throw new AssertionError("Digest error: " + e.getMessage(), e);
            }
        }

        @Override
        public void run(final String... args) {
            super.run(Stream.concat(Arrays.stream(args), Stream.of("sha-1")).toArray(String[]::new));
        }
    };

    @Override
    protected void check(
            final Collection<String> inputs,
            final Map<String, byte[]> files,
            final String inputFile,
            final String outputFile
    ) {
        super.check(inputs, files, inputFile, outputFile);
        other.check(inputs, files, inputFile, outputFile + "~2");
    }
}
