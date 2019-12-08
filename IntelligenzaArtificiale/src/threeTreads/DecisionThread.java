package threeTreads;

import rappresentazione.Node;
import ricerca.Search;

public class DecisionThread extends Thread {

	public Node root;
	private MemoryThread memory;
	public boolean isWhite;

	public DecisionThread() {
//		System.out.println("Decision ready");
		super();
	}

	public void setMemory(MemoryThread memory) {
		this.memory = memory;
		System.out.println("DT :@" + memory.getId());
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

	public boolean getCol() {
		return isWhite;
	}

	@Override
	public void run() {
		while (true) {
			if (memory.search) {
				Search s = new Search();
				Node ret = s.recursiveSearch(root, isWhite);
				memory.myMove = ret.getMossa() ;
				memory.search = false;
				root = null;
			}
		}
	}
}