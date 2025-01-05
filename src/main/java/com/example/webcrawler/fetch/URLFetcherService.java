package com.example.webcrawler.fetch;

import com.example.webcrawler.queue.QueueEnum;
import com.example.webcrawler.queue.RabbitMQService;
import com.example.webcrawler.robot.RobotService;
import com.example.webcrawler.s3.S3Service;
import com.example.webcrawler.url.UrlEntity;
import com.example.webcrawler.url.UrlEnum;
import com.example.webcrawler.url.UrlService;
import org.jsoup.Jsoup;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class URLFetcherService {
    private final RabbitMQService rabbitMQService;
    private final UrlService urlService;
    private final S3Service s3Service;
    private final WebClient webClient;
    private final RobotService robotsService;

    public URLFetcherService(RabbitMQService rabbitMQService, UrlService urlService,
                             S3Service s3Service,RobotService robotsService) {
        this.rabbitMQService = rabbitMQService;
        this.urlService = urlService;
        this.s3Service = s3Service;
        this.webClient = WebClient.builder()
                .defaultHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0")
                .defaultHeader(HttpHeaders.ACCEPT, "text/html,application/xhtml+xml")
                .build();
        this.robotsService = robotsService;
    }

    public void start() throws Exception {
        rabbitMQService.receiveMessages(QueueEnum.Name.FRONTIER_QUEUE, (consumerTag, delivery) -> {
            String urlString = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println("Received URL (fetcher): " + urlString);

            if (!robotsService.isAllowedToCrawl(urlString)) {
                System.err.println("URL not allowed by robots.txt: " + urlString);
                return;
            }

            long delay = robotsService.getCrawlDelay(urlString);
            if (delay > 0) {
                System.err.println("Delay by robots");
                try {
                    Thread.sleep(delay * 1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            String content = fetchContent(urlString);
            if (content != null) {
                String hash = calculateHash(content);
                if (urlService.isHashExists(hash)) {
                    System.out.println("Duplicate content detected (hash exists), skipping URL: " + urlString);
                    return;
                }
                String s3LinkHtml = s3Service.uploadContent(content, UrlEnum.Type.HTML.getType());
                String strippedContent = stripHtmlTags(content);
                String s3LinkText = s3Service.uploadContent(strippedContent, UrlEnum.Type.TEXT.getType());

                UrlEntity urlEntity = urlService.saveOrUpdateUrl(urlString, s3LinkHtml, s3LinkText, hash);

                try {
                    rabbitMQService.sendMessageToQueue(QueueEnum.Name.PARSING_QUEUE, urlEntity.getId());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Sent URL ID to Parsing Queue: " + urlEntity.getId());
            }
        });
    }

    private String stripHtmlTags(String html) {
        return Jsoup.parse(html).text();
    }

    private String calculateHash(String content) {
        try {
            String ALGORITHM = "SHA-256";
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            byte[] encodedHash = digest.digest(content.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error calculating hash", e);
        }
    }

    private String fetchContent(String urlString) {
        try {
            System.out.println("Fetching content from: " + urlString);
            return webClient.get()
                    .uri(urlString)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (Exception e) {
            System.err.println("Error fetching URL: " + urlString + ". Error: " + e.getMessage());
            return null;
        }
    }
}
