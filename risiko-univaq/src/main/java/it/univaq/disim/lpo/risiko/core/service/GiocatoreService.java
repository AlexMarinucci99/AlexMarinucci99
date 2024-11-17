package it.univaq.disim.lpo.risiko.core.service;

import java.util.List;

import it.univaq.disim.lpo.risiko.core.datamodel.Continente;
import it.univaq.disim.lpo.risiko.core.datamodel.Giocatore;
import it.univaq.disim.lpo.risiko.core.datamodel.Territorio;

public interface GiocatoreService {
    void aggiungiTerritorio(Giocatore giocatore, Territorio territorio);
    void rimuoviTerritorio(Giocatore giocatore, Territorio territorio);
    void aggiungiArmate(Giocatore giocatore, int armate);
    void rimuoviArmate(Giocatore giocatore, int armate);
    void trasferisciArmate(Giocatore giocatore, Territorio da, Territorio a, int armate);
    boolean controllaVittoria(Giocatore giocatore);
    void distribuzioneInizialeArmate(List<Giocatore> giocatori,int  armatePerGiocatore);
	List<Giocatore> creaGiocatori(int numeroGiocatori);
	List<Giocatore> lancioDadiPerPrimoGiocatore(List<Giocatore> giocatori);
	int calcolaArmatePerGiocatore(int numeroGiocatori);

    void distribuzioneTerritori(List<Giocatore> giocatori, List<Continente> continenti);
		
	

}