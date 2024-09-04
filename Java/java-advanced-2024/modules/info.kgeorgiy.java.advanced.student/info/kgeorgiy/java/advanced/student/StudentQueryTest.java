package info.kgeorgiy.java.advanced.student;

import info.kgeorgiy.java.advanced.base.BaseTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Tests for easy version
 * of <a href="https://www.kgeorgiy.info/courses/java-advanced/homeworks.html#homework-student">Student</a> homework
 * for <a href="https://www.kgeorgiy.info/courses/java-advanced/">Java Advanced</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class StudentQueryTest extends BaseTest implements StudentQuery {
    protected static final Random RANDOM = new Random(2350238475230489753L);
    private static final List<String> FIRST_NAMES = List.of("Абулмуслим", "Айрат", "Аким", "Александр", "Александра", "Алексей", "Алена", "Алёна", "Алина", "Алиса", "Анастасия", "Андрей", "Анна", "Ансар", "Антон", "Арина", "Аркадий", "Арман", "Арсений", "Артем", "Артём", "Артемий", "Артур", "Бобурмирзо", "Богдан", "Вадим", "Валерий", "Варвара", "Виктор", "Виталий", "Владимир", "Владислав", "Ву", "Вячеслав", "Георгий", "Глеб", "Григорий", "Даниил", "Данил", "Даниль", "Денис", "Джеукам", "Дмитрий", "Евгений", "Егор", "Екатерина", "Елизавета", "Иван", "Игорь", "Илья", "Искандер", "Камиль", "Кирилл", "Константин", "Ксения", "Лев", "Леонид", "Мажд", "Майя", "Максим", "Марина", "Матвей", "Михаил", "Наталья", "Никита", "Николай", "Олег", "Павел", "Паскаль", "Рамиль", "Ростислав", "Руслан", "Рустам", "Сергей", "Станислав", "Степан", "Тимофей", "Тимур", "Федор", "Фёдор", "Эдуард", "Эя", "Ярослав");
    private static final List<String> LAST_NAMES = List.of("Абрамов", "Алексеев", "Амиров", "Андриянов", "Артемьев", "Архангельский", "Архипова", "Астафьев", "Атаев", "Багаутдинов", "Багринцев", "Балакин", "Балбеков", "Баркина", "Бекаревич", "Белуган", "Беляев", "Бердников", "Бережной", "Беспалов", "Биктимиров", "Бондарев", "Борисов", "Борькин", "Бохссас", "Булавко", "Бурунсузян", "Васильев", "Ведерникова", "Веселкова", "Виноградов", "Власов", "Володин", "Высотин", "Гаврилюк", "Галкин", "Герасимов", "Гоге", "Голянский", "Горбунов", "Гордеев", "Горелов", "Городнов", "Григорьев", "Гришечкин", "Гришин", "Грушевский", "Губин", "Гуров", "Гусев", "Давыдкин", "Девятериков", "Дегтярев", "Денисов", "Дмитриев", "Добрис", "Долматова", "Доля", "Дробунин", "Дубинин", "Дунаев", "Духанин", "Дьяков", "Евсеева", "Егоров", "Елагина", "Елефтериади", "Ерёмин", "Еронин", "Ефименко", "Жуков", "Забейворота", "Загребин", "Зызлаев", "Иванов", "Ивченко", "Ивченков", "Изиланов", "Илляхунов", "Ильин", "Ильченко", "Илюхин", "Исаев", "Казаков", "Калачев", "Карпухин", "Каско", "Кинзин", "Кирьяк", "Кирюшкина", "Кистер", "Клементьев", "Клюшкин", "Ковалёв", "Козлов", "Колтаков", "Коновалов", "Корнилович", "Коробов", "Коршунов", "Краснов", "Краснояров", "Крупский", "Кузнецов", "Куксо", "Кулаковский", "Куликов", "Кумзериков", "Куприянов", "Куртеев", "Ле", "Лельчук", "Лещев", "Линевич", "Лубнин", "Лыженков", "Ляпин", "Максименко", "Масленников", "Матвеев", "Матсон", "Минаев", "Минчаков", "Мицеловский", "Мокин", "Морозов", "Мурысин", "Назаров", "Нартов", "Небабин", "Некрасов", "Никитин", "Новицкий", "Новоселов", "Нотфуллин", "Ночевкина", "Овсянникова", "Осипов", "Остриченко", "Павлов", "Пакульневич", "Пан", "Панасюк", "Патрушева", "Петров", "Петрова", "Петухов", "Подгороднев", "Подкорытов", "Прасолов", "Пресняков", "Примаков", "Пьянков", "Ратников", "Родионов", "Рудер", "Рыбкин", "Рязанова", "Сабитов", "Селезнев", "Селищев", "Сергеев", "Синицин", "Ситкина", "Скопцов", "Скорняков", "Сычев", "Тарасевич", "Тарасов", "Телевной", "Тимченко", "Тиунов", "Товмасян", "Трощий", "Тюленев", "Учкунов", "Ушенко", "Фарафонов", "Хайруллин", "Хакимов", "Хартманн", "Химченко", "Холодова", "Хритоненко", "Хури", "Цицин", "Челебаев", "Чепнгум", "Червяков", "Черкашин", "Черных", "Чернышев", "Чирятьев", "Чистов", "Чичев", "Шавалиева", "Шайдулин", "Шамшура", "Шаповалов", "Шарипов", "Швецов", "Шевченко", "Шибанов", "Шинкарева", "Широких", "Шовкопляс", "Шукалов", "Шульпин", "Щетинин", "Яковлев", "Ястребов", "Яцук");
    private static final List<GroupName> GROUPS = List.of(GroupName.values());

    protected static final List<Student> STUDENTS = RANDOM.ints(300)
            .mapToObj(id -> new Student(id, random(FIRST_NAMES), random(LAST_NAMES), random(GROUPS)))
            .collect(Collectors.toUnmodifiableList());

    private static final List<List<Student>> INPUTS = IntStream.range(0, STUDENTS.size())
            .mapToObj(size -> {
                final List<Student> students = new ArrayList<>(STUDENTS);
                Collections.shuffle(students, RANDOM);
                return List.copyOf(students.subList(0, size));
            })
            .collect(Collectors.toUnmodifiableList());

    private static <T> T random(final List<T> values) {
        return values.get(RANDOM.nextInt(values.size()));
    }

    private final StudentQuery db = createCUT();

    public StudentQueryTest() {
    }

    @Test
    public void test01_testGetFirstNames() {
        test(this::getFirstNames, db::getFirstNames);
    }

    @Test
    public void test02_testGetLastNames() {
        test(this::getLastNames, db::getLastNames);
    }

    @Test
    public void test03_testGetGroups() {
        test(this::getGroups, db::getGroups);
    }

    @Test
    public void test04_testGetFullNames() {
        test(this::getFullNames, db::getFullNames);
    }

    @Test
    public void test05_testGetDistinctFirstNames() {
        test(this::getDistinctFirstNames, db::getDistinctFirstNames);
    }

    @Test
    public void test06_testGetMaxStudentFirstName() {
        test(this::getMaxStudentFirstName, db::getMaxStudentFirstName);
    }

    @Test
    public void test07_testSortStudentsById() {
        test(this::sortStudentsById, db::sortStudentsById);
    }

    @Test
    public void test08_testSortStudentsByName() {
        test(this::sortStudentsByName, db::sortStudentsByName);
    }

    @Test
    public void test09_testFindStudentsByFirstName() {
        testBi(this::findStudentsByFirstName, db::findStudentsByFirstName, FIRST_NAMES);
    }

    @Test
    public void test10_testFindStudentsByLastName() {
        testBi(this::findStudentsByLastName, db::findStudentsByLastName, FIRST_NAMES);
    }

    @Test
    public void test11_testFindStudentsByGroup() {
        testBi(this::findStudentsByGroup, db::findStudentsByGroup, GROUPS);
    }

    @Test
    public void test12_findStudentNamesByGroup() {
        testBi(this::findStudentNamesByGroupList, db::findStudentNamesByGroupList, GROUPS);
    }

    public static <R> void test(final Function<List<Student>, R> reference, final Function<List<Student>, R> tested) {
        for (final List<Student> input : INPUTS) {
            Assertions.assertEquals(reference.apply(input), tested.apply(input), "For " + input);
        }
    }

    protected static <T, U> void testBi(
            final BiFunction<List<Student>, U, T> reference,
            final BiFunction<List<Student>, U, T> tested,
            final List<U> values
    ) {
        for (final U value : values.subList(0, Math.min(values.size(), 10))) {
            System.err.println("\tTesting " + value);
            try {
                test(input -> reference.apply(input, value), input -> tested.apply(input, value));
            } catch (final AssertionError e) {
                throw new AssertionError("Value " + value + ": " + e.getMessage(), e);
            }
        }
    }

    // Reference implementation follows.
    // This implementation is intentionally poorly-written and contains a lot of copy-and-paste.

    @Override
    public List<String> getFirstNames(final List<Student> students) {
        final List<String> result = new ArrayList<>();
        for (final Student student : students) {
            result.add(student.getFirstName());
        }
        return result;
    }

    @Override
    public List<String> getLastNames(final List<Student> students) {
        final List<String> result = new ArrayList<>();
        for (final Student student : students) {
            result.add(student.getLastName());
        }
        return result;
    }

    @Override
    public List<GroupName> getGroups(final List<Student> students) {
        final List<GroupName> result = new ArrayList<>();
        for (final Student student : students) {
            result.add(student.getGroup());
        }
        return result;
    }

    @Override
    public List<String> getFullNames(final List<Student> students) {
        final List<String> result = new ArrayList<>();
        for (final Student student : students) {
            result.add(getFullName(student));
        }
        return result;
    }

    protected static String getFullName(final Student student) {
        return student.getFirstName() + " " + student.getLastName();
    }

    @Override
    public Set<String> getDistinctFirstNames(final List<Student> students) {
        return new TreeSet<>(getFirstNames(students));
    }

    @Override
    public String getMaxStudentFirstName(final List<Student> students) {
        int maxId = Integer.MIN_VALUE;
        String maxName = "";
        for (final Student student : students) {
            if (maxId < student.getId()) {
                maxId = student.getId();
                maxName = student.getFirstName();
            }
        }
        return maxName;
    }

    @Override
    public List<Student> sortStudentsById(final Collection<Student> students) {
        final ArrayList<Student> sorted = new ArrayList<>(students);
        Collections.sort(sorted);
        return sorted;
    }

    private static final Comparator<Student> STUDENT_COMPARATOR = (a, b) -> {
        final int last = a.getLastName().compareTo(b.getLastName());
        if (last != 0) {
            return last;
        }
        final int first = a.getFirstName().compareTo(b.getFirstName());
        if (first != 0) {
            return first;
        }
        return -Integer.compare(a.getId(), b.getId());
    };

    @Override
    public List<Student> sortStudentsByName(final Collection<Student> students) {
        final ArrayList<Student> sorted = new ArrayList<>(students);
        sorted.sort(STUDENT_COMPARATOR);
        return sorted;
    }

    @Override
    public List<Student> findStudentsByFirstName(final Collection<Student> students, final String firstName) {
        final ArrayList<Student> result = new ArrayList<>(students);
        result.removeIf(student -> !student.getFirstName().equals(firstName));
        result.sort(STUDENT_COMPARATOR);
        return result;
    }

    @Override
    public List<Student> findStudentsByLastName(final Collection<Student> students, final String lastName) {
        final ArrayList<Student> result = new ArrayList<>(students);
        result.removeIf(student -> !student.getLastName().equals(lastName));
        result.sort(STUDENT_COMPARATOR);
        return result;
    }

    @Override
    public List<Student> findStudentsByGroup(final Collection<Student> students, final GroupName group) {
        final ArrayList<Student> result = new ArrayList<>(students);
        result.removeIf(student -> !student.getGroup().equals(group));
        result.sort(STUDENT_COMPARATOR);
        return result;
    }

    @Override
    public Map<String, String> findStudentNamesByGroup(final Collection<Student> students, final GroupName group) {
        final Map<String, String> result = new HashMap<>();
        for (final Student student : findStudentsByGroup(students, group)) {
            result.merge(student.getLastName(), student.getFirstName(), BinaryOperator.minBy(Comparable::compareTo));
        }
        return result;
    }
}
