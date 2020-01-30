package threeTreads;

import java.util.LinkedList;

import generators.MovesGenerator;
import rappresentazione.Node;
import ricerca.Search;

public class DecisionThread extends Thread {

	private final int MAX_FIRST_LEVEL = 5, START_LEVEL = 0, MAX_EXTENSION = 2;

	public volatile String myMove, opponentMove;
	public volatile String bestMove = "BAAANG";
	public volatile boolean startSearch;
	private boolean isWhite;
	private LinkedList<Node> toExpand;

	private MovesGenerator mg;
	private Search searcher;
	private Node root, best;
	//private MemoryThread mt;

	public DecisionThread() {
		super();
		myMove = null;
		opponentMove = null;
		mg = new MovesGenerator();
		searcher = new Search();
		System.out.println("DT created @" + this.getId());
	}

//	public void setMt(MemoryThread mt) {
//		this.mt = mt;
//	}

	public void setColor(boolean isWhite) {
		this.isWhite = isWhite;
	}

	@Override
	public void run() {
		//mt.start();
		System.out.println("Player " + (isWhite ? "white" : "black") + " started. @" + this.getId());
		byte[] posToPawn = new byte[32];
		for (int i = 0; i < 32; i++) {
			if (i == 1) // white start position
				posToPawn[i] = (byte) 12;
			else if (i == 30) // black start position
				posToPawn[i] = (byte) 32;
			else
				posToPawn[i] = (byte) 0;
		}

		mg = new MovesGenerator();
		mg.init();
		searcher.init();

		int bc = mg.createConfig(posToPawn, false);
		int wc = mg.createConfig(posToPawn, true);

		root = new Node(null, bc, wc, posToPawn,"","", "0");
		mg.generateMovesRecursive(root, true,isWhite, START_LEVEL, MAX_FIRST_LEVEL);

		while (true) {
			if (startSearch) {
				best = searcher.recursiveSearch(root, isWhite); // cerco il best per la scelta -> CODIFICARE QUI
				myMove = best.getMossa(); // lo dichiaro allo speaker per farlo comunicare al server
				best.setParent(null); // best diventa root perche scelto
				root.getSons().remove(best);
				root = best;
				System.gc();
				best = null;
				espandi(isWhite);
				startSearch=false;	// da togliere e mettere nello speaker e riuscire ad espandere il root best
			}
			if (opponentMove!=null) {
				for (int i = 0; i < root.getSons().size(); i++) { // cerco la scelta dell'avversario tra i miei figli
					if (root.getSons().get(i).getMossa().equals(opponentMove)) { // se lo trovo
						best = root.getSons().get(i);
						root.getSons().remove(i);
						best.setParent(null);
						//mt.addToDelete(root);
						root = best;
						System.gc();
						best = null;
						opponentMove = null;
						long t = System.currentTimeMillis();
						espandi(!isWhite);
						System.out.println("generazione opponent: "+(System.currentTimeMillis()-t));
						break;
					}
				}
			}
		}
	}

	private void espandi(boolean w) {
		toExpand = mg.getLeaves(root);
		
		if (toExpand.size() > 1000) {
			long tstart = System.currentTimeMillis();
			for (Node n : toExpand)
				mg.generateMoves(n, w, isWhite);
			long tend = System.currentTimeMillis();
			double sum = (tend - tstart) / 1000.0;
			System.out.println("Tempo: " + sum);
			System.out.println();
		}else {
			long tstart = System.currentTimeMillis();
			for (Node n : toExpand)
				mg.generateMovesRecursive(n, w, isWhite, 0, 4);
			long tend = System.currentTimeMillis();
			double sum = (tend - tstart) / 1000.0;
			System.out.println("Tempo 3: " + sum);
			System.out.println();
		}
//		for (Node n : toExpand) {
//			mg.generateMoves(n, w,isWhite);
//		}
		toExpand.clear();
	}
	
	private int getLevel(Node n) {
		if(n.getSons().size()==0) return 0;
		return 1+getLevel(n.getSons().get(0));
	}
	
	public void setSearch(Search s) {
		this.searcher = s;
		this.searcher.init();
	}

	

}