    package com.example.webcrawler.parser;

    import com.example.webcrawler.queue.QueueEnum;
    import com.example.webcrawler.queue.RabbitMQService;
    import com.example.webcrawler.robot.RobotService;
    import com.example.webcrawler.s3.S3Service;
    import com.example.webcrawler.url.UrlEntity;
    import com.example.webcrawler.url.UrlService;
    import org.jsoup.Jsoup;
    import org.jsoup.nodes.Document;
    import org.jsoup.nodes.Element;
    import org.springframework.stereotype.Service;

    import java.nio.charset.StandardCharsets;
    import java.util.ArrayList;
    import java.util.List;

    @Service
    public class ParserHTMLService {
        private final RabbitMQService rabbitMQService;
        private final UrlService urlService;
        private final S3Service s3Service;
        private final RobotService robotsService;

        public ParserHTMLService(RabbitMQService rabbitMQService, UrlService urlService, S3Service s3Service, RobotService robotsService) {
            this.rabbitMQService = rabbitMQService;
            this.urlService = urlService;
            this.s3Service = s3Service;
            this.robotsService = robotsService;
        }

        public void start() throws Exception {
            rabbitMQService.receiveMessages(QueueEnum.Name.PARSING_QUEUE, (consumerTag, delivery) -> {
                String idString = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println("Received URL ID from PARSING_QUEUE: " + idString);

                UrlEntity urlEntity = urlService.getUrlById(idString).orElse(null);
                if (urlEntity == null) {
                    System.err.println("URL Entity not found for ID: " + idString);
                    return;
                }

                String content = s3Service.downloadContent(urlEntity.getS3Link());
                List<String> links = extractLinksFromContent(content);

                for (String link : links) {
                    try {
                        rabbitMQService.sendMessageToQueue(QueueEnum.Name.FRONTIER_QUEUE, link);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("Sent URL to FRONTIER_QUEUE: " + link);
                }
            });
        }


        private List<String> extractLinksFromContent(String content) {
            List<String> links = new ArrayList<>();
            Document doc = Jsoup.parse(content);

            for (Element link : doc.select("a[href]")) {
                String href = link.absUrl("href").trim();
                if (!href.isEmpty()&& robotsService.isAllowedToCrawl(href)) {
                    links.add(href);
                }
            }
            return links;
        }
    }
