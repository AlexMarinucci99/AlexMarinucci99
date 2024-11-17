package it.univaq.disim.lpo.risiko.core.service;
import java.util.List;

import it.univaq.disim.lpo.risiko.core.datamodel.*;

public interface CartaObiettivoService {
    List<CartaObiettivo> generaObiettiviCasuali(int numeroObiettivi);
    
    static void assegnaObiettiviCasuali(List<Giocatore> giocatori, List<CartaObiettivo> obiettiviCasuali) {
		
		
	}
    
}