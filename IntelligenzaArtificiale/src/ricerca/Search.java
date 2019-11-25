package ricerca;


import java.util.LinkedList;
import java.util.Random;

import rappresentazione.Node;

public class Search {
	
	public Node search(Node t) {
		int z = 0;
		LinkedList<Node> l = new LinkedList();  //va settata più grande
		l.add(t);
		double alpha = Double.MIN_VALUE, beta = Double.MAX_VALUE;
		int p_min =0 ,p_max = 0, not_pruned =0;
		Node best=null;
		double m; 		// valore del miglior figlio
		while(!l.isEmpty()) {
			z++;
			Node x = l.getFirst(); 
			if(x.equals(t) && x.hasValue()) {
//				System.out.println("iterazioni: "+z+"\npruned min: "+p_min+"\npruned max: "+p_max+"\nnot pruned: "+not_pruned); 
				return best;
			}
			if(x.hasValue()) {
				Node p = x.getParent();
				boolean pruned = false;
				
				if(! p.isMax()) { //se nodo min
					alpha = max(p.siblings(), p.ancestors()); // false = cerco minimizzatori
					if(x.getValue()<=alpha) {
						for(Node n: p.getSons()) l.remove(n);
						l.remove(p);
						pruned = true;
						p_min++;
					}
					
				}else if( p.isMax()) {
					beta = min(p.siblings(),p.ancestors()); // true = cerco massimizzatori
					if(x.getValue()>=beta) {
						for(Node n: p.getSons()) l.remove(n);
						l.remove(p);
						pruned = true;
						p_max++;
					}
				}
				if(!pruned) {
					not_pruned++;
					if(! p.isMax()) {
						p.setValue(Math.min(x.getValue(),p.getValue()));
					}
					else {
						m = Math.max(x.getValue(),p.getValue());
						p.setValue(m);
						if( p.equals(t) &&  m==x.getValue()) {
							best=x;
						}
					}
					p.setHasValue(true);
					l.remove(x);
				}
			}else {
				if(!x.hasValue() && (x.leaf() || !x.expandable())) {
					x.setValue((new Random()).nextDouble());
//					x.setValue(z);
					x.setHasValue(true);
				}else {
					if(x.isMax()) x.setValue(Double.MIN_VALUE);
					else if(! x.isMax()) x.setValue(Double.MAX_VALUE);
					int i = 0;
					for(Node n: x.getSons()) {
						l.add(i, n);
						i++;
					}
					//System.out.println(l);
				}
			}
		}
		return null; 	// DA CAMBIARE!!
	}

	
	public Node recursiveSearch(Node n) {
		return maxVal(n,Double.MIN_VALUE,Double.MAX_VALUE);
	}
	
	public Node maxVal(Node n, double alpha, double beta) {
//		System.out.println(n.getId());
		Node ret = null;
		if(testTerminazione(n)) { n.setValue((new Random()).nextDouble()); return n;} //da cambiare
		double v = Double.MIN_VALUE;
		for(Node f: n.getSons()) {
			double min = (minVal(f,alpha,beta)).getValue();
			v = Math.max(v, min);
			if(v==min) ret=f;
			if(v >= beta) return n;
			alpha = Math.max(alpha, v);
		}
		if(ret!=null)
			return ret;
		return n;
	}
	
	public Node minVal(Node n, double alpha,double beta) {
//		System.out.println(n.getId());
		Node ret = null;
		if(testTerminazione(n)) { n.setValue((new Random()).nextDouble()); return n;} //da cambiare
		double v = Double.MAX_VALUE;
		for(Node f: n.getSons()) {
			double max = (maxVal(f,alpha,beta)).getValue();
			v = Math.min(v, max);
			if(v==max) ret=f;
			if(v <= alpha) return n;
			beta = Math.max(beta, v);
		}
		if(ret!=null)
			return ret;
		return n;
		
	}
	
	public boolean testTerminazione(Node n) {
		return n.leaf() || !n.expandable();
	}
	
	private double max(LinkedList<Node> siblings, LinkedList<Node> ancestors) {
		double max = Double.MIN_VALUE;
		for (Node node : ancestors) {
			if(node.getValue()>max) {
				max = node.getValue();
			}
		}
		
		if(siblings!=null) {
			for (Node node : siblings) {
				if(node.hasValue() && node.getValue()>max) {
					max = node.getValue();
				}
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
		
		if(siblings!=null) {
			for (Node node : siblings) {
				if(node.hasValue() && node.getValue()<min) {
					min = node.getValue();
				}
			}
		}
		return min;
	}
}
