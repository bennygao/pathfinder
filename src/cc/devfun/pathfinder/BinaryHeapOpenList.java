package cc.devfun.pathfinder;

import java.util.Collection;

public class BinaryHeapOpenList implements OpenList {
	private BinaryHeap<AStarNode> heap;
	
	public BinaryHeapOpenList() {
		heap = new BinaryHeap<AStarNode>();
	}

	@Override
	public void add(AStarNode node) {
		heap.add(node);
	}

	@Override
	public AStarNode poll() {
		return heap.pop();
	}

	@Override
	public Collection<AStarNode> values() {
		return null;
	}

	@Override
	public AStarNode get(int x, int y) {
		AStarNode node = AStarNode.getNode(x, y);
		int idx = heap.indexOf(node);
		return idx >= 0 ? heap.getAt(idx) : null;
	}

	@Override
	public int size() {
		return heap.size();
	}

	@Override
	public void clear() {
		heap.clear();
		
	}

	@Override
	public void touchNeighbor(AStarNode neighbor, AStarNode current,
			AStarNode target) {
		int index = heap.indexOf(neighbor);
		if (index < 0) {
			neighbor.calculatorF(current, target);
			add(neighbor);
		} else {
			AStarNode node = heap.getAt(index);
			if (node.getDistinctG(current) < node.getG()) {
				int oldF = node.getF();
				int newF = node.calculatorF(current, target);
				if (oldF != newF) {
					heap.modifiedAt(index);
				}
			}
		}
		
	}


}
