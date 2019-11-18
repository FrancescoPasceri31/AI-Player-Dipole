package ricerca;


import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import rappresentazione.Tree;
import rappresentazione.Node;

public class Search {
	
	public Node search(Node t) {
		LinkedList<Node> l = new LinkedList();  //va settata più grande
		l.add(t);
		double alpha = Double.MIN_VALUE, beta = Double.MAX_VALUE;
		
		while(!l.isEmpty()) {
			Node x = l.getFirst(); 
			if(x.equals(t) && x.hasValue()) return x; //Perchè lo stiamo ritornando? Se c'è dobbiamo andare avanti, altrimenti passiamo al prossimo
			if(x.hasValue()) {
				Node p = x.getParent();
				boolean pruned = false;
				
				if(! p.isMax()) { //se nodo min
					alpha = max(p.siblings(), p.ancestors()); // false = cerco minimizzatori
					if(x.getValue()<=alpha) {
						for(Node n: p.getSons()) l.remove(n);
						l.remove(p);
						pruned = true;
					}
					
				}else if( p.isMax()) {
					beta = min(p.siblings(),p.ancestors()); // true = cerco massimizzatori
					if(x.getValue()>=beta) {
						for(Node n: p.getSons()) l.remove(n);
						l.remove(p);
						pruned = true;
					}
				}
				if(!pruned) {
					if(! p.isMax()) p.setValue(min(x.getValue(),p.getValue()));
					else p.setValue(max(x.getValue(),p.getValue()));
					l.remove(x);
				}
			}else {
				if(!x.hasValue() && (x.leaf() || !x.expandable())) {
					x.setValue(/*calcola euristica*/);
				}else {
					if(x.isMax()) x.setValue(Double.MIN_VALUE);
					else if(! x.isMax()) x.setValue(Double.MAX_VALUE);
					int i = 0;
					for(Node n: x.getSons()) {
						l.add(i, n);
						i++;
					}
				}
			}
		}
	}

	private double max(LinkedList<Node> siblings, LinkedList<Node> ancestors) {
		double max = Double.MIN_VALUE;
		for (Node node : ancestors) {
			if(node.getValue()>max) {
				max = node.getValue();
			}
		}
		
		for (Node node : ancestors) {
			if(node.hasValue() && node.getValue()>max) {
				max = node.getValue();
			}
		}
		return max;
	}

	private double min(LinkedList<Node> siblings, LinkedList<Node> ancestors) {
		double min = Double.MAX_VALUE;
		for (Node node : ancestors) {
			if(node.getValue()<min) {
				min = node.getValue();
			}
		}
		
		for (Node node : ancestors) {
			if(node.hasValue() && node.getValue()<min) {
				min = node.getValue();
			}
		}
		return min;
	}
}
