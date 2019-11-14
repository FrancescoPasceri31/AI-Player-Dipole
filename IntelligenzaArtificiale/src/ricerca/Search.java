package ricerca;


import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import rappresentazione.Tree;
import rappresentazione.Node;

public class Search {
	
	public Node search(Node t) {
		LinkedList<Node> l = new LinkedList();  //va settata più grand
		l.add(t);
		Double alpha = null, beta = null;
		
		while(!l.isEmpty()) {
			Node x = l.getFirst();
			if(x.equals(t) && x.hasValue()) return x;
			if(x.hasValue()) {
				Tree p = x.parent();
				boolean pruned = false;
				if(p.isMin()) {
					alpha = max(p.siblings(),p.ancestors(min));
					if(alpha==null) alpha = Double.MIN_VALUE;
					if(x.getValue()<=alpha) {
						for(Node n: p.getSons()) l.remove(n);
						l.remove(p);
						pruned = true;
					}
				}else if(p.isMax()) {
					beta = min(p.siblings(),p.ancestors(!min));
					if(beta==null) beta = Double.MAX_VALUE;
					if(x.getValue()>=beta) {
						for(Node n: p.getSons()) l.remove(n);
						l.remove(p);
						pruned = true;
					}
				}
				if(!pruned) {
					if(p.isMin()) p.setValue(min(x.getValue(),p.getValue()));
					else p.setValue(max(x.getValue(),p.getValue()));
				}
			}else {
				if(!x.hasValue() && (x.leaf() || !x.expandable())) {
					x.setValue(/*calcola euristica*/);
				}else {
					if(x.isMax()) x.setValue(Double.MIN_VALUE);
					else if(x.isMin()) x.setValue(Double.MAX_VALUE);
					int i = 0;
					for(Node n: x.getSons()) {
						l.add(i, n);
						i++;
					}
				}
			}
		}
	}

}
