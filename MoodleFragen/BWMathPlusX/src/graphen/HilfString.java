package graphen;

import java.util.Arrays;

import aufgabenGenerator.Hilfsfunktionen;

/**
 * In dieser Klasse geht es um den Umgang mit der String[] Representation von Knoten/Ecken/Vertices und Kanten/Edges
 * 
 * Knoten: {name, (x,y), ...} 
 * Kanten: {nameStart, nameZiel, ...}
 * 
 * Argumente:
 * -f Farbe entweder Integer Nummer der Farbe oder Hex-Representation der RGB-Farbe AARRGGBB
 * -g Gewicht der Kante
 * -d Grad des Knotens
 * 
 * 
 * @author peter
 *
 */
public class HilfString {
	
	/**
	 * Hängt einen String an ein Array an
	 * @param array das Ursprungsarray
	 * @param append der anzuhängede String
	 * @return das erweiterte Array
	 */
	public static String[] appendString(String[] array, String append) {
		System.out.println("in Controller appendString: " + Arrays.toString(array) + " - " + append);
		if (array == null)
			return new String[] { append };
		if (append == null)
			return array;
		String[] ret = new String[array.length + 1];
		for (int i = 0; i < array.length; i++)
			ret[i] = array[i];
		ret[array.length] = append;
		System.out.println("Returns: " + Arrays.toString(ret));
		return ret;
	}
	/**
	 * Hängt ein Array (append) an ein Array(array) an
	 * @param array das Ursprungsarray
	 * @param append des anzuhängede Array
	 * @return das erweiterte Array
	 */
	public static String[] appendArray(String[] array, String[] append) {
		if (array == null)
			return append;
		if (append == null)
			return array;
		String[] ret = new String[array.length + append.length];
		for (int i = 0; i < array.length; i++)
			ret[i] = array[i];
		for (int i = 0; i < append.length; i++) {
			ret[array.length+i]=append[i];
		}
		return ret;
	}

	/**
	 * prüft ob ein String in einem Array vorhanden ist, liefert die Position oder
	 * -1 wenn nicht vorhanden
	 * 
	 * @param array das zu durchsuchende String[]
	 * @param suche den zu suchenden String
	 * @return die Position des Strings im Array oder -1
	 */
	public static int stringArrayEnthaelt(String[] array, String suche) {
		if (array != null) {
			System.out.println(Arrays.toString(array));
			for (int i = 0; i < array.length; i++) {
				System.out.println("in Controller - stringArrayEnthaelt: " + array[i] + " - " + suche);
				if (array[i].equals(suche))
					return i;
			}
		}
		return -1;
	}

	/**
	 * liefert den ersten Eintrag in einem Array der mit den String startsWith beginnt
	 * @param array das zu durchsuchende Array
	 * @param startsWith das Prefix nach dem gesucht wird
	 * @return der vollständige Eintrag (inkl. Prefix)
	 */
	public static String stringArrayElement(String[] array, String startsWith) {
		if (array != null) {
			for (int i = 0; i < array.length; i++) {
				if (array[i].startsWith(startsWith))
					return array[i];
			}
		}
		return ""; // vielleicht besser null?
	}

	/**
	 * Sucht die Position des ersten Eintrags in einem Array, der mit den zu suchenden Prefix beginnt
	 * @param array das zu durchsuchende Array
	 * @param startsWith das zu suchende Prefix
	 * @return die Position des gefundenen Objekts in dem Array (oder -1 falls nicht gefunden)
	 */
	public static int stringArrayElementPos(String[] array, String startsWith) {
		if (array != null && startsWith != null && startsWith.length() > 0) {
			for (int i = 0; i < array.length; i++) {
				if (array[i].startsWith(startsWith))
					return i;
			}
		}
		return -1;
	}
	
	/**
	 * liefert den Teil des Arrays ab Beginn index (inklusiv) bis endindex (exclusiv)
	 * @param array das zu kürzende Array
	 * @param beginindex der Index ab dem gestartet werden soll //inklusiv
	 * @param endindex der Index bis zu dem gesucht werden soll
	 * @return das verkürzte Array oder ein leeres Array
	 */
	public static String[] subArray(String[] array, int beginindex, int endindex) {
		if (array==null || array.length<=beginindex || beginindex>=endindex) return new String[] {};
		endindex=(array.length<endindex?array.length:endindex);
		String[] ret = new String[endindex-beginindex];
		for (int i = 0; i < ret.length; i++) {
			ret[i]=array[i+beginindex];
		}
		return ret;
	}
	
	/**
	 * liefert den Teil des Arrays ab Beginn index
	 * @param array das zu kürzende Array
	 * @param beginindex der Index ab dem gestartet werden soll //inklusiv
	 * @return das verkürzte Array oder ein leeres Array
	 */
	public static String[] subArray(String[] array, int beginindex) {
		return subArray(array, beginindex, array.length);
	}
	
	public static String[] duplikateLoeschen(String[] array) {
		if (array==null) return array;
		String[] ret = new String[array.length];
		for (int i=0; i<array.length; i++) ret[i]=array[i];
		int size = array.length;
		for (int i=0; i<size-1;i++) { //element dessen Duplikate gesucht werden sollen
			for (int j=i+1;j<size;j++) {
				if (ret[j].equals(ret[i])) {//Duplikat gefunden
					ret[j--]=ret[--size];
				}
			}
		}
		return HilfString.subArray(ret, 0, size);
	}
	/**
	 * loescht den Eintrag im Array an der angegebenen Position
	 * @param array - das Array
	 * @param i - die zu löschende Position
	 * @return Das um eins kleinere Array
	 */
	public static String[] removePosFromArray(String[] array, int index) {
		if (array==null || index<0 || index>=array.length) return array;
		String[] ret = new String[array.length-1];
		for (int i=0; i<ret.length;i++) {
			ret[i]=array[(i<index?i:i+1)];
		}
		return ret;
	}


}
