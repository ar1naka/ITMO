package info.kgeorgiy.java.advanced.mapper;

import info.kgeorgiy.java.advanced.iterative.NewListIP;
import info.kgeorgiy.java.advanced.iterative.NewListIPTest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class NewListMapperTest extends NewListIPTest<NewListIP> {
    public NewListMapperTest() {
        factors = List.of(1, 2, 5, 10);
    }

    @Test @Override
    public void test10_sleepPerformance() throws InterruptedException {
        new ScalarMapperTest().test10_sleepPerformance();
    }

    @Override
    protected NewListIP createInstance(final int threads) {
        return TestHelper.instance(threads);
    }

    @Override
    protected int getSubtasks(final int threads, final int totalThreads) {
        return TestHelper.subtasks(totalThreads);
    }

    @AfterEach
    public void close() {
        TestHelper.close();
    }
}
