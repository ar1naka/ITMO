package info.kgeorgiy.java.advanced.crawler;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Advanced tests for hard version
 * of <a href="https://www.kgeorgiy.info/courses/java-advanced/homeworks.html#crawler">Web Crawler</a> homework
 * for <a href="https://www.kgeorgiy.info/courses/java-advanced/">Java Advanced</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class AdvancedCrawlerTest extends HardCrawlerTest {
    private static final List<String> ALL_HOSTS = List.of(
            "enterprise.github.com", "gist.github.com", "github.community", "education.github.com",
            "github.blog", "docs.github.com", "creativecommons.org", "business.twitter.com",
            "support.github.com", "www.ifmo.ru", "help.twitter.com", "kgeorgiy.info",
            "support.twitter.com", "lab.github.com", "gkorneev.moikrug.ru", "www.kgeorgiy.info",
            "vk.com", "kgeorgiy.livejournal.com", "twitter.com", "www.facebook.com", "services.github.com",
            "opensource.guide", "github.com", "validator.w3.org", "www.linkedin.com", "keybase.io",
            "stars.github.com", "legal.twitter.com", "gg.gg", "goo.gl", "www.githubstatus.com"
    );

    private static final List<String> PARTS = ALL_HOSTS.stream()
            .flatMap(host -> Arrays.stream(host.split("\\.")))
            .distinct()
            .toList();

    public AdvancedCrawlerTest() {
    }

    @Test
    public void test50_singleHost() throws IOException {
        test("https://itmo.ru", 2, List.of("itmo.ru"));
    }

    @Test
    public void test51_otherHost() throws IOException {
        test("https://itmo.ru", 10, List.of("www.itmo.ru"));
        test("https://itmo.ru", 10, List.of("itmo.ru.ru"));
    }

    @Test
    public void test52_multiHosts() throws IOException {
        test("http://www.kgeorgiy.info", 10, List.of("www.kgeorgiy.info", "www.facebook.com", "twitter.com"));
    }

    @Test
    public void test53_allHosts() throws IOException {
        test("http://www.kgeorgiy.info", 5, ALL_HOSTS);
    }

    @Test
    public void test54_extraHosts() throws IOException {
        final Random random = new Random(8735083475083705845L);
        final List<String> hosts = Stream.concat(
                        ALL_HOSTS.stream(),
                        Stream.generate(() -> randomPart(random) + "." + randomPart(random)).limit(10_000)
                )
                .sorted()
                .toList();
        test("http://www.kgeorgiy.info", 5, hosts);
    }

    private static String randomPart(final Random random) {
        return PARTS.get(random.nextInt(PARTS.size()));
    }

    @Test
    public void test55_megaHosts() throws IOException {
        test("http://www.kgeorgiy.info", 10, Collections.nCopies(1 << 27, "www.kgeorgiy.info"));
    }

    private static void test(final String start, final int depth, final List<String> hosts) throws IOException {
        final Set<String> set = hosts.stream().collect(Collectors.toUnmodifiableSet());
        final Predicate<String> predicate = url -> {
            try {
                return set.contains(URLUtils.getHost(url));
            } catch (final MalformedURLException e) {
                return false;
            }
        };
        new CrawlerTask(start, depth, UNLIMITED, UNLIMITED, UNLIMITED, 0, 0).test(
                AdvancedCrawler.class,
                crawler -> crawler.advancedDownload(start, depth, hosts),
                predicate
        );
    }

    @Override
    protected long test(final CrawlerTask task) throws IOException {
        return NewEasyCrawlerTest.testTask(task, testName);
    }
}
