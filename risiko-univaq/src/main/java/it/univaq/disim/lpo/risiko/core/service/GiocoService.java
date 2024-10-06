package it.univaq.disim.lpo.risiko.core.service;

import java.io.IOException;
import java.util.List;

import it.univaq.disim.lpo.risiko.core.datamodel.Continente;
import it.univaq.disim.lpo.risiko.core.datamodel.Giocatore;
import it.univaq.disim.lpo.risiko.core.datamodel.Gioco;



public interface GiocoService {
	public Gioco inizializzaPartita() throws InizializzaPartitaException;
	Gioco caricaGioco(String filename) throws CloneNotSupportedException, IOException, ClassNotFoundException;

	

	void distribuzioneTerritori(List<Giocatore> giocatori, List<Continente> continenti);
	
	void distribuzioneInizialeArmate(List<Giocatore> giocatori,int  armatePerGiocatore);
	
   
	public int calcolaArmatePerGiocatore(int numeroGiocatori);
   	public boolean TurnoGiocatore(Giocatore giocatore, Gioco gioco);
	
}