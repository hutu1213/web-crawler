package com.example.webcrawler.url;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UrlService {
    private final UrlRepository urlRepository;


    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public UrlEntity saveOrUpdateUrl(String url, String s3LinkHtml, String s3TextLink, String hash) {
        UrlEntity urlEntity = urlRepository.findByUrl(url).orElse(new UrlEntity());
        urlEntity.setUrl(url);
        urlEntity.setS3Link(s3LinkHtml);  // Đường dẫn HTML đầy đủ
        urlEntity.setContent(s3TextLink); // Đường dẫn nội dung
        urlEntity.setHash(hash);
        urlEntity.setLastCrawlTime(LocalDateTime.now());
        return urlRepository.save(urlEntity);

    }
    public boolean isHashExists(String hash) {
        return urlRepository.existsByHash(hash); // Sử dụng repository query
    }
    public Optional<UrlEntity> getUrlById(String id) {
        return urlRepository.findById(id);

    }
}
