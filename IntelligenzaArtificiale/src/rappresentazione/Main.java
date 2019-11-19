package rappresentazione;

import java.util.LinkedList;

import ricerca.Search;

public class Main {
	
	public static void main(String[] args) {
		
		Node root = new Node(null, 0,0,null);
		for(int i=0;i<3;i++) {
			root.addSon(new Node(root,0,0,null));
		}
		
		double[][] values = {{3,12,8},{2,4,6},{14,5,2}};
		LinkedList<Node> sons = root.getSons();
		for(int i=0;i<3;i++) {
			for(int j=0;j<3;j++) {
				Node n = new Node(sons.get(i),0,0,null);
				n.setValue(values[i][j]);
				sons.get(i).addSon(n);
			}
		}
		
		Search s = new Search();
		s.search(root);
		
		System.out.println(root.getValue());
	}

}
