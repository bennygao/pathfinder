

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;

import cc.devfun.pathfinder.AStarNode;
import cc.devfun.pathfinder.AStarPathFinder;
import cc.devfun.pathfinder.TiledMap;

public class AstarPanel extends JPanel implements MouseListener,
		MouseMotionListener, KeyListener {

	private static final long serialVersionUID = 7286622284205423626L;

	private final static long MAX_FPS = 24;
	private final static long SPF = 1000 / MAX_FPS;
//	private final static int GRID_SIZE = 5;

	private final int gridWidth;
	private final int gridHeight;
	private final int xGridNum;
	private final int yGridNum;

	private final int totalWidth;
	private final int totalHeight;

	private AStarNode target;
	private AStarNode source;

	/** 线条颜色 */
	private final Color GRID_LINE_COLOR = new Color(64, 64, 64);
	/** 障碍物颜色 */
	private static final Color BARRIER_COLOR = new Color(128, 128, 128);

	/** 目标颜色 */
	private final Color TARGET_COLOR = new Color(255, 0, 0);
	/** 源颜色 */
	private final Color SOURCE_COLOR = new Color(0, 0, 255);

	private AStarPathFinder pathFinder;

	private List<AStarNode> path;
	private int currentDrawIndex = 0;

	private static final Color DRAW_DATA_COLOR = new Color(255, 128, 64);
	private static final Composite DRAW_DATA_COMPOSITE = AlphaComposite
			.getInstance(AlphaComposite.SRC_OVER, 0.7f);
	private static final Composite NORMAL_COMPOSITE = AlphaComposite
			.getInstance(1);

	private Iterator<AStarNode> openList;
	private boolean isDrawOpenList;
	private static final Color OPEN_LIST_NODE_COLOR = new Color(64, 255, 64);

	private int[][] drawData;
	private TiledMap map = null;

	private boolean isShiftDown;
	private int lastX, lastY;
	private StatusPanel statusPanel;

	private static final Composite TIPS_COMPOSITE = AlphaComposite.getInstance(
			AlphaComposite.SRC_OVER, 0.4f);
	private static final Composite TIPS_COMPOSITE2 = AlphaComposite
			.getInstance(AlphaComposite.SRC_OVER, 0.7f);
	private static final Color TIPS_BG = new Color(0, 0, 0);
	private static final Color TIPS_FG = new Color(255, 255, 0);
	private Point tipsPoint = new Point();
	private boolean isDrawTips = false;
	private int gridSize;

	public AstarPanel(int gridSize, int xGridNum, int yGridNum) {
		this.gridSize = gridSize;
		this.gridWidth = gridSize;
		this.gridHeight = gridSize;
		this.xGridNum = xGridNum;
		this.yGridNum = yGridNum;

		target = AStarNode.newNode(0, 0);
		source = AStarNode.newNode(xGridNum - 1, yGridNum - 1);
		drawData = new int[yGridNum][xGridNum];

		pathFinder = new AStarPathFinder();
		map = new TiledMap(xGridNum, yGridNum);

		totalWidth = gridWidth * xGridNum;
		totalHeight = gridHeight * yGridNum;

		setPreferredSize(new Dimension(totalWidth + 1, totalHeight + 1));

		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);

		startAnimatorThread();
	}

	private void startAnimatorThread() {
		new Thread() {
			@Override
			public void run() {
				try {
					while (true) {
						Thread.sleep(SPF);
						repaint();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		fillBarrier(g);
		fillTargetAndSourceGrid(g);
		fillOpenList(g);
		fillAStarPath(g);
		fillDrawData(g);
		drawGridLine(g);
		drawTips(g);
	}

	public TiledMap getMap() {
		return map;
	}

	private void drawTips(Graphics g) {
		if (isDrawTips) {
			Graphics2D g2d = (Graphics2D) g.create();
			int width = 300;
			int height = 20;
			int arc = 5;
			int x = (tipsPoint.getLocation().x + 1) * gridWidth;
			int y = (tipsPoint.getLocation().y + 1) * gridHeight;
			if (x + width >= getPreferredSize().width) {
				x = getPreferredSize().width - width - gridSize;
			}
			if (y + height >= getPreferredSize().height) {
				y = getPreferredSize().height - height - gridSize;
			}
			g2d.setColor(TIPS_BG);
			g2d.setComposite(TIPS_COMPOSITE);
			g2d.fillRoundRect(x, y, width, height, arc, arc);
			g2d.setComposite(TIPS_COMPOSITE2);
			g2d.setColor(TIPS_FG);
			g2d.drawString("Current grid num : (" + tipsPoint.getLocation().x
					+ ", " + tipsPoint.getLocation().y + ")", x + gridSize, y
					+ gridSize);
			g2d.dispose();
		}
	}

	private void fillOpenList(Graphics g) {
		if (isDrawOpenList && pathFinder != null) {
			openList = pathFinder.getOpenList().iterator();
			if (openList != null) {
				while (openList.hasNext()) {
					fillNode(g, OPEN_LIST_NODE_COLOR, openList.next());
				}
			}
		}
	}

	private void fillDrawData(Graphics g) {
		if (drawData != null) {
			Graphics2D g2d = (Graphics2D) g;
			g2d.setComposite(DRAW_DATA_COMPOSITE);
			for (int i = 0; i < drawData.length; i++) {
				for (int j = 0; j < drawData[i].length; j++) {
					if (drawData[i][j] == 1) {
						fillNode(g2d, DRAW_DATA_COLOR, j * gridWidth, i
								* gridHeight);
					}
				}
			}
			g2d.setComposite(NORMAL_COMPOSITE);
		}
	}

	/**
	 * 绘制AStar路径
	 * 
	 * @param graphics
	 */
	private void fillAStarPath(Graphics graphics) {
		int size = path == null ? 0 : path.size();
		if (size > 1) {
			int r, g = 0, b;
			Iterator<AStarNode> itr = path.iterator();
			int i = 0;
			while (i < currentDrawIndex && itr.hasNext()) {
				r = 255 * i / (size - 1);
				g = 0;
				b = 255 * (size - i) / size;
				fillNode(graphics, new Color(r, g, b), itr.next());
				
				++i;
			}
			
			if (currentDrawIndex < path.size()) {
				currentDrawIndex++;
			}
		}
	}

	/**
	 * 填充障碍物
	 * 
	 * @param g
	 */
	private void fillBarrier(Graphics g) {
		for (int i = 0; i < map.getVerticalTilesNum(); i++) {
			for (int j = 0; j < map.getHorizontalTilesNum(); j++) {
				if (map.isBarrier(j, i)) {
					fillNode(g, BARRIER_COLOR, j * gridWidth, i * gridHeight);
				}
			}
		}
	}

	private void fillNode(Graphics g, Color barrierColor, int startXj,
			int startY) {
		g.setColor(barrierColor);
		g.fillRect(startXj, startY, gridWidth, gridHeight);
	}

	/**
	 * 填充目标和源格子
	 * 
	 * @param g
	 */
	private void fillTargetAndSourceGrid(Graphics g) {
		if (pathFinder != null) {
			fillNode(g, TARGET_COLOR, target);
			fillNode(g, SOURCE_COLOR, source);
		}
	}

	/**
	 * 填充AStar结点
	 * 
	 * @param g
	 * @param color
	 * @param target
	 */
	private void fillNode(Graphics g, Color color, AStarNode target) {
		if (target != null) {
			g.setColor(color);
			int x = target.getX() * gridWidth;
			int y = target.getY() * gridHeight;
			g.fillRect(x, y, gridWidth, gridHeight);
		}
	}

	/**
	 * 绘制格子线条
	 * 
	 * @param g
	 */
	private void drawGridLine(Graphics g) {
		g.setColor(GRID_LINE_COLOR);
		for (int i = 0; i <= yGridNum; i++) {
			g.drawLine(0, i * gridHeight, totalWidth, i * gridHeight);
		}
		for (int i = 0; i <= xGridNum; i++) {
			g.drawLine(i * gridWidth, 0, i * gridWidth, totalHeight);
		}
	}

	public void startFind() {
		if (pathFinder != null) {
			if (pathFinder != null) {
				long start = System.currentTimeMillis();
				pathFinder.setMap(map.getData());
				path = pathFinder.findPath(source.toShortArray(), target.toShortArray());
				updateCostTimeMillis(System.currentTimeMillis() - start);
			}
		}
	}

	public void clearPath() {
		path = null;
		currentDrawIndex = 0;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			int x = e.getX() / gridWidth;
			int y = e.getY() / gridHeight;
			if (isShiftDown) {
				for (int j = Math.min(y, lastY); j <= Math.max(y, lastY); j++) {
					for (int i = Math.min(x, lastX); i <= Math.max(x, lastX); i++) {
						if (j >= 0 && j < drawData.length && i >= 0
								&& i < drawData[0].length) {
							drawData[j][i] = 1;
							map.setToBarrierNode(i, j);
						}
					}
				}
			} else if (e.getClickCount() == 2) { // 鼠标左键双击
				toggleBarrier(e);
			} else {
				mouseDragged(e);
			}

			lastX = x;
			lastY = y;
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3) {
			source.setX(target.getX());
			source.setY(target.getY());

			target.setX(e.getX() / gridWidth);
			target.setY(e.getY() / gridHeight);

			clearPath();
			startFind();
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		isDrawTips = true;
	}

	@Override
	public void mouseExited(MouseEvent e) {
		isDrawTips = false;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		int x = e.getX() / gridWidth;
		int y = e.getY() / gridHeight;
		if (y >= 0 && y < drawData.length && x >= 0 && x < drawData[0].length) {
			map.setToBarrierNode(x, y);
		}
	}

	private void toggleBarrier(MouseEvent e) {
		int x = e.getX() / gridWidth;
		int y = e.getY() / gridHeight;
		if (y >= 0 && y < drawData.length && x >= 0 && x < drawData[0].length) {
			if (drawData[y][x] == 2) {
				drawData[y][x] = -1;
			} else {
				drawData[y][x] = 2;
			}

			map.toggleBarrier(x, y);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		tipsPoint.setLocation(e.getX() / gridWidth, e.getY() / gridHeight);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void keyPressed(KeyEvent e) {
		this.isShiftDown = e.isShiftDown();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		this.isShiftDown = e.isShiftDown();
	}

	private void updateCostTimeMillis(long time) {
		if (statusPanel != null)
			statusPanel.setCostTimeMillis(time);
	}

	public void setStatusPanel(StatusPanel statusPanel) {
		this.statusPanel = statusPanel;
	}

	public void clearMap() {
		for (int i = 0; i < drawData.length; i++) {
			for (int j = 0; j < drawData[i].length; j++) {
				drawData[i][j] = -1;
			}
		}

		map.clear();
	}

	public void drawOpenList(boolean isDrawOpenList) {
		this.isDrawOpenList = isDrawOpenList;
	}
}