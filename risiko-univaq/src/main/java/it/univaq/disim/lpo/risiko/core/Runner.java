package it.univaq.disim.lpo.risiko.core;

import it.univaq.disim.lpo.risiko.core.service.*;
import it.univaq.disim.lpo.risiko.core.service.impl.*;

import java.util.List;

import it.univaq.disim.lpo.risiko.core.datamodel.*;


public class Runner {
	public static void main(String[] args) {
        GiocoService giocoService = new GiocoServiceImpl();
        GiocatoreService giocatoreService = new GiocatoreServiceImpl();
        
        boolean running = true;
        while (running) {
        try {
        	Gioco gioco = giocoService.inizializzaPartita();
        	 List<Giocatore> ordineGiocatori = giocoService.getOrdineGiocatori(gioco);
        
     
        	// Distribuisci le armate iniziali solo se non sono già state distribuite
             if (!gioco.isArmateDistribuite()) {
                 int numeroGiocatori = ordineGiocatori.size();
                 int armatePerGiocatore = giocatoreService.calcolaArmatePerGiocatore(numeroGiocatori);
                 giocatoreService.distribuzioneInizialeArmate(gioco.getGiocatori(), armatePerGiocatore);
                 
                 // Imposta il flag per evitare la ridistribuzione
                 gioco.setArmateDistribuite(true);
             }
      

            System.out.println("Sta per iniziare la partita!");
            
            // Eseguire i turni dei giocatori
            boolean partitaInCorso = true;
            
            while (partitaInCorso) {
                for (Giocatore giocatore : ordineGiocatori) {
                    partitaInCorso = giocoService.TurnoGiocatore(giocatore, gioco);
                    if (!partitaInCorso) {
                        break;
                    }
                }
            }
        } catch (InizializzaPartitaException e) {
            System.out.println("Errore durante l'inizializzazione della partita: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Si è verificato un errore: " + e.getMessage());
        }
    }
}
}
