package testing;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

public class Scacchiera {

	private List<String> BLACK_MASK = new ArrayList<String>();
	private List<String> Indietro_BLACK_MASK = new ArrayList<String>();
	private List<String> WHITE_MASK = new ArrayList<String>();
	private List<String> Indietro_WHITE_MASK = new ArrayList<String>();

	private final String ALFABETO = "ABCDEFGH";
	private final char[] COLORE_SCACCHIERA = { 'W', 'B', 'W', 'B', 'W', 'B', 'W', 'B', 'B', 'W', 'B', 'W', 'B', 'W',
			'B', 'W', 'W', 'B', 'W', 'B', 'W', 'B', 'W', 'B', 'B', 'W', 'B', 'W', 'B', 'W', 'B', 'W', 'W', 'B', 'W',
			'B', 'W', 'B', 'W', 'B', 'B', 'W', 'B', 'W', 'B', 'W', 'B', 'W', 'W', 'B', 'W', 'B', 'W', 'B', 'W', 'B',
			'B', 'W', 'B', 'W', 'B', 'W', 'B', 'W', };
	private Cella[][] matrix = new Cella[8][8];

	public Scacchiera() {
		int pos = 0;
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				matrix[i][j] = new Cella();
				matrix[i][j].colore = String.valueOf(COLORE_SCACCHIERA[pos]);
				pos++;
			}
		}
		MaskGenerator mg = new MaskGenerator(this);
		BLACK_MASK = mg.getBlackMask();
		WHITE_MASK = mg.getWhiteMask();
		Indietro_BLACK_MASK = mg.getIndietro_BLACK_MASK();
		Indietro_WHITE_MASK = mg.getIndietro_WHITE_MASK();
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
		;
		JFrame f = new JFrame("Scacchiera");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setResizable(false);
		f.setSize(600, 600);
		f.setLocation(300, 100);
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(getRowNumber(), getColumnNumber()));
		int pos = 0;
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				int miaCella = pos;
				JButton b = new JButton();
				String cellPosition = "(" + ALFABETO.charAt(i) + "," + (j + 1) + ")";
				b.setToolTipText(cellPosition);
				if ((matrix[i][j].colore).equals("B")) {
					b.setBackground(Color.BLACK);

					String bmask = BLACK_MASK.get(pos);
					String wmask = WHITE_MASK.get(pos);
					String textPos = "Posizione = " + cellPosition + " -> " + miaCella;
					String textBmask = "BlackMask = " + bmask;
					String textWmask = "WhiteMask = " + wmask;
					String toolTipText = "<html>" + textPos + "<br>" + textBmask + "<br>" + textWmask + "</html>";
					b.setToolTipText(toolTipText);

					pos++;
				} else {
					b.setBackground(Color.WHITE);
				}

				b.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						if (SwingUtilities.isLeftMouseButton(e)) {

							// riporto in B&W tutte le celle
							Component[] jbuttons = panel.getComponents();
							for (int k = 0; k < jbuttons.length; k++) {
								if (COLORE_SCACCHIERA[k] == 'B') {
									((JButton) jbuttons[k]).setBackground(Color.BLACK);
								} else {
									((JButton) jbuttons[k]).setBackground(Color.WHITE);
								}
								((JButton) jbuttons[k]).setBorder(new LineBorder(Color.WHITE, 0));
							}

							// coloro di rosso le celle della maschera solo se clicco su casella nera
							if (b.getBackground().equals(Color.BLACK)) {

								final int WHITE = 0;
								String[] players = { "WHITE", "BLACK" };
								int choice = JOptionPane.showOptionDialog(null, "Quale giocatore sei?", "Scegli colore",
										JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, players,
										players[0]);
								while (choice != 0 && choice != 1) {
									choice = JOptionPane.showOptionDialog(null, "Quale giocatore sei?", "Scegli colore",
											JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, players,
											players[0]);
								}

								String nPedine = JOptionPane.showInputDialog(null, null, "Quante pedine muovi?", 1);
								while (nPedine.isEmpty() || Integer.valueOf(nPedine) <= 0
										|| Integer.valueOf(nPedine) > 12) {
									nPedine = JOptionPane.showInputDialog(null, null, "Quante pedine muovi?", 1);
								}
								int nPedineMosse = Integer.valueOf(nPedine);

								int AVANTI = 0;
								String[] dir = { "AVANTI", "DIETRO" };
								int direction = JOptionPane.showOptionDialog(null, "Quale giocatore sei?",
										"Scegli colore", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null,
										dir, dir[0]);
								while (direction != 0 && direction != 1) {
									direction = JOptionPane.showOptionDialog(null, "Quale direzione vuoi?",
											"Scegli direzione", JOptionPane.DEFAULT_OPTION,
											JOptionPane.QUESTION_MESSAGE, null, dir, dir[0]);
								}

								char[] maskTmp;
								if (direction == AVANTI) { // mosse avanti
									if (choice == WHITE) { // scelto il bianco
										maskTmp = WHITE_MASK.get(miaCella).toCharArray();
									} else { // scelto il nero
										maskTmp = BLACK_MASK.get(miaCella).toCharArray();
									}
								} else { // mosse indietro
									if (choice == WHITE) { // scelto il bianco
										maskTmp = Indietro_WHITE_MASK.get(miaCella).toCharArray();
									} else { // scelto il nero
										maskTmp = Indietro_BLACK_MASK.get(miaCella).toCharArray();
									}
								}
								maskTmp = editMask(maskTmp, nPedineMosse, miaCella);
								System.out.println((choice != WHITE ? "BLACK_" : "WHITE_") + "Mask " + miaCella
										+ " con " + nPedineMosse + " pedine:\t " + String.valueOf(maskTmp) + "\n");
								for (int c = 0; c < jbuttons.length; c++) {
									if (((JButton) panel.getComponent(c)).getBackground().equals(Color.BLACK)) {
										if (maskTmp[c / 2] == '0') {
											b.setBackground(Color.RED);
											b.setBorder(new LineBorder(Color.BLACK, 15));
											((JButton) jbuttons[c]).setBorder(new LineBorder(Color.BLACK, 15));
											((JButton) jbuttons[c]).setBackground(Color.YELLOW);
											;
										}
									}
								}

							}
						}
					}

					/**
					 * Il metodo annulla le celle in cui posso andare se queste risultano essere
					 * maggiori del mio stack
					 */
					private char[] editMask(char[] maskTmp, int nPedineMosse, int miaCella) {
						for (int k = 0; k < maskTmp.length; k++) {
							if (maskTmp[k] == '0') {
								int differenzaRiga = Math.abs(miaCella / (matrix.length / 2) - k / (matrix.length / 2)); 
								if (differenzaRiga > nPedineMosse) {
									maskTmp[k] = '1';
								}else if ( differenzaRiga == 0 && 2*Math.abs(miaCella - k) > nPedineMosse ) {
									maskTmp[k] = '1';
								}
							}
						}
						return maskTmp;
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