package com.assignment.webcrawler.service;

import com.assignment.webcrawler.entity.Webpage;
import com.assignment.webcrawler.repository.WebpageRepository;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service
@Slf4j
public class WebpageService {

    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
    private final WebpageRepository webpageRepository;


    @Autowired
    public WebpageService(WebpageRepository webpageRepository) {
        this.webpageRepository = webpageRepository;
    }

    public List<Webpage> crawlUrls(String urlParam, int limit) {

        if (limit < 1) {
            return new ArrayList<>();
        }

        Map<String, Webpage> urlToWebpageMap = new HashMap<>();
        Set<String> urlsToCrawl = new HashSet<>();
        urlsToCrawl.add(urlParam);

        Iterator<String> urlsIter;
        while ((urlsIter = urlsToCrawl.iterator()).hasNext()) {
            String url = urlsIter.next();
            urlsIter.remove();

            Document htmlDocument = getHtmlDocument(url);
            if (htmlDocument == null) {
                log.warn("Could not retrieve from ({}) url", url);
                continue;
            }

            Webpage webpage;
            try {
                webpage = retrieveOrCreateIfMissing(url, htmlDocument.title());
            } catch (Exception e) {
                log.warn("failed to retrieveOrCreateIfMissing for ({})", url, e);
                continue;
            }
            urlToWebpageMap.put(url, webpage);
            if (urlToWebpageMap.keySet().size() == limit) {
                break;
            }

            extractLinksAndAddUniqueUrls(url, htmlDocument, urlsToCrawl, urlToWebpageMap.keySet());
        }

        return new ArrayList<>(urlToWebpageMap.values());
    }

    private Webpage retrieveOrCreateIfMissing(String url, String defaultTitle) {
        Webpage webpage = webpageRepository.findByUrl(url);
        log.info("Db record was {} for ({}) url", webpage == null ? "not found" : "found", url);
        if (webpage == null) {
            try {
                webpage = webpageRepository.save(Webpage.builder()
                        .url(url)
                        .title(defaultTitle)
                        .build());
            } catch (Exception e) {
                log.warn("Could not save webpage info for ({}) url", url);
            }
        }
        return webpage;
    }

    private void extractLinksAndAddUniqueUrls(String documentUrl, Document htmlDocument, Set<String> urlsToCrawl, Set<String> strings) {
        Elements linksOnPage = htmlDocument.select("a[href]");
        log.info("Found {} links in ({})", linksOnPage.size(), documentUrl);
        System.out.println();
        for (Element link : linksOnPage) {
            String url = link.absUrl("href");
            if (!strings.contains(url)) {
                urlsToCrawl.add(url);
            }
        }
    }

    private Document getHtmlDocument(String url) {
        Connection connection = Jsoup.connect(url).userAgent(USER_AGENT);
        try {
            Document htmlDocument = connection.get();
            return htmlDocument;
        } catch (IOException e) {
            return null;
        }
    }
}
