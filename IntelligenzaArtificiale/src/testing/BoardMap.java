package testing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import rappresentazione.Node;

public class BoardMap {

	private final Color BROWN = new Color(102, 51, 0);
	private final char[] COLORE_SCACCHIERA = { 'W', 'B', 'W', 'B', 'W', 'B', 'W', 'B', 'B', 'W', 'B', 'W', 'B', 'W',
			'B', 'W', 'W', 'B', 'W', 'B', 'W', 'B', 'W', 'B', 'B', 'W', 'B', 'W', 'B', 'W', 'B', 'W', 'W', 'B', 'W',
			'B', 'W', 'B', 'W', 'B', 'B', 'W', 'B', 'W', 'B', 'W', 'B', 'W', 'W', 'B', 'W', 'B', 'W', 'B', 'W', 'B',
			'B', 'W', 'B', 'W', 'B', 'W', 'B', 'W', };

	private static LinkedList<BoardMap> opened = new LinkedList<BoardMap>();

	private Node n;
	private JFrame f;

	public Node getNode() {
		return n;
	}

	public JFrame getFrame() {
		return f;
	}

	public void createFrameBoard(Node n) {
		for (int i = 0; i < opened.size(); i++) {
			boolean isMyAncestor = false;
			Node node = opened.get(i).getNode();
			Node myAncestor = n.getParent();
			while (node != null) {
				while (myAncestor != null) {
					if (myAncestor.getId() == node.getId()) {
						isMyAncestor = true;
					}
					myAncestor = myAncestor.getParent();
				}
				node = node.getParent();
			}
			if (!isMyAncestor) {
				(opened.get(i).getFrame()).dispose();
			}
		}

		this.n = n;
		JFrame frame = new JFrame("Board " + n.getId());
//		int whiteConf = n.getWc();
//		int blackConf = n.getBc();
		HashMap<Byte, Byte> posToPawn = (HashMap<Byte, Byte>) n.getPosToPawns().clone();

		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setResizable(false);
		frame.setSize(300, 500);
		frame.setLocation(300, 100);

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(8, 8));
		for (int i = 0; i < COLORE_SCACCHIERA.length; i++) {
			JPanel b = new JPanel();
			b.setBorder(new LineBorder(Color.BLACK, 1));
			if (COLORE_SCACCHIERA[i] == 'B') {
				int numPawns = posToPawn.get((byte) (i / 2));
				numPawns = (numPawns > 12 ? numPawns - 20 : numPawns);
				b.setBackground(BROWN);
				JButton l = new JButton("" + numPawns);
				b.add(l);
			} else {
				b.setBackground(Color.WHITE);
			}
			panel.add(b);
		}
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		frame.setVisible(true);

		this.f = frame;
		opened.add(this);
	}

	public static void main(String[] args) throws Exception {

		/* CREO LA DISPOSIZIONE DELLE PEDINE */
		HashMap<Byte, Byte> posToPawn = new HashMap<Byte, Byte>();
		for (int i = 0; i < 32; i++) {
			if (i == 1) // white start position
				posToPawn.put((byte) i, (byte) 12);
			else if (i == 30) // black start position
				posToPawn.put((byte) i, (byte) 32);
			else
				posToPawn.put((byte) i, (byte) 0);
		}

		/* GENERO LE MOSSE E L'ALBERO */
		MovesGenerator mg = new MovesGenerator();
		mg.init();
		int bc = mg.createConfig(posToPawn, false);
		int wc = mg.createConfig(posToPawn, true);
		Node root = new Node(null, bc, wc, posToPawn,"");
	
//		generateMovesRecursive(mg, root, false, 0, 2);
		
		mg.generateMoves(root, true);
		for (Node n : root.getSons()) {
			mg.generateMoves(n, false);
		}

		/* CREO LE VARIE SCACCHIERE CON DEEP FIRST */
		Scanner sc = new Scanner(System.in);
		LinkedList<Node> ll = new LinkedList<Node>();
		ll.add(root);
		while (ll.size() > 0) {
			Node r = ll.removeFirst();
			int i = 0;
			for (Node node : r.getSons()) {
				ll.add(i, node);
				i++;
			}
			BoardMap bm = new BoardMap();
			bm.createFrameBoard(r);
			System.out.print(">");
			sc.nextLine();
		}
		sc.close();
	}
	
	/* liv: indica il livello attuale, limite: il livello max di generazione */
	private static void generateMovesRecursive(MovesGenerator mg ,Node n, boolean isWhite, int liv, int limite) {
		if(liv==limite) return;
		mg.generateMoves(n, isWhite);
		for(Node son : n.getSons()) {
			generateMovesRecursive(mg, son, !isWhite, liv++, limite);
		}
	}
}
