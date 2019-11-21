package testing;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;

import rappresentazione.Node;
import ricerca.Search;

public class MovesGenerator {

	private HashMap<Byte, String> posToCell = null;
	private HashMap<Byte, HashMap<Byte,String>> posToDir = null;
	private HashMap<String, Byte> cellToPos = null;
	private HashMap<Byte, Object[]> masksBlack = null;
	private HashMap<Byte, Object[]> masksWhite = null;
	// private HashMap<Byte, Byte> posToPawn = null;
	private Byte[] posToCol = { 7, 5, 3, 1, 8, 6, 4, 2, 7, 5, 3, 1, 8, 6, 4, 2, 7, 5, 3, 1, 8, 6, 4, 2, 7, 5, 3, 1, 8,
			6, 4, 2 };


	public void init() throws Exception {

		ObjectInputStream i = new ObjectInputStream(new FileInputStream("hashMaps"));
		posToCell = (HashMap<Byte, String>) i.readObject();
		cellToPos = (HashMap<String, Byte>) i.readObject();
		masksBlack = (HashMap<Byte, Object[]>) i.readObject();
		masksWhite = (HashMap<Byte, Object[]>) i.readObject();
		posToDir = (HashMap<Byte, HashMap<Byte, String>>) i.readObject();
		i.close();

	}

	public void generateMoves(Node root,boolean isWhite) {//(int mc, int ec, HashMap<Byte, Byte> posToPawn, boolean isWhite) {

		//Node root = new Node(null);
		int mc;
		int ec;
		
		if(isWhite) {
			mc = root.getWc();
			ec = root.getBc();
		}else {
			mc = root.getBc();
			ec = root.getWc();
		}
		
		HashMap<Byte, Byte> posToPawn = root.getPosToPawns();
//		System.out.println(isWhite+" -> mc : "+ Integer.toBinaryString(mc)+" | ec : " + Integer.toBinaryString(ec)  );
//		System.out.println(posToPawn);

		byte[] myP = HashMapGenerator2.onesPosition(mc);

		HashMap<Byte, Object[]> masks = null;
		masks = isWhite? masksWhite : masksBlack;
		
//		System.out.println(isWhite);
//		HashMapGenerator2.printHash(masks);
	
		/* INIZIO CALCOLO MOSSA */
		for (int k = 0; k < myP.length; k++) {
			
			byte miaPosizione = myP[k];
			
			byte miaRiga = (byte) (miaPosizione / (byte) 4);

			ArrayList<Integer> ret = new ArrayList<Integer>();

			byte miePedine = posToPawn.get(miaPosizione);
			
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

			HashMap<Byte, Byte> posFiglio;
			byte rigaAvversario;
			byte numPedineDestinazione;
			boolean merge;
			int mcr, ecr;

			for (int j = 0; j < positions.length; j++) {

				mcr = mc;
				ecr = ec;

				posFiglio = (HashMap<Byte, Byte>) posToPawn.clone();

				rigaAvversario = (byte) (positions[j] / 4);
				numPedineDestinazione = posFiglio.get(positions[j]);
				//System.out.println("posizione: " + positions[j] + ", numero pedine: " + numPedine);
				merge = numPedineDestinazione == 0 || !(isWhite ^ (numPedineDestinazione > 0 && numPedineDestinazione <= 12));
				byte numPedineDaSpostare = (byte) Math.abs(miaRiga - rigaAvversario);

				if (!merge && numPedineDaSpostare == 0) // sono sulla stessa riga
					numPedineDaSpostare = (byte) Math.abs(posToCol[miaPosizione] - posToCol[positions[j]]);

				byte n = (byte) (miePedine - numPedineDaSpostare);

				if (!merge) { // sto attaccando
					if (isWhite)
						numPedineDestinazione -= 20;
					if (numPedineDaSpostare >= numPedineDestinazione) {
						posFiglio.put(miaPosizione, (byte) (n - (!isWhite && n == 20 ? 20 : 0)));
						posFiglio.put(positions[j], (byte) (isWhite ? numPedineDaSpostare : numPedineDaSpostare + 20));
						//System.out.println("attacco: " + posFiglio);

						ecr = ecr ^ (1 << positions[j]);
						mcr = mcr | (1 << positions[j]);

						if (n == 0)
							mcr = mcr ^ (1 << miaPosizione);

						if(isWhite) {
							root.addSon(new Node(root,ecr,mcr,posFiglio, posToCell.get(miaPosizione)+","+(posToDir.get(miaPosizione)).get(positions[j])+","+numPedineDaSpostare));
						}else {	// sono nero
							root.addSon(new Node(root,mcr,ecr,posFiglio, posToCell.get(miaPosizione)+","+(posToDir.get(miaPosizione)).get(positions[j])+","+numPedineDaSpostare));
						}
						

						// System.out.println(Integer.toBinaryString(mc));
						// System.out.println(Integer.toBinaryString(mcr));
						// System.out.println();

					}
				} else {
					posFiglio.put(miaPosizione, (byte) (n - (!isWhite && n == 20 ? 20 : 0)));
					posFiglio.put(positions[j],
							(byte) (numPedineDestinazione + numPedineDaSpostare + (!isWhite && numPedineDestinazione == 0 ? 20 : 0)));
					//System.out.println("merge: " + posFiglio);

					mcr = mcr | (1 << positions[j]);

					if (posFiglio.get(miaPosizione) == 0)
						mcr = mcr ^ (1 << miaPosizione);

					if(isWhite) {
						root.addSon(new Node(root,ecr,mcr,posFiglio, posToCell.get(miaPosizione)+","+(posToDir.get(miaPosizione)).get(positions[j])+","+numPedineDaSpostare));
					}else {	// sono nero
						root.addSon(new Node(root,mcr,ecr,posFiglio,posToCell.get(miaPosizione)+","+(posToDir.get(miaPosizione)).get(positions[j])+","+numPedineDaSpostare));
					}

					// System.out.println(Integer.toBinaryString(mc));
					// System.out.println(Integer.toBinaryString(mcr));
					// System.out.println();
				}
			}

			byte[] direzioni = HashMapGenerator2.getOutLeastPawns(masks, miaPosizione);

			posFiglio = (HashMap<Byte, Byte>) posToPawn.clone();

			//System.out.println("mie pedine: " + miePedine);

			mcr = mc;

			if (!isWhite)
				miePedine -= 20;

			if (miePedine >= direzioni[0]) { // NW - SE

				byte numPDT = (byte) (miePedine - direzioni[0]); // numero di Pedine Da Togliere

				// genero configurazione in cui tolgo tutto fuori
				mcr = mcr ^ (1 << miaPosizione);

				if(isWhite) {
					root.addSon(new Node(root,ec,mcr,posFiglio, posToCell.get(miaPosizione)+",NW,"+numPDT));
				}else {	// sono nero
					root.addSon(new Node(root,mcr,ec,posFiglio, posToCell.get(miaPosizione)+",SE,"+numPDT));
				}
				
				// System.out.println(Integer.toBinaryString(mc));
				// System.out.println(Integer.toBinaryString(mcr));
				// System.out.println();

				posFiglio.put(miaPosizione, (byte) 0);
				//System.out.println("tolgo " + miePedine + " NW: " + posFiglio);

				for (; numPDT > 0; numPDT--) {
					posFiglio = (HashMap<Byte, Byte>) posToPawn.clone();
					// genero mosse fuori numPDT nella mia casella
					mcr = mc;
					// System.out.println(Integer.toBinaryString(mc));
					// System.out.println(Integer.toBinaryString(mcr));
					// System.out.println();
					posFiglio.put(miaPosizione, (byte) (numPDT + (!isWhite ? 20 : 0)));
					//System.out.println("tolgo " + (miePedine - numPDT) + " NW: " + posFiglio);
					
					if(isWhite) {
						root.addSon(new Node(root,ec,mcr,posFiglio, posToCell.get(miaPosizione)+",NW,"+numPDT));
					}else {	// sono nero
						root.addSon(new Node(root,mcr,ec,posFiglio, posToCell.get(miaPosizione)+",SE,"+numPDT));
					}
					
				}

			}

			mcr = mc;

			if (miePedine >= direzioni[1]) { // NE

				byte numPDT = (byte) (miePedine - direzioni[1]); // numero di Pedine Da Togliere

				// genero configurazione in cui tolgo tutto fuori
				mcr = mcr ^ (1 << miaPosizione);
				// System.out.println(Integer.toBinaryString(mc));
				// System.out.println(Integer.toBinaryString(mcr));
				// System.out.println();
				if(isWhite) {
					root.addSon(new Node(root,ec,mcr,posFiglio, posToCell.get(miaPosizione)+",NE,"+numPDT));
				}else {	// sono nero
					root.addSon(new Node(root,mcr,ec,posFiglio, posToCell.get(miaPosizione)+",SW,"+numPDT));
				}

				posFiglio.put(miaPosizione, (byte) 0);
				//System.out.println("tolgo " + miePedine + " NE: " + posFiglio);

				for (; numPDT > 0; numPDT--) {
					posFiglio = (HashMap<Byte, Byte>) posToPawn.clone();
					// genero mosse fuori numPDT nella mia casella
					mcr = mc;
					// System.out.println(Integer.toBinaryString(mc));
					// System.out.println(Integer.toBinaryString(mcr));
					// System.out.println();
					posFiglio.put(miaPosizione, (byte) (numPDT + (!isWhite ? 20 : 0)));
					//System.out.println("tolgo " + (miePedine - numPDT) + " NE: " + posFiglio);
					
					if(isWhite) {
						root.addSon(new Node(root,ec,mcr,posFiglio, posToCell.get(miaPosizione)+",NE,"+numPDT));
					}else {	// sono nero
						root.addSon(new Node(root,mcr,ec,posFiglio, posToCell.get(miaPosizione)+",SW,"+numPDT));
					}
				}
			}

		}

	}

	public int createConfig(HashMap<Byte, Byte> posToPawn, boolean isWhite) {
		int conf = 0;
		byte nP;
		for (Byte position : posToPawn.keySet()) {
			nP = posToPawn.get(position);
			if ((isWhite && nP <= 12 && nP > 0) || (!isWhite && nP > 12)) {
				conf |= (1 << position);
			}
		}
		return conf;

	}

	public static void main(String[] args) throws Exception, IOException {
		

		HashMap<Byte, Byte> posToPawn = new HashMap<Byte, Byte>();
		for (int i = 0; i < 32; i++) {
			if (i == 1) // white start position
				posToPawn.put((byte) i, (byte) 12);
			else if (i == 30) // black start position
				posToPawn.put((byte) i, (byte) 32);
			else
				posToPawn.put((byte) i, (byte) 0);
		}
		
		long t = System.currentTimeMillis();

		MovesGenerator mg = new MovesGenerator();
		mg.init();

		int bc = mg.createConfig(posToPawn, false);
		int wc = mg.createConfig(posToPawn, true);

		Node root = new Node(null, bc, wc, posToPawn,"");
		
		generateMovesRecursive(mg, root, false, 0, 3);
		
/*		mg.generateMoves(root, false);
//		System.out.println(root.getSons().size());
		
		for(Node n: root.getSons()) {
			mg.generateMoves(n, true);
		}
*/		
//		System.out.println("num nodi tot: "+root.getSize());
		Search s = new Search();
		Node ret = s.search(root);
//		System.out.println((System.currentTimeMillis() - t)/1000.0);
//		System.out.println(ret.getId());
//		System.out.println(ret.hasValue());
//		System.out.println(ret.getValue());
		System.out.println(ret.getMossa());
//		
	}
	
	private static void generateMovesRecursive(MovesGenerator mg ,Node n, boolean isWhite, int liv, int limite) {
		if(liv==limite) return;
		mg.generateMoves(n, isWhite);
		System.out.println("Generato "+n.getId()+" livello "+liv+" <= limite "+limite);
		System.out.println("Pedine "+n.getPosToPawns());
		for(Node son : n.getSons()) {
			generateMovesRecursive(mg, son, !isWhite, liv+1, limite);
		}
	}
	
	
}