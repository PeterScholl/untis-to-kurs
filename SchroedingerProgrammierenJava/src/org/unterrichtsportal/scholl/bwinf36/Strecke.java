package org.unterrichtsportal.scholl.bwinf36;

public class Strecke {
	private Punkt start;
	private Punkt ende;
	public Strecke(Punkt start, Punkt ende) {
		super();
		this.start = start;
		this.ende = ende;
	}
	public Punkt getStart() {
		return start;
	}
	public void setStart(Punkt start) {
		this.start = start;
	}
	public Punkt getEnde() {
		return ende;
	}
	public void setEnde(Punkt ende) {
		this.ende = ende;
	}

	public float steigung() {
		if (ende.getxKoord()==start.getxKoord()) return Float.NaN;
		return (ende.getyKoord()-start.getyKoord())/(ende.getxKoord()-start.getxKoord());
	}

	public float yAchsenabschnitt() {
		if (steigung() == Float.NaN) return Float.NaN;
		return start.getyKoord()-steigung()*start.getxKoord();
	}

	public Punkt schnittpunktMit(Strecke s) {
		// Nach Cramerscher Regel
		// LGS:  a   b   |  c
		//       d   e   |  f
		// a,d ist der Richtungsvektor von a
		float a = s.ende.getxKoord()-s.start.getxKoord();
		float d = s.ende.getyKoord()-s.start.getyKoord();
		// b,e ist der negative eigene Richtungsvektor
		float b = this.start.getxKoord()-this.ende.getxKoord();
		float e = this.start.getyKoord()-this.ende.getyKoord();
		// c,f ist die Differenz der beiden Startpunkte
		float c = this.start.getxKoord()-s.start.getxKoord();
		float f = this.start.getyKoord()-s.start.getyKoord();
		// det (a b d e)
		float det = (a*e)-(d*b);
		if (det == 0) {
			return null; //Parallel oder identisch
		}
		// ab hier ist det != 0
		float mu = ((c*e)-(f*b))/det;
		float lambda = ((a*f)-(d*c))/det;
		if ((0 <= mu) && (mu <=1) && (lambda >=0) && (lambda <=1)) {
			//Schnittpunkt liegt auf beiden Strecken
			return new Punkt((1-lambda)*start.getxKoord()+lambda*ende.getxKoord(),
					(1-lambda)*start.getyKoord()+lambda*ende.getyKoord());
		} else {
			return null; // Schnittpunkt liegt nicht auf einer der Strecken				
		}
		

	}
}
