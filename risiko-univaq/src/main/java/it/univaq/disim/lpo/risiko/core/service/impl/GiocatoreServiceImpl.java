package it.univaq.disim.lpo.risiko.core.service.impl;




import it.univaq.disim.lpo.risiko.core.datamodel.Giocatore;

import it.univaq.disim.lpo.risiko.core.datamodel.Territorio;
import it.univaq.disim.lpo.risiko.core.service.GiocatoreService;

public class GiocatoreServiceImpl implements GiocatoreService {

    public void aggiungiTerritorio(Giocatore giocatore, Territorio territorio) {
        giocatore.getTerritori_controllati().add(territorio);
    }

    
    public void rimuoviTerritorio(Giocatore giocatore, Territorio territorio) {
        giocatore.getTerritori_controllati().remove(territorio);
    }

    
    public void aggiungiArmate(Giocatore giocatore, int armate) {
        giocatore.setArmate(giocatore.getArmate() + armate);
    }

  
    public void rimuoviArmate(Giocatore giocatore, int armate) {
        giocatore.setArmate(giocatore.getArmate() - armate);
    }

    public void trasferisciArmate(Giocatore giocatore, Territorio da, Territorio a, int armate) {
        
    }

    
    public boolean controllaVittoria(Giocatore giocatore) {
        
        return false;
    }

}