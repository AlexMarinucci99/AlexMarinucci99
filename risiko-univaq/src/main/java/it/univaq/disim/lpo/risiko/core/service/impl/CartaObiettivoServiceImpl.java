package it.univaq.disim.lpo.risiko.core.service.impl;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import it.univaq.disim.lpo.risiko.core.datamodel.CartaObiettivo;
import it.univaq.disim.lpo.risiko.core.datamodel.Giocatore;
import it.univaq.disim.lpo.risiko.core.service.CartaObiettivoService;

public class CartaObiettivoServiceImpl implements CartaObiettivoService {

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
    
    public static void assegnaObiettiviCasuali(List<Giocatore> giocatori, List<CartaObiettivo> obiettivi) {
        Collections.shuffle(obiettivi);
        
        for (int i = 0; i < giocatori.size(); i++) {
            Giocatore giocatore = giocatori.get(i);
            CartaObiettivo obiettivoAssegnato = obiettivi.get(i);
            giocatore.setObiettivo(obiettivoAssegnato);
           // System.out.println("Obiettivo assegnato a " + giocatore.getNome() + ": " + obiettivoAssegnato.getDescrizione());
        }
    }
       
    }

