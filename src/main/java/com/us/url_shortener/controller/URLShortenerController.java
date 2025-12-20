package com.us.url_shortener.controller;

import com.us.url_shortener.dto.CreateShortUrlRequest;
import com.us.url_shortener.dto.CreateShortUrlResponse;
import com.us.url_shortener.service.URLShortenerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/urls")
public class URLShortenerController {

    private URLShortenerService urlShortenerService;

    public URLShortenerController(URLShortenerService urlShortenerService) {
        this.urlShortenerService = urlShortenerService;
    }

    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok().body("Oh, hi Mark!");
    }

    @PostMapping("/shorten")
    public ResponseEntity<CreateShortUrlResponse> shorten(@Valid @RequestBody CreateShortUrlRequest createShortUrlRequest) {
        final CreateShortUrlResponse createShortUrlResponse = urlShortenerService.shorten(createShortUrlRequest);

        return ResponseEntity.ok().body(createShortUrlResponse);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        String longUrl = urlShortenerService.getLongUrl(shortCode);

        return ResponseEntity.status(HttpStatus.FOUND)
                             .location(URI.create(longUrl))
                             .build();
    }
}
