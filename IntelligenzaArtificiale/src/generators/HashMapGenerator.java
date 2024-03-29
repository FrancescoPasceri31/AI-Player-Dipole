package generators;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import testing.Scacchiera;

public class HashMapGenerator {

	public static void main(String[] args) throws Exception {

//		final char[] COLORE_SCACCHIERA = { 'W', 'B', 'W', 'B', 'W', 'B', 'W', 'B', 'B', 'W', 'B', 'W', 'B', 'W',
//				'B', 'W', 'W', 'B', 'W', 'B', 'W', 'B', 'W', 'B', 'B', 'W', 'B', 'W', 'B', 'W', 'B', 'W', 'W', 'B', 'W',
//				'B', 'W', 'B', 'W', 'B', 'B', 'W', 'B', 'W', 'B', 'W', 'B', 'W', 'W', 'B', 'W', 'B', 'W', 'B', 'W', 'B',
//				'B', 'W', 'B', 'W', 'B', 'W', 'B', 'W', };
//		
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
////	    System.out.println(posToCell.toString());
////		System.out.println(cellToPos.toString());
//
//		File f = new File("hashMaps");
//		ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(f));
//		f.createNewFile();
//		o.writeObject(posToCell);
//		o.writeObject(cellToPos);
////		o.close();
//
//		Scacchiera s = new Scacchiera();
////		s.init();
//		MaskGenerator mg = new MaskGenerator(s);
//		List<String> BLACK_MASK = mg.getBlackMask();
//		List<String> Indietro_BLACK_MASK = mg.getIndietro_BLACK_MASK();
//		List<String> WHITE_MASK = mg.getWhiteMask();
//		List<String> Indietro_WHITE_MASK = mg.getIndietro_WHITE_MASK();
//
//		HashMap<Byte, Object[]> masksBlack = new HashMap();
//		HashMap<Byte, Object[]> masksWhite = new HashMap();
//		HashMap<Byte, Byte> posToPawn = new HashMap<Byte,Byte>(); /* 1-> 12 white, 21->32 black */
//		byte[] dirWhite = {7,8,2, 5,8,4, 3,8,6, 1,8,8, 7,8,1, 6,8,3, 4,8,5, 2,8,7, 6,6,2, 5,6,4, 3,6,6, 1,6,6, 5,6,1, 5,6,3, 4,6,5, 2,6,5, 4,4,2, 4,4,4, 3,4,4, 1,4,4, 3,4,1, 3,4,3, 3,4,3, 2,4,3, 2,2,2, 2,2,2, 2,2,2, 1,2,2, 1,2,1 ,1,2,1, 1,2,1, 1,2,1};
//		byte[] dirBlack = {1,2,1, 1,2,1, 1,2,1, 1,2,1, 1,2,2, 2,2,2, 2,2,2, 2,2,2, 2,4,3, 3,4,3, 3,4,3, 3,4,1, 1,4,4, 3,4,4, 4,4,4, 4,4,2, 2,6,5, 4,6,5, 5,6,3, 5,6,1, 1,6,6, 3,6,6, 5,6,4, 6,6,2, 2,8,7, 4,8,5, 6,8,3, 7,8,1, 1,8,8, 3,8,6, 5,8,4, 7,8,2};
//		
//
//		// BLACK
//		for (int i = 7; i >= 0; i--) {
//			for (int j = 3; j >= 0; j--) {
//				int k = (i * 4) + j;
//				HashMap<Byte, Integer[]> m = new HashMap();
//				for (int z = 1; z <= 12; z++) {
//					m.put((byte) (z+20), new Integer[] {
//							Integer.parseUnsignedInt(
//									charToString(editMask(BLACK_MASK.get(31 - k).toCharArray(), z, 31 - k)), 2),
//							Integer.parseUnsignedInt(
//									charToString(editMask(Indietro_BLACK_MASK.get(31 - k).toCharArray(), z, 31 - k)),
//									2) });
//				}
//				masksBlack.put((byte) (k), new Object[] { dirBlack[k*3],dirBlack[(k*3)+1],dirBlack[(k*3)+2], m }); // new Object { #pedine, hashMap<numPedine,Maschere>
//																	// }
//			}
//		}
//
//		// WHITE
//		for (int i = 7; i >= 0; i--) {
//			for (int j = 3; j >= 0; j--) {
//				int k = (i * 4) + j;
//				HashMap<Byte, Integer[]> n = new HashMap();
//				for (int z = 1; z <= 12; z++) {
//					n.put((byte) z, new Integer[] {
//							Integer.parseUnsignedInt(
//									charToString(editMask(WHITE_MASK.get(31 - k).toCharArray(), z, 31 - k)), 2),
//							Integer.parseUnsignedInt(
//									charToString(editMask(Indietro_WHITE_MASK.get(31 - k).toCharArray(), z, 31 - k)),
//									2) });
//				}
//				masksWhite.put((byte) (k), new Object[] { dirWhite[k*3],dirWhite[(k*3)+1],dirWhite[(k*3)+2], n }); // new Object { #pedine, hashMap<numPedine,Maschere>
//																	// }
//			}
//		}
//		
//		for (int i = 0; i < 32; i++) {
//			if (i == 1) // white start position
//				posToPawn.put((byte) i, (byte) 12);
//			else if (i == 30) // black start position
//				posToPawn.put((byte) i, (byte) 32);
//			else
//				posToPawn.put((byte) i, (byte) 0);
//		}
////
////		System.out.println();
//
////		printHash(masksBlack);
////		System.out.println(Integer.toBinaryString(getMask(masksBlack, 31, 32, 1)));
////		System.out.println();
////		System.out.println(posToPawn);
////
//		o.writeObject(masksBlack);
//		o.writeObject(masksWhite);
////		o.close();
//////		+-
//		
//		HashMap<Byte,HashMap<Byte,String>> posToDir = new HashMap();
//		
//		
//		for(int riga1=0; riga1<8; riga1++) {
//			for(int colonna1=0; colonna1<8; colonna1++) {
//				if(COLORE_SCACCHIERA[riga1*8+colonna1] == 'B') {
//					byte pos1 = (byte) (riga1*8+colonna1);
//					pos1/=2;
//					pos1 = (byte) Math.abs(31 - pos1);
//				
//					HashMap<Byte,String> posDestToString = new HashMap<Byte, String>();
//					
//					for(int riga2=0; riga2<8; riga2++) {
//						for(int colonna2=0; colonna2<8; colonna2++) {
//						
//							String mossa="";
//						
//							byte pos2 = (byte) (riga2*8+colonna2);
//							pos2/=2;
//							pos2 = (byte) Math.abs(31 - pos2);
//	
//							
//							if(pos1 == pos2 || COLORE_SCACCHIERA[riga2*8+colonna2]=='W') {
//								continue;
//							}else {
//								if(riga1 == riga2) {	// mossa in orizzontale
//									if( colonna1 < colonna2 ) mossa+="E";
//									else if( colonna1 > colonna2 ) mossa+="W";
//								}
//								else if( colonna1 == colonna2 ) {	// mossa in verticale
//									if( riga1<riga2 ) mossa+="S";
//									else if( riga1>riga2 ) mossa+="N";
//								}
//								else { // mossa in obliquo
//									int off = riga1 - riga2;
//									if(off<0) mossa+="S";
//									else if(off>0) mossa+="N";
//									if(off<0) off*=-1;
//									if (colonna2 == colonna1 - off)
//										mossa+="W";
//									else if(colonna2 == colonna1 + off)
//										mossa+="E";
//									}
//								}
//							posDestToString.put(pos2, mossa);
//							}// for colonna2
//						} // for riga 2
//					
//					posToDir.put(pos1, posDestToString);
//				}
//			}
//		}
//		
//		o.writeObject(posToDir);
//		o.close();
		
		

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
//		int run = 10000;

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
		
		int[] r = getMask(masksWhite,(byte)14,(byte)3);
		System.out.println(Integer.toBinaryString(r[0]));
		System.out.println(Integer.toBinaryString(r[1]));
		
//
////		System.out.println(tempi / run);
//		System.out.println();
//		System.out.println(posToPawn.toString());
//		System.out.println();
//		System.out.println(posToCell.toString());
//		System.out.println();
//		System.out.println(cellToPos.toString());
//		System.out.println();
//		printHash(masksBlack);
//		printHash(masksWhite);
//		System.out.println();
//		//System.out.println(Integer.toBinaryString(getMask(masksWhite, 0, 4, 0)));
//		System.out.println();
//		System.out.println(Arrays.toString(getOutLeastPawns(masksWhite, (byte)31)));
//		System.out.println();
//		System.out.println();

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

	static void printHash(HashMap<Byte, Object[]> hp) {

		System.out.print("{ ");
		for (Entry<Byte, Object[]> e : hp.entrySet()) {
			System.out.print("'" + e.getKey() + "' : {\n");
			Object[] o = e.getValue();
			HashMap<Byte, Integer[]> hTmp = (HashMap<Byte, Integer[]>) o[2];
			for (Entry<Byte, Integer[]> e1 : hTmp.entrySet()) {
				byte numPedine = e1.getKey();
				Integer[] maschere = e1.getValue();
				System.out.print("\t\t'" + numPedine + "' : [ " + (byte) o[0] + " , " + (byte) o[1] + " , "
						+ maschere[0] + " , " + maschere[1] + " ]\n");
			}
		}
		System.out.println(" }");

	}

	static public byte[] onesPosition(int l) {
		int n = Integer.bitCount(l);
		byte[] ret = new byte[n];
		int x = 0, y = l;
		for (int i = 0; i < n; i++) {
			ret[i] = (byte)(31 - Integer.numberOfLeadingZeros(y));
			x = Integer.highestOneBit(y);
			// long x = (long) Math.pow(2, ret[i]);
			y -= x;
		}
		return ret;

	}

	static public byte[] zerosPosition(int l) {
		// long x = 4294967295L;
		return onesPosition(~l);
	}

	static String charToString(char[] c) {
		String ret = "";
		for (char k : c) {
			ret += k;
		}
		return ret;
	}

	// Restituisce una maschera data la mappa (map), la posizione(pos), il numero di
	// pedine(pawns) e la direzione(a_d, 0 avanti, 1 dietro)
	static public int[] getMask(HashMap<Byte, Object[]> map, int pos, byte pawns) {
//		System.out.println("Accedo alla maschera in posizione "+pos+" con "+pawns+" pedine.");
//		HashMapGenerator.printHash(map);
		Integer[] ret=((Integer[]) ((HashMap<Byte, Integer[]>) (((Object[]) (map.get((byte) pos)))[3]))
				.get((byte) pawns));
		//System.out.println(ret);
		return new int[] {ret[0],ret[1]};
	}

	// restituisce data la mappa(map) e la posizione(pos), il minimo numero di
	// pedine che servono per uscire fuori dalla scacchiera
	static public byte[] getOutLeastPawns(HashMap<Byte, Object[]> map, byte pos) {
		return new byte[] { (byte) ((Object[]) map.get((byte) pos))[0], (byte) ((Object[]) map.get(pos))[1] ,(byte) ((Object[]) map.get(pos))[2]};
	}
}
