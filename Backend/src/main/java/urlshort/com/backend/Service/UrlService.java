package urlshort.com.backend.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import urlshort.com.backend.Entity.Url;
import urlshort.com.backend.Exception.InvalidUrlException;
import urlshort.com.backend.Exception.UrlNotFoundException;
import urlshort.com.backend.Repository.UrlRepository;
import urlshort.com.backend.dto.CreateUrlRequest;
import urlshort.com.backend.dto.UrlResponse;

import java.net.URI;
import java.net.URISyntaxException;

@Service
public class UrlService {
    @Autowired
    private UrlRepository urlRepository;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Transactional //garanteaza atomicitatea operatiunilor DB, rollback automat la erori
    public UrlResponse createShortUrl(CreateUrlRequest request, String clientIp){
        //validare url, mai strict decat @Pattern
        validateUrl(request.getOriginalUrl());

        //generare short code
        String shortCode = generateShortCode();

        while(urlRepository.existsByShortCode(shortCode)){
            shortCode = generateShortCode();
        }

        //creare si salvare url
        Url url = new Url(shortCode, request.getOriginalUrl(), clientIp);
        url = urlRepository.save(url);

        //transformare entity in dto
        return mapToResponse(url);
    }

    @Transactional
    public Url getOriginalUrl(String shortCode){
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("URL-ul cu codul " + shortCode + " nu a fost gasit"));

        if(url.isExpired()){
            throw new UrlNotFoundException("URL expirat");
        }

        if(url.getIsActive()){
            throw new UrlNotFoundException("URL inactiv");
        }

        urlRepository.incrementClickCount(url.getId());//se face automat in DB

        return url;
    }

    //detaliile pt url pt statistici
    public UrlResponse getUrlDetails(String shortCode){
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("URL-ul cu codul " + shortCode + " nu a fost gasit"));

        return mapToResponse(url);
    }

    //validarea url
    //foloseste java.net.URI pt validare
    private void validateUrl(String url){
        try{
            URI uri = new URI(url);

            if(uri.getScheme() == null || (!uri.getScheme().equals("http") && !uri.getScheme().equals("https"))){
                throw new InvalidUrlException("URL trebuie sa foloseasca protocolul http:// sau https://");
            }
        } catch (URISyntaxException e) {
            throw new InvalidUrlException("URL-ul nu este valid: " + e.getMessage());
        }
    }

    //genereaza un shortcode simplificat (temporar)
    private String generateShortCode(){
        return Long.toString(System.currentTimeMillis(), 36).substring(0, 7);
    }

    private UrlResponse mapToResponse(Url url){
        UrlResponse urlResponse = new UrlResponse(
                url.getShortCode(),
                url.getOriginalUrl(),
                url.getClickCount(),
                url.getCreatedAt(),
                url.getExpiresAt()
        );

        urlResponse.setShortUrl(baseUrl + "/" + url.getShortCode());

        return urlResponse;
    }
}
