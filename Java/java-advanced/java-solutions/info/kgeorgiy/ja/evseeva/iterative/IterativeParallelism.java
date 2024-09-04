package info.kgeorgiy.ja.evseeva.iterative;

import info.kgeorgiy.java.advanced.iterative.NewScalarIP;
import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IterativeParallelism implements NewScalarIP {
    private final ParallelMapper mapper;

    public IterativeParallelism(){
        mapper = null;
    }

    public IterativeParallelism(ParallelMapper map){
        this.mapper = map;
    }


    private <T, R> R make(int threads, List<? extends T> values,
                          Function<List<? extends T>, R> f,
                          Function<Stream<R>, R> ans_func, int step) throws InterruptedException {
        List<Thread> threadsList = new ArrayList<>();

        List<? extends T> filter = Stream.iterate(0, index -> index + step)
                .limit((values.size() + step - 1) / step)
                .map(values::get)
                .collect(Collectors.toList());
        threads = Math.min(threads, filter.size());
        List<List<? extends T>> listOfSublists = partition(threads, filter);

        if (mapper == null) {
            List<R> results = new ArrayList<>(Collections.nCopies(threads, null));
            for (int i = 0; i < threads; i++) {
                int index = i;
                threadsList.add(new Thread(() -> results.set(index, f.apply(listOfSublists.get(index)))));
                threadsList.get(i).start();
            }
            threadsList.forEach(thread -> {
                try {
                    thread.join();
                } catch (InterruptedException ignored) {
                }
            });
            return ans_func.apply(results.stream());
        } else {
            return ans_func.apply(mapper.map(f, listOfSublists).stream());
        }
        // :NOTE: snake_case - не для Java :(
    }

    private <T> List<List<? extends T>> partition(int threads, List<? extends T> values) {
        int size = values.size() / threads;
        int remains = values.size() % threads;
        List<List<? extends T>> list = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            int startIndex = i * size + Math.min(i, remains);
            int endIndex = startIndex + size + (i < remains ? 1 : 0);
            List<? extends T> sublist = new ArrayList<>(values.subList(startIndex, endIndex));
            list.add(sublist);
        }
        return list;
    }

    @Override
    public <T> T maximum(int threads, List<? extends T> values, Comparator<? super T> comparator, int step) throws InterruptedException {
        return make(threads, values, l -> l.stream().max(comparator).orElseThrow(),
                stream -> stream.max(comparator).orElseThrow(), step);
    }

    @Override
    public <T> T minimum(int threads, List<? extends T> values, Comparator<? super T> comparator, int step) throws InterruptedException {
        return maximum(threads, values, comparator.reversed(), step);
    }

    @Override
    public <T> boolean all(int threads, List<? extends T> values, Predicate<? super T> predicate, int step) throws InterruptedException {
        return make(threads, values, l -> l.stream().allMatch(predicate),
                stream -> stream.reduce(Boolean::logicalAnd).orElse(true), step);
    }

    @Override
    public <T> boolean any(int threads, List<? extends T> values, Predicate<? super T> predicate, int step) throws InterruptedException {
        return !all(threads, values, predicate.negate(), step);
    }

    @Override
    public <T> int count(int threads, List<? extends T> values, Predicate<? super T> predicate, int step) throws InterruptedException {
        return make(threads, values, l -> (int) l.stream().filter(predicate).count(),
                stream -> stream.reduce(0, Integer::sum), step);
    }
}
