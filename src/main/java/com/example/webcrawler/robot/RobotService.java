package com.example.webcrawler.robot;

import com.example.webcrawler.domain.DomainEntity;
import com.example.webcrawler.domain.DomainService;
import crawlercommons.robots.SimpleRobotRules;
import crawlercommons.robots.SimpleRobotRulesParser;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.Optional;

@Service

public class RobotService {

    private DomainService domainService;
    private final WebClient webClient;
    private final SimpleRobotRulesParser robotParser;
    private final String USER_AGENT = "Mozilla/5.0";

    public RobotService(DomainService domainService) {
        this.webClient = WebClient.builder()
                .defaultHeader("User-Agent", USER_AGENT)
                .build();
        this.robotParser = new SimpleRobotRulesParser();
        this.domainService=domainService;
    }

    private SimpleRobotRules getRobotRules(String urlString) {
        try {
            URL url = new URL(urlString);
            String host = url.getHost();

            Optional<DomainEntity> domainOpt = domainService.getDomain(host);
            if (domainOpt.isPresent()) {
                DomainEntity domain = domainOpt.get();
                if (domain.getLastCrawlTime().plusHours(24).isAfter(LocalDateTime.now())) {
                    return parseRobotsTxt(domain.getRobots(), url);
                }
            }

            String robotsTxt = webClient.get()
                    .uri(url.getProtocol() + "://" + host + "/robots.txt")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            domainService.saveOrUpdateDomain(host, robotsTxt);
            return parseRobotsTxt(robotsTxt, url);

        } catch (Exception e) {

            System.err.printf("Error processing robots.txt for %s: %s%n", urlString, e.getMessage());
            return new SimpleRobotRules(SimpleRobotRules.RobotRulesMode.ALLOW_NONE);
        }
    }

    private SimpleRobotRules parseRobotsTxt(String robotsTxt, URL url) {
        return robotParser.parseContent(
                url.toString(),
                robotsTxt.getBytes(),
                "text/plain",
                USER_AGENT
        );
    }

    public long getCrawlDelay(String urlString) {

        return getRobotRules(urlString).getCrawlDelay();
    }

    public boolean isAllowedToCrawl(String urlString) {
        SimpleRobotRules rules = getRobotRules(urlString);
        return rules.isAllowed(urlString);
    }
}