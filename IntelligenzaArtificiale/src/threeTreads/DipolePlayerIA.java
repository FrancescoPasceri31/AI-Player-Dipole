package threeTreads;

public class DipolePlayerIA {

	public static void main(String[] args) {
		String address = args[0];
		int port = Integer.parseInt(args[1]);

		SpeakerThread st = new SpeakerThread(port, address);
		DecisionThread dt = new DecisionThread();
		MemoryThread mt = new MemoryThread();

		st.setDt(dt);
		dt.setMt(mt);

		st.start();
	}

}