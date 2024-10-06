package it.univaq.disim.lpo.risiko.core;

import it.univaq.disim.lpo.risiko.core.service.*;
import it.univaq.disim.lpo.risiko.core.service.impl.*;

import it.univaq.disim.lpo.risiko.core.datamodel.*;


public class Runner {
	public static void main(String[] args) {
        GiocoService giocoService = new GiocoServiceImpl();
        
        boolean running = true;
        while (running) {
        try {
        	Gioco gioco = giocoService.inizializzaPartita();
            int numeroGiocatori = gioco.getGiocatori().size();
        
     
            // Calcolare il numero di armate per giocatore
            int armatePerGiocatore = giocoService.calcolaArmatePerGiocatore(numeroGiocatori);

            // Distribuzione iniziale delle armate
            giocoService.distribuzioneInizialeArmate(gioco.getGiocatori(), armatePerGiocatore);

            // Stampa i dettagli del gioco inizializzato
            for (Giocatore giocatore : gioco.getGiocatori()) {
                //System.out.println("Giocatore " + giocatore.getNome() + " ha ricevuto " + giocatore.getArmate() + " armate.");
                System.out.println("Colore delle armate: " + giocatore.getColore());
                //da mette un log per le armate
        
            }
           //fica

            System.out.println("Sta per iniziare la partita!");
            
            // Eseguire i turni dei giocatori
            boolean partitaInCorso = true;
            
            while (partitaInCorso) {
                for (Giocatore giocatore : gioco.getGiocatori()) {
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
