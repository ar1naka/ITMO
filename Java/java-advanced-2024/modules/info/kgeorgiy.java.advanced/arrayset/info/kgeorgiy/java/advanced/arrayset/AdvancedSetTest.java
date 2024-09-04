package info.kgeorgiy.java.advanced.arrayset;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Tests for advanced version
 * of <a href="https://www.kgeorgiy.info/courses/java-advanced/homeworks.html#homework-arrayset">ArraySet</a> homework
 * for <a href="https://www.kgeorgiy.info/courses/java-advanced/">Java Advanced</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class AdvancedSetTest extends NavigableSetTest {
    public AdvancedSetTest() {
    }

    @Test
    @Override
    public void test01_implements() {
        super.test01_implements();
        assertImplements(List.class);
    }

    @Test
    public void test51_copySource() {
        final List<Integer> data = List.of(1, 10, 50);

        @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
        final List<Integer> list = new ArrayList<>(data);

        final TreeSet<Integer> set = treeSet(data);
        final SortedSet<Integer> integers = set(data);
        assertEq("initial", integers, set);
        list.set(1, 20);
        assertEq("mutated", integers, set);
    }

    @Test
    public void test52_immutableSource() {
        final List<Integer> data = List.of(1, 100, 10);
        assertEq("initial", treeSet(data), set(data));
    }

    @Test
    public void test53_descendingSet() {
        testDescendingSet(TEST_DESCENDING_SET_DATA, null);
    }

    @Test
    public void test54_descendingSetPerformance() {
        testDescendingSetPerformance(10, comparator("unsigned", Integer::compareUnsigned), 300);
        testDescendingSetPerformance(10, null, 300);
        testDescendingSetPerformance(PERFORMANCE_SIZE / 250, comparator("plane", Integer::compare), 2);
        testDescendingSetPerformance(PERFORMANCE_SIZE / 250, null, 2);
    }

    private void testDescendingSetPerformance(final int size, final NamedComparator<Integer> comparator, final int iterations) {
        final SetPair<Integer, NavigableSet<Integer>> pair = this.<NavigableSet<Integer>>pair(performanceList(size), comparator)
                .transformTested((NavigableSet<Integer> model) -> Stream.iterate(model, NavigableSet::descendingSet)
                        .skip(PERFORMANCE_SIZE & -2).findFirst().orElseThrow());
        performance(
                "descendingSet",
                () -> {
                    for (int i = 0; i < iterations; i++) {
                        testDescendingSet(pair);
                    }
                }
        );
    }

    @Override
    protected List<Integer> values(final Comparator<? super Integer> comparator, final Collection<Integer> elements) {
        return super.values(
                comparator,
                comparator == null
                        ? elements
                        : Stream.concat(elements.stream(), Stream.of(null, null)).collect(Collectors.toList())
        );
    }

    @Test
    public void test61_get() {
        testList(pair -> IntStream.range(0, pair.model.size())
                .forEach(index -> pair.testGet("get(%d)", List::get, index)));
    }

    @Test
    public void test62_indexOf() {
        testIndexOf("indexOf(%d)", List::indexOf);
    }

    @Test
    public void test63_lastIndexOf() {
        testIndexOf("lastIndexOf(%d)", List::lastIndexOf);
    }

    private void testIndexOf(final String format, final BiFunction<List<Integer>, Integer, Integer> method) {
        testList(pair -> {
            IntStream.range(0, pair.model.size())
                    .forEach(index -> pair.testGet(format, method, pair.model.get(index), index));
            if (pair.comparator != null) {
                Stream.of(Integer.MAX_VALUE - 10, Integer.MIN_VALUE + 10, null)
                        .forEach(value -> pair.testGet(format, method, value, Math.max(Collections.binarySearch(pair.model, value, pair.comparator), -1)));
            }
        });
    }

    @SuppressWarnings("unchecked")
    protected void testList(final TestCase<Integer, List<Integer>> testCase) {
        System.err.println("    Correctness");
        final TestCase<Integer, Collection<Integer>> test = pair -> testCase.test(pair.transform(ArrayList::new, t -> (List<Integer>) t));
        test(test);
        System.err.println("    Performance");
        performance("Performance", set -> test.test(pair(new ArrayList<>(set), comparator("", (Comparator<Integer>) set.comparator()))));
    }
}
