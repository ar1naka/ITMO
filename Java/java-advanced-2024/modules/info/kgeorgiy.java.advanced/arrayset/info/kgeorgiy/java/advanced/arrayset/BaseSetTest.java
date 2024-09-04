package info.kgeorgiy.java.advanced.arrayset;

import info.kgeorgiy.java.advanced.base.BaseTest;

import org.junit.jupiter.api.Assertions;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static net.java.quickcheck.generator.CombinedGenerators.lists;
import static net.java.quickcheck.generator.CombinedGeneratorsIterables.someExcludeValues;
import static net.java.quickcheck.generator.CombinedGeneratorsIterables.somePairs;
import static net.java.quickcheck.generator.PrimitiveGenerators.fixedValues;
import static net.java.quickcheck.generator.PrimitiveGenerators.integers;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public abstract class BaseSetTest extends BaseTest {
    protected static final List<NamedComparator<Integer>> NAMED_COMPARATORS = new ArrayList<>(Arrays.asList(
            comparator("Natural order", Integer::compare),
            comparator("Reverse order", Comparator.comparingInt(Integer::intValue).reversed()),
            comparator("Div 128", Comparator.comparingInt(i -> i >> 7)),
            comparator("Mod 1024", Comparator.comparingInt(i -> i & 1023)),
            comparator("Even first", Comparator.<Integer>comparingInt(i -> i % 2).thenComparing(Integer::intValue)),
            comparator("All equal", Comparator.comparingInt(i -> 0)),
            null
    ));

    protected BaseSetTest() {
    }

    protected static <E> List<E> toList(final Collection<E> set) {
        return new ArrayList<>(set);
    }

    protected static <E extends Number> List<Number> toArray(final Collection<E> set) {
        return Arrays.asList(set.toArray(new Number[0]));
    }

    private static <E, S extends Collection<E>> S treeSet(final List<E> elements, final Comparator<? super E> comparator) {
        final TreeSet<E> set = new TreeSet<>(comparator);
        set.addAll(elements);
        return cast(set);
    }

    protected static <E, S extends Collection<E>> S set(final List<E> elements, final Comparator<? super E> comparator) {
        return create(new Object[]{elements, comparator}, Collection.class, Comparator.class);
    }

    protected static <E, S extends Collection<E>> S treeSet(final List<E> elements) {
        return cast(new TreeSet<>(elements));
    }

    protected static <E, S extends SortedSet<E>> S set(final List<Integer> elements) {
        return create(new Object[]{elements}, Collection.class);
    }

    protected static <E extends Number, S extends SortedSet<E>> void assertEq(final SetPair<E, S> pair) {
        assertEq(pair.context, pair.model, pair.tested);
    }

    protected static <E extends Number, S extends SortedSet<E>> void assertEq(final String context, final S model, final S tested) {
        Assertions.assertEquals(toList(model), toList(tested), "invalid element order " + context);
        Assertions.assertEquals(toArray(model), toArray(tested), "invalid toArray " + context);
        Assertions.assertEquals(model.size(), (Object) tested.size(), "invalid set size " + context);
        Assertions.assertEquals(model.isEmpty(), tested.isEmpty(), "invalid isEmpty " + context);
        Assertions.assertSame(model.comparator(), tested.comparator(), "invalid comparator " + context);
    }

    protected static <E, S extends Collection<E>> S empty() {
        return create(new Object[]{});
    }

    @SuppressWarnings("unchecked")
    private static <E, S extends Collection<E>> S cast(final Object set) {
        return (S) set;
    }

    private static <E, S extends Collection<E>> S create(final Object[] params, final Class<?>... types) {
        try {
            return cast(loadClass().getConstructor(types).newInstance(params));
        } catch (final Exception e) {
            throw new AssertionError("Instantiation error", e);
        }
    }

    protected List<Integer> values(final Comparator<? super Integer> comparator, final Collection<Integer> elements) {
        return Stream.of(
                elements.stream(),
                Stream.of(0, Integer.MAX_VALUE, Integer.MIN_VALUE),
                StreamSupport.stream(someExcludeValues(integers(), elements).spliterator(), false)
        ).flatMap(Function.identity()).collect(Collectors.toList());
    }

    protected interface TestCase<E, S extends Collection<E>> {
        void test(SetPair<E, S> pair);
    }

    protected interface TestPair<E, S extends Collection<E>> {
        void test(SetPair<E, S> pair, E first, E second);
    }

    protected <S extends Collection<Integer>> void test(final TestCase<Integer, S> testCase) {
        somePairs(lists(integers()), fixedValues(NAMED_COMPARATORS)).forEach(
                pair -> testCase.test(pair(pair.getFirst(), pair.getSecond())));
    }

    protected <S extends Collection<Integer>> SetPair<Integer, S> pair(
            final List<Integer> elements,
            final NamedComparator<? super Integer> comparator
    ) {
        return new SetPair<Integer, S>(elements, comparator, this::values);
    }

    protected <S extends Collection<Integer>> void testPairs(final TestPair<Integer, S> testCase) {
        this.<S>test(pair -> pair.testPairs(testCase));
    }

    static NamedComparator<Integer> comparator(final String name, final Comparator<Integer> comparator) {
        final Comparator<Integer> cmp = (name.hashCode() & 1) == 0
                ? Comparator.nullsFirst(comparator)
                : Comparator.nullsLast(comparator);
        return new NamedComparator<>(name, cmp);
    }

    protected <S extends Collection<Integer>, R> void testGet(final String name, final BiFunction<S, Integer, R> method) {
        this.<S>test(pair -> pair.testGet(name, method));
    }

    protected <S extends Collection<Integer>, R> void testGet(final String name, final Function<S, R> method) {
        this.<S>test(pair -> pair.testGet(name, method));
    }

    /**
     * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
     */
    protected static final class NamedComparator<T> implements Comparator<T> {
        private final String name;
        private final Comparator<T> comparator;

        private NamedComparator(final String name, final Comparator<T> comparator) {
            this.name = Objects.requireNonNull(name);
            this.comparator = Objects.requireNonNull(comparator);
        }

        @Override
        public int compare(final T o1, final T o2) {
            return comparator.compare(o1, o2);
        }

        @Override
        public String toString() {
            return name;
        }
    }

    protected static class SetPair<E, S extends Collection<E>> {
        protected final List<E> elements;
        final Comparator<? super E> comparator;
        final S model;
        final S tested;
        final String context;
        private final BiFunction<Comparator<? super E>, Collection<E>, Collection<E>> values;

        private SetPair(final List<E> elements, final Comparator<? super E> comparator, final S model, final S tested, final String context, final BiFunction<Comparator<? super E>, Collection<E>, Collection<E>> values) {
            this.elements = elements;
            this.comparator = comparator;
            this.model = model;
            this.tested = tested;
            this.context = context;
            this.values = values;
        }

        public SetPair(final List<E> elements, final Comparator<? super E> comparator, final BiFunction<Comparator<? super E>, Collection<E>, Collection<E>> values) {
            this(
                    elements,
                    comparator,
                    treeSet(elements, comparator),
                    set(elements, comparator),
                    String.format("[comparator = %s, elements = %s]", comparator, elements),
                    values
            );
        }

        protected <R> void testGet(final String format, final BiFunction<S, E, R> method) {
            values().forEach(element -> testGet(format, method, element));
        }

        protected <R, V> void testGet(final String format, final BiFunction<S, V, R> method, final V value) {
            testGet(format, method, value, method.apply(model, value));
        }

        protected <R, V> void testGet(
                final String format,
                final BiFunction<S, V, R> method,
                final V value,
                final R expected
        ) {
            final R actual = method.apply(tested, value);
            Assertions.assertEquals(expected, actual, () -> String.format(format, value) + " " + context);
            if (expected instanceof SortedSet) {
                Assertions.assertEquals(
                        ((SortedSet<?>) expected).comparator(),
                        ((SortedSet<?>) actual).comparator(),
                        () -> String.format(format, value) + " " + context + " .comparator()"
                );
            }
        }

        protected Collection<E> values() {
            return values.apply(comparator, elements);
        }

        protected <R> void testGet(final String description, final Function<S, R> method) {
            try {
                final R expected = method.apply(model);
                final R actual = Assertions.assertDoesNotThrow(
                        () -> method.apply(tested),
                        () -> description + context + ": Unexpected exception "
                );
                Assertions.assertEquals(expected, actual, description + context);
            } catch (final RuntimeException ee) {
                Assertions.assertThrowsExactly(
                        ee.getClass(),
                        () -> method.apply(tested),
                        () -> description + context + ": " + ee.getClass().getName() + " expected " + ee.getMessage()
                );
            }
        }

        public <R extends Collection<E>> SetPair<E, R> transform(final Function<S, R> f) {
            return transform(f, f);
        }

        public <R extends Collection<E>> SetPair<E, R> transform(final Function<S, R> m, final Function<S, R> t) {
            return new SetPair<>(elements, comparator, m.apply(model), t.apply(tested), context, values);
        }

        public SetPair<E, S> transformTested(final Function<S, S> f) {
            return transform(Function.identity(), f);
        }

        private void testPairs(final TestPair<E, S> testCase) {
            somePairs(fixedValues(values()), fixedValues(values()))
                    .forEach(p -> testCase.test(this, p.getFirst(), p.getSecond()));
        }
    }
}
