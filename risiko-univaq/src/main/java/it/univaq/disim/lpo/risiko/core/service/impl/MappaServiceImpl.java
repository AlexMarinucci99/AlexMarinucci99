package it.univaq.disim.lpo.risiko.core.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.univaq.disim.lpo.risiko.core.datamodel.Continente;
import it.univaq.disim.lpo.risiko.core.datamodel.Mappa;
import it.univaq.disim.lpo.risiko.core.datamodel.Territorio;
import it.univaq.disim.lpo.risiko.core.service.InizializzaPartitaException;
import it.univaq.disim.lpo.risiko.core.service.MappaService;

public class MappaServiceImpl implements MappaService{
    private Mappa mappa;
    private List<Continente> continenti;

    // Costruttore
    public MappaServiceImpl() throws InizializzaPartitaException {
        this.continenti = inizializzaContinentiETerritori();
        this.mappa = new Mappa(continenti); // Passa l'elenco di continenti
        impostaAdiacenze();  // Assicurati che tutte le adiacenze siano correttamente impostate
        verificaAdiacenze();  // Aggiungi il metodo per stampare tutte le adiacenze e verificare la correttezza

        if (continenti == null || continenti.isEmpty()) {
            throw new InizializzaPartitaException("Errore nell'inizializzazione: i continenti non possono essere null o vuoti.");
        }
    }

    public List<Continente> inizializzaContinentiETerritori() {
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
        return continenti;
    }

    // Metodo per impostare le adiacenze in modo bidirezionale
    private void impostaAdiacenze() {
        // Creazione di una mappa per accedere rapidamente ai territori
        Map<String, Territorio> territorioMap = new HashMap<>();
        for (Continente continente : continenti) {
            for (Territorio territorio : continente.getTerritori()) {
                territorioMap.put(territorio.getNome(), territorio);
            }
        }

        // Definisci le adiacenze tra i territori in modo bidirezionale
        aggiungiAdiacenzeBidirezionali(territorioMap, "Alaska", "Alberta");
        aggiungiAdiacenzeBidirezionali(territorioMap, "Alaska", "Kamchatka");
        aggiungiAdiacenzeBidirezionali(territorioMap, "Alaska", "Territori del Nord-Ovest");

        aggiungiAdiacenzeBidirezionali(territorioMap, "Alberta", "Territori del Nord-Ovest");
        aggiungiAdiacenzeBidirezionali(territorioMap, "Alberta", "Ontario");
        aggiungiAdiacenzeBidirezionali(territorioMap, "Alberta", "Stati Uniti Occidentali");

        aggiungiAdiacenzeBidirezionali(territorioMap, "Territori del Nord-Ovest", "Ontario");
        aggiungiAdiacenzeBidirezionali(territorioMap, "Territori del Nord-Ovest", "Alaska");
        aggiungiAdiacenzeBidirezionali(territorioMap, "Territori del Nord-Ovest", "Groenlandia");

        aggiungiAdiacenzeBidirezionali(territorioMap, "Ontario", "Quebec");
        aggiungiAdiacenzeBidirezionali(territorioMap, "Ontario", "Stati Uniti Orientali");
        aggiungiAdiacenzeBidirezionali(territorioMap, "Ontario", "Stati Uniti Occidentali");

        aggiungiAdiacenzeBidirezionali(territorioMap, "Quebec", "Stati Uniti Orientali");
        aggiungiAdiacenzeBidirezionali(territorioMap, "Quebec", "Groenlandia");

        aggiungiAdiacenzeBidirezionali(territorioMap, "Groenlandia", "Islanda");
        aggiungiAdiacenzeBidirezionali(territorioMap, "Groenlandia", "Quebec");

        aggiungiAdiacenzeBidirezionali(territorioMap, "Stati Uniti Occidentali", "America Centrale");
        aggiungiAdiacenzeBidirezionali(territorioMap, "Stati Uniti Occidentali", "Alberta");
        aggiungiAdiacenzeBidirezionali(territorioMap, "Stati Uniti Occidentali", "Ontario");

        aggiungiAdiacenzeBidirezionali(territorioMap, "Stati Uniti Orientali", "America Centrale");
        aggiungiAdiacenzeBidirezionali(territorioMap, "Stati Uniti Orientali", "Quebec");
        aggiungiAdiacenzeBidirezionali(territorioMap, "Stati Uniti Orientali", "Ontario");

        aggiungiAdiacenzeBidirezionali(territorioMap, "America Centrale", "Venezuela");
        aggiungiAdiacenzeBidirezionali(territorioMap, "America Centrale", "Stati Uniti Occidentali");
        aggiungiAdiacenzeBidirezionali(territorioMap, "America Centrale", "Stati Uniti Orientali");

        aggiungiAdiacenzeBidirezionali(territorioMap, "Venezuela", "Brasile");
        aggiungiAdiacenzeBidirezionali(territorioMap, "Venezuela", "Perù");

        aggiungiAdiacenzeBidirezionali(territorioMap, "Perù", "Brasile");
        aggiungiAdiacenzeBidirezionali(territorioMap, "Perù", "Argentina");

        aggiungiAdiacenzeBidirezionali(territorioMap, "Brasile", "Argentina");
        aggiungiAdiacenzeBidirezionali(territorioMap, "Brasile", "Africa del Nord");

        aggiungiAdiacenzeBidirezionali(territorioMap, "Africa del Nord", "Egitto");
        aggiungiAdiacenzeBidirezionali(territorioMap, "Africa del Nord", "Congo");
        aggiungiAdiacenzeBidirezionali(territorioMap, "Africa del Nord", "Europa Occidentale");
        aggiungiAdiacenzeBidirezionali(territorioMap, "Africa del Nord", "Brasile");

        aggiungiAdiacenzeBidirezionali(territorioMap, "Egitto", "Africa Orientale");
        aggiungiAdiacenzeBidirezionali(territorioMap, "Egitto", "Medio Oriente");
        aggiungiAdiacenzeBidirezionali(territorioMap, "Egitto", "Africa del Nord");

        aggiungiAdiacenzeBidirezionali(territorioMap, "Congo", "Africa Orientale");
        aggiungiAdiacenzeBidirezionali(territorioMap, "Congo", "Africa del Sud");

        aggiungiAdiacenzeBidirezionali(territorioMap, "Africa Orientale", "Madagascar");
        aggiungiAdiacenzeBidirezionali(territorioMap, "Africa Orientale", "Congo");
        aggiungiAdiacenzeBidirezionali(territorioMap, "Africa Orientale", "Egitto");

        aggiungiAdiacenzeBidirezionali(territorioMap, "Madagascar", "Africa del Sud");
        aggiungiAdiacenzeBidirezionali(territorioMap, "Madagascar", "Africa Orientale");

        aggiungiAdiacenzeBidirezionali(territorioMap, "Europa Occidentale", "Europa Meridionale");
        aggiungiAdiacenzeBidirezionali(territorioMap, "Europa Occidentale", "Europa Settentrionale");
        aggiungiAdiacenzeBidirezionali(territorioMap, "Europa Occidentale", "Gran Bretagna");

        aggiungiAdiacenzeBidirezionali(territorioMap, "Europa Meridionale", "Ucraina");
        aggiungiAdiacenzeBidirezionali(territorioMap, "Europa Meridionale", "Medio Oriente");
        aggiungiAdiacenzeBidirezionali(territorioMap, "Europa Meridionale", "Egitto");

        aggiungiAdiacenzeBidirezionali(territorioMap, "Europa Settentrionale", "Ucraina");
        aggiungiAdiacenzeBidirezionali(territorioMap, "Europa Settentrionale", "Scandinavia");
        aggiungiAdiacenzeBidirezionali(territorioMap, "Europa Settentrionale", "Gran Bretagna");

        aggiungiAdiacenzeBidirezionali(territorioMap, "Gran Bretagna", "Islanda");
        aggiungiAdiacenzeBidirezionali(territorioMap, "Gran Bretagna", "Scandinavia");

        aggiungiAdiacenzeBidirezionali(territorioMap, "Scandinavia", "Islanda");
        aggiungiAdiacenzeBidirezionali(territorioMap, "Scandinavia", "Ucraina");

        aggiungiAdiacenzeBidirezionali(territorioMap, "Ucraina", "Urali");
        aggiungiAdiacenzeBidirezionali(territorioMap, "Ucraina", "Afghanistan");

        aggiungiAdiacenzeBidirezionali(territorioMap, "Urali", "Siberia");
        aggiungiAdiacenzeBidirezionali(territorioMap, "Urali", "Cina");

        aggiungiAdiacenzeBidirezionali(territorioMap, "Siberia", "Jacuzia");
        aggiungiAdiacenzeBidirezionali(territorioMap, "Siberia", "Čita");

        aggiungiAdiacenzeBidirezionali(territorioMap, "Jacuzia", "Čita");
        aggiungiAdiacenzeBidirezionali(territorioMap, "Jacuzia", "Kamchatka");

        aggiungiAdiacenzeBidirezionali(territorioMap, "Kamchatka", "Giappone");
        aggiungiAdiacenzeBidirezionali(territorioMap, "Kamchatka", "Alaska");

        aggiungiAdiacenzeBidirezionali(territorioMap, "Čita", "Mongolia");
        aggiungiAdiacenzeBidirezionali(territorioMap, "Čita", "Kamchatka");

        aggiungiAdiacenzeBidirezionali(territorioMap, "Mongolia", "Giappone");
        aggiungiAdiacenzeBidirezionali(territorioMap, "Mongolia", "Cina");

        aggiungiAdiacenzeBidirezionali(territorioMap, "Cina", "Siam");
        aggiungiAdiacenzeBidirezionali(territorioMap, "Cina", "India");

        aggiungiAdiacenzeBidirezionali(territorioMap, "India", "Siam");
        aggiungiAdiacenzeBidirezionali(territorioMap, "India", "Medio Oriente");

        aggiungiAdiacenzeBidirezionali(territorioMap, "Medio Oriente", "Afghanistan");
        aggiungiAdiacenzeBidirezionali(territorioMap, "Medio Oriente", "Europa Meridionale");

        aggiungiAdiacenzeBidirezionali(territorioMap, "Siam", "Indonesia");

        aggiungiAdiacenzeBidirezionali(territorioMap, "Indonesia", "Nuova Guinea");
        aggiungiAdiacenzeBidirezionali(territorioMap, "Indonesia", "Australia Occidentale");

        aggiungiAdiacenzeBidirezionali(territorioMap, "Nuova Guinea", "Australia Orientale");
        aggiungiAdiacenzeBidirezionali(territorioMap, "Nuova Guinea", "Australia Occidentale");

        aggiungiAdiacenzeBidirezionali(territorioMap, "Australia Orientale", "Australia Occidentale");
    }

    // Metodo di supporto per aggiungere adiacenze bidirezionali tra due territori
    private void aggiungiAdiacenzeBidirezionali(Map<String, Territorio> territorioMap, String nomeTerritorio1, String nomeTerritorio2) {
        Territorio territorio1 = territorioMap.get(nomeTerritorio1);
        Territorio territorio2 = territorioMap.get(nomeTerritorio2);

        if (territorio1 != null && territorio2 != null) {
            territorio1.aggiungiTerritorioAdiacente(territorio2);
            territorio2.aggiungiTerritorioAdiacente(territorio1);
        } else {
            System.out.println("Errore: Territori non trovati per l'adiacenza tra " + nomeTerritorio1 + " e " + nomeTerritorio2);
        }
    }

    // Metodo per verificare le adiacenze
    public void verificaAdiacenze() {
        for (Continente continente : continenti) {
            for (Territorio territorio : continente.getTerritori()) {
                System.out.print("Territorio: " + territorio.getNome() + " è adiacente a: ");
                for (Territorio adiacente : territorio.getTerritoriAdiacenti()) {
                    System.out.print(adiacente.getNome() + ", ");
                }
                System.out.println();
            }
        }
    }
    public Mappa getMappa() {
        return this.mappa;
    }

    public Mappa inizializzaMappa(List<Continente> continenti) throws InizializzaPartitaException {
        if (continenti == null || continenti.isEmpty()) {
            throw new InizializzaPartitaException("La lista dei continenti è vuota o nulla.");
        }
        mappa.setContinenti(continenti);
        return mappa;
    }

    // Metodo per ottenere la lista dei continenti dalla mappa
    public List<Continente> getContinenti() {
        return mappa.getContinenti();
    }

    // Metodo per ottenere tutti i territori della mappa
    public List<Territorio> getTuttiITerritori(String nomeContinente) {
        Continente continente = mappa.getContinente(nomeContinente);
        if (continente != null) {
            return continente.getTerritori();
        }
        return new ArrayList<>(); // Restituisce una lista vuota se il continente non esiste
    }
}
