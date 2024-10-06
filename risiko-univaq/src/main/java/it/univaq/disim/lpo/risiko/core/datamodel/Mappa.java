package it.univaq.disim.lpo.risiko.core.datamodel;

import java.util.List;
import java.io.Serializable;

public class Mappa implements Serializable{
	private static final long serialVersionUID = 1L;
    private List<Continente> continenti;

	public Mappa(List<Continente> continenti) {
        this.continenti = continenti;
    }

    public List<Continente> getContinenti() {
        return this.continenti;
    }

    public void setContinenti(List<Continente> continenti) {
        this.continenti = continenti;
    }
 // Metodo per ottenere un continente dal nome
    public Continente getContinente(String nomeContinente) {
        for (Continente continente : continenti) {
            if (continente.getNome().equalsIgnoreCase(nomeContinente)) {
                return continente;
            }
        }
        return null; // Ritorna null se il continente non viene trovato
    }
}