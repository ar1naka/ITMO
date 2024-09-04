package info.kgeorgiy.ja.evseeva.iterative;

import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ParallelMapperImpl implements ParallelMapper {
    private final List<Thread> threads;
    private final Queue<Runnable> queue = new ArrayDeque<>();
    public ParallelMapperImpl(int th) {
        threads = IntStream.range(0, th).mapToObj(i -> new Thread(() -> {
                    try {
                        while (!Thread.currentThread().isInterrupted()) {
                            runTasks();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }))
                .peek(Thread::start)
                .collect(Collectors.toList());
    }

    private  void runTasks() throws InterruptedException {
        final Runnable task;
        synchronized (queue) {
            while (queue.isEmpty()) {
                queue.wait();
            }
            task = queue.poll();
        }
        try {
            task.run();
        } catch (RuntimeException ignored){}
    }

    @Override
    public <T, R> List<R> map(Function<? super T, ? extends R> f, List<? extends T> items) throws InterruptedException {
        List<R> result = new ArrayList<>(Collections.nCopies(items.size(), null));
        Counter count = new Counter();
        IntStream.range(0, items.size()).forEach(i -> {
            synchronized (queue) {
                queue.add(() -> {
                    result.set(i, f.apply(items.get(i)));
                    count.incrementAndGet(result);
                });
                queue.notifyAll();
            }
        });
        count.get(result);
        return result;
    }


    private static class Counter {
        private int cnt = 0;
        public synchronized void incrementAndGet(List<?> list) {
            cnt++;
            if (cnt == list.size()) {
                notifyAll();
            }
        }
        public synchronized void get(List<?> list) throws InterruptedException {
            while (cnt != list.size()) {
                wait();
            }

        }
        public synchronized int getValue() {
            return cnt;
        }
        // :NOTE: unused = removed
    }

    @Override
    public void close() {
        for (Thread thread : threads) {
            thread.interrupt();
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
