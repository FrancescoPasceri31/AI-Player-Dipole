package testing;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class MovesGenerator {

	public static void main(String[] args) throws Exception, IOException {

		HashMap<Byte, String> posToCell = null;
		HashMap<String, Byte> cellToPos = null;
		Double tempi = 0.0;
		HashMap<Byte, Object[]> masksBlack = null;
		HashMap<Byte, Object[]> masksWhite = null;
		HashMap<Byte, Byte> posToPawn = new HashMap();
		int run = 10000;

//		for (int j = 0; j < run; j++) {
//			ObjectInputStream i = new ObjectInputStream(new FileInputStream("hashMap"));
//			long t = System.currentTimeMillis();
//			posToCell = (HashMap<Byte, String>) i.readObject();
//			cellToPos = (HashMap<String, Byte>) i.readObject();
//			masksBlack = (HashMap<Byte, Object[]>) i.readObject();
//			masksWhite = (HashMap<Byte, Object[]>) i.readObject();
//			i.close();
//			tempi += ((System.currentTimeMillis() - t) / 1000.0);
//		}

		for (int i = 0; i < 32; i++) {
			if (i == 1) // white start position
				posToPawn.put((byte) i, (byte) 12);
			else if (i == 30) // black start position
				posToPawn.put((byte) i, (byte) 32);
			else
				posToPawn.put((byte) i, (byte) 0);
		}

		posToPawn.put((byte) 1, (byte) 0);
		posToPawn.put((byte) 30, (byte) 0);
		posToPawn.put((byte) 17, (byte) 6);
		posToPawn.put((byte) 9, (byte) 21);
		posToPawn.put((byte) 24, (byte) 21);
		posToPawn.put((byte) 10, (byte) 1);
		posToPawn.put((byte) 26, (byte) 1);

		ObjectInputStream i = new ObjectInputStream(new FileInputStream("hashMaps"));
		long t = System.currentTimeMillis();
		posToCell = (HashMap<Byte, String>) i.readObject();
		cellToPos = (HashMap<String, Byte>) i.readObject();
		masksBlack = (HashMap<Byte, Object[]>) i.readObject();
		masksWhite = (HashMap<Byte, Object[]>) i.readObject();
		i.close();

//		System.out.println(tempi / run);

		long mc = Long.parseUnsignedLong("00000100000000100000010000000000", 2); // posizione 17
		long ec = Long.parseUnsignedLong("00000001000000000000001000000000", 2);
		
		int miaPosizione = 17;
		int miaRiga = miaPosizione / 4;

		ArrayList<Long> ret = new ArrayList();

		int miePedine = posToPawn.get((byte)miaPosizione);

		long m = HashMapGenerator2.getMask(masksWhite, miaPosizione, miePedine, 0); // maschera mossa posizione 17 in
																					// avanti
		long p = HashMapGenerator2.getMask(masksWhite, miaPosizione, miePedine, 1); // maschera mossa posizione 17
																					// all'indietro

		long r = m & (p | (~ec));

		int[] positions = HashMapGenerator2.zerosPosition(r);

		 //System.out.println(Arrays.toString(positions));
		//System.out.println(posToPawn.get((byte)9));
		 

		boolean white = true;
		
		HashMap<Byte, Byte> posFiglio;
		int rigaAvversario;
		int numPedine;
		boolean merge;

		for (int j = 0; j < positions.length; j++) {

			posFiglio = (HashMap<Byte,Byte>)posToPawn.clone();

			rigaAvversario = positions[j] / 4;
			numPedine = posFiglio.get((byte) positions[j]);
			System.out.println("posizione: "+positions[j]+", numero pedine: "+numPedine);
			merge = numPedine == 0 || !(white ^ (numPedine > 0 && numPedine <= 12));
			int numPedineDaSpostare = Math.abs(miaRiga - rigaAvversario);

			if (!merge) { // sto attaccando
				numPedine -=20;
				if (miePedine >= numPedine) {
					posFiglio.put((byte) miaPosizione, (byte) (miePedine - numPedineDaSpostare));
					posFiglio.put((byte) positions[j], (byte) (white ? numPedineDaSpostare : numPedineDaSpostare + 20));
					System.out.println("attacco: "+posFiglio);
					System.out.println();
					// genero mossa
				}
			} else {
				posFiglio.put((byte) miaPosizione, (byte) (miePedine - numPedineDaSpostare));
				posFiglio.put((byte) positions[j], (byte) (numPedine + numPedineDaSpostare));
				System.out.println("merge: "+posFiglio);
				System.out.println();
				// genero mossa
			}
		}

		byte[] direzioni = HashMapGenerator2.getOutLeastPawns(masksWhite, miaPosizione);

		posFiglio = (HashMap<Byte,Byte>)posToPawn.clone();
		
		System.out.println("mie pedine: "+miePedine);

		if (miePedine >= direzioni[0]) { // NW

			int numPDT = miePedine - direzioni[0]; // numero di Pedine Da Togliere
			// genero configurazione in cui tolgo tutto fuori
			posFiglio.put((byte) miaPosizione, (byte) 0);
			System.out.println("tolgo "+miePedine+" NW: "+posFiglio);

			for (; numPDT > 0; numPDT--) {
				posFiglio = (HashMap<Byte,Byte>)posToPawn.clone();
				// genero mosse fuori numPDT nella mia casella
				posFiglio.put((byte) miaPosizione, (byte) numPDT);
				System.out.println("tolgo "+(miePedine-numPDT)+" NW: "+posFiglio);
			}
		}
		

		if (miePedine >= direzioni[1]) { // NE

			int numPDT = miePedine - direzioni[1]; // numero di Pedine Da Togliere
			// genero configurazione in cui tolgo tutto fuori
			posFiglio.put((byte) miaPosizione, (byte) 0);
			System.out.println("tolgo "+miePedine+" NE: "+posFiglio);

			for (; numPDT > 0; numPDT--) {
				posFiglio = (HashMap<Byte,Byte>)posToPawn.clone();
				// genero mosse fuori numPDT nella mia casella
				posFiglio.put((byte) miaPosizione, (byte) numPDT);
				System.out.println("tolgo "+(miePedine-numPDT)+" NE: "+posFiglio);
			}
		}

	}

//	public ArrayList<Long> generateMoves(long mc,long ec, int pos, int ){
//		
//	}

}
