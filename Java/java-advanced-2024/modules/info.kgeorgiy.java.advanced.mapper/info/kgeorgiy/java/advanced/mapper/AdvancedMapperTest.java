package info.kgeorgiy.java.advanced.mapper;

import info.kgeorgiy.java.advanced.iterative.AdvancedIP;
import info.kgeorgiy.java.advanced.iterative.AdvancedIPTest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class AdvancedMapperTest extends AdvancedIPTest {
    public AdvancedMapperTest() {
        factors = List.of(1, 2, 5, 10);
    }

    @Test @Override
    public void test10_sleepPerformance() throws InterruptedException {
        new ScalarMapperTest().test10_sleepPerformance();
    }

    @Override
    protected AdvancedIP createInstance(final int threads) {
        return TestHelper.instance(threads);
    }

    @Override
    protected int getSubtasks(final int threads, final int totalThreads) {
        return TestHelper.subtasks(totalThreads);
    }

    @AfterAll
    public static void close() {
        TestHelper.close();
    }
}
