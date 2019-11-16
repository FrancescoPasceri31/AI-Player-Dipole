package rappresentazione;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

public class Node {

	private double value = (new Random()).nextDouble();
	private LinkedList<Node> sons = new LinkedList<Node>();
	private Node parent;

	private int mc, ec;
	private String mossa;
	private HashMap<Byte, Byte> posToPawns;

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

	public void setEc(int ec) {
		this.ec = ec;
	}

	public void setMc(int mc) {
		this.mc = mc;
	}

	public void setMossa(String mossa) {
		this.mossa = mossa;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public void setPosToPawns(HashMap<Byte, Byte> posToPawns) {
		this.posToPawns = (HashMap<Byte, Byte>) posToPawns.clone();
	}

	public int getEc() {
		return ec;
	}

	public int getMc() {
		return mc;
	}

	public String getMossa() {
		return mossa;
	}

	public Node getParent() {
		return parent;
	}

	public HashMap<Byte, Byte> getPosToPawns() {
		return posToPawns;
	}

	@Override
	public String toString() {
		return "(" + value + ", " + sons + ") ";
	}

}