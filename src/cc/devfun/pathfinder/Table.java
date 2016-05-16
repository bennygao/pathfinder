package cc.devfun.pathfinder;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

class Table {
	private Map<Long, AStarNode> map = new HashMap<Long, AStarNode>();
	
	public AStarNode get(int x, int y) {
		return map.get(getKey(x, y));
	}
	
	public void put(int x, int y, AStarNode node) {
		map.put(getKey(x, y), node);
	}
	
	public AStarNode remove(int x, int y) {
		return map.remove(getKey(x, y));
	}
	
	public void clear() {
		map.clear();
	}
	
	public Collection<AStarNode> getAllNodes() {
		return map.values();
	}
	
	public boolean contains(int x, int y) {
		return map.containsKey(getKey(x, y));
	}
	
	public int size() {
		return map.size();
	}
	
	private Long getKey(int x, int y) {
		long key = x;
		return key << 32 | y;
	}
}
