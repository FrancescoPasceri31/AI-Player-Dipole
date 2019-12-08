package threeTreads;

import java.util.LinkedList;

import generators.MovesGenerator;
import rappresentazione.Node;

public class MemoryThread extends Thread {

	private DecisionThread player;
	public volatile String opponentMove, myMove;
	private Node root;
	private boolean isInizialized;
	public volatile boolean search;
	private static LinkedList<Node> toBeDeleted = new LinkedList<Node>();
	
	public MemoryThread() {
		opponentMove = null;
		isInizialized = false;
		search = false;
	}

	public void setPlayer(DecisionThread player) {
		this.player = player;
	}

	@Override
	public void run() {
		MovesGenerator mg = new MovesGenerator();
		try {
			mg.init();
			if (!isInizialized) { // warm-up time
				byte[] posToPawn = new byte[32];
				for (int i = 0; i < 32; i++) {
					if (i == 1) // white start position
						posToPawn[i] = (byte) 12;
					else if (i == 30) // black start position
						posToPawn[i] = (byte) 32;
					else
						posToPawn[i] = (byte) 0;
				}

				int bc = MovesGenerator.createConfig(posToPawn, false);
				int wc = MovesGenerator.createConfig(posToPawn, true);

				root = new Node(null, bc, wc, posToPawn, ",0");
				if (player.isWhite) {
					MovesGenerator.generateMovesRecursive(mg, root, player.isWhite, 0, 3);
				} else {
					MovesGenerator.generateMovesRecursive(mg, root, player.isWhite, 0, 4);
				}
				player.root = root;
				isInizialized = true;
			}

			// fuori dal warm-up time
			while (true) {
				if (player.isWhite) {
					// se sono bianco l'alternanza è your_turn, opponent_turn
					controllaMossaMia(mg);
					controllaMossaAvversaria(mg);
				} else {
					// se son nero l'alternanza è opponent_turn, your_turn
					controllaMossaAvversaria(mg);
					controllaMossaMia(mg);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void controllaMossaMia(MovesGenerator mg) {
		if ((myMove != null)) {
			for (int i = 0; i < root.getSons().size(); i++) {
				if (root.getSons().get(i).getMossa().equals(myMove)) {
					root.getSons().get(i).setParent(null);
					Node newRoot = root.getSons().remove(i);
					newRoot.setParent(null);
					// se espando di numero dispari allora è il colore del giocatore altrimenti devo
					// fare l'opposto (not)
					MovesGenerator.generateMovesRecursive(mg, newRoot, player.isWhite, 0, 3);
					player.root = newRoot;
					System.out.println("Expanded after myMove");
					// scendo in depth first ed elimino tutti i nodi foglia
					toBeDeleted.add(root);
					while (!toBeDeleted.isEmpty()) {
						if (toBeDeleted.getFirst().getSons().size() <= 0) { // sono su una foglia
							toBeDeleted.getFirst().setParent(null);
							toBeDeleted.removeFirst();
						} else {
							toBeDeleted.addAll(0, toBeDeleted.getFirst().getSons());
						}
					}
					break;
				}
			}
		}
	}

	private void controllaMossaAvversaria(MovesGenerator mg) {
		if ((opponentMove != null)) {
			for (int i = 0; i < root.getSons().size(); i++) {
				if (root.getSons().get(i).getMossa().equals(opponentMove)) {
					root.getSons().get(i).setParent(null);
					Node newRoot = root.getSons().remove(i);
					newRoot.setParent(null);
					// se espando di numero dispari allora è il colore del giocatore altrimenti devo
					// fare l'opposto (not)
					MovesGenerator.generateMovesRecursive(mg, newRoot, player.isWhite, 0, 3);
					player.root = newRoot;
					System.out.println("Expanded after opponentMove");
					// scendo in depth first ed elimino tutti i nodi foglia
					toBeDeleted.add(root);
					while (!toBeDeleted.isEmpty()) {
						if (toBeDeleted.getFirst().getSons().size() <= 0) { // sono su una foglia
							toBeDeleted.getFirst().setParent(null);
							toBeDeleted.removeFirst();
						} else {
							toBeDeleted.addAll(0, toBeDeleted.getFirst().getSons());
						}
					}
					break;
				}
			}
			opponentMove = null;
		}
	}
}
