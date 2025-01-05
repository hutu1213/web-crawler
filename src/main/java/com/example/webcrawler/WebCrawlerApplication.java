package com.example.webcrawler;

import com.example.webcrawler.queue.QueueEnum;
import com.example.webcrawler.queue.RabbitMQService;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
    public class WebCrawlerApplication implements CommandLineRunner {
    private final RabbitMQService rabbitMQService;
    @Autowired
    public WebCrawlerApplication(RabbitMQService rabbitMQService) {
        this.rabbitMQService = rabbitMQService;
    }

    public static void main(String[] args) {
        // Load .env file
        Dotenv dotenv = Dotenv.configure().load();

        // Set environment variables for Spring Boot
        System.setProperty("AWS_S3_BUCKET_NAME", dotenv.get("AWS_S3_BUCKET_NAME"));
        System.setProperty("AWS_REGION", dotenv.get("AWS_REGION"));
        System.setProperty("AWS_ACCESS_KEY", dotenv.get("AWS_ACCESS_KEY"));
        System.setProperty("AWS_SECRET_KEY", dotenv.get("AWS_SECRET_KEY"));

        SpringApplication.run(WebCrawlerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Gửi URL ban đầu vào hàng đợi FRONTIER_QUEUE
        List<String> rootUrls = List.of(
//                "https://www.example.com"
                "https://www.wikipedia.org"
//                "https://www.stackoverflow.com"
        );

        for (String url : rootUrls) {
            rabbitMQService.sendMessageToQueue(QueueEnum.Name.FRONTIER_QUEUE, url);
        }
    }
}
