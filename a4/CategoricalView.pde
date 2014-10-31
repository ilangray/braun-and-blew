

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

		float unitHeight = bounds.h / pieCharts.size();

		// reposition each of the pie charts
		for (int i = 0; i < pieCharts.size(); i++) {
			float top = bounds.y + unitHeight * i;

			Rect pieBounds = new Rect(bounds.x, top, bounds.w, unitHeight);
			pieCharts.get(i).setBounds(pieBounds);
		}
	}

	public void render() {
		for (PieChart pc : pieCharts) {
			pc.render();
		}
	}
}