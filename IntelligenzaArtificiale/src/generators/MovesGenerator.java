package generators;

import java.util.HashMap;

import euristica.Euristica;
import rappresentazione.Node;

public class MovesGenerator {

	private Euristica e;

	private HashMap<Byte, String> posToCell = null;
	private HashMap<Byte, HashMap<Byte, String>> posToDir = null;
	private HashMap<String, Byte> cellToPos = null;
	private HashMap<Byte, Object[]> masksBlack = null;
	private HashMap<Byte, Object[]> masksWhite = null;

	private Byte[] posToCol = { 7, 5, 3, 1, 8, 6, 4, 2, 7, 5, 3, 1, 8, 6, 4, 2, 7, 5, 3, 1, 8, 6, 4, 2, 7, 5, 3, 1, 8,
			6, 4, 2 };

	public HashMap<String, Byte> getCellToPos() {
		return cellToPos;
	}

	public HashMap<Byte, String> getPosToCell() {
		return posToCell;
	}

	public HashMap<Byte, HashMap<Byte, String>> getPosToDir() {
		return posToDir;
	}

	public void init(Euristica e, HashMap<Byte, String> posToCell, HashMap<String, Byte> cellToPos,
			HashMap<Byte, Object[]> masksBlack, HashMap<Byte, Object[]> masksWhite,
			HashMap<Byte, HashMap<Byte, String>> posToDir) {
		this.e = e;
		this.cellToPos = cellToPos;
		this.posToCell = posToCell;
		this.masksBlack = masksBlack;
		this.masksWhite = masksWhite;
		this.posToDir = posToDir;
	}

	public void generateMoves(Node root, boolean isWhite, boolean myColor) {

		byte[] posToPawn, myPositionOnBoard, positionsToAnalyze, posFiglio, direzioni;
		byte numCellMyPosition, miaRiga, miePedine, rigaAvversario, numPedineDestinazione, numPedineDaSpostare,
				numPedineRimanenti, numMinimoPDT;
		int[] msk;
		int mc, ec, forwardMask, backwardMask, maskOfAllMoves, mcr, ecr;
		boolean merge;
		HashMap<Byte, Object[]> masks;
		Node f;

		if (isWhite) {
			mc = root.getWc();
			ec = root.getBc();
		} else {
			mc = root.getBc();
			ec = root.getWc();
		}
		if (mc == 0 || ec == 0)
			return;

		posToPawn = root.getPosToPawns();
		myPositionOnBoard = HashMapGenerator.onesPosition(mc);

		masks = isWhite ? masksWhite : masksBlack;

		int attacks = 0;

		/* INIZIO CALCOLO MOSSA */
		for (int indexOfMyPositionOnBoard = 0; indexOfMyPositionOnBoard < myPositionOnBoard.length; indexOfMyPositionOnBoard++) {

			numCellMyPosition = myPositionOnBoard[indexOfMyPositionOnBoard];
			miaRiga = (byte) (numCellMyPosition / 4);

			miePedine = posToPawn[numCellMyPosition];

			msk = HashMapGenerator.getMask(masks, numCellMyPosition, miePedine);
			forwardMask = msk[0]; // maschera in avanti
			backwardMask = msk[1]; // maschera indietro
			maskOfAllMoves = forwardMask & (backwardMask | (~ec)); // maschera risultato

			positionsToAnalyze = HashMapGenerator.zerosPosition(maskOfAllMoves);

			for (int indexOfPositionToAnalyze = 0; indexOfPositionToAnalyze < positionsToAnalyze.length; indexOfPositionToAnalyze++) {

				mcr = mc;
				ecr = ec;

				posFiglio = posToPawn.clone();

				rigaAvversario = (byte) (positionsToAnalyze[indexOfPositionToAnalyze] / 4);
				numPedineDestinazione = posFiglio[positionsToAnalyze[indexOfPositionToAnalyze]];

				merge = numPedineDestinazione == 0
						|| !(isWhite ^ (numPedineDestinazione > 0 && numPedineDestinazione <= 12));

				numPedineDaSpostare = (byte) Math.abs(miaRiga - rigaAvversario);

				if (!merge && numPedineDaSpostare == 0) { // sono sulla stessa riga
					numPedineDaSpostare = (byte) Math.abs(posToCol[numCellMyPosition] - posToCol[positionsToAnalyze[indexOfPositionToAnalyze]]);
				}

				numPedineRimanenti = (byte) (miePedine - numPedineDaSpostare);

				if (!merge) { // sto attaccando
					if (isWhite) {
						numPedineDestinazione -= 20;
					}
					if (numPedineDaSpostare >= numPedineDestinazione) {

						posFiglio[numCellMyPosition] = (byte) (numPedineRimanenti
								- (!isWhite && numPedineRimanenti == 20 ? 20 : 0));
						posFiglio[positionsToAnalyze[indexOfPositionToAnalyze]] = (byte) (isWhite ? numPedineDaSpostare : numPedineDaSpostare + 20);

						ecr = ecr ^ (1 << positionsToAnalyze[indexOfPositionToAnalyze]);
						mcr = mcr | (1 << positionsToAnalyze[indexOfPositionToAnalyze]);

						if (numPedineRimanenti == 0 || numPedineRimanenti == 20)
							mcr = mcr ^ (1 << numCellMyPosition);

						if (isWhite) {
							f = new Node(root, ecr, mcr, posFiglio, posToCell.get(numCellMyPosition),
									(posToDir.get(numCellMyPosition)).get(positionsToAnalyze[indexOfPositionToAnalyze]), "" + numPedineDaSpostare);
							f.setValue(root.getValue() + e.getEuristica(f, isWhite, myColor));
							root.addSon(f);
							attacks++;
						} else { // sono player black
							f = new Node(root, mcr, ecr, posFiglio, posToCell.get(numCellMyPosition),
									(posToDir.get(numCellMyPosition)).get(positionsToAnalyze[indexOfPositionToAnalyze]), "" + numPedineDaSpostare);
							f.setValue(root.getValue() + e.getEuristica(f, isWhite, myColor));
							root.addSon(f);
							attacks++;
						}
					}
				} else {

					posFiglio[numCellMyPosition] = (byte) (numPedineRimanenti
							- (!isWhite && numPedineRimanenti == 20 ? 20 : 0));
					posFiglio[positionsToAnalyze[indexOfPositionToAnalyze]] = (byte) (numPedineDestinazione + numPedineDaSpostare
							+ (!isWhite && numPedineDestinazione == 0 ? 20 : 0));

					mcr = mcr | (1 << positionsToAnalyze[indexOfPositionToAnalyze]);

					if (posFiglio[numCellMyPosition] == 0)
						mcr = mcr ^ (1 << numCellMyPosition);

					if (isWhite) {
						f = new Node(root, ecr, mcr, posFiglio, posToCell.get(numCellMyPosition),
								(posToDir.get(numCellMyPosition)).get(positionsToAnalyze[indexOfPositionToAnalyze]), "" + numPedineDaSpostare);
						f.setValue(root.getValue() + e.getEuristica(f, isWhite, myColor));
						root.addSon(f);
					} else { // sono player black
						f = new Node(root, mcr, ecr, posFiglio, posToCell.get(numCellMyPosition),
								(posToDir.get(numCellMyPosition)).get(positionsToAnalyze[indexOfPositionToAnalyze]), "" + numPedineDaSpostare);
						f.setValue(root.getValue() + e.getEuristica(f, isWhite, myColor));
						root.addSon(f);
					}
				}
			}

			direzioni = HashMapGenerator.getOutLeastPawns(masks, numCellMyPosition);

			posFiglio = posToPawn.clone();

			mcr = mc;

			if (!isWhite)
				miePedine -= 20;

			if (miePedine >= direzioni[0]) { // NW - SE

				numMinimoPDT = (byte) (direzioni[0]); // numero di Pedine Da Togliere

				// genero configurazione in cui tolgo tutto fuori
				mcr = mcr ^ (1 << numCellMyPosition);

				posFiglio[numCellMyPosition] = (byte) 0;

				if (isWhite) {
					f = new Node(root, ec, mcr, posFiglio, posToCell.get(numCellMyPosition), "NW", "" + (miePedine));
					f.setValue(root.getValue() + e.getEuristica(f, isWhite, myColor));
					root.addSon(f);
				} else { // sono player black
					f = new Node(root, mcr, ec, posFiglio, posToCell.get(numCellMyPosition), "SE", "" + (miePedine));
					f.setValue(root.getValue() + e.getEuristica(f, isWhite, myColor));
					root.addSon(f);
				}

				for (; numMinimoPDT < miePedine; numMinimoPDT++) {
					// incremento ogni volta il numero di pedine da togliere

					posFiglio = posToPawn.clone();

					// genero mosse fuori numPDT nella mia casella
					mcr = mc;

					posFiglio[numCellMyPosition] = (byte) (miePedine - numMinimoPDT + (!isWhite ? 20 : 0));

					if (isWhite) {
						f = new Node(root, ec, mcr, posFiglio, posToCell.get(numCellMyPosition), "NW", "" + numMinimoPDT);
						f.setValue(root.getValue() + e.getEuristica(f, isWhite, myColor));
						root.addSon(f);
					} else {// sono player black
						f = new Node(root, mcr, ec, posFiglio, posToCell.get(numCellMyPosition), "SE", "" + numMinimoPDT);
						f.setValue(root.getValue() + e.getEuristica(f, isWhite, myColor));
						root.addSon(f);
					}

				}

			}

			mcr = mc;

			if (miePedine >= direzioni[1]) { // N - S

				numMinimoPDT = (byte) (direzioni[1]); // numero di Pedine Da Togliere

				if (numMinimoPDT == miePedine) {

					// genero configurazione in cui tolgo tutto fuori
					mcr = mcr ^ (1 << numCellMyPosition);

					posFiglio[numCellMyPosition] = (byte) 0;

					if (isWhite) {
						f = new Node(root, ec, mcr, posFiglio, posToCell.get(numCellMyPosition), "N", "" + (miePedine));
						f.setValue(root.getValue() + e.getEuristica(f, isWhite, myColor));
						root.addSon(f);
					} else { // sono player black
						f = new Node(root, mcr, ec, posFiglio, posToCell.get(numCellMyPosition), "S", "" + (miePedine));
						f.setValue(root.getValue() + e.getEuristica(f, isWhite, myColor));
						root.addSon(f);
					}
				}
				for (; numMinimoPDT < miePedine; numMinimoPDT += 2) {

					posFiglio = posToPawn.clone();

					// genero mosse fuori numPDT nella mia casella
					mcr = mc;

					posFiglio[numCellMyPosition] = (byte) (miePedine - numMinimoPDT + (!isWhite ? 20 : 0));

					if (isWhite) {
						f = new Node(root, ec, mcr, posFiglio, posToCell.get(numCellMyPosition), "N", "" + numMinimoPDT);
						f.setValue(root.getValue() + e.getEuristica(f, isWhite, myColor));
						root.addSon(f);
					} else { // sono player black
						f = new Node(root, mcr, ec, posFiglio, posToCell.get(numCellMyPosition), "S", "" + numMinimoPDT);
						f.setValue(root.getValue() + e.getEuristica(f, isWhite, myColor));
						root.addSon(f);
					}
				}
			}

			mcr = mc;

			if (miePedine >= direzioni[2]) { // NE - SW

				numMinimoPDT = (byte) (direzioni[2]); // numero di Pedine Da Togliere

				// genero configurazione in cui tolgo tutto fuori
				mcr = mcr ^ (1 << numCellMyPosition);

				posFiglio[numCellMyPosition] = (byte) 0;

				if (isWhite) {
					f = new Node(root, ec, mcr, posFiglio, posToCell.get(numCellMyPosition), "NE", "" + (miePedine));
					f.setValue(root.getValue() + e.getEuristica(f, isWhite, myColor));
					root.addSon(f);
				} else { // sono player black
					f = new Node(root, mcr, ec, posFiglio, posToCell.get(numCellMyPosition), "SW", "" + (miePedine));
					f.setValue(root.getValue() + e.getEuristica(f, isWhite, myColor));
					root.addSon(f);
				}

				for (; numMinimoPDT < miePedine; numMinimoPDT++) {

					posFiglio = posToPawn.clone();

					// genero mosse fuori numPDT nella mia casella
					mcr = mc;

					posFiglio[numCellMyPosition] = (byte) (miePedine - numMinimoPDT + (!isWhite ? 20 : 0));

					if (isWhite) {
						f = new Node(root, ec, mcr, posFiglio, posToCell.get(numCellMyPosition), "NE", "" + numMinimoPDT);
						f.setValue(root.getValue() + e.getEuristica(f, isWhite, myColor));
						root.addSon(f);
					} else { // sono player black
						f = new Node(root, mcr, ec, posFiglio, posToCell.get(numCellMyPosition), "SW", "" + numMinimoPDT);
						f.setValue(root.getValue() + e.getEuristica(f, isWhite, myColor));
						root.addSon(f);
					}
				}

			}

		}

		if (root.getSons().size() == 0) {
			for (int i = 0; i < myPositionOnBoard.length; i++) {
				if (isWhite) {
					f = new Node(root, ec, mc, posToPawn, posToCell.get(myPositionOnBoard[i]), "N", "0");
					f.setValue(root.getValue() + e.getEuristica(f, isWhite, myColor));
					root.addSon(f);
				} else {
					f = new Node(root, mc, ec, posToPawn, posToCell.get(myPositionOnBoard[i]), "S", "0");
					f.setValue(root.getValue() + e.getEuristica(f, isWhite, myColor));
					root.addSon(f);
				}
			}
		}

		root.setAttacks(attacks);
	}

	public int createConfig(byte[] posToPawn, boolean isWhite) {
		int conf = 0;
		for (int i = 0; i < posToPawn.length; i++) {
			byte p = posToPawn[i];
			if ((isWhite && p <= 12 && p > 0) || (!isWhite && p > 12)) {
				conf |= (1 << i);
			}
		}
		return conf;

	}

	public void generateMovesRecursive(Node n, boolean isWhite, boolean myColor, int liv, int limite) {
		if (liv == limite || n == null) {
			return;
		}
		generateMoves(n, isWhite, myColor);
		for (Node son : n.getSons()) {
			generateMovesRecursive(son, !isWhite, myColor, liv + 1, limite);
		}
	}
}