package org.unterrichtsportal.schroedinger.kapitel01;

public class CheckWertebereich {
	public static void main(String[] args) {
		Long z = Long.valueOf(args[0]);
		System.out.println(z.toString()+" passt in long Wertebereich: "+(z>=Long.MIN_VALUE && z<=Long.MAX_VALUE));
		System.out.println(z.toString()+" passt in int Wertebereich: "+(z>=Integer.MIN_VALUE && z<=Integer.MAX_VALUE));
		System.out.println(z.toString()+" passt in short Wertebereich: "+(z>=Short.MIN_VALUE && z<=Short.MAX_VALUE));
		System.out.println(z.toString()+" passt in byte Wertebereich: "+(z>=Byte.MIN_VALUE && z<=Byte.MAX_VALUE));
				
	}

}
