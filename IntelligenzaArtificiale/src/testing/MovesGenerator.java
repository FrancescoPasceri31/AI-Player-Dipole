package testing;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;

import rappresentazione.Node;
import rappresentazione.Tree;

public class MovesGenerator {

	private HashMap<Byte, String> posToCell = null;
	private HashMap<String, Byte> cellToPos = null;
	private HashMap<Byte, Object[]> masksBlack = null;
	private HashMap<Byte, Object[]> masksWhite = null;
	// private HashMap<Byte, Byte> posToPawn = null;
	private Byte[] posToCol = { 7, 5, 3, 1, 8, 6, 4, 2, 7, 5, 3, 1, 8, 6, 4, 2, 7, 5, 3, 1, 8, 6, 4, 2, 7, 5, 3, 1, 8,
			6, 4, 2 };
	private Tree t = new Tree();
	private Node cur = t.getRoot();

	public void init() throws Exception {

		ObjectInputStream i = new ObjectInputStream(new FileInputStream("hashMaps"));
		posToCell = (HashMap<Byte, String>) i.readObject();
		cellToPos = (HashMap<String, Byte>) i.readObject();
		masksBlack = (HashMap<Byte, Object[]>) i.readObject();
		masksWhite = (HashMap<Byte, Object[]>) i.readObject();
		i.close();

	}

	public ArrayList<Integer> generateMoves(int mc, int ec, HashMap<Byte, Byte> posToPawn, boolean isWhite) {

		byte[] myP = HashMapGenerator2.onesPosition(mc);

		HashMap<Byte, Object[]> masks = null;
		if (isWhite)
			masks = masksWhite;
		else
			masks = masksBlack;

		for (int k = 0; k < myP.length; k++) {

			System.out.println();
			System.out.println("Mia posizione: " + myP[k]);
			System.out.println();

			byte miaPosizione = myP[k];
			byte miaRiga = (byte) (miaPosizione / (byte) 4);

			ArrayList<Integer> ret = new ArrayList<Integer>();

			byte miePedine = posToPawn.get(miaPosizione);

			// int m = HashMapGenerator2.getMask(masksWhite, miaPosizione, miePedine, 0); //
			// maschera mossa posizione 17 in
			// avanti
			// int p = HashMapGenerator2.getMask(masksWhite, miaPosizione, miePedine, 1); //
			// maschera mossa posizione 17
			// all'indietro
			
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
			byte numPedine;
			boolean merge;
			int mcr, ecr;

			for (int j = 0; j < positions.length; j++) {

				mcr = mc;
				ecr = ec;

				posFiglio = (HashMap<Byte, Byte>) posToPawn.clone();

				rigaAvversario = (byte) (positions[j] / 4);
				numPedine = posFiglio.get(positions[j]);
				System.out.println("posizione: " + positions[j] + ", numero pedine: " + numPedine);
				merge = numPedine == 0 || !(isWhite ^ (numPedine > 0 && numPedine <= 12));
				byte numPedineDaSpostare = (byte) Math.abs(miaRiga - rigaAvversario);

				if (!merge && numPedineDaSpostare == 0) // sono sulla stessa riga
					numPedineDaSpostare = (byte) Math.abs(posToCol[miaPosizione] - posToCol[positions[j]]);

				byte n = (byte) (miePedine - numPedineDaSpostare);

				if (!merge) { // sto attaccando
					if (isWhite)
						numPedine -= 20;
					if (numPedineDaSpostare >= numPedine) {// (numPedineDaSpostare==0 &&
															// Math.abs(posToCol[miaPosizione]-posToCol[positions[j]])
															// >= numPedine) //seconda condizione per mangiare in
															// orizzontale
						posFiglio.put(miaPosizione, (byte) (n - (!isWhite && n == 20 ? 20 : 0)));
						posFiglio.put(positions[j], (byte) (isWhite ? numPedineDaSpostare : numPedineDaSpostare + 20));
						System.out.println("attacco: " + posFiglio);

						ecr = ecr ^ (1 << positions[j]);
						mcr = mcr | (1 << positions[j]);

						if (n == 0)
							mcr = mcr ^ (1 << miaPosizione);

						t.createSons();

						// System.out.println(Integer.toBinaryString(mc));
						// System.out.println(Integer.toBinaryString(mcr));
						// System.out.println();

					}
				} else {
					posFiglio.put(miaPosizione, (byte) (n - (!isWhite && n == 20 ? 20 : 0)));
					posFiglio.put(positions[j],
							(byte) (numPedine + numPedineDaSpostare + (!isWhite && numPedine == 0 ? 20 : 0)));
					System.out.println("merge: " + posFiglio);

					mcr = mcr | (1 << positions[j]);

					if (posFiglio.get(miaPosizione) == 0)
						mcr = mcr ^ (1 << miaPosizione);

					t.createSons();

					// System.out.println(Integer.toBinaryString(mc));
					// System.out.println(Integer.toBinaryString(mcr));
					// System.out.println();
				}
			}

			// byte[] direzioni = HashMapGenerator2.getOutLeastPawns(masksWhite,
			// miaPosizione);
			byte[] direzioni = HashMapGenerator2.getOutLeastPawns(masks, miaPosizione);

			posFiglio = (HashMap<Byte, Byte>) posToPawn.clone();

			System.out.println("mie pedine: " + miePedine);

			mcr = mc;

			if (!isWhite)
				miePedine -= 20;

			if (miePedine >= direzioni[0]) { // NW

				byte numPDT = (byte) (miePedine - direzioni[0]); // numero di Pedine Da Togliere

				// genero configurazione in cui tolgo tutto fuori
				mcr = mcr ^ (1 << miaPosizione);

				t.createSons();
				// System.out.println(Integer.toBinaryString(mc));
				// System.out.println(Integer.toBinaryString(mcr));
				// System.out.println();

				posFiglio.put(miaPosizione, (byte) 0);
				System.out.println("tolgo " + miePedine + " NW: " + posFiglio);

				for (; numPDT > 0; numPDT--) {
					posFiglio = (HashMap<Byte, Byte>) posToPawn.clone();
					// genero mosse fuori numPDT nella mia casella
					mcr = mc;
					// System.out.println(Integer.toBinaryString(mc));
					// System.out.println(Integer.toBinaryString(mcr));
					// System.out.println();
					posFiglio.put(miaPosizione, (byte) (numPDT + (!isWhite ? 20 : 0)));
					System.out.println("tolgo " + (miePedine - numPDT) + " NW: " + posFiglio);
					t.createSons();
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
				t.createSons();

				posFiglio.put(miaPosizione, (byte) 0);
				System.out.println("tolgo " + miePedine + " NE: " + posFiglio);

				for (; numPDT > 0; numPDT--) {
					posFiglio = (HashMap<Byte, Byte>) posToPawn.clone();
					// genero mosse fuori numPDT nella mia casella
					mcr = mc;
					// System.out.println(Integer.toBinaryString(mc));
					// System.out.println(Integer.toBinaryString(mcr));
					// System.out.println();
					posFiglio.put(miaPosizione, (byte) (numPDT + (!isWhite ? 20 : 0)));
					System.out.println("tolgo " + (miePedine - numPDT) + " NE: " + posFiglio);
					t.createSons();
				}
			}

		}
		return null;

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

	public void printTree() {
		System.out.println(t);
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

		MovesGenerator mg = new MovesGenerator();
		mg.init();

		int mc = mg.createConfig(posToPawn, false);
		int ec = mg.createConfig(posToPawn, true);

		long t = System.currentTimeMillis();
		mg.generateMoves(mc, ec, posToPawn, false);
		// mg.printTree();

		System.out.println(System.currentTimeMillis() - t);

	}
}