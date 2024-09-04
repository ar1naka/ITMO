package info.kgeorgiy.ja.evseeva.crawler;

import info.kgeorgiy.java.advanced.crawler.Document;
import info.kgeorgiy.java.advanced.crawler.NewCrawler;
import info.kgeorgiy.java.advanced.crawler.Downloader;
import info.kgeorgiy.java.advanced.crawler.CachingDownloader;
import info.kgeorgiy.java.advanced.crawler.Result;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Class implements {@link NewCrawler} interface.
 */
public class WebCrawler implements NewCrawler {
    private final Downloader downloader;
    private final ExecutorService downloaders;
    private final ExecutorService extractors;

    /**
     * Constructs a new {@link WebCrawler} object.
     * @param downloader Allows you to download pages and extract links from them
     * @param downloaders Maximum number of pages that can be loaded simultaneously
     * @param extractors Maximum number of pages from which links are simultaneously extracted
     * @param perHost Maximum number of pages that can be downloaded simultaneously from one host
     */
    public WebCrawler(Downloader downloader, int downloaders, int extractors, int perHost){
        this.downloader = downloader;
        this.downloaders = Executors.newFixedThreadPool(downloaders);
        this.extractors = Executors.newFixedThreadPool(extractors);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Result download(String url, int depth, Set<String> excludes) {
        Set<String> visitedUrls = ConcurrentHashMap.newKeySet();
        Set<String> frontierUrls = ConcurrentHashMap.newKeySet();
        frontierUrls.add(url);

        Phaser phaser = new Phaser(1);
        ConcurrentHashMap<String, IOException> errors = new ConcurrentHashMap<>();

        for (int i = 0; i < depth; i++) {
            Set<String> tmp = ConcurrentHashMap.newKeySet();
            int finalI = i;
            frontierUrls.stream()
                    .filter(s -> excludes.stream().noneMatch(s::contains))
                    .forEach(s -> run(s, depth - finalI, errors, visitedUrls, tmp, phaser));
            phaser.arriveAndAwaitAdvance();

            frontierUrls.clear();
            frontierUrls.addAll(tmp);
            tmp.clear();
        }
        return new Result(visitedUrls.stream().toList(), errors);
    }

    private void run(String url, int depth, ConcurrentHashMap<String, IOException> errors,
                     Set<String> downloaded, Set<String> extracted, Phaser phaser) {
        phaser.register();
        downloaders.submit(() -> {
            try {
                if (!downloaded.contains(url) && !errors.containsKey(url)) {
                    Document document = downloader.download(url);
                    downloaded.add(url);
                    if (depth > 1) {
                        processExtracted(document, url, extracted, errors, phaser);
                    }
                }
            } catch (IOException e) {
                errors.put(url, e);
            } finally {
                phaser.arriveAndDeregister();
            }
        });
    }

    private void processExtracted(Document document, String url, Set<String> extracted,
                                  ConcurrentHashMap<String, IOException> errors, Phaser phaser) {
        phaser.register();
        extractors.submit(() -> {
            try {
                extracted.addAll(document.extractLinks());
            } catch (IOException e) {
                errors.put(url, e);
            } finally {
                phaser.arriveAndDeregister();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        // :NOTE: некорректное закрытие, прочитайте документацию

        // fixed
        downloaders.close();
        extractors.close();
    }

    /**
     * The main entry for a web crawler.
     * WebCrawler url [depth [downloads [extractors [perHost]]]]
     * @param args args[0] - link to page(url)
     *             args[1] - depth
     *             args[2] - downloads, Maximum number of pages that can be loaded simultaneously
     *             args[3] - extractors, Maximum number of pages from which links are simultaneously extracted;
     *             args[4] - perHost, Maximum number of pages that can be downloaded simultaneously from one host
     */
    public static void main(String[] args){
        if (args == null || args.length  > 5 || args.length < 1) {
            System.err.println("Incorrect input, expected: url [depth [downloads [extractors [perHost]]]]");
            return;
        }
        try {
            int depth = parseArgs(args, 1);
            int downloads = parseArgs(args, 2);
            int extractors = parseArgs(args, 3);
            int perHost = parseArgs(args, 4);

            String url = args[0];
            try (WebCrawler crawler = new WebCrawler(new CachingDownloader(1), depth, downloads, extractors)) {
                crawler.download(url, perHost);
            }
        } catch (IOException e){
            System.err.println("Error with CachingDownloader");
        }

    }
    private static int parseArgs(String[] args, int index) {
        return index > args.length ? 1 : Integer.parseInt(args[index]);
    }
}
