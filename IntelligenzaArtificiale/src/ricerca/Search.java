package ricerca;

import java.util.LinkedList;
import java.util.Random;

import generators.MovesGenerator;
import playerAI.PlayerDipole;
import rappresentazione.Node;

public class Search extends Thread {

	private MovesGenerator mg;

	private final double VICTORY = 1210037.0;
	private final double LOSE = -1510237.0;

	public void init(MovesGenerator mg) {
		this.mg = mg;
	}

	public Node bestTmp;
	static double bestTmpValue = -Double.MAX_VALUE;

	@Override
	public void run() {
		Node n = PlayerDipole.root;
		boolean isWhite = PlayerDipole.isWhite;
		int maxLevel = PlayerDipole.maxLevel;

		double v = maxVal(n, -Double.MAX_VALUE, Double.MAX_VALUE, isWhite, 0, maxLevel).getValue();
		LinkedList<Node> l = new LinkedList<Node>();
		for (Node f : n.getSons())
			if (f.getValue() == v)
				l.add(f);
		if (l.size() == 1)
			PlayerDipole.best = l.getFirst();
		try {
			PlayerDipole.best = l.get((new Random()).nextInt(l.size()));
		} catch (IllegalArgumentException e) {
			PlayerDipole.best = n.getSons().getFirst();
		}
	}

	public Node maxVal(Node n, double alpha, double beta, boolean isWhite, int level, int maxLevel) {
		Node ret = n;
		if (testTerminazione(n, level, maxLevel)) {
			if (isWhite && n.getBc() == 0)
				n.setValue(VICTORY - level);
			else if (isWhite && n.getWc() == 0)
				n.setValue(LOSE + level);
			else if (!isWhite && n.getWc() == 0)
				n.setValue(VICTORY - level);
			else if (!isWhite && n.getBc() == 0)
				n.setValue(LOSE + level);
			return n;
		}
		if (n.getSons().size() == 0)
			mg.generateMoves(n, isWhite, isWhite);

		double v = -Double.MAX_VALUE;

		double th = -Double.MAX_VALUE;
		for (Node f : n.getSons()) {

			if (n.getParent() == null) {
				if (f.getValue() > bestTmpValue) {
					bestTmp = f;
					bestTmpValue = f.getValue();
				}
			}

			if (f.getValue() > th) {
				if (f.getSons().size() == 0)
					mg.generateMoves(f, !isWhite, isWhite);
				double tmp = f.getValue();
				f.setValue(f.getValue() - (double) f.getAttacks());
				if (f.getValue() > th)
					th = tmp;
				else
					continue;
			} else
				continue;

			double min = (minVal(f, alpha, beta, isWhite, level + 1, maxLevel)).getValue();
			v = Math.max(v, min);
			if (v == min) {
				ret = f;
			}
			n.setValue(v);
			if (v >= beta) {
				return n;
			}
			alpha = Math.max(alpha, v);
		}
		return ret;
	}

	public Node minVal(Node n, double alpha, double beta, boolean isWhite, int level, int maxLevel) {
		Node ret = n;
		if (testTerminazione(n, level, maxLevel)) {
			if (isWhite && n.getBc() == 0)
				n.setValue(VICTORY + level);
			else if (isWhite && n.getWc() == 0)
				n.setValue(LOSE - level);
			else if (!isWhite && n.getWc() == 0)
				n.setValue(VICTORY + level);
			else if (!isWhite && n.getBc() == 0)
				n.setValue(LOSE - level);
			return n;
		}
		if (n.getSons().size() == 0)
			mg.generateMoves(n, !isWhite, isWhite);

		double v = Double.MAX_VALUE;

		double th = Double.MAX_VALUE;
		for (Node f : n.getSons()) {
			if (f.getValue() < th) {
				if (f.getSons().size() == 0)
					mg.generateMoves(f, isWhite, isWhite);
				double tmp = f.getValue();
				f.setValue(f.getValue() + (double) f.getAttacks());
				if (f.getValue() < th)
					th = tmp;
				else
					continue;
			} else
				continue;

			double max = (maxVal(f, alpha, beta, isWhite, level + 1, maxLevel)).getValue();
			v = Math.min(v, max);
			if (v == max) {
				ret = f;
			}
			n.setValue(v);
			if (v <= alpha) {
				return n;
			}
			beta = Math.max(beta, v);
		}
		return ret;

	}

	public boolean testTerminazione(Node n, int level, int maxLevel) {
		return level >= maxLevel || n.getBc() == 0 || n.getWc() == 0;
	}
}
