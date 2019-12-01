package testing;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class NodeM implements Serializable{

	private static final long serialVersionUID = 1L;

	private static int gid = 0;
	private int id;
	private double value;
	private Map<String, NodeM> sons = new HashMap<String, NodeM>();
	private NodeM parent;
	private boolean isMax, hasValue = false;

	private int bc, wc; // mia configurazione e avversaria
	private String mossa;
//	private HashMap<Byte, Byte> posToPawns;
	private byte[] posToPawns;

	public NodeM(NodeM parent, int bc, int wc, byte[] posToPawns, String mossa) {
		this.parent = parent;
		this.bc = bc;
		this.wc = wc;
		this.posToPawns = posToPawns.clone(); 
		this.mossa = mossa;
		isMax = (parent == null) ? true : !parent.isMax();
		id = gid++;
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

	public void addSon(NodeM n) {
		sons.put(n.getMossa(), n);
	}

	public Map<String,NodeM> getSons() {
		return sons;
	}

	public void setSons(Map<String,NodeM> sons) {
		this.sons = sons;
	}

	public int numSons() {
		return sons.size();
	}

	public void setWc(int wc) {
		this.wc = wc;
	}

	public void setBc(int bc) {
		this.bc = bc;
	}

	public void setMossa(String mossa) {
		this.mossa = mossa;
	}

	public void setParent(NodeM parent) {
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
		return mossa;
	}

	public NodeM getParent() {
		return parent;
	}

	public byte[] getPosToPawns() {
		return posToPawns;
	}

	public String toString() {
		return "[ id: " + id + ", mossa: "+mossa+", value: "+value+" ]";
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

	public boolean equals(NodeM n) {
		return id == n.getId();
	}

	public boolean leaf() {
		return this.numSons() == 0;
	}

	public LinkedList<NodeM> siblings() {
		if (parent == null)
			return null;
		LinkedList<NodeM> ll = (LinkedList<NodeM>) parent.getSons().values();
		ll.remove(this);
		return ll;

	}

	public LinkedList<NodeM> ancestors() {
		LinkedList<NodeM> ll = new LinkedList<NodeM>();
		NodeM p = parent;
		while (p != null) {
			if (!(isMax ^ p.isMax))
				ll.add(p);
			p = p.getParent();
		}
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

}