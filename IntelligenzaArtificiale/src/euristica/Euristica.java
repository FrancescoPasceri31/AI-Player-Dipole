package euristica;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;

import generators.HashMapGenerator;
import generators.MovesGenerator;
import rappresentazione.Node;

public class Euristica {
	

	private HashMap<String, Byte> cellToPos = null;
	private HashMap<Byte, Object[]> masksBlack = null;
	private HashMap<Byte, Object[]> masksWhite = null;

	@SuppressWarnings("unchecked")
	public void init() {
		try {
			ObjectInputStream i = new ObjectInputStream(new FileInputStream("hashMaps"));
			i.readObject();
			cellToPos = (HashMap<String, Byte>) i.readObject();
			masksBlack = (HashMap<Byte, Object[]>) i.readObject();
			masksWhite = (HashMap<Byte, Object[]>) i.readObject(); 
			i.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public double strategy_0(Node n, boolean isWhite) { // conta le pedine sulla scacchiera in base al colore
		byte[] posToPawn = n.getPosToPawns();
		int sum = 0;
		for (int i = 0; i < 32; i++) {
			if (posToPawn[i] > 0)
				sum += (isWhite && posToPawn[i] <= 12) ? posToPawn[i]
						: (!isWhite && posToPawn[i] > 12) ? posToPawn[i] - 20 : 0;
		}
		return (sum - 12);
	}

	public double strategy_1(Node n, boolean isWhite) { // non perdere pedine
		Node p = n.getParent();
		if (p == null)
			return 0;
		byte[] pawnsChild = n.getPosToPawns();
		byte[] pawnsParent = p.getPosToPawns();
		int childConf, parentConf;
		if (isWhite) {
			childConf = n.getWc();
			parentConf = p.getWc();
		} else {
			childConf = n.getBc();
			parentConf = p.getBc();
		}
		byte[] childPos = HashMapGenerator.onesPosition(childConf);
		byte[] parentPos = HashMapGenerator.onesPosition(parentConf);
		double ret = 0;
		for (int i = 0; i < childPos.length; i++) {
			byte x = pawnsChild[childPos[i]];
			if (!isWhite && x != 0)
				x -= 20;
			ret += x;
		}
		for (int i = 0; i < parentPos.length; i++) {
			byte x = pawnsParent[parentPos[i]];
			if (!isWhite && x != 0)
				x -= 20;
			ret -= x;
		}
		return ret;

	}

	public  double strategy_2(Node n, boolean isWhite) { // non muovermi troppo in avanti, 2 pedine vanno bene
		if (n.getParent() == null)
			return 0;
		String cella = n.getCella();
		String direzione = n.getDirezione();
		byte numPedine = Byte.parseByte(n.getPedine());
		double ret = 0;
		if (isWhite) {
			if (direzione.charAt(0) == 'N') { // mossa in avanti
				if (cella.charAt(0) <= 'D') {
					ret += daiPesi2(numPedine);
				} else {
					ret += daiPesi1(numPedine, cella.charAt(0), 'H', 'G');
				}
			} else { // mossa all'indietro
				ret = 12 + numPedine;
			}
		} else {
			if (direzione.charAt(0) == 'S') { // mossa in avanti
				if (cella.charAt(0) >= 'E') {
					ret += daiPesi2(numPedine);
				} else {
					ret += daiPesi1(numPedine, cella.charAt(0), 'A', 'B');
				}
			} else { // mossa all'indietro
				ret = 12 + numPedine;
			}

		}
		return ret;

	}

	public  double daiPesi2(byte numPedine) {
		switch (numPedine) {
		case 1:
			return -1;
		case 2:
			return -4;
		case 3:
			return -7;
		case 4:
			return -10;
		default:
			return -12; 
		}

	}

	public  double daiPesi1(byte numPedine, char cella, char c1, char c2) {
		if (cella == c1 || cella == c2) {
			switch (numPedine) {
			case 1:
				return -3;
			case 2:
				return -1;
			case 3:
				return -6;
			case 4:
				return -9;
			default:
				return -12;
			}
		} else {
			switch (numPedine) {
			case 1:
				return -4;
			case 2:
				return -1;
			case 3:
				return -6;
			case 4:
				return -10;
			default:
				return -12;
			}
		}

	}

	public  double strategy_3(Node n, boolean isWhite) { // minimizzare pedine dell'avversario
		return -strategy_0(n, !isWhite);// * (isWhite ? 13.5 : 27.71);
	}

	public double strategy_4(Node n, boolean isWhite) {
		double ret;
		if (n.getParent() == null)
			return 0;
		byte riga = (byte) (cellToPos.get(n.getCella()) / 4);
		if (isWhite)
			ret = -riga * 12 / 7;// 1.71428571; // 12/7
//			ret=(riga-7)*1.71428571; //12/7
		else
			ret = -(7 - riga) * 12 / 7;// 1.71428571;
		return ret;
	}

	public double strategy_5(Node n, boolean isWhite) { // non buttarti fuori
		if (n.getParent() == null)
			return 0;
		return (isOut(n, isWhite)) ? -(/*7351 */ Integer.parseUnsignedInt(n.getPedine())) : 0;
	}

	public boolean isOut(Node n, boolean isWhite) {
		byte pos = cellToPos.get(n.getCella());
		String dir = n.getDirezione();
		byte p = Byte.parseByte(n.getPedine());
		if (isWhite) {
			switch (dir) {
			case "N":
				return p >= HashMapGenerator.getOutLeastPawns(masksWhite, pos)[1];
			case "NW":
				return p >= HashMapGenerator.getOutLeastPawns(masksWhite, pos)[0];
			case "NE":
				return p >= HashMapGenerator.getOutLeastPawns(masksWhite, pos)[2];
			default:
				return false;
			}
		} else {
			switch (dir) {
			case "S":
				return p >= HashMapGenerator.getOutLeastPawns(masksBlack, pos)[1];
			case "SW":
				return p >= HashMapGenerator.getOutLeastPawns(masksBlack, pos)[0];
			case "SE":
				return p >= HashMapGenerator.getOutLeastPawns(masksBlack, pos)[2]; 
			default:
				return false; 
			}

		}
	}


	public double getEuristica(Node n, boolean isWhite) {
		double ret = (7*strategy_0(n, isWhite)) +(strategy_2(n, isWhite)) + (strategy_3(n, isWhite))
				+ (8*strategy_5(n, isWhite))+ (3*strategy_4(n, isWhite));
		return ret;
	}

}
