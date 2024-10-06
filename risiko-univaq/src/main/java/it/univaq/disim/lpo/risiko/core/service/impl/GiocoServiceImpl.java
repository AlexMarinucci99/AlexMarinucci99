package it.univaq.disim.lpo.risiko.core.service.impl;

import it.univaq.disim.lpo.risiko.core.datamodel.*;
import it.univaq.disim.lpo.risiko.core.service.*;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.nio.file.Files;
import java.nio.file.Paths;



public class GiocoServiceImpl implements GiocoService {
	
    private final Random random;
    private List<String> coloriDisponibili = new ArrayList<>(Arrays.asList("Rosso", "Blu", "Verde", "Giallo", "Nero", "Bianco"));
    private static final String LOG_FILE = "azioni_gioco.log";

 

    public GiocoServiceImpl() {
    
        this.random = new Random();
        
    }

    
    public Gioco inizializzaPartita() throws InizializzaPartitaException {
    	inizializzaLog();
        Gioco gioco = null;
        System.out.print("\nSelezionare (1) nuova partita o (2) per caricare una partita esistente: ");
        Integer modo = SingletonMain.getInstance().readIntegerUntilPossibleValue(new Integer[]{1, 2});

        if (modo == 2) {
            while (true) {
                System.out.print("\nSeleziona file da caricare: ");
                String valore = SingletonMain.getInstance().readString();
                try {
                    return caricaGioco(valore);
                } catch (ClassNotFoundException |  IOException e) {
                	System.out.println("Errore nel caricamento del file: " + e.getMessage());
                    throw new InizializzaPartitaException("Errore nel caricamento del file", e);
                }
            }
        }

        if (modo == 1) {
            System.out.print("\nSelezione in quanti giocatori volete giocare (2-6): ");
            Integer numeroGiocatori = SingletonMain.getInstance().readIntegerUntilPossibleValue(new Integer[]{2, 3, 4, 5, 6});
            System.out.println();
            List<Giocatore> giocatori = new ArrayList<>();

            for (int i = 1; i <= numeroGiocatori; i++) {
                System.out.print("Nome giocatore " + i + ": ");
                String nome = SingletonMain.getInstance().readString();
                Giocatore giocatore = new Giocatore(nome, 0, new ArrayList<>(),0,0);
                giocatori.add(giocatore);
            }
            
            List<CartaObiettivo> obiettivi = generaObiettiviCasuali(giocatori.size());
            for (int i = 0; i < giocatori.size(); i++) {
                giocatori.get(i).setObiettivo(obiettivi.get(i));
            }

            List<Continente> continenti = inizializzaContinentiETerritori();
            Mappa mappa = new Mappa(continenti);
            List<CartaTerritorio> carteTerritorio = new ArrayList<>();
            List<CartaObiettivo> carteObiettivo = new ArrayList<>();

            gioco = new Gioco("Inizio", giocatori, mappa, 6, carteTerritorio, carteObiettivo);

            List<Giocatore> ordineGiocatori = lancioDadiPerPrimoGiocatore(giocatori); 
             
            System.out.println("\nIl giocatore " + ordineGiocatori.get(0).getNome() + " inizia per primo!"); 
            distribuzioneTerritori(ordineGiocatori, continenti);

            int armatePerGiocatore = calcolaArmatePerGiocatore(numeroGiocatori);

            distribuzioneInizialeArmate(ordineGiocatori, armatePerGiocatore);
            
         

            return gioco;
        } else {
            throw new InizializzaPartitaException("Opzione non valida.");
        }
    }
    private void scriviLog(String messaggio) {
        try (FileWriter fw = new FileWriter(LOG_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(messaggio);
        } catch (IOException e) {
            System.out.println("Errore nella scrittura del file di log: " + e.getMessage());
        }
    }

    private void inizializzaLog() {
        try {
            Files.deleteIfExists(Paths.get(LOG_FILE));
        } catch (IOException e) {
            System.out.println("Errore nella cancellazione del file di log: " + e.getMessage());
        }
    }


    private List<Continente> inizializzaContinentiETerritori() {
        List<Continente> continenti = new ArrayList<>();

        List<Territorio> territoriAmericaDelNord = Arrays.asList(
                new Territorio("Alaska"), new Territorio("Alberta"), new Territorio("America Centrale"),
                new Territorio("Groenlandia"), new Territorio("Territori del Nord-Ovest"),
                new Territorio("Ontario"), new Territorio("Quebec"), new Territorio("Stati Uniti Orientali"),
                new Territorio("Stati Uniti Occidentali")
        );

        Continente americaDelNord = new Continente("America del Nord", territoriAmericaDelNord);
        continenti.add(americaDelNord);

        List<Territorio> territoriSudAmerica = Arrays.asList(
                new Territorio("Argentina"), new Territorio("Brasile"),
                new Territorio("Perù"), new Territorio("Venezuela")
        );

        Continente americaDelSud = new Continente("America del Sud", territoriSudAmerica);
        continenti.add(americaDelSud);
        
        List<Territorio> territoriEuropa = Arrays.asList(
                new Territorio("Islanda"),new Territorio("Scandinavia"),new Territorio("Gran Bretagna"),new Territorio("Europa Settentrionale"),
                new Territorio("Europa Occidentale"),new Territorio("Europa Meridionale"),new Territorio("Ucraina")
        );

        Continente europa = new Continente("Europa", territoriEuropa);
        continenti.add(europa);
        
        List<Territorio> territoriAfrica = Arrays.asList(
        		new Territorio("Africa del Nord"),new Territorio("Egitto"),new Territorio("Congo"),new Territorio("Africa Orientale"),
        		new Territorio("Africa del Sud"),new Territorio("Madagascar")
        		);
        Continente africa = new Continente("Africa", territoriAfrica);
		continenti.add(africa);
		
		List<Territorio> territoriAsia = Arrays.asList(
				new Territorio("Urali"),new Territorio("Siberia"),new Territorio("Jacuzia"),new Territorio("Čita"),new Territorio("Kamchatka" ),
				new Territorio("Giappone"),new Territorio("Mongolia"),new Territorio("Cina"),new Territorio("Medio Oriente"),
				new Territorio("India"),new Territorio("Siam"),new Territorio("Afghanistan")
				
				);
		Continente asia = new Continente("Asia", territoriAsia);		
		continenti.add(asia);
		
		List<Territorio> territoriOceania =Arrays.asList(
				new Territorio("Indonesia"),new Territorio("Nuova Guinea"),new Territorio("Australia Occidentale"),
				new Territorio("Australia Orientale")
				
				);

		Continente oceania = new Continente("Oceania", territoriOceania);
		continenti.add(oceania);

        for (Continente continente : continenti) {
            for (Territorio territorio : continente.getTerritori()) {
                territorio.setContinente(continente);
            }
        }
     // Creazione di una mappa per trovare rapidamente i territori
        Map<String, Territorio> territorioMap = new HashMap<>();
        for (Continente continente : continenti) {
            for (Territorio territorio : continente.getTerritori()) {
                territorioMap.put(territorio.getNome(), territorio);
            }
        }

        territorioMap.get("Alaska").aggiungiTerritorioAdiacente(territorioMap.get("Alberta"));
        territorioMap.get("Alaska").aggiungiTerritorioAdiacente(territorioMap.get("Kamchatka"));
        territorioMap.get("Alaska").aggiungiTerritorioAdiacente(territorioMap.get("Territori del Nord-Ovest"));

        territorioMap.get("Alberta").aggiungiTerritorioAdiacente(territorioMap.get("Territori del Nord-Ovest"));
        territorioMap.get("Alberta").aggiungiTerritorioAdiacente(territorioMap.get("Ontario"));
        territorioMap.get("Alberta").aggiungiTerritorioAdiacente(territorioMap.get("Stati Uniti Occidentali"));

        territorioMap.get("Territori del Nord-Ovest").aggiungiTerritorioAdiacente(territorioMap.get("Groenlandia"));
        territorioMap.get("Territori del Nord-Ovest").aggiungiTerritorioAdiacente(territorioMap.get("Ontario"));

        territorioMap.get("Ontario").aggiungiTerritorioAdiacente(territorioMap.get("Quebec"));
        territorioMap.get("Ontario").aggiungiTerritorioAdiacente(territorioMap.get("Stati Uniti Orientali"));
        territorioMap.get("Ontario").aggiungiTerritorioAdiacente(territorioMap.get("Stati Uniti Occidentali"));

        territorioMap.get("Quebec").aggiungiTerritorioAdiacente(territorioMap.get("Stati Uniti Orientali"));
        territorioMap.get("Quebec").aggiungiTerritorioAdiacente(territorioMap.get("Groenlandia"));

        territorioMap.get("Groenlandia").aggiungiTerritorioAdiacente(territorioMap.get("Islanda"));

        territorioMap.get("Stati Uniti Occidentali").aggiungiTerritorioAdiacente(territorioMap.get("America Centrale"));
        territorioMap.get("Stati Uniti Orientali").aggiungiTerritorioAdiacente(territorioMap.get("America Centrale"));

        territorioMap.get("America Centrale").aggiungiTerritorioAdiacente(territorioMap.get("Venezuela"));

        territorioMap.get("Venezuela").aggiungiTerritorioAdiacente(territorioMap.get("Brasile"));
        territorioMap.get("Venezuela").aggiungiTerritorioAdiacente(territorioMap.get("Perù"));

        territorioMap.get("Perù").aggiungiTerritorioAdiacente(territorioMap.get("Brasile"));
        territorioMap.get("Perù").aggiungiTerritorioAdiacente(territorioMap.get("Argentina"));

        territorioMap.get("Brasile").aggiungiTerritorioAdiacente(territorioMap.get("Argentina"));
        territorioMap.get("Brasile").aggiungiTerritorioAdiacente(territorioMap.get("Africa del Nord"));

        territorioMap.get("Argentina").aggiungiTerritorioAdiacente(territorioMap.get("Perù"));

        territorioMap.get("Africa del Nord").aggiungiTerritorioAdiacente(territorioMap.get("Congo"));
        territorioMap.get("Africa del Nord").aggiungiTerritorioAdiacente(territorioMap.get("Egitto"));
        territorioMap.get("Africa del Nord").aggiungiTerritorioAdiacente(territorioMap.get("Europa Meridionale"));
        territorioMap.get("Africa del Nord").aggiungiTerritorioAdiacente(territorioMap.get("Europa Occidentale"));

        territorioMap.get("Egitto").aggiungiTerritorioAdiacente(territorioMap.get("Africa Orientale"));
        territorioMap.get("Egitto").aggiungiTerritorioAdiacente(territorioMap.get("Medio Oriente"));
        territorioMap.get("Egitto").aggiungiTerritorioAdiacente(territorioMap.get("Europa Meridionale"));
        
        territorioMap.get("Africa Orientale").aggiungiTerritorioAdiacente(territorioMap.get("Congo"));
        territorioMap.get("Africa Orientale").aggiungiTerritorioAdiacente(territorioMap.get("Africa del Sud"));
        territorioMap.get("Africa Orientale").aggiungiTerritorioAdiacente(territorioMap.get("Madagascar"));
        
        territorioMap.get("Africa del Sud").aggiungiTerritorioAdiacente(territorioMap.get("Congo"));
        territorioMap.get("Africa del Sud").aggiungiTerritorioAdiacente(territorioMap.get("Madagascar"));
        
        territorioMap.get("Europa Occidentale").aggiungiTerritorioAdiacente(territorioMap.get("Europa Meridionale"));
        territorioMap.get("Europa Occidentale").aggiungiTerritorioAdiacente(territorioMap.get("Europa Settentrionale"));
        territorioMap.get("Europa Occidentale").aggiungiTerritorioAdiacente(territorioMap.get("Gran Bretagna"));
        
        territorioMap.get("Europa Meridionale").aggiungiTerritorioAdiacente(territorioMap.get("Europa Settentrionale"));
        territorioMap.get("Europa Meridionale").aggiungiTerritorioAdiacente(territorioMap.get("Ucraina"));
        territorioMap.get("Europa Meridionale").aggiungiTerritorioAdiacente(territorioMap.get("Medio Oriente"));
        
        territorioMap.get("Europa Settentrionale").aggiungiTerritorioAdiacente(territorioMap.get("Ucraina"));
        territorioMap.get("Europa Settentrionale").aggiungiTerritorioAdiacente(territorioMap.get("Gran Bretagna"));
        territorioMap.get("Europa Settentrionale").aggiungiTerritorioAdiacente(territorioMap.get("Scandinavia"));
        
        territorioMap.get("Gran Bretagna").aggiungiTerritorioAdiacente(territorioMap.get("Islanda"));
        territorioMap.get("Gran Bretagna").aggiungiTerritorioAdiacente(territorioMap.get("Scandinavia"));
        
        territorioMap.get("Scandinavia").aggiungiTerritorioAdiacente(territorioMap.get("Ucraina"));
        territorioMap.get("Scandinavia").aggiungiTerritorioAdiacente(territorioMap.get("Islanda"));
        
        territorioMap.get("Ucraina").aggiungiTerritorioAdiacente(territorioMap.get("Medio Oriente"));
        territorioMap.get("Ucraina").aggiungiTerritorioAdiacente(territorioMap.get("Urali"));
        territorioMap.get("Ucraina").aggiungiTerritorioAdiacente(territorioMap.get("Afghanistan"));
        
        territorioMap.get("Urali").aggiungiTerritorioAdiacente(territorioMap.get("Afghanistan"));
        territorioMap.get("Urali").aggiungiTerritorioAdiacente(territorioMap.get("Siberia"));
        territorioMap.get("Urali").aggiungiTerritorioAdiacente(territorioMap.get("Cina"));
        
        territorioMap.get("Siberia").aggiungiTerritorioAdiacente(territorioMap.get("Cina"));
        territorioMap.get("Siberia").aggiungiTerritorioAdiacente(territorioMap.get("Jacuzia"));
        territorioMap.get("Siberia").aggiungiTerritorioAdiacente(territorioMap.get("Mongolia"));
        territorioMap.get("Siberia").aggiungiTerritorioAdiacente(territorioMap.get("Čita"));
        
        territorioMap.get("Jacuzia").aggiungiTerritorioAdiacente(territorioMap.get("Čita"));
        territorioMap.get("Jacuzia").aggiungiTerritorioAdiacente(territorioMap.get("Kamchatka"));
        
        territorioMap.get("Kamchatka").aggiungiTerritorioAdiacente(territorioMap.get("Čita"));
        territorioMap.get("Kamchatka").aggiungiTerritorioAdiacente(territorioMap.get("Mongolia"));
        territorioMap.get("Kamchatka").aggiungiTerritorioAdiacente(territorioMap.get("Giappone"));
        
        territorioMap.get("Čita").aggiungiTerritorioAdiacente(territorioMap.get("Mongolia"));
        
        territorioMap.get("Mongolia").aggiungiTerritorioAdiacente(territorioMap.get("Giappone"));
        territorioMap.get("Mongolia").aggiungiTerritorioAdiacente(territorioMap.get("Cina"));
        
        territorioMap.get("Cina").aggiungiTerritorioAdiacente(territorioMap.get("Siam"));
        territorioMap.get("Cina").aggiungiTerritorioAdiacente(territorioMap.get("India"));
        territorioMap.get("Cina").aggiungiTerritorioAdiacente(territorioMap.get("Medio Oriente"));
        territorioMap.get("Cina").aggiungiTerritorioAdiacente(territorioMap.get("Afghanistan"));
        
        territorioMap.get("India").aggiungiTerritorioAdiacente(territorioMap.get("Siam"));
        territorioMap.get("India").aggiungiTerritorioAdiacente(territorioMap.get("Medio Oriente"));
        
        territorioMap.get("Medio Oriente").aggiungiTerritorioAdiacente(territorioMap.get("Afghanistan"));
        
        territorioMap.get("Siam").aggiungiTerritorioAdiacente(territorioMap.get("Indonesia"));
        
        territorioMap.get("Indonesia").aggiungiTerritorioAdiacente(territorioMap.get("Nuova Guinea"));
        territorioMap.get("Indonesia").aggiungiTerritorioAdiacente(territorioMap.get("Australia Occidentale"));
        
        territorioMap.get("Nuova Guinea").aggiungiTerritorioAdiacente(territorioMap.get("Australia Occidentale"));
        territorioMap.get("Nuova Guinea").aggiungiTerritorioAdiacente(territorioMap.get("Australia Orientale"));
        
        territorioMap.get("Australia Orientale").aggiungiTerritorioAdiacente(territorioMap.get("Australia Occidentale"));

        return continenti;
    }
   
    

    private List<Giocatore> lancioDadiPerPrimoGiocatore(List<Giocatore> giocatori) {
        List<Giocatore> vincitori = new ArrayList<>();
        int numeroMassimo = Integer.MIN_VALUE;
        System.out.println("\nVediamo chi inizia per primo...");
        System.out.println();
        for (Giocatore giocatore : giocatori) {
            int risultatoDado = lancioDado();
            giocatore.setRisultatoLancioDado(risultatoDado);
            System.out.println(giocatore.getNome() + " ha ottenuto: " + risultatoDado);

            if (risultatoDado > numeroMassimo) {
                numeroMassimo = risultatoDado;
                vincitori.clear();
                vincitori.add(giocatore);
            } else if (risultatoDado == numeroMassimo) {
                vincitori.add(giocatore);
            }
        }

        while (vincitori.size() > 1) {
            System.out.println("\nOps... c'è stato un pareggio, ripetiamo i lanci!");
            System.out.println();
            numeroMassimo = Integer.MIN_VALUE;
            List<Giocatore> nuoviVincitori = new ArrayList<>();
            for (Giocatore vincitore : vincitori) {
                int risultatoDado = lancioDado();
                vincitore.setRisultatoLancioDado(risultatoDado);
                System.out.println(vincitore.getNome() + " ha ottenuto: " + risultatoDado);

                if (risultatoDado > numeroMassimo) {
                    numeroMassimo = risultatoDado;
                    nuoviVincitori.clear();
                    nuoviVincitori.add(vincitore);
                } else if (risultatoDado == numeroMassimo) {
                    nuoviVincitori.add(vincitore);
                }
            }
            vincitori = nuoviVincitori;
        }
        //da rivedere perchè alcune volte ordina male
        List<Giocatore> ordineGiocatori = new ArrayList<>();
        ordineGiocatori.addAll(vincitori);
    
        for (Giocatore giocatore : giocatori) {
            if (!vincitori.contains(giocatore)) {
                ordineGiocatori.add(giocatore);
            }
        }    
    
        System.out.println("\nL'ordine dei giocatori è: " + ordineGiocatori.stream().map(Giocatore::getNome).collect(Collectors.joining(", ")));
    
        return ordineGiocatori;
    }

  
    public void distribuzioneTerritori(List<Giocatore> giocatori, List<Continente> continenti) {
        // Estrai tutti i territori dai continenti
        List<Territorio> tuttiTerritori = continenti.stream()
                .flatMap(continente -> continente.getTerritori().stream())
                .collect(Collectors.toList());

        // Mischia casualmente i territori
        Collections.shuffle(tuttiTerritori);

        int numeroGiocatori = giocatori.size();
        int territoriPerGiocatore = tuttiTerritori.size() / numeroGiocatori;
        int restantiTerritori = tuttiTerritori.size() % numeroGiocatori;

        int indiceTerritorio = 0;

        // Assegna i territori in modo uniforme tra i giocatori
        for (Giocatore giocatore : giocatori) {
            List<Territorio> territoriAssegnati = new ArrayList<>();
            for (int i = 0; i < territoriPerGiocatore; i++) {
                Territorio territorio = tuttiTerritori.get(indiceTerritorio++);
                territoriAssegnati.add(territorio);
                territorio.setGiocatore(giocatore);
                scriviLog("Giocatore " + giocatore.getNome() + " ha ricevuto il territorio " + territorio.getNome());
            }
            giocatore.setTerritori_controllati(territoriAssegnati);
        }
        // Assegna i restanti territori in modo sequenziale ai giocatori
        for (int i = 0; i < restantiTerritori; i++) {
            Territorio territorio = tuttiTerritori.get(indiceTerritorio++);
            Giocatore giocatore = giocatori.get(i % numeroGiocatori);
            giocatore.getTerritori_controllati().add(territorio);
            territorio.setGiocatore(giocatore);
            scriviLog("Giocatore " + giocatore.getNome() + " ha ricevuto il territorio " + territorio.getNome());
        }
    }

 
    public void salvaGioco(Gioco gioco, String filename) throws IOException {
        try (FileOutputStream fileOut = new FileOutputStream(filename);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(gioco);
            System.out.println("Partita salvata in " + filename);
        } catch (IOException e) {
            System.out.println("Errore durante il salvataggio della partita: " + e.getMessage());
            throw e;
        }
    }

    public Gioco caricaGioco(String filename) throws IOException, ClassNotFoundException {
        try (FileInputStream fileIn = new FileInputStream(filename);
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            Gioco gioco = (Gioco) in.readObject();
            System.out.println("Partita caricata da " + filename);
            return gioco;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Errore durante il caricamento della partita: " + e.getMessage());
            throw e;
        }
    }


    private int lancioDado() {
        return random.nextInt(6) + 1;
    }
     
      //da visualizzare non so se funziona


    public List<CartaObiettivo> generaObiettiviCasuali(int numeroObiettivi) {
        List<CartaObiettivo> obiettiviDisponibili = Arrays.asList(
                new CartaObiettivo("Conquistare 24 territori"),
                new CartaObiettivo("Conquistare la totalità del Nord America e dell'Africa"),
                new CartaObiettivo("Conquistare la totalità del Nord America e dell'Oceania"),
                new CartaObiettivo("Conquistare la totalità dell'Asia e del Sud America"),
                new CartaObiettivo("Conquistare la totalità dell'Asia e dell'Africa"),
                new CartaObiettivo("Conquistare 18 territori e occupare ognuno con almeno 2 armate")
             
        );
        Collections.shuffle(obiettiviDisponibili);
        return obiettiviDisponibili.subList(0, numeroObiettivi);
    }

   
    public void distribuzioneInizialeArmate(List<Giocatore> giocatori, int armatePerGiocatore) {
        boolean armateDaDistribuire = false;
        int armatePerTurno = calcolaArmatePerGiocatore(giocatori.size());
        Set<String> coloriScelti = new HashSet<>();

        // Prima fase: Ogni giocatore posiziona una armata su ogni territorio
        for (Giocatore giocatore : giocatori) {
            // Chiedere la scelta del colore solo al primo turno
            if (giocatore.getColore() == null) {
                scegliColore(giocatore, coloriScelti);
            }

            // Ogni territorio del giocatore deve avere almeno una armata
            for (Territorio territorio : giocatore.getTerritori_controllati()) {
                territorio.aggiungiArmate(1);
                giocatore.incrementaTotaleArmate(1);
                scriviLog("Giocatore " + giocatore.getNome() + " ha posizionato 1 armata su " + territorio.getNome());
            }
        }
        // Verifica se ci sono ancora armate da distribuire
        for (Giocatore giocatore : giocatori) {
            if (giocatore.getTotaleArmate() < armatePerGiocatore) {
                armateDaDistribuire = true;
                break;
            }
        }

     // Seconda fase: Distribuire le armate rimanenti, solo se necessario
        if (armateDaDistribuire) {
            while (armateDaDistribuire) {
                armateDaDistribuire = false;
                
                for (Giocatore giocatore : giocatori) {
                    if (giocatore.getTotaleArmate() < armatePerGiocatore) {
                        int armateRimanenti = armatePerGiocatore - giocatore.getTotaleArmate();
                        int armateDaPosizionare = Math.min(armatePerTurno, armateRimanenti);
                        System.out.println("Giocatore " + giocatore.getNome() + ", distribuisci " + armateDaPosizionare + " armate sui tuoi territori:");

                        while (armateDaPosizionare > 0) {
                            System.out.println("\nHai " + armateDaPosizionare + " armate da distribuire.");
                            System.out.println("Seleziona il territorio dove posizionare un'armata:");
                            for (int j = 0; j < giocatore.getTerritori_controllati().size(); j++) {
                                System.out.println(j + ". " + giocatore.getTerritori_controllati().get(j).getNome());
                            }

                            int indiceTerritorio = -1;
                            boolean territorioValido = false;

                            while (!territorioValido) {
                                try {
                                    indiceTerritorio = SingletonMain.getInstance().readInteger();

                                    if (indiceTerritorio >= 0 && indiceTerritorio < giocatore.getTerritori_controllati().size()) {
                                        territorioValido = true;
                                    } else {
                                        System.out.println("Hai selezionato un territorio non valido, riprova.");
                                    }
                                } catch (IndexOutOfBoundsException | InputMismatchException e) {
                                    System.out.println("Errore nella selezione, riprova.");
                                }
                            }

                            Territorio territorioSelezionato = giocatore.getTerritori_controllati().get(indiceTerritorio);
                            territorioSelezionato.aggiungiArmate(1);
                            giocatore.incrementaTotaleArmate(1);
                            scriviLog("Giocatore " + giocatore.getNome() + " ha posizionato 1 armata su " + territorioSelezionato.getNome());
                            armateDaPosizionare--;
                        }

                        if (giocatore.getTotaleArmate() < armatePerGiocatore) {
                            armateDaDistribuire = true;
                        }
                    }
                }
            }
        }
    }

    
    private void scegliColore(Giocatore giocatore, Set<String> coloriScelti) {
        System.out.println("\nGiocatore " + giocatore.getNome() + ", scegli un colore per le tue armate:");
        for (String colore : coloriDisponibili) {
            if (!coloriScelti.contains(colore)) {
                System.out.println("- " + colore);
            }
        }
        System.out.println();

        String coloreScelto = SingletonMain.getInstance().readString();

        System.out.println();


        while (coloriScelti.contains(coloreScelto) || !coloriDisponibili.contains(coloreScelto)) {
            System.out.println("Il colore scelto è non valido o già stato preso. Scegli un altro colore:\n");
            coloreScelto = SingletonMain.getInstance().readString();
            System.out.println();
        }

        giocatore.setColore(coloreScelto);
        coloriScelti.add(coloreScelto);
        System.out.println("Giocatore " + giocatore.getNome() + " ha scelto il colore " + coloreScelto + ".");
    }


    public int calcolaArmatePerGiocatore(int numeroGiocatori) {         
        switch (numeroGiocatori) {
            case 2:
                return 40;
            case 3:
                return 35;
            case 4:
                return 30;
            case 5:
                return 25;
            case 6:
                return 20;
            default:
                throw new IllegalArgumentException("Numero di giocatori non valido.");
        }
    }
    public boolean verificaVittoria(Giocatore giocatore, Gioco gioco) {
        CartaObiettivo obiettivo = giocatore.getObiettivo(); 

        switch (obiettivo.getDescrizione()) {
            case "Conquistare 24 territori":
                if (giocatore.getTerritori_controllati().size() >= 24) {
                    System.out.println("Giocatore " + giocatore.getNome() + " ha conquistato 24 territori e vince la partita!");
                    scriviLog("Giocatore " + giocatore.getNome() + " ha vinto la partita con l'obiettivo: " + obiettivo.getDescrizione());
                    return true;
                }
                break;

            case "Conquistare la totalità del Nord America e dell'Africa":
                if (haConquistatoContinente(giocatore, "Nord America", gioco) &&
                    haConquistatoContinente(giocatore, "Africa", gioco)) {
                    System.out.println("Giocatore " + giocatore.getNome() + " ha conquistato Nord America e Africa e vince la partita!");
                    scriviLog("Giocatore " + giocatore.getNome() + " ha vinto la partita con l'obiettivo: " + obiettivo.getDescrizione());
                    return true;
                }
                break;

            case "Conquistare la totalità del Nord America e dell'Oceania":
                if (haConquistatoContinente(giocatore, "Nord America", gioco) &&
                    haConquistatoContinente(giocatore, "Oceania", gioco)) {
                    System.out.println("Giocatore " + giocatore.getNome() + " ha conquistato Nord America e Oceania e vince la partita!");
                    scriviLog("Giocatore " + giocatore.getNome() + " ha vinto la partita con l'obiettivo: " + obiettivo.getDescrizione());
                    return true;
                }
                break;

            case "Conquistare la totalità dell'Asia e del Sud America":
                if (haConquistatoContinente(giocatore, "Asia", gioco) &&
                    haConquistatoContinente(giocatore, "Sud America", gioco)) {
                    System.out.println("Giocatore " + giocatore.getNome() + " ha conquistato Asia e Sud America e vince la partita!");
                    scriviLog("Giocatore " + giocatore.getNome() + " ha vinto la partita con l'obiettivo: " + obiettivo.getDescrizione());
                    return true;
                }
                break;

            case "Conquistare la totalità dell'Asia e dell'Africa":
                if (haConquistatoContinente(giocatore, "Asia", gioco) &&
                    haConquistatoContinente(giocatore, "Africa", gioco)) {
                    System.out.println("Giocatore " + giocatore.getNome() + " ha conquistato Asia e Africa e vince la partita!");
                    scriviLog("Giocatore " + giocatore.getNome() + " ha vinto la partita con l'obiettivo: " + obiettivo.getDescrizione());
                    return true;
                }
                break;

            case "Conquistare 18 territori e occupare ognuno con almeno 2 armate":
                long territoriConAlmenoDueArmate = giocatore.getTerritori_controllati().stream()
                    .filter(t -> t.getNumeroArmate() >= 2)
                    .count();
                if (territoriConAlmenoDueArmate >= 18) {
                    System.out.println("Giocatore " + giocatore.getNome() + " ha conquistato 18 territori con almeno 2 armate ciascuno e vince la partita!");
                    scriviLog("Giocatore " + giocatore.getNome() + " ha vinto la partita con l'obiettivo: " + obiettivo.getDescrizione());
                    return true;
                }
                break;

            default:
                System.out.println("Obiettivo non riconosciuto.");
                break;
        }

        return false;
    }

    private boolean haConquistatoContinente(Giocatore giocatore, String nomeContinente, Gioco gioco) {
        Continente continente = gioco.getMappa().getContinente(nomeContinente);
        if (continente == null) {
            System.out.println("Il continente " + nomeContinente + " non esiste nella mappa.");
            return false;
        }
        return giocatore.getTerritori_controllati().containsAll(continente.getTerritori());
    }

    private void dichiaraVittoria(Giocatore giocatore) {
        System.out.println("Congratulazioni " + giocatore.getNome() + "! Hai completato il tuo obiettivo e hai vinto la partita!");
        scriviLog("Il giocatore " + giocatore.getNome() + " ha vinto la partita completando l'obiettivo: " + giocatore.getObiettivo().getDescrizione());
        System.exit(0); // Termina il gioco
    }

  
    public boolean TurnoGiocatore(Giocatore giocatore, Gioco gioco) {

    	// Calcolo delle armate per il giocatore
        int armateTerritori = Math.max(3, giocatore.getTerritori_controllati().size() / 3);
        int armateContinenti = calcolaArmateContinenti(giocatore, gioco.getMappa().getContinenti());
        int armateTotali = armateTerritori + armateContinenti;

        System.out.println("Giocatore " + giocatore.getNome() + " riceve " + armateTotali + " armate (Territori: " + armateTerritori + ", Continenti: " + armateContinenti + ").");
        scriviLog("Giocatore " + giocatore.getNome() + " riceve " + armateTotali + " armate (Territori: " + armateTerritori + ", Continenti: " + armateContinenti + ").");
    	
    	
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
                	
                	  boolean attaccoTerminato = false;
                      while (!attaccoTerminato) {
                          // Seleziona territorio di partenza per l'attacco
                          System.out.println("Seleziona il territorio da cui vuoi attaccare:");
                          Territorio territorioAttaccante = selezionaTerritorioPerAttacco(giocatore);

                          if (territorioAttaccante == null) {
                              System.out.println("Non hai più territori con armate sufficienti per attaccare.");
                              break;
                          }
   

                          Territorio territorioDifensore = null;
                          boolean territorioValido = false;
                          while (!territorioValido) {
                              territorioDifensore = selezionaTerritorioAdiacente(territorioAttaccante);
                              
                              // Controlla se il territorio selezionato è già posseduto dal giocatore
                              if (territorioDifensore.getGiocatore().equals(giocatore)) {
                                  System.out.println("Non puoi attaccare un territorio che già possiedi. Seleziona un altro territorio.");
                              } else {
                                  territorioValido = true;
                              }
                          }
                          // Attacca il territorio selezionato
                          boolean territorioConquistato = attacca(giocatore, territorioAttaccante, territorioDifensore);

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
                      }
                      // Dopo l'attacco, verifica se il giocatore ha vinto
                      if (verificaVittoria(giocatore, gioco)) {
                          return false; // Fine del gioco
                      }
                      break;
                case 4:
                	System.out.println("Seleziona il territorio di partenza:");
                    Territorio territorioPartenza = selezionaTerritorio(giocatore);

                    System.out.println("Seleziona il territorio di destinazione:");
                    Territorio territorioDestinazione = selezionaTerritorio(giocatore);

                    System.out.println("Quante armate vuoi spostare?");
                    int numeroArmate = SingletonMain.getInstance().readInteger();

                    try {
                        spostamentoArmate(giocatore, territorioPartenza, territorioDestinazione, numeroArmate);
                        // Concludi il turno dopo lo spostamento
                        turnoTerminato = true;
                        System.out.println("Turno di " + giocatore.getNome() + " terminato dopo lo spostamento delle armate.");
                        scriviLog("Turno di " + giocatore.getNome() + " terminato dopo lo spostamento delle armate.");
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
                    scriviLog("Turno di " + giocatore.getNome() + " terminato.");
                    break;
                case 6:
                	 // Salva partita e esci
                    try {
                        System.out.print("Inserisci il nome del file per salvare la partita: ");
                        String filename = SingletonMain.getInstance().readString();
                        salvaGioco(gioco, filename);
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

 
 // Metodo per selezionare un territorio adiacente che appartiene ad un altro giocatore
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

        int indiceTerritorio = SingletonMain.getInstance().readInteger();
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

    private boolean attacca(Giocatore giocatore, Territorio territorioAttaccante, Territorio territorioDifensore) {
        // Stampa lo stato iniziale
        System.out.println("Attacco dal territorio " + territorioAttaccante.getNome() + " (armate: " + territorioAttaccante.getNumeroArmate() + ")");
        System.out.println("Difesa del territorio " + territorioDifensore.getNome() + " (armate: " + territorioDifensore.getNumeroArmate() + ")");

        // Numero massimo di dadi disponibili per l'attaccante e il difensore
        int numDadiAttacco = Math.min(territorioAttaccante.getNumeroArmate() - 1, 3); // Fino a 3 dadi
        int numDadiDifesa = Math.min(territorioDifensore.getNumeroArmate(), 2); // Fino a 2 dadi

        // L'attaccante lancia i dadi
        List<Integer> dadiAttacco = lanciaDadi(numDadiAttacco);
        System.out.println("L'attaccante ha lanciato i dadi: " + dadiAttacco);

        // Il difensore lancia i dadi
        List<Integer> dadiDifesa = lanciaDadi(numDadiDifesa);
        System.out.println("Il difensore ha lanciato i dadi: " + dadiDifesa);

        // Confronto dei dadi
        int armatePerseAttaccante = 0;
        int armatePerseDifensore = 0;

        for (int i = 0; i < Math.min(dadiAttacco.size(), dadiDifesa.size()); i++) {
            if (dadiAttacco.get(i) > dadiDifesa.get(i)) {
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
    
   
    private Territorio selezionaTerritorio(Giocatore giocatore) {
        System.out.println("Seleziona un territorio tra quelli controllati:");
        for (int i = 0; i < giocatore.getTerritori_controllati().size(); i++) {
            System.out.println(i + ". " + giocatore.getTerritori_controllati().get(i).getNome());
        }
        int indiceTerritorio = SingletonMain.getInstance().readInteger();
        return giocatore.getTerritori_controllati().get(indiceTerritorio);
    }

    public void spostamentoArmate(Giocatore giocatore, Territorio territorioPartenza, Territorio territorioDestinazione, int numeroArmate) throws Exception {
        // Verifica che i territori siano adiacenti
        if (!territorioPartenza.getTerritoriAdiacenti().contains(territorioDestinazione)) {
            throw new Exception("I territori non sono adiacenti.");
        }
        
        // Verifica che entrambi i territori siano sotto il controllo del giocatore
        if (territorioPartenza.getGiocatore() != giocatore || territorioDestinazione.getGiocatore() != giocatore) {
            throw new Exception("Entrambi i territori devono essere sotto il controllo del giocatore.");
        }

        // Verifica che il numero di armate da spostare sia valido
        if (numeroArmate < 1 || territorioPartenza.getNumeroArmate() - numeroArmate < 1) {
            throw new Exception("Numero di armate non valido. Devi lasciare almeno un'armata nel territorio di partenza.");
        }
        
        // Effettua lo spostamento
        territorioPartenza.rimuoviArmate(numeroArmate);
        territorioDestinazione.aggiungiArmate(numeroArmate);
        
        System.out.println("Hai spostato " + numeroArmate + " armate da " + territorioPartenza.getNome() + " a " + territorioDestinazione.getNome() + ". Il tuo turno è concluso.");
    }


    private void salvaEsci(Gioco gioco) {
        System.out.print("Inserisci il nome del file in cui salvare la partita (es: partita.salvataggio): ");
        String nomeFile = SingletonMain.getInstance().readString();

        try {
            salvaGioco(gioco, nomeFile);
            System.out.println("Partita salvata con successo in '" + nomeFile + "'. Uscita in corso...");
            scriviLog("Partita salvata in '" + nomeFile + "'.");
            // Terminare l'applicazione dopo il salvataggio
            System.exit(0);
        } catch (IOException e) {
            System.out.println("Errore durante il salvataggio della partita: " + e.getMessage());
            scriviLog("Errore nel salvataggio della partita: " + e.getMessage());
        }
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
            scriviLog("Giocatore " + giocatore.getNome() + " ha posizionato 1 armata su " + territorioSelezionato.getNome());
            armateDaDistribuire--;
        }
    }
} 
    
