package it.univaq.disim.lpo.risiko.core.service;


import it.univaq.disim.lpo.risiko.core.datamodel.Territorio;

public interface TerritorioService {
    void aggiungiArmate(Territorio territorio, int armate);
    void rimuoviArmate(Territorio territorio, int armate);
   
}