package testing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class HashMapGenerator {

	public static void main(String[] args) throws IOException, ClassNotFoundException {

//		final String a = "ABCDEFGH";
//
//		HashMap<Byte, String> posToCell = new HashMap();
//		HashMap<String, Byte> cellToPos = new HashMap();

//		int x = 0;
//		int y = 2;
//
//		for (byte i = 7; i >= 0; i--) {
//			for (byte j = 3; j >= 0; j--) {
//				posToCell.put((byte) ((i * 4) + j), "" + a.charAt(x) + y);
//				y += 2;
//			}
//			x++;
//			if (x % 2 != 0) {
//				y = 1;
//			} else {
//				y = 2;
//			}
//		}
//
//		for (Entry<Byte, String> e : posToCell.entrySet()) {
//			cellToPos.put(e.getValue(), e.getKey());
//		}

//		System.out.println(posToCell.toString());
//		System.out.println(cellToPos.toString());

//		File f = new File("hashMap");
//		ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(f));
//		f.createNewFile();
//		o.writeObject(posToCell);
//		o.writeObject(cellToPos);
//		o.close();

//		long t = System.currentTimeMillis();
//		ObjectInputStream i = new ObjectInputStream(new FileInputStream("hashMap"));
//		HashMap<Byte, String> posToCell = (HashMap<Byte, String>)i.readObject();
//		HashMap<Byte, String> cellToPos = (HashMap<Byte, String>)i.readObject();
//		
//		i.close();
//		System.out.println((System.currentTimeMillis()-t)/1000.0);
//		System.out.println();
//		System.out.println(posToCell.toString());
//		System.out.println();
//		System.out.println(cellToPos.toString());
		
		
		Scacchiera s = new Scacchiera();
		MaskGenerator mg = new MaskGenerator(s);
		List<String> BLACK_MASK = mg.getBlackMask();
		List<String> Indietro_BLACK_MASK = mg.getIndietro_BLACK_MASK();
		List<String> WHITE_MASK = mg.getWhiteMask();
		List<String> Indietro_WHITE_MASK = mg.getIndietro_WHITE_MASK();
		
		HashMap<Byte,Object[]> masksBlack = new HashMap();

		for (int i = 7; i >= 0; i--) {
			for (int j = 3; j >= 0; j--) {
				int x = (i * 4) + j;
				HashMap<Byte,Long[]> m = new HashMap();
				for(int z=1;z<=12;z++) {
					m.put((byte)z,new Long[]{Long.parseUnsignedLong(charToString(editMask(BLACK_MASK.get(x).toCharArray(), z, x)),2),Long.parseUnsignedLong(charToString(editMask(Indietro_BLACK_MASK.get(x).toCharArray(), z, x)),2)});
				}
				masksBlack.put((byte) (x), new Object[] {0,m});
			}
		}
//		System.out.println();
//		System.out.println(masksBlack);
//		System.out.println();
//		System.out.println(masksBlack.get(31));
		System.out.println();
		long m = Long.parseUnsignedLong(charToString(editMask(BLACK_MASK.get(9).toCharArray(), 1, 9)),2);
		long p = Long.parseUnsignedLong(charToString(editMask(Indietro_BLACK_MASK.get(9).toCharArray(), 1, 9)),2);
		long c = Long.parseUnsignedLong("00000001010000000000000001000000", 2); //centrato in 22
		long e = Long.parseUnsignedLong("01001000100000000100000000000000", 2); //nemico
		
		System.out.println("Nero");
		Test.stampaScacchiera(Test.onesPosition(c));
		System.out.println();
		System.out.println("Bianco");
		Test.stampaScacchiera(Test.onesPosition(e));
		System.out.println();
		System.out.println("Mosse");
		Test.stampaScacchiera(Test.zerosPosition(m&(p|(~e))));
		
		

	}
	
	static char[] editMask(char[] maskTmp, int nPedineMosse, int miaCella) {
		for (int k = 0; k < maskTmp.length; k++) {
			if (maskTmp[k] == '0') {
				int differenzaRiga = Math.abs(miaCella / (4) - k / (4));
				if (differenzaRiga > nPedineMosse) {
					maskTmp[k] = '1';
				} else if (differenzaRiga == 0 && 2 * Math.abs(miaCella - k) > nPedineMosse) {
					maskTmp[k] = '1';
				}
			}
		}
		return maskTmp;
	}
	
	static String charToString(char[] c) {
		String ret = "";
		for(char k: c) {
			ret+=k;
		}
		return ret;
	}

}
