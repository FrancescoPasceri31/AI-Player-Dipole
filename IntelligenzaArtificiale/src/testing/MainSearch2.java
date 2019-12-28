package testing;

import java.util.LinkedList;

import generators.MovesGenerator;
import rappresentazione.Node;
import ricerca.Search;

public class MainSearch2 {
	
	public static void main(String[] args) {
		
		byte[] posToPawn2 = new byte[32];
		for (int i = 0; i < 32; i++) {
			if (i == 1) // white start position
				posToPawn2[i] = (byte) 12;
			else if (i == 30) // black start position
				posToPawn2[i] = (byte) 32;
			else
				posToPawn2[i] = (byte) 0; 
		}
		
		MovesGenerator mg = new MovesGenerator();
		mg.init();

		int bc = mg.createConfig(posToPawn2, false);
		int wc = mg.createConfig(posToPawn2, true);
		
		boolean isWhite = true;
		int livelloMax = 6;
		long tstart,tend;
		double sum;
		
		Node root = new Node(null, bc, wc, posToPawn2,"","","0");
		Search2 s = new Search2();
		s.init();
		tstart = System.currentTimeMillis();
		Node best = s.recursiveSearch(root, isWhite, mg.getCellToPos(),livelloMax);
		tend = System.currentTimeMillis();
		sum = (tend - tstart)/1000.0;
		
		System.out.println("Search "+livelloMax+" livelli: "+sum);
		System.out.println("Best move: "+best.getMossa()+", "+best.getValue());
		
		//System.out.println(root.generateGenericVerbose("", false, false, new StringBuilder()));
		
		LinkedList<Node> leaves = null;
		tstart = System.currentTimeMillis();
		leaves = mg.getLeaves(best);
		for(Node leaf: leaves)
			mg.generateMoves(leaf, isWhite);
		tend = System.currentTimeMillis();
		sum = (tend - tstart)/1000.0;
		System.out.println("Tempo generazione "+(livelloMax+1)+"° livello: "+sum);
		System.out.println("Numero foglie: "+leaves.size());
		
		
		
	}

}
