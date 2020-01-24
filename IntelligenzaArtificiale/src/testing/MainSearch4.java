package testing;

import java.util.LinkedList;

import generators.MovesGenerator;
import rappresentazione.Node;

public class MainSearch4 { 

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
		int livelloMax = 8;
		long tstart, tend;
		double sum;

		Node root = new Node(null, bc, wc, posToPawn2, "", "", "0");
//		tstart = System.currentTimeMillis();
//		mg.generateMovesRecursive(root, isWhite,isWhite, 0, livelloMax);
//		tend = System.currentTimeMillis();
//		sum = (tend - tstart) / 1000.0;
//		
//		System.out.println("Generazione: "+sum);
		
//		 System.out.println(root.generateGenericVerbose("", false, false, new StringBuilder()));

		Search4 s = new Search4();
		s.init();
		Node best = null;
		for (int i = 0; i < 10; i++) { 
			tstart = System.currentTimeMillis();
			best = s.recursiveSearch(root, isWhite, livelloMax);
			tend = System.currentTimeMillis();
			sum = (tend - tstart) / 1000.0;
			
			System.out.println((i+1)+" Search " + livelloMax + " livelli: " + sum);
			System.out.println("Best move: " + best.getMossa() + ", " + best.getValue());
		
//			root=best;
			root.setParent(null);
			System.gc();
			isWhite=!isWhite;
			
			
			LinkedList<Node> l = mg.getLeaves(root);
			System.out.println("Leaves: "+l.size());
			System.out.println();
//			tstart = System.currentTimeMillis();
//			for(Node n: l) mg.generateMoves(n, isWhite);
//			tend = System.currentTimeMillis();
//			sum = (tend - tstart) / 1000.0;
//			System.out.println("Tempo: "+sum);
//			System.out.println();
		}
		
//		 System.out.println(root.generateGenericVerbose("", false, false, new StringBuilder()));
//
//		System.out.println(root.getSons());

		// System.out.println(root.generateGenericVerbose("", false, false, new
		// StringBuilder()));

//		LinkedList<Node> leaves = null;
//		tstart = System.currentTimeMillis();
//		leaves = mg.getLeaves(best);
//		for(Node leaf: leaves)
//			mg.generateMoves(leaf, isWhite);
//		tend = System.currentTimeMillis();
//		sum = (tend - tstart)/1000.0;
//		System.out.println("Tempo generazione "+(livelloMax+1)+"° livello: "+sum);
//		System.out.println("Numero foglie: "+leaves.size());

	}

	
	

}
