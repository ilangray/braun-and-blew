
class OrSelectionController extends RectSelectionController {

	public OrSelectionController(ArrayList<AbstractView> views) {
		super(views);
	}

	// the subclass should merge the results from each AbstractView
	protected ArrayList<Datum> accumulate(ArrayList<ArrayList<Datum>> datums) {
		return flatten(datums);
	}
}