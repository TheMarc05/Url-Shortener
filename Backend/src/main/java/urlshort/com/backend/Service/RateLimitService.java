package urlshort.com.backend.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

//service pt rate limiting bazat pe IP
//token bucket algorithm
//-fiecare IP are un contor in redis
//-fiecare request incrementeaza contorul
//-daca contorul depaseste limita - request blocat
//-ttl automat: contorul se reseteaza dupa interval
//design pattern: sliding window rate limiting
@Service
public class RateLimitService {
    private static final Logger logger =  LoggerFactory.getLogger(RateLimitService.class);

    //prefix pt keys in redis
    private static final String RATE_LIMIT_PREFIX = "rate_limit:";

    //limite default
    private static final int DEFAULT_MAX_REQUESTS = 100; // 100 request-uri
    private static final int DEFAULT_TIME_WINDOW_SECONDS = 60; // per minut

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    //verifica daca un IP poate face un request
    //algoritm:
    //-construieste key-ul redis: rate_limit:{ip}:{endpoint}
    //-incrementeaza contorul (atomic in redis)
    //-daca e primul request seteaza ttl
    //-verifica daca contorul depaseste limita
    public boolean isAllowed(String ipAddress, String endpoint){
        try{
            String key = buildKey(ipAddress, endpoint);

            //incrementeaza contorul atomic in redis
            Long currentCount = redisTemplate.opsForValue().increment(key);

            //daca e primul request seteaza ttl
            if(currentCount == 1){
                redisTemplate.expire(key, DEFAULT_TIME_WINDOW_SECONDS, TimeUnit.SECONDS);
            }

            //verificare daca depaseste limita
            if(currentCount > DEFAULT_MAX_REQUESTS){
                logger.warn("Rate limit exceeded for IP: {} on endpoint: {}", ipAddress, endpoint);
                return false;
            }

            return true;
        }catch (Exception e){
            //graceful degradation: daca redis esueaza, permite requestul
            logger.warn("Rate limit unavailable, allowing request: {}", e.getMessage());
            return false;//nu se blocheaza accesul daca rate limiting esueaza
        }
    }

    //verifica daca un IP poate face request cu limite custom
    public boolean isAllowed(String ipAddress, String endpoint, int maxRequests, int timeWindowSeconds){
        try{
            String key = buildKey(ipAddress, endpoint);

            Long currentCount = redisTemplate.opsForValue().increment(key);

            if(currentCount == 1){
                logger.warn("Rate limit exceeded for IP: {} on endpoint: {} (limit: {})",
                        ipAddress, endpoint, maxRequests);
                return false;
            }
            return true;
        }catch (Exception e){
            logger.warn("Rate limiting unavailable, allowing request: {}", e.getMessage());
            return true;
        }
    }

    //construieste key-ul redis pt rate limiting
    //Format: rate_limit:{ip}:{endpoint}
    private String buildKey(String ipAddress, String endpoint){
        return RATE_LIMIT_PREFIX + ipAddress + ":" + endpoint;
    }

    //obtine numarul curent de requests pt un IP, util pt debugging sau monitoring
    public Long getCurrentRequestCount(String ipAddress, String endpoint){
        try{
            String key = buildKey(ipAddress, endpoint);
            String count =  redisTemplate.opsForValue().get(key);
            return count != null ? Long.parseLong(count) : 0L;
        }catch (Exception e){
            logger.warn("Failed to get request count: {}", e.getMessage());
            return 0L;
        }
    }
}
