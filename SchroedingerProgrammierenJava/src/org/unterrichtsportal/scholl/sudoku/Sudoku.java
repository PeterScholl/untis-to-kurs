package org.unterrichtsportal.scholl.sudoku;

import java.util.ArrayList;
import java.util.Collections;

public class Sudoku {

	ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int su = 19;
		int a = 3;
		ArrayList<ArrayList<Integer>> t = kombinationen(su, a);
		System.out.println("Kombinationen zur Summe: " + su + " und Anzahl: " + a+" ("+t.size()+" Loesungen)");
		for (ArrayList<Integer> j : t) {
			for (Integer i : j) {
				System.out.print(i + " ");
			}
			System.out.println("");
		}

	}

	public static ArrayList<ArrayList<Integer>> kombinationen(int summe, int anzahl) {
		return kombinationen(summe, anzahl, 1);
	}

	public static ArrayList<ArrayList<Integer>> kombinationen(int summe, int anzahl, int min) {
		if (anzahl <= 1) {
			if (summe >= min && summe < 10) {
				ArrayList<Integer> t = new ArrayList<Integer>();
				t.add(summe);
				ArrayList<ArrayList<Integer>> t1 = new ArrayList<ArrayList<Integer>>();
				t1.add(t);
				return t1;
			} else {
				return new ArrayList<ArrayList<Integer>>();
			}
		} else {
			ArrayList<ArrayList<Integer>> t1 = new ArrayList<ArrayList<Integer>>();
			for (int i = min; i < 10; i++) {
				ArrayList<ArrayList<Integer>> t = kombinationen(summe - i, anzahl - 1, i + 1);
				for (ArrayList<Integer> j : t) {
					ArrayList<Integer> k = (ArrayList<Integer>) j.clone();
					k.add(i);
					t1.add(k);
				}
			}
			for (ArrayList<Integer> k : t1)	Collections.sort(k);
			return t1;
		}
	}
}
