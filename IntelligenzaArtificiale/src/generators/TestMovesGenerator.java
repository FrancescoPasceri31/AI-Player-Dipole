package generators;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.HashMap;

import euristica.Euristica;
import rappresentazione.Node;

public class TestMovesGenerator {

	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream("hashMaps"));
		HashMap<Byte, String> posToCell = (HashMap<Byte, String>) ois.readObject();
		HashMap<String, Byte> cellToPos = (HashMap<String, Byte>) ois.readObject();
		HashMap<Byte, Object[]> masksBlack = (HashMap<Byte, Object[]>) ois.readObject();
		HashMap<Byte, Object[]> masksWhite = (HashMap<Byte, Object[]>) ois.readObject();
		HashMap<Byte, HashMap<Byte, String>> posToDir = (HashMap<Byte, HashMap<Byte, String>>) ois.readObject();
		ois.close();
		
		Euristica e = new Euristica();
		e.init(cellToPos, masksBlack, masksWhite);
		MovesGenerator mg = new MovesGenerator();
		mg.init(e, posToCell, cellToPos, masksBlack, masksWhite, posToDir);
		
		byte[] posToPawn = new byte[32];
		for(int i=0; i<32; i++) {
			if(i == 30) posToPawn[i] = 23;
			if(i == 27) posToPawn[i] = 2;
			if(i == 25) posToPawn[i] = 3;
			if(i == 23) posToPawn[i] = 24;
			if(i == 22) posToPawn[i] = 24;
			if(i == 19) posToPawn[i] = 4;
			if(i == 1) posToPawn[i] = 3;
			
		}
		int wc = mg.createConfig(posToPawn, true);
		int bc = mg.createConfig(posToPawn, false);
		Node n = new Node(null, bc, wc, posToPawn, "", "", "0");
		
		mg.generateMoves(n, false, false);
		
		for(Node son : n.getSons()) {
			if(son.getMossa().equals("C2,SE,2")) {
				System.out.println(Arrays.toString(n.getPosToPawns()));
				System.out.println(n.getMossa()+"-->"+son.getMossa());
				System.out.println(Arrays.toString(son.getPosToPawns()));
				System.out.println();
			}
		}
	}
	
}
