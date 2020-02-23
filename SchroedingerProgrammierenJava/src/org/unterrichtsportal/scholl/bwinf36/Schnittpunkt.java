package org.unterrichtsportal.scholl.bwinf36;


public class Schnittpunkt {
	private float xKoord;
	private float yKoord;
	private Strecke s1,s2;
	
	public Strecke getS1() {
		return s1;
	}
	public void setS1(Strecke s1) {
		this.s1 = s1;
	}
	public Strecke getS2() {
		return s2;
	}
	public void setS2(Strecke s2) {
		this.s2 = s2;
	}
	public Schnittpunkt(Punkt p, Strecke s1, Strecke s2) {
		super();
		this.xKoord = p.getxKoord();
		this.yKoord = p.getyKoord();
		this.s1 = s1;
		this.s2 = s2;
	}
	public float getxKoord() {
		return xKoord;
	}
	public void setxKoord(float xKoord) {
		this.xKoord = xKoord;
	}
	public float getyKoord() {
		return yKoord;
	}
	public void setyKoord(float yKoord) {
		this.yKoord = yKoord;
	}
	
	public boolean aufStrecke (Strecke s) {
		return (s.equals(s1) || s.equals(s2));
	}

}
