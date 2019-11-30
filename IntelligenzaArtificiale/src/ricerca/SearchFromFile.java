package ricerca;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.LinkedList;
import java.util.Random;

import euristica.Euristica;
import rappresentazione.NodeToFile;

public class SearchFromFile {
	
	private Random r = new Random();
	
	public NodeToFile recursiveSearch(NodeToFile n, boolean isWhite) throws Exception {
		r.setSeed(42);
		return maxVal(n, Double.MIN_VALUE, Double.MAX_VALUE, isWhite);
	}

	public NodeToFile maxVal(NodeToFile n, double alpha, double beta, boolean isWhite) throws Exception{
//		System.out.println(n.getId());
		NodeToFile ret = null;
		if (testTerminazione(n)) {
			n.setValue(r.nextInt(24)+1);
			return n;
		} // da cambiare
		double v = -Double.MAX_VALUE;
		ObjectInputStream i;
		for (Integer f : n.getSons()) {
			i = new ObjectInputStream(new FileInputStream("./Nodes/"+f));
			NodeToFile x = (NodeToFile) i.readObject();
			i.close();
			double min = (minVal(x, alpha, beta, isWhite)).getValue(); // valore del figlio
			v = Math.max(v, min);
			if (v == min)
				ret = x; // se non entra mai in questo if, allora ritornerà null
			n.setValue(v);
			if (v >= beta)
				return n;
			;
//			if(v >= beta) {return n;};
			alpha = Math.max(alpha, v);
		}
		// n.setValue(v);
		return ret;
	}

	public NodeToFile minVal(NodeToFile n, double alpha, double beta, boolean isWhite) throws Exception {
//		System.out.println(n.getId());
		NodeToFile ret = null;
		if (testTerminazione(n)) {
			n.setValue(r.nextInt(24)+1);
			return n;
		} // da cambiare
		double v = Double.MAX_VALUE;
		ObjectInputStream i;
		for (Integer f : n.getSons()) {
			i = new ObjectInputStream(new FileInputStream("./Nodes/"+f));
			NodeToFile x = (NodeToFile) i.readObject();
			i.close();
			double max = (maxVal(x, alpha, beta, isWhite)).getValue(); // valore del figlio
			v = Math.min(v, max);
			if (v == max)
				ret = x;
			n.setValue(v);
			if (v <= alpha)
				return n;
			;
//			if(v <= alpha) {return n;};
			beta = Math.max(beta, v);
		}
		// n.setValue(v);
		return ret;

	}

	public boolean testTerminazione(NodeToFile n) {
		if(n.leaf()) return true;
		ObjectInputStream i;
		try {
			i = new ObjectInputStream(new FileInputStream("./Nodes/"+n.getSons().getFirst()));
		} catch (Exception e) {
			return true;
		}
		return false;
	}

}