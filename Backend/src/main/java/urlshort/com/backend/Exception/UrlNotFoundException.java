package urlshort.com.backend.Exception;

public class UrlNotFoundException extends RuntimeException{
    public UrlNotFoundException(String message){
        super(message);
    }
    public UrlNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
