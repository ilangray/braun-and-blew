
// TempuraShrimpView
class TemporalView extends AbstractView {

	// the visualization
	private final Heatmap heatmap;

	public TemporalView(ArrayList<Datum> data) {
		super(data);

		heatmap = new Heatmap(data, Datum.TIME, Datum.DEST_PORT);
	}

	public ArrayList<Datum> getHoveredDatums() {
		return heatmap.getHoveredDatums();
	}

	public void setBounds(Rect bounds) {
		super.setBounds(bounds);

		// pass these bounds off to the heatmap, which 
		// occupies all of the TemporalView's space
		heatmap.setBounds(bounds.inset(0, 40, 0, 0));
	}

	public void render() {
		renderTitle();
		heatmap.render();
	}

	private void renderTitle() {
		textAlign(LEFT, BOTTOM);

		float x = getBounds().x;
		float y = getBounds().y;

		fill(color(0,0,0));
		textSize(25);
		text("Temporal View", x + 10, y + 35);

		fill(color(0,0,0,128));
		textSize(15);
		text("a heatmap of port range activity by time", x + 200, y + 30);
	}
}