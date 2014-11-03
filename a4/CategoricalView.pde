

class CategoricalView extends AbstractView {

	// the visualization
	private final ArrayList<PieChart> pieCharts;

	public CategoricalView(ArrayList<Datum> data) {
		super(data);

		// construct the three pie charts
		PieChart operation = new PieChart(data, Datum.OPERATION); 
		PieChart priority = new PieChart(data, Datum.PRIORITY);
		PieChart protocol = new PieChart(data, Datum.PROTOCOL);

		pieCharts = makeList(operation, priority, protocol);
	}

	// unions the hovered elements from all pie charts
	public ArrayList<Datum> getHoveredDatums() {
		ArrayList<Datum> hovered = new ArrayList<Datum>();

		for (PieChart pc : pieCharts) {
			hovered.addAll(pc.getHoveredDatums());
		}

		return hovered;
	}

	public void setBounds(Rect bounds) {
		super.setBounds(bounds);

		Rect pieBounds = bounds.inset(0, 40, 0, 0);
		layoutPieCharts(pieBounds);
	}

	private void layoutPieCharts(Rect bounds) {
		ArrayList<Rect> rects = getPieChartBounds(bounds);

		for (int i = 0; i < rects.size(); ++i) {
			Rect r = rects.get(i);
			PieChart pc = pieCharts.get(i);

			pc.setBounds(r.inset(0, 20, 0, 0));
		}
	}

	private ArrayList<Rect> getPieChartBounds(Rect bounds) {
		ArrayList<Rect> rects = new ArrayList<Rect>();
		float unitHeight = bounds.h / pieCharts.size();

		// reposition each of the pie charts
		for (int i = 0; i < pieCharts.size(); i++) {
			float top = bounds.y + unitHeight * i;
			rects.add(new Rect(bounds.x, top, bounds.w, unitHeight));
		}

		return rects;
	}

	public void render() {
		renderTitle();
		renderPieCharts();
		renderPieChartTitles();
		renderSeparators();
	}

	private void renderPieChartTitles() {
		fill(color(0,0,0,128));
		textSize(15);
		textAlign(CENTER, BOTTOM);

		for (PieChart pc : pieCharts) {
			Rect bounds = pc.getBounds();

			float x = pc.getBounds().getCenter().x;
			float y = pc.getBounds().y;
			text("by " + pc.getProperty() + ":", x, y);
		}
	}

	private void renderPieCharts() {
		for (PieChart pc : pieCharts) {
			pc.render();
		}
	}

	private void renderTitle() {
		textAlign(CENTER, BOTTOM);

		float x = getBounds().x;
		float y = getBounds().y;

		Point center = getBounds().getCenter();

		fill(color(0,0,0));
		textSize(25);
		text("Categorical View", center.x, y + 35);
	}

	private void renderSeparators() {
		strokeWeight(1);

		for (int i = 1; i < pieCharts.size(); i++) {
			PieChart pc = pieCharts.get(i);
			Rect bounds = pc.getBounds();

			float y = bounds.y - 25;

			stroke(color(0,0,0,128));
			line(bounds.x, y, bounds.x + bounds.w, y);
		}
	}
}