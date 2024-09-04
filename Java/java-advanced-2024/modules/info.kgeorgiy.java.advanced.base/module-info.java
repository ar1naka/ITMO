/**
 * Base test classes for <a href="https://www.kgeorgiy.info/courses/java-advanced/">Java Advanced</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
module info.kgeorgiy.java.advanced.base {
    requires transitive org.junit.jupiter.api;
    requires org.junit.platform.launcher;

    exports info.kgeorgiy.java.advanced.base;
    opens info.kgeorgiy.java.advanced.base to org.junit.platform.launcher;
}