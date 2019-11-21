package rappresentazione;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Node {
	
	
	private static int gid = 0;
	private int id;
	private double value = (new Random()).nextDouble();
	private LinkedList<Node> sons = new LinkedList<Node>();
	private Node parent;
	private boolean isMax, hasValue=false;
	private static int size = 0;

	private int bc, wc;	//mia configurazione e avversaria
	private String mossa;
	private HashMap<Byte, Byte> posToPawns;
	

	public Node(Node copy) {
		this.parent = copy.parent;
		this.bc = copy.bc;
		this.wc = copy.wc;
		this.posToPawns = (HashMap<Byte, Byte>) copy.posToPawns.clone();
		this.mossa=copy.mossa;
		isMax = copy.isMax();
		id = copy.id;
	}
	
	public Node(Node parent, int bc, int wc, HashMap<Byte, Byte> posToPawns, String mossa) {
		this.parent = parent;
		this.bc = bc;
		this.wc = wc;
		this.posToPawns = (HashMap<Byte, Byte>) posToPawns.clone();
		this.mossa=mossa;
		isMax = (parent==null)? true : !parent.isMax();
		id = gid++;
		size++;
	}
	
	public int getSize() {
		return size;
	}

	public void setHasValue(boolean hasValue) {
		this.hasValue=hasValue;
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

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public void setPosToPawns(HashMap<Byte, Byte> posToPawns) {
		this.posToPawns = (HashMap<Byte, Byte>) posToPawns.clone();
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

	public Node getParent() {
		return parent;
	}

	public HashMap<Byte, Byte> getPosToPawns() {
		return posToPawns;
	}

	@Override
	public String toString() {
		return "[ id: " + id +", bc: "+toBinaryString(bc)+", wc: "+toBinaryString(wc)+", mossa: "+ mossa + " ]";
	}

	private String toBinaryString(int i) {
		String tmp = Integer.toBinaryString(i);
		while(tmp.length()<32) {
			tmp = "0"+tmp;
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
		return this.numSons()==0;
	}

	public LinkedList<Node> siblings() {
		if(parent==null)return null;
		LinkedList<Node> ll = (LinkedList<Node>) parent.getSons().clone();
		ll.remove(this);
		
//		System.out.println("siblings di "+ this.getId() +" -> "+ll);
		
		return ll;
		
	}

	public LinkedList<Node> ancestors() {	
		LinkedList<Node> ll = new LinkedList<Node>();
		Node p = parent;
		while(p!=null) {
				if(!(isMax ^ p.isMax)) 
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
		return true;
	}
	
	public static String generateGenericVerbose(Node node, String prefix, boolean isRightMost, boolean isLeftMost, StringBuilder sb) {
        int halfSize = (node.getSons().size() + 1) / 2;
        List<Node> children = new ArrayList<>(node.getSons());
        for (int i = children.size() - 1 ; i >= halfSize; i--) {
            Node child = children.get(i);
            generateGenericVerbose(child, 
                    prefix + (isRightMost && !isLeftMost ? "    " : "│   "), 
                    child.equals(children.get(node.getSons().size()-1)) ? true : false,
                    child.equals(children.get(0))  ? true : false,
                    sb);
        }
        sb.append(prefix).
        append(isRightMost && isLeftMost ? "└── " : "").
        append(isRightMost && !isLeftMost  ? "┌── " : "").
        append(isLeftMost  && !isRightMost ? "└── " : "").
        append(!isRightMost && !isLeftMost ? "├── " : "").
        append(node.toString()+" - "+node.getPosToPawns()).
        append("\n");
        for (int i = halfSize - 1; i >= 0; i--) {
            Node child = children.get(i);
            generateGenericVerbose(children.get(i), 
                    prefix + (isLeftMost ? "    " : "│   "),
                    child.equals(children.get(node.getSons().size()-1))? true : false,
                    child.equals(children.get(0))  ? true : false,
                    sb);
        }
        return sb.toString();
    }
	
	

}