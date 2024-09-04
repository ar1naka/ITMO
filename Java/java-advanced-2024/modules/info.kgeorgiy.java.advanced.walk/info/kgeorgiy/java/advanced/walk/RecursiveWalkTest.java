package info.kgeorgiy.java.advanced.walk;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Tests for hard version
 * of <a href="https://www.kgeorgiy.info/courses/java-advanced/homeworks.html#homework-walk">Walk</a> homework
 * for <a href="https://www.kgeorgiy.info/courses/java-advanced/">Java Advanced</a> course.
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class RecursiveWalkTest extends WalkTest {
    public RecursiveWalkTest() {
    }

    @Test
    @Override
    public void test46_filesAndDirs() throws IOException {
        new TestCase()
                .randomFiles(10, 10)
                .randomDirs(10)
                .testRecursive();
    }

    @Test
    @Override
    public void test53_dirsHash() throws IOException {
        new TestCase().randomDirs(10).testRecursive();
        new TestCase().testRecursive(testDir().toString());
    }

    @Test
    public void test70_singleRecursion() throws IOException {
        final Path root = testDir();
        new TestCase().files(util.randomDirs(3, 4, 100, root)).testRecursive(root.toString());
    }

    @Test
    public void test80_doubleRecursion() throws IOException {
        final Path root = testDir();
        final Path dir1 = root.resolve(util.randomFileName());
        final Path dir2 = root.resolve(util.randomFileName());
        final String from = dir1.toString();
        final String to = dir2.resolve("..").resolve(dir1.getFileName()).toString();

        final Map<String, byte[]> files = util.randomDirs(3, 4, 100, dir1);
        new TestCase()
                .files(files)
                .files(files.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().replace(from, to), Map.Entry::getValue)))
                .files(util.randomDirs(3, 4, 100, dir2))
                .testRecursive(from, dir2.toString(), to);
    }
}
