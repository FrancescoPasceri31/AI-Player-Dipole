package threeTreads;

import java.util.LinkedList;

import rappresentazione.Node;

public class MemoryThread extends Thread {

	private LinkedList<Node> toBeDelete;

	public MemoryThread() {
		toBeDelete = new LinkedList<Node>();
	}

	public void addToDelete(Node n) {
		toBeDelete.addLast(n);
		System.out.println("MT created @" + this.getId());
	}

	@Override
	public void run() {
		while (true) {
			if (!toBeDelete.isEmpty()) {
				free(toBeDelete.removeFirst());
			}
		}
	}

	private void free(Node first) {
		if (first.getSons() == null) {
			first.setParent(null);
			return;
		}
		first.setParent(null);
		for (Node son : first.getSons()) {
			free(son);
		}
	}

}
