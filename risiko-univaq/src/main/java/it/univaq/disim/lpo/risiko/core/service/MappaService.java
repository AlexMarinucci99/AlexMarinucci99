package it.univaq.disim.lpo.risiko.core.service;


	import it.univaq.disim.lpo.risiko.core.datamodel.Continente;
	import it.univaq.disim.lpo.risiko.core.datamodel.Territorio;
	import it.univaq.disim.lpo.risiko.core.datamodel.Mappa;
	import java.util.List;

public interface MappaService {

	    // Inizializza i continenti e i territori
	    List<Continente> inizializzaContinentiETerritori();
	   
	    List<Territorio> getTuttiITerritori(String nomeContinente);

	    // Restituisce la mappa completa
	   Mappa getMappa();

	    Mappa inizializzaMappa(List<Continente> continenti) throws InizializzaPartitaException;

	}
