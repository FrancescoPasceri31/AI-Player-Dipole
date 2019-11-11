package testing;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

public class Scacchiera {

	private HashMap<Byte, String> posToCell = null;
	private HashMap<String, Byte> cellToPos = null;
	private HashMap<Byte, Object[]> masksBlack = null;
	private HashMap<Byte, Object[]> masksWhite = null;
	private HashMap<Byte, Byte> posToPawn = new HashMap(); /* 1-> 12 white, 21->32 black */

	private static boolean selected = false;
	private static char[] maskTmp;
	private static byte from = -1, to = -1;
	private static int nPawnsSpostare = -1;
	private static boolean isWhiteMove;

	private final String ALFABETO = "ABCDEFGH";
	private final char[] COLORE_SCACCHIERA = { 'W', 'B', 'W', 'B', 'W', 'B', 'W', 'B', 'B', 'W', 'B', 'W', 'B', 'W',
			'B', 'W', 'W', 'B', 'W', 'B', 'W', 'B', 'W', 'B', 'B', 'W', 'B', 'W', 'B', 'W', 'B', 'W', 'W', 'B', 'W',
			'B', 'W', 'B', 'W', 'B', 'B', 'W', 'B', 'W', 'B', 'W', 'B', 'W', 'W', 'B', 'W', 'B', 'W', 'B', 'W', 'B',
			'B', 'W', 'B', 'W', 'B', 'W', 'B', 'W', };
	private Cella[][] matrix = new Cella[8][8];

	public Scacchiera() throws Exception, Exception {
		int pos = 0;
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				matrix[i][j] = new Cella();
				matrix[i][j].colore = String.valueOf(COLORE_SCACCHIERA[pos]);
				pos++;
			}
		}

		ObjectInputStream i = new ObjectInputStream(new FileInputStream("hashMaps"));
		posToCell = (HashMap<Byte, String>) i.readObject();
		cellToPos = (HashMap<String, Byte>) i.readObject();
		masksBlack = (HashMap<Byte, Object[]>) i.readObject();
		masksWhite = (HashMap<Byte, Object[]>) i.readObject();
		i.close();

		for (int k = 0; k < 32; k++) {
			if (k == 1) // white start position
				posToPawn.put((byte) k, (byte) 12);
			else if (k == 30) // black start position
				posToPawn.put((byte) k, (byte) 32);
			else
				posToPawn.put((byte) k, (byte) 0);
		}

	} // costruttore Scacchiera()

	public Cella[][] getMatrix() {
		return matrix;
	}// getMatrix

	public int getRowNumber() {
		return matrix.length;
	} // getRowNumber

	public int getColumnNumber() {
		return matrix[0].length;
	} // getColumnNumber

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(1000);
		sb.append("Scacchiera:\n");
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				sb.append(matrix[i][j].colore + " ");
			}
			sb.append("\n");
		}

		return sb.toString();
	} // toString

	public Cella getCell(int k, int l) {
		return matrix[k][l];
	} // getCell

	public void show() {
		javax.swing.ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
		JFrame f = new JFrame("Scacchiera");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setResizable(false);
		f.setSize(600, 600);
		f.setLocation(300, 100);
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(getRowNumber(), getColumnNumber()));
		int pos = 31;
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				int miaCella = pos;
				int pawns = 0;
				if (miaCella >= 0 && miaCella <= 31) {
					pawns = posToPawn.get((byte) miaCella);
				}
				System.out.println("cella " + (i * 8 + j) + " - In pos: " + miaCella + " ho " + pawns + " pedine");
				JPanel b = new JPanel();
				String cellPosition = "(" + ALFABETO.charAt(i) + "," + (j + 1) + ")";
				b.setToolTipText(cellPosition);
				if ((matrix[i][j].colore).equals("B")) {
					b.setBackground(Color.BLACK);

					String AV_bmask = "", IN_bmask = "", AV_wmask = "", IN_wmask = "";

					if (pawns > 0) {
						if (pawns > 12) {
							System.out.println("MASK_black");
							AV_bmask = Integer
									.toBinaryString(HashMapGenerator2.getMask(masksBlack, miaCella, pawns, 0));
							IN_bmask = Integer
									.toBinaryString(HashMapGenerator2.getMask(masksBlack, miaCella, pawns, 1));
						} else {
							System.out.println("MASK_white");
							AV_wmask = Integer
									.toBinaryString(HashMapGenerator2.getMask(masksWhite, miaCella, pawns, 0));
							IN_wmask = Integer
									.toBinaryString(HashMapGenerator2.getMask(masksWhite, miaCella, pawns, 1));
						}

						String textPos = "Posizione = " + cellPosition + " -> " + miaCella;
						String textBmask = "BlackMask = A:[ " + AV_bmask + " ] - D:[ " + IN_bmask + " ]";
						String textWmask = "WhiteMask = A:[ " + AV_wmask + " ] - D:[ " + IN_wmask + " ]";
						String toolTipText = "<html>" + textPos + "<br>" + textBmask + "<br>" + textWmask + "</html>";
						b.setToolTipText(toolTipText);

						pawns = pawns <= 12 ? pawns : pawns - 20;
						JButton but = new JButton("" + pawns);
						but.setBorder(new LineBorder(Color.WHITE, 1, true));
						b.add(but);
					} else {
						String textPos = "Posizione = " + cellPosition + " -> " + miaCella;
						String toolTipText = "<html>" + textPos + "</html>";
						b.setToolTipText(toolTipText);

						JButton but = new JButton();
						b.add(but);
					}

					pos--;
				} else {
					b.setBackground(Color.WHITE);
				}

				b.addMouseListener(new MouseAdapter() {

					/**
					 * Il metodo gestisce lo spostamento delle pedine
					 */
					@Override
					public void mousePressed(MouseEvent e) {

						/* GESTISCO MOSSA, RESETTO COLORI SCACCHIERA E VISUALIZZO MOSSE */
						if (SwingUtilities.isLeftMouseButton(e)) {
							// riporto in B&W tutte le celle
							Component[] jbuttons = panel.getComponents();
							for (int k = 0; k < jbuttons.length; k++) {
								if (COLORE_SCACCHIERA[k] == 'B') {
									((JPanel) jbuttons[k]).setBackground(Color.BLACK);
								} else {
									((JPanel) jbuttons[k]).setBackground(Color.WHITE);
								}
								((JPanel) jbuttons[k]).setBorder(new LineBorder(Color.WHITE, 0));
							}

							if (b.getBackground().equals(Color.BLACK)) {
								if (!selected) {
									primoClick();
									selected = true;
								} else if (selected) {
									secondoClick();
									selected = false;
								}
							} else {
								selected = false;
							}

						}
						/* TOLGO FUORI */
						if (SwingUtilities.isRightMouseButton(e)) {
							int nPawns = posToPawn.get((byte) miaCella);
							byte[] direzioni = HashMapGenerator2
									.getOutLeastPawns(nPawns <= 12 ? masksWhite : masksBlack, (byte) miaCella);
							String[] coord = { "NW", "NE" };

							String choicedDir = (String) JOptionPane.showInputDialog(null,
									"Scegliere direzione per togliere fuori pedine?", "", JOptionPane.QUESTION_MESSAGE,
									null, coord, coord[0]);
							int choosen = choicedDir.equals("NW") ? direzioni[0] : direzioni[1];
							int choice = Integer
									.valueOf(JOptionPane.showInputDialog("Inserire numero di pedine da togliere"));
							while (choice < choosen || choice > nPawns) {
								choice = Integer
										.valueOf(JOptionPane.showInputDialog("Inserire numero di pedine da togliere"));
							}

							if (choice > 0) {
								posToPawn.put((byte) miaCella, (byte) (posToPawn.get((byte) miaCella) - choice));
								((JButton) b.getComponent(0)).setText("" + posToPawn.get((byte) miaCella));
							}
						}
					}

					private void primoClick() {
						Component[] jbuttons = panel.getComponents();
						int nPawns = posToPawn.get((byte) miaCella);
						if (nPawns > 0) {
							from = (byte) miaCella;
							isWhiteMove = nPawns <= 12;

							// coloro di rosso le celle della maschera solo se clicco su casella nera
							String enemy = "";
							for (Byte position : posToPawn.keySet()) {
								if (posToPawn.get(position) > 12) {
									enemy += "1";
								} else {
									enemy += "0";
								}
							}
							int en = Integer.parseUnsignedInt(enemy, 2);
							int AV_mask = HashMapGenerator2.getMask((nPawns > 12 ? masksBlack : masksWhite), miaCella,
									nPawns, 0);
							int IN_mask = HashMapGenerator2.getMask((nPawns > 12 ? masksBlack : masksWhite), miaCella,
									nPawns, 1);
							String maskTmpS = Integer.toBinaryString(AV_mask & (IN_mask | (~en)));

							maskTmp = maskTmpS.toCharArray();
							for (int c = 0; c < jbuttons.length; c++) {
								if (((JPanel) panel.getComponent(c)).getBackground().equals(Color.BLACK)) {
									if (maskTmp[c / 2] == '0') {
										b.setBackground(Color.RED);
										b.setBorder(new LineBorder(Color.BLACK, 15));
										((JPanel) jbuttons[c]).setBorder(new LineBorder(Color.BLACK, 15));
										((JPanel) jbuttons[c]).setBackground(Color.YELLOW);
									}
								}
							}
						}
					}

					private void secondoClick() {
						to = (byte) miaCella;

//						for (int k = 0; k < maskTmp.length; k++) {
//							if (k == 31 - miaCella) {
//								System.out.print("{" + maskTmp[k] + "}");
//							} else {
//								System.out.print("" + maskTmp[k]);
//							}
//						}
//						System.out.println();
//						System.out.println(maskTmp[maskTmp.length - 1 - miaCella]);

						boolean condizioneSoddisfatta = maskTmp[maskTmp.length - 1 - miaCella] == '0';

						if (from != -1 && !(from == to) && condizioneSoddisfatta) {
							int nPawns = posToPawn.get(to);
							boolean merge = nPawns == 0 || !(isWhiteMove ^ (nPawns > 0 && nPawns < 12));
							nPawnsSpostare = Math.abs(from / 4 - to / 4);
							if (!merge) { // sto attaccando
								nPawns -= 20;
								if (nPawnsSpostare >= nPawns && posToPawn.get(from) >= nPawnsSpostare) {
									posToPawn.put(from, (byte) (posToPawn.get(from) - nPawnsSpostare));
									posToPawn.put(to, (byte) (isWhiteMove ? nPawnsSpostare : nPawnsSpostare + 20));
								}
							} else {
								posToPawn.put(from, (byte) (posToPawn.get(from) - nPawnsSpostare));
								posToPawn.put(to, (byte) (nPawns + nPawnsSpostare));
							}

							Component[] jpanels = panel.getComponents();
							for (int k = 0; k < jpanels.length; k++) {
								if (COLORE_SCACCHIERA[k] == 'B') {
									int posScacchiera = Math.abs(31 - (k / 2));
									int nPed = posToPawn.get((byte) posScacchiera);
									byte nPedByte = (byte) (nPed <= 12 ? nPed : nPed - 20);

									String text = nPedByte == 0 ? "" : "" + nPedByte;
									((JButton) ((JPanel) jpanels[k]).getComponent(0)).setText(text);
								}
							}

							System.out.println("MOSSA: " + posToPawn);
						}
						from = -1;
						to = -1;
						nPawnsSpostare = -1;
					}

				});

				panel.add(b);
			}
		}
		f.getContentPane().add(panel);
		f.setVisible(true);
	} // show

} // scacchiera

class Cella {
	public int info;
	public String colore;

	public Cella() {
		info = -1;
		colore = "";
	}

	@Override
	public String toString() {
		return "Cella [" + colore + "]";
	}
}