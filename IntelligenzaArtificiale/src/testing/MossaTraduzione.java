package testing;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import generators.MovesGenerator;
import rappresentazione.Node;

public class MossaTraduzione {

	public static void main(String[] args) throws Exception {
		String mossa = "H5,N,6";

		Node root;
		int bc, wc;
		Node fin1;
		NodeM fin2;
		NodeM root2;

		// genero posizioni con mosse
		byte[] posToPawn2 = new byte[32];
		for (int i = 0; i < 32; i++) {
			if (i == 1) // white start position
				posToPawn2[i] = (byte) 12;
			else if (i == 30) // black start position
				posToPawn2[i] = (byte) 32;
			else
				posToPawn2[i] = (byte) 0;
		}

		// genero configurazioni
		MovesGenerator mg = new MovesGenerator();
		mg.init();
		bc = mg.createConfig(posToPawn2, false);
		wc = mg.createConfig(posToPawn2, true);
		root = new Node(null, bc, wc, posToPawn2, "");
		root2 = new NodeM(null, bc, wc, posToPawn2, "");
		mg.generateMoves(root, true);

		// --------------------------------------------------------------------

		// ricerca lineare
		long rlStart = System.nanoTime();
		for (Node son : root.getSons()) {
			if (mossa.equals(son.getMossa())) {
				fin1 = son;
				break;
			}
		}
//		LocalDateTime rlEnd = LocalDateTime.now();
//		rlEnd = rlEnd.minusSeconds(rlStart.getSecond());
//		rlEnd = rlEnd.minusNanos(rlStart.getNano());
		long rlEnd = System.nanoTime() - rlStart;

		// --------------------------------------------------------------------

		// preparazione ricerca strana
		Map<String, NodeM> sons = new HashMap<String, NodeM>();
		for (Node son : root.getSons()) {
			sons.put(son.getMossa(), new NodeM(root2, son.getBc(), son.getWc(), son.getPosToPawns(), son.getMossa()));
		}

		// ricerca strana
//		LocalDateTime rsStart = LocalDateTime.now();
		long rsStart = System.nanoTime();
		fin2 = sons.get(mossa);
//		LocalDateTime rsEnd = LocalDateTime.now();
//		rsEnd = rsEnd.minusSeconds(rsStart.getSecond());
//		rsEnd = rsEnd.minusNanos(rsStart.getNano());
		long rsEnd = System.nanoTime() - rsStart;

		// stampe
//		System.out.println("Ricerca lineare -> " + rlEnd.getSecond() + "." + rlEnd.getNano());
//		System.out.println("Sons in map -> " + rsEnd.getSecond() + "." + rsEnd.getNano());
		System.out.println("RLi : " + rlEnd);
		System.out.println("MAP : " + rsEnd);

		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("LinkList"));
		ObjectOutputStream oosM = new ObjectOutputStream(new FileOutputStream("Map"));

		oos.writeObject(root);
		oosM.writeObject(root2);

		oos.close();
		oosM.close();
		
	}

}