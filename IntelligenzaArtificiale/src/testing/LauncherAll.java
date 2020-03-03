package testing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LauncherAll {

	private static int whiteWin = 0, blackWin = 0, invalidMoveError = 0, runWins = 0, threadWins = 0, timeout=0;
	private static int numPartite = 100;
	private static boolean gui = false;

	public static void main(String[] args) throws IOException, InterruptedException {

		// numPartite = Integer.parseInt(args[0]);
		String[][] players = {
				new String[] { "sh", "-c",
						"java -jar /home/ciccio/Scrivania/PlayerDipole_vFINALE_4.jar 127.0.0.1 8901" },
				new String[] { "sh", "-c",
						"java -jar /home/ciccio/Scrivania/PlayerDipole_vFINALE_4.jar 127.0.0.1 8901" } };

		for (int i = 0; i < numPartite; i++) {

			if (i == numPartite / 2) {
				System.out.println("*************************************************");
				whiteWin = 0;
				blackWin = 0;
			}

			Process p = Runtime.getRuntime().exec(new String[] { "sh", "-c",
					"java -jar /home/ciccio/Scrivania/IA/Jars/ServerDipole_v3.jar " + (gui ? "-gui " : "") + "-wt 3" });
			InputStream is = p.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			Thread.sleep(2000);
			Process p1, p2;
			if (i < numPartite / 2) {
				p1 = Runtime.getRuntime().exec(players[0]);
				Thread.sleep(2000);
				p2 = Runtime.getRuntime().exec(players[1]);
			} else {
				p1 = Runtime.getRuntime().exec(players[1]);
				Thread.sleep(2000);
				p2 = Runtime.getRuntime().exec(players[0]);
			}

			String line = null;
			while ((line = br.readLine()) != null) {
				if (line.equalsIgnoreCase("White wins - Black loses") || line.equalsIgnoreCase("Black loses - White wins")) {
					whiteWin += 1;
					if (i < numPartite / 2) {
						threadWins += 1;
					} else {
						runWins += 1;
					}
					break;
				} else if (line.equalsIgnoreCase("White loses - Black wins") || line.equalsIgnoreCase("Black wins - White loses")) {
					blackWin += 1;
					if (i < numPartite / 2) {
						runWins += 1;
					} else {
						threadWins += 1;
					}
					break;
				} else if (line.equalsIgnoreCase("INVALID MOVE")) {
					invalidMoveError += 1;
					System.out.println("REPEAT <- invalid move");
					break;
				} else if(line.equalsIgnoreCase("TIMEOUT")) {
					timeout += 1;
					System.out.println("REPEAT <- timeout");
					break;
				}
			}

			p.destroy();
			p1.destroy();
			p2.destroy();

			if (i < numPartite / 2) {
				System.out.println("Run[white]: " + whiteWin + ", Thread[black]: " + blackWin);
			} else {
				System.out.println("Thread[white]: " + whiteWin + ", Run[black]: " + blackWin);
			}
		}
		
		// 1000 : 100 = someting : x -> x = 100*something/1000
		System.out.println("RUN_rate_win = " + (100*runWins)/1000.0 );
		System.out.println("THREAD_rate_win = " + (100*threadWins)/1000.0);
		System.out.println("INVALID-MOVE_rate = " + (100*invalidMoveError)/1000.0);
		System.out.println("TIMEOUT_rate = " + (100*timeout)/1000.0);
	}

}
