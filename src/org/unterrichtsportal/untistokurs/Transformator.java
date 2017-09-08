package org.unterrichtsportal.untistokurs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
//import java.io.FileReader;
//import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class Transformator {
	private ArrayList<Unterricht> unterrichte = new ArrayList<>();
	

	public Transformator() {
		super();
	}
	
	public void addUnterricht(Unterricht u) {
		unterrichte.add(u);
	}
	
	public boolean readFile(String name) {
		try {
			//BufferedReader br = null;
			//FileReader fr = new FileReader(name);
			//br = new BufferedReader(fr);
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(name),"utf-8"));

			String zeile;
			int counter=0;
			while (br != null && (zeile = br.readLine()) != null ) {
				counter++;
				//System.out.println(zeile);
				String[] inhalt = zeile.split(";");
				unterrichte.add(new Unterricht(Integer.parseInt(inhalt[0]), 
						inhalt[1].replaceAll("\"", ""), 
						inhalt[2].replaceAll("\"", ""), 
						inhalt[3].replaceAll("\"", ""), 
						inhalt[4].replaceAll("\"", ""), 
						Integer.parseInt(inhalt[5]), 
						Integer.parseInt(inhalt[6])));				
			}
			System.out.println(""+counter+" Zeilen gelesen");
			//fr.close();
			br.close();
			return true;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return false;
		}
	}
	
	public boolean writeFile(String zieldatei, String klasse) {
		try {
			//FileWriter fw = new FileWriter(zieldatei);
			//BufferedWriter bw = new BufferedWriter(fw);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(zieldatei), "cp1252"));
			bw.write("\"Kurs\";\"Tag\";\"Std\";\"Woche\";\"Raum\";\"Lehrer\"\r\n");
			int counter=0;
			for (Unterricht unterricht : unterrichte) {
				if (unterricht.getKlasse().equals(klasse)) {
					counter++;
					bw.write(""+unterricht.getFachKurs()+
							";"+unterricht.getTag()+
							";"+unterricht.getStunde()+
							";0;"+unterricht.getRaum()+
							";"+unterricht.getLehrer()+"\r\n");
					bw.write(""+unterricht.getFachKurs()+
							";"+unterricht.getTag()+
							";"+unterricht.getStunde()+
							";1;"+unterricht.getRaum()+
							";"+unterricht.getLehrer()+"\r\n");
					//System.out.println(unterricht);
				}
			}
			System.out.println(""+counter+" Unterrichte geschrieben");
			
			bw.close();
			return true;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return false;
		}
	}

	@Override
	public String toString() {
		return "Transformator [unterrichte=" + unterrichte + "]";
	}
	
	


}
