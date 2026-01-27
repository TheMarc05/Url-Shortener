package urlshort.com.backend.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

//global exception handler - gestioneaza toatte exceptiile din aplicatie

//RestControllerAdvice = ControllerAdvice + ResponseBody
//-intercepteaza exceptiile din toate Controller-ele
//-returneaza automat JSON (nu HTML)

@RestControllerAdvice
public class GlobalExceptionHandler {
    //http status 404 NOT FOUND, resursa nu exista si clientul stie ca trebuie sa caute alt url
    @ExceptionHandler(UrlNotFoundException.class)//specifica ce tip de exceptie prinde aceasta metoda
    public ResponseEntity<ErrorResponse> handlerUrlNotFound(UrlNotFoundException ex){
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "URL_NOT_FOUND",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    //http status 400 BAD REQUEST, requestul clientului este invalid, clientul trebuie sa corecteze input-ul
    @ExceptionHandler(InvalidUrlException.class)
    public ResponseEntity<ErrorResponse> handleInvalidUrl(InvalidUrlException ex){
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "INVALID_URL",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    //handler pt validarea DTO-urilor, cand @Valid esueaza, Spring arunca MethodArgumentNotValidException
    //daca originalUrl este gol - @NotBlank esueaza
    //http status 400 BAD REQUEST
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException ex){

        Map<String, String> errors = new HashMap<>();

        //extrage toate erorile de validare
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        //construieste raspuns structurat
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "VALIDATION_ERROR");
        response.put("message", "Erori de validare in request");
        response.put("errors", errors);//detalii pt fiecare camp
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    //handler pt toate celelalte exceptii
    //http status 500 INTERNAL SERVER ERROR
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex){
        //loghez eroarea interna pt debugging
        ex.printStackTrace();

        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_SERVER_ERROR",
                "A aparut o eroare interna. Te rugam sa incerci mai tarziu",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    //clasa interna pt raspunsuri de eroare structurate
    //-type safety - compilatorul verifica structura
    //-documentare
    //-serializare JSON - converteste automat
    public static class ErrorResponse {
        private int status;
        private String error;
        private String message;
        private LocalDateTime timestamp;
        public ErrorResponse(int status, String error, String message, LocalDateTime timestamp) {
            this.status = status;
            this.error = error;
            this.message = message;
            this.timestamp = timestamp;
        }

        public int getStatus() {
            return status;
        }

        public String getError() {
            return error;
        }

        public String getMessage() {
            return message;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }
    }
}
