package rappresentazione;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;

import generators.MovesGenerator;

public class MainSerializzazioneVsGenerazione {

	public static void main(String[] args) throws Exception {

		/*
		 ********************************************************************************************************************************* 
		 * SETTING UP PARAMETRI INIZIALI
		 *********************************************************************************************************************************
		 */

		HashMap<Byte, Byte> posToPawn = new HashMap<Byte, Byte>();
		for (int i = 0; i < 32; i++) {
			if (i == 1) // white start position
				posToPawn.put((byte) i, (byte) 12);
			else if (i == 30) // black start position
				posToPawn.put((byte) i, (byte) 32);
			else
				posToPawn.put((byte) i, (byte) 0);
		}

		byte[] posToPawn2 = new byte[32];
		for (int i = 0; i < 32; i++) {
			if (i == 1) // white start position
				posToPawn2[i] = (byte) 12;
			else if (i == 30) // black start position
				posToPawn2[i] = (byte) 32;
			else
				posToPawn2[i] = (byte) 0;
		}

		MovesGenerator mg = new MovesGenerator();
		mg.init();

		int bc = mg.createConfig(posToPawn2, false);
		int wc = mg.createConfig(posToPawn2, true);

		File dir = new File("./Nodes/");
/*
		File[] arr = dir.listFiles();
		for (int i = 0; i < arr.length; i++) {
			arr[i].delete();
		}
*/
		/*
		 ********************************************************************************************************************************* 
		 * GENERAZIONE ALBERO MOSSE
		 *********************************************************************************************************************************
		 */

		Node root, r, curr;
		ObjectOutputStream oos;
		ObjectInputStream ois;

		boolean isWhite = true;
		int livelloMax = 5;
/*
		root = new Node(null, bc, wc, posToPawn2, ",0");
		mg.generateMovesRecursive(mg, root, isWhite, 0, livelloMax);

		// SERIALIZZO L'ALBERO INIZIALE

		File f = new File("./Nodes/root.node");
		f.createNewFile();
		oos = new ObjectOutputStream(new FileOutputStream(f));
		oos.writeObject(root);
		oos.close();

//		mg.metodoStampaTree(root, 6);

		// ******************* WARM UP TIME

		ois = new ObjectInputStream(new FileInputStream(f));
		r = (Node) ois.readObject();
		ois.close();

		System.out.println("First tree depth " + livelloMax + " from radix ok.");
		// mg.metodoStampaTree(root, 6);

		// ******************* EXPAND TIME
		curr = r;
		LinkedList<Node> leaves = getLeaves(curr);
		int depthIncludingCurrRoot = 2;
		expandLeaves(leaves, mg, depthIncludingCurrRoot, livelloMax);
		System.out.println("All leaves are expanded by other " + depthIncludingCurrRoot + " levels.");

		// ** SERIALIZZO I NODI FOGLIA PRECEDENTI

		for (Node prev_leaf : leaves) {
			f = new File("./Nodes/" + prev_leaf.getId() + ".node");
			oos = new ObjectOutputStream(new FileOutputStream(f));
			prev_leaf.setParent(null);
			LinkedList<Node> sonsTMP = prev_leaf.getSons();
			for (Node son : sonsTMP) {
				son.setParent(null);
			}
			oos.writeObject(sonsTMP);
			oos.close();
		}
		System.out.println("All expanded leaves are serialized");
*/
		// ******************** DESERIALIZZO SIMULANDO WARM UP E FASE SUCCESSIVA
		r = null;
		curr = null;
		root = null;

		// DESERIALIZZO
		LocalDateTime t1 = LocalDateTime.now();
		ois = new ObjectInputStream(new FileInputStream("./Nodes/root.node"));
		r = (Node) ois.readObject();
		ois.close();
		LocalDateTime t2 = LocalDateTime.now();
		LinkedList<Node> leavesTMP = getLeaves(r);
		for (Node leaf : leavesTMP) {
			ois = new ObjectInputStream(new FileInputStream("./Nodes/" + leaf.getId() + ".node"));
			LinkedList<Node> tmp = (LinkedList<Node>) ois.readObject();
			for (Node n : tmp)
				n.setParent(leaf);
			leaf.setSons(tmp);
			ois.close();
		}
		LocalDateTime t3 = LocalDateTime.now();
		System.out.println("Deserializzato root (" + t1.getSecond() + "." + t1.getNano() + " -> " + t2.getSecond() + "."
				+ t2.getNano() + ") e foglie (" + t2.getSecond() + "." + t2.getNano() + " -> " + t3.getSecond() + "."
				+ t3.getNano() + ")");
		// mg.metodoStampaTree(r, 10);

		// GENERO AL MOMENTO
		LinkedList<Node> ll = null;
		t1 = LocalDateTime.now();
		root = new Node(null, bc, wc, posToPawn2, ",0");
		mg.generateMovesRecursive(mg, root, isWhite, 0, livelloMax);
		t2 = LocalDateTime.now();
		ll = getLeaves(root);
		for (Node l : ll) {
			mg.generateMovesRecursive(mg, l, livelloMax % 2 != 0, 0, 2);
		}
		t3 = LocalDateTime.now();
		System.out.println("Deserializzato root (" + t1.getSecond() + "." + t1.getNano() + " -> " + t2.getSecond() + "."
				+ t2.getNano() + ") e foglie (" + t2.getSecond() + "." + t2.getNano() + " -> " + t3.getSecond() + "."
				+ t3.getNano() + ")");

	}

	private static void expandLeaves(LinkedList<Node> leaves, MovesGenerator mg, int depth, int depthPrev)
			throws Exception {
		boolean asWhite = depthPrev % 2 != 0;
		for (int i = 0; i < leaves.size(); i++) {
			Node tmp = leaves.get(i);
			mg.generateMovesRecursive(mg, tmp, asWhite, 0, depth);
		}
	}

	private static LinkedList<Node> getLeaves(Node n) {
		LinkedList<Node> ll = new LinkedList<Node>();
		LinkedList<Node> open = new LinkedList<Node>();
		open.add(n);
		while (!open.isEmpty()) {
			Node f = open.removeFirst();
			if (f.getSons().size() > 0)
				open.addAll(f.getSons());
			else
				ll.add(f);
		}
		return ll;
	}

}
