
abstract class RectSelectionController extends AccumulatingSelectionController {

	// previous rectangles currently being tracked
	private final ArrayList<Rect> rects = new ArrayList<Rect>();

	// the rect currently being expanded, or null if none exists.
	private Rect currentSelection;
	private Point touchdownLocation;

	public RectSelectionController(ArrayList<AbstractView> views) {
		super(views);
	}

	// render all of the rectangles
	public void render() {
		// int count = rects.size() + (currentSelection == null ? 0 : 1);
		// println("rendering " + count + " rects");

		for (Rect r : rects) {
			renderRect(r);
		}

		if (currentSelection != null) {
			renderRect(currentSelection);	
		}
	}

	private final int RECT_FILL = color(0,0,0,85);
	private final int RECT_STROKE = color(0,0,0,200);

	private void renderRect(Rect r) {
		fill(RECT_FILL);
		stroke(RECT_STROKE);
		rect(r.x, r.y, r.w, r.h);
	}

	private void commitCurrentRect() {
		rects.add(currentSelection);
		currentSelection = null;
	}

	private void clearRectangles() {
		rects.clear();
		currentSelection = null;
	}

	public void mousePressed() { 
		println("mouse pressed!");
		currentSelection = new Rect(mouseX, mouseY, 0, 0);

		touchdownLocation = new Point(mouseX, mouseY);
	}
	
	// fails if you go upper left of starting point?
	public void mouseDragged() { 
		float origX = touchdownLocation.x;
		float origY = touchdownLocation.y;

		float w = Math.abs(mouseX - origX);
		float h = Math.abs(mouseY - origY);

		float x = Math.min(origX, mouseX);
		float y = Math.min(origY, mouseY);

		currentSelection.x = x;
		currentSelection.y = y;
		currentSelection.w = w;
		currentSelection.h = h;
	}

	public void mouseReleased() { 
		commitCurrentRect();
	}

	public void mouseClicked() { 
		if (mouseButton == RIGHT) {
			clearRectangles();
		}
	}

	private ArrayList<Rect> getAllRects() {
		ArrayList<Rect> rs = new ArrayList<Rect>(rects);

		if (currentSelection != null) {
			rs.add(currentSelection);
		}

		return rs;
	}

	// the subclass should extract the selected datums from the given view
	protected ArrayList<Datum> getSelectedDatums(AbstractView view) {
		ArrayList<ArrayList<Datum>> ds = new ArrayList<ArrayList<Datum>>();

		for (Rect r : getAllRects()) {
			ds.add(view.getSelectedDatums(r));
		}

		return flatten(ds);
	}
}