
class Heatmap extends AbstractView {

	private final String xProperty;
	private final String yProperty;

	public Heatmap(ArrayList<Datum> data, String xProperty, String yProperty) {
		super(data);

		this.xProperty = xProperty;
		this.yProperty = yProperty; 
	}

	public void render() {

	}

	public ArrayList<Datum> getHoveredDatums() {
		// find which cell (port range + time bucket) is under the mouse

		// return all of those datums
		return new ArrayList<Datum>();
	}

}