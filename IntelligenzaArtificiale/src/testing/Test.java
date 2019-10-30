package testing;

import java.util.Arrays;

public class Test {

	public static void main(String[] args) {
		long c = Long.parseUnsignedLong("00000001010000000000000001000000", 2); //centrato in 22
		long e = Long.parseUnsignedLong("01001000100000000100000000000000", 2); //nemico
		long m = Long.parseUnsignedLong("11111111111110010001011010101111", 2); //maschera solo in avanti
		long p = Long.parseUnsignedLong("00011001010011111111111111111111",2); //maschera all'indietro
		
		long y = Long.parseUnsignedLong("00011001010010010001011010101111", 2); //maschera generale
		
		long t = System.currentTimeMillis();
		
		//long r =((c|m)&m)&(~e);
		//long r =m&(p|(~e));
		long r = y|(~e);
		//long r = m&p;
		
		System.out.println((System.currentTimeMillis() - t) / 1000.0);
		
		
		System.out.println("Numero di uni: "+Long.bitCount(r));
		System.out.println("Stringa: "+Long.toBinaryString(r));
		System.out.println("Zeri: "+Arrays.toString((new Test()).zerosPosition(r)));
		System.out.println("Uni: "+Arrays.toString((new Test()).onesPosition(r)));
		
		System.out.println();
		System.out.println("Nero");
		stampaScacchiera((new Test()).onesPosition(c));
		System.out.println();
		System.out.println("Bianco");
		stampaScacchiera((new Test()).onesPosition(e));
		System.out.println();
		System.out.println("Mosse");
		stampaScacchiera((new Test()).zerosPosition(r));
	}

	public int[] onesPosition(long l) {
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

	public int[] zerosPosition(long l) {
		long x = 4294967295L;
		return onesPosition(~(l+(~x)));
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