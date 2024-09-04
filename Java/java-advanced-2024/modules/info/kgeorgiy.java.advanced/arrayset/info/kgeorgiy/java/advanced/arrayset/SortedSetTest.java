package info.kgeorgiy.java.advanced.arrayset;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.java.quickcheck.generator.CombinedGeneratorsIterables.someLists;
import static net.java.quickcheck.generator.PrimitiveGenerators.integers;

/**
 * Tests for easy version
 * of <a href="https://www.kgeorgiy.info/courses/java-advanced/homeworks.html#homework-arrayset">ArraySet</a> homework
 * for <a href="https://www.kgeorgiy.info/courses/java-advanced/">Java Advanced</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class SortedSetTest extends BaseSetTest {
    public static final int PERFORMANCE_SIZE = 300_000;
    public static final int PERFORMANCE_TIME = 10_000;

    public SortedSetTest() {
    }

    @Test
    public void test01_implements() {
        assertImplements(SortedSet.class);
    }

    protected static void assertImplements(final Class<?> type) {
        checkImplements(type, loadClass());
    }

    @Test
    public void test02_constructors() {
        final Class<?> token = loadClass();

        checkConstructor("default constructor", token);
        checkConstructor("constructor out of Collection", token, Collection.class);
        checkConstructor("constructor out of Collection and Comparator", token, Collection.class, Comparator.class);
    }

    @Test
    public void test03_empty() {
        testEmpty(empty());
        testEmpty(set(List.of()));
        testEmpty(set(List.of(), Comparator.<Integer>naturalOrder()));
        testEmpty(set(List.of(), null));
    }

    private static void testEmpty(final Collection<?> set) {
        Assertions.assertEquals(0, set.size(), "Empty set size should be zero");
        Assertions.assertTrue(set.isEmpty(), "Empty set should be empty");
        Assertions.assertEquals(0, (Object) set.toArray().length, "toArray for empty set should return empty array");
    }

    @Test
    public void test03_naturalOrder() {
        for (final List<Integer> elements : someLists(integers())) {
            assertEq("elements = " + elements, treeSet(elements), set(elements));
        }
    }

    @Test
    public void test04_externalOrder() {
        this.<SortedSet<Integer>>test(BaseSetTest::assertEq);
    }

    @Test
    public void test05_constructorPerformance() {
        performance("constructor", SortedSetTest::performanceSet);
    }

    @Test
    public void test10_contains() {
        this.<SortedSet<Integer>, Boolean>testGet("contains(%s)", SortedSet::contains);
    }

    @Test
    public void test11_containsPerformance() {
        performance("contains", set -> {
            for (final Integer element : set) {
                Assertions.assertTrue(set.contains(element));
            }
        });
    }

    @Test
    public void test12_containsAll() {
        test(pair -> Assertions.assertTrue(pair.tested.containsAll(pair.elements),
                                           "set should contains() all elements " + " " + pair.context));
        testGet("containsAll(List.of(%s))", (set, element) -> set.containsAll(Arrays.asList(element, element)));
    }

    protected static void performance(final String description, final Runnable runnable) {
        runnable.run();

        final long start = System.currentTimeMillis();
        runnable.run();
        final long time = System.currentTimeMillis() - start;
        System.err.println("    " + description + " done in " + time + "ms");
        Assertions.assertTrue(time < PERFORMANCE_TIME, description + " works too slow");
    }

    protected static void performance(final String description, final Consumer<SortedSet<Integer>> action) {
        performance(description, () -> action.accept(performanceSet()));
    }

    protected static SortedSet<Integer> performanceSet() {
        return set(performanceList(PERFORMANCE_SIZE), Comparator.nullsFirst(Comparator.naturalOrder()));
    }

    protected static List<Integer> performanceList(final int size) {
        return new Random().ints().limit(size).boxed().distinct().collect(Collectors.toList());
    }

    @Test
    public void test21_comparator() {
        this.<SortedSet<Integer>, Comparator<? super Integer>>testGet("comparator()", SortedSet::comparator);
        for (final List<Integer> elements : someLists(integers())) {
            Assertions.assertNull(set(elements).comparator(), "comparator() should return null for default order");
        }
    }

    @Test
    public void test22_headSet() {
        this.<SortedSet<Integer>, SortedSet<Integer>>testGet("headSet(%s)", SortedSet::headSet);
    }

    @Test
    public void test23_tailSet() {
        this.<SortedSet<Integer>, SortedSet<Integer>>testGet("tailSet(%s)", SortedSet::tailSet);
    }

    @Test
    public void test24_subSet() {
        this.<SortedSet<Integer>>testPairs((pair, from, to) -> pair.testGet("subSet(" + from + ", " + to + ") ", s -> s.subSet(from, to)));
    }

    @Test
    public void test25_first() {
        this.<SortedSet<Integer>, Integer>testGet("first()", SortedSet::first);
    }

    @Test
    public void test26_last() {
        this.<SortedSet<Integer>, Integer>testGet("last()", SortedSet::last);
    }

    @Test
    public void test27_iterator() {
        test(pair -> {
            testIterator(pair);
            testIterator(pair);
        });
    }

    private void testIterator(final SetPair<Integer, Collection<Integer>> pair) {
        final Iterator<Integer> model = pair.model.iterator();
        final Iterator<Integer> tested = pair.tested.iterator();
        while (model.hasNext()) {
            Assertions.assertTrue(tested.hasNext(), "hasNext()");
            Assertions.assertSame(model.next(), tested.next(), "next()");
        }
        Assertions.assertFalse(tested.hasNext(), "hasNext()");
    }

    @Override
    protected List<Integer> values(final Comparator<? super Integer> comparator, final Collection<Integer> elements) {
        return super.values(comparator, elements.stream().flatMap(e -> e == null ? Stream.of((Integer) null) : Stream.of(e, e - 1, e + 1)).collect(Collectors.toList()));
    }
}
