package testing;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map.Entry;

public class Hash {

	public static void main(String[] args) throws Exception, IOException {
		HashMap<Byte, String> posToCell = null;
		HashMap<String, Byte> cellToPos = null;
		HashMap<Byte, Object[]> masksBlack = null;
		HashMap<Byte, Object[]> masksWhite = null;
		
		ObjectInputStream i = new ObjectInputStream(new FileInputStream("hashMaps"));
		long t = System.currentTimeMillis();
		posToCell = (HashMap<Byte, String>) i.readObject();
		cellToPos = (HashMap<String, Byte>) i.readObject();
		masksBlack = (HashMap<Byte, Object[]>) i.readObject();
		masksWhite = (HashMap<Byte, Object[]>) i.readObject();
		i.close();
		
			System.out.println(Integer.toBinaryString(((HashMap<Byte,Integer[]>)masksBlack.get((byte)0)[3]).get((byte)21)[1]));
	}

}
