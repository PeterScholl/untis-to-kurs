package bwinf39;

/**
 * Die Klasse LVM enthaelt die Funktionsweise der Luftballon-Vepackungsmaschine
 * sie verwaltet die Fächer, die Summe im Ausgabefach, die befüllten Tüten und die
 * mit zu vielen Ballons gefüllten Tyten und gibt bei Anfrage die entsprechenden 
 * Informationen aus.
 * 
 * @author (Ihr Name) 
 * @version (eine Versionsnummer oder ein Datum)
 */
public class Aufgabe1
{
    // Instanzvariablen bzw. Attribute der Klasse bzw. eines Objekts
    private int summe; //Ballons im Ausgabefach
    private int packete; // Anzahl alle befüllten Pakete
    private int fehler; // Anzahl davon mit zu vielen befüllten Paketen
    private int[] faecher = new int[10]; // Die Anzahlen in den 10 Faechern (0 wenn leer)
    private Queue<Integer> q; //Warteschlange mit den Befüllungszahlen

    /**
     * Konstruktor für Objekte der Klasse LVM
     */
    public Aufgabe1()
    {
        // Instanzvariable initialisieren
        summe = 0;
        packete = 0;
        fehler = 0;
        for (int i = 0 ; i <faecher.length; i++) faecher[i]=0;
        q = new Queue<Integer>();
    }
    public static void main(String[] args) {
    	System.out.println("test");
    }

    /**
     * leert das angegebene Fach in das Ausgabefach
     * 
     * @param  pFachNr   zu leerendes Fach
     */
    public void fach(int pFachNr) 
    {
        // tragen Sie hier den Code ein
        summe+=faecher[pFachNr];
        faecher[pFachNr]=0;

    }

    /**
     * leert das Ausgabefach in eine Tüte, passt
     * die Attribute entsprechend an und befüllt die leeren Faecher mit
     * den naechsten Werten aus der Befüllungsschlange.
     * Passt die Zähler für packete und fehler an.
     */
    public void verpacken() {
        if (summe >= 20) {
            packete++;
            if (summe>20) fehler++;
            summe=0;
        }
        befuelleLeereFaecherMitNaechstenWertenDerSchlange();
    }

    /**
     * setzt alle Werte auf 0 zurück (ausser der Schlange) und befüllt
     * die (jetzt) leeren Fächer mit den Inhalten aus der Schlange
     */
    public void starteNeu() {
        summe=0;
        packete=0;
        fehler=0;
        for (int i = 0; i<faecher.length; i++) {
            faecher[i]=0;
        }
        befuelleLeereFaecherMitNaechstenWertenDerSchlange();
    }

    /**
     * erzeugt eine entsprechende Anzahl von Elementen und hängt sie an die
     * Schlange an. Die Werte sollen zwischen 1 und 10 liegen (einschliesslich)
     */
    public void erzeugeZufallsSchlange(int anzahlElemente) {
        for (int i = 0; i<anzahlElemente; i++) {
            q.enqueue(new java.util.Random().nextInt(10)+1);
        }
    }

    /**
     * leert die Schlange bzw. eretzt sie durch eine neue leere Schlange
     */
    public void leereSchlange() {
        q = new Queue<Integer>();
    }

    /**
     * (Hilfmethode) durchsucht das Feld nach leeren Fächern und befüllt diese mit dem nächsten
     * Wert der Schlange (natürlich wird der Wert auch aus der Schlange entfernt)
     */
    private void befuelleLeereFaecherMitNaechstenWertenDerSchlange() {
        for (int i = 0; i < faecher.length; i++) {
            if (faecher[i]==0 && !q.isEmpty()) {
                faecher[i]=q.front();
                q.dequeue();
            }
        }
    }

    /**
     * haengt das übergebene Elment an die Schlange an
     * 
     * 
     */
    public void haengeElementAnSchlangeAn(int pElement) {
        q.enqueue(pElement);

    }

    public int gibInhaltAusgabefach() {
        return summe;  
    }

    public int gibInhaltFach(int pFachNr) {
        return faecher[pFachNr];
    }

    public int gibAnzahlBefuelltePackete() {
        return packete;
    }

    public int gibAnzahlFalscherPackete() {
        return fehler;
    }

    public boolean istSchlangeLeer() {
        return q.isEmpty();
    }
}
