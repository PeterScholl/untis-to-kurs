package bwinf38Rd2Strom;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import bwinf38Rd2Abbiegen.Graph;
import bwinf38Rd2Abbiegen.Kante;
import bwinf38Rd2Abbiegen.Punkt;

public class Start {
	public static void main(String[] args) {
		try {
			stromrallye(args[0]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void stromrallye(String file) throws IOException {
		System.out.println("Working Directory: " + System.getProperty("user.dir"));
		System.out.println("\n| Datei einlesen |\n");

		FileReader fr = new FileReader("src/bwinf38Rd2Stromrallye/"+file);
		BufferedReader reader = new BufferedReader(fr);
		
		int size = Integer.parseInt(reader.readLine());
		String[] robstart = reader.readLine().split(",");
		Stromrallye rallye = new Stromrallye(size, Integer.parseInt(robstart[0])-1, Integer.parseInt(robstart[1])-1, Integer.parseInt(robstart[2]));

		int anzl = Integer.parseInt(reader.readLine());
		for (int i = 0; i < anzl; i++) {
			String[] batt = reader.readLine().split(",");
			rallye.addBatterie(Integer.parseInt(batt[0])-1, Integer.parseInt(batt[1])-1, Integer.parseInt(batt[2]));
		}
		
		reader.close();
		
		System.out.println(rallye);
		MyCanvas mc = new MyCanvas(500, 500);
		mc.writeText(20, 10, "Hallo Welt");
		rallye.setMyCanvas(mc);
		rallye.zeichne();
	}

}
