package urlshort.com.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class CreateUrlRequest {

    @NotBlank(message = "URL-ul nu poate fi gol") //valideaza ca nu e null
    @Pattern(regexp = "^(https?://).+", message = "URL-ul trebuie sa inceapa cu http:// sau https://") //valideaza formatul url-ului
    private String originalUrl;

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }
}
