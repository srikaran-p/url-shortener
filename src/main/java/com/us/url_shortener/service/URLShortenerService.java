package com.us.url_shortener.service;

import com.us.url_shortener.adapter.ShortUrlAdapter;
import com.us.url_shortener.dto.CreateShortUrlRequest;
import com.us.url_shortener.dto.CreateShortUrlResponse;
import com.us.url_shortener.model.ShortUrl;
import com.us.url_shortener.repository.ShortUrlRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    public String getLongUrl(String shortCode) {
        return shortUrlRepository.findByShortCode(shortCode)
                                 .map(ShortUrl::getLongUrl)
                                 .orElseThrow(() -> new ResponseStatusException(
                                         HttpStatus.NOT_FOUND, "Short Url not found")
                                 );
    }
}
