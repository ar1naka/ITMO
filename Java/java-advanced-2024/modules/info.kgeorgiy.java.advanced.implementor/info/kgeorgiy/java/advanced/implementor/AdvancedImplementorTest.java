package info.kgeorgiy.java.advanced.implementor;

import info.kgeorgiy.java.advanced.implementor.advanced.ArraysTest;
import info.kgeorgiy.java.advanced.implementor.advanced.CovariantReturns;
import info.kgeorgiy.java.advanced.implementor.advanced.Overridden;
import info.kgeorgiy.java.advanced.implementor.advanced.WeirdInheritance;
import info.kgeorgiy.java.advanced.implementor.advanced.standard.Collection;
import info.kgeorgiy.java.advanced.implementor.advanced.standard.Iterable;
import info.kgeorgiy.java.advanced.implementor.advanced.standard.Map;

import org.junit.jupiter.api.Test;

/**
 * Full tests for advanced version
 * of <a href="https://www.kgeorgiy.info/courses/java-advanced/homeworks.html#homework-implementor">Implementor</a> homework
 * for <a href="https://www.kgeorgiy.info/courses/java-advanced/">Java Advanced</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class AdvancedImplementorTest extends ClassImplementorTest {
    public AdvancedImplementorTest() {
    }

    @Test
    public void test41_overridden() {
        test(Overridden.OK, Overridden.FAILED);
    }

    @Test
    public void test44_weird() {
        testOk(WeirdInheritance.OK);
    }

    @Test
    public void test45_arrays() {
        testOk(ArraysTest.class);
    }

    @Test
    public void test46_covariantReturns() {
        testOk(CovariantReturns.OK);
    }

    @Test
    public void test47_collectionInterfaces() {
        testOk(Iterable.class, Collection.class, Map.class, Map.Entry.class);
    }
}
