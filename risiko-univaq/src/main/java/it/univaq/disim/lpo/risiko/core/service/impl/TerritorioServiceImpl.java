package it.univaq.disim.lpo.risiko.core.service.impl;

import it.univaq.disim.lpo.risiko.core.service.TerritorioService;
import it.univaq.disim.lpo.risiko.core.datamodel.Giocatore;
import it.univaq.disim.lpo.risiko.core.datamodel.Territorio;

public class TerritorioServiceImpl implements TerritorioService {
 
    public void aggiungiArmate(Territorio territorio, int armate) {
        territorio.setArmate(territorio.getArmate() + armate);
    }

   
    public void rimuoviArmate(Territorio territorio, int armate) {
        territorio.setArmate(territorio.getArmate() - armate);
    }

   
    public void aggiungiProprietario(Territorio territorio, Giocatore proprietario) {
        territorio.setGiocatore(proprietario);
    }

 
    public void rimuoviProprietario(Territorio territorio) {
      
    }
}