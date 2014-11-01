import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.*; 
import java.util.*; 
import java.util.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class a4 extends PApplet {


String FILENAME = "data_aggregate.csv";
Kontroller kontroller;

public void setup() {
  size(1000, 600);	
  
  ArrayList<Datum> data = new DerLeser(FILENAME).readIn();
  kontroller = new Kontroller(data);
}

public void draw() {
  kontroller.render();
}

abstract class AbstractView {

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
	private Rect bounds;

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

// buckets datums by a pair of properties
// buckets are indexed by either (col,row) or a
// pair of values for the properties
class Bucketizer{

	private final ArrayList<Datum> data;

	private final String xProperty;
	private final String yProperty;

	private final ArrayList<String> xValues;
	private final ArrayList<String> yValues;

	private final DatumGrid grid;

	private final int maxCount;

	public Bucketizer(ArrayList<Datum> data, String xProperty, String yProperty){
		this.data = data;

		this.xProperty = xProperty;
		this.yProperty = yProperty;

		this.xValues = getUniqueValues(data, xProperty);
		this.yValues = getUniqueValues(data, yProperty);

		this.grid = initGrid();
		this.maxCount = computeMaxCount();
	}

	private DatumGrid initGrid() {
		DatumGrid grid = new DatumGrid(xValues.size(), yValues.size());

		// add everything into the grid
		for (Datum d : data) {
			addToGrid(grid, d);
		}

		return grid;
	}

	private void addToGrid(DatumGrid grid, Datum d) {
		// figure out where it should be
		String xValue = d.getValue(xProperty);
		String yValue = d.getValue(yProperty);

		int c = xValues.indexOf(xValue);
		int r = yValues.indexOf(yValue);
		
		// grab the ArrayList thats already there
		ArrayList<Datum> ds = grid.get(c, r);

		// construct if need be
		if (ds == null) {
			ds = new ArrayList<Datum>();
		}

		// add teh datum to the list
		ds.add(d);

		// put the (new) list back in the grid
		grid.put(c, r, ds);
	}

	private int computeMaxCount() {
		int maxCount = 0;
		for (int r = 0; r < grid.getHeight(); r++) {
			for (int c = 0; c < grid.getWidth(); c++) {
				maxCount = Math.max(maxCount, getCount(c, r));
			}
		}
		return maxCount;
	}

	public ArrayList<Datum> getDatums(int col, int row) {
		ArrayList<Datum> ds = grid.get(col, row);

		if (ds == null) {
			return new ArrayList();
		} else {
			return ds;
		}
	}

	public int getCount(int col, int row) {
		return getDatums(col, row).size();
	}

	public int getMaxCount() {
		return maxCount;
	}

	public ArrayList<String> getXValues() {
		return xValues;
	}

	public ArrayList<String> getYValues() {
		return yValues;
	}	

	// returns all of the unique values for the property on the data
	private ArrayList<String> getUniqueValues(ArrayList<Datum> data, String property) {
		HashSet<String> vals = new HashSet<String>();

		for (Datum d : data) {
			vals.add(d.getValue(property));
		}

		return new ArrayList<String>(vals);
	}
}


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
public class Datum {

	// the names of datum properties
	public static final String TIME = "time";
	public static final String DEST_IP = "destIP";
	public static final String SOURCE_IP = "sourceIP";
	public static final String DEST_PORT = "destPort";
	public static final String OPERATION = "operation";
	public static final String PRIORITY = "priority";
	public static final String PROTOCOL = "protocol";

	public final int id;
	public final String time;	
	public final String destIP;	
	public final String sourceIP;
	public final String destPort;
	public final String operation;
	public final String priority;
	public final String protocol;

	private boolean selected = false;

	public Datum (int id, String time, String destIP, String sourceIP, 
		String destPort, String operation, String priority, String protocol) {	

		this.id = id;
		this.time = time;	
		this.destIP = destIP;	
		this.sourceIP = sourceIP;
		this.destPort = destPort;
		this.operation = operation;
		this.priority = priority;
		this.protocol = protocol;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean s) {
		selected = s;
	}

	// property should be on of the constants defined above: TIME, DEST_IP, etc
	public String getValue(String property) {
		if (property == null) {
			throw new IllegalArgumentException("Cannot retrieve datum's value for null property");
		}

		if (property.equals(TIME)) {
			return time;
		}
		if (property.equals(DEST_IP)) {
			return destIP;
		}
		if (property.equals(SOURCE_IP)) {
			return sourceIP;
		}
		if (property.equals(DEST_PORT)) {
			return destPort;
		}
		if (property.equals(OPERATION)) {
			return operation;
		}
		if (property.equals(PRIORITY)) {
			return priority;
		}
		if (property.equals(PROTOCOL)) {
			return protocol;
		}

		throw new IllegalArgumentException("Unknown datum property = " + property);
	}

}
// reader
public class DerLeser {
	private final String fileName;

	public DerLeser (String fileName) {
		this.fileName = fileName;
	}

	public ArrayList<Datum> readIn() {
		ArrayList<Datum> toReturn = new ArrayList<Datum>();
		String[] lines = loadStrings(fileName);

		int counter = 0;
		for (String l : lines) {
			if (l.startsWith("Time")) {  // Header
				continue;
			}

			toReturn.add(createDatum(l, counter));

			counter++;
		}

		return toReturn;
	}


	// Takes in a string that is comma-separated Datum and makes Datum
	private Datum createDatum(String l, int counter) {
		String[] listL = split(l, ",");

		return new Datum(counter, listL[0], listL[3], listL[1], listL[4], 
			listL[6], listL[5], listL[7]);
	}

	public void tPrintOne(ArrayList<Datum> d) {
		Datum dat = d.get(100);
		println("id = " + dat.id);
		println("time = " + dat.time);
		println("destIP = " + dat.destIP);
		println("sourceIP = " + dat.sourceIP);
		println("destPort = " + dat.destPort);
		println("operation = " + dat.operation);
		println("priority = " + dat.priority);
		println("protocol = " + dat.protocol);
	}

}
public interface Shape {}

class Wedge implements Shape { 
  public final Point center;
  public final float radius;
  public final float startAngle;
  public final float endAngle;

  public Wedge(Point center, float radius, float startAngle, float endAngle) {
    this.center = center;
    this.radius = radius;
    this.startAngle = startAngle;
    this.endAngle = endAngle;
  }

  public boolean containsPoint(Point p) {
    float dist = center.distFrom(p);

    float angle = center.angleBetween(p);
    if (angle < 0) {
      angle = TWO_PI + angle;
    }

    return dist <= radius && angle > startAngle && angle < endAngle;
  }

  public float getMiddleAngle() {
    return (startAngle + endAngle)/2.0f;
  }

  // returns a list of points that approximate the rounded edge of the wedge
  public ArrayList<Point> lerpRoundedEdge(int count) {
    ArrayList<Point> pts = new ArrayList<Point>();

    for (int i = 0; i <= count; i++) {
      float percent = 1.0f/count * i;
      float angle = lerp(startAngle, endAngle, percent);

      pts.add(new Point(center.x + radius * cos(angle), center.y + radius * sin(angle)));
    }

    return pts;
  }

  public String toString() {
    return "Wedge{center = " + center + ", radius = " + radius + ", startAngle = " + startAngle + ", endAngle = " + endAngle + "}";
  }
}

// This is a point
class Point {
  public float x;
  public float y;
  
  public Point() {
    this.x = 0;
    this.y = 0;
  }
  
  public Point(float x, float y) {
    this.x = x;
    this.y = y;
  }

  public Point scale(float sx, float sy) {
    this.x *= sx;
    this.y *= sy; 
  
    return this;
  }
  
  public void add(Vector v) {
    x += v.x;
    y += v.y; 
  }

  public Point diff(Point other) {
    return new Point(other.x - x, other.y - y);
  }
  
  public Point offset(Point other) {
    return new Point(other.x + x, other.y + y);
  }
  
  public float distFrom(Point other) {
    float dx = (other.x - x);
    float dy = (other.y - y);
    
    return sqrt(dx*dx + dy*dy);
  }

   public float angleBetween(Point other) {
    float dx = dx(other);
    float dy = dy(other);

    return atan2(dy, dx);
  }

  public float dx(Point other) {
    return other.x - x;
  }

  public float dy(Point other) {
    return other.y - y;
  }
  
  public String toString() {
    return "Point{x = " + x + ", y = " + y + "}"; 
  }

  public int hashCode() {
    return (int)Math.pow(x, y);
  }
}

class Vector {
  public float x;
  public float y;
  
  public Vector(float x, float y) {
    this.x = x;
    this.y = y;
  }

  public Vector() {
    this(0, 0);
  }
  
  public Vector(Point p, Point q) {
    this(q.x - p.x, q.y - p.y);
  }

  public void add(Vector v) {
    this.x += v.x;
    this.y += v.y;
  }
  
  public void subtract(Vector v) {
    this.x -= v.x;
    this.y -= v.y; 
  }
   
  public void reset() {
    this.x = 0;
    this.y = 0;
  }
 
  public Vector normalize() {
     float mag = getMagnitude();
     return scale(1.0f/mag, 1.0f/mag);
  }
 
  public float getMagnitude() {
    return mag(x, y);
  }
 
  // switches the direction of the force
  public Vector reverse() {
    return scale(-1, -1);
  }
  
  public Vector scale(float sx, float sy) {
    this.x *= sx;
    this.y *= sy; 
    
    return this;
  }
  
  public Vector copy() {
    return new Vector(x, y);
  }
  
  public String toString() {
    return "Vector{x = " + x + ", y = " + y + "}"; 
  }
}
  
class Rect implements Shape {
  public final float x, y, w, h;
  
  Rect(float x, float y, float w, float h) {
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
  }
  
  Rect(Point ul, Point lr) {
    this(ul.x, ul.y, lr.x - ul.x, lr.y - ul.y);
  }

  public String toString() {
    return "x=" + x + ", y=" + y + ", w=" + w + ", h=" + h; 
  }
  
  public Point getCenter() {
    return new Point(x + w/2, y + h/2);    
  }
  
  public float getMinY() {
    return y;
  }
  
  public float getAspectRatio() {
    float widthOHeight = w / h;
    float heightOWidth = h / w;
    
    return (widthOHeight > heightOWidth ? widthOHeight : heightOWidth);
  }
  
  public float getArea() {
    return w * h;
  }
  
  public Rect scale(float sx, float sy) {
    float newWidth = w * sx;
    float newHeight = h * sy;
    float heightDiff = h - newHeight;
    float widthDiff = w - newWidth;
    
    return new Rect(x + widthDiff/2, y + heightDiff/2, newWidth, newHeight);
  }
  
  public boolean containsPoint(float x, float y) {
    return containsX(x) && containsY(y);
  }
  
  public boolean containsX(float x) {
    return (x >= this.x) && x <= (this.x + w);
  }
    
  public boolean containsY(float y) {
    return (y >= this.y) && y <= (this.y + h);
  }
  
  public Rect inset(int amount) {
   return new Rect(x + amount, y + amount, w - 2 * amount, h - 2 * amount);
  }
}
  
<T> ArrayList<T> makeList(T... values) {
  ArrayList<T> ts = new ArrayList<T>();

  for (T v : values) {
    ts.add(v);
  }

  return ts;
}

<T> ArrayList<T> flatten(ArrayList<T>... lists) {
  ArrayList<T> master = new ArrayList<T>();

  for (ArrayList<T> list : lists) {
    master.addAll(list);
  }

  return master;
}
 
// clamp like a champ --> "clampion"
public float clamp(float x, int min, int max) {
  return min(max(x, min), max); 
}



class DatumGrid {
	private final Map<String, ArrayList<Datum>> data;

	private final int w;
	private final int h;

	public DatumGrid(int w, int h){
		this.w = w;
		this.h = h;
		this.data = new HashMap<String, ArrayList<Datum>>();
	}

	private String getKey(int col, int row) {
		return col + "," + row;
	}

	public void put(int col, int row, ArrayList<Datum> elem) {
		data.put(getKey(col, row), elem);
	}

	public ArrayList<Datum> get(int col, int row){
		return data.get(getKey(col, row));
	}

	public int getWidth() {
		return w;
	}

	public int getHeight() {
		return h;
	}
}

// helpful functions for laying out stuff in a grid
class GridLayout {
	
	private final int cols;
	private final int rows;

	private Rect bounds;

	public GridLayout(int cols, int rows) {
		this.cols = cols;
		this.rows = rows;
	}

	public Rect getCellBounds(int col, int row) {
		float w = getCellWidth();
		float h = getCellHeight();

		float x = col * w;
		float y = row * h;

		return new Rect(bounds.x + x, bounds.y + y, w, h);
	}

	// returns null if x,y are not inside the receivers bounds
	public Point getCellCoords(int x, int y) {
		float localX = x - bounds.x;
		float localY = y - bounds.y;

		// println("y = " + y + ", bounds.y = " + bounds.y + ", localY = " + localY);

		int xCoord = (int)(localX / getCellWidth());
		int yCoord = (int)(localY / getCellHeight());

		Point coord = new Point(xCoord, yCoord);
		return nullIfOutOfBounds(coord);
	}

	private Point nullIfOutOfBounds(Point coord) {
		if (coord.x < 0 || coord.x >= cols) {
			return null;
		}

		if (coord.y < 0 || coord.y >= rows) {
			return null;
		}

		return coord;
	}

	public void setBounds(Rect bounds) {
		this.bounds = bounds;
	}

	public Rect getBounds() {
		return bounds;
	}

	private float getCellWidth() {
		return bounds.w / cols;
	}

	private float getCellHeight() {
		return bounds.h / rows;
	}
}



class Heatmap extends AbstractView {

	private final String xProperty;
	private final String yProperty;

	private final Bucketizer bucketizer; 
	private final GridLayout gridLayout;

	public Heatmap(ArrayList<Datum> data, String xProperty, String yProperty) {
		super(data);
		this.xProperty = xProperty;
		this.yProperty = yProperty; 

		bucketizer = new Bucketizer(data, xProperty, yProperty);
		gridLayout = new GridLayout(bucketizer.getXValues().size(), bucketizer.getYValues().size());
	}

	public void setBounds(Rect bounds) {
		super.setBounds(bounds);
		gridLayout.setBounds(bounds);
	}

	public void render() {
		for (int col = 0; col < bucketizer.getXValues().size(); col++) {
			for (int row = 0; row < bucketizer.getYValues().size(); row++) {

				int count = bucketizer.getCount(col, row);
				Rect bounds = gridLayout.getCellBounds(col, row);
				int fillColor = getColor(col, row, count);

				noStroke();
				fill(fillColor);
				rect(bounds.x, bounds.y, bounds.w, bounds.h);

				fill(color(0,0,0));
				textSize(5);
				textAlign(LEFT, TOP);
				text(count, bounds.x, bounds.y);
			}
		}
	}

	// maps counts to colors
	private int getColor(int col, int row, int count) {
		// should this (col,row) be selected?
		if (isSelected(col, row)) {
			return color(0, 0, 0);
		}

		// return interpolated, non-selected color
		float p = (float)count / bucketizer.getMaxCount();
		return color(255, 0, 0, p * 255);
	}

	private boolean isSelected(int col, int row) {
		ArrayList<Datum> ds = bucketizer.getDatums(col, row);

		for (Datum d : ds) {
			if (d.isSelected()) {
				return true;
			}
		}
		return false;
	}

	public ArrayList<Datum> getHoveredDatums() {
		// find which cell (port range + time bucket) is under the mouse
		Point cellHit = gridLayout.getCellCoords(mouseX, mouseY);

		// return the datums from that cell
		if (cellHit == null) {
			return new ArrayList<Datum>();	
		} else {
			return bucketizer.getDatums((int)cellHit.x, (int)cellHit.y);
		}
	}
}

class Kontroller {
  
  private final ArrayList<Datum> data;

  private final CategoricalView categoricalView;
  private final TemporalView temporalView;
  
  public Kontroller(ArrayList<Datum> data) {
    this.data = data;
    
    this.categoricalView = new CategoricalView(data);
    this.temporalView = new TemporalView(data);
  } 
  
  public void render() {
    deselectAllData();

    updateGraphPositions();
    
    // mouse over
    ArrayList<Datum> hovered = getHoveredDatums();
    selectData(hovered);

    // render:
    background(color(255, 255, 255));
    categoricalView.render();
    temporalView.render();
  }
  
  // repositions the graphs based on the current width/height of the screen
  private void updateGraphPositions() {
    positionView(temporalView, 0, 0.5f, 0.75f, 0.5f);
    positionView(categoricalView, 0.75f, 0, 0.25f, 1.0f);
  }

  private void positionView(AbstractView view, float px, float py, float pw, float ph) {
    float x = width * px;
    float y = height * py;
    float w = width * pw;
    float h = height * ph;
    view.setBounds(new Rect(x, y, w, h));
  }
  
  // returns the datum currently moused-over, or null if none.
  private ArrayList<Datum> getHoveredDatums() {
    // ask each graph what Datum is moused over
    return flatten( 
      categoricalView.getHoveredDatums(),
      temporalView.getHoveredDatums()
    );
  }
  
  private void selectData(ArrayList<Datum> toSelect) {
    for (Datum d : toSelect) {
      d.setSelected(true);
    } 
  }
  
  private void deselectAllData() {
    for (Datum d : data) {
      d.setSelected(false);
    } 
  }
}



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
			float r = radius * 1.15f;

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
		float radius = 0.75f * limitingDimen/2;

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

// TempuraShrimpView
class TemporalView extends AbstractView {

	// the visualization
	private final Heatmap heatmap;

	public TemporalView(ArrayList<Datum> data) {
		super(data);

		heatmap = new Heatmap(data, Datum.TIME, Datum.DEST_PORT);
	}

	public ArrayList<Datum> getHoveredDatums() {
		return heatmap.getHoveredDatums();
	}

	public void setBounds(Rect bounds) {
		super.setBounds(bounds);

		// pass these bounds off to the heatmap, which 
		// occupies all of the TemporalView's space
		heatmap.setBounds(bounds);
	}

	public void render() {
		heatmap.render();
	}
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "a4" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
