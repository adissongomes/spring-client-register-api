package br.com.adissongomes.client.exceptions;

public class ClientInexistenteException extends RuntimeException {
    public ClientInexistenteException() {
        super("Client nao cadastrado");
    }

    public ClientInexistenteException(String msg) {
        super(msg);
    }
}
