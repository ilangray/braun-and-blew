import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.*; 
import java.util.*; 
import java.util.Map; 
import java.lang.*; 
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
NetworkView nv;
boolean done = false;

public void setup() {
	size(1000, 600);	
	frame.setResizable(true);

	ArrayList<Datum> data = new DerLeser(FILENAME).readIn();
	kontroller = new Kontroller(data);
}

// converts ms to seconds
public float seconds(int ms) {
  return ms / 1000.0f; 
}

public void draw() {
  	kontroller.render();
}

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
	public Rect bounds;

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

		// sort x and y values
		Collections.sort(yValues, new Comparator<String>() {
			public int compare(String s1, String s2) {
				return compareRange(s1, s2);
			}
		});
		Collections.sort(xValues, new Comparator<String>() {
			public int compare(String s1, String s2) {
				return compareTimes(s1, s2);
			}
		});

		this.grid = initGrid();
		this.maxCount = computeMaxCount();
	}

	private int compareTimes(String t1, String t2) {
		ArrayList<Integer> times1 = splitTimes(t1);
		ArrayList<Integer> times2 = splitTimes(t2);

		for (int i = 0; i < times1.size(); i++) {
			if (times1.get(i) > times2.get(i)) {
				return 1;
			}
			if (times1.get(i) < times2.get(i)) {
				return -1;
			}
		}
		return 0;
	}

	private ArrayList<Integer> splitTimes(String time) {
		String[] parts = trim(split(time, ":"));

		ArrayList<Integer> ints = new ArrayList<Integer>();

		for (String p : parts) {
			ints.add(Integer.parseInt(p));
		}

		return ints;
	}

	private int compareRange(String r1, String r2) {
		int s1First = Integer.parseInt(trim(split(r1, "-")[0]));
		int s2First = Integer.parseInt(trim(split(r2, "-")[0]));

		return s1First < s2First ? 1 : -1;
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

/**
 * A Damper applies a force proportional to a 
 * node's velocity, in the opposite direction.
 */
class Damper implements ForceSource {

  private static final float K = 2f;
  // private static final float K = 0;


  private final Node node;

  public Damper(Node node) {
    this.node = node;
  }
    
  public void applyForce() {
    Vector velocity = node.vel.copy().scale(-K, -K);
    node.addForce(velocity);
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
class ForceDirectedGraph extends AbstractView {
	private RenderMachine rm;
	private Simulator sm;
	private CenterPusher cp;
	private ArrayList<Node> nodes;

	public ForceDirectedGraph(ArrayList<Node> nodes, ArrayList<Spring> springs,
		ArrayList<Zap> zaps, ArrayList<Damper> dampers, ArrayList<Datum> data) {
		super(data);

		rm = new RenderMachine(nodes, springs);
		sm = new Simulator(nodes, springs, zaps, dampers);
		cp = new CenterPusher(nodes);		
		this.nodes = nodes;
	}

	public void render() {
		 // if (!done || previous_w != width || previous_h != height) {
    	done = !sm.step(seconds(16));
  	// }

		// background(color(255, 255, 255));
		cp.push();
		rm.render();
	}

	public Simulator getSimulator() {
		return sm;
	}

	public CenterPusher getCenterPusher() {
		return cp;
	}

	public RenderMachine getRenderMachine() {
		return rm;
	}

	public ArrayList<Node> getNodes() {
		return nodes;
	}

	public void setBounds(Rect bounds) {
		super.setBounds(bounds);
		cp.setBounds(bounds);
	}


	// TODO: Actually write this
	public ArrayList<Datum> getHoveredDatums() {
		return null;
	}

}

// A ForceSource is something that can apply forces
interface ForceSource {
  // calculate the first applied by this source on its endpoints,
  // and update the nodes to reflect this force
  public void applyForce();
}

/**
 * An InterNodeForce is a force that acts between two nodes.
 */
abstract class InterNodeForce implements ForceSource {
  public Node endA;
  public Node endB;
  
  public InterNodeForce(Node endA, Node endB) {
    this.endA = endA;
    this.endB = endB;
  }
  
  protected float getDistance() {
    return endB.pos.distFrom(endA.pos);
//    return dist(endA.pos.x, endA.pos.y, endB.pos.x, endB.pos.y); 
  }
  
  // calculate the first applied by this source on its endpoints,
  // and update the nodes to reflect this force
  public abstract void applyForce();
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

  public Rect inset(int left, int top, int right, int bottom) {
    float newWidth = w - left - right;
    float newHeight = h - top - bottom;

    return new Rect(x + left, y + top, newWidth, newHeight);
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

	private static final int PADDING_LEFT = 90;
	private static final int PADDING_BOTTOM = 60;
	private static final int FONT_SIZE = 12;

	private final String xProperty;
	private final String yProperty;

	private final Bucketizer bucketizer; 
	private final GridLayout gridLayout;

	// layouts that chop up the space available for axis labeling
	private final GridLayout xLabelLayout;
	private final GridLayout yLabelLayout;

	public Heatmap(ArrayList<Datum> data, String xProperty, String yProperty) {
		super(data);
		this.xProperty = xProperty;
		this.yProperty = yProperty; 

		bucketizer = new Bucketizer(data, xProperty, yProperty);

		int cols = bucketizer.getXValues().size();
		int rows = bucketizer.getYValues().size();

		gridLayout = new GridLayout(cols, rows);
		xLabelLayout = new GridLayout(cols, 1);
		yLabelLayout = new GridLayout(1, rows); 
	}

	public void setBounds(Rect bounds) {
		super.setBounds(bounds);

		// the grid gets inside by the padding left & bottom
		Rect gridBounds = bounds.inset(PADDING_LEFT, 0, 0, PADDING_BOTTOM);
		gridLayout.setBounds(gridBounds);

		// position the axis layouts
		yLabelLayout.setBounds(new Rect(bounds.x, bounds.y, PADDING_LEFT, bounds.h - PADDING_BOTTOM));
		xLabelLayout.setBounds(new Rect(
			bounds.x + PADDING_LEFT, bounds.y + bounds.h - PADDING_BOTTOM, bounds.w - PADDING_LEFT, PADDING_BOTTOM));
	}

	public void render() {
		renderGrid();
		labelCells();
		renderCells();
	}

	private void labelCells() {
		labelX();
		labelY();
	}

	private void renderGrid() {
		ArrayList<String> xLabels = bucketizer.getXValues();
		ArrayList<String> yLabels = bucketizer.getYValues();

		// add vertical lines
		for (int col = 0; col < xLabels.size(); col++) {
			// grab the top + bottom
			Rect top = gridLayout.getCellBounds(col, 0);
			Rect bottom = gridLayout.getCellBounds(col, yLabels.size()-1);

			// draw left edge
			stroke(color(208,208,208));
			fill(color(208, 208, 208));
			line(top.x, top.y, bottom.x, bottom.y + bottom.h + PADDING_BOTTOM - 10);

			// draw right edge on last col
			if (col == xLabels.size() - 1) {
				line(top.x + top.w, top.y, bottom.x + bottom.w, bottom.y + bottom.h + PADDING_BOTTOM - 10);
			}
		}

		// add horizontal lines
		for (int row = 0; row < yLabels.size(); row++) {
			// grab the top + bottom
			Rect left = gridLayout.getCellBounds(0, row);
			Rect right = gridLayout.getCellBounds(xLabels.size()-1, row);

			// draw left edge
			stroke(color(208,208,208));
			fill(color(208, 208, 208));
			line(left.x - PADDING_LEFT + 10, left.y, right.x + right.w, right.y);

			// draw bottom line on last row
			if (row == yLabels.size() - 1) {
				line(left.x - PADDING_LEFT + 10, left.y + left.h, right.x + right.w, right.y + right.h);
			}
		}
	}

	private void labelX() {
		ArrayList<String> labels = bucketizer.getXValues();
		for (int col = 0; col < labels.size(); col++) {
			Point center = xLabelLayout.getCellBounds(col, 0).getCenter();
			renderLabel(labels.get(col), center, true);
		}
	}

	private void labelY() {
		ArrayList<String> labels = bucketizer.getYValues();
		for (int row = 0; row < labels.size(); row++) {
			Point center = yLabelLayout.getCellBounds(0, row).getCenter();
			renderLabel(labels.get(row), center, false);
		}
	}

	private void renderLabel(String letters, Point center, boolean vertical) {
		textSize(FONT_SIZE);
		textAlign(CENTER, CENTER);
		fill(color(0,0,0));
		
		pushMatrix();
	  	translate(center.x, center.y);

	  	if (vertical) {
	  		rotate(HALF_PI);	
	  	}
		
		text(letters, 0,0);

		popMatrix();
	}

	private void renderCells() {
		for (int col = 0; col < bucketizer.getXValues().size(); col++) {
			for (int row = 0; row < bucketizer.getYValues().size(); row++) {

				int count = bucketizer.getCount(col, row);
				Rect bounds = gridLayout.getCellBounds(col, row);
				int fillColor = getColor(col, row, count);

				noStroke();
				fill(fillColor);
				rect(bounds.x, bounds.y, bounds.w, bounds.h);
			}
		}
	}

	// maps counts to colors
	private int getColor(int col, int row, int count) {
		// should this (col,row) be selected?
		if (isSelected(col, row)) {
			return SELECTED_COLOR;
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

  private final NetworkView networkView;
  private final CategoricalView categoricalView;
  private final TemporalView temporalView;
  
  public Kontroller(ArrayList<Datum> data) {
    this.data = data;
    
    this.categoricalView = new CategoricalView(data);
    this.temporalView = new TemporalView(data);

    Rect bounds = new Rect(0, 0, 0.75f * width, height / 2);
    this.networkView = new NetworkView(data, bounds);
    positionView(networkView, 0, 0, 0.75f, 0.5f);
  } 
  
  public void render() {
    // reposition everything
    updateGraphPositions();
    
    // hover
    ArrayList<Datum> hovered = getHoveredDatums();
    deselectAllData();
    selectData(hovered);

    // render:
    background(color(255, 255, 255));
    categoricalView.render();
    temporalView.render();
    networkView.render();
  }
  
  // repositions the graphs based on the current width/height of the screen
  private void updateGraphPositions() {
    positionView(temporalView, 0, 0.5f, 0.75f, 0.5f);
    positionView(categoricalView, 0.75f, 0, 0.25f, 1.0f);
    positionView(networkView, 0, 0, 0.75f, 0.5f);
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
      temporalView.getHoveredDatums(),
      networkView.getHoveredDatums()
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


class NetworkView extends AbstractView {

	public final float NODE_WEIGHT = 4;
	public final float SPRING_LENGTH = 100;
	public final int MAX_SPRING_THICKNESS = 4;

	private final ArrayList<Node> nodes;

	private ForceDirectedGraph fdg;

	public NetworkView(ArrayList<Datum> data, Rect myBounds) {
		super(data);
		ArrayList<Node> nodes = createNodes();
		this.nodes = nodes;
		ArrayList<Spring> springs = createSprings();
		ArrayList<Zap> zaps = createZaps();
		ArrayList<Damper> dampers = createDampers();
		setAllBounds(myBounds);
		placeNodes(myBounds);
		addBackingDatums();
		scaleSpringThickness(springs);
		fdg = new ForceDirectedGraph(nodes, springs, zaps, dampers, data);
	}

	private ArrayList<Node> createNodes() {
		HashSet<String> nodesToMake = getNodesToMake();
		ArrayList<Node> toReturn = new ArrayList<Node>();

		for (String s : nodesToMake) {
			toReturn.add(new Node(s, NODE_WEIGHT));
		}

		return toReturn;
	}


	// HashSet filters out duplicates
	private HashSet<String> getNodesToMake() {
		HashSet<String> toReturn = new HashSet<String>();

		for (Datum d : getData()) {
			toReturn.add(d.destIP);
			toReturn.add(d.sourceIP);
		}

		return toReturn;
	}


	private ArrayList<Spring> createSprings() {
		ArrayList<Spring> toReturn = new ArrayList<Spring>();

		HashMap<String, Integer> springsToMake= getSpringsToMake();

		for (Map.Entry me : springsToMake.entrySet()) {
			toReturn.add(makeSpring(me));			
		}

		return toReturn;
	}

	// Creates "destIP,sourceIP" strings to tell calling function
	// which springs to make
	private HashMap<String, Integer> getSpringsToMake() {
		HashMap<String, Integer> toReturn = new HashMap<String, Integer>();

		for (Datum d : getData()) {
			String toBeKey = d.destIP + "," + d.sourceIP;
			if (toReturn.get(toBeKey) ==  null) { // First time seeing it
				toReturn.put(toBeKey, 1);  // First one seen
			} else {  // Seen it before
				toReturn.put(toBeKey, toReturn.get(toBeKey) + 1); // Increment #
			}
		}

		return toReturn;
	}

	private Spring makeSpring(Map.Entry me) {
		String k = (String)me.getKey();  // UGLY CASTING YUK
		Integer weightPoint = (Integer)me.getValue();
		int weight = weightPoint.intValue();
		String[] listL = split(k, ",");
		String endAID = listL[0];
		String endBID = listL[1];

		Spring spring = new Spring(getCorrectNode(endAID), 
			getCorrectNode(endBID), SPRING_LENGTH);

		spring.setWeight(weight);

		return spring;
	}

	private Node getCorrectNode(String id) {
		for (Node n : nodes) {
			if (n.id.equals(id)) {
				return n;
			}
		}

		// Didn't find a node, something wrong
		println("ERROR: Invalid Node ID in getCorrectNode");
		return null;
	}

  // Makes a bunch of zaps
  private ArrayList<Zap> createZaps() {
    ArrayList<Zap> toReturn = new ArrayList<Zap>();
    for (int i = 0; i < nodes.size(); i++) {
      for (int j = (i + 1); j < nodes.size(); j++) {
        toReturn.add(new Zap(nodes.get(i), nodes.get(j)));
      }
    }
    return toReturn;
  }

  private ArrayList<Damper> createDampers() {
    ArrayList<Damper> toReturn = new ArrayList<Damper>();
    for (int i = 0; i < nodes.size(); i++) {
      toReturn.add(new Damper(nodes.get(i)));
    }

    return toReturn;
  }

	public void render() {
		fdg.render();
	}

	public ArrayList<Datum> getHoveredDatums() {
		ArrayList<Datum> toReturn = new ArrayList<Datum>();
		for (Node n : fdg.getNodes()) {
			if (n.containsPoint(mouseX, mouseY)) {
				for (Datum d :  n.datumsEncapsulated) {
					toReturn.add(d);
				}
			}
		}

		return toReturn;
	}

	public void setBounds(Rect bounds) {
		setAllBounds(bounds);
		fdg.setBounds(bounds);
	}

	private void setAllBounds(Rect myBounds) {
    	for (Node n : nodes) {
      		n.setBounds(myBounds);
    	}
	}

  // Randomly assigns position of nodes within playing field.
  private void placeNodes(Rect myBounds) {
    for (int i = 0; i < nodes.size(); i++) {
      Node toEdit = nodes.get(i);
      toEdit.pos.x = random(myBounds.x, myBounds.w);
      toEdit.pos.y = random(myBounds.y, myBounds.h);

      if (toEdit.id.equals("*.1.0-10")) {
      	toEdit.pos.x = myBounds.x + myBounds.w / 2;
      	toEdit.pos.y = myBounds.y + myBounds.h / 2;
      }
    }
  }

  private void addBackingDatums() {
  	for (Node n : nodes) {
  		n.datumsEncapsulated = new ArrayList<Datum>();
  		for (Datum d : getData()) {
  			if (n.id.equals(d.destIP) || 
  				n.id.equals(d.sourceIP)) {
  				n.datumsEncapsulated.add(d);
  			}
  		}
  	}
  }

  // Finds the most weighted spring, sets this to MAX_SPRING_THICKNESS
  // and the scales the rest of the springs
  private void scaleSpringThickness(ArrayList<Spring> springs) {
  	int currentMaxThickness = getCurrentMaxThickness(springs);

  	for (Spring s : springs) {
  		s.setWeight((s.getWeight() * MAX_SPRING_THICKNESS) / currentMaxThickness);
  	}
  }

  private int getCurrentMaxThickness(ArrayList<Spring> springs) {
  	int maxW = 0;

  	for (Spring s : springs) {
  		if (s.getWeight() > maxW) {
  			maxW = s.getWeight();
  		}
  	}

  	return maxW;
  }

  public ForceDirectedGraph getFDG() {
  	return fdg;
  }
}


class Node {
  public Point pos = new Point();
  public Vector vel = new Vector();
  
  private Vector netForce = new Vector();
  private Vector acc;
  
  public final String id;
  public final float mass;
  public final float radius;
  public ArrayList<Datum> datumsEncapsulated = null;
  
  public boolean fixed = false;

  private Rect bounds = new Rect(0, 0, width, height); // Default val
  
  public Node(String id, float mass) {
    this.id = id;
    this.mass = mass;
    this.radius = sqrt(mass / PI) * 10;
  }

  public Rect getBounds() {
    return bounds;
  }

  public void setBounds(Rect r) {
    bounds = r;
  }
  
  public void addForce(Vector f) {
//    println("adding force = " + f);
    netForce.add(f);
  }
  
  // f = m * a --> a = f / m
  private void updateAcceleration(float dt) {
//    println("node = " + id + ", netforce = " + netForce);
    
    Vector prev = acc;

    Float f1 = new Float(netForce.x);
    Float f2 = new Float(netForce.y);

    if (f1.isNaN(f1) || f2.isNaN(f2)) {
      netForce = new Vector();
    }
    
    float scale = 1.0f / mass;
    this.acc = netForce.copy().scale(scale, scale);

    if (id.equals("*.1.0-10")) {
      // println("NewAcc = " + acc);
      // System.exit(1);
    }
    
    // reset netForce for next time
    netForce.reset();
  }
  
  private void updateVelocity(float dt) {
    Vector prev = vel.copy();
    
    vel.add(acc.scale(dt, dt));

  }
  
  /**
   * Hit tests a point against the node's position (radius/center)
   */
  public boolean containsPoint(int x, int y) {
    float dist = dist(pos.x, pos.y, x, y);
    return dist < radius;
  }
  
  public void updatePosition(float dt) {
    if (fixed) { 
      netForce.reset();  // Shouldn't accumulate forces if fixed
      return;
    }
    
    updateAcceleration(dt);
    updateVelocity(dt);
    
    Point prev = new Point(pos.x, pos.y);
    pos.add(vel.copy().scale(dt, dt));
    
    ensureInBounds();
  }
  
  private static final float COLLISION_SCALE = -0.8f;
  
  private void ensureInBounds() {
    if (bounds == null) {
      bounds = new Rect(0, 0, width, height);
    }

    float xMin = bounds.x + radius;
    float xMax = bounds.w + bounds.x - radius;
    float yMin = bounds.y + radius;
    float yMax = (bounds.h + bounds.y) - radius;
    if (pos.x < xMin) {
      pos.x = xMin;
  
      vel.x *= COLLISION_SCALE;
    }
    else if (pos.x > xMax) {
      pos.x = xMax;   
         
      vel.x *= COLLISION_SCALE;
    }
    
    if (pos.y < yMin) {
      pos.y = yMin;
      vel.y *= COLLISION_SCALE;
    }
    else if (pos.y > yMax) {
      pos.y = yMax;
      vel.y *= COLLISION_SCALE;
    } 

    Float p1 = new Float(pos.x);
    Float p2 = new Float(pos.y);
    Float v1 = new Float(vel.x);
    Float v2 = new Float(vel.y);

    // If anything is NaN -- make new Point and Velocity
    if (p1.isNaN(p1) || p2.isNaN(p2) ||
        v1.isNaN(v1) || v2.isNaN(v2)) {
        pos = new Point(random(width), random(height));  // Place new point randomly
        vel = new Vector(0.0f, 0.0f);  // Start it out with no movement
    }
  }
  
  public float getKineticEnergy() {
    float speed = vel.getMagnitude();
    float ke = 0.5f * mass * speed*speed;
    return ke;   // 0.5 m * (v^2)
  }

  public String toString() {
    return "id = " + id + ", mass = " + mass + ";";
  }
}



class PieChart extends AbstractView {

	// draws a wedge
	class WedgeView {

		private final String value;
		private final ArrayList<Datum> data;

		private final float startAngle;
		private final float endAngle;

		private final int fillColor;

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
			// stroke(OUTLINE_COLOR);
			noStroke();

			// set fill color based on whether any element is selected
			if (containsSelectedDatum()) {
				fill(SELECTED_COLOR);
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
			float r = radius * 1.3f;

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
		float radius = 0.6f * limitingDimen/2;

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

/**
 * Responsible for rendering the current state of the simulation.
 */
class RenderMachine {
  
  private final int TEXT_SIZE = 14;
  private final int STROKE_WEIGHT = 1;
  
  private final int EMPTY_NODE_COLOR = color(0,0,0);
  private final int MOUSED_NODE_COLOR = color(0, 255, 0);
  
  private final int SPRING_COLOR = color(0,0,255);
  
  private final ArrayList<Node> nodes;
  private final ArrayList<Spring> springs;
 
  public RenderMachine(ArrayList<Node> nodes, ArrayList<Spring> springs) {
    this.nodes = nodes;
    this.springs = springs;
  } 

  public void setAllBounds(Rect r) {
    for (Node n : nodes) {
      n.setBounds(r);
    }
  }
  
  public void render() {
    renderSprings();
    renderNodes();
  }
  
  private void renderSprings() {
    for (Spring s : springs) {
      renderSpring(s);
    }
  }
  
  private void renderSpring(Spring s) {
    Point endA = s.endA.pos;
    Point endB = s.endB.pos;
   
    stroke(SPRING_COLOR);
    fill(SPRING_COLOR);
    
    strokeWeight(s.weight);
    line(endA.x, endA.y, endB.x, endB.y); 
    strokeWeight(STROKE_WEIGHT);
  }
  
  private void renderNodes() {
    for (Node n : nodes) {
      renderNode(n, getNodeColor(n));
    } 
  }
  
  private boolean isSelected(Node n) {
    for (Datum d : n.datumsEncapsulated) {
      if (d.isSelected()) {
        return true;
      }
    }
    return false;
  }

  private int getNodeColor(Node n) {
    if (isSelected(n)) {
      return color(255, 255, 0);  // stolen from AbstractView
    } else {
      return EMPTY_NODE_COLOR;
    }
    // return n.containsPoint(mouseX, mouseY) ? MOUSED_NODE_COLOR : EMPTY_NODE_COLOR;
  }
  
  private void renderNode(Node n, int c) {
    stroke(c);
    fill(c);
    circle(n.pos, n.radius);
  }
  
  
  private void circle(Point center, float radius) {
    ellipseMode(RADIUS);
    ellipse(center.x, center.y, radius, radius);
  }
}
// Runs the simulation
class Simulator {
  
  private static final float RESTING_ENERGY = 200;
  
  private final ArrayList<Node> nodes;
  private final ArrayList<Spring> springs;
  private final ArrayList<Zap> zaps;
  private final ArrayList<Damper> dampers;
  
  public Simulator(ArrayList<Node> nodes, ArrayList<Spring> springs, ArrayList<Zap> zaps, ArrayList<Damper> dampers) {
    this.nodes = nodes;
    this.springs = springs;
    this.zaps = zaps;
    this.dampers = dampers;
  } 
  
  // returns true if the system should be redrawn
  public boolean step(float dt) {
    aggregateForces();
    updatePositions(dt);
    
    return getKineticEnergy() > RESTING_ENERGY;
  }
  
  private void aggregateForces() {
    // tell all of the springs to apply their forces
    for (Spring s : springs) {
      s.applyForce(); 
    }
    
    // tell all the dampers to apply their forces
    for (Damper d : dampers) {
      d.applyForce(); 
    }
    
    // tell all the zaps to apply their forces
    for (Zap z : zaps) {
      z.applyForce(); 
    }
    
  }
  
  private float getKineticEnergy() {
    float total = 0;
    
    for (Node n : nodes) {
      total += n.getKineticEnergy(); 
    }
        
    return total;
  }
  
  // applies nodes' velocities to t
  private void updatePositions(float dt) {
    for (Node n : nodes) {
      n.updatePosition(dt);
    } 
  }
  
  private ArrayList<Node> getNodes() {
    return nodes; 
  }
  
}
// This is what you think it is
class Spring extends InterNodeForce {
  
  private static final float K = 2f;
  // private static final float K = 0f;
  
  public final float restLen;
  private int weight = 3;
  
  public Spring(Node endA, Node endB, float restLen) {
    super(endA, endB);
    this.restLen = restLen;
  }

  public int getWeight() {
    return weight;
  }

  public void setWeight(int w) {
    weight = w;
  }
  
  public void applyForce() {
    // force is proportional to the diff between restLen and current idst 
    
    // a vector from A --> B
    Vector diff = new Vector(endA.pos, endB.pos);
    
    // compute the current distance
    float dist = diff.getMagnitude();
    // compute dx, which is what the force depends on
    float dx = Math.abs(dist - restLen);
    
    // a vector containing just the direction component of A --> B 
    Vector dir = diff.copy().normalize();

    Vector force = dir.copy().scale(-K * dx, -K * dx);    
    
    if (restLen < getDistance()) {
      // forces go INWARDS
      
      endB.addForce(force);
      endA.addForce(force.reverse());  
    } else {
      // forces go OUTWARDS
      
      endA.addForce(force);
      endB.addForce(force.reverse());
    }
  }

  public String toString() {
    return "Node 1 = " + endA.id + ", Node 2 = " + endB.id + ", restLen = "
      + restLen + ";";
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
// Instances of Coluomb laws
class Zap extends InterNodeForce {

  private static final float K = 100000f;
  // private static final float K = 0;
  
  public Zap(Node endA, Node endB) {
    super(endA, endB);
  }
  
  public void applyForce() {
    float r = getDistance();
    
    // make sure r is always >= 1
    r = max(1, r);

    // compute the magnitude of the coulombs force
    float mag = K / (r*r);
    
    // normalize to extract direction, then scale by mag
    Vector force = new Vector(endA.pos, endB.pos).normalize().scale(mag, mag);
    
    // apply to end points
    endB.addForce(force);
    endA.addForce(force.reverse());
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
