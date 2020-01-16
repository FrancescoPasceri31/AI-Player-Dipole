package testing;

import java.util.Comparator;

import rappresentazione.Node;

public class ByValue implements Comparator<Node> {

	@Override
	public int compare(Node arg0, Node arg1) {
		return (int) (arg1.getValue()-arg0.getValue());
	}



}
