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
		int livelloMax = 1;
		long tstart, tend;
		double sum;

		Node root = new Node(null, bc, wc, posToPawn2, "", "", "0");
//		tstart = System.currentTimeMillis();
		mg.generateMovesRecursive(root, isWhite,isWhite, 0, 1);
//		tend = System.currentTimeMillis();
//		sum = (tend - tstart) / 1000.0;
//		
//		System.out.println("Generazione: "+sum);
//		System.out.println(root.getSons());

//		 System.out.println(root.getSons().getFirst().getSons().getFirst().generateGenericVerbose("", false, false, new StringBuilder()));

//		Search4 s = new Search4();
//		s.init();
//		Node best = null;
//		for (int i = 0; i < 1; i++) {
//			tstart = System.currentTimeMillis();
//			root = s.recursiveSearch(root, isWhite, livelloMax);
//			tend = System.currentTimeMillis();
//			sum = (tend - tstart) / 1000.0;
//
//			System.out.println((i + 1) + " Search " + livelloMax + " livelli: " + sum);
//			System.out.println("Best move: " + root.getMossa() + ", " + root.getValue());
//
//			root.setParent(null);
//			System.gc();
//			isWhite = !isWhite;
//
//			LinkedList<Node> leaves = mg.getLeaves(root);
//			System.out.println("Leaves: " + leaves.size()); 
//			System.out.println();
//
//			int th = leaves.size();
//			if (th <= 4750)
//				livelloMax = 12;
//			if (th > 4750 && th <= 6525)
//				livelloMax = 11;
//			if (th > 6525 && th <= 8700)
//				livelloMax = 10;
//			else
//				livelloMax = 8;
////			tstart = System.currentTimeMillis();
////			for(Node n: l) mg.generateMoves(n,< isWhite);
////			tend = System.currentTimeMillis();
////			sum = (tend - tstart) / 1000.0;
////			System.out.println("Tempo: "+sum);
////			System.out.println();
//		}
//
//		System.gc();
		
//		mg.generateMoves(root, !isWhite, isWhite);
//		System.out.println();
//		root = root.getSons().get(5);
//		mg.generateMoves(root, isWhite, isWhite);

//		System.out.println(root.getSons());

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
