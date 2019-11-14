package rappresentazione;

public class Main {
	
	public static void main(String[] args) {
		Tree t = new Tree();
		t.createSons();
		t.createSons();
		//((Tree)t.getSons().get(0)).createSons();
		System.out.println(t);
	}

}
