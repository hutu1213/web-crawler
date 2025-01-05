package com.example.webcrawler;

import com.example.webcrawler.fetch.URLFetcherService;
import com.example.webcrawler.parser.ParserHTMLService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class WebCrawlerRunner implements CommandLineRunner {
    private final URLFetcherService urlFetcherService;
    private final ParserHTMLService parserHTMLService;

    public WebCrawlerRunner(URLFetcherService urlFetcherService, ParserHTMLService parserHTMLService) {
        this.urlFetcherService = urlFetcherService;
        this.parserHTMLService = parserHTMLService;
    }

    @Override
    public void run(String... args) throws Exception {
        // Khởi chạy các dịch vụ
        urlFetcherService.start();
        parserHTMLService.start();

        System.out.println("Services started.");
    }
}
