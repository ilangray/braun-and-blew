import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class linetrum extends PApplet {

// main

// constants 
String FILENAME = "hierarchy2.shf";

// globals
SQTM tm;

public void setup() {
  // general canvas setup
  size(600, 400);
  frame.setResizable(true);
  
  // init SQTM
  Rect bounds = new Rect(5, 5, width - 10, height - 10);
  Datum root = new Reader(FILENAME).read();
  
  println("root = " + root);
 
  tm = new SQTM(bounds, root);
}


public void draw() {
  background(color(255, 255, 255));
  tm.render(); 
}


public void mousePressed() {
  if (mouseButton == LEFT) {
    tm.zoomIn(new Point(mouseX, mouseY));
  } else {
    tm.zoomOut();
  }
}


// owned by ilan
class Datum {
  
  public final static int INVALID_VALUE = -1;
  
  public final int id;
  public int value;
  public final ArrayList<Datum> children;
  public final boolean isLeaf;
  
  /**
   * Creates a new leaf datum with the given id and value. children will be null
   */
  public Datum(int id, int value) {
    this(id, value, true);
  }
  
  /**
   * Creates a new NON-leaf datum with the given id. Children can be
   * added by accessing and mutating the list of children.
   */
  public Datum(int id) {
    this(id, INVALID_VALUE, false);
  }
  
  private Datum(int id, int value, boolean isLeaf) {
    this.id = id;
    this.value = value;
    this.isLeaf = isLeaf;
   
    if (isLeaf) {
      this.children = null;
    } else {
      this.children = new ArrayList<Datum>();
    } 
  }
  
  public int calculateValue() {
     if (value != INVALID_VALUE) {
       return value;
     }
     
     int sum = 0;
     for (Datum d : children) {
       sum += d.calculateValue();
     }
     
     this.value = sum;
     return sum;
  }
  
  public void print() {
    println("id = " + id + ", value = " + value);
    
    if (children == null) {
      return;
    }
   
    for (Datum d : children) {
      d.print();
    } 
  }
  
  public float getValueF()
  {
    return (float)value;
  }
  
  public String toString() {
    return "Datum{id = " + id + ", value = " + value + ", isLeaf = " + isLeaf + ", kids = " + children + "}"; 
  }
}
// This is a point
class Point {
  public final float x;
  public final float y;
  
  public Point(float x, float y) {
    this.x = x;
    this.y = y;
  }
  
//  public Point(float x, float y) {
//    this(round(x), round(y));
//  }
  
  public Point offset(Point other) {
    return new Point(other.x + x, other.y + y);
  }
  
  public float distFrom(Point other) {
    float dx = (other.x - x);
    float dy = (other.y - y);
    
    return sqrt(dx*dx + dy*dy);
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

  public void drawRect(Rect r, int stroke, int fill) {
     stroke(stroke);
     fill(fill);
     rect(r.x, r.y, r.w, r.h);
  }
  
  public void strokeRect(Rect r, int stroke) {
    noFill();
    stroke(stroke);
    rect(r.x, r.y, r.w, r.h); 
  }
 

// owned by phil
class Layout {
  private class Algorithm {
    private final int INSET_AMOUNT = 2;
    View parent;  // Parent view
    private Point realUL;  // The actual coordinate of the UL point of canvas
    private float sumOfRects;  // Sum of rects to be placed
    private float scale;  // To scale up to dimenssions of the screen
    private float vWidth;
    private float vHeight;
    // TODO: Change to boolean

    // The floats are the unscaled area
    private ArrayList<Datum> remRects;  // Rectangles to be placed
    private ArrayList<Rect> currentRects;  // Rectangles in current row
    private ArrayList<Datum> currentDatums;  // Parallel arrays with currentRects corresponding Datums
    private ArrayList<View> finalViews;  // Final view for the level -- exported by class


    public Algorithm(View parent, float vWidth, float vHeight, ArrayList<Datum> startingVals, Point realUL) {
      this.parent = parent;
      this.remRects = startingVals;
      this.currentRects = null;
      this.currentDatums = null;
      this.finalViews = new ArrayList();
      this.realUL = realUL;
      this.sumOfRects = 0;
      this.scale = 0;
      this.vWidth = vWidth;
      this.vHeight = vHeight;
    }

    private void squarify(boolean segmentsAreVertical) {
      if (remRects == null || remRects.isEmpty()) {
        return;
      }

      if (segmentsAreVertical) {
        placeVerticalSegments();
      } else {
        placeHorizontalSegments();
      }
    }


    private void placeVerticalSegments() {
      println("In placeVerticalSegments");
      sumOfRects = getSumOfRects();
      scale = vWidth * vHeight / sumOfRects;
      float rectHeight = vHeight; // Shared side
      float xCoord = realUL.x;
      float yCoord = realUL.x;

      // Place all the rectangles
      float widthUsed = 0;
      while (!remRects.isEmpty()) {
        Datum d = getLargestRemaining();
        remRects.remove(getLargestRemaining());  // Take it out of remRects
        float scaledArea = d.getValueF() * scale;
        float rectWidth = scaledArea / rectHeight;
        Rect r = new Rect(xCoord + widthUsed, yCoord, vWidth - widthUsed, vHeight);
        finalViews.add(new View(d, r));

        widthUsed += rectWidth;
      }

    }

    private void placeHorizontalSegments() {
      return;
    }




    

    // private void _squarify() {
    //   // A Base case -- no remaining rects to place
    //   if (remRects == null || remRects.isEmpty()) {
    //     return;
    //   }

    //   // Need clean list every time
    //   currentRects = new ArrayList();
    //   currentDatums = new ArrayList();

    //   sumOfRects = getSumOfRects();
    //   scale = canvShort * canvLong / sumOfRects;

    //   addFirstRect();
    //   // Rect added, so need to delete it from remaining rects
    //   remRects.remove(getIndexLargestRemaining());

    //   // Loop invariant: All rectangles already in remRects have been scaled
    //   // to fit the screen
    //   while (!remRects.isEmpty ()) {
    //     float oldWorstAR = getWorstAR(currentRects);
        
    //     // Get next rectangle
    //     Datum nodeToConsider = getLargestRemaining();
    //     float areaCurrent = nodeToConsider.getValueF() * scale;

    //     // Calculate length of shared long side
    //     float sharedLong = 0;
    //     for (int i = 0; i < currentRects.size (); i++) {
    //       sharedLong += currentRects.get(i).getArea();
    //     }

    //     sharedLong += areaCurrent;
    //     sharedLong /= canvShort;

    //     ArrayList<Rect> tempRects = new ArrayList();
    //     ArrayList<Datum> tempDatums = new ArrayList();
    //     float shortAreaUsedUp = 0;  // Used for placing the rectangles
    //     // Put in previous rectangles
    //     for (int i = 0; i < currentRects.size (); i++) {
    //       Rect cRect = currentRects.get(i);
    //       Datum toAdd = currentDatums.get(i);
    //       shortAreaUsedUp += addNewRectToTemp(tempRects, cRect.getArea(), tempDatums, toAdd, sharedLong, shortAreaUsedUp);
    //     }
    //     addNewRectToTemp(tempRects, areaCurrent, tempDatums, nodeToConsider, sharedLong, shortAreaUsedUp); 
    //     float newWorstAR = getWorstAR(tempRects);

    //     // If next rectangle makes things worse, then GTFO
    //     if (newWorstAR >= oldWorstAR) {
    //       break;
          
    //     }       
    //     // If next rectange would improve aspect ratio, add it in
    //     currentRects = tempRects;  // Yah Garbage collection
    //     currentDatums = tempDatums;
    //     // Need to remove the largest one
    //     remRects.remove(getIndexLargestRemaining());
    //   } 
    //   // Update pointUL and longSide of the canvas
    //   // Update canvas dims
    //   if (shortIsWidth) {
    //     float longShared = currentRects.get(0).h;
    //     realUL = realUL.offset(new Point(0, longShared));
    //     canvLong -= longShared;
    //   } else {
    //     float longShared = currentRects.get(0).w;
    //     realUL = realUL.offset(new Point(longShared, 0));
    //     canvLong -= longShared;
    //   }

    //   // Check if the short and long sides have swapped
    //   if  (canvLong < canvShort) {  
    //     shortIsWidth = !shortIsWidth;
    //     float temp = canvLong;
    //     canvLong = canvShort;
    //     canvShort = temp;
    //   } 

    //   // Recurse!
    //   addViews();
    //   squarify();
    // }

    // private void addViews() {
    //   for (int i = 0; i < currentRects.size (); i++) {
    //     Datum toAddDatum = currentDatums.get(i);
    //     Rect toAddRect = currentRects.get(i).inset(INSET_AMOUNT);
    //     View toAddView = new View(toAddDatum, toAddRect);
    //     finalViews.add(toAddView);
    //   }
    // }

    // Adds largest datum to currentRect and currentDatum
    // private void addFirstRect() {
    //   Datum toAdd = getLargestRemaining();
    //   float areaCurrent = toAdd.getValueF() * scale;
    //   float longCurrent = areaCurrent / canvShort;
    //   // Added extra multiplication by scale cause 2D

    //   // Short side isn't necessarily width or height, constructing rectangle changes based on this
    //   // NOTE: rectange added in is already scaled
    //   if (shortIsWidth) {
    //     currentRects.add(new Rect(realUL.x, realUL.y, canvShort, longCurrent));  
    //   } else {   // The height is the short side
    //     currentRects.add(new Rect(realUL.x, realUL.y, longCurrent, canvShort));
    //   }
    //   currentDatums.add(toAdd);
    // }

    // // Returns the worst aspect ratio in an ArayList of Rects
    // // Cann't be called on empty ArrayList  -- it will explode
    // private float getWorstAR(ArrayList<Rect> inputList) {
    //   float worstAR = inputList.get(0).getAspectRatio();

    //   for (int i = 1; i < inputList.size (); i++) {
    //     float currentAR = inputList.get(i).getAspectRatio();
    //     if (currentAR > worstAR) {
    //       worstAR = currentAR;
    //     }
    //   }

    //   return worstAR;
    // }

    // Adds in a newRect and returns the amount of the short side that was used
    // private float addNewRectToTemp(ArrayList<Rect> tempRects, float areaRect, ArrayList<Datum> tempDatums, Datum toAddDatum, float sharedLong, float shortAreaUsedUp) {
    //   // Area of rectangle has already been scaled
    //   float shortSideRect = areaRect / sharedLong;

    //   Rect newRect;
    //   if (shortIsWidth) {
    //     float xCoord = realUL.x + shortAreaUsedUp;
    //     float yCoord = realUL.y;
    //     float rectWidth = shortSideRect;
    //     float rectHeight = sharedLong;
    //     newRect = new Rect(xCoord, yCoord, rectWidth, rectHeight);
    //   } else {  // Short side is the height
    //     float xCoord = realUL.x;
    //     float yCoord = realUL.y + shortAreaUsedUp;
    //     float rectWidth = sharedLong;
    //     float rectHeight = shortSideRect;
    //     newRect = new Rect(xCoord, yCoord, rectWidth, rectHeight);
    //   }
    //   tempDatums.add(toAddDatum);

    //   tempRects.add(newRect);

    //   return shortSideRect;
    // }

    private Datum getLargestRemaining() {
      Datum max = remRects.get(0);
      Datum current = null;
      for (int i = 1; i < remRects.size (); i++) {
        current = remRects.get(i);
        if (current.getValueF() > max.getValueF()) {
          max = current;
        }
      }
      return max;
    }

    // Cannot be called on empty remRects array
    // NOTE: remRects is an ArrayList of Numbers
    private int getIndexLargestRemaining() {
      float max = remRects.get(0).getValueF();
      int maxInd = 0;

      for (int i = 1; i < remRects.size (); i++) {
        float current = remRects.get(i).getValueF();
        if (current > max) {
          max = current;
          maxInd = i;
        }
      }

      return maxInd;
    }

    private float getSumOfRects() {
      float sum = 0;
      for (int i = 0; i < remRects.size (); i++) {
        sum += remRects.get(i).getValueF();
      }
      return sum;
    }
  }

  public final Datum root;

  public Layout(Datum root) {
    this.root = root;
  }

  private ArrayList<Datum> copy(ArrayList<Datum> ds) {
    if (ds == null) {
      return null;
    }
    
    return new ArrayList<Datum>(ds);
  }

  private View recurSolve(View node, int currentLevel) {
    // Boolean shortIsWidth = node.bounds.h > node.bounds.w;
    // float canvShort = shortIsWidth ? node.bounds.w : node.bounds.h;     
    // float canvLong = shortIsWidth ? node.bounds.h : node.bounds.w;
    //This part used to assume that the width was always the long side of the view
    Algorithm a = new Algorithm(node, width, height, copy(node.datum.children), new Point(node.bounds.x, node.bounds.y));
    a.squarify(true);
    
    for (View v : a.finalViews) {
      node.subviews.add(v);
    }
    
    for (View v : node.subviews) {
      // recurSolve(v, currentLevel + 1);
    }
    
    return node;
  }
  
  public View solve() {
    View viewRoot = new View(root, new Rect(0, 0, width, height));
    if(root.children != null && !root.children.isEmpty()) {
      recurSolve(viewRoot, 0);
    }
    return viewRoot;
  }
  
  // public void printTree(View node) {
  //   print(node.datum.id);
  //   print(": ");
  //   println(node.bounds.toString());
    
  //   for (View v : node.subviews) {
  //     printTree(v); 
  //   }
  // }

  // public void testPrintNumArray(ArrayList<Number> arr) {
  //   for (int i = 0; i < arr.size (); i++) {
  //     println(arr.get(i).floatValue());
  //   }
  // }


  // Gets all children whose parents are level 
  // private ArrayList<Datum> getChildren(int level) {
  //   ArrayList<Datum> saveList = new ArrayList();
  //   recurGetChildren(root, 0, level + 1, saveList);
  //   return saveList;
  // }

  // Cannot be called on null pointer
  // 10Q: http://stackoverflow.com/questions/13349853/find-all-nodes-in-a-binary-tree-on-a-specific-level-interview-query
  // private void recurGetChildren(Datum node, int currentLev, int targetLev, ArrayList<Datum> saveList) {
  //   // Target case
  //   if (currentLev == targetLev) {
  //     saveList.add(node);
  //   }

  //   // Base case
  //   if (node.children == null) {
  //     return;
  //   }

  //   // Recursion case
  //   for (int i = 0; i < node.children.size(); i++) {
  //     recurGetChildren(node.children.get(i), currentLev + 1, targetLev, saveList);
  //   }
  // }
}




// reads a file with a given name and returns a 
// tree of Datums representing the contents
// owned by ilan
class Reader {
  
  private final String filename;
  
  public Reader(String filename) {
    this.filename = filename;
  }
 
  public Datum read() {
    String[] lines = loadStrings(filename);
    
    // parse the leaves
    int leafCount = Integer.parseInt(lines[0]);
    ArrayList<Datum> leaves = parseDatums(slice(lines, 1, leafCount));
    
    // parse parents + relationships
    int start = leafCount + 1;
    int relCount = Integer.parseInt(lines[start]);
    ArrayList<Datum> parents = parseRelationships(leaves, slice(lines, start+1, relCount));
    
    // find the parent that is the root
    Datum root = findRoot(leaves, parents);
    root.calculateValue();
    return root;
  }
  
  private ArrayList<Datum> parseRelationships(ArrayList<Datum> leaves, String[] input) {
    ArrayList<Datum> parents = new ArrayList<Datum>();
    
    for (int i = 0; i < input.length; i++) {  
      // parse the line of input
      String[] comps = split(input[i], " ");
      int id = Integer.parseInt(comps[0]);
      int childID = Integer.parseInt(comps[1]);
      
      // check if that a datum with that id already exists 
      Datum d = findDatumById(parents, id);
      if (d == null) {
        d = new Datum(id);
        parents.add(d); 
      }
      
      // add the child
      Datum child = findDatumById(childID, leaves, parents);
      if (child == null) {
        // create the child if we have not yet seen one with that id
        child = new Datum(childID);
        parents.add(child);
      }
      d.children.add(child);
    }
    
    return parents;
  }
  
  private Datum findDatumById(int id, ArrayList<Datum>... dss) {
    for (ArrayList<Datum> ds : dss) {
      Datum d = findDatumById(ds, id);
      if (d != null) {
        return d;
      } 
    }
    
    println("WARNING: could not find the datum w/ id = " + id);
    return null;
  }
  
  private Datum findDatumById(ArrayList<Datum> ds, int id) {
    for (int i = 0; i < ds.size(); i++) {
      Datum d = ds.get(i);
      
      if (d.id == id) {
        return d;
      }
    } 
    
    return null;
  }
  
  private ArrayList<Datum> parseDatums(String[] input) {
    ArrayList<Datum> ds = new ArrayList<Datum>();
    
    for (int i = 0; i < input.length; i++) {
      String[] comps = split(input[i], " ");
      int id = Integer.parseInt(comps[0]);
      int value = Integer.parseInt(comps[1]);
      
      ds.add(new Datum(id, value));
    }
    
    return ds;
  }
  
  // the root is the one datum that does not appear as a child of any other node
  private Datum findRoot(ArrayList<Datum> leaves, ArrayList<Datum> parents) {    
    HashSet<Integer> childIds = new HashSet<Integer>(getChildIds(parents));
     
    HashSet<Integer> allIds = new HashSet<Integer>();
    allIds.addAll(getIds(leaves));
    allIds.addAll(getIds(parents));
     
    // set difference: all - children => ID of the (only) root
    allIds.removeAll(childIds);
     
    if (allIds.size() == 1) {
       int id = allIds.toArray(new Integer[0])[0];   // that was gross.
       return findDatumById(parents, id);
    }
    
    return null;
  }
  
  private ArrayList<Integer> getIds(ArrayList<Datum> ds) {
    ArrayList<Integer> ids = new ArrayList<Integer>();
    
    for (int i = 0; i < ds.size(); i++) {
      ids.add(ds.get(i).id); 
    }
    
    return ids;
  }
  
  private ArrayList<Integer> getChildIds(ArrayList<Datum> ds) {
    ArrayList<Integer> ids = new ArrayList<Integer>();
    
    for (int i = 0; i < ds.size(); i++) {
      ids.addAll(getIds(ds.get(i).children)); 
    }
    
    return ids;
  }
  
  private String[] slice(String[] list, int start, int count) {
    String[] ss = new String[count];
    for (int i = 0; i < count; i++) {
      ss[i] = list[start + i];
    } 
    return ss;
  }  
}

// owned by ben
// displays a squarified tree map
class SQTM {
  
  private final Rect bounds; 
//  private final Datum root;
  
  // holds the views that we zoomed through
  private final Stack<Datum> zoomOutStack;
  
  // the current root view being display. takes up the whole bounds
  private View currentView;
  private Datum currentDatum;

  public SQTM(Rect bounds, Datum root) {
    this.bounds = bounds;
//    this.root = root;
    this.currentDatum = root;
    this.zoomOutStack = new Stack<Datum>();
    //println("datum after layout" + current.datum);
  }
  
  // p determines which rectangle to zoom in on
  // NOTE: what happens if p is not inside the bounds of the receiving SQTM
  public void zoomIn(Point p) {
    return;
    // View temp = currentView.viewSelected(p);
    // if(temp != null){
    //   zoomOutStack.push(currentDatum);
    //   currentDatum = temp.datum;
    // }
  }
   
  public void zoomOut() {
    if(!zoomOutStack.isEmpty()) {
      currentDatum = zoomOutStack.pop();
    }
  } 
  
  // calls render on the root view
  public void render() {
   currentView = new Layout(currentDatum).solve();
   currentView.render();
  }
}


// A stack!
// owned by ilan
class Stack<T> {
  
  private final ArrayList<T> elements;
  
  public Stack() {
    elements = new ArrayList<T>();
  }
  
  // adds a new element
  public void push(T element) {
    elements.add(element);
  }
  
  // returns the top element of the stack and removes it. returns null if empty
  public T pop() {
    if (isEmpty()) {
      return null;
    }
    
    T top = top();
    elements.remove(lastIndex());
    return top;
  }
  
  // returns the top element of the stack without removing it. returns null if empty
  public T top() {
    return isEmpty()? null : elements.get(lastIndex());
  }
 
  // returns the index of the last elements in the underlying arraylist
  private int lastIndex() {
    return elements.size() - 1;
  }
  
  public boolean isEmpty() {
    return elements.isEmpty();  
  }
}

// owned by ben
class View {
  
  private final int STROKE_COLOR = color(0, 0, 0);
  private final int REGULAR_FILL = color(255, 255, 255);
  private final int HIGHLIGHTED_FILL = color(0, 0, 255);
  private final Datum datum;
  private final Rect bounds;
  private final ArrayList<View> subviews;
  
  
  public View(Datum datum, Rect bounds) {
    this.datum = datum;
    this.bounds = bounds;
    this.subviews = new ArrayList();
  }
  
 
  // rendering a view also renders all subviews
  // bounds for subviews must already be specified
 public void render() {
    if(datum.isLeaf) {
      int viewFill = bounds.containsPoint(mouseX, mouseY) ? HIGHLIGHTED_FILL : REGULAR_FILL;
      drawRect(bounds, STROKE_COLOR, viewFill);
      textAlign(CENTER, CENTER);
      fill(color(0, 0, 0));
      text(datum.id, bounds.x + bounds.w / 2, bounds.y + bounds.h / 2 );
    } else {
    
       for (View subview : subviews) {
         subview.render(); 
       }
       strokeRect(bounds, STROKE_COLOR);
    }
  } 
  
  // returns the view that should be zoomed in on for a click at point p, 
  // or null if none exists

  public View viewSelected(Point p) {
    if(!bounds.containsPoint(p.x, p.y)) {
      return null;
    }
      
    for(int i = 0; i < subviews.size(); i ++) {
      if(subviews.get(i).bounds.containsPoint(p.x, p.y)) {
        return subviews.get(i);
      }
    }
    return null; //it should never get here, but just in case
  }
  
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "linetrum" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
