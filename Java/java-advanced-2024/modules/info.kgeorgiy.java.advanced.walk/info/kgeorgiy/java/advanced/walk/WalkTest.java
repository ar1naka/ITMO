package info.kgeorgiy.java.advanced.walk;

import info.kgeorgiy.java.advanced.base.BaseTest;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Tests for easy version
 * of <a href="https://www.kgeorgiy.info/courses/java-advanced/homeworks.html#homework-walk">Walk</a> homework
 * for <a href="https://www.kgeorgiy.info/courses/java-advanced/">Java Advanced</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class WalkTest extends BaseTest {
    protected static final byte[] DIR_CONTENT = new byte[]{0, 1, 2, 3};

    protected final WalkUtil util = new WalkUtil();

    public WalkTest() {
    }

    @BeforeAll
    public static void beforeAll() throws IOException {
        WalkUtil.clear();
    }

    protected Path testDir() {
        return WalkUtil.path(testName);
    }

    @Test
    public void test10_oneEmptyFile() throws IOException {
        testRandomFiles(1, 0);
    }

    @Test
    public void test15_tenEmptyFiles() throws IOException {
        testRandomFiles(10, 0);
    }

    @Test
    public void test20_smallRandomFiles() throws IOException {
        testRandomFiles(10, 100);
    }

    @Test
    public void test21_mediumRandomFiles() throws IOException {
        testRandomFiles(10, 10_000);
    }

    @Test
    public void test22_largeRandomFiles() throws IOException {
        testRandomFiles(10, 1_000_000);
    }

    @Test
    public void test23_veryLargeFile() throws IOException {
        testRandomFiles(1, 10_000_000);
    }

    @Test
    public void test30_missingFiles() throws IOException {
        new TestCase().randomFiles(3, 10)
                .error(util.randomFileName())
                .error(util.randomFileName())
                .error(util.randomFileName())
                .test();
    }

    @Test
    public void test31_invalidFiles() throws IOException {
        final String upperPath = System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("win")
                ? "//.."
                : "/../\0/...";
        new TestCase().randomFiles(3, 10)
                .error(upperPath)
                .error(upperPath + "2")
                .test();
    }

    @Test
    public void test40_errorReading() throws IOException {
        final Path root = WalkUtil.path("");
        new TestCase().randomFiles(3, 10)
                .error(root + "..")
                .error(root + "@")
                .test();
    }

    @Test
    public void test45_partiallyMissingFiles() throws IOException {
        new TestCase()
                .error("no-such-file-1")
                .randomFiles(10, 100)
                .error("no-such-file-2")
                .randomFiles(10, 100)
                .error("no-such-file-3")
                .randomFiles(10, 100)
                .test();
    }

    @Test
    public void test46_filesAndDirs() throws IOException {
        new TestCase()
                .randomFiles(10, 100)
                .randomDirs(10)
                .test();
    }

    @Test
    public void test50_whitespaceSupport() throws IOException {
        testAlphabet(10, 100, " \u00a0_");
    }

    @Test
    public void test51_dirSupport() throws IOException {
        missingDirs(testName + File.separator + testName + ".out");
    }

    @Test
    public void test52_dirsSupport() throws IOException {
        missingDirs(testName + File.separator + "111" + File.separator + testName + ".out");
    }

    @Test
    public void test53_dirsHash() throws IOException {
        new TestCase().randomDirs(10).test();
    }

    @Test
    public void test55_chineseSupport() throws IOException {
        testAlphabet(10, 100, "\u8acb\u554f\u4f60\u7684\u7a0b\u5e8f\u652f\u6301\u4e2d\u570b");
    }

    @Test
    public void test56_emojiSupport() throws IOException {
        testAlphabet(10, 100, "\uD83D\uDC81\uD83D\uDC4C\uD83C\uDF8D\uD83D\uDE0D\uD83D\uDE08");
    }

    @Test
    public void test60_noInput() {
        util.run(util.randomFileName(), util.randomFileName());
    }

    @Test
    public void test61_invalidInput() {
        util.run("/", util.randomFileName());
        util.run("\0*", util.randomFileName());
    }

    @Test
    public void test62_invalidOutput() throws IOException {
        final String input = createEmptyFile(testName);
        util.run(input, WalkUtil.path("").toString());
        util.run(input, "\0*");
        final String file = createEmptyFile(testName);
        util.run(input, file + File.separator + util.randomFileName());
    }

    @Test
    public void test63_invalidFiles() throws IOException {
        testAlphabet(1, 10, "\0\\*");
    }

    @Test
    public void test70_singleArgument() throws IOException {
        util.run(createEmptyFile(testName));
    }

    @Test
    public void test71_noArguments() {
        util.run();
    }

    @Test
    public void test72_nullArguments() {
        util.run((String[]) null);
    }

    @Test
    public void test73_firstArgumentNull() {
        util.run(null, "");
    }

    @Test
    public void test74_secondArgumentNull() throws IOException {
        util.run(createEmptyFile(testName), null);
    }

    @Test
    public void test75_threeArguments() throws IOException {
        util.run(createEmptyFile("a"), createEmptyFile("b"), "c");
    }

    private void testRandomFiles(final int n, final int maxSize) throws IOException {
        new TestCase().randomFiles(n, maxSize).test();
    }

    private void missingDirs(final String output) throws IOException {
        final TestCase testCase = new TestCase().randomFiles(10, 100);
        check(testCase.files.keySet(), testCase.files, testName + ".in", output);
    }

    private void testAlphabet(final int n, final int maxSize, final String alphabet) throws IOException {
        util.alphabet = WalkUtil.codePoints(alphabet);
        testRandomFiles(n, maxSize);
        util.alphabet = WalkUtil.ENGLISH_DIGITS;
    }

    protected String randomDir() throws IOException {
        final Path dir = testDir();
        final Path path = dir.resolve(util.randomFileName());
        Files.createDirectories(path);
        return path.toString();
    }

    private static String createEmptyFile(final String name) throws IOException {
        final Path input = WalkUtil.path(name);
        Files.write(input, new byte[0]);
        return input.toString();
    }

    protected void check(
            final Collection<String> inputs,
            final Map<String, byte[]> resolved,
            final String inputFile,
            final String outputFile
    ) {
        util.check(inputs, resolved, inputFile, outputFile);
    }

    protected final class TestCase {
        private final LinkedHashMap<String, byte[]> files = new LinkedHashMap<>();

        TestCase() {
        }

        private TestCase error(final String path) {
            files.put(path, WalkUtil.ERROR_CONTENT);
            return this;
        }

        void test() {
            final Map<String, byte[]> resolved = files.entrySet().stream().collect(Collectors.toMap(
                    Map.Entry::getKey,
                    e -> DIR_CONTENT == e.getValue() ? WalkUtil.ERROR_CONTENT : e.getValue()
            ));
            test(resolved.keySet(), resolved);
        }

        void testRecursive(final String... inputs) {
            final Map<String, byte[]> resolved = files.entrySet().stream()
                    .filter(e -> DIR_CONTENT != e.getValue())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            test(inputs.length == 0 ? files.keySet() : List.of(inputs), resolved);
        }

        private void test(final Collection<String> inputs, final Map<String, byte[]> resolved) {
            check(inputs, resolved, testName + ".in", testName + ".out");
        }

        TestCase randomFiles(final int n, final int maxSize) throws IOException {
            files.putAll(util.randomFiles(n, maxSize, testDir()));
            return this;
        }

        TestCase randomDirs(final int n) throws IOException {
            for (int i = 0; i < n; i++) {
                files.put(randomDir(), DIR_CONTENT);
            }
            return this;
        }

        TestCase files(final Map<String, byte[]> files) {
            this.files.putAll(files);
            return this;
        }
    }
}
