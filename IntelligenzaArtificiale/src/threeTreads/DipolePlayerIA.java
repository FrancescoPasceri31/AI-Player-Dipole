package threeTreads;

public class DipolePlayerIA {

	public static void main(String[] args) {
		String address = args[0];
		int port = Integer.parseInt(args[1]);
		
		SpeakerThread st = new SpeakerThread(port, address);
		MemoryThread mt = new MemoryThread();
		DecisionThread dt = new DecisionThread();
		
		st.setPlayer(dt); st.setMemory(mt);
		mt.setPlayer(dt);
		dt.setSpeaker(st); dt.setMemory(mt);
		
		st.start();
	}
	
}
