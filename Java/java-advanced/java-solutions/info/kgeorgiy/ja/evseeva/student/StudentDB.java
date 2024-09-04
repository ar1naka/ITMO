package info.kgeorgiy.ja.evseeva.student;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import info.kgeorgiy.java.advanced.student.*;
public class StudentDB implements StudentQuery {
    public static Comparator<Student> comp = Comparator.comparing(Student::getLastName)
            .thenComparing(Student::getFirstName)
            // Student compareTo
            .thenComparing(Student::getId, Comparator.reverseOrder());

    private <T> List<T> get(List<Student> students, Function<Student, T> f) {
        return students.stream().map(f).collect(Collectors.toList());
    }

    public List<String> getFirstNames(List<Student> students) {
        return get(students, Student::getFirstName);
    }

    public List<String> getLastNames(List<Student> students){
        return get(students, Student::getLastName);
    }

    public List<GroupName> getGroups(List<Student> students){
        return get(students, Student::getGroup);
    }

    // get?
    public List<String> getFullNames(List<Student> students){
        return students.stream().map(student -> student.getFirstName() + " "
                + student.getLastName()).collect(Collectors.toList());
    }

    public Set<String> getDistinctFirstNames(List<Student> students){
        return students.stream().map(Student::getFirstName).collect(Collectors.toSet());
    }

    public String getMaxStudentFirstName(List<Student> students){
        // compareTo
        return students.stream().max(Comparator.comparing(Student::getId)).
                map(Student::getFirstName).orElse("");
    }

    // Общий код
    public List<Student> sortStudentsById(Collection<Student> students){
        return students.stream().sorted(Comparator.comparing(Student::getId)).collect(Collectors.toList());
    }

    // Общий код
    public List<Student> sortStudentsByName(Collection<Student> students){
        return students.stream().sorted(comp).collect(Collectors.toList());
    }
    
    private <T> List<Student> find(Collection<Student> students, Function<Student, T> f, T type) {
        return students.stream().filter(student -> f.apply(student).equals(type)).sorted(comp)
                .collect(Collectors.toList());
    }

    public List<Student> findStudentsByFirstName(Collection<Student> students, String name) {
        return find(students, Student::getFirstName, name);
    }

    public List<Student> findStudentsByLastName(Collection<Student> students, String name) {
        return find(students, Student::getLastName, name);
    }

    public List<Student> findStudentsByGroup(Collection<Student> students, GroupName group) {
        return find(students, Student::getGroup, group);
    }

    public Map<String, String> findStudentNamesByGroup(Collection<Student> students, GroupName group){
        return students.stream().filter(s -> s.getGroup().equals(group)).collect(Collectors.groupingBy(Student::getLastName,
                Collectors.collectingAndThen(Collectors.minBy(Comparator.comparing(Student::getFirstName)),
                 s -> s.map(Student::getFirstName).orElse(""))));
    }
}
