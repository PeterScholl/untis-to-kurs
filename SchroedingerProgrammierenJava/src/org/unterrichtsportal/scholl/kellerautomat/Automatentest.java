package org.unterrichtsportal.scholl.kellerautomat;

public class Automatentest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Kellerautomat k = new Kellerautomat();
		System.out.println(k.readFile("/home/peter/eclipse-workspace/SchroedingerProgrammierenJava/src/org/unterrichtsportal/scholl/kellerautomat/automat2.txt"));
        System.out.println("Teste Wort: aabbcccc");
		k.testeWort("aabbcccc");
		System.out.println("Teste Wort: aa");
        k.testeWort("aa");
	}

}
