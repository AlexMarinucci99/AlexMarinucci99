package it.univaq.disim.lpo.risiko.core.service.impl;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import it.univaq.disim.lpo.risiko.core.datamodel.Gioco;
import it.univaq.disim.lpo.risiko.core.service.FileService;


public  class FileServiceImpl implements FileService{
	
	 private static FileServiceImpl instance;
	 private static final String LOG_FILE = "azioni_gioco.log";

	    // Costruttore privato per il Singleton
	    private FileServiceImpl() {}

	    // Metodo per ottenere l'istanza Singleton
	    public static FileServiceImpl getInstance() {
	        if (instance == null) {
	            synchronized (FileServiceImpl.class) {
	                if (instance == null) {
	                    instance = new FileServiceImpl();
	                }
	            }
	        }
	        return instance;
	    }
	    
	    // Metodo per scrivere log di gioco su un file (con append)
	    public void writeLog(String data) {
	        Path logFilePath = Paths.get(LOG_FILE);
	        try (FileWriter writer = new FileWriter(logFilePath.toFile(), true);
	             BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
	            bufferedWriter.write(data);
	            bufferedWriter.newLine();
	            System.out.println("Log scritto su " + logFilePath.toAbsolutePath());
	        } catch (IOException e) {
	            System.out.println("Errore durante la scrittura del file di log: " + e.getMessage());
	        }
	    }
	    @Override
	    public void salvaGioco(Gioco gioco, String fileName) throws IOException {
	        Path path = Paths.get(fileName);
	        try (FileOutputStream fileOut = new FileOutputStream(path.toFile());
	             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
	            out.writeObject(gioco);
	            System.out.println("Partita salvata in " + path.toAbsolutePath());
	        } catch (IOException e) {
	            System.out.println("Errore durante il salvataggio della partita: " + e.getMessage());
	            throw e;
	        }
	    }

	    @Override
	    public Gioco caricaGioco(String fileName) throws IOException, ClassNotFoundException {
	        Path path = Paths.get(fileName);
	        try (FileInputStream fileIn = new FileInputStream(path.toFile());
	             ObjectInputStream in = new ObjectInputStream(fileIn)) {
	            Gioco gioco = (Gioco) in.readObject();
	            System.out.println("Partita caricata da " + path.toAbsolutePath());
	            return gioco;
	        } catch (IOException | ClassNotFoundException e) {
	            System.out.println("Errore durante il caricamento della partita: " + e.getMessage());
	            throw e;
	        }
	    }
	    
	    @Override
	    public String readData(String fileName) {
	        Path path = Paths.get(fileName);
	        try {
	            return Files.readString(path);
	        } catch (IOException e) {
	            System.out.println("Errore durante la lettura del file: " + e.getMessage());
	            return "";
	        }
	    }
	    
	    @Override
	    public void writeData(String fileName, String data) {
	        Path path = Paths.get(fileName);
	        try {
	            Files.writeString(path, data);
	            System.out.println("Dati scritti su " + path.toAbsolutePath());
	        } catch (IOException e) {
	            System.out.println("Errore durante la scrittura del file: " + e.getMessage());
	        }
	    }
	    
}

