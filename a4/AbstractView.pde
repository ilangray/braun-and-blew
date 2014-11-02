
abstract class AbstractView {

	public final int SELECTED_COLOR = color(255, 255, 0);
	public final int OUTLINE_COLOR = color(40,40,40);

	// an AbstractView has a notion of the data that it is displaying
	private final ArrayList<Datum> data;

	public AbstractView(ArrayList<Datum> data) {
		this.data = data;
	}

	// returns this AbstractView's list of Datums
	public ArrayList<Datum> getData() {
		return data;
	}

	// the bounds of this AbstractView, in pixels, not percentages
	// this is where the AbstractView should draw itself in render()
	protected Rect bounds;

	public final Rect getBounds() {
		return bounds;
	}

	// sets the bounds of the receiver
	public void setBounds(Rect bounds) {
		this.bounds = bounds;
	}

	// tells the view to render its contents in its bounds
	public abstract void render();

	// return the Datum(s) that is currently under the 
	// mouse, or an empty ArrayList if no such datum exists
	public abstract ArrayList<Datum> getHoveredDatums();

}