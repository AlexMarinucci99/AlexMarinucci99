package it.univaq.disim.lpo.risiko.core.datamodel;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Territorio implements Serializable{
	private static final long serialVersionUID = 1L;
    private String nome;
    private Giocatore giocatore;
    private int armate;
    private Continente continente;
    private List<Territorio> territoriAdiacenti;


    public Territorio(String nome, Giocatore giocatore, int armate, Continente continente) {
        this.nome = nome;
        this.giocatore = giocatore;
        this.armate = armate;
        this.continente = continente;
        this.territoriAdiacenti = new ArrayList<>();
     
    }
    public Territorio(String nome) {
    	this(nome, null, 0, null);                             
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Giocatore getGiocatore() {
        return giocatore;
    }

    public void setGiocatore(Giocatore giocatore) {
        this.giocatore = giocatore;
    }

    public int getArmate() {
        return armate;
    }

    public void setArmate(int armate) {
        this.armate = armate;
    }
    
    public Continente getContinente() {
        return continente;
    }

    public void setContinente(Continente continente) {
        this.continente = continente;
    }
    public int getNumeroArmate() {
        return armate;
    }

    public void aggiungiArmate(int armate) {
        this.armate = armate;
    }
    public void rimuoviArmate(int numero) {
        if (numero <= this.armate) {
            this.armate -= numero;
        } else {
            System.out.println("Errore: Non ci sono abbastanza armate da rimuovere.");
        }
    }
    public List<Territorio> getTerritoriAdiacenti() {
        return territoriAdiacenti;
    }

    public void aggiungiTerritorioAdiacente(Territorio territorio) {
        if (!this.territoriAdiacenti.contains(territorio)) {
            this.territoriAdiacenti.add(territorio);
            territorio.aggiungiTerritorioAdiacenteSingolo(this); // Assicura bidirezionalità
        }
    }

    // Metodo privato per evitare ricorsione infinita
    private void aggiungiTerritorioAdiacenteSingolo(Territorio territorio) {
        if (!this.territoriAdiacenti.contains(territorio)) {
            this.territoriAdiacenti.add(territorio);
        }
    }

   
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Territorio that = (Territorio) o;
        return Objects.equals(nome, that.nome);
    }

   
    public int hashCode() {
        return Objects.hash(nome);
    }
	
}