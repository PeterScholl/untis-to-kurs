package facharbeit;

import java.util.Arrays;

import graphen.Controller;


class Main{
  public static void main(String[] args) {
	  
	
    Graph gn = hausVomNikolausUnd1();
    //   Graph gn = hausVomNikolaus();
    //gn = stadtPlan();
    //gn.allePunkteMitKoordinatenBelegen();
	new Controller(gn);
	return;
	/*
    //gn.allePunkteMitKoordinatenBelegen();
    //System.out.println(Arrays.toString(gn.gibGrenzKoordinatenDerKnoten()));
    //gn.graphZeichnen();

    sleep(1000);
    //System.exit(0);
    //System.out.println(gn.anzKanten()+" AnzKnoten: "+gn.anzKnoten());
    //System.out.println(hausVomNikolaus());
    //System.out.println("Knotengrad von 3: "+gn.knotenGrad(gn.gibKnotenMitName("3")));
    //System.out.println(hausVomNikolaus().eulerTour());
    System.out.println("Euler-Tour zu erweitertem Haus vom Nikolaus\n"+hausVomNikolausUnd1().eulerTour());
    Graph g1 =stadtPlan();
    g1.dijkstra("k");
    System.out.println(g1);
    */
  }
   public static Graph hausVomNikolaus() {
        Graph g = new Graph("Haus vom Nikolaus");
        Knoten k1 = new Knoten("1",0,0); // unten Links
        Knoten k2 = new Knoten("2",2,0); // unten Rechts
        Knoten k3 = new Knoten("3",0,2); // mitte Links
        Knoten k4 = new Knoten("4",2,2); // mitte Rechts
        Knoten k5 = new Knoten("5",1,3); // oben Mitte
        g.kanteHinzufuegen(new Kante(k1,k2));
        g.kanteHinzufuegen(new Kante(k2,k4));
        g.kanteHinzufuegen(new Kante(k4,k5));
        g.kanteHinzufuegen(new Kante(k5,k3));
        g.kanteHinzufuegen(new Kante(k3,k1));
        g.kanteHinzufuegen(new Kante(k1,k4));
        g.kanteHinzufuegen(new Kante(k4,k3));
        g.kanteHinzufuegen(new Kante(k3,k2));
        return g;
    }
     public static Graph hausVomNikolausUnd1() {
        Graph g = new Graph("Haus vom Nikolaus");
        Knoten k1 = new Knoten("1",0,0); // unten Links
        Knoten k2 = new Knoten("2",2,0); // unten Rechts
        Knoten k3 = new Knoten("3",0,2); // mitte Links
        Knoten k4 = new Knoten("4",2,2); // mitte Rechts
        Knoten k5 = new Knoten("5",1,3); // oben Mitte
        Knoten k6 = new Knoten("6",1,-1); // unten Mitte
        g.kanteHinzufuegen(new Kante(k1,k2));
        g.kanteHinzufuegen(new Kante(k2,k4));
        g.kanteHinzufuegen(new Kante(k4,k5));
        g.kanteHinzufuegen(new Kante(k5,k3));
        g.kanteHinzufuegen(new Kante(k3,k1));
        g.kanteHinzufuegen(new Kante(k1,k4));
        g.kanteHinzufuegen(new Kante(k4,k3));
        g.kanteHinzufuegen(new Kante(k3,k2));
        g.kanteHinzufuegen(new Kante(k6,k1));
        g.kanteHinzufuegen(new Kante(k6,k2));
        return g;
    }
    public static Graph stadtPlan(){
      Graph g1 = new Graph("stadtplan");
      Knoten a = new Knoten("a");
      Knoten b = new Knoten("b");
      Knoten c = new Knoten("c");
      Knoten d = new Knoten("d");
      Knoten e = new Knoten("e");
      Knoten f = new Knoten("f");
      Knoten g = new Knoten("g");
      Knoten h = new Knoten("h");
      Knoten i = new Knoten("i");
      Knoten j = new Knoten("j");
      Knoten k = new Knoten("k");
      Knoten l = new Knoten("l");
      Knoten m = new Knoten("m");
      Knoten n = new Knoten("n");
      Knoten o = new Knoten("o");
      Knoten p = new Knoten("p");
      Knoten q = new Knoten("q");
      Knoten r = new Knoten("r");
      Knoten s = new Knoten("s");
      Knoten t = new Knoten("t");
      Knoten u = new Knoten("u");
      Knoten v = new Knoten("v");
      Knoten w = new Knoten("w");
      Knoten x = new Knoten("x");
      g1.kanteHinzufuegen(new Kante(a,e,8));
      g1.kanteHinzufuegen(new Kante(e,d,2));
      g1.kanteHinzufuegen(new Kante(d,b,6));
      g1.kanteHinzufuegen(new Kante(e,f,4));
      g1.kanteHinzufuegen(new Kante(f,g,3));
      g1.kanteHinzufuegen(new Kante(g,c,9));
      g1.kanteHinzufuegen(new Kante(g,h,6));
      g1.kanteHinzufuegen(new Kante(e,i,9));
      g1.kanteHinzufuegen(new Kante(g,m,17));
      g1.kanteHinzufuegen(new Kante(i,j,6));
      g1.kanteHinzufuegen(new Kante(j,o,11));
      g1.kanteHinzufuegen(new Kante(m,l,11));
      g1.kanteHinzufuegen(new Kante(l,k,10));
      g1.kanteHinzufuegen(new Kante(k,n,2));
      g1.kanteHinzufuegen(new Kante(n,p,5));
      g1.kanteHinzufuegen(new Kante(m,r,5));
      g1.kanteHinzufuegen(new Kante(o,r,8));
      g1.kanteHinzufuegen(new Kante(o,w,18));
      g1.kanteHinzufuegen(new Kante(p,q,6));
      g1.kanteHinzufuegen(new Kante(r,q,10));
      g1.kanteHinzufuegen(new Kante(q,s,3));
      g1.kanteHinzufuegen(new Kante(s,v,8));
      g1.kanteHinzufuegen(new Kante(w,v,8));
      g1.kanteHinzufuegen(new Kante(v,u,5));
      g1.kanteHinzufuegen(new Kante(u,t,3));
      g1.kanteHinzufuegen(new Kante(u,x,6));
      g1.kanteHinzufuegen(new Kante(t,x,6));
      g1.kanteHinzufuegen(new Kante(l,n,6));
      return g1;
    }

    public static void sleep(int msecs) {
      try {
        Thread.sleep(msecs);
      } catch (Exception e) {
        System.out.println(e);
      }
    }
}
