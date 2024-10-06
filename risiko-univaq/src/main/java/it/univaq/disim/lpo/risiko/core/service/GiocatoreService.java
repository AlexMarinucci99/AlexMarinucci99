package it.univaq.disim.lpo.risiko.core.service;

import it.univaq.disim.lpo.risiko.core.datamodel.Giocatore;
import it.univaq.disim.lpo.risiko.core.datamodel.Territorio;

public interface GiocatoreService {
    void aggiungiTerritorio(Giocatore giocatore, Territorio territorio);
    void rimuoviTerritorio(Giocatore giocatore, Territorio territorio);
    void aggiungiArmate(Giocatore giocatore, int armate);
    void rimuoviArmate(Giocatore giocatore, int armate);
    void trasferisciArmate(Giocatore giocatore, Territorio da, Territorio a, int armate);
    boolean controllaVittoria(Giocatore giocatore);
    
}