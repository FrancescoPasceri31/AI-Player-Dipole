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

		byte[] posToPawn, myP, positions, posFiglio, direzioni;
		byte miaPosizione, miaRiga, miePedine, rigaAvversario, numPedineDestinazione, numPedineDaSpostare,
				numPedineRimanenti, numMinimoPDT;
		int[] msk;
		int mc, ec, m, p, r, mcr, ecr;
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
		myP = HashMapGenerator.onesPosition(mc);

		masks = isWhite ? masksWhite : masksBlack;

		int attacks = 0;

		/* INIZIO CALCOLO MOSSA */
		for (int k = 0; k < myP.length; k++) {

			miaPosizione = myP[k];
			miaRiga = (byte) (miaPosizione / 4);

			miePedine = posToPawn[miaPosizione];

			msk = HashMapGenerator.getMask(masks, miaPosizione, miePedine);
			m = msk[0]; // maschera in avanti
			p = msk[1]; // maschera indietro
			r = m & (p | (~ec)); // maschera risultato

			positions = HashMapGenerator.zerosPosition(r);

			for (int j = 0; j < positions.length; j++) {

				mcr = mc;
				ecr = ec;

				posFiglio = posToPawn.clone();

				rigaAvversario = (byte) (positions[j] / 4);
				numPedineDestinazione = posFiglio[positions[j]];

				merge = numPedineDestinazione == 0
						|| !(isWhite ^ (numPedineDestinazione > 0 && numPedineDestinazione <= 12));

				numPedineDaSpostare = (byte) Math.abs(miaRiga - rigaAvversario);

				if (!merge && numPedineDaSpostare == 0) { // sono sulla stessa riga
					numPedineDaSpostare = (byte) Math.abs(posToCol[miaPosizione] - posToCol[positions[j]]);
				}

				numPedineRimanenti = (byte) (miePedine - numPedineDaSpostare);

				if (!merge) { // sto attaccando
					if (isWhite) {
						numPedineDestinazione -= 20;
					}
					if (numPedineDaSpostare >= numPedineDestinazione) {

						posFiglio[miaPosizione] = (byte) (numPedineRimanenti
								- (!isWhite && numPedineRimanenti == 20 ? 20 : 0));
						posFiglio[positions[j]] = (byte) (isWhite ? numPedineDaSpostare : numPedineDaSpostare + 20);

						ecr = ecr ^ (1 << positions[j]);
						mcr = mcr | (1 << positions[j]);

						if (numPedineRimanenti == 0 || numPedineRimanenti == 20)
							mcr = mcr ^ (1 << miaPosizione);

						if (isWhite) {
							f = new Node(root, ecr, mcr, posFiglio, posToCell.get(miaPosizione),
									(posToDir.get(miaPosizione)).get(positions[j]), "" + numPedineDaSpostare);
							f.setValue(root.getValue() + e.getEuristica(f, isWhite, myColor));
							root.addSon(f);
							attacks++;
						} else { // sono player black
							f = new Node(root, mcr, ecr, posFiglio, posToCell.get(miaPosizione),
									(posToDir.get(miaPosizione)).get(positions[j]), "" + numPedineDaSpostare);
							f.setValue(root.getValue() + e.getEuristica(f, isWhite, myColor));
							root.addSon(f);
							attacks++;
						}
					}
				} else {

					posFiglio[miaPosizione] = (byte) (numPedineRimanenti
							- (!isWhite && numPedineRimanenti == 20 ? 20 : 0));
					posFiglio[positions[j]] = (byte) (numPedineDestinazione + numPedineDaSpostare
							+ (!isWhite && numPedineDestinazione == 0 ? 20 : 0));

					mcr = mcr | (1 << positions[j]);

					if (posFiglio[miaPosizione] == 0)
						mcr = mcr ^ (1 << miaPosizione);

					if (isWhite) {
						f = new Node(root, ecr, mcr, posFiglio, posToCell.get(miaPosizione),
								(posToDir.get(miaPosizione)).get(positions[j]), "" + numPedineDaSpostare);
						f.setValue(root.getValue() + e.getEuristica(f, isWhite, myColor));
						root.addSon(f);
					} else { // sono player black
						f = new Node(root, mcr, ecr, posFiglio, posToCell.get(miaPosizione),
								(posToDir.get(miaPosizione)).get(positions[j]), "" + numPedineDaSpostare);
						f.setValue(root.getValue() + e.getEuristica(f, isWhite, myColor));
						root.addSon(f);
					}
				}
			}

			direzioni = HashMapGenerator.getOutLeastPawns(masks, miaPosizione);

			posFiglio = posToPawn.clone();

			mcr = mc;

			if (!isWhite)
				miePedine -= 20;

			if (miePedine >= direzioni[0]) { // NW - SE

				numMinimoPDT = (byte) (direzioni[0]); // numero di Pedine Da Togliere

				// genero configurazione in cui tolgo tutto fuori
				mcr = mcr ^ (1 << miaPosizione);

				posFiglio[miaPosizione] = (byte) 0;

				if (isWhite) {
					f = new Node(root, ec, mcr, posFiglio, posToCell.get(miaPosizione), "NW", "" + (miePedine));
					f.setValue(root.getValue() + e.getEuristica(f, isWhite, myColor));
					root.addSon(f);
				} else { // sono player black
					f = new Node(root, mcr, ec, posFiglio, posToCell.get(miaPosizione), "SE", "" + (miePedine));
					f.setValue(root.getValue() + e.getEuristica(f, isWhite, myColor));
					root.addSon(f);
				}

				for (; numMinimoPDT < miePedine; numMinimoPDT++) {
					// incremento ogni volta il numero di pedine da togliere

					posFiglio = posToPawn.clone();

					// genero mosse fuori numPDT nella mia casella
					mcr = mc;

					posFiglio[miaPosizione] = (byte) (miePedine - numMinimoPDT + (!isWhite ? 20 : 0));

					if (isWhite) {
						f = new Node(root, ec, mcr, posFiglio, posToCell.get(miaPosizione), "NW", "" + numMinimoPDT);
						f.setValue(root.getValue() + e.getEuristica(f, isWhite, myColor));
						root.addSon(f);
					} else {// sono player black
						f = new Node(root, mcr, ec, posFiglio, posToCell.get(miaPosizione), "SE", "" + numMinimoPDT);
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
					mcr = mcr ^ (1 << miaPosizione);

					posFiglio[miaPosizione] = (byte) 0;

					if (isWhite) {
						f = new Node(root, ec, mcr, posFiglio, posToCell.get(miaPosizione), "N", "" + (miePedine));
						f.setValue(root.getValue() + e.getEuristica(f, isWhite, myColor));
						root.addSon(f);
					} else { // sono player black
						f = new Node(root, mcr, ec, posFiglio, posToCell.get(miaPosizione), "S", "" + (miePedine));
						f.setValue(root.getValue() + e.getEuristica(f, isWhite, myColor));
						root.addSon(f);
					}
				}
				for (; numMinimoPDT < miePedine; numMinimoPDT += 2) {

					posFiglio = posToPawn.clone();

					// genero mosse fuori numPDT nella mia casella
					mcr = mc;

					posFiglio[miaPosizione] = (byte) (miePedine - numMinimoPDT + (!isWhite ? 20 : 0));

					if (isWhite) {
						f = new Node(root, ec, mcr, posFiglio, posToCell.get(miaPosizione), "N", "" + numMinimoPDT);
						f.setValue(root.getValue() + e.getEuristica(f, isWhite, myColor));
						root.addSon(f);
					} else { // sono player black
						f = new Node(root, mcr, ec, posFiglio, posToCell.get(miaPosizione), "S", "" + numMinimoPDT);
						f.setValue(root.getValue() + e.getEuristica(f, isWhite, myColor));
						root.addSon(f);
					}
				}
			}

			mcr = mc;

			if (miePedine >= direzioni[2]) { // NE - SW

				numMinimoPDT = (byte) (direzioni[2]); // numero di Pedine Da Togliere

				// genero configurazione in cui tolgo tutto fuori
				mcr = mcr ^ (1 << miaPosizione);

				posFiglio[miaPosizione] = (byte) 0;

				if (isWhite) {
					f = new Node(root, ec, mcr, posFiglio, posToCell.get(miaPosizione), "NE", "" + (miePedine));
					f.setValue(root.getValue() + e.getEuristica(f, isWhite, myColor));
					root.addSon(f);
				} else { // sono player black
					f = new Node(root, mcr, ec, posFiglio, posToCell.get(miaPosizione), "SW", "" + (miePedine));
					f.setValue(root.getValue() + e.getEuristica(f, isWhite, myColor));
					root.addSon(f);
				}

				for (; numMinimoPDT < miePedine; numMinimoPDT++) {

					posFiglio = posToPawn.clone();

					// genero mosse fuori numPDT nella mia casella
					mcr = mc;

					posFiglio[miaPosizione] = (byte) (miePedine - numMinimoPDT + (!isWhite ? 20 : 0));

					if (isWhite) {
						f = new Node(root, ec, mcr, posFiglio, posToCell.get(miaPosizione), "NE", "" + numMinimoPDT);
						f.setValue(root.getValue() + e.getEuristica(f, isWhite, myColor));
						root.addSon(f);
					} else { // sono player black
						f = new Node(root, mcr, ec, posFiglio, posToCell.get(miaPosizione), "SW", "" + numMinimoPDT);
						f.setValue(root.getValue() + e.getEuristica(f, isWhite, myColor));
						root.addSon(f);
					}
				}

			}

		}

		if (root.getSons().size() == 0) {
			for (int i = 0; i < myP.length; i++) {
				if (isWhite) {
					f = new Node(root, ec, mc, posToPawn, posToCell.get(myP[i]), "N", "0");
					f.setValue(root.getValue() + e.getEuristica(f, isWhite, myColor));
					root.addSon(f);
				} else {
					f = new Node(root, mc, ec, posToPawn, posToCell.get(myP[i]), "S", "0");
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