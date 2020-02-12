package euristica;

import java.util.HashMap;

import generators.HashMapGenerator;
import rappresentazione.Node;

public class Euristica {

	private HashMap<String, Byte> cellToPos = null;
	private HashMap<Byte, Object[]> masksBlack = null;
	private HashMap<Byte, Object[]> masksWhite = null;

	public void init(HashMap<String, Byte> cellToPos, HashMap<Byte, Object[]> masksBlack,
			HashMap<Byte, Object[]> masksWhite) {
		this.cellToPos = cellToPos;
		this.masksBlack = masksBlack;
		this.masksWhite = masksWhite;
	}

	public double strategy_0(Node n, boolean isWhite) { // conta le pedine sulla scacchiera in base al colore
		byte[] posToPawn = n.getPosToPawns();
		int sum = 0;
		for (int i = 0; i < 32; i++) {
			if (posToPawn[i] > 0)
				sum += (isWhite && posToPawn[i] <= 12) ? posToPawn[i]
						: (!isWhite && posToPawn[i] > 12) ? posToPawn[i] - 20 : 0;
		}
		return sum / 12;
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
		return (ret + 12) / 12;

	}

	public double strategy_2(Node n, boolean isWhite) { // non muovermi troppo in avanti, 2 pedine vanno bene
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
				ret = (numPedine - 1) / 6; // maxNumPed=7,minNumPed=1
			}
		} else {
			if (direzione.charAt(0) == 'S') { // mossa in avanti
				if (cella.charAt(0) >= 'E') {
					ret += daiPesi2(numPedine);
				} else {
					ret += daiPesi1(numPedine, cella.charAt(0), 'A', 'B');
				}
			} else { // mossa all'indietro
				ret = (numPedine - 1) / 6;
			}

		}
		return ret;
	}

	public double daiPesi2(byte numPedine) {
		switch (numPedine) {
		case 1:
			return 0.7;
		case 2:
			return 0.6;
		case 3:
			return 0.4;
		case 4:
			return 0.2;
		default:
			return 0.1;
		}

	}

	public double daiPesi1(byte numPedine, char cella, char c1, char c2) {
		if (cella == c1 || cella == c2) {
			switch (numPedine) {
			case 1:
				return 0.7;
			case 2:
				return 0.9;
			case 3:
				return 0.5;
			case 4:
				return 0.4;
			default:
				return 0.1;
			}
		} else {
			switch (numPedine) {
			case 1:
				return 0.8;
			case 2:
				return 0.9;
			case 3:
				return 0.5;
			case 4:
				return 0.2;
			default:
				return 0.1;
			}
		}

	}

	public double strategy_3(Node n, boolean isWhite) { // minimizzare pedine dell'avversario
		return 1 - strategy_0(n, !isWhite);
	}

	public double strategy_4(Node n, boolean isWhite) {
		if (n.getParent() == null)
			return 0;
		double riga = (double) (cellToPos.get(n.getCella()) / 4);
		if (isWhite) {
			if (n.getDirezione().charAt(0) == 'N') // avanti
				return -riga / 7;
		} else {
			if (n.getDirezione().charAt(0) == 'S')
				return -(7 - riga) / 7;
		}
		return 0;
	}

	public double strategy_5(Node n, boolean isWhite) { // non buttarti fuori
		if (n.getParent() == null)
			return 0;
		return (isOut(n, isWhite)) ? -((Integer.parseUnsignedInt(n.getPedine()) / 12.0)) : 0.0;
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
				return p >= HashMapGenerator.getOutLeastPawns(masksBlack, pos)[2];
			case "SE":
				return p >= HashMapGenerator.getOutLeastPawns(masksBlack, pos)[0];
			default:
				return false;
			}

		}
	}

	public double getEuristica(Node n, boolean isWhite, boolean myColor) {
		if (!(isWhite ^ myColor)) { // isWhite == myColor
			return 1.5 * strategy_0(n, myColor) + 3.75 * strategy_1(n, myColor) + strategy_2(n, myColor)
					+ 1.95 * strategy_3(n, myColor) + strategy_4(n, myColor) + 1.75 * strategy_5(n, myColor);
		} else {
			return -1.5 * strategy_0(n, isWhite) - 3.75 * strategy_1(n, isWhite) - strategy_2(n, isWhite)
					- 1.95 * (strategy_3(n, isWhite) - 1) - strategy_4(n, isWhite) - 1.75 * strategy_5(n, isWhite);
		}
	}

}
