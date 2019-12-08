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
	private MemoryThread mt;

	public DecisionThread() {
		super();
		myMove = null;
		opponentMove = null;
		mg = new MovesGenerator();
		searcher = new Search();
		System.out.println("DT created @" + this.getId());
	}

	public void setMt(MemoryThread mt) {
		this.mt = mt;
	}

	public void setColor(boolean isWhite) {
		this.isWhite = isWhite;
	}

	@Override
	public void run() {
		mt.start();
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

		int bc = mg.createConfig(posToPawn, false);
		int wc = mg.createConfig(posToPawn, true);

		root = new Node(null, bc, wc, posToPawn, ",0");
		mg.generateMovesRecursive(root, isWhite, START_LEVEL, MAX_FIRST_LEVEL);

		while (true) {
			if (startSearch) {
				best = searcher.recursiveSearch(root, isWhite); // cerco il best per la scelta -> CODIFICARE QUI
																// L'ALGORITMO DI RICERCA PER SETTARE IL BEST TEMPORANEO
				myMove = best.getMossa(); // lo dichiaro allo speaker per farlo comunicare al server
				best.setParent(null); // best diventa root perche scelto
				root.getSons().remove(best);
				mt.addToDelete(root); // elimino il resto dell'albero per liberare memoria
				root = best;
				best = null;
				espandi();
				startSearch=false;	// da togliere e mettere nello speaker e riuscire ad espandere il root best
			}
			if (opponentMove!=null) {
				for (int i = 0; i < root.getSons().size(); i++) { // cerco la scelta dell'avversario tra i miei figli
					if (root.getSons().get(i).getMossa().equals(opponentMove)) { // se lo trovo
						best = root.getSons().get(i);
						root.getSons().remove(i);
						best.setParent(null);
						mt.addToDelete(root);
						root = best;
						best = null;
						opponentMove = null;
						espandi();
						break;
					}
				}
			}
		}
	}

	private void espandi() {
		toExpand = mg.getLeaves(root);
		for (Node n : toExpand) {
			mg.generateMovesRecursive(n, isWhite, START_LEVEL, MAX_EXTENSION);
		}
		toExpand.clear();
	}

}