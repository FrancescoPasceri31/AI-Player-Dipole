package threeTreads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

public class SpeakerThread extends Thread {

	private DecisionThread dt;
	private String address;
	private int port;

	public SpeakerThread(int port, String address) {
		this.port = port;
		this.address = address;
		System.out.println("ST created @" + this.getId());
	}

	public void setDt(DecisionThread dt) {
		this.dt = dt;
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
					System.out.println(ret);
					dt.setColor(st.nextToken().equals("White"));
					break;
				case "MESSAGE":
					if(st.nextToken().equals("All"))
						dt.start();
					System.out.println(ret);
					break;
				case "OPPONENT_MOVE":
					System.out.println(ret);
					dt.opponentMove = st.nextToken();
					break;
				case "YOUR_TURN":
					System.out.println(ret);
					long time = System.currentTimeMillis();
					dt.startSearch = true;
					while (System.currentTimeMillis() < time + 900 && dt.myMove == null) {
						;
					}
					dt.startSearch = false;
					if (dt.myMove == null)
						out.println("MOVE " + dt.bestMove);
					else
						out.println("MOVE " + dt.myMove);
					dt.myMove = null;
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
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
}
