
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
		heatmap.setBounds(bounds);
	}

	public void render() {
		heatmap.render();
	}
}