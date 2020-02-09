package run;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.StringTokenizer;

import generators.MovesGenerator;
import rappresentazione.Node;
import ricerca.Search;
import testing.MovesGeneratorVecchio;
import testing.Search2;
import testing.Search3;
import testing.Search4;

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

		Search3 s = null;

		LinkedList<Node> leaves = null;

		int livelloMax = 8;

		try {
			soc = new Socket(address, port);
			in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			out = new PrintWriter(soc.getOutputStream(), true);

			while (true) {
				// if(root!=null) System.out.println(root.getMossa());
				ret = in.readLine();
				// if(ret==null) continue;
				st = new StringTokenizer(ret, " ");
				switch (st.nextToken()) {
				case "WELCOME":
					if (st.nextToken().equals("White"))
						isWhite = true;
					break;
				case "MESSAGE":
					if (st.nextToken().equals("All")) {

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

						root = new Node(null, bc, wc, posToPawn, "", "", "0");

//					    mg.generateMovesRecursive(root, isWhite,isWhite, 0, 4);

						s = new Search3();
						s.init();
					}
					System.out.println(ret);
					break;
				case "OPPONENT_MOVE":
					opponent_move = st.nextToken();
					if (root.getSons().size() == 0)
						mg.generateMoves(root, !isWhite, isWhite);
					for (Node f : root.getSons())
						if (f.getMossa().equals(opponent_move)) {
							root = f;
							root.setParent(null);
							System.gc();
							break;
						}
					break;
				case "YOUR_TURN":

					long tstart = System.currentTimeMillis();
					root = s.recursiveSearch(root, isWhite, livelloMax);
					long tend = System.currentTimeMillis();
					System.out.println("LivelloMax: " + livelloMax);
					System.out.println("Tempo: " + (tend - tstart) / 1000.0);

					root.setParent(null);
					out.println("MOVE " + root.getMossa());
					System.gc();

					leaves = mg.getLeaves(root);
					System.out.println("Leaves: " + leaves.size());
					System.out.println();

					int th = leaves.size();
					
					if (th <= 1631)
						livelloMax = 14;
					else if (th > 1631 && th <= 3262)
						livelloMax = 13;
					else if (th > 3262 && th <= 4893)
						livelloMax = 12;
					else if (th > 4893 && th <= 6525)
						livelloMax = 11;
					else if (th > 6525 && th <= 8700)
						livelloMax = 10;
					else if (th > 8700 && th <=13000)
						livelloMax = 9;
					else
						livelloMax = 8;
					
//					if (th <= 3262)
//						livelloMax = 12;
//					else if (th > 3262 && th <= 4893)
//						livelloMax = 11;
//					else if (th > 4893 && th <= 6525)
//						livelloMax = 10;
//					else if (th > 6525 && th <= 8700)
//						livelloMax = 9;
//					else
//						livelloMax = 8;

//					if (th <= 5600)
//						livelloMax = 12;
//					else if (th > 5600 && th <= 8700)
//						livelloMax = 10;
//					else
//						livelloMax = 8;

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
		} finally {
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
