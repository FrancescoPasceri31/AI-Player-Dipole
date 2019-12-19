package rappresentazione;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import euristica.Euristica;

public class Node implements Serializable{
	

	private static final long serialVersionUID = 7164838078008152527L;
	
	private static int gid = 0;
	private int id;
	private double value;
	private LinkedList<Node> sons = new LinkedList<Node>();
	private Node parent;
	private boolean isMax, hasValue = false;

	private int bc, wc; // mia configurazione e avversaria
	private String cella,direzione,pedine;
//	private HashMap<Byte, Byte> posToPawns;
	private byte[] posToPawns;

	public Node(Node parent, int bc, int wc, byte[] posToPawns, String cella, String direzione, String pedine) {
		this.parent = parent;
		this.bc = bc;
		this.wc = wc;
		this.posToPawns = posToPawns.clone(); 
		this.cella = cella;
		this.direzione = direzione;
		this.pedine = pedine;
		isMax = (parent == null) ? true : !parent.isMax();
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

	public void setHasValue(boolean hasValue) {
		this.hasValue = hasValue;
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
		return cella+","+direzione+","+pedine;
	}

	public Node getParent() {
		return parent;
	}

	public byte[] getPosToPawns() {
		return posToPawns;
	}

//	@Override
//	public String toString() {
//		return "[ id: " + id + ", bc: " + toBinaryString(bc) + ", wc: " + toBinaryString(wc) + ", mossa: " + mossa
//				+ " ]";
//	}
	
	public String toString() {
//		return "\n[ p_id: " + parent.id +", p_mossa: "+parent.getMossa() +", p_value: "+parent.value+" ]"+"\n[ id: " + id +", mossa: "+getMossa() +", value: "+value+" ]\n";
		return "[ id: " + id +", mossa: "+getMossa() +", value: "+value+" bc: "+bc+" wc: "+wc+" ]\n";
	}

	private String toBinaryString(int i) {
		String tmp = Integer.toBinaryString(i);
		while (tmp.length() < 32) {
			tmp = "0" + tmp;
		}
		return tmp;
	}

	public boolean isMax() {
		return this.isMax;
	}

	public boolean equals(Node n) {
		return id == n.getId();
	}

	public boolean leaf() {
		return this.sons.size() == 0;
	}

	public LinkedList<Node> siblings() {
		if (parent == null)
			return null;
		LinkedList<Node> ll = (LinkedList<Node>) parent.getSons().clone();
		ll.remove(this);

//		System.out.println("siblings di "+ this.getId() +" -> "+ll);

		return ll;

	}

	public LinkedList<Node> ancestors() {
		LinkedList<Node> ll = new LinkedList<Node>();
		Node p = parent;
		while (p != null) {
			if (!(isMax ^ p.isMax))
				ll.add(p);
			p = p.getParent();
		}

//		System.out.println("ancestors di "+ this.getId() +" -> "+ll);

		return ll;
	}

	public boolean hasValue() {
		return hasValue;
	}

	public void reset() {
		hasValue = false;
	}

	public int getId() {
		return id;
	}

	/**
	 * DA CAMBIARE!!
	 */
	public boolean expandable() {
		return bc != 0 && wc != 0;
//		return true;
	}

	public String generateGenericVerbose(String prefix, boolean isRightMost, boolean isLeftMost,
			StringBuilder sb) {
		int halfSize = (sons.size() + 1) / 2;
		List<Node> children = new ArrayList<>(sons);
		for (int i = children.size() - 1; i >= halfSize; i--) {
			Node child = children.get(i);
			child.generateGenericVerbose(prefix + (isRightMost && !isLeftMost ? "    " : "│   "),
					child.equals(children.get(sons.size() - 1)) ? true : false,
					child.equals(children.get(0)) ? true : false, sb);
		}
		sb.append(prefix).append(isRightMost && isLeftMost ? "└── " : "")
				.append(isRightMost && !isLeftMost ? "┌── " : "").append(isLeftMost && !isRightMost ? "└── " : "")
				.append(!isRightMost && !isLeftMost ? "├── " : "")
				.append(toString()).append("\n");
		for (int i = halfSize - 1; i >= 0; i--) {
			Node child = children.get(i);
			children.get(i).generateGenericVerbose( prefix + (isLeftMost ? "    " : "│   "),
					child.equals(children.get(sons.size() - 1)) ? true : false,
					child.equals(children.get(0)) ? true : false, sb);
		}
		return sb.toString();
	}

}