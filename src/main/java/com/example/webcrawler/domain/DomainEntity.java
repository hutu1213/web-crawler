package com.example.webcrawler.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
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

}
