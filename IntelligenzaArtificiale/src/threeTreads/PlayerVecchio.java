package threeTreads;

import ricerca.Search;
import testing.SearchVecchia;

public class PlayerVecchio {
	public static void main(String[] args) {
//		String address = args[0];
//		int port = Integer.valueOf(args[1]);

//		String address = "192.168.43.86";
		String address = "localhost";
		int port = 8901;
		
		System.out.println((new Search()) instanceof SearchVecchia);

		SpeakerThread st = new SpeakerThread(port, address);
		DecisionThread dt = new DecisionThread();
		dt.setSearch(new SearchVecchia());
		//MemoryThread mt = new MemoryThread();

		st.setDt(dt);
		//dt.setMt(mt);

		st.start();
	}
}
