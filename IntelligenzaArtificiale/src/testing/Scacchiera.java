package testing;

import java.awt.BorderLayout;
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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import generators.HashMapGenerator;

public class Scacchiera {

	private final Color BROWN = new Color(102, 51, 0);

	private HashMap<Byte, String> posToCell = null;
	private HashMap<String, Byte> cellToPos = null;
	private HashMap<Byte, Object[]> masksBlack = null;
	private HashMap<Byte, Object[]> masksWhite = null;
	private HashMap<Byte, Byte> posToPawn = new HashMap<Byte, Byte>(); /* 1-> 12 white, 21->32 black */

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

	private static Byte[] posToCol = { 7, 5, 3, 1, 8, 6, 4, 2, 7, 5, 3, 1, 8, 6, 4, 2, 7, 5, 3, 1, 8, 6, 4, 2, 7, 5, 3,
			1, 8, 6, 4, 2 };

	public Scacchiera() {
		int pos = 0;
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				matrix[i][j] = new Cella();
				matrix[i][j].colore = String.valueOf(COLORE_SCACCHIERA[pos]);
				pos++;
			}
		}
	}

	public void init() throws Exception, Exception {
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

	} // init di Scacchiera

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
				byte pawns = 0;
				if (miaCella >= 0 && miaCella <= 31) {
					pawns = posToPawn.get((byte) miaCella);
				}
				JPanel b = new JPanel();
				String cellPosition = "(" + ALFABETO.charAt(i) + "," + (j + 1) + ")";
				b.setToolTipText(cellPosition);
				if ((matrix[i][j].colore).equals("B")) {
					b.setBackground(BROWN);

					String AV_bmask = "", IN_bmask = "", AV_wmask = "", IN_wmask = "";

					if (pawns > 0) {
						if (pawns > 12) {
							int[] msk = HashMapGenerator.getMask(masksBlack, miaCella, pawns);
							AV_bmask = Integer.toBinaryString(msk[0]);
							IN_bmask = Integer.toBinaryString(msk[1]);
						} else {
							int[] msk = HashMapGenerator.getMask(masksWhite, miaCella, pawns);
							AV_wmask = Integer.toBinaryString(msk[0]);
							IN_wmask = Integer.toBinaryString(msk[1]);
						}

						String textPos = "Posizione = " + cellPosition + " -> " + miaCella;
						String textBmask = "BlackMask = A:[ " + AV_bmask + " ] - D:[ " + IN_bmask + " ]";
						String textWmask = "WhiteMask = A:[ " + AV_wmask + " ] - D:[ " + IN_wmask + " ]";
						String toolTipText = "<html>" + textPos + "<br>" + textBmask + "<br>" + textWmask + "</html>";
						b.setToolTipText(toolTipText);

						JButton but = new JButton();
						but.setBackground(pawns == 0 ? null : pawns <= 12 ? Color.cyan : Color.green);
						pawns = (byte) (pawns <= 12 ? pawns : pawns - 20);
						but.setText("" + pawns);
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

					private boolean whiteFinished, blackFinished;

					/**
					 * Il metodo gestisce lo spostamento delle pedine
					 */
					@Override
					public void mousePressed(MouseEvent e) {

						boolean finished = controllaVittoria();

						if (!finished) {
							/* GESTISCO MOSSA, RESETTO COLORI SCACCHIERA E VISUALIZZO MOSSE */
							if (SwingUtilities.isLeftMouseButton(e)) {
								// riporto in B&W tutte le celle
								Component[] jbuttons = panel.getComponents();
								for (int k = 0; k < jbuttons.length; k++) {
									if (COLORE_SCACCHIERA[k] == 'B') {
										((JPanel) jbuttons[k]).setBackground(BROWN);
									} else {
										((JPanel) jbuttons[k]).setBackground(Color.WHITE);
									}
									((JPanel) jbuttons[k]).setBorder(new LineBorder(Color.WHITE, 0));
								}

								if (b.getBackground().equals(BROWN)) {
									if (!selected) {
										primoClick();
										// selected = true;
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
								byte[] direzioni = HashMapGenerator
										.getOutLeastPawns(nPawns <= 12 ? masksWhite : masksBlack, (byte) miaCella);
								String[] coord = null;

								if (nPawns <= 12) {
									String[] coord2 = { "NW", "NE" };
									coord = coord2;
								} else {
									String[] coord2 = { "SW", "SE" };
									coord = coord2;
								}

								String choicedDir = (String) JOptionPane.showInputDialog(null,
										"Scegliere direzione per togliere fuori pedine?", "",
										JOptionPane.QUESTION_MESSAGE, null, coord, coord[0]);
								if (choicedDir != null) {
									int choosen = choicedDir.equals("NW") || choicedDir.equals("SE") ? direzioni[0]
											: direzioni[1];
									String nPedineTogliereFuori = (JOptionPane
											.showInputDialog("Inserire numero di pedine da togliere"));

									if (nPedineTogliereFuori != null && !nPedineTogliereFuori.isEmpty()) {
										int choice = Integer.valueOf(nPedineTogliereFuori);
										while (choice < choosen || choice > nPawns) {
											choice = Integer.valueOf(JOptionPane
													.showInputDialog("Inserire numero di pedine da togliere"));
										}

										if (choice > 0) {
											byte nPawnsIn = posToPawn.get((byte) miaCella);
											posToPawn.put((byte) miaCella,
													(byte) (nPawnsIn - choice == 20 ? 0 : nPawnsIn - choice));
											((JButton) b.getComponent(0)).setText("" + posToPawn.get((byte) miaCella));
											((JButton) b.getComponent(0))
													.setBackground(posToPawn.get((byte) miaCella) == 0 ? null
															: posToPawn.get((byte) miaCella) <= 12 ? Color.cyan
																	: Color.green);

										}
									}
								}
							}
						}

						if (controllaVittoria()) {
							JButton exitButton = new JButton("Termina partita");
							exitButton.addMouseListener(new MouseAdapter() {
								@Override
								public void mouseClicked(MouseEvent e) {
									if (SwingUtilities.isLeftMouseButton(e)) {
										f.dispose();
										System.exit(0);
									}
								}
							});

							JButton startNewMatchButton = new JButton("Inizia nuova partita");
							startNewMatchButton.addMouseListener(new MouseAdapter() {
								@Override
								public void mouseClicked(MouseEvent e) {
									try {
										init();
										f.dispose();
										show();
									} catch (Exception ex) {
										ex.printStackTrace();
										System.exit(-1);
									}
								}
							});

							JLabel ll = new JLabel("VITTORIA " + (blackFinished ? "WHITE" : "BLACK"));

							JPanel panel_north = new JPanel();
							panel_north.setBorder(new LineBorder(Color.BLACK, 1));
							panel_north.setBackground(Color.GREEN);
							panel_north.add(ll);
							panel_north.add(startNewMatchButton);
							panel_north.add(exitButton);
							f.getContentPane().add(panel_north, BorderLayout.NORTH);
						}

					} // mouse pressed

					private boolean controllaVittoria() {
						whiteFinished = true;
						blackFinished = true;

						/* PRIMA DI FARE UNA QUALSIASI MOSSA VEDO SE QUALCUNO HA VINTO */
						for (Byte nP : posToPawn.values()) {
							if (nP > 0) {
								if (nP > 12)
									blackFinished = false;
								if (nP <= 12)
									whiteFinished = false;
							}
						}
						return whiteFinished || blackFinished;
					} // controllaVittoria

					private void primoClick() {
						Component[] jbuttons = panel.getComponents();
						byte nPawns = posToPawn.get((byte) miaCella);
						if (nPawns > 0) {
							selected = true;
							from = (byte) miaCella;
							isWhiteMove = nPawns <= 12;

							// coloro di rosso le celle della maschera solo se clicco su casella nera
							String enemy = "";
							for (Byte position : posToPawn.keySet()) {
								int nP = posToPawn.get(position);
								if ((isWhiteMove && nP > 12) || (!isWhiteMove && nP < 12 && nP > 0)) {
									enemy = "1" + enemy;
								} else {
									enemy = "0" + enemy;
								}
							}

							int en = Integer.parseUnsignedInt(enemy, 2);
							int[] msk = HashMapGenerator.getMask((nPawns > 12 ? masksBlack : masksWhite), miaCella,
									nPawns);
							int AV_mask = msk[0];
							int IN_mask = msk[1];

							String maskTmpS = Integer.toBinaryString(AV_mask & (IN_mask | (~en)));
							while (maskTmpS.length() < 32) {
								maskTmpS = "0" + maskTmpS;
							}
							maskTmp = maskTmpS.toCharArray();

							for (int c = 0; c < jbuttons.length; c++) {
								if (((JPanel) panel.getComponent(c)).getBackground().equals(BROWN)) {
									if (maskTmp[c / 2] == '0') {
										b.setBackground(Color.RED);
										b.setBorder(new LineBorder(Color.BLACK, 15));
										((JPanel) jbuttons[c]).setBorder(new LineBorder(Color.BLACK, 15));
										((JPanel) jbuttons[c]).setBackground(Color.YELLOW);
									}
								}
							}
						}
					} // primo click

					private void secondoClick() {
						to = (byte) miaCella;

						boolean condizioneSoddisfatta = maskTmp[maskTmp.length - 1 - miaCella] == '0';

						if (from != -1 && !(from == to) && condizioneSoddisfatta) {
							int nPawns = posToPawn.get(to);
							boolean merge = nPawns == 0 || !(isWhiteMove ^ (nPawns > 0 && nPawns < 12));
							nPawnsSpostare = Math.abs(from / 4 - to / 4); // se uguale a 0 sono sulla stessa riga
							if (!merge) { // sto attaccando
								if (isWhiteMove)
									nPawns -= 20;
								if (nPawnsSpostare >= nPawns && nPawnsSpostare != 0) {

									posToPawn.put(from, (byte) (posToPawn.get(from) - nPawnsSpostare
											- (!isWhiteMove && (posToPawn.get(from) - nPawnsSpostare) == 20 ? 20 : 0)));
									posToPawn.put(to, (byte) (isWhiteMove ? nPawnsSpostare : nPawnsSpostare + 20));
								} else {
									nPawnsSpostare = (Math.abs(posToCol[from] - posToCol[to]));
									if (nPawnsSpostare >= nPawns) {
										posToPawn.put(from,
												(byte) (posToPawn.get(from) - nPawnsSpostare
														- (!isWhiteMove && (posToPawn.get(from) - nPawnsSpostare) == 20
																? 20
																: 0)));
										posToPawn.put(to, (byte) (isWhiteMove ? nPawnsSpostare : nPawnsSpostare + 20));
									}
								}
							} else {
								posToPawn.put(from, (byte) (posToPawn.get(from) - nPawnsSpostare
										- (!isWhiteMove && (posToPawn.get(from) - nPawnsSpostare) == 20 ? 20 : 0)));
								posToPawn.put(to,
										(byte) (nPawns + nPawnsSpostare + (!isWhiteMove && nPawns == 0 ? 20 : 0)));
							}

							Component[] jpanels = panel.getComponents();
							for (int k = 0; k < jpanels.length; k++) {
								if (COLORE_SCACCHIERA[k] == 'B') {
									int posScacchiera = Math.abs(31 - (k / 2));
									int nPed = posToPawn.get((byte) posScacchiera);
									byte nPedByte = (byte) (nPed <= 12 ? nPed : nPed - 20);

									String text = nPedByte == 0 ? "" : "" + nPedByte;
									((JButton) ((JPanel) jpanels[k]).getComponent(0)).setText(text);

									((JButton) ((JPanel) jpanels[k]).getComponent(0))
											.setBackground(nPed == 0 ? null : nPed <= 12 ? Color.cyan : Color.green);
								}
							}

							System.out.println("MOSSA: " + posToPawn);
						}
						from = -1;
						to = -1;
						nPawnsSpostare = -1;
					} // secondo click
				}); // listener mouse su casella

				panel.add(b);
			}
		}
		f.getContentPane().add(panel, BorderLayout.CENTER);
		f.setVisible(true);
	} // show

} // scacchiera
