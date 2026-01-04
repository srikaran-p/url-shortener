package com.us.url_shortener.adapter;

import com.us.url_shortener.dto.CreateShortUrlRequest;
import com.us.url_shortener.dto.CreateShortUrlResponse;
import com.us.url_shortener.model.ShortUrl;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class ShortUrlAdapter {

    private static final long TIME_TO_EXPIRY_IN_SECONDS = 3000;

    public static ShortUrl toShortUrl(CreateShortUrlRequest createShortUrlRequest) {
        if (createShortUrlRequest == null) {
            return null;
        }

        final String longUrl = createShortUrlRequest.getUrl();

        final ShortUrl shortUrl = new ShortUrl();
        shortUrl.setLongUrl(longUrl);

        final Long expirationTime = getExpirationTime();
        shortUrl.setExpirationTime(expirationTime);

        return shortUrl;
    }

    public static CreateShortUrlResponse toCreateResponse(ShortUrl shortUrl) {
        if (shortUrl == null) {
            return null;
        }

        final CreateShortUrlResponse createShortUrlResponse = new CreateShortUrlResponse();
        createShortUrlResponse.setId(shortUrl.getId());
        createShortUrlResponse.setLongUrl(shortUrl.getLongUrl());
        createShortUrlResponse.setShortCode(shortUrl.getShortCode());
        createShortUrlResponse.setExpirationTime(shortUrl.getExpirationTime());

        return createShortUrlResponse;
    }

    private static Long getExpirationTime() {
        final Instant now = Instant.now();
        Instant futureInstant = now.plus(Duration.ofSeconds(TIME_TO_EXPIRY_IN_SECONDS));
        return futureInstant.toEpochMilli();
    }
}
