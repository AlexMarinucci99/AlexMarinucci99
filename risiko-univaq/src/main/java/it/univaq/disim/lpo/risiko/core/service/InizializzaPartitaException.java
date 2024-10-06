package it.univaq.disim.lpo.risiko.core.service;

public class InizializzaPartitaException extends Exception{
    public InizializzaPartitaException() {
        super();
    }

    public InizializzaPartitaException(String messaggio) {
        super(messaggio);
    }

    public InizializzaPartitaException(String messaggio, Throwable cause) {
        super(messaggio, cause);
    }

    public InizializzaPartitaException(Throwable cause) {
        super(cause);
    }

}