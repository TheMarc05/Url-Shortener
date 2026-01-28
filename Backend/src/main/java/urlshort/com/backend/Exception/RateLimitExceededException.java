package urlshort.com.backend.Exception;

//exception pt cand rate limit este depasit
//http status - 429 too many requests
public class RateLimitExceededException extends RuntimeException{
    public RateLimitExceededException(String message){
        super(message);
    }
}
