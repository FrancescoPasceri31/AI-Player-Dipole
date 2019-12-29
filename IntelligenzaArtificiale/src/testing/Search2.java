package testing;


import java.util.HashMap;
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

	
	public Node recursiveSearch(Node n,boolean isWhite,HashMap<String, Byte> cp,int maxLevel) {
		double c = 0;
		double v = maxVal(n,-Double.MAX_VALUE,Double.MAX_VALUE,isWhite,c,cp,0,maxLevel).getValue();
		LinkedList<Node> l = new LinkedList();
		for(Node f: n.getSons()) { 
			if(f.getValue()==v) l.add(f);
			//f.setParent(null); 
		}
		//n.setSons(new LinkedList());
		//System.gc();
		return l.get((new Random()).nextInt(l.size()));
	}
	
	public Node maxVal(Node n, double alpha, double beta,boolean isWhite,double c,HashMap<String, Byte> cp, int level, int maxLevel) {
		c += e.getEuristica(n, isWhite, cp);
//		System.out.println(n.getId());
		Node ret = null;
		if(level==maxLevel || n.getBc()==0||n.getWc()==0) { 
			if(isWhite && n.getBc()==0) n.setValue(VICTORY-level);
			else if(isWhite && n.getWc()==0) n.setValue(LOSE-level);
			else if(!isWhite && n.getWc()==0) n.setValue(VICTORY-level);
			else if(!isWhite && n.getBc()==0) n.setValue(LOSE-level);
			else n.setValue(c);
//			System.out.println("Valore Euristica nodo "+n.getId()+" "+n.getValue());
			return n;
			} //da cambiare
		if(n.getSons().size()==0)
			mg.generateMoves(n, isWhite);
		double v = -Double.MAX_VALUE;
		for(Node f: n.getSons()) {
			double min = (minVal(f,alpha,beta,isWhite,c,cp,level+1,maxLevel)).getValue(); //valore del figlio
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
	
	public Node minVal(Node n, double alpha,double beta,boolean isWhite,double c,HashMap<String, Byte> cp,int level,int maxLevel) {
		c += e.getEuristica(n, isWhite, cp);
//		System.out.println(n.getId());
		Node ret = null;
		if(level==maxLevel || n.getBc()==0|| n.getWc()==0) { 
			if(isWhite && n.getBc()==0) n.setValue(VICTORY-level);
			else if(isWhite && n.getWc()==0) n.setValue(LOSE-level);
			else if(!isWhite && n.getWc()==0) n.setValue(VICTORY-level);
			else if(!isWhite && n.getBc()==0) n.setValue(LOSE-level);
			else n.setValue(c);
			return n;
			} //da cambiare
		if(n.getSons().size()==0)
			mg.generateMoves(n, !isWhite);
		double v = Double.MAX_VALUE;
		for(Node f: n.getSons()) {
			double max = (maxVal(f,alpha,beta,isWhite,c,cp,level+1,maxLevel)).getValue(); //valore del figlio
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
	
	private double max(LinkedList<Node> siblings, LinkedList<Node> ancestors) {
		double max = Double.MIN_VALUE;
		for (Node node : ancestors) {
			if(node.getValue()>max) {
				max = node.getValue();
			}
		}
		
		if(siblings!=null) {
			for (Node node : siblings) {
				if(node.hasValue() && node.getValue()>max) {
					max = node.getValue();
				}
			}
		}
		return max;
	}

	private double min(LinkedList<Node> siblings, LinkedList<Node> ancestors) {
		double min = Double.MAX_VALUE;
		for (Node node : ancestors) {
			if(node.getValue()<min) {
				min = node.getValue();
			}
		}
		
		if(siblings!=null) {
			for (Node node : siblings) {
				if(node.hasValue() && node.getValue()<min) {
					min = node.getValue();
				}
			}
		}
		return min;
	}
}