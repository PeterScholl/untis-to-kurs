package org.unterrichtsportal.scholl.bwinf36;


public class Punkt {
	private float xKoord;
	private float yKoord;
	public Punkt(float xKoord, float yKoord) {
		super();
		this.xKoord = xKoord;
		this.yKoord = yKoord;
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

}
