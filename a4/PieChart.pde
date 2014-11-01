
import java.util.*;

class PieChart extends AbstractView {

	// draws a wedge
	class WedgeView {

		private final String value;
		private final ArrayList<Datum> data;

		private final float startAngle;
		private final float endAngle;

		private final int fillColor;// = color(Math.round(Math.random() * 255), 0, 0);

		// set these to change how the WedgeView appears
		private Point center = new Point(0,0);
		private float radius = 10;

		private WedgeView(String value, ArrayList<Datum> data, float startAngle, float endAngle, int fillColor) {
			this.value = value;
			this.data = data;
			this.startAngle = startAngle;
			this.endAngle = endAngle;
			this.fillColor = fillColor;
		}

		// renders, given the center and radius
		private void render() {
			ellipseMode(RADIUS);
			// stroke(color(0,0,0));
			noStroke();

			// set fill color based on whether any element is selected
			if (containsSelectedDatum()) {
				fill(color(0,0,0));
			} else {
				fill(fillColor);	
			}
			
			float start = Math.min(startAngle, endAngle);
			float end = Math.max(startAngle, endAngle);

			arc(center.x, center.y, radius, radius, start, end, PIE);
		}

		// draws the label
		private void label() {
			float angle = getMiddleAngle();
			float r = radius * 1.15;

			float x = center.x + r * cos(angle);
			float y = center.y + r * sin(angle);

			textSize(15);
			fill(color(0,0,0));
			textAlign(CENTER);
			text(value, x, y);
		}

		private boolean containsPoint(Point p) {
    		float dist = center.distFrom(p);

    		float angle = center.angleBetween(p);
		    if (angle < 0) {
		      angle = TWO_PI + angle;
		    }

		    return dist <= radius && angle > startAngle && angle < endAngle;
  		}

		private float getMiddleAngle() {
    		return (startAngle + endAngle)/2.0f;
  		}

  		// returns true if at least one of the WedgeView's datums are selected
  		private boolean containsSelectedDatum() {
  			for (Datum d : data) {
  				if (d.isSelected()) {
  					return true;
  				}
  			}
  			return false;
  		}
	}

	private final ArrayList<Integer> colors = makeList(color(255,0,0), color(0, 255, 0), color(0,0,255));

	// the property on which the PieChart splits
	private final String property;

  	// the WedgeViews that render the segments of the PieChart
  	private final ArrayList<WedgeView> wedgeViews;

	public PieChart(ArrayList<Datum> data, String property) {
		super(data);

		this.property = property;
		this.wedgeViews = initWedgeViews(groupByValue(data, property));
	}

	public void setBounds(Rect bounds) {
		super.setBounds(bounds);

		// calc new center + radius
		Point center = bounds.getCenter();

		float limitingDimen = Math.min(bounds.w, bounds.h);
		float radius = 0.75 * limitingDimen/2;

		// update radius + center for each WedgeView
		for (WedgeView wv : wedgeViews) {
			wv.radius = radius;
			wv.center = center;
		}
	}

	public ArrayList<Datum> getHoveredDatums() {
		Point mouse = new Point(mouseX, mouseY);

		// find the WedgeView with the mouse, return its data
		for (WedgeView wv : wedgeViews) {
			if (wv.containsPoint(mouse)) {
				return wv.data;
			}
		}

		// if none hit, return empty list
		return new ArrayList<Datum>();
	}

	public void render() {
		// render + label each WedgeView
		for (WedgeView wv : wedgeViews) {
			wv.render();
			wv.label();
		}
	}

	private ArrayList<WedgeView> initWedgeViews(Map<String, ArrayList<Datum>> groups) {
		ArrayList<WedgeView> wedgeViews = new ArrayList<WedgeView>();

		float currentStart = 0;

		ArrayList<String> keys = new ArrayList<String>(groups.keySet());
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			ArrayList<Datum> group = groups.get(key);

			float startAngle = currentStart;
			float percentWidth = (float)group.size() / getData().size();
			float angularWidth = TWO_PI * percentWidth;
			float endAngle = startAngle + angularWidth;

			wedgeViews.add(new WedgeView(key, group, startAngle, endAngle, colors.get(i)));

			currentStart += angularWidth;
		}

		return wedgeViews;
	}

	private Map<String, ArrayList<Datum>> groupByValue(ArrayList<Datum> data, String property) {
		Map<String, ArrayList<Datum>> groups = new HashMap<String, ArrayList<Datum>>();

		for (Datum datum : data) {
			String value = datum.getValue(property);

			// increment the count
			ArrayList<Datum> ds = groups.get(value);
			if (ds == null) {
				ds = new ArrayList<Datum>();
			}
			
			ds.add(datum);

			// associate new count with value
			groups.put(value, ds);
		}

		return groups;
	}
}