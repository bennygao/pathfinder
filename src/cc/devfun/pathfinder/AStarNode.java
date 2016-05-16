package cc.devfun.pathfinder;

public class AStarNode implements Comparable<AStarNode> {
	private static Table allNodes = new Table();
	
	public static synchronized AStarNode getNode(int x, int y) {
		AStarNode node = allNodes.get(x, y);
		if (node == null) {
			node = new AStarNode(x, y);
			allNodes.put(x, y, node);
		}
		
		return node;
	}
	
	public static void clearAllNodes() {
		for (AStarNode node : allNodes.getAllNodes()) {
			node.clear();
		}
	}
	
	public static AStarNode getNode(short[] posi) {
		return getNode(posi[1], posi[0]);
	}
	
	public static AStarNode newNode(int x, int y) {
		return new AStarNode(x, y);
	}
	
	private final static int[][] GMatrix = {
		{14, 10, 14},
		{10, 10, 10},
		{14, 10, 14}
	};

	private short[] posi = new short[2];
	
	private int g = 0; // 从起点source移动到当前点的耗费
	private int h = 0; // 从当前点到终点的估值耗费
	private int f = 0; // f = g + h
	private AStarNode father = null; // 父结点

	private AStarNode(int x, int y) {
		this.posi[1] = (short) x;
		this.posi[0] = (short) y;
	}
	
	private AStarNode(short[] posi) {
		this.posi[1] = posi[1];
		this.posi[0] = posi[0];
	}
	
	public void clear() {
		g = h = f = 0;
		father = null;
	}
	
	public short[] toShortArray() {
		return posi;
	}

	public int getX() {
		return this.posi[1];
	}

	public void setX(int x) {
		this.posi[1] = (short) x;
	}

	public int getY() {
		return this.posi[0];
	}

	public void setY(int y) {
		this.posi[0] = (short) y;
	}

	public AStarNode getFather() {
		return father;
	}

	public void setFather(AStarNode father) {
		this.father = father;
	}

	public void init(AStarNode target) {
		this.g = 0;
		this.h = heuristicCostEstimate(this, target);
		this.f = g + h;
	}

	/**
	 * 计算H
	 * 
	 * @param source
	 * @param target
	 * @return
	 */
	public int heuristicCostEstimate(AStarNode source, AStarNode target) {
		return (Math.abs(source.getX() - target.getX()) + Math.abs(source.getY() - target.getY()))
				* GMatrix[1][1];
	}

	@Override
	public int compareTo(AStarNode o) {
		return this.f < o.f ? -1 : 1;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof AStarNode)) {
			return false;
		}

		AStarNode node = (AStarNode) obj;
		return node.getX() == this.getX() && node.getY() == this.getY();
	}

	@Override
	public String toString() {
		return new StringBuilder().append(getX()).append(',').append(getY()).toString();
	}

	public int calculatorF(AStarNode father, AStarNode target) {
		this.g = getDistinctG(father);
		this.h = heuristicCostEstimate(this, target);
		this.f = g + h;
		this.father = father;
		return f;
	}

	public int getDistinctG(AStarNode father) {
		int offsetX = getX() - father.getX();
		int offsetY = getY() - father.getY();
		return GMatrix[offsetX + 1][offsetY + 1] + father.g;
	}

	public int getG() {
		return g;
	}

	/**
	 * 是否比指定的点更好
	 * 
	 * @param node
	 * @return
	 */
	public boolean isBetter(AStarNode node) {
		return isGBetter(node);
	}

	public boolean isGBetter(AStarNode node) {
		return g + getDistinctG(node) < node.g;
	}

	public boolean isFBetter(AStarNode node) {
		return f < node.f;
	}

	public int getF() {
		return f;
	}
}