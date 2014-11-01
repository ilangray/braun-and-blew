
import java.util.*;

class Heatmap extends AbstractView {

	private final String xProperty;
	private final String yProperty;

	private final Bucketizer bucketizer; 
	private final GridLayout gridLayout;

	public Heatmap(ArrayList<Datum> data, String xProperty, String yProperty) {
		super(data);
		this.xProperty = xProperty;
		this.yProperty = yProperty; 

		bucketizer = new Bucketizer(data, xProperty, yProperty);
		gridLayout = new GridLayout(bucketizer.getXValues().size(), bucketizer.getYValues().size());
	}

	public void setBounds(Rect bounds) {
		super.setBounds(bounds);

		int paddingLeft = 60;
		int paddingBottom = 60;

		gridLayout.setBounds(bounds.inset(paddingLeft, 0, 0, paddingBottom));
	}

	public void render() {
		labelCells();
		renderCells();
	}

	private void labelCells() {

	}

	private void renderCells() {
		for (int col = 0; col < bucketizer.getXValues().size(); col++) {
			for (int row = 0; row < bucketizer.getYValues().size(); row++) {

				int count = bucketizer.getCount(col, row);
				Rect bounds = gridLayout.getCellBounds(col, row);
				int fillColor = getColor(col, row, count);

				noStroke();
				fill(fillColor);
				rect(bounds.x, bounds.y, bounds.w, bounds.h);

				// fill(color(0,0,0));
				// textSize(5);
				// textAlign(LEFT, TOP);
				// text(count, bounds.x, bounds.y);
			}
		}
	}

	// maps counts to colors
	private int getColor(int col, int row, int count) {
		// should this (col,row) be selected?
		if (isSelected(col, row)) {
			return color(0, 0, 0);
		}

		// return interpolated, non-selected color
		float p = (float)count / bucketizer.getMaxCount();
		return color(255, 0, 0, p * 255);
	}

	private boolean isSelected(int col, int row) {
		ArrayList<Datum> ds = bucketizer.getDatums(col, row);

		for (Datum d : ds) {
			if (d.isSelected()) {
				return true;
			}
		}
		return false;
	}

	public ArrayList<Datum> getHoveredDatums() {
		// find which cell (port range + time bucket) is under the mouse
		Point cellHit = gridLayout.getCellCoords(mouseX, mouseY);

		// return the datums from that cell
		if (cellHit == null) {
			return new ArrayList<Datum>();	
		} else {
			return bucketizer.getDatums((int)cellHit.x, (int)cellHit.y);
		}
	}
}