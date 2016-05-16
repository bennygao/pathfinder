package cc.devfun.pathfinder;

import java.util.Collection;

public interface OpenList {
	public void add(AStarNode node);
	public AStarNode poll();
	public Collection<AStarNode> values();
	public AStarNode get(int x, int y);
	public int size();
	public void clear();
	public void touchNeighbor(AStarNode neighbor, AStarNode current, AStarNode target);
}
