package testing;


import java.util.LinkedList;
import java.util.Random;

import euristica.Euristica;
import generators.MovesGenerator;
import rappresentazione.Node;
 
public class Search2 {
	
	public Euristica e;
	public MovesGenerator mg;
	
	private final double VICTORY = 121037.0;
	private final double LOSE = -151237.0;
	
	
	public void init() {
		this.e = new Euristica(); 
		this.e.init();
		this.mg = new MovesGenerator(); 
		this.mg.init();
	}

	
	public Node recursiveSearch(Node n,boolean isWhite,int maxLevel) {
		double v = maxVal(n,-Double.MAX_VALUE,Double.MAX_VALUE,isWhite,0,maxLevel).getValue();
		LinkedList<Node> l = new LinkedList();
		for(Node f: n.getSons()) { 
			if(f.getValue()==v) l.add(f);
		}
		System.out.print("l: ");
		System.out.println(l);
		return l.get((new Random()).nextInt(l.size()));
	}
	
	public Node maxVal(Node n, double alpha, double beta,boolean isWhite, int level, int maxLevel) {
		Node ret = n;
		if(testTerminazione(n,level,maxLevel)) { 
			if(isWhite && n.getBc()==0) n.setValue(VICTORY-level);
			else if(isWhite && n.getWc()==0) n.setValue(LOSE-level);
			else if(!isWhite && n.getWc()==0) n.setValue(VICTORY-level);
			else if(!isWhite && n.getBc()==0) n.setValue(LOSE-level);
			return n;
			} 
		if(n.getSons().size()==0) 
			mg.generateMoves(n, isWhite,isWhite);
		double v = -Double.MAX_VALUE;
		for(Node f: n.getSons()) {
			double min = (minVal(f,alpha,beta,isWhite,level+1,maxLevel)).getValue(); //valore del figlio
			v = Math.max(v, min);
			if(v==min) ret=f;  //se non entra mai in questo if, allora ritornerà null
			n.setValue(v);
			if(v >= beta) return n;
			alpha = Math.max(alpha, v);
		}
		return ret;
	}
	
	public Node minVal(Node n, double alpha,double beta,boolean isWhite,int level,int maxLevel) {
		Node ret = n;
		if(testTerminazione(n,level,maxLevel)) { 
			if(isWhite && n.getBc()==0) n.setValue(VICTORY-level);
			else if(isWhite && n.getWc()==0) n.setValue(LOSE-level);
			else if(!isWhite && n.getWc()==0) n.setValue(VICTORY-level);
			else if(!isWhite && n.getBc()==0) n.setValue(LOSE-level);
			return n;
			} //da cambiare
		if(n.getSons().size()==0)
			mg.generateMoves(n, !isWhite,isWhite); 
		double v = Double.MAX_VALUE;
		for(Node f: n.getSons()) {
			double max = (maxVal(f,alpha,beta,isWhite,level+1,maxLevel)).getValue(); //valore del figlio
			v = Math.min(v, max);
			if(v==max) ret=f;
			n.setValue(v);
     		if(v <= alpha) return n;;
			beta = Math.max(beta, v);
		}
		return ret;
		
	}

	
	public boolean testTerminazione(Node n, int level, int maxLevel) {
		return level==maxLevel || n.getBc()==0||n.getWc()==0;
	}
	

	

}