package it.univaq.disim.lpo.risiko.core.datamodel;


import java.util.List;


import java.io.Serializable;

public class Gioco implements Serializable{
	private static final long serialVersionUID = 1L;
    private String fase;
    private List<Giocatore> giocatori;
    private Mappa mappa;
    private int faccia_dado;
    private List<CartaTerritorio> carteTerritorio;
    private List<CartaObiettivo> carteObiettivo;

	public Gioco(String fase, List<Giocatore> giocatori, Mappa mappa, int faccia_dado, List<CartaTerritorio> carteTerritorio, List<CartaObiettivo> carteObiettivo) {
        this.fase = fase;	
        setGiocatori(giocatori); 
        this.mappa = mappa;
        this.faccia_dado = faccia_dado;
        this.carteTerritorio = carteTerritorio;
        this.carteObiettivo = carteObiettivo;
    }

    public String getFase() {
        return this.fase;
    }

    public void setFase(String fase) {
        this.fase = fase;
    }

    public List<Giocatore> getGiocatori() {
        return this.giocatori;
    }

    public void setGiocatori(List<Giocatore> giocatori) {
        if (giocatori.size() < 2 || giocatori.size() > 6) {
            throw new IllegalArgumentException("Il numero di giocatori deve essere compreso tra 2 e 6.");
        }
      
        this.giocatori = giocatori;
    }

    public Mappa getMappa() {
        return this.mappa;
    }

    public void setMappa(Mappa mappa) {
        this.mappa = mappa;
    }

    public int getFaccia_dado() {
        return this.faccia_dado;
    }

    public void setFaccia_dado(int faccia_dado) {
        this.faccia_dado = faccia_dado;
    }

    public List<CartaTerritorio> getCarteTerritorio() {
        return this.carteTerritorio;
    }

    public void setCarteTerritorio(List<CartaTerritorio> carteTerritorio) {
        this.carteTerritorio = carteTerritorio;
    }

    public List<CartaObiettivo> getCarteObiettivo() {
        return this.carteObiettivo;
    }

    public void setCarteObiettivo(List<CartaObiettivo> carteObiettivo) {
        this.carteObiettivo = carteObiettivo;
    }
}