package it.univaq.disim.lpo.risiko.core.service;

import java.io.IOException;

import it.univaq.disim.lpo.risiko.core.datamodel.Gioco;

public interface FileService {
	
	 void salvaGioco(Gioco gioco, String fileName) throws IOException;
    Gioco caricaGioco(String fileName) throws IOException, ClassNotFoundException;
    String readData(String fileName);
    void writeData(String fileName, String data);
}
