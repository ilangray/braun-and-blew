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

public class fdg extends PApplet {


// this is awesome. lets do some physics yolo swag


Simulator sm;
RenderMachine rm;
CenterPusher cp;
ForceDirectedGraph fdg;
Rect halfBounds;
Rect bounds;
NetworkView nv;

boolean done = false;

boolean first = true;

int previous_w;
int previous_h;

public void setup() {
  size(1400, 800);
  previous_w = width;
  previous_h = height;
  frame.setResizable(true);
  bounds = new Rect(0, 0, width, height);
  nv = new NetworkView(new DerLeser("data_aggregate.csv").readIn(), bounds);

  // bounds = new Rect(width / 2, height / 2, width - width/2, height - height/2);


  nv.setBounds(bounds);

  // DieWelt w = new Configurator("data.csv", bounds).configure();

  // fdg = new ForceDirectedGraph(w.nodes, w.springs, w.zaps, w.dampers, null);

  // if (fdg == null) {
  //   println("In die Gerate fdg null ist");
  // }

  // fdg.setBounds(bounds);
  // fdg.getCenterPusher().setBounds(bounds);

}

// converts ms to seconds
public float seconds(int ms) {
  return ms / 1000.0f; 
}

public void draw() {
  nv.render();
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
	protected Rect bounds;

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
// // Reads in the file
// class Configurator {
//   public final String fileName;
//   public final Rect bounds;

//   public Configurator(String fileName, Rect bounds) {
//     this.fileName = fileName;
//     this.bounds = bounds;
//   }

//   public DieWelt configure() {
//     ArrayList<String> al = read();
//     DieWelt world = initWorld(al);
//     // // setAllBounds(world);
//     // placeNodes(world);
//     return world;
//   }

//   // Randomly assigns position of nodes within playing field.
//   // TODO: Currently doesn't prevent nodes from overlapping or from part of a node from hanging off the screen
//   private void placeNodes(DieWelt world) {
//     for (int i = 0; i < world.nodes.size(); i++) {
//       Node toEdit = world.nodes.get(i);
//       toEdit.pos.x = random(bounds.x, bounds.w);
//       toEdit.pos.y = random(bounds.y, bounds.h);
//     }
//   }

//   private void setAllBounds(DieWelt world) {
//     for (Node n : world.nodes) {
//       n.setBounds(bounds);
//     }
//   }

//   // Initializes the nodes and springs from the info from the file.
//   // Creates an ArrayList of Zaps and an ArrayList of Dampers, but these
//   // last two are independent on the input file.
//   // Returns Die Welt.
//   private DieWelt initWorld(ArrayList<String> al) {
//     ArrayList<Node> nodes = new ArrayList<Node>();
//     ArrayList<Spring> springs = new ArrayList<Spring>();
//     ArrayList<Zap> zaps = new ArrayList<Zap>();
//     ArrayList<Damper> dampers = new ArrayList<Damper>();
//     boolean inNodes = false;

//     for (int i = 0; i < al.size (); i++) {
//       // If no commas - they are list sizes and we don't need those
//       if (al.get(i).indexOf(',') == -1) {
//         inNodes = !inNodes;
//         continue;
//       }

//       // Split on commas
//       String[] listL = split(al.get(i), ',');

//       // If get to this point, not in first line or nodes -- definitely in Springs
//       if (!inNodes) {
//         Spring newSpring = new Spring(getCorrectNode(int(listL[0]), nodes), getCorrectNode(int(listL[1]), nodes), int(listL[2]));
//         springs.add(newSpring);
//       } else {  // We are in Nodes
//         Node newNode = new Node(listL[0], float(listL[1]));
//         nodes.add(newNode);
//       }
//     }

    
//     DieWelt w = new DieWelt(nodes, springs, createZaps(nodes), createDampers(nodes), bounds);

//     setAllBounds(w);
//     placeNodes(w);

//     return w;
//   }

//   // Returns the node with the id
//   // If no node exists with that id, than null is returned
//   public Node getCorrectNode(int idToFind, ArrayList<Node> nodes) {
//     for (String i = 0; i < nodes.size (); i++) {
//       if (nodes.get(i).id == idToFind) {
//         return nodes.get(i);
//       }
//     }
//     return null;
//   }
  
//   // Makes a bunch of zaps
//   public ArrayList<Zap> createZaps(ArrayList<Node> nodes) {
//     ArrayList<Zap> toReturn = new ArrayList<Zap>();
//     for (int i = 0; i < nodes.size(); i++) {
//       for (int j = (i + 1); j < nodes.size(); j++) {
//         toReturn.add(new Zap(nodes.get(i), nodes.get(j)));
//       }
//     }
//     return toReturn;
//   }
  
//   public ArrayList<Damper> createDampers(ArrayList<Node> nodes) {
//     ArrayList<Damper> toReturn = new ArrayList<Damper>();
//     for (int i = 0; i < nodes.size(); i++) {
//       toReturn.add(new Damper(nodes.get(i)));
//     }
//     return toReturn;
//   }

//   // Just reads in lines and puts them in ArrayList -- does nothing else
//   private ArrayList<String> read() {
//     String[] linesNormalArray = loadStrings(fileName);
//     ArrayList<String> lines = new ArrayList<String>();

//     for (int i = 0; i < linesNormalArray.length; i++) {
//       lines.add(linesNormalArray[i]);
//     } 
//     return lines;
//   }
// }


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
// Used to return an ArrayList of Springs and an ArrayList of Nodes to the Simulator
class DieWelt {
  public final ArrayList<Node> nodes;
  public final ArrayList<Spring> springs;
  public final ArrayList<Zap> zaps;
  public final ArrayList<Damper> dampers;
  public final Rect bounds;
  
  public DieWelt(ArrayList<Node> nodes, ArrayList<Spring> springs, ArrayList<Zap> zaps, 
    ArrayList<Damper> dampers, Rect bounds) {
    this.nodes = nodes;
    this.springs = springs;
    this.zaps = zaps;
    this.dampers = dampers;
    this.bounds = bounds;
  }
}
  // the node currently being dragged
  public Node dragged = null;

  public Node getNode(int x, int y) {
    for (Node n : nv.fdg.getSimulator().getNodes()) {
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
        if (bounds == null) {
            println("BOUNDS ARE NULL IN DRAG MANAGER");
            System.exit(1);
        }
        float xMin = bounds.x + dragged.radius;
        float xMax = bounds.w + bounds.x - dragged.radius;
        float yMin = bounds.y + dragged.radius;
        float yMax = (bounds.h + bounds.y) - dragged.radius;
      dragged.pos.x = clamp(mouseX, (int)xMin, (int)xMax);
      dragged.pos.y = clamp(mouseY, (int)yMin, (int)yMax);  
    }
  }

  public void mouseReleased() {
    if (dragged != null) {
      dragged.fixed = false;
      dragged = null; 
    }
  }
class ForceDirectedGraph extends AbstractView {
	private RenderMachine rm;
	private Simulator sm;
	private CenterPusher cp;

	public ForceDirectedGraph(ArrayList<Node> nodes, ArrayList<Spring> springs,
		ArrayList<Zap> zaps, ArrayList<Damper> dampers, ArrayList<Datum> data) {
		super(data);

		rm = new RenderMachine(nodes, springs);
		sm = new Simulator(nodes, springs, zaps, dampers);
		cp = new CenterPusher(nodes);		
	}

	public void render() {
		 if (!done || dragged != null || previous_w != width || previous_h != height) {
   		 // update sim
    	done = !sm.step(seconds(16));
  	}

		background(color(255, 255, 255));
		cp.push();
		rm.render();
	}

	public Simulator getSimulator() {
		return sm;
	}

	public CenterPusher getCenterPusher() {
		return cp;
	}

	public void setBounds(Rect bounds) {
		this.bounds = bounds;
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


class NetworkView extends AbstractView {

	public final float NODE_WEIGHT = 4;
	public final float SPRING_LENGTH = 100;

	public ForceDirectedGraph fdg;

	public NetworkView(ArrayList<Datum> data, Rect myBounds) {
		super(data);
		ArrayList<Node> nodes = createNodes();
		ArrayList<Spring> springs = createSprings(nodes);
		ArrayList<Zap> zaps = createZaps(nodes);
		ArrayList<Damper> dampers = createDampers(nodes);
		setAllBounds(nodes, myBounds);
		placeNodes(nodes, myBounds);
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


	public ArrayList<Spring> createSprings(ArrayList<Node> nodes) {
		ArrayList<Spring> toReturn = new ArrayList<Spring>();

		HashSet<String> springsToMake= getSpringsToMake();

		for (String s : springsToMake) {
			toReturn.add(makeSpring(s, nodes));
		}

		return toReturn;
	}

	// Creates "destIP,sourceIP" strings to tell calling function
	// which springs to make
	public HashSet<String> getSpringsToMake() {
		HashSet<String> toReturn = new HashSet<String>();

		for (Datum d : getData()) {
			toReturn.add(d.destIP + "," + d.sourceIP);
		}

		return toReturn;
	}

	public Spring makeSpring(String s, ArrayList<Node> nodes) {
		String[] listL = split(s, ",");
		String endAID = listL[0];
		String endBID = listL[1];

		return new Spring(getCorrectNode(endAID, nodes), 
			getCorrectNode(endBID, nodes), SPRING_LENGTH);
	}

	public Node getCorrectNode(String id, ArrayList<Node> nodes) {
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

	public void render() {
		fdg.render();
	}

	public ArrayList<Datum> getHoveredDatums() {
		return null;
	}

	public void setBounds(Rect bounds) {
		fdg.setBounds(bounds);
	}

	private void setAllBounds(ArrayList<Node> nodes, Rect myBounds) {
    	for (Node n : nodes) {
      		n.setBounds(myBounds);
    	}
	}

  // Randomly assigns position of nodes within playing field.
  private void placeNodes(ArrayList<Node> nodes, Rect myBounds) {
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
}


class Node {
  public Point pos = new Point();
  public Vector vel = new Vector();
  
  private Vector netForce = new Vector();
  private Vector acc;
  
  public final String id;
  public final float mass;
  public final float radius;
  
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

    // if (id.equals("*.1.0-10")) {
      // println("PrevAcc = " + prev);
      // println("Mass is = " + mass);
      // netForce = new Vector();
      
    // }

    Float f1 = new Float(netForce.x);
    Float f2 = new Float(netForce.y);

    if (f1.isNaN(f1) || f2.isNaN(f2)) {
      netForce = new Vector();
    }

    // if (id.equals("*.1.0-10")) {
    //   println("TROUBLE:");
    // } else {
    //   println("okay:");
    // }

    // println("Netforce = " + netForce);


    
    float scale = 1.0f / mass;
    this.acc = netForce.copy().scale(scale, scale);

    if (id.equals("*.1.0-10")) {
      // println("NewAcc = " + acc);
      // System.exit(1);
    }
    
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

    if (p1.isNaN(p1) || p2.isNaN(p2)) {
      // println("POS IS NAN = " + id);
    }

    if (v1.isNaN(v1) || v2.isNaN(v2)) {
      // println("VELOCITY NULL = " + id);
    }

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
  
  private void renderNodes() {
    for (Node n : nodes) {
      renderNode(n, getNodeColor(n));
    } 
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
  
  private void renderNode(Node n, int c) {
    stroke(c);
    fill(c);
    circle(n.pos, n.radius);
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
  // private static final float K = 0f;
  
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

  public String toString() {
    return "Node 1 = " + endA.id + ", Node 2 = " + endB.id + ", restLen = "
      + restLen + ";";
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
    String[] appletArgs = new String[] { "fdg" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
