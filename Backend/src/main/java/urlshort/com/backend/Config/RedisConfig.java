package urlshort.com.backend.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

//configuratie redis pt spring boot
//se configureaza conexiunea la Redis, configureaza serializarea, creeaza redis template pt operatiuni
//de ce redis template? - abstrage operatiunile redis(GET, SET, DELETE, etc), type-safe, spring management (dependency injection ready)
@Configuration
public class RedisConfig {
    //redis template pt operatiuni cu Redis
    //serializare keys sau value (pt obiecte complexe)
    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory){
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        //serializare pt keys (String)
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        //serializare pt values (String)
        template.setValueSerializer(stringSerializer);
        template.setHashValueSerializer(stringSerializer);

        template.afterPropertiesSet();//activeaza serializarea pt toate operatiunile

        return template;
    }
}
