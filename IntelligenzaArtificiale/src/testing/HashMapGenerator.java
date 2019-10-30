package testing;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class HashMapGenerator {

	public static void main(String[] args) {

		final String a = "ABCDEFGH";

		HashMap<Byte, String> posToCell = new HashMap();
		HashMap<String, Byte> cellToPos = new HashMap();

		int x = 0;
		int y = 2;

		for (byte i = 7; i >= 0; i--) {
			for (byte j = 3; j >= 0; j--) {
				posToCell.put((byte) ((i * 4) + j), "" + a.charAt(x) + y);
				y += 2;
			}
			x++;
			if (x % 2 != 0) {
				y = 1;
			} else {
				y = 2;
			}
		}

		for (Entry<Byte, String> e : posToCell.entrySet()) {
			cellToPos.put(e.getValue(), e.getKey());
		}

		System.out.println(posToCell.toString());
		System.out.println(cellToPos.toString());
	}

}
