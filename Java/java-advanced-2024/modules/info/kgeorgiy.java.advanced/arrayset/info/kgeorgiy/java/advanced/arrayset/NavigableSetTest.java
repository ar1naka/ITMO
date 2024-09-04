package info.kgeorgiy.java.advanced.arrayset;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Tests for hard version
 * of <a href="https://www.kgeorgiy.info/courses/java-advanced/homeworks.html#homework-arrayset">ArraySet</a> homework
 * for <a href="https://www.kgeorgiy.info/courses/java-advanced/">Java Advanced</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class NavigableSetTest extends SortedSetTest {
    protected static final List<NamedComparator<Integer>> MORE_COMPARATORS = Arrays.asList(
            null,
            comparator("AllEqual.nullsFirst", Comparator.nullsFirst(Comparator.comparingInt(v -> 0))),
            comparator("Mod1000.nullsLast", Comparator.nullsLast(Comparator.comparingInt(v -> v % 1000)))
    );

    @BeforeAll
    public static void beforeAll() {
        NAMED_COMPARATORS.addAll(MORE_COMPARATORS);
    }

    @AfterAll
    public static void afterAll() {
        NAMED_COMPARATORS.removeAll(MORE_COMPARATORS);
    }

    public static final List<Integer> TEST_DESCENDING_SET_DATA = List.of(10, 20, 30, 40);

    public NavigableSetTest() {
    }

    @Test
    @Override
    public void test01_implements() {
        assertImplements(NavigableSet.class);
    }

    @Test
    public void test31_lower() {
        testGetN("lower(%s)", NavigableSet::lower);
    }

    @Test
    public void test32_ceiling() {
        testGetN("ceiling(%s)", NavigableSet::ceiling);
    }

    @Test
    public void test33_higher() {
        testGetN("higher(%s)", NavigableSet::higher);
    }

    @Test
    public void test34_floor() {
        testGetN("floor(%s)", NavigableSet::floor);
    }

    @Test
    public void test35_navigableTailSet() {
        testGetN("tailSet(%s, true)", (s, e) -> s.tailSet(e, true));
        testGetN("tailSet(%s, false)", (s, e) -> s.tailSet(e, false));
    }

    @Test
    public void test36_navigableHeadSet() {
        testGetN("headSet(%s, true)", (s, e) -> s.headSet(e, true));
        testGetN("headSet(%s, false)", (s, e) -> s.headSet(e, false));
    }

    @Test
    public void test37_navigableSubSet() {
        testPairsN((pair, from, to) -> IntStream.range(0, 4).forEach(i -> pair.testGet(
                String.format("subSet(%d, %b, %d, %b)", from, i % 2 == 1, to, i / 2 == 1),
                set -> set.subSet(from, i % 2 == 1, to, i / 2 == 1)
        )));
    }

    @Test
    public void test38_descendingSet() {
        testDescendingSet(TEST_DESCENDING_SET_DATA, comparator("unsigned", Integer::compareUnsigned));
    }

    protected void testDescendingSet(final List<Integer> data, final Comparator<Integer> comparator) {
        testDescendingSet(new SetPair<Integer, NavigableSet<Integer>>(data, comparator, this::values));
    }

    protected static void testDescendingSet(final SetPair<Integer, NavigableSet<Integer>> basePair) {
        final SetPair<Integer, NavigableSet<Integer>> pair = basePair.transform(NavigableSet::descendingSet);

        pair.testGet("toArray()", NavigableSetTest::toArray);
        pair.testGet("size()", NavigableSet::size);
        pair.testGet("first()", SortedSet::first);
        pair.testGet("last()", SortedSet::last);
        pair.testGet("descendingIterator().next()", s -> s.descendingIterator().next());

        pair.testGet("floor(%s)", NavigableSet::floor);
        pair.testGet("lower(%s)", NavigableSet::lower);
        pair.testGet("ceiling(%s)", NavigableSet::ceiling);
        pair.testGet("higher(%s)", NavigableSet::higher);

        pair.testGet("headSet(%s).size()", (s, e) -> s.headSet(e).size());
        pair.testGet("tailSet(%s).size()", (s, e) -> s.tailSet(e).size());

        pair.testGet("descendingSet().toArray()", s -> toArray(s.descendingSet()));
    }

    @Test
    public void test41_immutable() {
        final NavigableSet<Integer> set = set(List.of(1));
        checkUnsupported("add", () -> set.add(1));
        checkUnsupported("addAll", () -> set.addAll(List.of(1, 2)));
        checkUnsupported("clear", set::clear);
        checkUnsupported("iterator.remove", () -> {
            final Iterator<Integer> iterator = set.iterator();
            iterator.next();
            iterator.remove();
        });
        checkUnsupported("remove", () -> set.remove(1));
        checkUnsupported("removeAll", () -> set.removeAll(List.of(1)));
        checkUnsupported("retainAll", () -> set.retainAll(List.of(0)));
        checkUnsupported("pollFirst", set::pollFirst);
        checkUnsupported("pollLast", set::pollLast);
    }

    protected static void checkUnsupported(final String method, final Runnable command) {
        Assertions.assertThrows(
                UnsupportedOperationException.class,
                command::run,
                "Method '" + method + "'"
        );
    }

    @Test
    public void test42_containsAllPerformance() {
        performance("containsAll", set -> Assertions.assertTrue(set.containsAll(toList(set))));
    }

    @Test
    public void test43_tailSetPerformance() {
        performance(
                "tailSet",
                set -> set.forEach(element -> Assertions.assertTrue(set.tailSet(element).contains(element)))
        );
    }

    @Test
    public void test44_headSetPerformance() {
        performance(
                "headSet",
                set -> set.forEach(element -> Assertions.assertFalse(set.headSet(element).contains(element)))
        );
    }

    private void testPairsN(final TestPair<Integer, NavigableSet<Integer>> testCase) {
        testPairs(testCase);
    }

    protected <R> void testGetN(final String name, final BiFunction<NavigableSet<Integer>, Integer, R> testCase) {
        testGet(name, testCase);
    }

    @Override
    protected <S extends Collection<Integer>> SetPair<Integer, S> pair(
            final List<Integer> elements,
            final NamedComparator<? super Integer> comparator
    ) {
        return super.pair(
                comparator != null && comparator.toString().contains("nulls")
                        ? elements.stream().map(v -> v % 100 == 0 ? null : v).collect(Collectors.toList())
                        : elements,
                comparator
        );
    }
}
