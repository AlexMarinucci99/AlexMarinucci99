package it.univaq.disim.lpo.risiko.core.service.impl;

import java.io.Closeable;
import java.io.IOException;

import java.util.Scanner;

public class SingletonMain implements Closeable{
	
	private Scanner scanner ;
	private static SingletonMain instance= null;

	private SingletonMain () {
		scanner = new Scanner(System.in);
		
	}
	

	public static SingletonMain getInstance() {
		 if (instance == null) {
			 instance= new SingletonMain();
	        }
		return instance;
	}

	public Scanner getScanner() {
		return scanner;
	}

	public void disposeScanner() {
		scanner.close();
	}


	public void close() throws IOException {
		scanner.close();	
	}
	
	Integer readInteger(){
		while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Input non valido. Inserisci un numero.");
            }
        }
		
	} 
	public Integer readIntegerUntilPossibleValue(Integer[] possibleValues){
		while (true) {
			try {
				int value = Integer.parseInt(scanner.nextLine());
                for (int possibleValue : possibleValues) {
                    if (value == possibleValue) {
                        return value;
                    }
                }
                System.out.println("Valore non valido. Riprova.");
            } catch (NumberFormatException e) {
                System.out.println("Input non valido. Inserisci un numero.");
            }
		}
		
	}
	String readString(){
		return scanner.nextLine();
	}
}
	