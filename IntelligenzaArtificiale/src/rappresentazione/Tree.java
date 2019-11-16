package rappresentazione;

import java.util.LinkedList;

public class Tree {

	private Node root = new Node(null);

	public Node getRoot() {
		return root;
	}

	public LinkedList<Node> getSons() {
		return root.getSons();
	}

	public double getValue() {
		return root.getValue();
	}

	public void createSons() {
		root.getSons().add(new Node(root));
	}

	public void next(Node n) {
		root = n;
	}

	@Override
	public String toString() {
		return "Tree [root=" + root + "] " + root.numSons();
	}

}
