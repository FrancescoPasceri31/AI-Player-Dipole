package threeTreads;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

public class SpeakerThread extends Thread {

	private DecisionThread player;
	private MemoryThread memory;
	private String address;
	private int port;

	public SpeakerThread(int port, String address) {
		this.port = port;
		this.address = address;
//		System.out.println("Speaker ready");
	}

	public void setPlayer(DecisionThread player) {
		this.player = player;
	}

	public void setMemory(MemoryThread memory) {
		this.memory = memory;
		System.out.println("ST : @" + memory.getId());
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public void run() {

		Socket soc = null;
		BufferedReader in = null;
		PrintWriter out = null;
		String ret = null;
		StringTokenizer st = null;

		try {
			soc = new Socket(address, port);
			in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			out = new PrintWriter(soc.getOutputStream(), true);

			while (true) {
				ret = in.readLine();
				st = new StringTokenizer(ret, " ");

				switch (st.nextToken()) {
				case "WELCOME":
					if (st.nextToken().equals("White")) {
						player.setCol(true);
					} else {
						player.setCol(false);
					}
					break;
				case "MESSAGE":
					// devo far avviare il thread gestore della memoria
					if (st.nextToken().equals("All")) {
						memory.start();
						player.start();
					}
					System.out.println(ret);
					break;
				case "OPPONENT_MOVE":
					memory.opponentMove = st.nextToken();
					break;
				case "YOUR_TURN":
					long time = System.currentTimeMillis();
					
					memory.search = true;
					
					// attendo per 900 millisecondi e con i restanti 100 vedo se l'euristica ha
					// trovato il migliore altrimenti mi accontento del migliore attuale
					while (/* time + System.currentTimeMillis() <= time + 900 && */ memory.myMove == null) {
						;
					}
					if (memory.myMove == null) {
						player.interrupt();
						System.out.println("MOVE " + "bang"); // da cambiare con il get del best nodo trovato fino ad
																// ora
					}

					out.println("MOVE " + memory.myMove);
					memory.myMove = null;
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

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
				in.close();
				soc.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
