# URL Shortener Service

A backend-focused URL shortener built using Spring Boot, PostgreSQL/H2, and Redis, designed to explore core backend concepts such as API design, persistence, caching, and performance measurement.

## Features

- Create short URLs for long URLs
- Redirect short URLs to original URLs
- Support for URL expiry
- Redis-based caching for low-latency redirects
- Load tested using k6

## Tech Stack

- **Java 17**
- **Spring Boot**
- Spring Web, Spring Data JPA, Spring Validation
- **H2** (local testing) / **PostgreSQL** (production-ready)
- **Redis** (caching)
- **k6** (load testing)

## API Overview
### Create Short URL
```
POST /urls/shorten
```

**Request body:**
```
{
"url": "https://example.com/very/long/url"
}
```

**Response:**
```
{
"id": "uuid",
"longUrl": "https://example.com/very/long/url",
"shortCode": "abc123"
}
```
### Redirect
```
GET /{shortCode}
```

- Redirects to the original URL if valid
- Returns **404** if not found
- Returns **410 (Gone)** if expired

## URL Expiry Design

- Each short URL stores an absolute expiration timestamp (epoch millis)
- On every redirect request:
  - current time is compared against expiration time 
  - expired links are rejected with HTTP 410

This keeps expiry logic simple and deterministic.

## Caching Strategy
### Why caching?

The redirect endpoint is **read-heavy** and latency-sensitive.
Initial load testing showed database access dominating request latency.

## What is cached?

Mapping of ```shortCode → longUrl```

Cached using **Spring Cache abstraction backed by Redis**

## Cache configuration

- Read-through cache using @Cacheable
- Cache eviction on delete
- Fixed TTL (1 minute) for simplicity

*(Future improvement: align cache TTL with URL expiry time)*

## Performance Testing

Load tests were conducted using **k6** on the redirect endpoint.

### Before caching

- p90 latency: ~18 ms
- Every request hit the database

### After Redis caching

- p90 latency: ~6.5 ms
- Majority of requests served from cache
- Significant reduction in database queries

This demonstrated a clear improvement in tail latency under load.

## Key Learnings

- Measuring performance before optimization is critical
- Redis caching significantly improves read-heavy workloads
- Tail latency (p90) is more meaningful than averages
- Simple designs with clear trade-offs are easier to evolve

## Future Improvements

- Cache TTL aligned with per-URL expiration time
- Rate limiting for abuse prevention
- Collision handling and retries for short code generation
- Metrics and monitoring (Micrometer + Prometheus)
- Scheduled cleanup of expired URLs

## How to Run

1. Start Redis locally
2. Run the Spring Boot application
3. Use H2 console or PostgreSQL as configured
4. Test endpoints via Postman or curl