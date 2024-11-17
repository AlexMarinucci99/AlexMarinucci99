package it.univaq.disim.lpo.risiko.core.service;

import java.io.IOException;
import java.util.List;

import it.univaq.disim.lpo.risiko.core.RisikoException;

import it.univaq.disim.lpo.risiko.core.datamodel.Giocatore;
import it.univaq.disim.lpo.risiko.core.datamodel.Gioco;

public interface GiocoService {
	public Gioco inizializzaPartita() throws InizializzaPartitaException;
	Gioco caricaGioco(String fileName) throws IOException, ClassNotFoundException;

	
	List<Giocatore> getOrdineGiocatori(Gioco gioco);
   	public boolean TurnoGiocatore(Giocatore giocatore, Gioco gioco)throws RisikoException;;
	
}