package com.example.webcrawler.url;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getDepth() {
        return depth;
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getS3Link() {
        return s3Link;
    }

    public void setS3Link(String s3Link) {
        this.s3Link = s3Link;
    }

    public LocalDateTime getLastCrawlTime() {
        return lastCrawlTime;
    }

    public void setLastCrawlTime(LocalDateTime lastCrawlTime) {
        this.lastCrawlTime = lastCrawlTime;
    }
}
