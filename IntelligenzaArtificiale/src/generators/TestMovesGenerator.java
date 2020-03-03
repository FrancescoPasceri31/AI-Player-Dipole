package generators;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
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
			if(i == 31) posToPawn[i] = 1;
			if(i == 25) posToPawn[i] = 6;
			if(i == 23) posToPawn[i] = 24;
			if(i == 22) posToPawn[i] = 22;
			if(i == 14) posToPawn[i] = 24;
			if(i == 10) posToPawn[i] = 4;
		}
		int wc = mg.createConfig(posToPawn, true);
		int bc = mg.createConfig(posToPawn, false);
		Node n = new Node(null, bc, wc, posToPawn, "", "", "0");
		
		mg.generateMoves(n, true, true);
		
		System.out.println(n.getSons());
	}
	
}
