package cc.devfun.pathfinder;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

class SimpleOpenList implements OpenList{
	private Table table;
	private List<AStarNode> list;
	
	public SimpleOpenList() {
		table = new Table();
		list = new LinkedList<AStarNode>();
	}

	@Override
	public void add(AStarNode node) {
		int x = node.getX();
		int y = node.getY();
		if (table.get(x, y) == null) {
			table.put(x, y, node);
			list.add(node);
		}
		
	}

	@Override
	public AStarNode poll() {
		if (table.size() == 0) {
			return null;
		}
		
		AStarNode result = null;
		AStarNode node = null;
		int minF = Integer.MAX_VALUE;
		Iterator<AStarNode> itr = list.iterator();
		int idx = 0, position = 0;
		while (itr.hasNext()) {
			node = itr.next();
			if (node.getF() < minF) {
				minF = node.getF();
				result = node;
				position = idx;
			}
			
			++idx;
		}
		
		table.remove(result.getX(), result.getY());
		list.remove(position);
		
		return result;
	}

	@Override
	public Collection<AStarNode> values() {
		return list;
	}

	@Override
	public AStarNode get(int x, int y) {
		return table.get(x, y);
	}

	@Override
	public int size() {
		return table.size();
	}

	@Override
	public void clear() {
		table.clear();
		list.clear();
		
	}

	@Override
	public void touchNeighbor(AStarNode neighbor, AStarNode current,
			AStarNode target) {
		AStarNode node = get(neighbor.getX(), neighbor.getY());
		if (node == null) {
			neighbor.calculatorF(current, target);
			add(neighbor);
		} else if (node.getDistinctG(current) < node.getG()) {
			node.calculatorF(current, target);
		}
	}
}
