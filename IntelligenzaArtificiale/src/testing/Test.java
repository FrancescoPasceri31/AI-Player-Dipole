package testing;

import java.util.Arrays;

public class Test {

	public static void main(String[] args) {
	
		int c = Integer.parseUnsignedInt("00000001010000000000000001000000", 2); //centrato in 22
		int e = Integer.parseUnsignedInt("01001000100000000100000000000000", 2); //nemico
		int m = Integer.parseUnsignedInt("11111111111110010001011010101111", 2); //maschera solo in avanti
		int p = Integer.parseUnsignedInt("00011001010011111111111111111111",2); //maschera all'indietro
		
		int y = Integer.parseUnsignedInt("00011001010010010001011010101111", 2); //maschera generale
		
		long t = System.currentTimeMillis();
		
		//long r =((c|m)&m)&(~e);
		int r =m&(p|(~e));
		//long r = y|(~e);
		//long r = m&p;
		
		System.out.println((System.currentTimeMillis() - t) / 1000.0);
		
		
		System.out.println("Numero di uni: "+Integer.bitCount(r));
		System.out.println("Stringa: "+Integer.toBinaryString(r));
		System.out.println("Zeri: "+Arrays.toString(zerosPosition(r)));
		System.out.println("Uni: "+Arrays.toString(onesPosition(r)));
		
		System.out.println();
		System.out.println("Nero");
		stampaScacchiera(onesPosition(c));
		System.out.println();
		System.out.println("Bianco");
		stampaScacchiera(onesPosition(e));
		System.out.println();
		System.out.println("Mosse");
		stampaScacchiera(zerosPosition(r));

	}

	static public int[] onesPosition(int l) {
		int n = Integer.bitCount(l);
		int[] ret = new int[n];
		int x=0,y = l;
		for (int i = 0; i < n; i++) {
			ret[i] = 31 - Integer.numberOfLeadingZeros(y);
			x = Integer.highestOneBit(y);
			// long x = (long) Math.pow(2, ret[i]);
			y -= x;
		}
		return ret;

	}

	static public int[] zerosPosition(int l) {
		//long x = 4294967295L;
		return onesPosition(~l);
	}
	
	static String charToString(char[] c) {
		String ret = "";
		for(char k: c) {
			ret+=k;
		}
		return ret;
	}

	public static void stampaScacchiera(int[] pos) {
		int x = 0;
		int y = 31;
		for(int i=0;i<8;i++) {
			for(int j=0;j<4;j++) {
				if(i%2==0) {
					System.out.print("* ");
					if(pos[x]==y) {
						System.out.print("1 ");
						if(x<pos.length-1)
							x++;
					}else {
						System.out.print("0 ");
					}
				}else {
					if(pos[x]==y) {
						System.out.print("1 ");
						if(x<pos.length-1)
							x++;
					}else {
						System.out.print("0 ");
					}
					System.out.print("* ");
				}
				if(j==3) {
					System.out.println();
				}
				y--;
			}
		}
	}
}