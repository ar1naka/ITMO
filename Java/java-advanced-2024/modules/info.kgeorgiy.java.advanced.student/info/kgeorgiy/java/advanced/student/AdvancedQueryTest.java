package info.kgeorgiy.java.advanced.student;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.BiFunction;

/**
 * Tests for advanced version
 * of <a href="https://www.kgeorgiy.info/courses/java-advanced/homeworks.html#homework-student">Student</a> homework
 * for <a href="https://www.kgeorgiy.info/courses/java-advanced/">Java Advanced</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class AdvancedQueryTest extends GroupQueryTest implements AdvancedQuery {
    private final AdvancedQuery db = createCUT();

    public AdvancedQueryTest() {
    }

    @Test
    public void test31_testGetMostPopularName() {
        test(this::getMostPopularName, db::getMostPopularName);
    }

    @Test
    public void test31_testGetLeastPopularName() {
        test(this::getLeastPopularName, db::getLeastPopularName);
    }

    @Test
    public void test41_testGetFirstNames() {
        testGet(this::getFirstNames, db::getFirstNames);
    }

    @Test
    public void test42_testGetLastNames() {
        testGet(this::getLastNames, db::getLastNames);
    }

    @Test
    public void test43_testGetGroups() {
        testGet(this::getGroups, db::getGroups);
    }

    @Test
    public void test35_testGetFullNames() {
        testGet(this::getFullNames, db::getFullNames);
    }

    private static <T> void testGet(
            final BiFunction<Collection<Student>, int[], List<T>> reference,
            final BiFunction<Collection<Student>, int[], List<T>> tested
    ) {
        for (int i = 0; i < STUDENTS.size(); i++) {
            final int[] args = RANDOM.ints(i, 0, STUDENTS.size()).toArray();
            Assertions.assertEquals(reference.apply(STUDENTS, args), tested.apply(STUDENTS, args));
        }
    }

    // Reference implementation follows
    // This implementation is intentionally poorly-written and contains a lot of copy-and-paste

    public String getMostPopularName(final Collection<Student> students) {
        final NavigableMap<String, Integer> counts = new TreeMap<>();
        for (final Group group : getGroupsByName(students)) {
            for (final String name : Set.copyOf(getFirstNames(group.getStudents()))) {
                counts.merge(name, 1, Integer::sum);
            }
        }

        if (counts.isEmpty()) {
            return "";
        }

        final int max = Collections.max(counts.values());
        for (final Map.Entry<String, Integer> entry : counts.entrySet()) {
            if (entry.getValue() == max) {
                return entry.getKey();
            }
        }
        return "";
    }

    public String getLeastPopularName(final Collection<Student> students) {
        final NavigableMap<String, Integer> counts = new TreeMap<>();
        for (final Group group : getGroupsByName(students)) {
            for (final String name : Set.copyOf(getFirstNames(group.getStudents()))) {
                counts.merge(name, 1, Integer::sum);
            }
        }

        if (counts.isEmpty()) {
            return "";
        }

        final int min = Collections.min(counts.values());
        for (final Map.Entry<String, Integer> entry : counts.entrySet()) {
            if (entry.getValue() == min) {
                return entry.getKey();
            }
        }
        return "";
    }

    @Override
    public List<String> getFirstNames(final Collection<Student> students, final int[] indices) {
        final List<String> firstNames = getFirstNames(List.copyOf(students));
        final List<String> result = new ArrayList<>();
        for (final int index : indices) {
            result.add(firstNames.get(index));
        }
        return result;
    }

    @Override
    public List<String> getLastNames(final Collection<Student> students, final int[] indices) {
        final List<String> lastNames = getLastNames(List.copyOf(students));
        final List<String> result = new ArrayList<>();
        for (final int index : indices) {
            result.add(lastNames.get(index));
        }
        return result;
    }

    @Override
    public List<GroupName> getGroups(final Collection<Student> students, final int[] indices) {
        final List<GroupName> groups = getGroups(List.copyOf(students));
        final List<GroupName> result = new ArrayList<>();
        for (final int index : indices) {
            result.add(groups.get(index));
        }
        return result;
    }

    @Override
    public List<String> getFullNames(final Collection<Student> students, final int[] indices) {
        final List<String> fullNames = getFullNames(List.copyOf(students));
        final List<String> result = new ArrayList<>();
        for (final int index : indices) {
            result.add(fullNames.get(index));
        }
        return result;
    }
}
