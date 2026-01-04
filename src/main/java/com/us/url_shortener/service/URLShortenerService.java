package com.us.url_shortener.service;

import com.us.url_shortener.adapter.ShortUrlAdapter;
import com.us.url_shortener.dto.CreateShortUrlRequest;
import com.us.url_shortener.dto.CreateShortUrlResponse;
import com.us.url_shortener.dto.ShortCodeSequenceDAO;
import com.us.url_shortener.model.ShortUrl;
import com.us.url_shortener.repository.ShortUrlRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import util.Base10Encoder;

import java.time.Instant;
import java.util.Optional;

@Service
public class URLShortenerService {

    private final ShortUrlRepository shortUrlRepository;
    private final ShortCodeSequenceDAO shortCodeSequenceDAO;

    public URLShortenerService(ShortUrlRepository shortUrlRepository, ShortCodeSequenceDAO shortCodeSequenceDAO) {
        this.shortUrlRepository = shortUrlRepository;
        this.shortCodeSequenceDAO = shortCodeSequenceDAO;
    }

    // CREATE SEQUENCE short_code_seq START WITH 1 INCREMENT BY 1;
    public CreateShortUrlResponse shorten(CreateShortUrlRequest createShortUrlRequest) {
        long counter = shortCodeSequenceDAO.getNext();
        System.out.println("Counter: " + counter);
        String shortCode = Base10Encoder.BASE_62.encode(counter);
        System.out.println("ShortCode: " + shortCode);

        ShortUrl newShortUrl = ShortUrlAdapter.toShortUrl(createShortUrlRequest);
        newShortUrl.setShortCode(shortCode);

        ShortUrl saved = null;
        try {
            saved = shortUrlRepository.save(newShortUrl);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Short code collision", e);
        }

        return ShortUrlAdapter.toCreateResponse(saved);
    }

    @Cacheable(value = "longUrlCache", key = "#shortCode")
    public String getLongUrl(String shortCode) {
        final Optional<ShortUrl> shortUrlOptional = shortUrlRepository.findByShortCode(shortCode);
        if (shortUrlOptional.isPresent()) {
            final ShortUrl shortUrl = shortUrlOptional.get();
            final long now = Instant.now().toEpochMilli();
            if (now < shortUrl.getExpirationTime()) {
                return shortUrl.getLongUrl();
            } else {
                throw new ResponseStatusException(HttpStatus.GONE, "Short Url expired");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Short Url not found");
        }
    }

    @CacheEvict(value = "longUrlCache", key = "#shortCode")
    public void deleteLongUrlByShortCode(String shortCode) {
        shortUrlRepository.deleteByShortCode(shortCode);
    }
}
