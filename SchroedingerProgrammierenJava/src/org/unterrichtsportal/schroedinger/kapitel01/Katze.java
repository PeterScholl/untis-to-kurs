package org.unterrichtsportal.schroedinger.kapitel01;

public class Katze {
	private String name;
	private Katze schwester;
	
	public Katze(String name) {
		this.name = name;
	}

	public Katze(String name, Katze schwester) {
		this.name = name;
		this.schwester = schwester;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Katze getSchwester() {
		return schwester;
	}

	public void setSchwester(Katze schwester) {
		this.schwester = schwester;
	}
	
	

}
