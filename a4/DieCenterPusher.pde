 class CenterPusher {

 	// private static final float PERCENT_DIST = 0.01;
 	private static final float PERCENT_DIST = 0;

 	private final ArrayList<Node> nodes;
 	private Rect bounds = null;

 	public CenterPusher(ArrayList<Node> nodes) {
 		this.nodes = nodes;
 	}

 	public void push() {
 		// if (dragged != null) {
 		// 	return;
 		// }

 		applyOffset(getOffset(getBounds()));
 	}

 	public void setBounds(Rect r) {
 		this.bounds = r;
 
 	}

 	private Rect getBounds() {
 		if (bounds == null) {
 			println("BOUNDS ARE NULL IN DIE CENTER PUSHER");
 			System.exit(1);
 		}
 		float left = bounds.w;
 		float top = bounds.h;
 		float right = bounds.x;
 		float bottom = bounds.y;

 		for (Node n : nodes) {
 			left = min(left, n.pos.x - n.radius);
 			top = min(top, n.pos.y - n.radius);
 			right = max(right, n.pos.x + n.radius);
 			bottom = max(bottom, n.pos.y + n.radius);
		}

 		return new Rect(left, top, right - left, bottom - top);
	}

	private Point getOffset(Rect r) {
		Point screenCenter = new Point((bounds.x + bounds.w / 2), 
			(bounds.y + bounds.h / 2));
		Point rectCenter = r.getCenter();

		Point diff = rectCenter.diff(screenCenter).scale(PERCENT_DIST, PERCENT_DIST);
		return diff;
	}

	private void applyOffset(Point offset) {
		for (Node n : nodes) {
			n.pos.add(new Vector(offset.x, offset.y));
		}
	}
}