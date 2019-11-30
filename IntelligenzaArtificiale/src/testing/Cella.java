package testing;

public class Cella {
	public int info;
	public String colore;

	public Cella() {
		info = -1;
		colore = "";
	}

	@Override
	public String toString() {
		return "Cella [" + colore + "]";
	}
}