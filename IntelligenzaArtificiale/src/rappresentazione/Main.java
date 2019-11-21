package rappresentazione;

import java.util.LinkedList;

import ricerca.Search;

public class Main {
	
	public static void main(String[] args) {
		
		Node root = new Node(null, 0,0,null,"");
		for(int i=0;i<3;i++) {
			root.addSon(new Node(root,0,0,null,""+root.getId()));
			root.getSons().getLast().setMossa(root.getSons().getLast().getMossa()+"."+root.getSons().getLast().getId());
		}
		
		double[][] values = {{3,12,8},{2,4,6},{14,5,2}};
		LinkedList<Node> sons = root.getSons();
		for(int i=0;i<3;i++) {
			for(int j=0;j<3;j++) {
				Node n = new Node(sons.get(i),0,0,null, ""+sons.get(i).getId());
				n.setMossa(n.getMossa()+"."+n.getId());
				n.setValue(values[i][j]);
				n.setHasValue(true);
				sons.get(i).addSon(n);
			}
		}
		
		Search s = new Search();
		Node best = s.search(root);
		
		
		System.out.println("Nodo best = "+best+", "+best.getValue()+" == "+root.getValue());
		System.out.println("Mossa nodo best "+best.getMossa());
	}

}
