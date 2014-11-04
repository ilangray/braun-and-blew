
import java.util.*;

class AndSelectionController extends RectSelectionController {

	public AndSelectionController(ArrayList<AbstractView> views) {
		super(views);
	}

	// the subclass should merge the results from each AbstractView
	protected ArrayList<Datum> accumulate(ArrayList<ArrayList<Datum>> datums) {

		Set<Integer> ids = new HashSet<Integer>();

		// start with the first set of datums' ids
		ids.addAll(getIds(datums.get(0)));

		for (ArrayList<Datum> ds : datums) {
			ids.retainAll(getIds(ds));
		}

		return getDatumsWithIds(flatten(datums), ids);
	}

	private ArrayList<Integer> getIds(ArrayList<Datum> ds) {
		ArrayList<Integer> ids = new ArrayList<Integer>();

		for (Datum d : ds) {
			ids.add(d.id);
		}

		return ids;
	}

	private ArrayList<Datum> getDatumsWithIds(ArrayList<Datum> ds, Set<Integer> ids) {
		ArrayList<Datum> out = new ArrayList<Datum>();

		for (Datum d : ds) {
			if (ids.contains(d.id)) {
				out.add(d);
			}
		}

		return out;
	}
}