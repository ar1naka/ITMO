/**
 * Tests for <a href="https://www.kgeorgiy.info/courses/java-advanced/homeworks.html#homework-concurrent">Iterative parallelism</a> homework
 * of <a href="https://www.kgeorgiy.info/courses/java-advanced/">Java Advanced</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
module info.kgeorgiy.java.advanced.iterative {
    requires transitive info.kgeorgiy.java.advanced.base;

    exports info.kgeorgiy.java.advanced.iterative;

    opens info.kgeorgiy.java.advanced.iterative to org.junit.platform.launcher;
}
