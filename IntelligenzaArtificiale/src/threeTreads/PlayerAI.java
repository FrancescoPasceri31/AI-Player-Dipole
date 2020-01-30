package threeTreads;

public class PlayerAI {
	public static void main(String[] args) {
//		String address = args[0];
//		int port = Integer.valueOf(args[1]);

//		String address = "160.97.138.93";
		String address = "localhost";
		int port = 8901;

		SpeakerThread st = new SpeakerThread(port, address);
		DecisionThread dt = new DecisionThread();
		//MemoryThread mt = new MemoryThread();

		st.setDt(dt);
		//dt.setMt(mt);

		st.start();
	}
}
