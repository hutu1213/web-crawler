package com.example.webcrawler.domain;

import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

public class DomainEntity {

    @Id
    private String id;
    private LocalDateTime lastCrawlTime;
    private String robots;

    public DomainEntity(String robots, LocalDateTime lastCrawlTime, String id) {
        this.robots = robots;
        this.lastCrawlTime = lastCrawlTime;
        this.id = id;
    }

    public String getRobots() {
        return robots;
    }

    public void setRobots(String robots) {
        this.robots = robots;
    }

    public LocalDateTime getLastCrawlTime() {
        return lastCrawlTime;
    }

    public void setLastCrawlTime(LocalDateTime lastCrawlTime) {
        this.lastCrawlTime = lastCrawlTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
