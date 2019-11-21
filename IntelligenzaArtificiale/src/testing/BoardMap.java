package testing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import rappresentazione.Node;

public class BoardMap {

	private final Color BROWN = new Color(102, 51, 0);
	private final char[] COLORE_SCACCHIERA = { 'W', 'B', 'W', 'B', 'W', 'B', 'W', 'B', 'B', 'W', 'B', 'W', 'B', 'W',
			'B', 'W', 'W', 'B', 'W', 'B', 'W', 'B', 'W', 'B', 'B', 'W', 'B', 'W', 'B', 'W', 'B', 'W', 'W', 'B', 'W',
			'B', 'W', 'B', 'W', 'B', 'B', 'W', 'B', 'W', 'B', 'W', 'B', 'W', 'W', 'B', 'W', 'B', 'W', 'B', 'W', 'B',
			'B', 'W', 'B', 'W', 'B', 'W', 'B', 'W', };

	private static LinkedList<BoardMap> opened = new LinkedList<BoardMap>();
	private static Node root;

	private boolean[] openedSonsFrames;
	private int nextToOpen;
	private Node n;
	private JFrame f;

	public Node getNode() {
		return n;
	}

	public JFrame getFrame() {
		return f;
	}

	public boolean[] getOpenedSonsFrames() {
		return openedSonsFrames;
	}

	public void setOpenedSonsFrames(boolean[] openedSonsFrames) {
		this.openedSonsFrames = openedSonsFrames;
	}

	public int getNextToOpen() {
		return nextToOpen;
	}

	public void setNextToOpen(int nextToOpen) {
		this.nextToOpen = nextToOpen;
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

		openedSonsFrames = new boolean[n.getSons().size()];
		for (int i = 0; i < openedSonsFrames.length; i++) {
			openedSonsFrames[i] = false;
		}
		this.n = n;
		nextToOpen = 0;
		JFrame frame = new JFrame("Board " + n.getId());
//		int whiteConf = n.getWc();
//		int blackConf = n.getBc();
		HashMap<Byte, Byte> posToPawn = (HashMap<Byte, Byte>) n.getPosToPawns().clone();

		if (n.getId() != root.getId()) {
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		} else {
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
		frame.setResizable(false);
		frame.setSize(300, 400);
		frame.setLocation(300, 100);

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(8, 8));
		for (int i = 0; i < COLORE_SCACCHIERA.length; i++) {
			byte pos = (byte) Math.abs(31 - i / 2);
			JPanel b = new JPanel();
			b.setBorder(new LineBorder(Color.BLACK, 1));
			if (COLORE_SCACCHIERA[i] == 'B') {
				int numPawns = posToPawn.get(pos);
				JLabel l = new JLabel();
				l.setForeground(numPawns > 12 ? Color.GREEN : Color.CYAN);
				numPawns = (numPawns > 12 ? numPawns - 20 : numPawns);
				b.setBackground(BROWN);
				l.setText(numPawns > 0 ? "" + numPawns : "");
				b.add(l);
			} else {
				b.setBackground(Color.WHITE);
			}
			panel.add(b);
		}
		frame.getContentPane().add(panel, BorderLayout.CENTER);

		JPanel panNext = new JPanel();
		JButton next = new JButton("NEXT");
		JButton back = new JButton("BACK");

		next.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e)) {
					nextSon();
					return;
				}
			}
		});

		back.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e)) {
					if (n.getId() != root.getId()) {
						getFrame().dispose();
						for (BoardMap boardMap : opened) {
							if (boardMap.getNode().getId() == n.getParent().getId()) {
								boardMap.setNextToOpen( boardMap.getNextToOpen()-2 );
								if (boardMap.getNextToOpen() >= 0) {
									boardMap.nextSon();
									return;
								}else {
									boardMap.setNextToOpen(0);
								}
							}
						}
					}
				}
			}

		});

		panNext.add(back);
		panNext.add(next);
		frame.getContentPane().add(panNext, BorderLayout.NORTH);

		frame.setVisible(true);

		this.f = frame;
		opened.add(this);
	}

	public void nextSon() {

		if (nextToOpen < n.getSons().size()) {
			BoardMap bson = new BoardMap();
			bson.createFrameBoard(n.getSons().get(nextToOpen));
			nextToOpen += 1;
		} else {

			for (BoardMap boardMap : opened) {
				if (boardMap.getNode().getId() == n.getParent().getId()) {
					boardMap.nextSon();
					break;
				}
			}
		}
	}

	public static void main(String[] args) throws Exception {

		/*
		 ********************************************************************************************************************************* 
		 *********************************************************************************************************************************
		 * SETTING UP PARAMETRI INIZIALI
		 *********************************************************************************************************************************
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

		MovesGenerator mg = new MovesGenerator();
		mg.init();

		int bc = mg.createConfig(posToPawn, false);
		int wc = mg.createConfig(posToPawn, true);

		/*
		 ********************************************************************************************************************************* 
		 *********************************************************************************************************************************
		 * GENERAZIONE ALBERO MOSSE
		 *********************************************************************************************************************************
		 *********************************************************************************************************************************
		 */

		boolean isWhite = false;
		int livelloMax = 4;
		root = new Node(null, bc, wc, posToPawn, "");

//		generateMovesRecursive(mg, root, isWhite, 0, livelloMax);
		generateMovesIterative(mg, root, isWhite, livelloMax);

		BoardMap bm = new BoardMap();
		bm.createFrameBoard(root);

	}

	private void avanzaNodo(Node root) {
		/* CREO LE VARIE SCACCHIERE CON DEEP FIRST */
		Scanner sc = new Scanner(System.in);
		LinkedList<Node> ll = new LinkedList<Node>();
		ll.add(root);
		while (ll.size() > 0) {
			Node r = ll.removeFirst();

			System.out.println(r.getMossa());

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

	private static void generateMovesRecursive(MovesGenerator mg, Node n, boolean isWhite, int liv, int limite) {
		if (liv == limite || n == null) {
			return;
		}
		mg.generateMoves(n, isWhite);
		for (Node son : n.getSons()) {
			generateMovesRecursive(mg, son, !isWhite, liv + 1, limite);
		}
	}

	private static void generateMovesIterative(MovesGenerator mg, Node root, boolean isWhite, int limite) {
		HashMap<Integer, Object[]> nodeLev = new HashMap<Integer, Object[]>();
		LinkedList<Node> ll = new LinkedList<Node>();
		nodeLev.put(root.getId(), new Object[] { 0, isWhite });

		ll.add(root);
		while (!ll.isEmpty()) {
			Node n = ll.removeFirst();
			if (((int) nodeLev.get(n.getId())[0]) < limite) {
				boolean myColor = (boolean) nodeLev.get(n.getId())[1];
				mg.generateMoves(n, myColor);
				ll.addAll(0, n.getSons());
				for (Node node : n.getSons()) {
					nodeLev.put(node.getId(),
							new Object[] { 1 + ((int) nodeLev.get(node.getParent().getId())[0]), !myColor });
				}
			}
		}
	}

}
