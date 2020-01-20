package br.com.adissongomes.client.exceptions;

import br.com.adissongomes.client.service.model.ClientModel;

public class ClientExistenteException extends RuntimeException {
    public ClientExistenteException() {
        super("Client ja cadastrado");
    }

    public ClientExistenteException(String msg) {
        super(msg);
    }
}
