package cc.devfun.pathfinder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class FinderContext {
	private boolean achieved = false;
	private OpenList openList = new BinaryHeapOpenList();
	private Table closeMap = new Table();
	private AStarNode source = null, target = null;
	private TiledMap map = new TiledMap();
	private List<AStarNode> path = new ArrayList<AStarNode>();

	public FinderContext() {
	}
	
	public void setMap(byte[][] data) {
		map.setData(data);
	}
	
	public TiledMap getMap() {
		return map;
	}
	
	public void reset(AStarNode source, AStarNode target) {
		this.source = source;
		this.target = target;
		
		achieved = false;
		openList.clear();
		closeMap.clear();
		path.clear();
		
		// 把起点(source)加入到openList
		openList.add(source);
	}
	
	public List<AStarNode> getPath() {
		return path;
	}
	
	public Collection<AStarNode> getOpenList() {
		return openList.values();
	}
	
	public AStarNode getMinFNode() {
		return openList.poll();
	}
	
	public AStarNode getOpenNode(AStarNode node) {
		return openList.get(node.getX(), node.getY());
	}
	
	public boolean isClosedListContains(AStarNode node) {
		return isClosedListContains(node.getX(), node.getY());
	}
	
	public boolean isClosedListContains(int x, int y) {
		return closeMap.contains(x, y);
	}
	
	public boolean isEmpty() {
		return openList.size() == 0;
	}
	
	public AStarNode getSource() {
		return source;
	}

	public AStarNode getTarget() {
		return target;
	}

	public boolean isAchieved() {
		return achieved;
	}
	
	public void achieved() {
		achieved = true;
	}
	
	public void addToClosedList(AStarNode node) {
		closeMap.put(node.getX(), node.getY(), node);
	}
	
	public void addToOpenList(AStarNode node) {
		openList.add(node);
	}
	
	public void touchNeighbor(AStarNode neighbor, AStarNode current, AStarNode target) {
		openList.touchNeighbor(neighbor, current, target);
	}
}
