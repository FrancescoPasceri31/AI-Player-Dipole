package rappresentazione;

import java.util.LinkedList;
import java.util.Random;

public class Node {
	
	private double value = (new Random()).nextDouble();
	private LinkedList<Node> sons = new LinkedList();
	private Node parent;
	
	public Node(Node parent) {
		this.parent = parent;
	}
	
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public LinkedList<Node> getSons() {
		return sons;
	}
	public void setSons(LinkedList<Node> sons) {
		this.sons = sons;
	}
	
	public int numSons() {
		return sons.size();
	}
	@Override
	public String toString() {
		return "(" + value + ", " + sons + ") ";
	}

	
	
}