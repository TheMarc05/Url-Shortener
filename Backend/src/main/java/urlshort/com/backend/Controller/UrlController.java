package urlshort.com.backend.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import urlshort.com.backend.Entity.Url;
import urlshort.com.backend.Exception.RateLimitExceededException;
import urlshort.com.backend.Service.RateLimitService;
import urlshort.com.backend.Service.UrlService;
import urlshort.com.backend.dto.CreateUrlRequest;
import urlshort.com.backend.dto.UrlResponse;

@RestController
public class UrlController {

    @Autowired
    private UrlService urlService;
    @Autowired
    private RateLimitService rateLimitService;

    //creeaza url scurtat
    @PostMapping("/api/urls")
    public ResponseEntity<UrlResponse> createShortUrl(
            @Valid @RequestBody CreateUrlRequest request,
            HttpServletRequest httpRequest
            ){
        String endpoint = "POST:/api/urls";
        String clientIp = extractClientIp(httpRequest);
        //verificare rate limiting
        if(!rateLimitService.isAllowed(clientIp, endpoint)){
            throw new RateLimitExceededException("Prea multe request-uri. Te rugam sa incerci mai tarziu");
        }
        UrlResponse response = urlService.createShortUrl(request, clientIp);
        return ResponseEntity.ok(response);
    }

    //returneaza detalii despre url (statistici)
    @GetMapping("/api/urls/{shortCode}")
    public ResponseEntity<UrlResponse> getUrlDetails(@PathVariable String shortCode){
        UrlResponse response = urlService.getUrlDetails(shortCode);
        return ResponseEntity.ok(response);
    }

    //redirect catre url original
    @GetMapping("/{shortCode}")
    public RedirectView redirectToOriginalUrl(@PathVariable String shortCode){
        Url url = urlService.getOriginalUrl(shortCode);
        return new RedirectView(url.getOriginalUrl());
    }

    //extrage ip-ul clientului, pt rate limiting
    private String extractClientIp(HttpServletRequest request){
        //daca exista proxy sau load balancer, IP-ul real este in header
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if(forwardedFor != null && !forwardedFor.isBlank()){
            return forwardedFor.split(",")[0].trim();//primul IP din lista
        }

        return request.getRemoteAddr();
    }
}
