package testing;


import java.util.Comparator;
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
	
	public void pathSearch(Node n, double v,int level, int maxLevel) {
		if(level==maxLevel) return;
		
		LinkedList<Node> l = new LinkedList<Node>();
		for(Node f: n.getSons())
			if(f.getValue()==v)
				l.add(f);
		
		for(Node x:l) {
			n.getSons().remove(x);
			n.getSons().addFirst(x);
			pathSearch(x,v,level+1, maxLevel);
		}
	}
	
	public Node recursiveSearch(Node n,boolean isWhite, int maxLevel) {
		double v=0.0;
		for(int i=1;i<=maxLevel;i++) {
			v = (maxVal(n,-Double.MAX_VALUE,Double.MAX_VALUE,isWhite,0,i)).getValue();
			pathSearch(n,v,0,i);
		}
		LinkedList<Node> l = new LinkedList();
		for (Node f : n.getSons())
			if (f.getValue() == v)
				l.add(f);
		System.out.println(l);
		if(l.size()==1) return l.getFirst();
		try {
			return l.get((new Random()).nextInt(l.size()));
		}catch(IllegalArgumentException e) {
			return n.getSons().getFirst();
		}
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
//		Collections.sort(n.getSons(),new Decrescente());
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
//		Collections.sort(n.getSons(),new Crescente());
		double v = Double.MAX_VALUE;
		for(Node f: n.getSons()) {
			double max = (maxVal(f,alpha,beta,isWhite,level+1,maxLevel)).getValue(); //valore del figlio
			v = Math.min(v, max);
			if(v==max) ret=f;
			n.setValue(v);
     		if(v <= alpha) return n;
			beta = Math.max(beta, v);
		}
		return ret;
		
	}

	
	public boolean testTerminazione(Node n, int level, int maxLevel) {
		return level==maxLevel || n.getBc()==0||n.getWc()==0;
	}
	
	
	private class Crescente implements Comparator<Node>{

		@Override
		public int compare(Node n1, Node n2) {
			if(n1.getValue() > n2.getValue()) return 1;
			else if(n1.getValue() < n2.getValue()) return -1;
			else return 0;
		}
		
	}
	
	private class Decrescente implements Comparator<Node>{

		@Override
		public int compare(Node n1, Node n2) {
			if(n2.getValue() > n1.getValue()) return 1;
			else if(n2.getValue() < n1.getValue()) return -1;
			else return 0;
		}
		
	}
	

	

}