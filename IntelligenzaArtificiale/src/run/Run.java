package run;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

import generators.MovesGenerator;
import rappresentazione.Node;
import ricerca.Search;

public class Run {
	
	public static void main(String[] args) {
		
		String address = args[0];
		int port = Integer.parseInt(args[1]);
		
		Socket soc = null;
		BufferedReader in = null;
		PrintWriter out = null;
		String ret = null;
		StringTokenizer st = null;
		boolean isWhite = false;
		MovesGenerator mg = null;
		Node root = null;
		String opponent_move = null;
		Search s = null;
		try {
			soc = new Socket(address,port);
			in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			out = new PrintWriter(soc.getOutputStream(),true);
			
			while(true) {
				ret = in.readLine();
				//if(ret==null) continue;
				st = new StringTokenizer(ret," ");
				switch(st.nextToken()){
				case "WELCOME":
					if(st.nextToken().equals("White"))
						isWhite = true;
					break;
				case "MESSAGE":
					if(st.nextToken().equals("All")) {
						byte[] posToPawn = new byte[32];
						for (int i = 0; i < 32; i++) {
							if (i == 1) // white start position
								posToPawn[i] = (byte) 12;
							else if (i == 30) // black start position
								posToPawn[i] = (byte) 32;
							else
								posToPawn[i] = (byte) 0;
						}

						mg = new MovesGenerator();
						mg.init();

						int bc = mg.createConfig(posToPawn, false);
						int wc = mg.createConfig(posToPawn, true);
						
					    root = new Node(null, bc, wc, posToPawn, ",0");
						mg.generateMovesRecursive(mg, root, isWhite, 0,6);
						
						s = new Search();
						System.out.println(ret);
					}
					break;
				case "OPPONENT_MOVE":
					System.out.println(ret);
					opponent_move = st.nextToken();
					for(Node f: root.getSons())
						if(f.getMossa().equals(opponent_move)) {
							root = f;
							break;
						}
					break;
				case "YOUR_TURN":
//					if(isWhite)
//						out.println("MOVE H5,NE,1");
//					else
//						out.println("MOVE A4,SE,1");
					root = s.recursiveSearch(root, isWhite);
					out.println("MOVE "+root.getMossa());
					System.out.println("Mossa inviata: "+root.getMossa());
					break;
				case "VALID_MOVE":
					System.out.println(ret);
					break;
				case "ILLEGAL_MOVE":
					System.out.println(ret);
					break;
				case "TIMEOUT":
					System.out.println("Timer scaduto");
					break;
				case "VICTORY":
					System.out.println("Hai vinto");
					System.exit(0);
				case "TIE":
					System.out.println("Hai pareggiato");
					System.exit(0);
				case "DEFEAT":
					System.out.println("Hai perso");
					System.exit(0);
				default:
					System.out.println("Exit");
					System.exit(0);
				}
			}
		
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				out.close();
				in.close();
				soc.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	
		
		
	}

}
