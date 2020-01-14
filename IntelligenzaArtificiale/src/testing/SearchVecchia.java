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
	/*
	 * public Node search(Node t) { int z = 0; LinkedList<Node> l = new
	 * LinkedList(); //va settata più grande l.add(t); double alpha =
	 * Double.MIN_VALUE, beta = Double.MAX_VALUE; int p_min =0 ,p_max = 0,
	 * not_pruned =0; Node best=null; double m; // valore del miglior figlio
	 * while(!l.isEmpty()) { z++; Node x = l.getFirst(); if(x.equals(t) &&
	 * x.hasValue()) { //
	 * System.out.println("iterazioni: "+z+"\npruned min: "+p_min+"\npruned max: "
	 * +p_max+"\nnot pruned: "+not_pruned); return best; } if(x.hasValue()) { Node p
	 * = x.getParent(); boolean pruned = false;
	 * 
	 * if(! p.isMax()) { //se nodo min alpha = max(p.siblings(), p.ancestors()); //
	 * false = cerco minimizzatori if(x.getValue()<=alpha) { for(Node n:
	 * p.getSons()) l.remove(n); l.remove(p); pruned = true; p_min++; }
	 * 
	 * }else if( p.isMax()) { beta = min(p.siblings(),p.ancestors()); // true =
	 * cerco massimizzatori if(x.getValue()>=beta) { for(Node n: p.getSons())
	 * l.remove(n); l.remove(p); pruned = true; p_max++; } } if(!pruned) {
	 * not_pruned++; if(! p.isMax()) {
	 * p.setValue(Math.min(x.getValue(),p.getValue())); } else { m =
	 * Math.max(x.getValue(),p.getValue()); p.setValue(m); if( p.equals(t) &&
	 * m==x.getValue()) { best=x; } } p.setHasValue(true); l.remove(x); } }else {
	 * if(!x.hasValue() && (x.leaf() || !x.expandable())) { x.setValue((new
	 * Random()).nextDouble()); // x.setValue(z); x.setHasValue(true); }else {
	 * if(x.isMax()) x.setValue(Double.MIN_VALUE); else if(! x.isMax())
	 * x.setValue(Double.MAX_VALUE); int i = 0; for(Node n: x.getSons()) { l.add(i,
	 * n); i++; } //System.out.println(l); } } } return null; // DA CAMBIARE!! }
	 */
	
	public Node recursiveSearch(Node n,boolean isWhite,HashMap<String, Byte> cp) {
		double c = 0;
		double v = maxVal(n,-Double.MAX_VALUE,Double.MAX_VALUE,isWhite,c,cp,0).getValue();
		LinkedList<Node> l = new LinkedList();
		for(Node f: n.getSons()) 
			if(f.getValue()==v) l.add(f);
		return l.get((new Random()).nextInt(l.size()));
	}
	
	public Node maxVal(Node n, double alpha, double beta,boolean isWhite,double c,HashMap<String, Byte> cp, int level) {
		c += e.getEuristica(n, isWhite, cp);
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
		c += e.getEuristica(n, isWhite, cp);
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