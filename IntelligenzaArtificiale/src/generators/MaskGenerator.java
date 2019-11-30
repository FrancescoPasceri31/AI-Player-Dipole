package generators;

import java.util.ArrayList;
import java.util.List;

import testing.Scacchiera;

public class MaskGenerator {

	private List<String> BLACK_MASK = new ArrayList<String>();
	private List<String> Indietro_BLACK_MASK = new ArrayList<String>();
	private List<String> WHITE_MASK = new ArrayList<String>();
	private List<String> Indietro_WHITE_MASK = new ArrayList<String>();
	
	
	public MaskGenerator(Scacchiera scacchiera) {

		System.out.print("Generating black masks... ");
		for (int i = 0; i < scacchiera.getRowNumber(); i++) {
			for (int j = 0; j < scacchiera.getColumnNumber(); j++) {
				if ((scacchiera.getCell(i, j).colore).equals("B")) {
					BLACK_MASK.add(calcolaMask(i, j, scacchiera, true));
				}
			}
		}
		System.out.println("FINISHED");
		System.out.print("Generating white masks... ");
		for (int i = 0; i < scacchiera.getRowNumber(); i++) {
			for (int j = 0; j < scacchiera.getColumnNumber(); j++) {
				if ((scacchiera.getCell(i, j).colore).equals("B")) {
					WHITE_MASK.add(calcolaMask(i, j, scacchiera, false));
				}
			}
		}
		System.out.println("FINISHED");
	
		System.out.print("Generating INDIETRO black masks... ");
		for (int i = 0; i < scacchiera.getRowNumber(); i++) {
			for (int j = 0; j < scacchiera.getColumnNumber(); j++) {
				if ((scacchiera.getCell(i, j).colore).equals("B")) {
					Indietro_BLACK_MASK.add(calcolaMaskIndietro(i, j, scacchiera, true));
				}
			}
		}
		System.out.println("FINISHED");
		System.out.print("Generating INDIETRO white masks... ");
		for (int i = 0; i < scacchiera.getRowNumber(); i++) {
			for (int j = 0; j < scacchiera.getColumnNumber(); j++) {
				if ((scacchiera.getCell(i, j).colore).equals("B")) {
					Indietro_WHITE_MASK.add(calcolaMaskIndietro(i, j, scacchiera, false));
				}
			}
		}
		System.out.println("FINISHED");
		
	
	
	}

	private String calcolaMaskIndietro(int i, int j, Scacchiera scacchiera, boolean isBlackChess) {
		String mask = "";
		if (isBlackChess) { // se sono il giocatore nero
			for (int k = 0; k < scacchiera.getRowNumber(); k++) {
				for (int l = 0; l < scacchiera.getColumnNumber(); l++) {
					if ((scacchiera.getCell(k, l).colore).equals("B")) {
						if (k > i||((k==i)&&l==j)) { 	// riga avanti
							mask += "1";
						} else {	// riga dietro
							int offset = Math.abs(k - i);

							if ((scacchiera.getCell(i, j).colore).equals("B")) {
								if (l == j - offset || l == j + offset || l == j || k==i) {
									mask += "0";
								} else {
									mask += "1";
								}
							}
						}
					}
				}
			}
		} else { // se sono giocatore bianco
			for (int k = 0; k < scacchiera.getRowNumber(); k++) {
				for (int l = 0; l < scacchiera.getColumnNumber(); l++) {
					if ((scacchiera.getCell(k, l).colore).equals("B")) {
						if (k < i||((k==i)&&l==j)) {
							mask += "1";
						} else {
							int offset = Math.abs(k - i);

							if ((scacchiera.getCell(i, j).colore).equals("B")) {
								if (l == j || l == j - offset || l == j + offset ||  k==i) {
									mask += "0";
								} else {
									mask += "1";
								}
							}
						}
					}
				}
			}
		}
		return mask;
	}

	

	private static String calcolaMask(int i, int j, Scacchiera scacchiera, boolean isBlackChess) {
		String mask = "";
		if (isBlackChess) { // se sono il giocatore nero
			for (int k = 0; k < scacchiera.getRowNumber(); k++) {
				for (int l = 0; l < scacchiera.getColumnNumber(); l++) {
					if ((scacchiera.getCell(k, l).colore).equals("B")) {
						if (k <= i) { 	// riga dietro
							mask += "1";
						} else {	// riga avanti
							int offset = Math.abs(k - i);

							if ((scacchiera.getCell(i, j).colore).equals("B")) {
								if (l == j - offset || l == j + offset || l == j) {
									mask += "0";
								} else {
									mask += "1";
								}
							}
						}
					}
				}
			}
		} else { // se sono giocatore bianco
			for (int k = 0; k < scacchiera.getRowNumber(); k++) {
				for (int l = 0; l < scacchiera.getColumnNumber(); l++) {
					if ((scacchiera.getCell(k, l).colore).equals("B")) {
						if (k >= i) {
							mask += "1";
						} else {
							int offset = Math.abs(k - i);

							if ((scacchiera.getCell(i, j).colore).equals("B")) {
								if (l == j || l == j - offset || l == j + offset) {
									mask += "0";
								} else {
									mask += "1";
								}
							}
						}
					}
				}
			}
		}

		/*
		 * char[] maskChar = mask.toCharArray(); String maskReturn = ""; for (int k = 0;
		 * k < maskChar.length; k++) { if (k != 0 && k % 4 == 0) { maskReturn += "-"; }
		 * maskReturn += maskChar[k]; } return maskReturn;
		 */

		return mask;
	}

	public List<String> getBlackMask() {
		return BLACK_MASK;
	}

	public List<String> getWhiteMask() {
		return WHITE_MASK;
	}
	
	public List<String> getIndietro_BLACK_MASK() {
		return Indietro_BLACK_MASK;
	}

	public List<String> getIndietro_WHITE_MASK() {
		return Indietro_WHITE_MASK;
	}
	
}
