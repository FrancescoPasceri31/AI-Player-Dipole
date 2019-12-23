package ricerca;

import java.util.HashMap;
import java.util.LinkedList;

import generators.MovesGenerator;
import rappresentazione.Node;

public class MainSearch {
	
	
	public static void main(String[] args) throws Exception {

		/*
		 ********************************************************************************************************************************* 
		 * SETTING UP PARAMETRI INIZIALI
		 *********************************************************************************************************************************
		 */

		HashMap<Byte, Byte> posToPawn = new HashMap<Byte, Byte>();
		for (int i = 0; i < 32; i++) {
			if (i == 1) // white start position
				posToPawn.put((byte) i, (byte) 12);
			else if (i == 30) // black start position
				posToPawn.put((byte) i, (byte) 32);
			else
				posToPawn.put((byte) i, (byte) 0);
		}

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

		/*
		 ********************************************************************************************************************************* 
		 * GENERAZIONE ALBERO MOSSE
		 *********************************************************************************************************************************
		 */
		boolean isWhite = true;
		int livelloMax = 5;
//		int livelloMax =Integer.parseInt(args[0]);
		
		long tstart,tend;
		Node root = null;
//		int iterations = Integer.parseInt(args[1]);
		int iterations = 1;
		double sum = 0;
		for(int i =0;i<iterations; i++) {
			System.out.println("iterazione: "+(i+1));
			tstart = System.currentTimeMillis();
			root = new Node(null, bc, wc, posToPawn2,"","","0");
			mg.generateMovesRecursive(root, isWhite, 0, livelloMax);
			tend = System.currentTimeMillis();
			sum += (tend - tstart)/1000.0;
		}
		
		System.out.println("tempo generazione "+livelloMax+" livelli -> "+ (sum/iterations));
		Search s = new Search();
		s.init();
		root = s.recursiveSearch(root, isWhite, mg.getCellToPos());
		LinkedList<Node> leaves = null;
		tstart = System.currentTimeMillis();
		leaves = mg.getLeaves(root);
		for(Node leaf: leaves)
			mg.generateMoves(leaf, isWhite);
		tend = System.currentTimeMillis();
		sum = (tend - tstart)/1000.0;
		System.out.println("Tempo generazione "+(livelloMax+1)+"° livello: "+sum);
		System.out.println("Best move: "+root.getMossa()+", "+root.getValue());
		System.out.println("Numero foglie: "+leaves.size());
		/*
		 ********************************************************************************************************************************* 
		 * RICERCA MINIMAX CON PRUNING ALPHA-BETA
		 *********************************************************************************************************************************
		 */
		
//		for(Node f: root.getSons()) {
//			if(f.getMossa().equals("H5,NE,1")) {
//				root = f;
//				break;
//			}
//		}
//		
//
//		for(Node f: root.getSons()) {
//			if(f.getMossa().equals("A4,S,6")) {
//				root = f;
//				break;
//			}
//		}
//		
//		for(Node f: root.getSons()) {
//			if(f.getMossa().equals("H5,NE,1")) {
//				root = f;
//				break;
//			}
//		}
//		
//		for(Node f: root.getSons()) {
//			if(f.getMossa().equals("G4,SW,1")) {
//				root = f;
//				break;
//			}
//		}
//		
//		System.out.println(root.getSons());
		

//		Search s = new Search();
//
////		tstart = System.currentTimeMillis();
////		Node ret = s.search(root);
////		tend = System.currentTimeMillis();
////		
////		System.out.println("tempo search iterativa "+livelloMax+" livelli -> "+ ((tend - tstart)/1000.0)+", res: "+ret.getValue());
//
//		tstart = System.currentTimeMillis();
//		Node ret1 = s.recursiveSearch(root,isWhite,mg.getCellToPos());
//		tend = System.currentTimeMillis();
////		
////		System.out.println(ret1);
////		ret1 = s.minVal(root.getSons().get(ret1.getId()-1), Double.MIN_VALUE, Double.MAX_VALUE);
////		System.out.println(ret1);
//
//		
//		
//		
//		System.out.println("tempo search ricorsiva " + livelloMax + " livelli -> " + ((tend - tstart) / 1000.0)+ "\nres: " + ret1);
		
//		System.out.println(Node.generateGenericVerbose(root.getSons().get(ret1.getId()-1), "", false, false, new StringBuilder()));
//		System.out.println(Node.generateGenericVerbose(root, "", false, false, new StringBuilder()));
		
		/*
		 *********************************************************************************************************************************
		 * RISULTATI GENERAZIONE E RICERCA
		 *********************************************************************************************************************************
		 */
//		System.out.println(Node.generateGenericVerbose(root, "", false, false, new StringBuilder()));

//		File f = new File("tree");
//		ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(f));
//		f.createNewFile();
//		tstart = System.currentTimeMillis();
//		o.writeObject(root);
//		tend = System.currentTimeMillis();
//		o.close();
////		
//		System.out.println("Scrittura effettuata in "+((tend-tstart)/1000.0)+" secondi, dimensione "+((double)f.length() / (1024 * 1024) + " mb"));
//		
//		File f1 = new File("posToPawn");
//		File f2 = new File("posToPawn2");
//		f1.createNewFile();
//		f2.createNewFile();
//		ObjectOutputStream o1 = new ObjectOutputStream(new FileOutputStream(f1));
//		ObjectOutputStream o2 = new ObjectOutputStream(new FileOutputStream(f2));
//		o1.writeObject(posToPawn);
//		o2.writeObject(posToPawn2);
//		o1.close();
//		o2.close();
////		
//		System.out.println("hash: "+((double)f1.length() / (1024 * 1024) + " mb"));
//		System.out.println("array: "+((double)f2.length() / (1024 * 1024) + " mb"));

	}
}
