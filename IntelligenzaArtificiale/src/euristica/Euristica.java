package euristica;

import java.util.Arrays;
import java.util.Random;

import generators.HashMapGenerator;
import rappresentazione.Node;

public class Euristica {
	
	private static double strategy_0(Node n, boolean isWhite) {
		byte[] posToPawn = n.getPosToPawns();
		int sum=0;
		for(int i=0;i<32;i++) {
			if(posToPawn[i]>0)
				sum+=(isWhite && posToPawn[i]<=12)?posToPawn[i]:(!isWhite && posToPawn[i]>12)?posToPawn[i]-20:0;
		}
		return sum;
	}
	
	private static double strategy_1(Node n, boolean isWhite) {   //non perdere pedine
		Node p = n.getParent();
		if(p==null) return 0;
		byte[] pawnsChild = n.getPosToPawns();
		byte[] pawnsParent = p.getPosToPawns();
//		System.out.println("id:"+n.getId()+" isWhite: "+isWhite);
//		System.out.println("parent: "+Arrays.toString(pawnsParent));
//		System.out.println("child: "+Arrays.toString(pawnsChild));
		int childConf,parentConf;
		if(isWhite) {
			childConf = n.getWc();
			parentConf = p.getWc();
		}else {
			childConf = n.getBc();
			parentConf = p.getBc();
		}
		byte[] childPos = HashMapGenerator.onesPosition(childConf);
		byte[] parentPos = HashMapGenerator.onesPosition(parentConf);
//		System.out.println(n.getId());
//		System.out.println(Arrays.toString(pawnsChild));
//		System.out.println("cPos: "+Arrays.toString(childPos));
//		System.out.println(Arrays.toString(pawnsParent));
//		System.out.println("pPos: "+Arrays.toString(parentPos));
		double ret = 0;
		for(int i=0;i<childPos.length;i++) ret+=pawnsChild[childPos[i]];
		if(ret==0 && !isWhite) ret+=20;
		for(int i=0;i<parentPos.length;i++) ret-=pawnsParent[parentPos[i]];
//		System.out.println("id: "+n.getId()+" isWhite: "+isWhite);
//		System.out.println("ret: "+ret);
//		System.out.println();
		return ret;
		
	}
	
	public static double strategy_2(Node f) {  //non muovermi troppo in avanti, 2 pedine vanno bene
		Node n = f.getParent();
//		if(n == null) return 0;
		int x = n.getMossa().lastIndexOf(",");
		byte numPedine= Byte.parseByte(n.getMossa().substring(x+1));
//		System.out.println("id: "+n.getId()+" "+numPedine);
		switch(numPedine) {
		case 1: return 1;
		case 2: return 5;
		case 3: return 4;
		case 4: return 3;
		case 5: return 2;
		case 6: return 1;
		default: return 0; 
		}
	}
	
	public static double strategy_3(Node n) {  //
		
		return 0;
	}
	
	public static double getEuristica(Node n, boolean isWhite) {
		return strategy_2(n)+2*strategy_1(n, isWhite)+3*strategy_0(n, isWhite);
	}

}
