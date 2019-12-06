package threeTreads;

import rappresentazione.Node;
import ricerca.Search;

public class DecisionThread extends Thread {

	public Node root;
	private SpeakerThread speaker;
	private MemoryThread memory;
	public boolean isWhite;

	public DecisionThread() {
		super();
	}

	public void setSpeaker(SpeakerThread speaker) {
		this.speaker = speaker;
	}

	public void setMemory(MemoryThread memory) {
		this.memory = memory;
	}

	public void setCol(boolean isWhite) {
		this.isWhite = isWhite;
	}

	public void setRoot(Node root) {
		this.root = root;
	}

	public Node getRoot() {
		return root;
	}

	@Override
	public void run() {
		try {
			memory.myMove = (Search.recursiveSearch(root, isWhite)).getMossa();
		} finally {
			System.out.println("Player pulisce tutto");
			root = null;
		}
	}

}
