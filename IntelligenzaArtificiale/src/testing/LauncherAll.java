package testing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LauncherAll {

	private static int whiteWin = 0, blackWin = 0;
	private static int numPartite;
	private static boolean gui = false;

	public static void main(String[] args) throws IOException, InterruptedException {
		
		numPartite = Integer.parseInt(args[0]);
		
		for (int i = 0; i < numPartite; i++) {
			Process p = Runtime.getRuntime().exec(new String[] { "sh", "-c",
					"java -jar " + System.getProperty("user.dir") + "/DipoleServer_v2.jar"+( gui? " -gui " : " " )+"-wt 3 -tt 600000" });
			InputStream is = p.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			Thread.sleep(2000);
			Process p1 = Runtime.getRuntime().exec(
					new String[] { "sh", "-c", "java -jar " + System.getProperty("user.dir") + "/RunPLAYER.jar 127.0.0.1 8901" });
			Thread.sleep(2000);
			Process p2 = Runtime.getRuntime().exec(
					new String[] { "sh", "-c", "java -jar " + System.getProperty("user.dir") + "/RunPLAYER.jar 127.0.0.1 8901" });
			String line = null;
			int c = 0;
			while ((line = br.readLine()) != null) {
				if (line.equals("White wins - Black loses") || line.equals("Black loses - White wins")) {
					whiteWin += 1;
					break;
				} else if (line.equals("White loses - Black wins") || line.equals("Black wins - White loses")) {
					blackWin += 1;
					break;
				}else if( line.equals("INVALID MOVE") ) {
					i-=1;
					System.out.println("REPEAT");
					break;
				}
			}
			System.out.println("Win stats round "+i+" => wW=" + whiteWin + " - bW=" + blackWin+"\t");
			
			p.destroy();
			p1.destroy();
			p2.destroy();
		}
		File f = new File("WinStats");
		f.createNewFile();
		BufferedWriter w = new BufferedWriter(new FileWriter(f));
		w.write("W: "+whiteWin+", B: "+blackWin);
		w.close();
	}
	

}
