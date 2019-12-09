package testing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import generators.MovesGenerator;
import rappresentazione.Node;

public class BoardMap {

	private final Color BROWN = new Color(102, 51, 0);
	private final char[] COLORE_SCACCHIERA = { 'W', 'B', 'W', 'B', 'W', 'B', 'W', 'B', 'B', 'W', 'B', 'W', 'B', 'W',
			'B', 'W', 'W', 'B', 'W', 'B', 'W', 'B', 'W', 'B', 'B', 'W', 'B', 'W', 'B', 'W', 'B', 'W', 'W', 'B', 'W',
			'B', 'W', 'B', 'W', 'B', 'B', 'W', 'B', 'W', 'B', 'W', 'B', 'W', 'W', 'B', 'W', 'B', 'W', 'B', 'W', 'B',
			'B', 'W', 'B', 'W', 'B', 'W', 'B', 'W', };
	private final char[] LETTERE = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H' };

	private static LinkedList<BoardMap> opened = new LinkedList<BoardMap>();
	private static Node root;
	private static MovesGenerator mg;

	private Node n;
	private JFrame f;
	private JMenu mosse;

	public Node getNode() {
		return n;
	}

	public JFrame getFrame() {
		return f;
	}

	private void endGame(Node n) {
		JFrame frame = new JFrame("GAME OVER");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setSize(300, 400);
		frame.setLocation(300, 100);

		JLabel jlbWins = new JLabel((n.getBc() == 0 ? "WHITE" : "BLACK") + " WINS!");
		jlbWins.setForeground(Color.RED);
		frame.getContentPane().add(jlbWins, BorderLayout.CENTER);

		JButton btnRestart = new JButton("RESTART");
		btnRestart.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e)) {
					BoardMap starter = new BoardMap();
					for (int i = 0; i < opened.size(); i++) {
						opened.get(i).getFrame().dispose();
						opened.remove(i);
					}
					frame.dispose();
					starter.createFrameBoard(root, true);
				}
			}
		});
		frame.getContentPane().add(btnRestart, BorderLayout.SOUTH);
		frame.setVisible(true);
		return;
	}

	public void createFrameBoard(Node n, boolean isWhite) {

		if (n.getBc() == 0 || n.getWc() == 0) {
			endGame(n);
			return;
		}

		mg.generateMoves(n, isWhite);
		
//		System.out.println(Arrays.toString(n.getPosToPawns()));
		if(n.getParent()!=null)
			System.out.println("paren --> wc: "+Integer.toBinaryString(n.getParent().getWc())+" bc: "+Integer.toBinaryString(n.getParent().getBc()));
		System.out.println("child --> wc: "+Integer.toBinaryString(n.getWc())+" bc: "+Integer.toBinaryString(n.getBc()));
		System.out.println();

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
		byte[] posToPawn = n.getPosToPawns().clone();

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
			b.setLayout(new GridBagLayout());
			b.setBorder(new LineBorder(Color.BLACK, 1));
			if (COLORE_SCACCHIERA[i] == 'B') {
				int numPawns = posToPawn[pos];
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

		JPanel panNext = new JPanel();
		JButton next = new JButton("NEXT");
		JButton back = new JButton("BACK");

		JMenuBar jmb = new JMenuBar();
		mosse = new JMenu(n.getSons().get(0).getMossa());
		for (int i = 0; i < n.getSons().size(); i++) {
			JMenuItem jmi = new JMenuItem(n.getSons().get(i).getMossa());
			jmi.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					mosse.setText(jmi.getText());
				}
			});
			mosse.add(jmi);

		}
		jmb.add(mosse);

		next.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e)) {
					for (Node son : n.getSons()) {
						if (son.getMossa().equals(mosse.getText())) {
							BoardMap bSon = new BoardMap();
							bSon.createFrameBoard(son, !isWhite);
						}
					}
				}
			}
		});

		back.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e)) {
					if (n.getId() != root.getId()) {
						getFrame().dispose();
					}
				}
			}

		});

		panNext.add(back);
		panNext.add(next);
		panNext.add(jmb);
		frame.getContentPane().add(panNext, BorderLayout.SOUTH);

		JPanel panColonne = new JPanel();
		panColonne.setLayout(new FlowLayout(3));
		for (int i = 1; i <= 8; i++) {
			JPanel pan = new JPanel();
			pan.setLayout(new GridBagLayout());
			JLabel l = new JLabel("" + i);
			l.setForeground(Color.RED);
			pan.add(l);
			panColonne.add(Box.createHorizontalStrut(16));
			panColonne.add(pan);
		}

		JPanel panRighe = new JPanel();
		panRighe.setLayout(new GridLayout(8, 1));
		for (int i = 0; i < 8; i++) {
			JPanel pan = new JPanel();
			JLabel l = new JLabel("" + LETTERE[i]);
			l.setForeground(Color.RED);
			pan.add(l);
			panRighe.add(pan);
		}

		frame.getContentPane().add(panColonne, BorderLayout.NORTH);
		frame.getContentPane().add(panRighe, BorderLayout.WEST);
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		frame.getContentPane().add(new JPanel(), BorderLayout.EAST);
		frame.setVisible(true);

		this.f = frame;
		opened.add(this);
	}

	public static void main(String[] args) throws Exception {

		/*
		 ********************************************************************************************************************************* 
		 * SETTING UP PARAMETRI INIZIALI
		 *********************************************************************************************************************************
		 */

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

		/*
		 ********************************************************************************************************************************* 
		 * GENERAZIONE ALBERO MOSSE
		 *********************************************************************************************************************************
		 */

		boolean isWhite = true;
		int livelloMax = 3;
		root = new Node(null, bc, wc, posToPawn, "","","0");

//		generateMovesRecursive(mg, root, isWhite, 0, livelloMax);
//		generateMovesIterative(mg, root, isWhite, livelloMax);

//		System.out.println("Created tree with depth " + livelloMax);
//		System.out.println(Node.generateGenericVerbose(root, "", false, false, new StringBuilder()));

		BoardMap bm = new BoardMap();
		bm.createFrameBoard(root, true);
	}

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
