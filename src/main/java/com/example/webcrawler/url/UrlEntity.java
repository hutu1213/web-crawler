package com.example.webcrawler.url;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "urls")
public class UrlEntity {

    @Id
    private String id;

    private String url;
    private String s3Link;
    private LocalDateTime lastCrawlTime;
    @Indexed(unique = true)
    private String hash;
    private Integer depth;
    private String content;

}
