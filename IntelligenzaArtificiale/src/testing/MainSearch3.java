package testing;

import java.util.LinkedList;

import generators.MovesGenerator;
import rappresentazione.Node;

public class MainSearch3 { 

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
		
	
		Search3 s = new Search3();
		s.init();

		Node best = null;
		for (int i = 0; i < 10; i++) { 
			tstart = System.currentTimeMillis();
			best = s.recursiveSearch(root, isWhite, livelloMax);
			tend = System.currentTimeMillis();
			sum = (tend - tstart) / 1000.0;
			
			System.out.println((i+1)+" Search " + livelloMax + " livelli: " + sum);
			System.out.println("Best move: " + best.getMossa() + ", " + best.getValue());
		
			root=best;
			root.setParent(null);
			isWhite=!isWhite;
			
			
			LinkedList<Node> l = mg.getLeaves(root);
			System.out.println("Leaves: "+l.size());
			System.out.println();
			root.setSons(new LinkedList<Node>());
			System.gc();
			
			int th = l.size();
			
			if (th <= 3262)
				livelloMax = 12;
			else if (th > 3262 && th <= 4893)
				livelloMax = 11;
			else if (th > 4893 && th <= 6525)
				livelloMax = 10;
			else if (th > 6525 && th <= 8700)
				livelloMax = 9;
			else
				livelloMax = 8;

		}

	}
	

}
