
// helpful functions for laying out stuff in a grid
class GridLayout {
	
	private final int cols;
	private final int rows;

	private Rect bounds;

	public GridLayout(int cols, int rows) {
		this.cols = cols;
		this.rows = rows;
	}

	public Rect getCellBounds(int col, int row) {
		float w = getCellWidth();
		float h = getCellHeight();

		float x = col * w;
		float y = row * h;

		return new Rect(bounds.x + x, bounds.y + y, w, h);
	}

	// returns null if x,y are not inside the receivers bounds
	public Point getCellCoords(int x, int y) {
		float localX = x - bounds.x;
		float localY = y - bounds.y;

		// println("y = " + y + ", bounds.y = " + bounds.y + ", localY = " + localY);

		int xCoord = (int)(localX / getCellWidth());
		int yCoord = (int)(localY / getCellHeight());

		Point coord = new Point(xCoord, yCoord);
		return nullIfOutOfBounds(coord);
	}

	public int getCellXCoord(float x) {
		float localX = x - bounds.x;
		int xCoord = (int)(localX / getCellWidth());
		return xCoord;
	}

	public int getCellYCoord(float y) {
		float localY = y - bounds.y;
		int yCoord = (int)(localY / getCellHeight());
		return yCoord;
	}

	private Point nullIfOutOfBounds(Point coord) {
		if (coord.x < 0 || coord.x >= cols) {
			return null;
		}

		if (coord.y < 0 || coord.y >= rows) {
			return null;
		}

		return coord;
	}

	public void setBounds(Rect bounds) {
		this.bounds = bounds;
	}

	public Rect getBounds() {
		return bounds;
	}

	private float getCellWidth() {
		return bounds.w / cols;
	}

	private float getCellHeight() {
		return bounds.h / rows;
	}
}