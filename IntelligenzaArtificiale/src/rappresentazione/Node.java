package rappresentazione;

import java.util.LinkedList;

public class Node {

	private static int gid = 0;
	private int id;
	private double value;
	private LinkedList<Node> sons = new LinkedList<Node>();
	private Node parent;

	private int attacks;

	private int bc, wc; // mia configurazione e avversaria
	private String cella, direzione, pedine;
	private byte[] posToPawns;

	public Node(Node parent, int bc, int wc, byte[] posToPawns, String cella, String direzione, String pedine) {
		this.parent = parent;
		this.bc = bc;
		this.wc = wc;
		this.posToPawns = posToPawns.clone();
		this.cella = cella;
		this.direzione = direzione;
		this.pedine = pedine;
		id = gid++;
	}

	public String getCella() {
		return cella;
	}

	public void setCella(String cella) {
		this.cella = cella;
	}

	public String getDirezione() {
		return direzione;
	}

	public void setDirezione(String direzione) {
		this.direzione = direzione;
	}

	public String getPedine() {
		return pedine;
	}

	public void setPedine(String pedine) {
		this.pedine = pedine;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public void addSon(Node n) {
		sons.add(n);
	}

	public LinkedList<Node> getSons() {
		return sons;
	}

	public void setSons(LinkedList<Node> sons) {
		this.sons = sons;
	}

	public void setWc(int wc) {
		this.wc = wc;
	}

	public void setBc(int bc) {
		this.bc = bc;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public void setPosToPawns(byte[] posToPawns) {
		this.posToPawns = posToPawns.clone();
	}

	public int getWc() {
		return wc;
	}

	public int getBc() {
		return bc;
	}

	public String getMossa() {
		return cella + "," + direzione + "," + pedine;
	}

	public Node getParent() {
		return parent;
	}

	public byte[] getPosToPawns() {
		return posToPawns;
	}

	public String toString() {
		return "[ id: " + id + ", mossa: " + getMossa() + ", value: " + value + " bc: " + bc + " wc: " + wc + " ]\n";
	}

	public boolean leaf() {
		return this.sons.size() == 0;
	}

	public int getId() {
		return id;
	}

	public boolean expandable() {
		return bc != 0 && wc != 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (!(obj instanceof Node))
			return false;
		Node x = (Node) obj;
		return wc == x.wc && bc == x.bc;
	}

	public int getAttacks() {
		return attacks;
	}

	public void setAttacks(int attacks) {
		this.attacks = attacks;
	}
}