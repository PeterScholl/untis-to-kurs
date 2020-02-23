package org.unterrichtsportal.scholl.mathekalender2019;

import java.util.ArrayList;

public class Rendezvous {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ArrayList<Double> v = new ArrayList<Double>();
		v.add(0.);
		v.add(1.);
		double erwartungA = 0.0;
		double gesamtW = 0.0;
		for (int i = 0; i<10000; i++) {
			v = nextverteilung(v);
			erwartungA += (i+1)*v.get(0);
			gesamtW+=v.get(0);
		}
		System.out.println("Erw(a): "+erwartungA+ " GesamtWkeit: "+gesamtW);

		double erwartungB = 0.0;
		double gesamtWB = 0.0;
		for (int i = 0; i<100; i++) {
			erwartungB+=(2*i+1)*0.25*(Math.pow(0.75,i));
			gesamtWB += 0.25*(Math.pow(0.75,i));
		}
		System.out.println("Erw(b): "+erwartungB+ " GesamtWkeit: "+gesamtWB);

		double erwartungC = 0.0;
		double gesamtWC = 0.0;
		for (int i = 0; i<10; i++) {
			erwartungC+=((3*i+1)*0.25+(3*i+3)*0.25)*(Math.pow(0.5,i));
			gesamtWC += (Math.pow(0.5,i+1));
		}
		System.out.println("Erw(c): "+erwartungC+ " GesamtWkeit: "+gesamtWC);
	}
	
	public static ArrayList<Double> nextverteilung(ArrayList<Double> valt) {
		ArrayList<Double> vneu = new ArrayList<Double>();
		ArrayList<Double> valtc = (ArrayList<Double>)valt.clone();
		valtc.add(0.);
		valtc.add(0.);
		vneu.add(0.25*valtc.get(1));
		vneu.add(valtc.get(1)*0.5+0.25*valtc.get(2));
		for (int i=2; i<valt.size()+1; i++) {
			vneu.add(0.25*valtc.get(i-1)+0.5*valtc.get(i)+0.25*valtc.get(i+1));
		}
		return vneu;
	}
	

}
