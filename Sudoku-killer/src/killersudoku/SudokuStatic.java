package killersudoku;

import java.util.ArrayList;

public class SudokuStatic {
	public static ArrayList<Possibilities> possibilities(int summe, int anzahl) {
		return possibilities_rek(summe, anzahl, new Possibilities());
	}
	
	public static ArrayList<Possibilities> possibilities(int summe, int anzahl, Possibilities allowed) {
		return possibilities_rek(summe,anzahl, allowed);
	}
	
	public static ArrayList<Possibilities> possibilities(int summe, int anzahl, Possibilities allowed, Possibilities forced) {
		if ((forced.possiblevalues & allowed.possiblevalues) != forced.possiblevalues) return new ArrayList<Possibilities>();
		ArrayList<Possibilities> sublsg = possibilities_rek(summe-forced.sum(), anzahl-forced.anz(), new Possibilities(allowed.possiblevalues & ~forced.possiblevalues));
		ArrayList<Possibilities> result = new ArrayList<Possibilities>();
		for (Possibilities p : sublsg) { // for every possibility in the solution 
			Possibilities t = new Possibilities(p.possiblevalues | forced.possiblevalues);
			result.add(t); //add the forced numbers to the solution and put in into the result array
		}		
		return result;
	}

	private static ArrayList<Possibilities> possibilities_rek(int summe, int anzahl, Possibilities rest) {
		ArrayList<Possibilities> result = new ArrayList<Possibilities>();
		if (anzahl == 1) {
			if (rest.hasPossibility(summe)) {
				result.add(new Possibilities(0).addPossibilityC(summe));
			} 
			return result;
		} else if (anzahl > 1) {
			for (Integer i : rest.possibilitiesArray()) { //for all numbers which are allowed to use / not yet used
				if (i < summe) { // if this number is possible to add without rising above the searched sum
					// generate all Possible Solutions for the rest with one integer less and only possibilities bigger then the actualy used 
					ArrayList<Possibilities> sublsg = possibilities_rek(summe - i, anzahl - 1, rest.removePossibilitiesUpToC(i));
					for (Possibilities p : sublsg) { // for every possibility in the solution 
						result.add(p.addPossibilityC(i)); //add the used Integer i to the solution and put in into the result array
					}
				}
			}
			return result;
		} else { // Something went wrong
			System.err.println("Mistake in generating possibilities");
			return null;
		}
	}
	
	public static boolean isPossible(int summe, int anzahl, Possibilities allowed) {
		return isPossible_rek(summe, anzahl, allowed);
	}
	
	private static boolean isPossible_rek(int summe, int anzahl, Possibilities allowed) {
		if (summe <=0) {
			return false;
		} else if (anzahl == 1) {
			return allowed.hasPossibility(summe);
		} else if (anzahl > 1) {
			for (Integer i : allowed.possibilitiesArray()) { 
				if (isPossible_rek(summe-i,anzahl-1,allowed.removePossibilitiesUpToC(i))) {
					return true;
				}
			}
			return false;
		} else { // Something went wrong
			System.err.println("Mistake in generating possibilities");
			return false;			
		}
	}
	
	 // Function to remove duplicates from an ArrayList 
    public static <T> ArrayList<T> removeDuplicates(ArrayList<T> list) 
    { 
  
        // Create a new ArrayList 
        ArrayList<T> newList = new ArrayList<T>(); 
  
        // Traverse through the first list 
        for (T element : list) { 
  
            // If this element is not present in newList 
            // then add it 
            if (!newList.contains(element)) { 
  
                newList.add(element); 
            } 
        } 
  
        // return the new list 
        return newList; 
    } 

}
