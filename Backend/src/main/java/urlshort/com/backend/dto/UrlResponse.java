package urlshort.com.backend.dto;

import java.time.LocalDateTime;

public class UrlResponse {
    private String shortCode;
    private String shortUrl;      //url-ul complet scurtat
    private String originalUrl;
    private Long clickCount;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    public UrlResponse() {
    }

    public UrlResponse(String shortCode, String originalUrl, Long clickCount,
                       LocalDateTime createdAt, LocalDateTime expiresAt) {
        this.shortCode = shortCode;
        this.originalUrl = originalUrl;
        this.clickCount = clickCount;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        // shortUrl va fi construit din shortCode (Service Layer)
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public Long getClickCount() {
        return clickCount;
    }

    public void setClickCount(Long clickCount) {
        this.clickCount = clickCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}
