package playerAI;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.StringTokenizer;

import euristica.Euristica;
import generators.MovesGenerator;
import rappresentazione.Node;
import ricerca.Search;

public class PlayerDipole {

	public static volatile Node best;
	
	public static volatile Node root; 
	public static volatile boolean isWhite; 
	public static volatile int maxLevel;

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {

		String address = args[0];
		int port = Integer.valueOf(args[1]);

		Socket soc = null;
		BufferedReader in = null;
		PrintWriter out = null;
		String ret = null;
		StringTokenizer st = null;

		Euristica e =null;
		MovesGenerator mg = null;
		String opponentMove = null;
		root = null;
		maxLevel  = 8;
		
		
		Search[] searches = null;
		
		int idSearch=0;
		
		LinkedList<Node> ll = null;
		
		try {

			
			soc = new Socket(address,port);
			in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			out = new PrintWriter(soc.getOutputStream(), true);
			
			while(true) {
				
				ret = in.readLine();
				st = new StringTokenizer(ret, " ");
				
				switch(st.nextToken()) {
				
				case "WELCOME":
					isWhite = "White".equalsIgnoreCase(st.nextToken());
					break;
				
				case "MESSAGE":
					if(st.nextToken().equals("All")) {
						
						byte[] posToPawn = new byte[32];
						for(int i=0; i<32; i++) {
							if(i==1) { // white start position
								posToPawn[i] = 12;
							}else if(i==30) {	// black start position
								posToPawn[i] = 32;
							}else {
								posToPawn[i] = 0;
							}
						}
						
						ObjectInputStream ois = new ObjectInputStream(new FileInputStream("hashMaps"));
						HashMap<Byte, String> posToCell = (HashMap<Byte, String>) ois.readObject();
						HashMap<String, Byte> cellToPos = (HashMap<String, Byte>) ois.readObject();
						HashMap<Byte, Object[]> masksBlack = (HashMap<Byte, Object[]>) ois.readObject();
						HashMap<Byte, Object[]> masksWhite = (HashMap<Byte, Object[]>) ois.readObject();
						HashMap<Byte, HashMap<Byte, String>> posToDir = (HashMap<Byte, HashMap<Byte, String>>) ois.readObject();
						ois.close();
						
						e = new Euristica();
						e.init(cellToPos, masksBlack, masksWhite);
						
						mg = new MovesGenerator();
						mg.init(e, posToCell, cellToPos, masksBlack, masksWhite, posToDir);
						
						int wc = mg.createConfig(posToPawn, true);
						int bc = mg.createConfig(posToPawn, false);
						
						root = new Node(null, bc, wc, posToPawn, "", "", "0");
						
						searches = new Search[60];
						for(int i=0; i<searches.length; i++) {
							searches[i] = new Search();
							searches[i].init(mg);
						}
					}
					break;
				
				case "OPPONENT_MOVE":
					long timeF = System.currentTimeMillis();
					opponentMove = st.nextToken();
					if(root.getSons().size()==0) {
						mg.generateMoves(root, !isWhite, isWhite);
					}
					ll = root.getSons();
					for(Node son : ll) {
						if(son.getMossa().equals(opponentMove)) {
							root = son;
							root.setParent(null);
							//System.gc();
							break;
						}
					}
					long tendF = System.currentTimeMillis();
					System.out.println("TEMPO opp: "+ (tendF - timeF)/1000.0 );
					break;
					
				case "YOUR_TURN":
					
					long tstart = System.currentTimeMillis();
					
					searches[idSearch].start();
					int attempts=8;
					while(attempts>0 && best == null) {
						Thread.sleep(100);
						attempts-=1;
					}
					if(best == null) Thread.sleep(80);
					
					searches[idSearch].interrupt();
					
					if(best == null) {
						root = searches[idSearch].bestTmp;
					}else {
						root = best;
						best = null;
					}
					root.setParent(null);
					out.println("MOVE "+root.getMossa());
					
					long tendSearch = System.currentTimeMillis();
					
					System.out.println("LIVELLO: "+maxLevel);
					System.out.println("TEMPO SEARCH: "+  (tendSearch - tstart)/1000.0  );
					
					idSearch+=1;
					
					//System.gc();

					int th = countLeaves(root);

					if (th <= 1631)
						maxLevel = 14;
					else if (th > 1631 && th <= 3262)
						maxLevel = 13;
					else if (th > 3262 && th <= 4893)
						maxLevel = 12;
					else if (th > 4893 && th <= 6525)
						maxLevel = 11;
					else if (th > 6525 && th <= 8700)
						maxLevel = 10;
					else if (th > 8700 && th <=13000)
						maxLevel = 9;
					else
						maxLevel = 8;

					long tend = System.currentTimeMillis();	
					System.out.println("TEMPO TOTALE: "+ (tend - tstart)/1000.0);

					
					
					break;
				
				case "VALID_MOVE":
					break;
				case "ILLEGAL_MOVE":
					break;
				case "TIMEOUT":
					break;
				default:
					System.exit(0);
				}
			}
		} catch (Exception err) {
			err.printStackTrace();
		} finally {
			try {
				out.close();
				in.close();
				soc.close();
			} catch (Exception err) {
				err.printStackTrace();
			}
		}
	}

	public static int countLeaves(Node n) {
		int countLeaves=0;
		LinkedList<Node> open = new LinkedList<Node>();
		open.add(n);
		while (!open.isEmpty()) {
			Node f = open.removeFirst();
			if (!f.leaf() && f.expandable())
				open.addAll(f.getSons());
			else
				countLeaves+=1;
		}
		return countLeaves;
	}
}