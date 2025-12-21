package com.us.url_shortener.service;

import com.us.url_shortener.adapter.ShortUrlAdapter;
import com.us.url_shortener.dto.CreateShortUrlRequest;
import com.us.url_shortener.dto.CreateShortUrlResponse;
import com.us.url_shortener.model.ShortUrl;
import com.us.url_shortener.repository.ShortUrlRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Optional;

@Service
public class URLShortenerService {

    private final ShortUrlRepository shortUrlRepository;

    public URLShortenerService(ShortUrlRepository shortUrlRepository) {
        this.shortUrlRepository = shortUrlRepository;
    }

    public CreateShortUrlResponse shorten(CreateShortUrlRequest createShortUrlRequest) {
        ShortUrl newShortUrl = null;
        try {
            newShortUrl = shortUrlRepository.save(ShortUrlAdapter.toShortUrl(createShortUrlRequest));
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Short code collision");
        }

        return ShortUrlAdapter.toCreateResponse(newShortUrl);
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
