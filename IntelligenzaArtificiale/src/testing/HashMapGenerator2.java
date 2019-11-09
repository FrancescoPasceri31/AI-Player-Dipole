package testing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import java.util.Collections;

public class HashMapGenerator2 {

	public static void main(String[] args) throws IOException, ClassNotFoundException {

//		final String a = "ABCDEFGH";
//
//		HashMap<Byte, String> posToCell = new HashMap();
//		HashMap<String, Byte> cellToPos = new HashMap();
//
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
//
//		System.out.println(posToCell.toString());
//		System.out.println(cellToPos.toString());
//
//		File f = new File("hashMaps");
//		ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(f));
//		f.createNewFile();
//		o.writeObject(posToCell);
//		o.writeObject(cellToPos);
////		o.close();
//
//		Scacchiera s = new Scacchiera();
//		MaskGenerator mg = new MaskGenerator(s);
//		List<String> BLACK_MASK = mg.getBlackMask();
//		List<String> Indietro_BLACK_MASK = mg.getIndietro_BLACK_MASK();
//		List<String> WHITE_MASK = mg.getWhiteMask();
//		List<String> Indietro_WHITE_MASK = mg.getIndietro_WHITE_MASK();
////
//		HashMap<Byte, Object[]> masksBlack = new HashMap();
//		HashMap<Byte, Object[]> masksWhite = new HashMap();
		HashMap<Byte,Byte> posToPawn = new HashMap();  /* 1-> 12 white, 21->32 black*/
//		byte[] dirWhite = {7,2,5,4,3,6,1,8,7,1,6,3,4,5,2,7,6,2,5,4,3,6,1,6,5,1,5,3,4,5,2,5,4,2,4,4,3,4,1,4,3,1,3,3,3,3,2,3,2,2,2,2,2,2,1,2,1,1,1,1,1,1,1,1};
//		byte[] dirBlack = {1,1,1,1,1,1,1,1,1,2,2,2,2,2,2,2,2,3,3,3,3,3,3,1,1,4,3,4,4,4,4,2,2,5,4,5,5,3,5,1,1,6,3,6,5,4,6,2,2,7,4,5,6,3,7,1,1,8,3,6,5,4,7,2};
//		
//
//		// BLACK
//		for (int i = 7; i >= 0; i--) {
//			for (int j = 3; j >= 0; j--) {
//				int k = (i * 4) + j;
//				HashMap<Byte, Long[]> m = new HashMap();
//				for (int z = 1; z <= 12; z++) {
//					m.put((byte) (z+20), new Long[] {
//							Long.parseUnsignedLong(
//									charToString(editMask(BLACK_MASK.get(31 - k).toCharArray(), z, 31 - k)), 2),
//							Long.parseUnsignedLong(
//									charToString(editMask(Indietro_BLACK_MASK.get(31 - k).toCharArray(), z, 31 - k)),
//									2) });
//				}
//				masksBlack.put((byte) (k), new Object[] { dirBlack[k*2],dirBlack[(k*2)+1], m }); // new Object { #pedine, hashMap<numPedine,Maschere>
//																	// }
//			}
//		}
//
//		// WHITE
//		for (int i = 7; i >= 0; i--) {
//			for (int j = 3; j >= 0; j--) {
//				int k = (i * 4) + j;
//				HashMap<Byte, Long[]> n = new HashMap();
//				for (int z = 1; z <= 12; z++) {
//					n.put((byte) z, new Long[] {
//							Long.parseUnsignedLong(
//									charToString(editMask(WHITE_MASK.get(31 - k).toCharArray(), z, 31 - k)), 2),
//							Long.parseUnsignedLong(
//									charToString(editMask(Indietro_WHITE_MASK.get(31 - k).toCharArray(), z, 31 - k)),
//									2) });
//				}
//				masksWhite.put((byte) (k), new Object[] { dirWhite[k*2],dirWhite[(k*2)+1], n }); // new Object { #pedine, hashMap<numPedine,Maschere>
//																	// }
//			}
//		}
//		
		for(int i=0;i<32;i++) {
			if(i==1) //white start position
				posToPawn.put((byte)i, (byte) 12 );
			else if(i==30) //black start position
				posToPawn.put((byte)i, (byte) 32 );
			else
				posToPawn.put((byte)i, (byte) 0 );
		}
//
//		System.out.println();
//		
//		printHash(masksBlack);
//		System.out.println();
//		System.out.println(posToPawn);
//
//		o.writeObject(masksBlack);
//		o.writeObject(masksWhite);
//		o.close();
////		
//		System.out.println(masksBlack);

//		System.out.print("{ ");
//		for (Entry<Byte, Object[]> e : masksBlack.entrySet()) {
//			System.out.print("'" + e.getKey() + "' : {\n");
//			Object[] o = e.getValue();
//			HashMap<Byte, Long[]> hTmp = (HashMap<Byte, Long[]>) o[1];
//			for (Entry<Byte, Long[]> e1 : hTmp.entrySet()) {
//				byte numPedine = e1.getKey();
//				Long[] maschere = e1.getValue();
//				System.out.print("\t'" + numPedine + "' : [ " + maschere[0] + " , " + maschere[1] + " ]\n");
//			}
//		}
//		System.out.println(" }");
//		System.out.println();
////		System.out.println(masksBlack.get(31));
////		System.out.println();
//		long m = Long.parseUnsignedLong(charToString(editMask(BLACK_MASK.get(9).toCharArray(), 1, 9)), 2);
//		long p = Long.parseUnsignedLong(charToString(editMask(Indietro_BLACK_MASK.get(9).toCharArray(), 1, 9)), 2);
//		long c = Long.parseUnsignedLong("00000001010000000000000001000000", 2); // centrato in 22
//		long e = Long.parseUnsignedLong("01001000100000000100000000000000", 2); // nemico

//      Test sulla tabella 

//		System.out.println(charToString(editMask(WHITE_MASK.get(9).toCharArray(), 3, 9)));
//		long l = ((Long[]) ((HashMap) masksWhite.get((byte) 22)[1]).get((byte) 3))[0];
//		System.out.println();
//		System.out.println(Long.toBinaryString(l));
//		System.out.println();

//      Test mosse

//		long m = Long.parseUnsignedLong(charToString(editMask(BLACK_MASK.get(9).toCharArray(), 1, 9)), 2);
//		long p = Long.parseUnsignedLong(charToString(editMask(Indietro_BLACK_MASK.get(9).toCharArray(), 1, 9)), 2);
//		long c = Long.parseUnsignedLong("00000001010000000000000001000000", 2); // centrato in 22
//		long e = Long.parseUnsignedLong("01001000100000000100000000000000", 2); // nemico
//
//		System.out.println("Nero");
//		Test.stampaScacchiera(Test.onesPosition(c));
//		System.out.println();
//		System.out.println("Bianco");
//		Test.stampaScacchiera(Test.onesPosition(e));
//		System.out.println();
//		System.out.println("Mosse");
//		Test.stampaScacchiera(Test.zerosPosition(m & (p | (~e))));

		HashMap<Byte, String> posToCell = null;
		HashMap<String, Byte> cellToPos = null;
		Double tempi = 0.0;
		HashMap<Byte, Object[]> masksBlack = null;
		HashMap<Byte, Object[]> masksWhite = null;
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
		
		ObjectInputStream i = new ObjectInputStream(new FileInputStream("hashMaps"));
		long t = System.currentTimeMillis();
		posToCell = (HashMap<Byte, String>) i.readObject();
		cellToPos = (HashMap<String, Byte>) i.readObject();
		masksBlack = (HashMap<Byte, Object[]>) i.readObject();
		masksWhite = (HashMap<Byte, Object[]>) i.readObject();
		i.close();

//		System.out.println(tempi / run);
		System.out.println();
		System.out.println(posToPawn.toString());
		System.out.println();
		System.out.println(posToCell.toString());
		System.out.println();
		System.out.println(cellToPos.toString());
		System.out.println();
		printHash(masksBlack);
		printHash(masksWhite);
		System.out.println();
		System.out.println(Long.toBinaryString(getMask(masksWhite,0,4,0)));
		System.out.println();
		System.out.println(Arrays.toString(getOutLeastPawns(masksWhite, 31)));
		System.out.println();
		System.out.println();
		
//		System.out.println(Long.toBinaryString(((Long[])((HashMap)masksWhite.get((byte)17)[3]).get((byte)4))[1]));
//		System.out.println(masksWhite.get((byte)17)[1]+" "+masksWhite.get((byte)17)[2]);

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
		for (char k : c) {
			ret += k;
		}
		return ret;
	}

	static void printHash(HashMap<Byte, Object[]> hp) {

		System.out.print("{ ");
		for (Entry<Byte, Object[]> e : hp.entrySet()) {
			System.out.print("'" + e.getKey() + "' : {\n");
			Object[] o = e.getValue();
			HashMap<Byte, Long[]> hTmp = (HashMap<Byte, Long[]>) o[2];
			for (Entry<Byte, Long[]> e1 : hTmp.entrySet()) {
				byte numPedine = e1.getKey();
				Long[] maschere = e1.getValue();
				System.out.print("\t\t'" + numPedine + "' : [ "+(byte)o[0]+" , "+(byte)o[1] +" , "+ maschere[0] + " , " + maschere[1] + " ]\n");
			}
		}
		System.out.println(" }");

	}
	
	static public int[] onesPosition(long l) {
		int n = Long.bitCount(l);
		int[] ret = new int[n];
		long x=0,y = l;
		for (int i = 0; i < n; i++) {
			ret[i] = 63 - Long.numberOfLeadingZeros(y);
			x = Long.highestOneBit(y);
			// long x = (long) Math.pow(2, ret[i]);
			y -= x;
		}
		return ret;

	}

	static public int[] zerosPosition(long l) {
		long x = 4294967295L;
		return onesPosition(~(l+(~x)));
	}
	
	
	//Restituisce una maschera data la mappa (map), la posizione(pos), il numero di pedine(pawns) e la direzione(a_d, 0 avanti, 1 dietro)
	static public long getMask(HashMap<Byte,Object[]> map, int pos,int pawns, int a_d) {
		return ((Long[])((HashMap<Byte,Long[]>)(((Object[])(map.get((byte)pos)))[2])).get((byte)pawns))[a_d];
	}
	
	//restituisce data la mappa(map) e la posizione(pos), il minimo numero di pedine che servono per uscire fuori dalla scacchiera 
	static public byte[] getOutLeastPawns(HashMap<Byte,Object[]> map, int pos) {
		return new byte[]{(byte)((Object[])map.get((byte)pos))[0],(byte)((Object[])map.get((byte)pos))[1]};
	}
}
