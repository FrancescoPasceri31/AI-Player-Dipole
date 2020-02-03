package testing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import euristica.Euristica;
import generators.MovesGenerator;
import rappresentazione.Node;

public class Search4 {

	// valori dentro generator, espansione solo se necessaria

	private Euristica e;
	private MovesGenerator mg;

	private final double VICTORY = 1210037.0;
	private final double LOSE = -1510237.0;

	public void init() {
		this.e = new Euristica();
		this.e.init();
		this.mg = new MovesGenerator();
		this.mg.init();
	}

	public Node recursiveSearch(Node n, boolean isWhite, int maxLevel) {
		double v = maxVal(n, -Double.MAX_VALUE, Double.MAX_VALUE, isWhite, 0, maxLevel).getValue();
		LinkedList<Node> l = new LinkedList();
		for (Node f : n.getSons())
			if (f.getValue() == v)
				l.add(f);
//		System.out.println(l);
//		if(l.size()==1) return l.getFirst();
//		double max = -Double.MAX_VALUE;
//		Node ret = null;
//		for(Node x: l) {
//			Node t = minVal(x,-Double.MAX_VALUE,Double.MAX_VALUE,!isWhite, 0,maxLevel);
//			if(t.getValue() > max) {
//				max = t.getValue();
//				ret = x;
//			}
//		}
//		return ret;
		
		
		
		
		
		System.out.println(l);
		if(l.size()==1) return l.getFirst();
		Node best = maxVal(n, -Double.MAX_VALUE, Double.MAX_VALUE, isWhite, 0, 6);
		System.out.println("Best: "+best);
		if(l.contains(best)) return best;
		try {
			return l.get((new Random()).nextInt(l.size()));
		}catch(IllegalArgumentException e) {
			return n.getSons().getFirst();
		}
	}

	public Node maxVal(Node n, double alpha, double beta, boolean isWhite, int level, int maxLevel) {
		Node ret = n;
		if (testTerminazione(n, level, maxLevel)) {
			if (isWhite && n.getBc() == 0)
				n.setValue(VICTORY - level);
			else if (isWhite && n.getWc() == 0)
				n.setValue(LOSE - level);
			else if (!isWhite && n.getWc() == 0)
				n.setValue(VICTORY - level);
			else if (!isWhite && n.getBc() == 0)
				n.setValue(LOSE - level);
			return n;
		}
		if (n.getSons().size() == 0)
			mg.generateMoves(n, isWhite, isWhite);

		double v = -Double.MAX_VALUE;

		double th = -Double.MAX_VALUE;
		for (Node f : n.getSons()) {  
			if (f.getValue() > th) {
				if (f.getSons().size() == 0)
					mg.generateMoves(f, !isWhite, isWhite);
				double tmp = f.getValue();
//				f.setValue(f.getValue() - (double)(f.getSons().size() - f.getMovesOut())/f.getSons().size());
				f.setValue(f.getValue() - (double)f.getAttacks());
//				f.setValue(f.getValue() - (double)f.getMoves());
				if (f.getValue() > th)
					th = tmp;
				else
					continue;
			} else
				continue;

//		for(Node f: n.getSons()) {

			double min = (minVal(f, alpha, beta, isWhite, level + 1, maxLevel)).getValue(); // valore del figlio
			v = Math.max(v, min);
			if (v == min)
				ret = f; // se non entra mai in questo if, allora ritornerà null
			n.setValue(v);
			if (v >= beta) {
				return n;
			}
			alpha = Math.max(alpha, v);
		}
		return ret;
	}

	public Node minVal(Node n, double alpha, double beta, boolean isWhite, int level, int maxLevel) {
		Node ret = n;
		if (testTerminazione(n, level, maxLevel)) {
			if (isWhite && n.getBc() == 0)
				n.setValue(VICTORY - level);
			else if (isWhite && n.getWc() == 0)
				n.setValue(LOSE - level);
			else if (!isWhite && n.getWc() == 0)
				n.setValue(VICTORY - level);
			else if (!isWhite && n.getBc() == 0)
				n.setValue(LOSE - level);
			return n;
		} // da cambiare
		if (n.getSons().size() == 0)
			mg.generateMoves(n, !isWhite, isWhite);

		double v = Double.MAX_VALUE;

		double th = Double.MAX_VALUE;
		for (Node f : n.getSons()) {
			if (f.getValue() < th) {
				if (f.getSons().size() == 0)
					mg.generateMoves(f, isWhite, isWhite);
				double tmp = f.getValue();
//				f.setValue(f.getValue() + (double)(f.getSons().size() - f.getMovesOut())/f.getSons().size());
				f.setValue(f.getValue() + (double)f.getAttacks());
//				f.setValue(f.getValue() + (double)f.getMoves());
				if (f.getValue() < th)
					th = tmp;
				else
					continue;
			} else
				continue;

//		for(Node f: n.getSons()) {
			double max = (maxVal(f, alpha, beta, isWhite, level + 1, maxLevel)).getValue(); // valore del figlio
			v = Math.min(v, max);
			if (v == max)
				ret = f;
			n.setValue(v);
			if (v <= alpha) {
				return n;
				
			}
			beta = Math.max(beta, v);
		}
		return ret;

	}

	public boolean testTerminazione(Node n, int level, int maxLevel) {
		return level >= maxLevel || n.getBc() == 0 || n.getWc() == 0;
	}

}