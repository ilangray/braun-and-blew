import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

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
  DieWelt w = new Configurator("data.csv").configure();
  
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

// Reads in the file
class Configurator {
  public final String fileName;

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

  // Initializes the nodes and springs from the info from the file.
  // Creates an ArrayList of Zaps and an ArrayList of Dampers, but these
  // last two are independent on the input file.
  // Returns Die Welt.
  private DieWelt initWorld(ArrayList<String> al) {
    ArrayList<Node> nodes = new ArrayList<Node>();
    ArrayList<Spring> springs = new ArrayList<Spring>();
    ArrayList<Zap> zaps = new ArrayList<Zap>();
    ArrayList<Damper> dampers = new ArrayList<Damper>();
    boolean inNodes = false;

    for (int i = 0; i < al.size (); i++) {
      // If no commas - they are list sizes and we don't need those
      if (al.get(i).indexOf(',') == -1) {
        inNodes = !inNodes;
        continue;
      }

      // Split on commas
      String[] listL = split(al.get(i), ',');

      // If get to this point, not in first line or nodes -- definitely in Springs
      if (!inNodes) {
        Spring newSpring = new Spring(getCorrectNode(PApplet.parseInt(listL[0]), nodes), getCorrectNode(PApplet.parseInt(listL[1]), nodes), PApplet.parseInt(listL[2]));
        springs.add(newSpring);
      } else {  // We are in Nodes
        Node newNode = new Node(PApplet.parseInt(listL[0]), PApplet.parseFloat(listL[1]));
        nodes.add(newNode);
      }
    }
    
    return new DieWelt(nodes, springs, createZaps(nodes), createDampers(nodes));
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
  
  public DieWelt(ArrayList<Node> nodes, ArrayList<Spring> springs, ArrayList<Zap> zaps, ArrayList<Damper> dampers) {
    this.nodes = nodes;
    this.springs = springs;
    this.zaps = zaps;
    this.dampers = dampers;
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


class Node {
  public Point pos = new Point();
  public Vector vel = new Vector();
  
  private final Vector netForce = new Vector();
  private Vector acc;
  
  public final int id;
  public final float mass;
  public final float radius;
  
  public boolean fixed = false;

  public Rect bounds;
  
  public Node(int id, float mass) {
    this.id = id;
    this.mass = mass;
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
    String[] appletArgs = new String[] { "fdg" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
