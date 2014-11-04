
interface MouseHandler {
	void mousePressed();
	void mouseDragged();
	void mouseReleased();
	void mouseClicked();
}

interface SelectionController extends MouseHandler {

	// the controller should return the datums that are selected
	ArrayList<Datum> getSelectedDatums();

	// allows the controller to render stuff relating to selections
	void render();
}

//
abstract class AccumulatingSelectionController implements SelectionController {

	private ArrayList<AbstractView> views;

	public AccumulatingSelectionController(ArrayList<AbstractView> views) {
		this.views = views;
	}

	public ArrayList<Datum> getSelectedDatums() {
		ArrayList<ArrayList<Datum>> datums = new ArrayList<ArrayList<Datum>>();

		for (AbstractView view : views) {
			datums.add(getSelectedDatums(view));
		}

		return accumulate(datums);
	}

	// the subclass should extract the selected datums from the given view
	abstract protected ArrayList<Datum> getSelectedDatums(AbstractView view);

	// the subclass should merge the results from each AbstractView
	abstract protected ArrayList<Datum> accumulate(ArrayList<ArrayList<Datum>> datums);

	// default impl does nothing
	public void render() {}

	// default impls of the mousehandler functions,
	// so you can override only what the subclass needs
	public void mousePressed() { }
	public void mouseDragged() { }
	public void mouseReleased() { }
	public void mouseClicked() { }
}