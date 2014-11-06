import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.*; 
import java.lang.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class lab9 extends PApplet {


// this is awesome. lets do some physics yolo swag

Simulator sm;
RenderMachine rm;
CenterPusher cp;
Rect halfBounds;

boolean done = false;
boolean first = true;

int previous_w;
int previous_h;

public void setup() {
  size(1400, 800);
  previous_w = width;
  previous_h = height;
  frame.setResizable(true);
 
  // read data
  DieWelt w = new Configurator("data1.csv").configure();
  
  // System.exit(1);
  // configur renderer and simulator
  rm = new RenderMachine(w.nodes, w.springs);
  rm.setAllBounds(halfBounds);
  sm = new Simulator(w.nodes, w.springs, w.zaps, w.dampers);
  cp = new CenterPusher(w.nodes);
}

// converts ms to seconds
public float seconds(int ms) {
  return ms / 1000.0f; 
}

public void draw() {
  // yoloswag
  if (first) {
    rm.renderLabel(new Point(0,0), "hooha");
    first = false; 
  }
  
  if (!done || dragged != null || previous_w != width || previous_h != height) {
    // update sim
    done = !sm.step(seconds(16));
  }
  
  cp.push();
  render();
  
  previous_w = width;
  previous_h = height;
}

public void render() {
  background(color(255,255,255));
  rm.render();
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
// Reads in the file
class Configurator {
  public final String fileName;
  public final int MASS = 10;
  public final int EXTERNAL_LINK_WEIGHT = 1;
  public final float SPRING_REST_LEN = 100.0f;;

  public Configurator(String fileName) {
    this.fileName = fileName;
  }

  public DieWelt configure() {
    ArrayList<String> al = read();
    DieWelt world = initWorld(al);
    placeNodes(world);
    return world;
  }

  // Randomly assigns position of nodes within playing field.
  // TODO: Currently doesn't prevent nodes from overlapping or from part of a node from hanging off the screen
  private void placeNodes(DieWelt world) {
    for (int i = 0; i < world.nodes.size(); i++) {
      Node toEdit = world.nodes.get(i);
      toEdit.pos.x = random(width);
      toEdit.pos.y = random(height);
    }
  }

  // New initWorld
  private DieWelt initWorld(ArrayList<String> al) {
    ArrayList<Node> nodes = new ArrayList<Node>();
    ArrayList<Spring> springs = new ArrayList<Spring>();
    ArrayList<Zap> zaps = new ArrayList<Zap>();
    ArrayList<Damper> dampers = new ArrayList<Damper>();
    ArrayList<Link> externalLinks = new ArrayList<Link>();

    boolean newNode = false;
    boolean inLinks = false;

    int count = -1;
    int nodeID = -1;
    int numPeeps = -1;
    int numLinks = -1;
    ArrayList<String> newAuthors = null;
    ArrayList<Link> newLinks = null;
    for (int i = 0; i < al.size(); i++) {
      if (i == 0) { continue; };  // Num nodes I already know this

      // Does the line start with a number? -- if so it is a new node
      if (Character.isDigit(al.get(i).charAt(0))) {
          newNode = true;
          count = 0;
          int[] nums = PApplet.parseInt(split(al.get(i), ','));
          nodeID = nums[0];
          numPeeps = nums[1];
          numLinks = nums[2];

          newAuthors = new ArrayList<String>();
          newLinks = new ArrayList<Link>();
      } else if (newNode) {
          if (count  >= numPeeps + numLinks) {  // Don't count descriptor line
          Datum newDatum = new Datum(nodeID ,newAuthors, newLinks);
          nodes.add(new Node(newDatum, MASS, nodeID));
          newNode = false;
          inLinks = false;
          }   else {
          // Reading in the links
            if (inLinks) {
              String[] listL = split(al.get(i), ',');
              newLinks.add(new Link(listL[0], listL[1], Integer.parseInt(listL[2])));
            } else {  // Reading in the peoples
              newAuthors.add(al.get(i));
            }

            if (count >= numPeeps) {   // Now I'll be in links
              inLinks = true;
            }
          } 
      } else if (!Character.isDigit(al.get(i).charAt(0)) && !newNode) {
        // This happens when you have external connections
        // Make the external link

        String[] listL = split(al.get(i), ',');
        externalLinks.add(new Link(listL[0], listL[2], EXTERNAL_LINK_WEIGHT));

        // Make the spring connecting these
        springs.add(new Spring(getNodeAtIndex(Integer.parseInt(listL[1]), nodes), getNodeAtIndex(Integer.parseInt(listL[3]), nodes), SPRING_REST_LEN));
        }
      count++;
        
      }
      return new DieWelt(nodes, springs, createZaps(nodes), createDampers(nodes), externalLinks);
    }


    // Returns null if index not found
    private Node getNodeAtIndex(int ind, ArrayList<Node> nodes) {
      for (Node n : nodes) {
        if (n.id == ind) {
          return n;
        }
      }

      return null;
    }

  // Returns the node with the id
  // If no node exists with that id, than null is returned
  public Node getCorrectNode(int idToFind, ArrayList<Node> nodes) {
    for (int i = 0; i < nodes.size (); i++) {
      if (nodes.get(i).id == idToFind) {
        return nodes.get(i);
      }
    }
    return null;
  }
  
  // Makes a bunch of zaps
  public ArrayList<Zap> createZaps(ArrayList<Node> nodes) {
    ArrayList<Zap> toReturn = new ArrayList<Zap>();
    for (int i = 0; i < nodes.size(); i++) {
      for (int j = (i + 1); j < nodes.size(); j++) {
        toReturn.add(new Zap(nodes.get(i), nodes.get(j)));
      }
    }
    return toReturn;
  }
  
  public ArrayList<Damper> createDampers(ArrayList<Node> nodes) {
    ArrayList<Damper> toReturn = new ArrayList<Damper>();
    for (int i = 0; i < nodes.size(); i++) {
      toReturn.add(new Damper(nodes.get(i)));
    }
    return toReturn;
  }

  // Just reads in lines and puts them in ArrayList -- does nothing else
  private ArrayList<String> read() {
    String[] linesNormalArray = loadStrings(fileName);
    ArrayList<String> lines = new ArrayList<String>();

    for (int i = 0; i < linesNormalArray.length; i++) {
      lines.add(linesNormalArray[i]);
    } 
    return lines;
  }
}


/**
 * A Damper applies a force proportional to a 
 * node's velocity, in the opposite direction.
 */
class Damper implements ForceSource {

  private static final float K = 0.5f;

  private final Node node;

  public Damper(Node node) {
    this.node = node;
  }
    
  public void applyForce() {
    Vector velocity = node.vel.copy().scale(-K, -K);
    node.addForce(velocity);
  }
}

class Link {
  private final String authorA;
  private final String authorB;
  private final int weight;
 
  public Link(String authorA, String authorB, int weight) {
    this.authorA = authorA;
    this.authorB = authorB;
    this.weight = weight;
  } 
  
  public boolean hasAuthors(String a1, String a2) {
     return authorA.equals(a1) && authorB.equals(a2) || 
            authorA.equals(a2) && authorB.equals(a1); 
  }

  public String toString() {
    return "AuthorA = " + authorA + ", AuthorB = " + authorB + ", weight = " + weight;
  }
}

class Datum {
  
  private int id;
  private ArrayList<String> authors;
  private ArrayList<Link> links;
  
  public Datum(int id, ArrayList<String> authors, ArrayList<Link> links) {
    this.id = id;
    this.authors = authors;
    this.links = links;
  }

  public ArrayList<String> getAllAuthors() {
    return authors;
  }

  public ArrayList<Link> getAllLinks() {
    return links;
  }
  
  // defaults to zero if no such link exists
  public int getLink(String a1, String a2) {
    for (Link link : links) {
      if (link.hasAuthors(a1, a2)) {
        return link.weight; 
      }
    }
    
    return 0;
  } 
  
  public boolean containsAuthor(String query) {
    for (String author : authors) {
      if (author.equals(query)) {
        return true;
      } 
    }
    return false;
  }
}
 class CenterPusher {

 	private static final float PERCENT_DIST = 0.01f;

 	private final ArrayList<Node> nodes;

 	public CenterPusher(ArrayList<Node> nodes) {
 		this.nodes = nodes;
 	}

 	public void push() {
 		if (dragged != null) {
 			return;
 		}

 		applyOffset(getOffset(getBounds()));
 	}

 	private Rect getBounds() {
 		float left = width;
 		float top = height;
 		float right = 0;
 		float bottom = 0;

 		for (Node n : nodes) {
 			left = min(left, n.pos.x - n.radius);
 			top = min(top, n.pos.y - n.radius);
 			right = max(right, n.pos.x + n.radius);
 			bottom = max(bottom, n.pos.y + n.radius);
		}

 		return new Rect(left, top, right - left, bottom - top);
	}

	private Point getOffset(Rect r) {
		Point screenCenter = new Point(width/2, height/2);
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
// Used to return an ArrayList of Springs and an ArrayList of Nodes to the Simulator
class DieWelt {
  public final ArrayList<Node> nodes;
  public final ArrayList<Spring> springs;
  public final ArrayList<Zap> zaps;
  public final ArrayList<Damper> dampers;
  public final ArrayList<Link> externalLinks;
  
  public DieWelt(ArrayList<Node> nodes, ArrayList<Spring> springs, 
    ArrayList<Zap> zaps, ArrayList<Damper> dampers, ArrayList<Link> externalLinks) {
    this.nodes = nodes;
    this.springs = springs;
    this.zaps = zaps;
    this.dampers = dampers;
    this.externalLinks = externalLinks;
  }
}

// the node currently being dragged
Node dragged = null;

public Node getNode(int x, int y) {
  for (Node n : sm.getNodes()) {
    if (n.containsPoint(x, y)) {
      return n;
    } 
  }
  return null;
}

public void mousePressed() {
  // what did we hit?
  dragged = getNode(mouseX, mouseY);
  
  if (dragged != null) {
    dragged.fixed = true; 
  }
}

public void mouseDragged() {
  if (dragged != null) {
    float r = dragged.radius;
    dragged.pos.x = clamp(mouseX, (int)r, (int)(width - r));
    dragged.pos.y = clamp(mouseY, (int)r, (int)(height - r));  
  }
}

public void mouseReleased() {
  if (dragged != null) {
    dragged.fixed = false;
    dragged = null; 
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
  
  public String toString() {
    return "Point{x = " + x + ", y = " + y + "}"; 
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
  

class Rect {
  public final float x, y, w, h;
  
  Rect(float x, float y, float w, float h) {
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
  }
  
//  Rect(float x, float y, float w, float h) {
//    this(round(x), round(y), round(w), round(h));
//  }
  
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
 
// clamp like a champ --> "clampion"
public float clamp(float x, int min, int max) {
  return min(max(x, min), max); 
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

        private final Datum datum;

	private final GridLayout gridLayout;

	// layouts that chop up the space available for axis labeling
	private final GridLayout xLabelLayout;
	private final GridLayout yLabelLayout;

	public Heatmap(Datum datum) {
		super(null);
                this.datum = datum;

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

        // returns the center of the label for the given value
        public Point getLabel(String value) {
          int index = getValues().indexOf(value);
          
          return yLabelLayout.getCellBounds(0, index).getCenter();
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

        private ArrayList<String> getValues() {
                return new ArrayList<String>(datum.authors);
        }

	private void renderGrid() {
		ArrayList<String> xLabels = getValues();
		ArrayList<String> yLabels = getValues();

		// gotta set the weight 
		strokeWeight(1);

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
		ArrayList<String> labels = getValues();
		for (int col = 0; col < labels.size(); col++) {
			Point center = xLabelLayout.getCellBounds(col, 0).getCenter();
			renderLabel(labels.get(col), center, true);
		}
	}

	private void labelY() {
		ArrayList<String> labels = getValues();
		for (int row = 0; row < labels.size(); row++) {
			Point center = yLabelLayout.getCellBounds(0, row).getCenter();
			renderLabel(labels.get(row), center, false);
		}
	}

	private void renderLabel(String letters, Point center, boolean vertical) {
		textSize(FONT_SIZE);
		textAlign(CENTER, CENTER);
		fill(color(0,0,0));
		text(letters, 0,0);

		popMatrix();
	}

	private void renderCells() {
                ArrayList<String> authors = getValues();
                
		for (int col = 0; col < authors.size(); col++) {
			for (int row = 0; row < authors.size(); row++) {

                                int count = datum.getLink(authors.get(col), authors.get(row));
				Rect bounds = gridLayout.getCellBounds(col, row);
				int fillColor = getColor(col, row, count);

				noStroke();
				fill(fillColor);
				rect(bounds.x, bounds.y, bounds.w, bounds.h);

				// if hit, render label
				if (bounds.containsPoint(mouseX, mouseY)) {
					renderLabel(bounds.getCenter(), "" + count);
				}
			}
		}
	}

	private void renderLabel(Point p, String s) {  
		textSize(14);
		textAlign(CENTER, CENTER);
		fill(color(0,0,0));
		text(s, p.x, p.y);
	}

	// maps counts to colors
	private int getColor(int col, int row, int count) {
		// should this (col,row) be selected?
		if (isSelected(col, row)) {
			return SELECTED_COLOR;
		}

		// return interpolated, non-selected color
		float p = (float)count / 5;
		return color(255, 0, 0, p * 255);
	}

	private boolean isSelected(int col, int row) {
		return false;
	}

	public ArrayList<Datum> getHoveredDatums() {
		return new ArrayList<Datum>();
	}
}


class Node {
  public Point pos = new Point();
  public Vector vel = new Vector();
  
  private final Vector netForce = new Vector();
  private Vector acc;
 
  public final float mass;
  public final float radius;
  public final int id;
  
  public final Datum datum;
  
  public boolean fixed = false;

  public Rect bounds;
  
  public Node(Datum datum, float mass, int id) {
    this.datum = datum;
    this.mass = mass;
    this.id = id;
    this.radius = sqrt(mass / PI) * 10;

    this.bounds = new Rect(0, 0, width, height);
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
    
    float scale = 1.0f / mass;
    this.acc = netForce.copy().scale(scale, scale);
    
//    println(" -- prev acc = " + prev + ", new = " + acc);
    
    // reset netForce for next time
    netForce.reset();
  }
  
  private void updateVelocity(float dt) {
    Vector prev = vel.copy();
    
    vel.add(acc.scale(dt, dt));
    
//    println(" -- prev vel = " + prev + ", new = " + vel);
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
    
//    println("Node w/ id = " + id);
    
    updateAcceleration(dt);
    updateVelocity(dt);
    
    Point prev = new Point(pos.x, pos.y);
    pos.add(vel.copy().scale(dt, dt));
    
    ensureInBounds();
    
   // println(" -- prev point = " + prev + ", new = " + pos);
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
}

/**
 * Responsible for rendering the current state of the simulation.
 */
class RenderMachine {
  
  private final int TEXT_SIZE = 14;
  
  private final int EMPTY_NODE_COLOR = color(0,0,0);
  private final int MOUSED_NODE_COLOR = color(0, 255, 0);
  
  private final int SPRING_COLOR = color(0,0,255);
  
  private final ArrayList<Node> nodes;
  private final ArrayList<Spring> springs;
  private final ArrayList<Link> externalLinks;
 
  public RenderMachine(ArrayList<Node> nodes, ArrayList<Spring> springs, ArrayList<Link> externalLinks) {
    this.nodes = nodes;
    this.springs = springs;
    this.externalLinks = externalLinks;
  } 
  
  private void renderExternalLinks(ArrayList<Heatmap> heatmaps) {
    for (Link externalLink : externalLinks) {
      renderExternalLink(externalLink, heatmaps);
    } 
  }
  
  private void renderExternalLink(Link link, ArrayList<Heatmap> heatmaps) {
    Point endA = getLinkEnd(link.authorA, heatmaps);
    Point endB = getLinkEnd(link.authorB, heatmaps);
   
    fill(color(0,0,0));
    stroke(color(0,0,0));
    line(endA.x, endA.y, endB.x, endB.y); 
  }
  
  private Point getLinkEnd(String author, ArrayList<Heatmap> heatmaps) {
    // find the node containing that author
    int index = getNodeByAuthor(author);
    Heatmap hm = heatmaps.get(index);
    return hm.getLabel(author);
  }

  // returns node index
  private int getNodeByAuthor(String author) {
    for (int i = 0; i < nodes.size(); i++) {
      if (nodes.get(i).datum.containsAuthor(author)) {
        return i; 
      }
    }
    
    return -1;
  }

  public void setAllBounds(Rect r) {
    for (Node n : nodes) {
      n.setBounds(r);
    }
  }
  
  public void render() {
    renderSprings();
    ArrayList<Heatmap> hms = renderNodes();
    renderExternalLinks(hms);
    renderLabels();
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
    
    line(endA.x, endA.y, endB.x, endB.y); 
  }
  
  // returns the heatmaps
  private ArrayList<Heatmap> renderNodes() {
    ArrayList<Heatmap> heatmaps = new ArrayList<Heatmap>();
    
    for (Node n : nodes) {
      heatmaps.add(renderNode(n, getNodeColor(n)));
    } 
    
    return heatmaps;
  }
  
  public void renderLabels(){
    for (Node n : nodes) {
      if(n.containsPoint(mouseX, mouseY)) {
        String label = "Id: " + n.id + ", Mass: " + n.mass;
        renderLabel(n.pos, label);
      }
    }
  }
  
  private int getNodeColor(Node n) {
    return n.containsPoint(mouseX, mouseY) ? MOUSED_NODE_COLOR : EMPTY_NODE_COLOR;
  }
  
  // this now uses a heatmap
  private Heatmap renderNode(Node n, int c) {
    Heatmap map = new Heatmap(n.data);
    
    float r = n.radius;
    float x = n.center.x;
    float y = n.center.y;
    
    map.setBounds(new Rect(x - r, y - r, 2*r, 2*r));
    map.render();
    
    return map;
  }
  
  
  private void circle(Point center, float radius) {
    ellipseMode(RADIUS);
    ellipse(center.x, center.y, radius, radius);
  }
  
    // renders the given string as a label above the hitbox
  public void renderLabel(Point p, String s) {
     float x = p.x;
     float y = p.y;
     
     // set font size because text measurements depend on it
     textSize(TEXT_SIZE);
     
     // bounding rectangle
     float w = textWidth(s) * 1.1f;
     float h = TEXT_SIZE * 1.3f;
     fill(255,255,255, 200);
     noStroke();
     Rect r = new Rect(x - w/2, y - h, w, h);
     rect(r.x, r.y, r.w, r.h, 3);
     
     // text 
     textAlign(CENTER, BOTTOM);
     fill(color(0,0,0));
     text(s, x, y);
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
    
//    println("ke = " + getKineticEnergy());
    return getKineticEnergy() > RESTING_ENERGY;
//    return true;
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
  
  public final float restLen;
  
  public Spring(Node endA, Node endB, float restLen) {
    super(endA, endB);
    this.restLen = restLen;
  }
  
  public void applyForce() {
    // force is proportional to the diff between restLen and current idst 
//    println("restlen = " + restLen + ", curr dist = " + getDistance()); 
    
    // a vector from A --> B
    Vector diff = new Vector(endA.pos, endB.pos);
    
    // compute the current distance
    float dist = diff.getMagnitude();
    // compute dx, which is what the force depends on
    float dx = Math.abs(dist - restLen);
    
    // a vector containing just the direction component of A --> B 
    Vector dir = diff.copy().normalize();
    
    // ensure that the diff's mag is > 1
//    if (diff.getMagnitude() < 1) {
//      println(" __________ NORMALIZED TO GET MAG UP TO 1 _________________");
//      diff.normalize(); 
//    }
    
//    Vector force = diff.copy().scale(-K, -K);

    Vector force = dir.copy().scale(-K * dx, -K * dx);    
//    println("spring btwn = [" + endA.id + ", " + endB.id + "], dist = " + dist + ", dx = " + dx + ", force = " + force + ", force mag = " + force.getMagnitude());
    
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
}
// Instances of Coluomb laws
class Zap extends InterNodeForce {

  private static final float K = 100000f;
  
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
    String[] appletArgs = new String[] { "lab9" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
