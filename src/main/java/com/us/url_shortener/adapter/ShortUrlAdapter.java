package com.us.url_shortener.adapter;

import com.us.url_shortener.dto.CreateShortUrlRequest;
import com.us.url_shortener.dto.CreateShortUrlResponse;
import com.us.url_shortener.model.ShortUrl;

import java.util.UUID;

public class ShortUrlAdapter {

    public static ShortUrl toShortUrl(CreateShortUrlRequest createShortUrlRequest) {
        if (createShortUrlRequest == null) {
            return null;
        }

        final String longUrl = createShortUrlRequest.getUrl();

        final ShortUrl shortUrl = new ShortUrl();
        shortUrl.setLongUrl(longUrl);
        shortUrl.setShortCode(createShortUrl(longUrl));

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

        return createShortUrlResponse;
    }

    private static String createShortUrl(String longUrl) {
        // Write the complex logic here later lol

        return UUID.randomUUID().toString().substring(0, 8);
    }
}
