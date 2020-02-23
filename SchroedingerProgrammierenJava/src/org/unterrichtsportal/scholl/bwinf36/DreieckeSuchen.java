package org.unterrichtsportal.scholl.bwinf36;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DreieckeSuchen {
	private Strecke[] strecken;
	private int anzSchnittpunkte = 0;
	private Schnittpunkt[] schnittpunkte;
	
	public DreieckeSuchen() {
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DreieckeSuchen d = new DreieckeSuchen();
		d.readFile("/tmp/dreiecke1.txt");
		System.out.println("Es gibt "+d.strecken.length+" Strecken");
		d.bestimmeAnzahlSchnittpunkte();
		System.out.println("und "+d.anzSchnittpunkte+" Schnittpunkte");
		d.generiereSchnittpunkte();
		d.sucheDreiecke();

	}

	public boolean readFile(String name) {
		BufferedReader br = null;
		strecken = null;
		try {
			FileReader fr = new FileReader(name);
			br = new BufferedReader(fr);
			String zeile;
			int nr = 0;
			while (br != null && (zeile = br.readLine()) != null ) {
				System.out.println(zeile);
				if (strecken == null) {
					strecken = new Strecke[Integer.parseInt(zeile)];
				} else {
					String[] koords = zeile.split(" ");
					strecken[nr++]=new Strecke(new Punkt(Float.parseFloat(koords[0]),
														Float.parseFloat(koords[1])),
											   new Punkt(Float.parseFloat(koords[2]),
														Float.parseFloat(koords[3]))); 
				}
			}
			fr.close();
			br.close();
			return true;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return false;
		}
	}
	
	public void bestimmeAnzahlSchnittpunkte() {
		anzSchnittpunkte=0;
		for (int i=0; i<strecken.length-1; i++) {
			for (int j=i+1; j<strecken.length; j++) {
				if (strecken[i].schnittpunktMit(strecken[j]) != null) anzSchnittpunkte++;
			}
		}
	}
	
	public void generiereSchnittpunkte() {
		Punkt ap;
		this.bestimmeAnzahlSchnittpunkte();
		schnittpunkte = new Schnittpunkt[anzSchnittpunkte];
		int nr=0;
		for (int i=0; i<strecken.length-1; i++) {
			for (int j=i+1; j<strecken.length; j++) {
				if ((ap=strecken[i].schnittpunktMit(strecken[j])) != null) {
					schnittpunkte[nr++]=new Schnittpunkt(ap, strecken[i], strecken[j]);
				}
			}
		}

		
	}
	
	public void sucheDreiecke() {
		int anzD = 0;
		for (int i=0; i<schnittpunkte.length-2; i++) {
			//System.out.println(schnittpunkte[i].getS1());
			Schnittpunkt[] sp1 = new Schnittpunkt[anzSchnittpunkte];
			int nr1 = 0;
			Schnittpunkt[] sp2 = new Schnittpunkt[anzSchnittpunkte];
			int nr2 = 0;
			for (int j=i+1; j<schnittpunkte.length; j++) {
				//System.out.println(j);
				if (schnittpunkte[j].aufStrecke(schnittpunkte[i].getS1())) {
					sp1[nr1++]=schnittpunkte[j];
				} else if (schnittpunkte[j].aufStrecke(schnittpunkte[i].getS2())) {
					sp2[nr2++]=schnittpunkte[j];
				} 
			}
			for (int j=0; j<nr1; j++) {
				for (int k=0; k<nr2; k++) {
					if (sp1[j].getS1().equals(sp2[k].getS1())
							|| sp1[j].getS1().equals(sp2[k].getS2())
							|| sp1[j].getS2().equals(sp2[k].getS1())
							|| sp1[j].getS2().equals(sp2[k].getS2())
							) {
						System.out.println("Dreieck gefunden! : "+ (++anzD));
					}
				}
			}
		}
		
	}
}
