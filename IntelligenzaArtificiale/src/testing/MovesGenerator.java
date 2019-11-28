package testing;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.LinkedList;

import rappresentazione.Node;
import ricerca.Search;

public class MovesGenerator {

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
	

	public void init() throws Exception {

		ObjectInputStream i = new ObjectInputStream(new FileInputStream("hashMaps"));
		posToCell = (HashMap<Byte, String>) i.readObject();
		cellToPos = (HashMap<String, Byte>) i.readObject();
		masksBlack = (HashMap<Byte, Object[]>) i.readObject();
		masksWhite = (HashMap<Byte, Object[]>) i.readObject();
		posToDir = (HashMap<Byte, HashMap<Byte, String>>) i.readObject();
		i.close();

	}

	public void generateMoves(Node root, boolean isWhite) {// (int mc, int ec, HashMap<Byte, Byte> posToPawn, boolean
															// isWhite) {

		int mc;
		int ec;

		if (isWhite) {
			mc = root.getWc();
			ec = root.getBc();
		} else {
			mc = root.getBc();
			ec = root.getWc();
		}
		
		if(mc==0 || ec == 0) return;

		byte[] posToPawn = root.getPosToPawns();
//		System.out.println(isWhite+" -> mc : "+ Integer.toBinaryString(mc)+" | ec : " + Integer.toBinaryString(ec)  );
//		System.out.println(posToPawn);
		byte[] myP = HashMapGenerator2.onesPosition(mc);

		HashMap<Byte, Object[]> masks = null;
		masks = isWhite ? masksWhite : masksBlack;

//		System.out.println(isWhite);
//		HashMapGenerator2.printHash(masks);

		/* INIZIO CALCOLO MOSSA */
		for (int k = 0; k < myP.length; k++) {

			byte miaPosizione = myP[k];
			byte miaRiga = (byte) (miaPosizione / (byte) 4);

			byte miePedine = posToPawn[miaPosizione];

//			System.out.println();
//			System.out.println("Mia posizione: " + myP[k]+" num pedine: "+miePedine+" "+Integer.toBinaryString(mc) );
//			System.out.println();

			int[] msk = HashMapGenerator2.getMask(masks, miaPosizione, miePedine);
			int m = msk[0];
			// avanti
			int p = msk[1];
			int r = m & (p | (~ec));

			byte[] positions = HashMapGenerator2.zerosPosition(r);

			// System.out.println(Arrays.toString(positions));
			// System.out.println(posToPawn.get((byte)9));

			byte[] posFiglio;
			byte rigaAvversario;
			byte numPedineDestinazione;
			boolean merge;
			int mcr, ecr;

			for (int j = 0; j < positions.length; j++) {

				mcr = mc;
				ecr = ec;

				posFiglio = posToPawn.clone();

				rigaAvversario = (byte) (positions[j] / 4);
				numPedineDestinazione = posFiglio[positions[j]];
				// System.out.println("posizione: " + positions[j] + ", numero pedine: " +
				// numPedine);
				merge = numPedineDestinazione == 0
						|| !(isWhite ^ (numPedineDestinazione > 0 && numPedineDestinazione <= 12));
				byte numPedineDaSpostare = (byte) Math.abs(miaRiga - rigaAvversario);

				if (!merge && numPedineDaSpostare == 0) { // sono sulla stessa riga
					numPedineDaSpostare = (byte) Math.abs(posToCol[miaPosizione] - posToCol[positions[j]]);
				}

				byte numPedineRimanenti = (byte) (miePedine - numPedineDaSpostare);

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
							root.addSon(new Node(root, ecr, mcr, posFiglio, posToCell.get(miaPosizione) + ","
									+ (posToDir.get(miaPosizione)).get(positions[j]) + "," + numPedineDaSpostare));
						} else { // sono nero
							root.addSon(new Node(root, mcr, ecr, posFiglio, posToCell.get(miaPosizione) + ","
									+ (posToDir.get(miaPosizione)).get(positions[j]) + "," + numPedineDaSpostare));
						}

						// System.out.println(Integer.toBinaryString(mc));
						// System.out.println(Integer.toBinaryString(mcr));
						// System.out.println();
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
						root.addSon(new Node(root, ecr, mcr, posFiglio, posToCell.get(miaPosizione) + ","
								+ (posToDir.get(miaPosizione)).get(positions[j]) + "," + numPedineDaSpostare));
					} else { // sono nero
						root.addSon(new Node(root, mcr, ecr, posFiglio, posToCell.get(miaPosizione) + ","
								+ (posToDir.get(miaPosizione)).get(positions[j]) + "," + numPedineDaSpostare));
					}

					// System.out.println(Integer.toBinaryString(mc));
					// System.out.println(Integer.toBinaryString(mcr));
					// System.out.println();
				}
			}

			byte[] direzioni = HashMapGenerator2.getOutLeastPawns(masks, miaPosizione);

			posFiglio = posToPawn.clone();

			// System.out.println("mie pedine: " + miePedine);

			mcr = mc;

			if (!isWhite)
				miePedine -= 20;

			if (miePedine >= direzioni[0]) { // NW - SE

				byte numMinimoPDT = (byte) (direzioni[0]); // numero di Pedine Da Togliere

				// genero configurazione in cui tolgo tutto fuori
				mcr = mcr ^ (1 << miaPosizione);

				posFiglio[miaPosizione] = (byte) 0;

				if (isWhite) {
					root.addSon(new Node(root, ec, mcr, posFiglio, posToCell.get(miaPosizione) + ",NW," + (miePedine)));
				} else { // sono nero
					root.addSon(new Node(root, mcr, ec, posFiglio, posToCell.get(miaPosizione) + ",SE," + (miePedine)));
				}

				// System.out.println(Integer.toBinaryString(mc));
				// System.out.println(Integer.toBinaryString(mcr));
				// System.out.println();

				for (; numMinimoPDT < miePedine; numMinimoPDT++) { // incremento ogni volta il numero di pedine da
																	// togliere

					posFiglio = posToPawn.clone();

					// genero mosse fuori numPDT nella mia casella
					mcr = mc;

					// System.out.println(Integer.toBinaryString(mc));
					// System.out.println(Integer.toBinaryString(mcr));
					// System.out.println();

					posFiglio[miaPosizione] = (byte) (miePedine - numMinimoPDT + (!isWhite ? 20 : 0));

					if (isWhite) {
						root.addSon(new Node(root, ec, mcr, posFiglio,
								posToCell.get(miaPosizione) + ",NW," + numMinimoPDT));
					} else { // sono nero
						root.addSon(new Node(root, mcr, ec, posFiglio,
								posToCell.get(miaPosizione) + ",SE," + numMinimoPDT));
					}

				}

			}

			mcr = mc;

			if (miePedine >= direzioni[1]) { // NE - SW

				byte numMinimoPDT = (byte) (direzioni[1]); // numero di Pedine Da Togliere

				// genero configurazione in cui tolgo tutto fuori
				mcr = mcr ^ (1 << miaPosizione);

				// System.out.println(Integer.toBinaryString(mc));
				// System.out.println(Integer.toBinaryString(mcr));
				// System.out.println();

				posFiglio[miaPosizione] = (byte) 0;

				if (isWhite) {
					root.addSon(new Node(root, ec, mcr, posFiglio, posToCell.get(miaPosizione) + ",NE," + (miePedine)));
				} else { // sono nero
					root.addSon(new Node(root, mcr, ec, posFiglio, posToCell.get(miaPosizione) + ",SW," + (miePedine)));
				}

				for (; numMinimoPDT < miePedine; numMinimoPDT++) {

					posFiglio = posToPawn.clone();

					// genero mosse fuori numPDT nella mia casella
					mcr = mc;

					// System.out.println(Integer.toBinaryString(mc));
					// System.out.println(Integer.toBinaryString(mcr));
					// System.out.println();

					posFiglio[miaPosizione] = (byte) (miePedine - numMinimoPDT + (!isWhite ? 20 : 0));

					if (isWhite) {
						root.addSon(new Node(root, ec, mcr, posFiglio,
								posToCell.get(miaPosizione) + ",NE," + numMinimoPDT));
					} else { // sono nero
						root.addSon(new Node(root, mcr, ec, posFiglio,
								posToCell.get(miaPosizione) + ",SW," + numMinimoPDT));
					}
				}
			}

		}

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


	public  static void generateMovesIterative(MovesGenerator mg, Node root, boolean isWhite, int limite) {
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

	public  static void generateMovesRecursive(MovesGenerator mg, Node n, boolean isWhite, int liv, int limite)
			throws IOException {
		if (liv == limite || n == null) {
			return;
		}
		mg.generateMoves(n, isWhite);
		for (Node son : n.getSons()) {
			generateMovesRecursive(mg, son, !isWhite, liv + 1, limite);
		}
	}

	/**
	 * METODO STAMPA
	 */
	public static void metodoStampaTree(Node n, int liv) {
		for (int i = 0; i < liv; i++) {
			System.out.print("\t");
		}
		System.out.println(n.toString());
		for (Node son : n.getSons()) {
			metodoStampaTree(son, liv + 1);
		}
	}
}