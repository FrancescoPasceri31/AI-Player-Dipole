package testing;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import euristica.Euristica;
import generators.HashMapGenerator;
import rappresentazione.Node;

public class Search3 {

	private final double VICTORY = 121037.0;
	private final double LOSE = -151237.0;

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

	public Node recursiveSearch(Node n,boolean isWhite,int maxLevel) {
		double v = generateMoveSearchMax(n,-Double.MAX_VALUE,Double.MAX_VALUE,isWhite,isWhite,0,0,maxLevel).getValue();
		LinkedList<Node> l = new LinkedList();
		for(Node f: n.getSons()) { 
			if(f.getValue()==v) l.add(f);
		}
		System.out.println(l);
		return l.get((new Random()).nextInt(l.size()));
	}

	public Node generateMoveSearchMax(Node root, double alpha, double beta, boolean isWhite, boolean myColor, double c,int level, int maxLevel) {// (int mc, int ec, HashMap<Byte, Byte> posToPawn, boolean
		c += e.getEuristica(root, myColor);
		Node ret = null;
		if (testTerminazione(root, level, maxLevel)) {
			if (myColor && root.getBc() == 0)
				root.setValue(VICTORY - level);
			else if (myColor && root.getWc() == 0)
				root.setValue(LOSE - level);
			else if (!myColor && root.getWc() == 0)
				root.setValue(VICTORY - level);
			else if (!myColor && root.getBc() == 0)
				root.setValue(LOSE - level);
			else
				root.setValue(c);
			return root;
		}
		double v = -Double.MAX_VALUE;
		if(root.getSons().size()!=0) {
			for(Node f: root.getSons()) {
				double min = (generateMovesSearchMin(f, alpha, beta, !isWhite, myColor, c, level + 1,
						maxLevel)).getValue(); // valore del figlio
				v = Math.max(v, min);
				if (v == min)
					ret = f; // se non entra mai in questo if, allora ritornerà null
				root.setValue(v);
//				if (v >= beta)
//					return root;
				alpha = Math.max(alpha, v);
			}
			return ret;
		}
		
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
			return root;

		posToPawn = root.getPosToPawns();
		myP = HashMapGenerator.onesPosition(mc);

		masks = isWhite ? masksWhite : masksBlack;


		/* INIZIO CALCOLO MOSSA */
		for (int k = 0; k < myP.length; k++) {

			miaPosizione = myP[k];
			miaRiga = (byte) (miaPosizione / 4);
			miePedine = posToPawn[miaPosizione];
			msk = HashMapGenerator.getMask(masks, miaPosizione, miePedine);
			m = msk[0];
			// avanti
			p = msk[1];
			r = m & (p | (~ec));

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
//							f.setValue(root.getValue()+e.getEuristica(f, myColor));
							root.addSon(f);

							double min = (generateMovesSearchMin(f, alpha, beta, !isWhite, myColor, c, level + 1,
									maxLevel)).getValue(); // valore del figlio
							v = Math.max(v, min);
							if (v == min)
								ret = f; // se non entra mai in questo if, allora ritornerà null
							root.setValue(v);
//							if (v >= beta)
//								return root;
							alpha = Math.max(alpha, v);

						} else { // sono nero
							f = new Node(root, mcr, ecr, posFiglio, posToCell.get(miaPosizione),
									(posToDir.get(miaPosizione)).get(positions[j]), "" + numPedineDaSpostare);
//							f.setValue(root.getValue()+e.getEuristica(f, myColor));
							root.addSon(f);

							double min = (generateMovesSearchMin(f, alpha, beta, !isWhite, myColor, c, level + 1,
									maxLevel)).getValue(); // valore del figlio
							v = Math.max(v, min);
							if (v == min)
								ret = f; // se non entra mai in questo if, allora ritornerà null
							root.setValue(v);
//							if (v >= beta)
//								return root;
							alpha = Math.max(alpha, v);
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
//						f.setValue(root.getValue()+e.getEuristica(f, myColor));
						root.addSon(f);
				
						double min = (generateMovesSearchMin(f, alpha, beta, !isWhite, myColor, c, level + 1, maxLevel))
								.getValue(); // valore del figlio
						v = Math.max(v, min);
						if (v == min)
							ret = f; // se non entra mai in questo if, allora ritornerà null
						root.setValue(v);
//						if (v >= beta)
//							return root;
						alpha = Math.max(alpha, v);

					} else { // sono nero
						f = new Node(root, mcr, ecr, posFiglio, posToCell.get(miaPosizione),
								(posToDir.get(miaPosizione)).get(positions[j]), "" + numPedineDaSpostare);
//						f.setValue(root.getValue()+e.getEuristica(f, myColor));
						root.addSon(f);

						double min = (generateMovesSearchMin(f, alpha, beta, !isWhite, myColor, c, level + 1, maxLevel))
								.getValue(); // valore del figlio
						v = Math.max(v, min);
						if (v == min)
							ret = f; // se non entra mai in questo if, allora ritornerà null
						root.setValue(v);
//						if (v >= beta)
//							return root;
						alpha = Math.max(alpha, v);
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
//					f.setValue(root.getValue()+e.getEuristica(f, myColor));
					root.addSon(f);
					double min = (generateMovesSearchMin(f, alpha, beta, !isWhite, myColor, c, level + 1, maxLevel))
							.getValue(); // valore del figlio
					v = Math.max(v, min);
					if (v == min)
						ret = f; // se non entra mai in questo if, allora ritornerà null
					root.setValue(v);
//					if (v >= beta)
//						return root;
					alpha = Math.max(alpha, v);
				} else { // sono nero
					f = new Node(root, mcr, ec, posFiglio, posToCell.get(miaPosizione), "SE", "" + (miePedine));
//					f.setValue(root.getValue()+e.getEuristica(f, myColor));
					root.addSon(f);
					double min = (generateMovesSearchMin(f, alpha, beta, !isWhite, myColor, c, level + 1, maxLevel))
							.getValue(); // valore del figlio
					v = Math.max(v, min);
					if (v == min)
						ret = f; // se non entra mai in questo if, allora ritornerà null
					root.setValue(v);
//					if (v >= beta)
//						return root;
					alpha = Math.max(alpha, v);
				}

				for (; numMinimoPDT < miePedine; numMinimoPDT++) { // incremento ogni volta il numero di pedine da
																	// togliere

					posFiglio = posToPawn.clone();

					// genero mosse fuori numPDT nella mia casella
					mcr = mc;

					posFiglio[miaPosizione] = (byte) (miePedine - numMinimoPDT + (!isWhite ? 20 : 0));

					if (isWhite) {
						f = new Node(root, ec, mcr, posFiglio, posToCell.get(miaPosizione), "NW", "" + numMinimoPDT);
//						f.setValue(root.getValue()+e.getEuristica(f, myColor));
						root.addSon(f);
						double min = (generateMovesSearchMin(f, alpha, beta, !isWhite, myColor, c, level + 1, maxLevel))
								.getValue(); // valore del figlio
						v = Math.max(v, min);
						if (v == min)
							ret = f; // se non entra mai in questo if, allora ritornerà null
						root.setValue(v);
//						if (v >= beta)
//							return root;
						alpha = Math.max(alpha, v);
					} else { // sono nero
						f = new Node(root, mcr, ec, posFiglio, posToCell.get(miaPosizione), "SE", "" + numMinimoPDT);
//						f.setValue(root.getValue()+e.getEuristica(f, myColor));
						root.addSon(f);
						double min = (generateMovesSearchMin(f, alpha, beta, !isWhite, myColor, c, level + 1, maxLevel))
								.getValue(); // valore del figlio
						v = Math.max(v, min);
						if (v == min)
							ret = f; // se non entra mai in questo if, allora ritornerà null
						root.setValue(v);
//						if (v >= beta)
//							return root;
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

				if (isWhite) {
					f = new Node(root, ec, mcr, posFiglio, posToCell.get(miaPosizione), "N", "" + (miePedine));
//					f.setValue(root.getValue()+e.getEuristica(f, myColor));
					root.addSon(f);
					double min = (generateMovesSearchMin(f, alpha, beta, !isWhite, myColor, c, level + 1, maxLevel))
							.getValue(); // valore del figlio
					v = Math.max(v, min);
					if (v == min)
						ret = f; // se non entra mai in questo if, allora ritornerà null
					root.setValue(v);
//					if (v >= beta)
//						return root;
					alpha = Math.max(alpha, v);
				} else { // sono nero
					f = new Node(root, mcr, ec, posFiglio, posToCell.get(miaPosizione), "S", "" + (miePedine));
//					f.setValue(root.getValue()+e.getEuristica(f, myColor));
					root.addSon(f);
					double min = (generateMovesSearchMin(f, alpha, beta, !isWhite, myColor, c, level + 1, maxLevel))
							.getValue(); // valore del figlio
					v = Math.max(v, min);
					if (v == min)
						ret = f; // se non entra mai in questo if, allora ritornerà null
					root.setValue(v);
//					if (v >= beta)
//						return root;
					alpha = Math.max(alpha, v);
				}

				for (; numMinimoPDT < miePedine; numMinimoPDT += 2) {

					posFiglio = posToPawn.clone();

					// genero mosse fuori numPDT nella mia casella
					mcr = mc;

					posFiglio[miaPosizione] = (byte) (miePedine - numMinimoPDT + (!isWhite ? 20 : 0));

					if (isWhite) {
						f = new Node(root, ec, mcr, posFiglio, posToCell.get(miaPosizione), "N", "" + numMinimoPDT);
//						f.setValue(root.getValue()+e.getEuristica(f, myColor));
						root.addSon(f);
						double min = (generateMovesSearchMin(f, alpha, beta, !isWhite, myColor, c, level + 1, maxLevel))
								.getValue(); // valore del figlio
						v = Math.max(v, min);
						if (v == min)
							ret = f; // se non entra mai in questo if, allora ritornerà null
						root.setValue(v);
//						if (v >= beta)
//							return root;
						alpha = Math.max(alpha, v);
					} else { // sono nero
						f = new Node(root, mcr, ec, posFiglio, posToCell.get(miaPosizione), "S", "" + numMinimoPDT);
//						f.setValue(root.getValue()+e.getEuristica(f, myColor));
						root.addSon(f);
						double min = (generateMovesSearchMin(f, alpha, beta, !isWhite, myColor, c, level + 1, maxLevel))
								.getValue(); // valore del figlio
						v = Math.max(v, min);
						if (v == min)
							ret = f; // se non entra mai in questo if, allora ritornerà null
						root.setValue(v);
//						if (v >= beta)
//							return root;
						alpha = Math.max(alpha, v);
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
//					f.setValue(root.getValue()+e.getEuristica(f, myColor));
					root.addSon(f);
					double min = (generateMovesSearchMin(f, alpha, beta, !isWhite, myColor, c, level + 1, maxLevel))
							.getValue(); // valore del figlio
					v = Math.max(v, min);
					if (v == min)
						ret = f; // se non entra mai in questo if, allora ritornerà null
					root.setValue(v);
//					if (v >= beta)
//						return root;
					alpha = Math.max(alpha, v);
				} else { // sono nero
					f = new Node(root, mcr, ec, posFiglio, posToCell.get(miaPosizione), "SW", "" + (miePedine));
//					f.setValue(root.getValue()+e.getEuristica(f, myColor));
					root.addSon(f);
					double min = (generateMovesSearchMin(f, alpha, beta, !isWhite, myColor, c, level + 1, maxLevel))
							.getValue(); // valore del figlio
					v = Math.max(v, min);
					if (v == min)
						ret = f; // se non entra mai in questo if, allora ritornerà null
					root.setValue(v);
//					if (v >= beta)
//						return root;
					alpha = Math.max(alpha, v);
				}

				for (; numMinimoPDT < miePedine; numMinimoPDT++) {

					posFiglio = posToPawn.clone();

					// genero mosse fuori numPDT nella mia casella
					mcr = mc;

					posFiglio[miaPosizione] = (byte) (miePedine - numMinimoPDT + (!isWhite ? 20 : 0));

					if (isWhite) {
						f = new Node(root, ec, mcr, posFiglio, posToCell.get(miaPosizione), "NE", "" + numMinimoPDT);
//						f.setValue(root.getValue()+e.getEuristica(f, myColor));
						root.addSon(f);
						double min = (generateMovesSearchMin(f, alpha, beta, !isWhite, myColor, c, level + 1, maxLevel))
								.getValue(); // valore del figlio
						v = Math.max(v, min);
						if (v == min)
							ret = f; // se non entra mai in questo if, allora ritornerà null
						root.setValue(v);
//						if (v >= beta)
//							return root;
						alpha = Math.max(alpha, v);
					} else { // sono nero
						f = new Node(root, mcr, ec, posFiglio, posToCell.get(miaPosizione), "SW", "" + numMinimoPDT);
//						f.setValue(root.getValue()+e.getEuristica(f, myColor));
						root.addSon(f);
						double min = (generateMovesSearchMin(f, alpha, beta, !isWhite, myColor, c, level + 1, maxLevel))
								.getValue(); // valore del figlio
						v = Math.max(v, min);
						if (v == min)
							ret = f; // se non entra mai in questo if, allora ritornerà null
						root.setValue(v);
//						if (v >= beta)
//							return root;
						alpha = Math.max(alpha, v);
					}
				}
			}

		}

		if (root.getSons().size() == 0) {
			for (int i = 0; i < myP.length; i++) {
				if (isWhite) {
					f = new Node(root, ec, mc, posToPawn, posToCell.get(myP[i]), "N", "0");
//					f.setValue(root.getValue()+e.getEuristica(f, myColor));
					root.addSon(f);
					double min = (generateMovesSearchMin(f, alpha, beta, !isWhite, myColor, c, level + 1, maxLevel))
							.getValue(); // valore del figlio
					v = Math.max(v, min);
					if (v == min)
						ret = f; // se non entra mai in questo if, allora ritornerà null
					root.setValue(v);
//					if (v >= beta)
//						return root;
					alpha = Math.max(alpha, v);
				} else {
					f = new Node(root, mc, ec, posToPawn, posToCell.get(myP[i]), "S", "0");
//					f.setValue(root.getValue()+e.getEuristica(f, myColor));
					root.addSon(f);
					double min = (generateMovesSearchMin(f, alpha, beta, !isWhite, myColor, c, level + 1, maxLevel))
							.getValue(); // valore del figlio
					v = Math.max(v, min);
					if (v == min)
						ret = f; // se non entra mai in questo if, allora ritornerà null
					root.setValue(v);
//					if (v >= beta)
//						return root;
					alpha = Math.max(alpha, v);
				}
			}
		}

		return ret;

	}

	public Node generateMovesSearchMin(Node root, double alpha, double beta, boolean isWhite, boolean myColor, double c,
			int level, int maxLevel) {
		c += e.getEuristica(root, myColor);
		Node ret = null;
		if (testTerminazione(root, level, maxLevel)) {
			if (myColor && root.getBc() == 0)
				root.setValue(VICTORY - level);
			else if (myColor && root.getWc() == 0)
				root.setValue(LOSE - level);
			else if (!myColor && root.getWc() == 0)
				root.setValue(VICTORY - level);
			else if (!myColor && root.getBc() == 0)
				root.setValue(LOSE - level);
			else
				root.setValue(c);
			return root;
		}
		double v = Double.MAX_VALUE;
		if(root.getSons().size()!=0) {
			for(Node f: root.getSons()) {
				double max = (generateMoveSearchMax(f, alpha, beta, !isWhite, myColor, c, level + 1, maxLevel))
						.getValue(); // valore del figlio
				v = Math.min(v, max);
				if (v == max)
					ret = f;
				root.setValue(v);
	     		if(v <= alpha) return root;
				beta = Math.max(beta, v);
			}
			return ret;
		}
		
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
			return root;

		posToPawn = root.getPosToPawns();
		myP = HashMapGenerator.onesPosition(mc);

		masks = isWhite ? masksWhite : masksBlack;

		/* INIZIO CALCOLO MOSSA */
		for (int k = 0; k < myP.length; k++) {

			miaPosizione = myP[k];
			miaRiga = (byte) (miaPosizione / 4);
			miePedine = posToPawn[miaPosizione];
	
			msk = HashMapGenerator.getMask(masks, miaPosizione, miePedine);
			m = msk[0];
			p = msk[1];
			r = m & (p | (~ec));

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
//							f.setValue(root.getValue() + e.getEuristica(f, myColor));
							root.addSon(f);

							double max = (generateMoveSearchMax(f, alpha, beta, !isWhite, myColor, c, level + 1, maxLevel))
									.getValue(); // valore del figlio
							v = Math.min(v, max);
							if (v == max)
								ret = f;
							root.setValue(v);
				     		if(v <= alpha) return root;
							beta = Math.max(beta, v);

						} else { // sono nero
							f = new Node(root, mcr, ecr, posFiglio, posToCell.get(miaPosizione),
									(posToDir.get(miaPosizione)).get(positions[j]), "" + numPedineDaSpostare);
//							f.setValue(root.getValue() + e.getEuristica(f, myColor));
							root.addSon(f);

							double max = (generateMoveSearchMax(f, alpha, beta, !isWhite, myColor, c, level + 1, maxLevel))
									.getValue(); // valore del figlio
							v = Math.min(v, max);
							if (v == max)
								ret = f;
							root.setValue(v);
				     		if(v <= alpha) return root;
							beta = Math.max(beta, v);
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
//						f.setValue(root.getValue() + e.getEuristica(f, myColor));
						root.addSon(f);
						double max = (generateMoveSearchMax(f, alpha, beta, !isWhite, myColor, c, level + 1, maxLevel))
								.getValue(); // valore del figlio
						v = Math.min(v, max);
						if (v == max)
							ret = f;
						root.setValue(v);
			     		if(v <= alpha) return root;
						beta = Math.max(beta, v);
					} else { // sono nero
						f = new Node(root, mcr, ecr, posFiglio, posToCell.get(miaPosizione),
								(posToDir.get(miaPosizione)).get(positions[j]), "" + numPedineDaSpostare);
//						f.setValue(root.getValue() + e.getEuristica(f, myColor));
						root.addSon(f);
						double max = (generateMoveSearchMax(f, alpha, beta, !isWhite, myColor, c, level + 1, maxLevel))
								.getValue(); // valore del figlio
						v = Math.min(v, max);
						if (v == max)
							ret = f;
						root.setValue(v);
			     		if(v <= alpha) return root;
						beta = Math.max(beta, v);
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
//					f.setValue(root.getValue() + e.getEuristica(f, myColor));
					root.addSon(f);

					double max = (generateMoveSearchMax(f, alpha, beta, !isWhite, myColor, c, level + 1, maxLevel))
							.getValue(); // valore del figlio
					v = Math.min(v, max);
					if (v == max)
						ret = f;
					root.setValue(v);
		     		if(v <= alpha) return root;
					beta = Math.max(beta, v);
				} else { // sono nero
					f = new Node(root, mcr, ec, posFiglio, posToCell.get(miaPosizione), "SE", "" + (miePedine));
//					f.setValue(root.getValue() + e.getEuristica(f, myColor));
					root.addSon(f);

					double max = (generateMoveSearchMax(f, alpha, beta, !isWhite, myColor, c, level + 1, maxLevel))
							.getValue(); // valore del figlio
					v = Math.min(v, max);
					if (v == max)
						ret = f;
					root.setValue(v);
		     		if(v <= alpha) return root;
					beta = Math.max(beta, v);
				}

				for (; numMinimoPDT < miePedine; numMinimoPDT++) { // incremento ogni volta il numero di pedine da
					// togliere

					posFiglio = posToPawn.clone();

// genero mosse fuori numPDT nella mia casella
					mcr = mc;

					posFiglio[miaPosizione] = (byte) (miePedine - numMinimoPDT + (!isWhite ? 20 : 0));

					if (isWhite) {
						f = new Node(root, ec, mcr, posFiglio, posToCell.get(miaPosizione), "NW", "" + numMinimoPDT);
//						f.setValue(root.getValue() + e.getEuristica(f, myColor));
						root.addSon(f);

						double max = (generateMoveSearchMax(f, alpha, beta, !isWhite, myColor, c, level + 1, maxLevel))
								.getValue(); // valore del figlio
						v = Math.min(v, max);
						if (v == max)
							ret = f;
						root.setValue(v);
			     		if(v <= alpha) return root;
						beta = Math.max(beta, v);
					} else { // sono nero
						f = new Node(root, mcr, ec, posFiglio, posToCell.get(miaPosizione), "SE", "" + numMinimoPDT);
//						f.setValue(root.getValue() + e.getEuristica(f, myColor));
						root.addSon(f);

						double max = (generateMoveSearchMax(f, alpha, beta, !isWhite, myColor, c, level + 1, maxLevel))
								.getValue(); // valore del figlio
						v = Math.min(v, max);
						if (v == max)
							ret = f;
						root.setValue(v);
			     		if(v <= alpha) return root;
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

				if (isWhite) {
					f = new Node(root, ec, mcr, posFiglio, posToCell.get(miaPosizione), "N", "" + (miePedine));
//					f.setValue(root.getValue() + e.getEuristica(f, myColor));
					root.addSon(f);

					double max = (generateMoveSearchMax(f, alpha, beta, !isWhite, myColor, c, level + 1, maxLevel))
							.getValue(); // valore del figlio
					v = Math.min(v, max);
					if (v == max)
						ret = f;
					root.setValue(v);
		     		if(v <= alpha) return root;
					beta = Math.max(beta, v);
				} else { // sono nero
					f = new Node(root, mcr, ec, posFiglio, posToCell.get(miaPosizione), "S", "" + (miePedine));
//					f.setValue(root.getValue() + e.getEuristica(f, myColor));
					root.addSon(f);

					double max = (generateMoveSearchMax(f, alpha, beta, !isWhite, myColor, c, level + 1, maxLevel))
							.getValue(); // valore del figlio
					v = Math.min(v, max);
					if (v == max)
						ret = f;
					root.setValue(v);
		     		if(v <= alpha) return root;
					beta = Math.max(beta, v);
				}

				for (; numMinimoPDT < miePedine; numMinimoPDT += 2) {

					posFiglio = posToPawn.clone();

// genero mosse fuori numPDT nella mia casella
					mcr = mc;

					posFiglio[miaPosizione] = (byte) (miePedine - numMinimoPDT + (!isWhite ? 20 : 0));

					if (isWhite) {
						f = new Node(root, ec, mcr, posFiglio, posToCell.get(miaPosizione), "N", "" + numMinimoPDT);
//						f.setValue(root.getValue() + e.getEuristica(f, myColor));
						root.addSon(f);

						double max = (generateMoveSearchMax(f, alpha, beta, !isWhite, myColor, c, level + 1, maxLevel))
								.getValue(); // valore del figlio
						v = Math.min(v, max);
						if (v == max)
							ret = f;
						root.setValue(v);
			     		if(v <= alpha) return root;
						beta = Math.max(beta, v);
					} else { // sono nero
						f = new Node(root, mcr, ec, posFiglio, posToCell.get(miaPosizione), "S", "" + numMinimoPDT);
//						f.setValue(root.getValue() + e.getEuristica(f, myColor));
						root.addSon(f);

						double max = (generateMoveSearchMax(f, alpha, beta, !isWhite, myColor, c, level + 1, maxLevel))
								.getValue(); // valore del figlio
						v = Math.min(v, max);
						if (v == max)
							ret = f;
						root.setValue(v);
			     		if(v <= alpha) return root;
						beta = Math.max(beta, v);
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
//					f.setValue(root.getValue() + e.getEuristica(f, myColor));
					root.addSon(f);

					double max = (generateMoveSearchMax(f, alpha, beta, !isWhite, myColor, c, level + 1, maxLevel))
							.getValue(); // valore del figlio
					v = Math.min(v, max);
					if (v == max)
						ret = f;
					root.setValue(v);
		     		if(v <= alpha) return root;
					beta = Math.max(beta, v);
				} else { // sono nero
					f = new Node(root, mcr, ec, posFiglio, posToCell.get(miaPosizione), "SW", "" + (miePedine));
//					f.setValue(root.getValue() + e.getEuristica(f, myColor));
					root.addSon(f);

					double max = (generateMoveSearchMax(f, alpha, beta, !isWhite, myColor, c, level + 1, maxLevel))
							.getValue(); // valore del figlio
					v = Math.min(v, max);
					if (v == max)
						ret = f;
					root.setValue(v);
		     		if(v <= alpha) return root;
					beta = Math.max(beta, v);
				}

				for (; numMinimoPDT < miePedine; numMinimoPDT++) {

					posFiglio = posToPawn.clone();

// genero mosse fuori numPDT nella mia casella
					mcr = mc;

					posFiglio[miaPosizione] = (byte) (miePedine - numMinimoPDT + (!isWhite ? 20 : 0));

					if (isWhite) {
						f = new Node(root, ec, mcr, posFiglio, posToCell.get(miaPosizione), "NE", "" + numMinimoPDT);
//						f.setValue(root.getValue() + e.getEuristica(f, myColor));
						root.addSon(f);

						double max = (generateMoveSearchMax(f, alpha, beta, !isWhite, myColor, c, level + 1, maxLevel))
								.getValue(); // valore del figlio
						v = Math.min(v, max);
						if (v == max)
							ret = f;
						root.setValue(v);
			     		if(v <= alpha) return root;
						beta = Math.max(beta, v);
					} else { // sono nero
						f = new Node(root, mcr, ec, posFiglio, posToCell.get(miaPosizione), "SW", "" + numMinimoPDT);
//						f.setValue(root.getValue() + e.getEuristica(f, myColor));
						root.addSon(f);

						double max = (generateMoveSearchMax(f, alpha, beta, !isWhite, myColor, c, level + 1, maxLevel))
								.getValue(); // valore del figlio
						v = Math.min(v, max);
						if (v == max)
							ret = f;
						root.setValue(v);
			     		if(v <= alpha) return root;
						beta = Math.max(beta, v);
					}
				}
			}

		}

		if (root.getSons().size() == 0) {
			for (int i = 0; i < myP.length; i++) {
				if (isWhite) {
					f = new Node(root, ec, mc, posToPawn, posToCell.get(myP[i]), "N", "0");
//					f.setValue(root.getValue() + e.getEuristica(f, myColor));
					root.addSon(f);

					double max = (generateMoveSearchMax(f, alpha, beta, !isWhite, myColor, c, level + 1, maxLevel))
							.getValue(); // valore del figlio
					v = Math.min(v, max);
					if (v == max)
						ret = f;
					root.setValue(v);
		     		if(v <= alpha) return root;
					beta = Math.max(beta, v);
				} else {
					f = new Node(root, mc, ec, posToPawn, posToCell.get(myP[i]), "S", "0");
//					f.setValue(root.getValue() + e.getEuristica(f, myColor));
					root.addSon(f);

					double max = (generateMoveSearchMax(f, alpha, beta, !isWhite, myColor, c, level + 1, maxLevel))
							.getValue(); // valore del figlio
					v = Math.min(v, max);
					if (v == max)
						ret = f;
					root.setValue(v);
		     		if(v <= alpha) return root;
					beta = Math.max(beta, v);
				}
			}
		}

		return ret;

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

	public LinkedList<Node> getLeaves(Node n) {
		LinkedList<Node> ll = new LinkedList<Node>();
		LinkedList<Node> open = new LinkedList<Node>();
		open.add(n);
		while (!open.isEmpty()) {
			Node f = open.removeFirst();
			if (!f.leaf() && f.expandable())
				open.addAll(f.getSons());
			else
				ll.add(f);
		}
		return ll;
	}

	public boolean testTerminazione(Node n, int level, int maxLevel) {
		return level == maxLevel || n.getBc() == 0 || n.getWc() == 0;
	}

}