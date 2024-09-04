package info.kgeorgiy.java.advanced.crawler;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Full tests for hard version
 * of <a href="https://www.kgeorgiy.info/courses/java-advanced/homeworks.html#crawler">Web Crawler</a> homework
 * for <a href="https://www.kgeorgiy.info/courses/java-advanced/">Java Advanced</a> course.

 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class NewEasyCrawlerTest extends EasyCrawlerTest {
    private static final List<Set<String>> EXCLUDES = List.of(
            Set.of("200"),
            Set.of("https"),
            Set.of(),
            Set.of("j", "q", "v")
    );
    private static final String SLOW = ".*(erformance|deep|Both)\\(\\)";

    public NewEasyCrawlerTest() {
    }

    @Override
    protected long test(final CrawlerTask task) throws IOException {
        return testTask(task, testName);
    }

    protected static long testTask(final CrawlerTask task, final String testName) throws IOException {
        long result = 0;
        for (final Set<String> excludes : testName.matches(SLOW) ? List.of(Set.of("j")) : EXCLUDES) {
            try {
                result = Math.max(result, task.test(
                        NewCrawler.class,
                        crawler -> crawler.download(task.url(), task.depth(), excludes),
                        url -> excludes.stream().noneMatch(url::contains)
                ));
            } catch (final AssertionError e) {
                throw new AssertionError(String.format(
                        "url = %s, depth = %s, excludes = %s",
                        task.url(),
                        task.depth(),
                        excludes
                ), e);
            }
        }
        return result;
    }
}
