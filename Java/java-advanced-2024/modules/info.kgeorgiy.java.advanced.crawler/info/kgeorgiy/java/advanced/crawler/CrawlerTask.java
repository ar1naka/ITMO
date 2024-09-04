package info.kgeorgiy.java.advanced.crawler;

import info.kgeorgiy.java.advanced.base.BaseTest;

import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Web crawler task.
 */
public record CrawlerTask(
        String url,
        int depth,
        int downloaders,
        int extractors,
        int perHost,
        int downloadTimeout,
        int extractTimeout
) {
    public <C extends Crawler> C createCrawler(final Downloader downloader, final Class<C> type) {
        try {
            final Constructor<?> constructor = BaseTest.loadClass().getConstructor(
                    Downloader.class,
                    int.class,
                    int.class,
                    int.class
            );
            return type.cast(constructor.newInstance(downloader, downloaders, extractors, perHost));
        } catch (final Exception e) {
            throw new AssertionError(e);
        }
    }

    private <C extends Crawler> Result download(
            final Downloader downloader,
            final Class<C> type,
            final Function<C, Result> crawl,
            final Predicate<String> filter
    ) {
        final CheckingDownloader checkingDownloader = new CheckingDownloader(
                downloader,
                downloaders,
                extractors,
                perHost,
                filter
        );
        try (final C crawler = createCrawler(checkingDownloader, type)) {
            return crawl.apply(crawler);
        } finally {
            Assertions.assertNull(checkingDownloader.getError(), checkingDownloader.getError());
        }
    }

    public <C extends Crawler> long test(
            final Class<C> type,
            final Function<C, Result> crawl,
            final Predicate<String> allowed
    ) throws IOException {
        final ReplayDownloader replayDownloader = new ReplayDownloader(url, downloadTimeout, extractTimeout);

        final long start = System.currentTimeMillis();
        final Result actual = download(replayDownloader, type, crawl, allowed);
        final long time = System.currentTimeMillis() - start;

        checkResult(replayDownloader.expected(url, depth, allowed), actual);
        return time;
    }

    public static void checkResult(final Result expected, final Result actual) {
        compare("Downloaded OK", Set.copyOf(expected.getDownloaded()), Set.copyOf(actual.getDownloaded()));
        compare("Downloaded with errors", expected.getErrors().keySet(), actual.getErrors().keySet());
        Assertions.assertEquals(expected.getErrors(), actual.getErrors(), "Errors");
    }

    private static Set<String> difference(final Set<String> a, final Set<String> b) {
        return a.stream().filter(Predicate.not(b::contains)).collect(Collectors.toSet());
    }

    private static void compare(final String context, final Set<String> expected, final Set<String> actual) {
        final Set<String> missing = difference(expected, actual);
        final Set<String> excess = difference(actual, expected);
        final String message = String.format("%s:%n    missing = %s%n    excess = %s%n", context, missing, excess);
        Assertions.assertTrue(missing.isEmpty() && excess.isEmpty(), message);
    }
}
