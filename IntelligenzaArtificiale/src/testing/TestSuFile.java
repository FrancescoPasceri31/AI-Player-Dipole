package testing;

import java.io.FileInputStream;
import java.io.ObjectInputStream;

import generators.MovesGeneratorToFile;
import rappresentazione.Node;
import rappresentazione.NodeToFile;
import ricerca.SearchFromFile;

public class TestSuFile {
	
	
	public static void main(String[] args) throws Exception {
		
		/*     	GENERZIONE NODI       */
		
		byte[] posToPawn = new byte[32];
		for (int i = 0; i < 32; i++) {
			if (i == 1) // white start position
				posToPawn[i] = (byte) 12;
			else if (i == 30) // black start position
				posToPawn[i] = (byte) 32;
			else
				posToPawn[i] = (byte) 0;
		}
		
		MovesGeneratorToFile mg = new MovesGeneratorToFile();
		mg.init();
		
		NodeToFile root = new NodeToFile(-1, mg.createConfig(posToPawn, false), mg.createConfig(posToPawn, true), posToPawn,"");
		
		int livelloMax = 5;
		long t = System.currentTimeMillis();
		mg.generateMovesRecursive(mg, root, true, 0, livelloMax);
		System.out.println("Generazione "+livelloMax+" livelli terminata in:"+(System.currentTimeMillis()-t)/1000.0 +"s");
//		
		
		ObjectInputStream i = new ObjectInputStream(new FileInputStream("./Nodes/0"));
		NodeToFile n = (NodeToFile) i.readObject();
		i.close();
		
		t = System.currentTimeMillis();
		Node r = mg.reconstruct(n,null,0,livelloMax);
		System.out.println("Ricostruzione terminata in: "+(System.currentTimeMillis()-t)/1000.0 +"s per "+livelloMax+" livelli");
		
//		SearchFromFile s = new SearchFromFile();
//		long t = System.currentTimeMillis();
//		NodeToFile ret = s.recursiveSearch(n, true);
//		System.out.println("Search terminata in:"+(System.currentTimeMillis()-t)/1000.0 +"s");
//		
//		System.out.println(ret);
		
//		System.out.println(Node.generateGenericVerbose(r, "", false, false, new StringBuilder()));

	}
	
	
	

}
