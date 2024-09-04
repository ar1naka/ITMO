package info.kgeorgiy.java.advanced.mapper;

import info.kgeorgiy.java.advanced.iterative.NewScalarIP;
import info.kgeorgiy.java.advanced.iterative.NewScalarIPTest;

import org.junit.jupiter.api.AfterEach;

import java.util.List;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class NewScalarMapperTest extends NewScalarIPTest<NewScalarIP> {
    public NewScalarMapperTest() {
        factors = List.of(1, 2, 5, 10, 20);
    }

    @Override
    protected NewScalarIP createInstance(final int threads) {
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
