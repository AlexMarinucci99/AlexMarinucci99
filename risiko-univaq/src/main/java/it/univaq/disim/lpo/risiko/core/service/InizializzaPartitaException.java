package it.univaq.disim.lpo.risiko.core.service;

import it.univaq.disim.lpo.risiko.core.RisikoException;

public class InizializzaPartitaException extends RisikoException{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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