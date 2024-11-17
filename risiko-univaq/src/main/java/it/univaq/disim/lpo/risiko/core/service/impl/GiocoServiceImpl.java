package it.univaq.disim.lpo.risiko.core.service.impl;

import it.univaq.disim.lpo.risiko.core.datamodel.*;
import it.univaq.disim.lpo.risiko.core.service.*;
import java.io.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class GiocoServiceImpl implements GiocoService {

   
    private final FileService fileService = FileServiceImpl.getInstance();
    private final GiocatoreService giocatoreService = new GiocatoreServiceImpl();
    private final CartaObiettivoService obiettivoService = new CartaObiettivoServiceImpl();
    private final MappaService mappaService;

    public GiocoServiceImpl() {
        try {
            this.mappaService = new MappaServiceImpl();
        } catch (InizializzaPartitaException e) {
            throw new RuntimeException("Errore nell'inizializzazione della mappa", e);
        }
    }  
    public List<Giocatore> getOrdineGiocatori(Gioco gioco) {
        return gioco.getOrdineGiocatori();
    }
    
    public Gioco inizializzaPartita() throws InizializzaPartitaException {

        System.out.print("\nSelezionare (1) nuova partita o (2) per caricare una partita esistente: ");
        Integer modo = SingletonMain.getInstance().readIntegerUntilPossibleValue(new Integer[]{1, 2});

        switch (modo) {
            case 1:
                return avviaNuovaPartita();
            case 2:
            	 
                return caricaPartitaEsistente();
            default:
                throw new InizializzaPartitaException("Opzione non valida.");
        }
    }

    private Gioco caricaPartitaEsistente() throws InizializzaPartitaException {
        while (true) {
            System.out.print("\nSeleziona file da caricare: ");
            String valore = SingletonMain.getInstance().readString();
            try {
                Gioco giocoCaricato = fileService.caricaGioco(valore);
                if (giocoCaricato.isArmateDistribuite()) {
                    System.out.println("Caricata una partita esistente con armate già distribuite.");
                } else {
                    System.out.println("Caricata una partita esistente senza armate distribuite.");
                }
                return giocoCaricato;
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Errore nel caricamento del file: " + e.getMessage());
            }
        }
    }




    private Gioco avviaNuovaPartita() throws InizializzaPartitaException {
        // Step 1: Inizializzazione del numero di giocatori
        System.out.print("\nSelezione in quanti giocatori volete giocare (2-6): ");
        Integer numeroGiocatori = SingletonMain.getInstance().readIntegerUntilPossibleValue(new Integer[]{2, 3, 4, 5, 6});
        System.out.println();

        // Step 2: Creazione dei giocatori
        List<Giocatore> giocatori = giocatoreService.creaGiocatori(numeroGiocatori);

        // Step 3: Generazione degli obiettivi
        List<CartaObiettivo> obiettivi = obiettivoService.generaObiettiviCasuali(giocatori.size());
        
        CartaObiettivoServiceImpl.assegnaObiettiviCasuali(giocatori, obiettivi);

        // Step 4: Inizializzazione della mappa
        List<Continente> continenti = mappaService.inizializzaContinentiETerritori();
        Mappa mappa = mappaService.inizializzaMappa(continenti);

        // Step 5: Creazione del gioco
        Gioco gioco = new Gioco("Inizio", giocatori, mappa, 6, new ArrayList<>(), new ArrayList<>());
        
        // Step 6: Determinazione dell'ordine dei giocatori
        List<Giocatore> ordineGiocatori = giocatoreService.lancioDadiPerPrimoGiocatore(giocatori);
        gioco.setOrdineGiocatori(ordineGiocatori);
        System.out.println("\nIl giocatore " + ordineGiocatori.get(0).getNome() + " inizia per primo!");

        // Step 7: Distribuzione dei territori
        giocatoreService.distribuzioneTerritori(ordineGiocatori, mappa.getContinenti());
        

        return gioco;
    }



    public Gioco caricaGioco(String fileName) throws IOException, ClassNotFoundException {
        return fileService.caricaGioco(fileName);
    }

 

   

    public boolean verificaVittoria(Giocatore giocatore, Gioco gioco) {
        CartaObiettivo obiettivo = giocatore.getObiettivo();

        switch (obiettivo.getDescrizione()) {
            case "Conquistare 24 territori":
                if (giocatore.getTerritori_controllati().size() >= 24) {
                    dichiaraVittoria(giocatore);
                    return true;
                }
                break;

            case "Conquistare la totalità del Nord America e dell'Africa":
                if (haConquistatoContinente(giocatore, "Nord America", gioco) &&
                    haConquistatoContinente(giocatore, "Africa", gioco)) {
                    dichiaraVittoria(giocatore);
                    return true;
                }
                break;

            case "Conquistare la totalità del Nord America e dell'Oceania":
                if (haConquistatoContinente(giocatore, "Nord America", gioco) &&
                    haConquistatoContinente(giocatore, "Oceania", gioco)) {
                    dichiaraVittoria(giocatore);
                    return true;
                }
                break;

            case "Conquistare la totalità dell'Asia e del Sud America":
                if (haConquistatoContinente(giocatore, "Asia", gioco) &&
                    haConquistatoContinente(giocatore, "Sud America", gioco)) {
                    dichiaraVittoria(giocatore);
                    return true;
                }
                break;

            case "Conquistare la totalità dell'Asia e dell'Africa":
                if (haConquistatoContinente(giocatore, "Asia", gioco) &&
                    haConquistatoContinente(giocatore, "Africa", gioco)) {
                    dichiaraVittoria(giocatore);
                    return true;
                }
                break;

            case "Conquistare 30 territori e occupare ognuno con almeno 2 armate":
                long territoriConAlmenoDueArmate = giocatore.getTerritori_controllati().stream()
                    .filter(t -> t.getNumeroArmate() >= 2)
                    .count();
                if (territoriConAlmenoDueArmate >= 30) {
                    dichiaraVittoria(giocatore);
                    return true;
                }
                break;

            default:
                System.out.println("Obiettivo non riconosciuto.");
                break;
        }

        return false;
    }

    // Metodo privato per verificare se un giocatore ha conquistato un continente
    private boolean haConquistatoContinente(Giocatore giocatore, String nomeContinente, Gioco gioco) {
        Continente continente = gioco.getMappa().getContinente(nomeContinente);
        if (continente == null) {
            System.out.println("Il continente " + nomeContinente + " non esiste nella mappa.");
            return false;
        }
        return giocatore.getTerritori_controllati().containsAll(continente.getTerritori());
    }

    // Metodo privato per dichiarare la vittoria di un giocatore
    private void dichiaraVittoria(Giocatore giocatore) {
        System.out.println("Congratulazioni " + giocatore.getNome() + "! Hai completato il tuo obiettivo e hai vinto la partita!");
        FileServiceImpl.getInstance().writeLog("Il giocatore " + giocatore.getNome() + " ha vinto la partita completando l'obiettivo: " + giocatore.getObiettivo().getDescrizione());
        
    }
  
    public boolean TurnoGiocatore(Giocatore giocatore, Gioco gioco) {

    	// Calcolo delle armate per il giocatore
        int armateTerritori = Math.max(3, giocatore.getTerritori_controllati().size() / 3);
        int armateContinenti = calcolaArmateContinenti(giocatore, gioco.getMappa().getContinenti());
        int armateTotali = armateTerritori + armateContinenti;

        System.out.println("Giocatore " + giocatore.getNome() + " riceve " + armateTotali + " armate (Territori: " + armateTerritori + ", Continenti: " + armateContinenti + ").");
        FileServiceImpl.getInstance().writeLog("Giocatore " + giocatore.getNome() + " riceve " + armateTotali + " armate (Territori: " + armateTerritori + ", Continenti: " + armateContinenti + ").");

    	
    	
        // Distribuzione delle armate calcolate
        distribuzioneArmate(giocatore, armateTotali);
  

    	
        boolean turnoTerminato = false;
        while (!turnoTerminato) {
            System.out.println("Turno di " + giocatore.getNome() + ":");
            System.out.println("1. Visualizza Obiettivo");
            System.out.println("2. Visualizza Territori Controllati");
            System.out.println("3. Attacca");
            System.out.println("4. Spostamento Armate ");
            System.out.println("5. Termina Turno");
            System.out.println("6. Salva partita e esci");
            System.out.print("Scegli un'opzione: ");

            Integer[] opzioniValide = {1, 2, 3, 4 ,5, 6};
            int scelta = SingletonMain.getInstance().readIntegerUntilPossibleValue(opzioniValide);

            switch (scelta) {
                case 1:
                    visualizzaObiettivo(giocatore);
                    break;
                case 2:
                    visualizzaTerritoriControllati(giocatore);
                    break;
                
                	
                case 3:
                    attaccoGiocatore(giocatore, gioco);
                    break;

                      
                case 4:
                	try {
                        spostamentoArmate(giocatore);  // Usa il nuovo metodo per il movimento delle armate
                        // Concludi il turno dopo lo spostamento
                        turnoTerminato = true;
                        System.out.println("Turno di " + giocatore.getNome() + " terminato dopo lo spostamento delle armate.");
                        FileServiceImpl.getInstance().writeLog("Turno di " + giocatore.getNome() + " terminato dopo lo spostamento delle armate.");
                    } catch (Exception e) {
                        System.out.println("Errore durante lo spostamento delle armate: " + e.getMessage());
                    }
                    
                    // Dopo lo spostamento, verifica se il giocatore ha vinto
                    if (verificaVittoria(giocatore, gioco)) {
                        return false; // Fine del gioco
                    }
                    break;
                case 5:
                    turnoTerminato = true;
                    System.out.println("Turno di " + giocatore.getNome() + " terminato.");
                    FileServiceImpl.getInstance().writeLog("Turno di " + giocatore.getNome() + " terminato.");

                    break;
                case 6:
                	 // Salva partita e esci
                    try {
                        System.out.print("Inserisci il nome del file per salvare la partita: ");
                        String filename = SingletonMain.getInstance().readString();
                        FileServiceImpl.getInstance().salvaGioco(gioco, filename);
                  
                    } catch (IOException e) {
                        System.out.println("Errore durante il salvataggio della partita: " + e.getMessage());
                    }
                    return false;
                default:
                    System.out.println("Scelta non valida. Riprova.");
            }
        }
        return true;  // Continua il gioco
    }

    private Territorio selezionaTerritorioAdiacente(Territorio territorioAttaccante) {
        List<Territorio> territoriAttaccabili = territorioAttaccante.getTerritoriAdiacenti().stream()
            .filter(t -> !t.getGiocatore().equals(territorioAttaccante.getGiocatore())) // Filtra i territori non posseduti dall'attaccante
            .collect(Collectors.toList());

        if (territoriAttaccabili.isEmpty()) {
            System.out.println("Non ci sono territori adiacenti attaccabili.");
            return null;
        }

        System.out.println("Seleziona un territorio adiacente a " + territorioAttaccante.getNome() + " da attaccare:");
        for (int i = 0; i < territoriAttaccabili.size(); i++) {
            System.out.println(i + ". " + territoriAttaccabili.get(i).getNome() + " (Giocatore: " + territoriAttaccabili.get(i).getGiocatore().getNome() + ")");
        }

        int indiceTerritorio = SingletonMain.getInstance().readIntegerUntilPossibleValue(
            IntStream.range(0, territoriAttaccabili.size()).boxed().toArray(Integer[]::new)
        );

        return territoriAttaccabili.get(indiceTerritorio);
    }

 

    // Metodo per selezionare un territorio per l'attacco
    private Territorio selezionaTerritorioPerAttacco(Giocatore giocatore) {
        List<Territorio> territoriAttaccabili = giocatore.getTerritori_controllati().stream()
            .filter(t -> t.getNumeroArmate() >= 2)
            .collect(Collectors.toList());

        if (territoriAttaccabili.isEmpty()) {
            System.out.println("Non hai territori con abbastanza armate per attaccare.");
            return null;
        }

        for (int i = 0; i < territoriAttaccabili.size(); i++) {
            System.out.println(i + ". " + territoriAttaccabili.get(i).getNome() + " (" + territoriAttaccabili.get(i).getNumeroArmate() + " armate)");
        }

        int indiceTerritorio = SingletonMain.getInstance().readInteger();
        return territoriAttaccabili.get(indiceTerritorio);
    }

    // Metodo per chiedere quante armate spostare dopo una conquista
    private int scegliQuanteArmateSpostare(Territorio territorioAttaccante) {
        int armateMinime = 1;
        int armateMassime = territorioAttaccante.getNumeroArmate() - 1;
        System.out.println("Quante armate vuoi spostare? (Minimo 1, massimo " + armateMassime + "):");

        int armateDaSpostare;
        do {
            armateDaSpostare = SingletonMain.getInstance().readInteger();
        } while (armateDaSpostare < armateMinime || armateDaSpostare > armateMassime);

        return armateDaSpostare;
    }
    private void attaccoGiocatore(Giocatore giocatore, Gioco gioco) {
        boolean attaccoTerminato = false;

        while (!attaccoTerminato) {
            // Seleziona territorio di partenza per l'attacco
            System.out.println("Seleziona il territorio da cui vuoi attaccare:");
            Territorio territorioAttaccante = selezionaTerritorioPerAttacco(giocatore);

            if (territorioAttaccante == null) {
                System.out.println("Non hai più territori con armate sufficienti per attaccare.");
                break;
            }

            List<Territorio> territoriAttaccabili = territorioAttaccante.getTerritoriAdiacenti().stream()
            	    .filter(t -> !t.getGiocatore().equals(giocatore) && t.getNumeroArmate() > 0)
            	    .collect(Collectors.toList());

            
            
            territoriAttaccabili.forEach(t -> {
                System.out.println("Territorio adiacente attaccabile: " + t.getNome() + ", Posseduto da: " + t.getGiocatore().getNome() + " (HashCode giocatore: " + System.identityHashCode(t.getGiocatore()) + ")");
            });


            if (territoriAttaccabili.isEmpty()) {
                System.out.println("Non ci sono territori adiacenti attaccabili. Vuoi selezionare un altro territorio? (s/n):");
                String risposta = SingletonMain.getInstance().readString();
                if (risposta.equalsIgnoreCase("n")) {
                    break;  // Esce dalla fase di attacco se il giocatore decide di non selezionare un altro territorio
                } else {
                    continue; // Se vuole selezionare un altro territorio, continua il ciclo
                }
            }

            // Stampa i territori adiacenti attaccabili
            System.out.println("Territori adiacenti di " + territorioAttaccante.getNome() + ":");
            for (int i = 0; i < territoriAttaccabili.size(); i++) {
                System.out.println(i + ". " + territoriAttaccabili.get(i).getNome() + " (Giocatore: " + territoriAttaccabili.get(i).getGiocatore().getNome() + ")");
            }

            // Seleziona il territorio da attaccare
            int indiceTerritorioDifensore = SingletonMain.getInstance().readIntegerUntilPossibleValue(
                    IntStream.range(0, territoriAttaccabili.size()).boxed().toArray(Integer[]::new)
            );
            Territorio territorioDifensore = territoriAttaccabili.get(indiceTerritorioDifensore);

            // Chiedi al difensore quante armate utilizzare
            int numDadiDifesa = Math.min(territorioDifensore.getNumeroArmate(), 3);
            System.out.println("Difensore, quante armate vuoi usare per difenderti? (1-" + numDadiDifesa + "):");
            int dadiDifesa = SingletonMain.getInstance().readIntegerUntilPossibleValue(new Integer[]{1, 2, 3});

            // Chiedi all'attaccante quante armate utilizzare
            int maxDadiAttacco = Math.min(territorioAttaccante.getNumeroArmate() - 1, 3);
            System.out.println("Attaccante, quante armate vuoi usare per attaccare? (1-" + maxDadiAttacco + "):");
            int dadiAttacco = SingletonMain.getInstance().readIntegerUntilPossibleValue(new Integer[]{1, 2, 3});

            // Attacca il territorio selezionato
            boolean territorioConquistato = attacca(giocatore, territorioAttaccante, territorioDifensore, dadiAttacco, dadiDifesa);

            // Se il territorio è stato conquistato, chiedi quante armate spostare
            if (territorioConquistato) {
                int armateSpostate = scegliQuanteArmateSpostare(territorioAttaccante);
                territorioAttaccante.rimuoviArmate(armateSpostate);
                territorioDifensore.aggiungiArmate(armateSpostate);
            }

            // Chiedi se continuare l'attacco o terminare
            System.out.print("Vuoi continuare ad attaccare? (s/n): ");
            String risposta = SingletonMain.getInstance().readString();
            if (risposta.equalsIgnoreCase("n")) {
                attaccoTerminato = true;
            }
            territoriAttaccabili.forEach(t -> {
                System.out.println("Territorio adiacente: " + t.getNome() + ", Posseduto da: " + t.getGiocatore().getNome());
            });
            System.out.println("Attaccante: " + giocatore.getNome() + " (" + System.identityHashCode(giocatore) + ")");
            territoriAttaccabili.forEach(t -> {
                System.out.println("Territorio adiacente: " + t.getNome() + ", Posseduto da: " + t.getGiocatore().getNome() + " (" + System.identityHashCode(t.getGiocatore()) + ")");
            });


        }
    }


    private boolean attacca(Giocatore giocatore, Territorio territorioAttaccante, Territorio territorioDifensore, int dadiAttacco, int dadiDifesa) {
        // Stampa lo stato iniziale
        System.out.println("Attacco dal territorio " + territorioAttaccante.getNome() + " (armate: " + territorioAttaccante.getNumeroArmate() + ")");
        System.out.println("Difesa del territorio " + territorioDifensore.getNome() + " (armate: " + territorioDifensore.getNumeroArmate() + ")");

        // L'attaccante lancia i dadi
        List<Integer> risultatiAttacco = lanciaDadi(dadiAttacco);
        System.out.println("L'attaccante ha lanciato i dadi: " + risultatiAttacco);

        // Il difensore lancia i dadi
        List<Integer> risultatiDifesa = lanciaDadi(dadiDifesa);
        System.out.println("Il difensore ha lanciato i dadi: " + risultatiDifesa);

        // Confronto dei dadi
        int armatePerseAttaccante = 0;
        int armatePerseDifensore = 0;

        for (int i = 0; i < Math.min(risultatiAttacco.size(), risultatiDifesa.size()); i++) {
            if (risultatiAttacco.get(i) > risultatiDifesa.get(i)) {
                armatePerseDifensore++;
            } else {
                armatePerseAttaccante++;
            }
        }

        // Aggiornamento delle armate sui territori
        territorioAttaccante.rimuoviArmate(armatePerseAttaccante);
        territorioDifensore.rimuoviArmate(armatePerseDifensore);

        System.out.println("Risultato dell'attacco: " + armatePerseAttaccante + " armate perse dall'attaccante, " + armatePerseDifensore + " armate perse dal difensore.");

        // Se il difensore ha perso tutte le armate, il territorio è conquistato
        if (territorioDifensore.getNumeroArmate() == 0) {
            System.out.println("Il territorio " + territorioDifensore.getNome() + " è stato conquistato!");
            territorioDifensore.setGiocatore(giocatore); // Il giocatore conquista il territorio
            territorioDifensore.aggiungiArmate(1); // Sposta almeno un'armata
            territorioAttaccante.rimuoviArmate(1); // Rimuove almeno un'armata dall'attaccante
            giocatore.aggiungiTerritorio(territorioDifensore); // Aggiunge il territorio conquistato ai territori del giocatore
            return true;
        } else {
            return false;
        }
    }

    // Metodo per simulare il lancio dei dadi (restituisce una lista di risultati in ordine decrescente)
    private List<Integer> lanciaDadi(int numDadi) {
        Random random = new Random();
        List<Integer> risultati = new ArrayList<>();
        for (int i = 0; i < numDadi; i++) {
            risultati.add(random.nextInt(6) + 1); // Lancia un dado (valore da 1 a 6)
        }
        risultati.sort(Collections.reverseOrder()); // Ordina i risultati in ordine decrescente
        return risultati;
    }
 
    
    private void visualizzaObiettivo(Giocatore giocatore) {
        CartaObiettivo obiettivo = giocatore.getObiettivo();
        if (obiettivo != null) {
            System.out.println("Il tuo obiettivo è: " + obiettivo.getDescrizione());
        } else {
            System.out.println("Nessun obiettivo assegnato.");
        }
    }

    private void visualizzaTerritoriControllati(Giocatore giocatore) {
        System.out.println("Territori controllati da " + giocatore.getNome() + ":");
        for (Territorio territorio : giocatore.getTerritori_controllati()) {
            System.out.println("- " + territorio.getNome() + " con " + territorio.getNumeroArmate() + " armate");
        }
    }
    
   
    private void spostamentoArmate(Giocatore giocatore) {
        // Mostra tutti i territori controllati dal giocatore
        List<Territorio> territoriControllati = giocatore.getTerritori_controllati();

        if (territoriControllati.isEmpty()) {
            System.out.println("Non controlli nessun territorio per spostare le armate.");
            return;
        }

        System.out.println("Seleziona il territorio da cui vuoi spostare le armate:");
        for (int i = 0; i < territoriControllati.size(); i++) {
            System.out.println(i + ". " + territoriControllati.get(i).getNome() + " (" + territoriControllati.get(i).getNumeroArmate() + " armate)");
        }

        int indicePartenza = SingletonMain.getInstance().readIntegerUntilPossibleValue(
            IntStream.range(0, territoriControllati.size()).boxed().toArray(Integer[]::new)
        );
        Territorio territorioPartenza = territoriControllati.get(indicePartenza);

        if (territorioPartenza.getNumeroArmate() <= 1) {
            System.out.println("Non hai abbastanza armate per spostarle da questo territorio. Deve rimanere almeno una armata.");
            return;
        }

        // Seleziona un territorio adiacente in cui spostare le armate
        List<Territorio> territoriAdiacentiPosseduti = territorioPartenza.getTerritoriAdiacenti().stream()
            .filter(t -> t.getGiocatore().equals(giocatore)) // Solo territori posseduti dal giocatore
            .collect(Collectors.toList());

        if (territoriAdiacentiPosseduti.isEmpty()) {
            System.out.println("Non ci sono territori adiacenti controllati in cui spostare le armate.");
            return;
        }

        System.out.println("Seleziona il territorio di destinazione:");
        for (int i = 0; i < territoriAdiacentiPosseduti.size(); i++) {
            System.out.println(i + ". " + territoriAdiacentiPosseduti.get(i).getNome() + " (" + territoriAdiacentiPosseduti.get(i).getNumeroArmate() + " armate)");
        }

        int indiceDestinazione = SingletonMain.getInstance().readIntegerUntilPossibleValue(
            IntStream.range(0, territoriAdiacentiPosseduti.size()).boxed().toArray(Integer[]::new)
        );
        Territorio territorioDestinazione = territoriAdiacentiPosseduti.get(indiceDestinazione);

        // Effettua lo spostamento delle armate
        System.out.println("Quante armate vuoi spostare? (Minimo 1, massimo " + (territorioPartenza.getNumeroArmate() - 1) + "):");
        int armateDaSpostare = SingletonMain.getInstance().readIntegerUntilPossibleValue(
            IntStream.range(1, territorioPartenza.getNumeroArmate()).boxed().toArray(Integer[]::new)
        );

        territorioPartenza.rimuoviArmate(armateDaSpostare);
        territorioDestinazione.aggiungiArmate(armateDaSpostare);

        System.out.println("Hai spostato " + armateDaSpostare + " armate da " + territorioPartenza.getNome() + " a " + territorioDestinazione.getNome() + ". Il tuo turno è concluso.");
    }


 
    private int calcolaArmateContinenti(Giocatore giocatore, List<Continente> continenti) {
        int armateBonus = 0;

        for (Continente continente : continenti) {
            boolean possiedeTuttiITerritori = true;
            for (Territorio territorio : continente.getTerritori()) {
                if (!territorio.getGiocatore().equals(giocatore)) {
                    possiedeTuttiITerritori = false;
                    break;
                }
            }

            if (possiedeTuttiITerritori) {
                switch (continente.getNome()) {
                    case "Oceania":
                        armateBonus += 2;
                        break;
                    case "Europa":
                        armateBonus += 5;
                        break;
                    case "Sud America":
                        armateBonus += 2;
                        break;
                    case "America del Nord":
                        armateBonus += 5;
                        break;
                    case "Africa":
                        armateBonus += 3;
                        break;
                    case "Asia":
                        armateBonus += 7;
                        break;
                }
            }
        }

        return armateBonus;
    }
    
    
    private void distribuzioneArmate(Giocatore giocatore, int armateDaDistribuire) {
        while (armateDaDistribuire > 0) {
            System.out.println("Hai " + armateDaDistribuire + " armate da distribuire.");
            System.out.println("Seleziona il territorio dove posizionare un'armata:");

            for (int j = 0; j < giocatore.getTerritori_controllati().size(); j++) {
                System.out.println(j + ". " + giocatore.getTerritori_controllati().get(j).getNome());
            }

            int indiceTerritorio = SingletonMain.getInstance().readInteger();
            Territorio territorioSelezionato = giocatore.getTerritori_controllati().get(indiceTerritorio);
            territorioSelezionato.aggiungiArmate(1);
            giocatore.incrementaTotaleArmate(1);
            FileServiceImpl.getInstance().writeLog("Giocatore " + giocatore.getNome() + " ha posizionato 1 armata su " + territorioSelezionato.getNome());

            armateDaDistribuire--;
        }
    }
 
    
} 
    
