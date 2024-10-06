package it.univaq.disim.lpo.risiko.core.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileManager {
		  private static FileManager instance;
		  private final String fileName;
		  private String data;

		  FileManager(String fileName) {
		    this.fileName = fileName;
		    this.data = "";
		    // Read data from file on initialization (optional)
		    readFromFile();
		  }

		  public static FileManager getInstance(String fileName) {
		    if (instance == null) {
		      instance = new FileManager(fileName);
		    }
		    return instance;
		  }

		  public String readData() {
			readFromFile();
		    return data;
		  }

		  public void writeData(String data) {
		    this.data = data;
		    writeToFile();
		  }

		  private void readFromFile() {
		    try {
		      File file = new File(fileName);
		      if (file.exists()) {
		        FileReader reader = new FileReader(file);
		        BufferedReader bufferedReader = new BufferedReader(reader);
		        //StringBuilderstringBuilder = new StringBuilder();
		        String line;
		        String result="";
		        while ((line = bufferedReader.readLine()) != null) {
		        	//System.out.println(line);
		          //stringBuilder.append(line).append("\n");
		        	result = result+line+"\n";
		        }
		        bufferedReader.close();
		        this.data = result;
		      }
		    } catch (IOException e) {
		      // Handle file read exception
		      e.printStackTrace();
		    }
		  }

		  private void writeToFile() {
		    try {
		      FileWriter writer = new FileWriter(fileName);
		      writer.write(data);
		      writer.close();
		    } catch (IOException e) {
		      // Handle file write exception
		      e.printStackTrace();
		    }
		  }
}