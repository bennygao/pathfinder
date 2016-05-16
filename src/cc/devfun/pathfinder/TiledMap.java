package cc.devfun.pathfinder;

public class TiledMap {
	private final static int PATH = 1;
	private final static int BARRIER = 0;
	
	private int horizontalTilesNum, verticalTilesNum;
	private byte[][] mapData;
	
	public TiledMap() {
		this.horizontalTilesNum = 0;
		this.verticalTilesNum = 0;
		this.mapData = null;
	}
	
	public TiledMap(int hNum, int vNum) {
		this.horizontalTilesNum = hNum;
		this.verticalTilesNum = vNum;
		this.mapData = new byte[vNum][hNum];
		clear();
	}
	
	public void clear() {
		for (int y = 0; y < verticalTilesNum; ++y) {
			for (int x = 0; x < horizontalTilesNum; ++x) {
				mapData[y][x] = PATH;
			}
		}
	}

	public void setData(byte[][] data) {
		mapData = data;
		horizontalTilesNum = data[0].length;
		verticalTilesNum = data.length;
	}
	
	public byte[][] getData() {
		return mapData;
	}

	public int getHorizontalTilesNum() {
		return horizontalTilesNum;
	}

	public int getVerticalTilesNum() {
		return verticalTilesNum;
	}
	
	public byte get(int x, int y) {
		return mapData[y][x];
	}
	
	public void set(int x, int y, Integer v) {
		mapData[y][x] = v.byteValue();
	}
	
	public boolean isBarrier(int x, int y) {
		return get(x, y) != PATH;
	}
	
	public void toggleBarrier(int x, int y) {
		set(x, y, isBarrier(x, y) ? PATH : BARRIER);
	}
	
	public void setToBarrierNode(int x, int y) {
		set(x, y, BARRIER);
	}
}
