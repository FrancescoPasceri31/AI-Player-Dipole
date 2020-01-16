package ricerca;


import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import euristica.Euristica;
import generators.MovesGenerator;
import rappresentazione.Node;
import testing.ByValue;

public class Search {
	
	public Euristica e;
	public MovesGenerator mg;
	
	private final double VICTORY = 1210037.0;
	private final double LOSE = -1510237.0;
	
	
	public void init() {
		this.mg = new MovesGenerator();
		this.mg.init();
		this.e = new Euristica();
		this.e.init();
	}
	
	public Node recursiveSearch(Node n,boolean isWhite) {
		boolean myColor = isWhite;
		double v = maxVal(n,-Double.MAX_VALUE,Double.MAX_VALUE,isWhite,myColor,0).getValue();
		LinkedList<Node> l = new LinkedList();
		for(Node f: n.getSons()) 
			if(f.getValue()==v) l.add(f);
		System.out.println(l);
		return l.get((new Random()).nextInt(l.size()));
	}
	
	
	public Node maxVal(Node n, double alpha, double beta,boolean isWhite,boolean myColor, int level) {
		Node ret = n;
		if(testTerminazione(n)|| n.getBc()==0||n.getWc()==0) { 
			if(isWhite && n.getBc()==0) n.setValue(VICTORY-level);
			else if(isWhite && n.getWc()==0) n.setValue(LOSE-level);
			else if(!isWhite && n.getWc()==0) n.setValue(VICTORY-level);
			else if(!isWhite && n.getBc()==0) n.setValue(LOSE-level);
			else {
				if(n.getToExpand()) {
					mg.generateMoves(n, !isWhite,myColor);
					n.setToExpand(false);
				}else {
					n.setToExpand(true);
					return n;
				}
			}
		} //da cambiare
		double v = -Double.MAX_VALUE;
		for(Node f: n.getSons()) {
			double min = (minVal(f,alpha,beta,isWhite,myColor,level+1)).getValue(); //valore del figlio
			v = Math.max(v, min);
			if(v==min) ret=f;  //se non entra mai in questo if, allora ritornerà null
			n.setValue(v);
			if(v >= beta) return n;
			alpha = Math.max(alpha, v);
		}
		return ret;
	}
	
	public Node minVal(Node n, double alpha,double beta,boolean isWhite,boolean myColor,int level) {
		Node ret = n;
		if(testTerminazione(n)|| n.getBc()==0||n.getWc()==0) { 
			if(isWhite && n.getBc()==0) n.setValue(VICTORY-level);
			else if(isWhite && n.getWc()==0) n.setValue(LOSE-level);
			else if(!isWhite && n.getWc()==0) n.setValue(VICTORY-level);
			else if(!isWhite && n.getBc()==0) n.setValue(LOSE-level);
			else {
				
				if(n.getToExpand()) {
					mg.generateMoves(n, isWhite,myColor);
					n.setToExpand(false);
				}else {
					n.setToExpand(true);
					return n;
				}
			}
			} //da cambiare
		double v = Double.MAX_VALUE;
		for(Node f: n.getSons()) {
			double max = (maxVal(f,alpha,beta,isWhite,myColor,level+1)).getValue(); //valore del figlio
			v = Math.min(v, max);
			if(v==max) ret=f;
			n.setValue(v);
     		if(v <= alpha) return n;;
			beta = Math.max(beta, v);
		}
		return ret;
		
	}

	
	public boolean testTerminazione(Node n) {
		return n.leaf() || !n.expandable();
	}

}