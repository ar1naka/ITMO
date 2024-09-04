package info.kgeorgiy.java.advanced.walk;

import info.kgeorgiy.java.advanced.base.BaseTest;

import org.junit.jupiter.api.Assertions;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility functions for {@link WalkTest} and {@link RecursiveWalkTest}.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class WalkUtil {
    /* package-private */ static final Path DIR = Path.of("__Test__Walk__");
    public static final List<String> ENGLISH_DIGITS = codePoints("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789");
    public static final byte[] ERROR_CONTENT = new byte[]{0, 1, 2, 3};

    private static final SimpleFileVisitor<Path> DELETE = new SimpleFileVisitor<>() {
        @Override
        public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
            Files.delete(file);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {
            Files.delete(dir);
            return FileVisitResult.CONTINUE;
        }
    };

    /* package-private */ List<String> alphabet = ENGLISH_DIGITS;
    /* package-private */ final Random random = new Random(3495823423458708L);

    /* pckage-private */ final String errorHash;

    @SuppressWarnings("this-escape")
    public WalkUtil() {
        errorHash = hash(null);
    }

    public String hash(final byte[] bytes) {
        if (bytes == ERROR_CONTENT) {
            return errorHash;
        }
        int hash = 0;
        if (bytes != null) {
            for (final byte b : bytes) {
                hash += b & 0xff;
                hash += hash << 10;
                hash ^= hash >>> 6;
            }
            hash += hash << 3;
            hash ^= hash >>> 11;
            hash += hash << 15;
        }
        return String.format("%08x", hash);
    }

    public static void clear() throws IOException {
        if (Files.exists(DIR)) {
            Files.walkFileTree(DIR, DELETE);
        }
    }

    public static Path path(final String inner) {
        return DIR.resolve(inner);
    }

    public static List<String> codePoints(final String string) {
        //noinspection SimplifyStreamApiCallChains
        return string.codePoints().mapToObj(Character::toString).collect(Collectors.toUnmodifiableList());
    }


    public Map<String, byte[]> randomFiles(final int n, final int maxSize, final Path dir) throws IOException {
        Files.createDirectories(dir);
        final Map<String, byte[]> result = new HashMap<>();
        for (int i = 0; i < n; i++) {
            final String name = randomFileName();
            try {
                final Path file = dir.resolve(name);
                final byte[] bytes = new byte[maxSize / 2 + random.nextInt(maxSize / 2 + 1)];
                random.nextBytes(bytes);
                Files.write(file, bytes);
                result.put(file.toString(), bytes);
            } catch (final InvalidPathException ignore) {
                result.put(dir + File.separator + name, ERROR_CONTENT);
            }
        }
        return result;
    }

    public String randomFileName() {
        return random.ints(30, 0, alphabet.size())
                .mapToObj(alphabet::get)
                .collect(Collectors.joining());
    }

    public Map<String, byte[]> randomDirs(final int n, final int d, final int maxL, final Path dir) throws IOException {
        final Map<String, byte[]> result = randomFiles(random.nextInt(n + 1), maxL, dir);
        if (d > 0) {
            for (int i = random.nextInt(n + 1); i < n; i++) {
                result.putAll(randomDirs(n, d - 1, maxL, dir.resolve(randomFileName())));
            }
        }
        return result;
    }

    public void check(
            final Collection<String> inputs,
            final Map<String, byte[]> files,
            final String inputName,
            final String outputName
    ) {
        check(inputs, files, path(inputName), path(outputName));
    }

    private void check(
            final Collection<String> inputs,
            final Map<String, byte[]> files,
            final Path inputFile,
            final Path outputFile
    ) {
        WalkUtil.writeInput(inputs, inputFile);
        run(inputFile.toString(), outputFile.toString());
        checkOutput(files, outputFile);
    }

    protected static void writeInput(final Collection<String> inputs, final Path inputFile) {
        try {
            Files.write(inputFile, inputs);
        } catch (final IOException e) {
            throw new AssertionError("Cannot write input file " + inputFile);
        }
    }

    protected void checkOutput(final Map<String, byte[]> files, final Path outputFile) {
        final Map<String, String> hashes = files.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> hash(e.getValue())));
        try {
            for (final String line : Files.readAllLines(outputFile, StandardCharsets.UTF_8)) {
                final String[] parts = line.split(" ", 2);
                Assertions.assertEquals(2, parts.length, "Invalid line format\n" + line);
                Assertions.assertTrue(hashes.containsKey(parts[1]), "Unexpected file " + parts[1]);
                Assertions.assertEquals(hashes.remove(parts[1]), parts[0], "Wrong hash for " + parts[1]);
            }
        } catch (final IOException e) {
            throw new AssertionError("Cannot read output file " + outputFile);
        }

        Assertions.assertTrue(hashes.isEmpty(), "Some files missing: \n    " + String.join("\n    ", hashes.keySet()));
    }

    public void run(final String... args) {
        final Method method;
        final Class<?> cut = BaseTest.loadClass();
        try {
            method = cut.getMethod("main", String[].class);
        } catch (final NoSuchMethodException e) {
            throw new AssertionError("Cannot find method main(String[]) of " + cut, e);
        }
        try {
            method.invoke(null, (Object) args);
        } catch (final IllegalAccessException e) {
            throw new AssertionError("Cannot call main(String[]) of " + cut, e);
        } catch (final InvocationTargetException e) {
            throw new AssertionError("Error thrown", e.getCause());
        }
    }
}
