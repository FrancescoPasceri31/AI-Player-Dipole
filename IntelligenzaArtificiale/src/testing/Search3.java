package testing;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import euristica.Euristica;
import generators.HashMapGenerator;
import generators.MovesGenerator;
import rappresentazione.Node;

public class Search3 {

	public Euristica e;

	private final double VICTORY = 121037.0;
	private final double LOSE = -151237.0;

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

	public void init() {
		this.e = new Euristica();
		this.e.init();

		try {
			ObjectInputStream i = new ObjectInputStream(new FileInputStream("hashMaps"));
			posToCell = (HashMap<Byte, String>) i.readObject();
			cellToPos = (HashMap<String, Byte>) i.readObject();
			masksBlack = (HashMap<Byte, Object[]>) i.readObject();
			masksWhite = (HashMap<Byte, Object[]>) i.readObject();
			posToDir = (HashMap<Byte, HashMap<Byte, String>>) i.readObject();
			i.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Node recursiveSearch(Node n, boolean isWhite, HashMap<String, Byte> cp, int maxLevel) {
		double c = 0;
		double v = maxVal(n, Double.MIN_VALUE, Double.MAX_VALUE, isWhite, isWhite, c, cp, 0, maxLevel).getValue();
		LinkedList<Node> l = new LinkedList();
		for (Node f : n.getSons()) {
			if (f.getValue() == v)
				l.add(f);
//			f.setParent(null);
		}
//		n.setSons(new LinkedList());
//		System.gc();
		return l.get((new Random()).nextInt(l.size()));
	}

	public Node maxVal(Node n, double alpha, double beta, boolean isWhite, boolean myColor, double c,
			HashMap<String, Byte> cp, int level, int maxLevel) {
		c += e.getEuristica(n, isWhite, cp);
//		System.out.println(n.getId());  
		Node ret = null;
		if (level == maxLevel || n.getBc() == 0 || n.getWc() == 0) {
			if (isWhite && n.getBc() == 0)
				n.setValue(VICTORY - level);
			else if (isWhite && n.getWc() == 0)
				n.setValue(LOSE - level);
			else if (!isWhite && n.getWc() == 0)
				n.setValue(VICTORY - level);
			else if (!isWhite && n.getBc() == 0)
				n.setValue(LOSE - level);
			else
				n.setValue(c);
//			System.out.println("Valore Euristica nodo "+n.getId()+" "+n.getValue());
			return n;
		} // da cambiare
		double v = -Double.MAX_VALUE;
		
		byte[] posToPawn, myP, positions, posFiglio, direzioni;
		byte miaPosizione, miaRiga, miePedine, rigaAvversario, numPedineDestinazione, numPedineDaSpostare,
				numPedineRimanenti, numMinimoPDT;
		int[] msk;
		int mc, ec, m, p, r, mcr, ecr;
		boolean merge;
		HashMap<Byte, Object[]> masks;
		Node f;

		if (myColor) {
			mc = n.getWc();
			ec = n.getBc();
		} else {
			mc = n.getBc();
			ec = n.getWc();
		}
		// if(Byte.parseByte(n.getMossa().substring(n.getMossa().lastIndexOf(",")+1))=='0')
		// return;
		posToPawn = n.getPosToPawns();
//		System.out.println(isWhite+" -> mc : "+ Integer.toBinaryString(mc)+" | ec : " + Integer.toBinaryString(ec)  );
//		System.out.println(posToPawn);
		myP = HashMapGenerator.onesPosition(mc);

		masks = myColor ? masksWhite : masksBlack;

//		System.out.println(isWhite);
//		HashMapGenerator2.printHash(masks);

		/* INIZIO CALCOLO MOSSA */
		for (int k = 0; k < myP.length; k++) {

			miaPosizione = myP[k];
			miaRiga = (byte) (miaPosizione / 4);

			miePedine = posToPawn[miaPosizione];

//			System.out.println();
//			System.out.println("Mia posizione: " + myP[k]+" num pedine: "+miePedine+" "+Integer.toBinaryString(mc) );
//			System.out.println();

			msk = HashMapGenerator.getMask(masks, miaPosizione, miePedine);
			m = msk[0];
			// avanti
			p = msk[1];
			r = m & (p | (~ec));

			positions = HashMapGenerator.zerosPosition(r);

			// System.out.println(Arrays.toString(positions));
			// System.out.println(posToPawn.get((byte)9));

			// if(!isWhite) miePedine-=20;

			for (int j = 0; j < positions.length; j++) {

				mcr = mc;
				ecr = ec;

				posFiglio = posToPawn.clone();

				rigaAvversario = (byte) (positions[j] / 4);
				numPedineDestinazione = posFiglio[positions[j]];
				// System.out.println("posizione: " + positions[j] + ", numero pedine: " +
				// numPedine);
				merge = numPedineDestinazione == 0
						|| !(myColor ^ (numPedineDestinazione > 0 && numPedineDestinazione <= 12));
				numPedineDaSpostare = (byte) Math.abs(miaRiga - rigaAvversario);

				if (!merge && numPedineDaSpostare == 0) { // sono sulla stessa riga
					numPedineDaSpostare = (byte) Math.abs(posToCol[miaPosizione] - posToCol[positions[j]]);
				}

				numPedineRimanenti = (byte) (miePedine - numPedineDaSpostare);

				if (!merge) { // sto attaccando
					if (myColor) {
						numPedineDestinazione -= 20;
					}
					if (numPedineDaSpostare >= numPedineDestinazione) {

						posFiglio[miaPosizione] = (byte) (numPedineRimanenti
								- (!myColor && numPedineRimanenti == 20 ? 20 : 0));
						posFiglio[positions[j]] = (byte) (myColor ? numPedineDaSpostare : numPedineDaSpostare + 20);

						ecr = ecr ^ (1 << positions[j]);
						mcr = mcr | (1 << positions[j]);

						if (numPedineRimanenti == 0 || numPedineRimanenti == 20)
							mcr = mcr ^ (1 << miaPosizione);

						if (myColor) {
							f = new Node(n, ecr, mcr, posFiglio, posToCell.get(miaPosizione),
									(posToDir.get(miaPosizione)).get(positions[j]), "" + numPedineDaSpostare);
							n.addSon(f);
							double min = (minVal(f, alpha, beta, isWhite, !myColor, c, cp, level + 1, maxLevel))
									.getValue();
							v = Math.max(v, min);
							if (v == min)
								ret = f;
							n.setValue(v);
							if (v >= beta)
								return n;
							alpha = Math.max(alpha, v);
						} else { // sono nero
							f = new Node(n, mcr, ecr, posFiglio, posToCell.get(miaPosizione),
									(posToDir.get(miaPosizione)).get(positions[j]), "" + numPedineDaSpostare);
							n.addSon(f);
							double min = (minVal(f, alpha, beta, isWhite, !myColor, c, cp, level + 1, maxLevel))
									.getValue();
							v = Math.max(v, min);
							if (v == min)
								ret = f;
							n.setValue(v);
							if (v >= beta)
								return n;
							alpha = Math.max(alpha, v);
						}

						// System.out.println(Integer.toBinaryString(mc));
						// System.out.println(Integer.toBinaryString(mcr));
						// System.out.println();
					}
				} else {

					posFiglio[miaPosizione] = (byte) (numPedineRimanenti
							- (!myColor && numPedineRimanenti == 20 ? 20 : 0));
					posFiglio[positions[j]] = (byte) (numPedineDestinazione + numPedineDaSpostare
							+ (!myColor && numPedineDestinazione == 0 ? 20 : 0));

					mcr = mcr | (1 << positions[j]);

					if (posFiglio[miaPosizione] == 0)
						mcr = mcr ^ (1 << miaPosizione);

					if (myColor) {
						f = new Node(n, ecr, mcr, posFiglio, posToCell.get(miaPosizione),
								(posToDir.get(miaPosizione)).get(positions[j]), "" + numPedineDaSpostare);
						n.addSon(f);
						double min = (minVal(f, alpha, beta, isWhite, !myColor, c, cp, level + 1, maxLevel)).getValue();
						v = Math.max(v, min);
						if (v == min)
							ret = f;
						n.setValue(v);
						if (v >= beta)
							return n;
						alpha = Math.max(alpha, v);
					} else { // sono nero
						f = new Node(n, mcr, ecr, posFiglio, posToCell.get(miaPosizione),
								(posToDir.get(miaPosizione)).get(positions[j]), "" + numPedineDaSpostare);
						n.addSon(f);
						double min = (minVal(f, alpha, beta, isWhite, !myColor, c, cp, level + 1, maxLevel)).getValue();
						v = Math.max(v, min);
						if (v == min)
							ret = f;
						n.setValue(v);
						if (v >= beta)
							return n;
						alpha = Math.max(alpha, v);
					}

					// System.out.println(Integer.toBinaryString(mc));
					// System.out.println(Integer.toBinaryString(mcr));
					// System.out.println();
				}
			}

			direzioni = HashMapGenerator.getOutLeastPawns(masks, miaPosizione);

			posFiglio = posToPawn.clone();

			// System.out.println("mie pedine: " + miePedine);

			mcr = mc;

			if (!myColor)
				miePedine -= 20;

			if (miePedine >= direzioni[0]) { // NW - SE

				numMinimoPDT = (byte) (direzioni[0]); // numero di Pedine Da Togliere

				// genero configurazione in cui tolgo tutto fuori
				mcr = mcr ^ (1 << miaPosizione);

				posFiglio[miaPosizione] = (byte) 0;

				if (myColor) {
					f = new Node(n, ec, mcr, posFiglio, posToCell.get(miaPosizione), "NW", "" + (miePedine));
					n.addSon(f);
					double min = (minVal(f, alpha, beta, isWhite, !myColor, c, cp, level + 1, maxLevel)).getValue();
					v = Math.max(v, min);
					if (v == min)
						ret = f;
					n.setValue(v);
					if (v >= beta)
						return n;
					alpha = Math.max(alpha, v);
				} else { // sono nero
					f = new Node(n, mcr, ec, posFiglio, posToCell.get(miaPosizione), "SE", "" + (miePedine));
					n.addSon(f);
					double min = (minVal(f, alpha, beta, isWhite, !myColor, c, cp, level + 1, maxLevel)).getValue();
					v = Math.max(v, min);
					if (v == min)
						ret = f;
					n.setValue(v);
					if (v >= beta)
						return n;
					alpha = Math.max(alpha, v);
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

					posFiglio[miaPosizione] = (byte) (miePedine - numMinimoPDT + (!myColor ? 20 : 0));

					if (myColor) {
						f = new Node(n, ec, mcr, posFiglio, posToCell.get(miaPosizione), "NW", "" + numMinimoPDT);
						n.addSon(f);
						double min = (minVal(f, alpha, beta, isWhite, !myColor, c, cp, level + 1, maxLevel)).getValue();
						v = Math.max(v, min);
						if (v == min)
							ret = f;
						n.setValue(v);
						if (v >= beta)
							return n;
						alpha = Math.max(alpha, v);
					} else { // sono nero
						f = new Node(n, mcr, ec, posFiglio, posToCell.get(miaPosizione), "SE", "" + numMinimoPDT);
						n.addSon(f);
						double min = (minVal(f, alpha, beta, isWhite, !myColor, c, cp, level + 1, maxLevel)).getValue();
						v = Math.max(v, min);
						if (v == min)
							ret = f;
						n.setValue(v);
						if (v >= beta)
							return n;
						alpha = Math.max(alpha, v);
					}

				}

			}

			mcr = mc;

			if (miePedine >= direzioni[1]) { // N - S

				numMinimoPDT = (byte) (direzioni[1]); // numero di Pedine Da Togliere

				// genero configurazione in cui tolgo tutto fuori
				mcr = mcr ^ (1 << miaPosizione);

				posFiglio[miaPosizione] = (byte) 0;

				if (myColor) {
					f = new Node(n, ec, mcr, posFiglio, posToCell.get(miaPosizione), "N", "" + (miePedine));
					n.addSon(f);
					double min = (minVal(f, alpha, beta, isWhite, !myColor, c, cp, level + 1, maxLevel)).getValue();
					v = Math.max(v, min);
					if (v == min)
						ret = f;
					n.setValue(v);
					if (v >= beta)
						return n;
					alpha = Math.max(alpha, v);
				} else { // sono nero
					f = new Node(n, mcr, ec, posFiglio, posToCell.get(miaPosizione), "S", "" + (miePedine));
					n.addSon(f);
					double min = (minVal(f, alpha, beta, isWhite, !myColor, c, cp, level + 1, maxLevel)).getValue();
					v = Math.max(v, min);
					if (v == min)
						ret = f;
					n.setValue(v);
					if (v >= beta)
						return n;
					alpha = Math.max(alpha, v);
				}

				for (; numMinimoPDT < miePedine; numMinimoPDT += 2) {

					posFiglio = posToPawn.clone();

					// genero mosse fuori numPDT nella mia casella
					mcr = mc;

					posFiglio[miaPosizione] = (byte) (miePedine - numMinimoPDT + (!myColor ? 20 : 0));

					if (myColor) {
						f = new Node(n, ec, mcr, posFiglio, posToCell.get(miaPosizione), "N", "" + numMinimoPDT);
						n.addSon(f);
						double min = (minVal(f, alpha, beta, isWhite, !myColor, c, cp, level + 1, maxLevel)).getValue();
						v = Math.max(v, min);
						if (v == min)
							ret = f;
						n.setValue(v);
						if (v >= beta)
							return n;
						alpha = Math.max(alpha, v);
					} else { // sono nero
						f = new Node(n, mcr, ec, posFiglio, posToCell.get(miaPosizione), "S", "" + numMinimoPDT);
						n.addSon(f);
						double min = (minVal(f, alpha, beta, isWhite, !myColor, c, cp, level + 1, maxLevel)).getValue();
						v = Math.max(v, min);
						if (v == min)
							ret = f;
						n.setValue(v);
						if (v >= beta)
							return n;
						alpha = Math.max(alpha, v);
					}
				}
			}

			mcr = mc;

			if (miePedine >= direzioni[2]) { // NE - SW

				numMinimoPDT = (byte) (direzioni[2]); // numero di Pedine Da Togliere

				// genero configurazione in cui tolgo tutto fuori
				mcr = mcr ^ (1 << miaPosizione);

				// System.out.println(Integer.toBinaryString(mc));
				// System.out.println(Integer.toBinaryString(mcr));
				// System.out.println();

				posFiglio[miaPosizione] = (byte) 0;

				if (myColor) {
					f = new Node(n, ec, mcr, posFiglio, posToCell.get(miaPosizione), "NE", "" + (miePedine));
					n.addSon(f);
					double min = (minVal(f, alpha, beta, isWhite, !myColor, c, cp, level + 1, maxLevel)).getValue();
					v = Math.max(v, min);
					if (v == min)
						ret = f;
					n.setValue(v);
					if (v >= beta)
						return n;
					alpha = Math.max(alpha, v);
				} else { // sono nero
					f = new Node(n, mcr, ec, posFiglio, posToCell.get(miaPosizione), "SW", "" + (miePedine));
					n.addSon(f);
					double min = (minVal(f, alpha, beta, isWhite, !myColor, c, cp, level + 1, maxLevel)).getValue();
					v = Math.max(v, min);
					if (v == min)
						ret = f;
					n.setValue(v);
					if (v >= beta)
						return n;
					alpha = Math.max(alpha, v);
				}

				for (; numMinimoPDT < miePedine; numMinimoPDT++) {

					posFiglio = posToPawn.clone();

					// genero mosse fuori numPDT nella mia casella
					mcr = mc;

					// System.out.println(Integer.toBinaryString(mc));
					// System.out.println(Integer.toBinaryString(mcr));
					// System.out.println();

					posFiglio[miaPosizione] = (byte) (miePedine - numMinimoPDT + (!myColor ? 20 : 0));

					if (myColor) {
						f = new Node(n, ec, mcr, posFiglio, posToCell.get(miaPosizione), "NE", "" + numMinimoPDT);
						n.addSon(f);
						double min = (minVal(f, alpha, beta, isWhite, !myColor, c, cp, level + 1, maxLevel)).getValue();
						v = Math.max(v, min);
						if (v == min)
							ret = f;
						n.setValue(v);
						if (v >= beta)
							return n;
						alpha = Math.max(alpha, v);
					} else { // sono nero
						f = new Node(n, mcr, ec, posFiglio, posToCell.get(miaPosizione), "SW", "" + numMinimoPDT);
						n.addSon(f);
						double min = (minVal(f, alpha, beta, isWhite, !myColor, c, cp, level + 1, maxLevel)).getValue();
						v = Math.max(v, min);
						if (v == min)
							ret = f;
						n.setValue(v);
						if (v >= beta)
							return n;
						alpha = Math.max(alpha, v);
					}
				}
			}

		}

		if (n.getSons().size() == 0) {
			for (int i = 0; i < myP.length; i++) {
				if (myColor) {
					f = new Node(n, ec, mc, posToPawn, posToCell.get(myP[i]), "N", "0");
					n.addSon(f);
					double min = (minVal(f, alpha, beta, isWhite, !myColor, c, cp, level + 1, maxLevel)).getValue();
					v = Math.max(v, min);
					if (v == min)
						ret = f;
					n.setValue(v);
					if (v >= beta)
						return n;
					alpha = Math.max(alpha, v);
				} else {
					f = new Node(n, mc, ec, posToPawn, posToCell.get(myP[i]), "S", "0");
					n.addSon(f);
					double min = (minVal(f, alpha, beta, isWhite, !myColor, c, cp, level + 1, maxLevel)).getValue();
					v = Math.max(v, min);
					if (v == min)
						ret = f;
					n.setValue(v);
					if (v >= beta)
						return n;
					alpha = Math.max(alpha, v);
				}
			}
		}

		return ret;
	}

	public Node minVal(Node n, double alpha, double beta, boolean isWhite, boolean myColor, double c,
			HashMap<String, Byte> cp, int level, int maxLevel) {
		c += e.getEuristica(n, isWhite, cp);
//		System.out.println(n.getId());
		Node ret = null;
		if (level == maxLevel || n.getBc() == 0 || n.getWc() == 0) {
			if (isWhite && n.getBc() == 0)
				n.setValue(VICTORY - level);
			else if (isWhite && n.getWc() == 0)
				n.setValue(LOSE - level);
			else if (!isWhite && n.getWc() == 0)
				n.setValue(VICTORY - level);
			else if (!isWhite && n.getBc() == 0)
				n.setValue(LOSE - level);
			else
				n.setValue(c);
			return n;
		} // da cambiare
		double v = Double.MAX_VALUE;
		
		byte[] posToPawn, myP, positions, posFiglio, direzioni;
		byte miaPosizione, miaRiga, miePedine, rigaAvversario, numPedineDestinazione, numPedineDaSpostare,
				numPedineRimanenti, numMinimoPDT;
		int[] msk;
		int mc, ec, m, p, r, mcr, ecr;
		boolean merge;
		HashMap<Byte, Object[]> masks;
		Node f;
		
		if (myColor) {
			mc = n.getWc();
			ec = n.getBc();
		} else {
			mc = n.getBc();
			ec = n.getWc();
		}
		// if(Byte.parseByte(n.getMossa().substring(n.getMossa().lastIndexOf(",")+1))=='0')
		// return;
		posToPawn = n.getPosToPawns();
//		System.out.println(isWhite+" -> mc : "+ Integer.toBinaryString(mc)+" | ec : " + Integer.toBinaryString(ec)  );
//		System.out.println(posToPawn);
		myP = HashMapGenerator.onesPosition(mc);

		masks = myColor ? masksWhite : masksBlack;

//		System.out.println(isWhite);
//		HashMapGenerator2.printHash(masks);

		/* INIZIO CALCOLO MOSSA */
		for (int k = 0; k < myP.length; k++) {

			miaPosizione = myP[k];
			miaRiga = (byte) (miaPosizione / 4);

			miePedine = posToPawn[miaPosizione];

//			System.out.println();
//			System.out.println("Mia posizione: " + myP[k]+" num pedine: "+miePedine+" "+Integer.toBinaryString(mc) );
//			System.out.println();

			msk = HashMapGenerator.getMask(masks, miaPosizione, miePedine);
			m = msk[0];
			// avanti
			p = msk[1];
			r = m & (p | (~ec));

			positions = HashMapGenerator.zerosPosition(r);

			// System.out.println(Arrays.toString(positions));
			// System.out.println(posToPawn.get((byte)9));

			// if(!isWhite) miePedine-=20;

			for (int j = 0; j < positions.length; j++) {

				mcr = mc;
				ecr = ec;

				posFiglio = posToPawn.clone();

				rigaAvversario = (byte) (positions[j] / 4);
				numPedineDestinazione = posFiglio[positions[j]];
				// System.out.println("posizione: " + positions[j] + ", numero pedine: " +
				// numPedine);
				merge = numPedineDestinazione == 0
						|| !(myColor ^ (numPedineDestinazione > 0 && numPedineDestinazione <= 12));
				numPedineDaSpostare = (byte) Math.abs(miaRiga - rigaAvversario);

				if (!merge && numPedineDaSpostare == 0) { // sono sulla stessa riga
					numPedineDaSpostare = (byte) Math.abs(posToCol[miaPosizione] - posToCol[positions[j]]);
				}

				numPedineRimanenti = (byte) (miePedine - numPedineDaSpostare);

				if (!merge) { // sto attaccando
					if (myColor) {
						numPedineDestinazione -= 20;
					}
					if (numPedineDaSpostare >= numPedineDestinazione) {

						posFiglio[miaPosizione] = (byte) (numPedineRimanenti
								- (!myColor && numPedineRimanenti == 20 ? 20 : 0));
						posFiglio[positions[j]] = (byte) (myColor ? numPedineDaSpostare : numPedineDaSpostare + 20);

						ecr = ecr ^ (1 << positions[j]);
						mcr = mcr | (1 << positions[j]);

						if (numPedineRimanenti == 0 || numPedineRimanenti == 20)
							mcr = mcr ^ (1 << miaPosizione);

						if (myColor) {
							f = new Node(n, ecr, mcr, posFiglio, posToCell.get(miaPosizione),
									(posToDir.get(miaPosizione)).get(positions[j]), "" + numPedineDaSpostare);
							n.addSon(f);
							double max = (maxVal(f, alpha, beta, isWhite, !myColor, c, cp, level + 1, maxLevel))
									.getValue();
							v = Math.min(v, max);
							if (v == max)
								ret = f;
							n.setValue(v);
							if (v <= alpha)
								return n;
							;
							beta = Math.max(beta, v);
						} else { // sono nero
							f = new Node(n, mcr, ecr, posFiglio, posToCell.get(miaPosizione),
									(posToDir.get(miaPosizione)).get(positions[j]), "" + numPedineDaSpostare);
							n.addSon(f);
							double max = (maxVal(f, alpha, beta, isWhite, !myColor, c, cp, level + 1, maxLevel))
									.getValue();
							v = Math.min(v, max);
							if (v == max)
								ret = f;
							n.setValue(v);
							if (v <= alpha)
								return n;
							;
							beta = Math.max(beta, v);
						}

						// System.out.println(Integer.toBinaryString(mc));
						// System.out.println(Integer.toBinaryString(mcr));
						// System.out.println();
					}
				} else {

					posFiglio[miaPosizione] = (byte) (numPedineRimanenti
							- (!myColor && numPedineRimanenti == 20 ? 20 : 0));
					posFiglio[positions[j]] = (byte) (numPedineDestinazione + numPedineDaSpostare
							+ (!myColor && numPedineDestinazione == 0 ? 20 : 0));

					mcr = mcr | (1 << positions[j]);

					if (posFiglio[miaPosizione] == 0)
						mcr = mcr ^ (1 << miaPosizione);

					if (myColor) {
						f = new Node(n, ecr, mcr, posFiglio, posToCell.get(miaPosizione),
								(posToDir.get(miaPosizione)).get(positions[j]), "" + numPedineDaSpostare);
						n.addSon(f);
						double max = (maxVal(f, alpha, beta, isWhite, !myColor, c, cp, level + 1, maxLevel)).getValue();
						v = Math.min(v, max);
						if (v == max)
							ret = f;
						n.setValue(v);
						if (v <= alpha)
							return n;
						;
						beta = Math.max(beta, v);
					} else { // sono nero
						f = new Node(n, mcr, ecr, posFiglio, posToCell.get(miaPosizione),
								(posToDir.get(miaPosizione)).get(positions[j]), "" + numPedineDaSpostare);
						n.addSon(f);
						double max = (maxVal(f, alpha, beta, isWhite, !myColor, c, cp, level + 1, maxLevel)).getValue();
						v = Math.min(v, max);
						if (v == max)
							ret = f;
						n.setValue(v);
						if (v <= alpha)
							return n;
						;
						beta = Math.max(beta, v);
					}

					// System.out.println(Integer.toBinaryString(mc));
					// System.out.println(Integer.toBinaryString(mcr));
					// System.out.println();
				}
			}

			direzioni = HashMapGenerator.getOutLeastPawns(masks, miaPosizione);

			posFiglio = posToPawn.clone();

			// System.out.println("mie pedine: " + miePedine);

			mcr = mc;

			if (!myColor)
				miePedine -= 20;

			if (miePedine >= direzioni[0]) { // NW - SE

				numMinimoPDT = (byte) (direzioni[0]); // numero di Pedine Da Togliere

				// genero configurazione in cui tolgo tutto fuori
				mcr = mcr ^ (1 << miaPosizione);

				posFiglio[miaPosizione] = (byte) 0;

				if (myColor) {
					f = new Node(n, ec, mcr, posFiglio, posToCell.get(miaPosizione), "NW", "" + (miePedine));
					n.addSon(f);
					double max = (maxVal(f, alpha, beta, isWhite, !myColor, c, cp, level + 1, maxLevel)).getValue();
					v = Math.min(v, max);
					if (v == max)
						ret = f;
					n.setValue(v);
					if (v <= alpha)
						return n;
					;
					beta = Math.max(beta, v);
				} else { // sono nero
					f = new Node(n, mcr, ec, posFiglio, posToCell.get(miaPosizione), "SE", "" + (miePedine));
					n.addSon(f);
					double max = (maxVal(f, alpha, beta, isWhite, !myColor, c, cp, level + 1, maxLevel)).getValue();
					v = Math.min(v, max);
					if (v == max)
						ret = f;
					n.setValue(v);
					if (v <= alpha)
						return n;
					;
					beta = Math.max(beta, v);
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

					posFiglio[miaPosizione] = (byte) (miePedine - numMinimoPDT + (!myColor ? 20 : 0));

					if (myColor) {
						f = new Node(n, ec, mcr, posFiglio, posToCell.get(miaPosizione), "NW", "" + numMinimoPDT);
						n.addSon(f);
						double max = (maxVal(f, alpha, beta, isWhite, !myColor, c, cp, level + 1, maxLevel)).getValue();
						v = Math.min(v, max);
						if (v == max)
							ret = f;
						n.setValue(v);
						if (v <= alpha)
							return n;
						;
						beta = Math.max(beta, v);
					} else { // sono nero
						f = new Node(n, mcr, ec, posFiglio, posToCell.get(miaPosizione), "SE", "" + numMinimoPDT);
						n.addSon(f);
						double max = (maxVal(f, alpha, beta, isWhite, !myColor, c, cp, level + 1, maxLevel)).getValue();
						v = Math.min(v, max);
						if (v == max)
							ret = f;
						n.setValue(v);
						if (v <= alpha)
							return n;
						;
						beta = Math.max(beta, v);
					}

				}

			}

			mcr = mc;

			if (miePedine >= direzioni[1]) { // N - S

				numMinimoPDT = (byte) (direzioni[1]); // numero di Pedine Da Togliere

				// genero configurazione in cui tolgo tutto fuori
				mcr = mcr ^ (1 << miaPosizione);

				posFiglio[miaPosizione] = (byte) 0;

				if (myColor) {
					f = new Node(n, ec, mcr, posFiglio, posToCell.get(miaPosizione), "N", "" + (miePedine));
					n.addSon(f);
					double max = (maxVal(f, alpha, beta, isWhite, !myColor, c, cp, level + 1, maxLevel)).getValue();
					v = Math.min(v, max);
					if (v == max)
						ret = f;
					n.setValue(v);
					if (v <= alpha)
						return n;
					;
					beta = Math.max(beta, v);
				} else { // sono nero
					f = new Node(n, mcr, ec, posFiglio, posToCell.get(miaPosizione), "S", "" + (miePedine));
					n.addSon(f);
					double max = (maxVal(f, alpha, beta, isWhite, !myColor, c, cp, level + 1, maxLevel)).getValue();
					v = Math.min(v, max);
					if (v == max)
						ret = f;
					n.setValue(v);
					if (v <= alpha)
						return n;
					;
					beta = Math.max(beta, v);
				}

				for (; numMinimoPDT < miePedine; numMinimoPDT += 2) {

					posFiglio = posToPawn.clone();

					// genero mosse fuori numPDT nella mia casella
					mcr = mc;

					posFiglio[miaPosizione] = (byte) (miePedine - numMinimoPDT + (!myColor ? 20 : 0));

					if (myColor) {
						f = new Node(n, ec, mcr, posFiglio, posToCell.get(miaPosizione), "N", "" + numMinimoPDT);
						n.addSon(f);
						double max = (maxVal(f, alpha, beta, isWhite, !myColor, c, cp, level + 1, maxLevel)).getValue();
						v = Math.min(v, max);
						if (v == max)
							ret = f;
						n.setValue(v);
						if (v <= alpha)
							return n;
						;
						beta = Math.max(beta, v);
					} else { // sono nero
						f = new Node(n, mcr, ec, posFiglio, posToCell.get(miaPosizione), "S", "" + numMinimoPDT);
						n.addSon(f);
						double max = (maxVal(f, alpha, beta, isWhite, !myColor, c, cp, level + 1, maxLevel)).getValue();
						v = Math.min(v, max);
						if (v == max)
							ret = f;
						n.setValue(v);
						if (v <= alpha)
							return n;
						;
						beta = Math.max(beta, v);
					}
				}
			}

			mcr = mc;

			if (miePedine >= direzioni[2]) { // NE - SW

				numMinimoPDT = (byte) (direzioni[2]); // numero di Pedine Da Togliere

				// genero configurazione in cui tolgo tutto fuori
				mcr = mcr ^ (1 << miaPosizione);

				// System.out.println(Integer.toBinaryString(mc));
				// System.out.println(Integer.toBinaryString(mcr));
				// System.out.println();

				posFiglio[miaPosizione] = (byte) 0;

				if (myColor) {
					f = new Node(n, ec, mcr, posFiglio, posToCell.get(miaPosizione), "NE", "" + (miePedine));
					n.addSon(f);
					double max = (maxVal(f, alpha, beta, isWhite, !myColor, c, cp, level + 1, maxLevel)).getValue();
					v = Math.min(v, max);
					if (v == max)
						ret = f;
					n.setValue(v);
					if (v <= alpha)
						return n;
					;
					beta = Math.max(beta, v);
				} else { // sono nero
					f = new Node(n, mcr, ec, posFiglio, posToCell.get(miaPosizione), "SW", "" + (miePedine));
					n.addSon(f);
					double max = (maxVal(f, alpha, beta, isWhite, !myColor, c, cp, level + 1, maxLevel)).getValue();
					v = Math.min(v, max);
					if (v == max)
						ret = f;
					n.setValue(v);
					if (v <= alpha)
						return n;
					;
					beta = Math.max(beta, v);
				}

				for (; numMinimoPDT < miePedine; numMinimoPDT++) {

					posFiglio = posToPawn.clone();

					// genero mosse fuori numPDT nella mia casella
					mcr = mc;

					// System.out.println(Integer.toBinaryString(mc));
					// System.out.println(Integer.toBinaryString(mcr));
					// System.out.println();

					posFiglio[miaPosizione] = (byte) (miePedine - numMinimoPDT + (!myColor ? 20 : 0));

					if (myColor) {
						f = new Node(n, ec, mcr, posFiglio, posToCell.get(miaPosizione), "NE", "" + numMinimoPDT);
						n.addSon(f);
						double max = (maxVal(f, alpha, beta, isWhite, !myColor, c, cp, level + 1, maxLevel)).getValue();
						v = Math.min(v, max);
						if (v == max)
							ret = f;
						n.setValue(v);
						if (v <= alpha)
							return n;
						;
						beta = Math.max(beta, v);
					} else { // sono nero
						f = new Node(n, mcr, ec, posFiglio, posToCell.get(miaPosizione), "SW", "" + numMinimoPDT);
						n.addSon(f);
						double max = (maxVal(f, alpha, beta, isWhite, !myColor, c, cp, level + 1, maxLevel)).getValue();
						v = Math.min(v, max);
						if (v == max)
							ret = f;
						n.setValue(v);
						if (v <= alpha)
							return n;
						;
						beta = Math.max(beta, v);
					}
				}
			}

		}

		if (n.getSons().size() == 0) {
			for (int i = 0; i < myP.length; i++) {
				if (myColor) {
					f = new Node(n, ec, mc, posToPawn, posToCell.get(myP[i]), "N", "0");
					n.addSon(f);
					double max = (maxVal(f, alpha, beta, isWhite, !myColor, c, cp, level + 1, maxLevel)).getValue();
					v = Math.min(v, max);
					if (v == max)
						ret = f;
					n.setValue(v);
					if (v <= alpha)
						return n;
					;
					beta = Math.max(beta, v);
				} else {
					f = new Node(n, mc, ec, posToPawn, posToCell.get(myP[i]), "S", "0");
					n.addSon(f);
					double max = (maxVal(f, alpha, beta, isWhite, !myColor, c, cp, level + 1, maxLevel)).getValue();
					v = Math.min(v, max);
					if (v == max)
						ret = f;
					n.setValue(v);
					if (v <= alpha)
						return n;
					beta = Math.max(beta, v);
				}
			}
		}
		return ret;

	}

	public boolean testTerminazione(Node n) {
		return n.leaf() || !n.expandable();
	}

}