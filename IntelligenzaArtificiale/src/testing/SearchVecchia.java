package testing;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import euristica.Euristica;
import rappresentazione.Node;
import ricerca.Search;

public class SearchVecchia extends Search{
	
	public Euristica e;
	
	private final double VICTORY = 1210037.0;
	private final double LOSE = -1510237.0;
	
	
	public void init() {
		this.e = new Euristica();
		this.e.init();
	}
	
	public Node recursiveSearch(Node n,boolean isWhite,HashMap<String, Byte> cp) {
		double c = 0;
		double v = maxVal(n,-Double.MAX_VALUE,Double.MAX_VALUE,isWhite,c,cp,0).getValue();
		LinkedList<Node> l = new LinkedList();
		for(Node f: n.getSons()) 
			if(f.getValue()==v) l.add(f);
		return l.get((new Random()).nextInt(l.size()));
	}
	
	public Node maxVal(Node n, double alpha, double beta,boolean isWhite,double c,HashMap<String, Byte> cp, int level) {
		c += e.getEuristica(n, isWhite);
//		System.out.println(n.getId());
		Node ret = null;
		if(testTerminazione(n)) { 
			if(isWhite && n.getBc()==0) n.setValue(VICTORY-level);
			else if(isWhite && n.getWc()==0) n.setValue(LOSE-level);
			else if(!isWhite && n.getWc()==0) n.setValue(VICTORY-level);
			else if(!isWhite && n.getBc()==0) n.setValue(LOSE-level);
			else n.setValue(c);
//			System.out.println("Valore Euristica nodo "+n.getId()+" "+n.getValue());
			return n;
			} //da cambiare
		double v = -Double.MAX_VALUE;
		for(Node f: n.getSons()) {
			double min = (minVal(f,alpha,beta,isWhite,c,cp,level+1)).getValue(); //valore del figlio
			v = Math.max(v, min);
			if(v==min) ret=f;  //se non entra mai in questo if, allora ritornerà null
			n.setValue(v);
			if(v >= beta) return n;
//			if(v >= beta) {return n;};
			alpha = Math.max(alpha, v);
		}
		//n.setValue(v);
		return ret;
	}
	
	public Node minVal(Node n, double alpha,double beta,boolean isWhite,double c,HashMap<String, Byte> cp,int level) {
		c += e.getEuristica(n, isWhite);
//		System.out.println(n.getId());
		Node ret = null;
		if(testTerminazione(n)) { 
			if(isWhite && n.getBc()==0) n.setValue(VICTORY-level);
			else if(isWhite && n.getWc()==0) n.setValue(LOSE-level);
			else if(!isWhite && n.getWc()==0) n.setValue(VICTORY-level);
			else if(!isWhite && n.getBc()==0) n.setValue(LOSE-level);
			else n.setValue(c);
			return n;
			} //da cambiare
		double v = Double.MAX_VALUE;
		for(Node f: n.getSons()) {
			double max = (maxVal(f,alpha,beta,isWhite,c,cp,level+1)).getValue(); //valore del figlio
			v = Math.min(v, max);
			if(v==max) ret=f;
			n.setValue(v);
     		if(v <= alpha) return n;;
//			if(v <= alpha) {return n;};
			beta = Math.max(beta, v);
		}
		//n.setValue(v);
		return ret;
		
	}

	
	public boolean testTerminazione(Node n) {
		return n.leaf() || !n.expandable();
	}
	

}