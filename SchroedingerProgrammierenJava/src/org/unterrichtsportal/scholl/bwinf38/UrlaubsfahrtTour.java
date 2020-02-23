package org.unterrichtsportal.scholl.bwinf38;

public class UrlaubsfahrtTour {
	private double kosten;
	private String[][] angefahreneTankstellen;

	public UrlaubsfahrtTour(double k) {
		kosten = k;
		angefahreneTankstellen = new String[0][0] ;
	}

	public UrlaubsfahrtTour(UrlaubsfahrtTour t) {
		if (t == null) {
			kosten = 0.0;
			angefahreneTankstellen = new String[0][0];
		} else {
			this.kosten = t.getKosten();
			this.angefahreneTankstellen = new String[t.angefahreneTankstellen.length][2];
			for (int i=0;i<t.angefahreneTankstellen.length;i++) {
				this.angefahreneTankstellen[i][0]=t.angefahreneTankstellen[i][0];
				this.angefahreneTankstellen[i][1]=t.angefahreneTankstellen[i][1];
			}
		}
	}

	public double getKosten() {
		return kosten;
	}

	public void setKosten(double kosten) {
		this.kosten = kosten;
	}

	public String[][] getAngefahreneTankstellen() {
		return angefahreneTankstellen;
	}

	public void setAngefahreneTankstellen(String[][] angefahreneTankstellen) {
		this.angefahreneTankstellen = angefahreneTankstellen;
	}

	public void addAngefahreneTankstelle(String[] s) {
		String[][] temp = new String[this.angefahreneTankstellen.length+1][2];
		for (int i=0;i<this.angefahreneTankstellen.length;i++) {
			temp[i][0]=this.angefahreneTankstellen[i][0];
			temp[i][1]=this.angefahreneTankstellen[i][1];
		}		
		temp[this.angefahreneTankstellen.length][0]=s[0];
		temp[this.angefahreneTankstellen.length][1]=s[1];
		this.angefahreneTankstellen=temp;
	}

	public String toString() {
		String out = "Kosten: " + kosten + "\n";
		out+="km\t Preis\n";
		for (String[] i : this.angefahreneTankstellen) {
			out+=i[0]+"\t "+i[1]+"\n";
		}
		return out;
	}
	
	public static void main(String[] args) {
		UrlaubsfahrtTour t1 = new UrlaubsfahrtTour(-1.0);
		System.out.println(t1);
		String[] s = {"1000","116"};
		t1.addAngefahreneTankstelle(s);
		System.out.println(t1);
		UrlaubsfahrtTour t2 =  new UrlaubsfahrtTour(t1);
		s = new String[] {"2000","117"};
		t2.addAngefahreneTankstelle(s);
		System.out.println(t2);
		

	}

}
