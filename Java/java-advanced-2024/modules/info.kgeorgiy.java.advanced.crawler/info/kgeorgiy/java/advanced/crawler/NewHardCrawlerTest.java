package info.kgeorgiy.java.advanced.crawler;

import java.io.IOException;

/**
 * Full tests for hard version
 * of <a href="https://www.kgeorgiy.info/courses/java-advanced/homeworks.html#crawler">Web Crawler</a> homework
 * for <a href="https://www.kgeorgiy.info/courses/java-advanced/">Java Advanced</a> course.

 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class NewHardCrawlerTest extends HardCrawlerTest {
    public NewHardCrawlerTest() {
    }

    @Override
    protected long test(final CrawlerTask task) throws IOException {
        return NewEasyCrawlerTest.testTask(task, testName);
    }
}
