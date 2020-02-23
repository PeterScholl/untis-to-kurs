//package de.stevworks.parallelen; // Programmiert von Stefan Cames
package org.unterrichtsportal.scholl.bwinf38;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

public class Parallelen {
	
	static String eingabeFile = "parallelen.txt";
	
	static ArrayList<String> text = new ArrayList<>();

	public static void main(String[] args) throws FileNotFoundException {
		
		System.out.println("Working Directory: "+System.getProperty("user.dir"));
		
		FileReader fr = new FileReader(eingabeFile);
		Scanner scanner = new Scanner(fr);
		
		while(scanner.hasNext()) {
			String line[] = scanner.nextLine().replaceAll(", |[^a-zßA-Z0-9- ]", "").split(" ");
			for (int i = 0; i < line.length; i++) {
				if (line.length > 1) {
					text.add(line[i]);
				}
				//System.out.println(line[i]);
			}
			
		}
		scanner.close();
		//System.out.println(text.size());
		for (int i = 0; i < text.size() / 2; i++) {
			boolean hasNextWord = true;
			int actual = i;
			while (hasNextWord) {
				String nextword = text.get(actual + text.get(actual).length());
				actual = actual + text.get(actual).length();
				System.out.println(nextword + " - " + text.get(actual).length());
				if (text.get(actual).length() == 0) {
					actual += 1;
				}
				if (actual + text.get(actual).length() >= text.size()) {
					hasNextWord = false;
					System.out.println("i: "+i+" nextword: "+nextword+" text.get(i):"+text.get(i));
					/*if(nextword == text.get(i)) {
						System.out.println("\nOkay\n");
					} else {
						System.out.println("\nDie Hypothese ist falsch!\n");
						System.exit(0);
					}*/
				}
			}
			System.out.println("\nnächster Durchgang\n");
		}
		System.out.println("Die Hypothese stimmt!");
	}

}
