package cc.devfun.pathfinder;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class AStarPathFinder {
	private FinderContext ctx = new FinderContext();
	private LinkedList<short[]> path;
	

	public AStarPathFinder() {
		path = new LinkedList<short[]>();
	}

	/**
	 * 地图格子信息，第一维Y轴，第二维X轴，值为1表示可通过
	 */
	public void setMap(byte[][] mapData) {
		ctx.setMap(mapData);
	}

	private TiledMap getMap() {
		return ctx.getMap();
	}

	public Collection<AStarNode> getOpenList() {
		return ctx.getOpenList();
	}

	public LinkedList<short[]> searchPath(short[] startPos, short[] objectPos) {
		List<AStarNode> astarPath = findPath(startPos, objectPos);
		if (path == null) {
			return null;
		} else {
			path.clear();
			for (AStarNode n : astarPath) {
				path.add(n.toShortArray());
			}

			return path;
		}
	}
	
	public void setSearchTimes(short maxTimes) {
	}
	
	public void resetList() {
	}
	
	public void setLimit(byte[] limit) {
	}
	
	public void release() {
	}
	
	public void clear() {
	}

	/**
	 * 搜索算法
	 */
	public List<AStarNode> findPath(short[] srcPos, short[] targetPos) {
		// 初始化数据 开启列表和关闭列表 将源结点加入到开启列表中
		AStarNode.clearAllNodes();
		AStarNode source = AStarNode.getNode(srcPos);
		AStarNode target = AStarNode.getNode(targetPos);
		source.init(target);
		ctx.reset(source, target);

		TiledMap map = ctx.getMap();
		AStarNode current = null;
		List<AStarNode> path = null;
		int x, y;
		int hNum = getMap().getHorizontalTilesNum();
		int vNum = getMap().getVerticalTilesNum();

		while (!ctx.isEmpty() && !ctx.isAchieved()) {
			current = ctx.getMinFNode();
			if (isAchieve(current, target)) { // 是否已经完成寻路
				ctx.achieved();
				path = buildPath(ctx, current);
			} else {
				ctx.addToClosedList(current);
				for (int i = 0; i < 9; ++i) { // 遍历临近节点
					x = current.getX() + i / 3 - 1;
					y = current.getY() + i % 3 - 1;
					
					if (x < 0 || y < 0 || x >= hNum || y >= vNum
							|| (x == current.getX() && y == current.getY()) /* 自己 */
							|| ctx.isClosedListContains(x, y)
							|| isCannotGo(map, current, x, y)) {
						continue;
					} else {
						AStarNode neighbor = AStarNode.getNode(x, y);
						ctx.touchNeighbor(neighbor, current, target);
					}
				}
			}
		}

		return path;
	}

	/**
	 * 判断从from结点到to结点是否不可行
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	private boolean isCannotGo(TiledMap tiledMap, AStarNode from, int toX,
			int toY) {
		if (tiledMap.isBarrier(toX, toY)) { /* 如果这一格已经是障碍物，那么不能走 */
			return true;
		} else { /* 如果他旁边 */
			int offsetX = from.getX() - toX;
			int offsetY = from.getY() - toY;
			if (Math.abs(offsetX) == 1 && Math.abs(offsetY) == 1) { // 只有在走斜线的时候才要继续判断
				if ((offsetX == 1
						&& offsetY == -1
						&& (isValidX(from.getX() - 1)
								&& tiledMap.isBarrier(from.getX() - 1,
										from.getY()) || isValidY(from.getY() + 1)
								&& tiledMap.isBarrier(from.getX(),
										from.getY() + 1)) || (offsetX == 1
						&& offsetY == 1
						&& (isValidY(from.getY() - 1)
								&& tiledMap.isBarrier(from.getX(),
										from.getY() - 1) || isValidX(from
								.getX() - 1)
								&& tiledMap.isBarrier(from.getX() - 1,
										from.getY()))
						|| (offsetX == -1 && offsetY == 1 && (isValidX(from
								.getX() + 1)
								&& tiledMap.isBarrier(from.getX() + 1,
										from.getY()) || isValidY(from.getY() - 1)
								&& tiledMap.isBarrier(from.getX(),
										from.getY() - 1))) || (offsetX == -1
						&& offsetY == -1 && (isValidX(from.getX() + 1)
						&& tiledMap.isBarrier(from.getX() + 1, from.getY()) || isValidY(from
						.getY() + 1)
						&& tiledMap.isBarrier(from.getX(), from.getY() + 1))))))
					return true;
			}
		}
		return false;
	}

	private boolean isValidX(int x) {
		return x >= 0 && x < getMap().getHorizontalTilesNum();
	}

	private boolean isValidY(int y) {
		return y >= 0 && y < getMap().getVerticalTilesNum();
	}

	private List<AStarNode> buildPath(FinderContext ctx, AStarNode current) {
		List<AStarNode> path = ctx.getPath();
		path.clear();
		while (current != null) {
			path.add(0, current);
			current = current.getFather();
		}

		return path;
	}

	/**
	 * 比较指定结点是否是目标结点
	 * 
	 * @param current
	 * @return
	 */
	private boolean isAchieve(AStarNode current, AStarNode target) {
		return current.equals(target);
	}
}