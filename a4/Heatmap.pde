
import java.util.*;

class Heatmap extends AbstractView {

	private static final int PADDING_LEFT = 90;
	private static final int PADDING_BOTTOM = 60;
	private static final int FONT_SIZE = 12;

	private final String xProperty;
	private final String yProperty;

	private final Bucketizer bucketizer; 
	private final GridLayout gridLayout;

	// layouts that chop up the space available for axis labeling
	private final GridLayout xLabelLayout;
	private final GridLayout yLabelLayout;

	public Heatmap(ArrayList<Datum> data, String xProperty, String yProperty) {
		super(data);
		this.xProperty = xProperty;
		this.yProperty = yProperty; 

		bucketizer = new Bucketizer(data, xProperty, yProperty);

		int cols = bucketizer.getXValues().size();
		int rows = bucketizer.getYValues().size();

		gridLayout = new GridLayout(cols, rows);
		xLabelLayout = new GridLayout(cols, 1);
		yLabelLayout = new GridLayout(1, rows); 
	}

	public ArrayList<Datum> getSelectedDatums(Rect r) {
		return null;
	}

	public void setBounds(Rect bounds) {
		super.setBounds(bounds);

		// the grid gets inside by the padding left & bottom
		Rect gridBounds = bounds.inset(PADDING_LEFT, 0, 0, PADDING_BOTTOM);
		gridLayout.setBounds(gridBounds);

		// position the axis layouts
		yLabelLayout.setBounds(new Rect(bounds.x, bounds.y, PADDING_LEFT, bounds.h - PADDING_BOTTOM));
		xLabelLayout.setBounds(new Rect(
			bounds.x + PADDING_LEFT, bounds.y + bounds.h - PADDING_BOTTOM, bounds.w - PADDING_LEFT, PADDING_BOTTOM));
	}

	public void render() {
		renderGrid();
		labelCells();
		renderCells();
	}

	private void labelCells() {
		labelX();
		labelY();
	}

	private void renderGrid() {
		ArrayList<String> xLabels = bucketizer.getXValues();
		ArrayList<String> yLabels = bucketizer.getYValues();

		// gotta set the weight 
		strokeWeight(1);

		// add vertical lines
		for (int col = 0; col < xLabels.size(); col++) {
			// grab the top + bottom
			Rect top = gridLayout.getCellBounds(col, 0);
			Rect bottom = gridLayout.getCellBounds(col, yLabels.size()-1);

			// draw left edge
			stroke(color(208,208,208));
			fill(color(208, 208, 208));
			line(top.x, top.y, bottom.x, bottom.y + bottom.h + PADDING_BOTTOM - 10);

			// draw right edge on last col
			if (col == xLabels.size() - 1) {
				line(top.x + top.w, top.y, bottom.x + bottom.w, bottom.y + bottom.h + PADDING_BOTTOM - 10);
			}
		}

		// add horizontal lines
		for (int row = 0; row < yLabels.size(); row++) {
			// grab the top + bottom
			Rect left = gridLayout.getCellBounds(0, row);
			Rect right = gridLayout.getCellBounds(xLabels.size()-1, row);

			// draw left edge
			stroke(color(208,208,208));
			fill(color(208, 208, 208));
			line(left.x - PADDING_LEFT + 10, left.y, right.x + right.w, right.y);

			// draw bottom line on last row
			if (row == yLabels.size() - 1) {
				line(left.x - PADDING_LEFT + 10, left.y + left.h, right.x + right.w, right.y + right.h);
			}
		}
	}

	private void labelX() {
		ArrayList<String> labels = bucketizer.getXValues();
		for (int col = 0; col < labels.size(); col++) {
			Point center = xLabelLayout.getCellBounds(col, 0).getCenter();
			renderLabel(labels.get(col), center, true);
		}
	}

	private void labelY() {
		ArrayList<String> labels = bucketizer.getYValues();
		for (int row = 0; row < labels.size(); row++) {
			Point center = yLabelLayout.getCellBounds(0, row).getCenter();
			renderLabel(labels.get(row), center, false);
		}
	}

	private void renderLabel(String letters, Point center, boolean vertical) {
		textSize(FONT_SIZE);
		textAlign(CENTER, CENTER);
		fill(color(0,0,0));
		
		pushMatrix();
	  	translate(center.x, center.y);

	  	if (vertical) {
	  		rotate(HALF_PI);	
	  	}
		
		text(letters, 0,0);

		popMatrix();
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

				// if hit, render label
				if (bounds.containsPoint(mouseX, mouseY)) {
					renderLabel(bounds.getCenter(), "" + count);
				}
			}
		}
	}

	private void renderLabel(Point p, String s) {  
		textSize(14);
		textAlign(CENTER, CENTER);
		fill(color(0,0,0));
		text(s, p.x, p.y);
	}

	// maps counts to colors
	private int getColor(int col, int row, int count) {
		// should this (col,row) be selected?
		if (isSelected(col, row)) {
			return SELECTED_COLOR;
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