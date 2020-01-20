package br.com.adissongomes.client.infra;

import br.com.adissongomes.client.exceptions.ClientExistenteException;
import br.com.adissongomes.client.exceptions.ClientInexistenteException;
import br.com.adissongomes.client.exceptions.FalhaOperacaoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class APIControllerAdvice {

    @ExceptionHandler(ClientInexistenteException.class)
    public ResponseEntity clientInexistente(ClientInexistenteException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErroResponse(e.getMessage()));
    }

    @ExceptionHandler(ClientExistenteException.class)
    public ResponseEntity clientExistente(ClientExistenteException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErroResponse(e.getMessage()));
    }

    @ExceptionHandler(FalhaOperacaoException.class)
    public ResponseEntity clientExistente(FalhaOperacaoException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErroResponse(e.getMessage()));
    }

    private static class ErroResponse {
        private String message;

        public ErroResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

}
