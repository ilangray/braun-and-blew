
class HoverSelectionController extends AccumulatingSelectionController {

	public HoverSelectionController(ArrayList<AbstractView> views) {
		super(views);
	}

	// the subclass should extract the selected datums from the given view
	protected ArrayList<Datum> getSelectedDatums(AbstractView view) {
		return view.getHoveredDatums();
	}

	// the subclass should merge the results from each AbstractView
	protected ArrayList<Datum> accumulate(ArrayList<ArrayList<Datum>> datums) {
		return flatten(datums);
	}
}