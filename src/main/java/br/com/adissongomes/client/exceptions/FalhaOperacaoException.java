package br.com.adissongomes.client.exceptions;

public class FalhaOperacaoException extends RuntimeException {
    public FalhaOperacaoException() {
        super("Erro inexperado na operação");
    }

    public FalhaOperacaoException(String msg) {
        super(msg);
    }

    public FalhaOperacaoException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}
