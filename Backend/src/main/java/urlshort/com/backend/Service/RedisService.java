package urlshort.com.backend.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

//service pt operatiuni redis (caching)
@Service
public class RedisService {
    private static final Logger logger = LoggerFactory.getLogger(RedisService.class);
    private static final String URL_CACHE_PREFIX = "url:"; //prefix pt keys in redis

    private static final long CACHE_TTL_HOURS = 24;//TTL (time to live) pt cache - 24 ore

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    //salveaza un url in cache
    //ttl: 24 ore si expira automat
    public void cacheUrl(String shortCode, String originalUrl){
        try{
            String key = URL_CACHE_PREFIX + shortCode;
            redisTemplate.opsForValue().set(key, originalUrl, CACHE_TTL_HOURS, TimeUnit.HOURS);
            logger.debug("URL cached: {}", key);
        } catch (Exception e){
            logger.warn("Fail to cache URL: {}", e.getMessage());//daca redis esueaza, logheaza si continua sa functioneze
        }
    }

    public String getCachedUrl(String shortCode){
        try{
            String key = URL_CACHE_PREFIX + shortCode;
            String value = redisTemplate.opsForValue().get(key);

            if(value != null){
                logger.debug("Cache HIT: {}", key);
                return value;
            }

            logger.debug("Cache MISS: {}", key);
            return null;
        }catch (Exception e){
            logger.warn("Failed to read from cache: {}", e.getMessage());
            return null;
        }
    }

    //sterge un url din cache
    //folosit cand url este sters, dezactivat, actualizat
    public void invalidateUrl(String shortCode){
        try{
            String key = URL_CACHE_PREFIX + shortCode;
            redisTemplate.delete(key);
            logger.debug("Cache invalidated: {}", key);
        }catch (Exception e){
            logger.warn("Failed to invalidate cache: {}", e.getMessage());
        }
    }

    //verifica daca redis este disponibil
    public boolean isRedisAvailable(){
        try{
            redisTemplate.getConnectionFactory().getConnection().ping();
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
