package it.univaq.disim.lpo.risiko.core;

public class RisikoException extends Exception {
	
    private static final long serialVersionUID = 1L;
	public RisikoException() {
        super();
    }

    public RisikoException(String message) {
        super(message);
    }

    public RisikoException(String message, Throwable cause) {
        super(message, cause);
    }

    public RisikoException(Throwable cause) {
        super(cause);
    }
}


