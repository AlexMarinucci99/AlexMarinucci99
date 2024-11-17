package it.univaq.disim.lpo.risiko.core.service.impl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import it.univaq.disim.lpo.risiko.core.datamodel.Continente;
import it.univaq.disim.lpo.risiko.core.datamodel.Giocatore;
import it.univaq.disim.lpo.risiko.core.datamodel.Territorio;
import it.univaq.disim.lpo.risiko.core.service.GiocatoreService;

public class GiocatoreServiceImpl implements GiocatoreService {
	 private final Random random;
	    private List<String> coloriDisponibili = new ArrayList<>(Arrays.asList("rosso", "blu", "verde", "giallo", "nero", "bianco"));

	    public GiocatoreServiceImpl() {
	        
	        this.random = new Random();
	        
	    }    
	 
	    public List<Giocatore> creaGiocatori(int numeroGiocatori) {
	    	List<Giocatore> giocatori = new ArrayList<>();

	        for (int i = 1; i <= numeroGiocatori; i++) {
	    	System.out.print("Scegli il tuo nome di battaglia!" + ": ");
	        String nome = SingletonMain.getInstance().readString();

	       

	        Giocatore giocatore = new Giocatore(nome, numeroGiocatori, new ArrayList<>(), numeroGiocatori);

	        giocatori.add(giocatore);

	        
	        }

	        return giocatori;
	    }

	 private int lancioDado() {
	        return random.nextInt(6) + 1; 
	    }
	
	 public List<Giocatore> lancioDadiPerPrimoGiocatore(List<Giocatore> giocatori) {
		    System.out.println("\nVediamo chi inizia per primo...");
		    System.out.println();

		    // Effettua il lancio del dado per ogni giocatore
		    for (Giocatore giocatore : giocatori) {
		        int risultatoDado = lancioDado();
		        giocatore.setRisultatoLancioDado(risultatoDado);
		        System.out.println(giocatore.getNome() + " ha ottenuto: " + risultatoDado);
		    }

		    // Determina il risultato massimo e gestisce i pareggi
		    List<Giocatore> vincitori = new ArrayList<>();
		    int numeroMassimo = Integer.MIN_VALUE;

		    // Trova il numero massimo e individua i vincitori
		    for (Giocatore giocatore : giocatori) {
		        int risultatoDado = giocatore.getRisultatoLancioDado();
		        if (risultatoDado > numeroMassimo) {
		            numeroMassimo = risultatoDado;
		            vincitori.clear();
		            vincitori.add(giocatore);
		        } else if (risultatoDado == numeroMassimo) {
		            vincitori.add(giocatore);
		        }
		    }

		    // Se ci sono pareggi, ripetere il lancio del dado finché non rimane un solo vincitore
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

		    // Ordina i giocatori in base al risultato del dado in ordine decrescente
		    List<Giocatore> ordineGiocatori = new ArrayList<>(giocatori);
		    ordineGiocatori.sort((g1, g2) -> Integer.compare(g2.getRisultatoLancioDado(), g1.getRisultatoLancioDado()));

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
		    int numeroTerritori = tuttiTerritori.size();
		    int territoriPerGiocatore = numeroTerritori / numeroGiocatori;
		    int restantiTerritori = numeroTerritori % numeroGiocatori;

		    int indiceTerritorio = 0;

		    // Assegna i territori in modo uniforme tra i giocatori
		    for (Giocatore giocatore : giocatori) {
		        List<Territorio> territoriAssegnati = new ArrayList<>();
		        for (int i = 0; i < territoriPerGiocatore; i++) {
		            Territorio territorio = tuttiTerritori.get(indiceTerritorio++);
		            territoriAssegnati.add(territorio);
		            territorio.setGiocatore(giocatore);
		        }
		        giocatore.setTerritori_controllati(territoriAssegnati);
		    }

		    // Assegna i restanti territori in modo sequenziale ai giocatori
		    for (int i = 0; i < restantiTerritori; i++) {
		        Territorio territorio = tuttiTerritori.get(indiceTerritorio++);
		        Giocatore giocatore = giocatori.get(i % numeroGiocatori);
		        giocatore.aggiungiTerritorio(territorio);
		        territorio.setGiocatore(giocatore);
		    }
		}

	 public void distribuzioneInizialeArmate(List<Giocatore> giocatori, int armatePerGiocatore) {
		
		    Set<String> coloriScelti = new HashSet<>();

		    // Prima fase: Ogni giocatore posiziona una armata su ogni territorio
		    for (Giocatore giocatore : giocatori) {
		        if (giocatore.getColore() == null) {
		            scegliColore(giocatore, coloriScelti);
		        }

		        for (Territorio territorio : giocatore.getTerritori_controllati()) {
		            territorio.aggiungiArmate(1);
		            giocatore.incrementaTotaleArmate(1);
		        }
		    }

		    // Seconda fase: Distribuire le armate rimanenti
		    for (Giocatore giocatore : giocatori) {
		        int armateRimanenti = armatePerGiocatore - giocatore.getTerritori_controllati().size();
		        System.out.println("Giocatore " + giocatore.getNome() + " ha " + armateRimanenti + " armate rimanenti da distribuire.");


		        while (armateRimanenti > 0) {
		            System.out.println("Giocatore " + giocatore.getNome() + ", hai " + armateRimanenti + " armate da distribuire.");
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
		                        System.out.println("Territorio non valido. Riprovare.");
		                    }
		                } catch (Exception e) {
		                    System.out.println("Errore nella selezione del territorio. Riprovare.");
		                }
		            }

		            Territorio territorioSelezionato = giocatore.getTerritori_controllati().get(indiceTerritorio);
		            territorioSelezionato.aggiungiArmate(1);
		            giocatore.incrementaTotaleArmate(1);
		            armateRimanenti--;
		        }
		     

		    }
		    
		}

	    private void scegliColore(Giocatore giocatore, Set<String> coloriScelti) {
	        System.out.println("\nGiocatore " + giocatore.getNome() + ", scegli un colore per le tue armate:");
	        for (String colore : coloriDisponibili) {
	        	if (!coloriScelti.contains(colore.toLowerCase())) {
	                System.out.println("- " + capitalize(colore));
	            }
	        }
	        System.out.println();

	        // Richiedi la scelta del colore
	        String coloreScelto = SingletonMain.getInstance().readString().trim().toLowerCase();

	        // Controllo della validità del colore scelto
	        while (coloriScelti.contains(coloreScelto) || !coloriDisponibili.contains(coloreScelto)) {
	            System.out.println("Il colore scelto è non valido o già stato preso. Scegli un altro colore:\n");
	            coloreScelto = SingletonMain.getInstance().readString().trim().toLowerCase();
	            System.out.println();
	        }

	        // Imposta il colore scelto
	        giocatore.setColore(capitalize(coloreScelto));
	        coloriScelti.add(coloreScelto);
	        System.out.println("Giocatore " + giocatore.getNome() + " ha scelto il colore " + capitalize(coloreScelto) + ".");
	    }
	    private String capitalize(String input) {
	        if (input == null || input.isEmpty()) {
	            return input;
	        }
	        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
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

	    public void aggiungiTerritorio(Giocatore giocatore, Territorio territorio) {
	        if (!giocatore.getTerritori_controllati().contains(territorio)) {
	            giocatore.getTerritori_controllati().add(territorio);
	            territorio.setGiocatore(giocatore); // Aggiorna il riferimento al giocatore nel territorio
	        }
	    }

	    public void rimuoviTerritorio(Giocatore giocatore, Territorio territorio) {
	        if (giocatore.getTerritori_controllati().contains(territorio)) {
	            giocatore.getTerritori_controllati().remove(territorio);
	            territorio.setGiocatore(null); // Rimuove il riferimento al giocatore nel territorio
	        }
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