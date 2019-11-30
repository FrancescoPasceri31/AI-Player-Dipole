package generators;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.LinkedList;

import rappresentazione.Node;
import rappresentazione.NodeToFile;

public class MovesGeneratorToFile {
	
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

	public LinkedList<NodeToFile> generateMoves(NodeToFile root, boolean isWhite) {// (int mc, int ec, HashMap<Byte, Byte> posToPawn, boolean
															// isWhite) {
		LinkedList<NodeToFile> ret = new LinkedList<NodeToFile>();
		
		int mc;
		int ec;

		if (isWhite) {
			mc = root.getWc();
			ec = root.getBc();
		} else {
			mc = root.getBc();
			ec = root.getWc();
		}
		
		if(mc==0 || ec == 0) return ret;

		byte[] posToPawn = root.getPosToPawns();
//		System.out.println(isWhite+" -> mc : "+ Integer.toBinaryString(mc)+" | ec : " + Integer.toBinaryString(ec)  );
//		System.out.println(posToPawn);
		byte[] myP = HashMapGenerator.onesPosition(mc);

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

			int[] msk = HashMapGenerator.getMask(masks, miaPosizione, miePedine);
			int m = msk[0];
			// avanti
			int p = msk[1];
			int r = m & (p | (~ec));

			byte[] positions = HashMapGenerator.zerosPosition(r);

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
							NodeToFile x = new NodeToFile(root.getId(), ecr, mcr, posFiglio, posToCell.get(miaPosizione) + ","
									+ (posToDir.get(miaPosizione)).get(positions[j]) + "," + numPedineDaSpostare);
							ret.add(x);
							root.addSon(x.getId());
						} else { // sono nero
							NodeToFile x = new NodeToFile(root.getId(), mcr, ecr, posFiglio, posToCell.get(miaPosizione) + ","
									+ (posToDir.get(miaPosizione)).get(positions[j]) + "," + numPedineDaSpostare);
							ret.add(x);
							root.addSon(x.getId());
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
						NodeToFile x = new NodeToFile(root.getId(), ecr, mcr, posFiglio, posToCell.get(miaPosizione) + ","
								+ (posToDir.get(miaPosizione)).get(positions[j]) + "," + numPedineDaSpostare);
						ret.add(x);
						root.addSon(x.getId());
					} else { // sono nero
						NodeToFile x = new NodeToFile(root.getId(), mcr, ecr, posFiglio, posToCell.get(miaPosizione) + ","
								+ (posToDir.get(miaPosizione)).get(positions[j]) + "," + numPedineDaSpostare);
						ret.add(x);
						root.addSon(x.getId());
					}

					// System.out.println(Integer.toBinaryString(mc));
					// System.out.println(Integer.toBinaryString(mcr));
					// System.out.println();
				}
			}

			byte[] direzioni = HashMapGenerator.getOutLeastPawns(masks, miaPosizione);

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
					NodeToFile x = new NodeToFile(root.getId(), ec, mcr, posFiglio, posToCell.get(miaPosizione) + ",NW," + (miePedine));
					ret.add(x);
					root.addSon(x.getId());
				} else { // sono nero
					NodeToFile x = new NodeToFile(root.getId(), mcr, ec, posFiglio, posToCell.get(miaPosizione) + ",SE," + (miePedine));
					ret.add(x);
					root.addSon(x.getId());
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
						NodeToFile x = new NodeToFile(root.getId(), ec, mcr, posFiglio,posToCell.get(miaPosizione) + ",NW," + numMinimoPDT);
						ret.add(x);
						root.addSon(x.getId());
					} else { // sono nero
						NodeToFile x = new NodeToFile(root.getId(), mcr, ec, posFiglio,posToCell.get(miaPosizione) + ",SE," + numMinimoPDT);
						ret.add(x);
						root.addSon(x.getId());
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
					NodeToFile x = new NodeToFile(root.getId(), ec, mcr, posFiglio, posToCell.get(miaPosizione) + ",NE," + (miePedine));
					ret.add(x);
					root.addSon(x.getId());
				} else { // sono nero
					NodeToFile x = new NodeToFile(root.getId(), mcr, ec, posFiglio, posToCell.get(miaPosizione) + ",SW," + (miePedine));
					ret.add(x);
					root.addSon(x.getId());
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
						NodeToFile x = new NodeToFile(root.getId(), ec, mcr, posFiglio,
								posToCell.get(miaPosizione) + ",NE," + numMinimoPDT);
						ret.add(x);
						root.addSon(x.getId());
					} else { // sono nero
						NodeToFile x = new NodeToFile(root.getId(), mcr, ec, posFiglio,
								posToCell.get(miaPosizione) + ",SW," + numMinimoPDT);
						ret.add(x);
						root.addSon(x.getId());
					}
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


	public void generateMovesRecursive(MovesGeneratorToFile mg, NodeToFile n, boolean isWhite, int liv, int limite)
			throws IOException {
		if (liv == limite || n == null) {
			return;
		}
		
		LinkedList<NodeToFile> sons = generateMoves(n, isWhite);
		
		File f = new File("./Nodes/"+n.getId());
		f.createNewFile();
		ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(f));
		o.writeObject(n);
		o.close();

		for (NodeToFile son : sons) {
			generateMovesRecursive(mg, son, !isWhite, liv + 1, limite);
		}
	}
	
	public Node reconstruct(NodeToFile n,Node parent,int liv, int limite){
		if (liv == limite || n == null) {
			return null;
		}
		
		Node ret = new Node(parent,n.getBc(),n.getWc(),n.getPosToPawns(),n.getMossa());

		for (Integer s : n.getSons()) {
			ObjectInputStream i;
			NodeToFile son = null;
			try {
				i = new ObjectInputStream(new FileInputStream("./Nodes/"+s));
				son = (NodeToFile) i.readObject();
				i.close();
			} catch (Exception e) {
				break;
			}
			ret.addSon(reconstruct(son,ret,liv+1,limite));
		}
		return ret;
	}

}
