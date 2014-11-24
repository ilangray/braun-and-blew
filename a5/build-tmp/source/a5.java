import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import controlP5.*; 

import controlP5.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class a5 extends PApplet {


final int NUM_TIMES_TO_RUN = 15;
final int DECIDE_YOURSELF = -1; // This is a placeholder for variables you will replace.

/**
 * This is a global variable for the dataset in your visualization. You'll be overwriting it each trial.
 */
Data d = null;
int chartType = 5;

int NUMBER_OF_CHARTS = 2;

public void getNextChart() {
    d = new Data();
    chartType = (int)random(100) < 50 ? 1 : 0;
    println("Chart type = " + chartType);
}

public ArrayList<Datum> getMarkedDatums() {
  ArrayList<Datum> realDatums = getDatumFromData(d.data);
   ArrayList<Datum> marked = new ArrayList<Datum>();
   
   for (Datum d : realDatums) {
     if (d.marked) {
       marked.add(d);
     } 
   }
   
   return marked;
}

public float calculateRealPercent() {
  ArrayList<Datum> marked = getMarkedDatums();
  
  Datum d1 = marked.get(0);
  Datum d2 = marked.get(1);
  
  Datum small = d1.value < d2.value ? d1 : d2;
  Datum large = d1.value < d2.value ? d2 : d1;
  
  return small.value / large.value;
}

public void setup() {
    totalWidth = displayWidth;
    totalHeight = displayHeight;
    chartLeftX = totalWidth / 2.0f - chartSize / 2.0f;
    chartLeftY = totalHeight / 2.0f - chartSize / 2.0f - margin_top;

    size((int) totalWidth, (int) totalHeight);
    //if you have a Retina display, use the line below (looks better)
    //size((int) totalWidth, (int) totalHeight, "processing.core.PGraphicsRetina2D");

    background(255);
    frame.setTitle("Comp150-07 Visualization, Lab 5, Experiment");

    cp5 = new ControlP5(this);
    pfont = createFont("arial", fontSize, true); 
    textFont(pfont);
    page1 = true;

    /**
     ** Finish this: decide how to generate the dataset you are using (see DataGenerator)
     **/
    getNextChart();

    /**
     ** Finish this: how to generate participant IDs
     ** You can write a short alphanumeric ID generator (cool) or modify this for each participant (less cool).
     **/
    partipantID = 7;
}

public ArrayList<Datum> getDatumFromData(Data.DataPoint[] input) {
  ArrayList<Datum> toReturn = new ArrayList();
  
  for (int i = 0; i < NUM; i++) {
    toReturn.add(new Datum(input[i]));
  }
  
  return toReturn; 
}

public void draw() {
    textSize(fontSize);
    
    // figure out where the graph should be
    Rect bounds = new Rect(chartLeftX, chartLeftY, chartSize, chartSize);
  
    // what are we displaying?
    ArrayList<Datum> realDatums = getDatumFromData(d.data);
    SQTMDatum root = makeSQTMDatums(realDatums);
    
    /**
     ** add more: you may need to draw more stuff on your screen
     **/
    if (index < 0 && page1) {
        drawIntro();
        page1 = false;
    } else if (index >= 0 && index < NUM_TIMES_TO_RUN) {
        if (index == 0 && page2) {
            clearIntro();
            drawTextField();
            drawInstruction();
            page2 = false;
        }
      
        fill(color(255,255,255));
        stroke(color(255,255,255));
        rect(chartLeftX, chartLeftY, chartSize, chartSize);

        switch (chartType) {
            case 0:
                TM tm = new TM(bounds, root);
                tm.render();
                break;
            case 1:
                SQTM sqtm = new SQTM(bounds, root);
                sqtm.render();
                break;
            default:
                println("YOU FUCKED UP. type = " + chartType);
                break;
        }

        drawWarning();

    } else if (index > NUM_TIMES_TO_RUN - 1 && pagelast) {
        drawThanks();
        drawClose();
        pagelast = false;
    }
}

/**
 * This method is called when the participant clicked the "NEXT" button.
 */
public void next() {
    String str = cp5.get(Textfield.class, "answer").getText().trim();
    float num = parseFloat(str);
    /*
     * We check their percentage input for you.
     */
    if (!(num >= 0)) {
        warning = "Please input a number!";
        if (num < 0) {
            warning = "Please input a non-negative number!";
        }
    } else if (num > 100) {
        warning = "Please input a number between 0 - 100!";
    } else {
        if (index >= 0 && index < NUM_TIMES_TO_RUN) {
            float ans = parseFloat(cp5.get(Textfield.class, "answer").getText());

            /**
             ** Finish this: decide how to compute the right answer
             **/
            truePerc = calculateRealPercent(); // hint: from your list of DataPoints, extract the two marked ones to calculate the "true" percentage

            reportPerc = ans / 100.0f; // this is the participant's response
            
            println("true percentage = " + truePerc);
            
            /**
             ** Finish this: decide how to compute the log error from Cleveland and McGill (see the handout for details)
             **/
            error = log2(Math.abs(reportPerc - truePerc) + 1/8.0f) * 100;

            saveJudgement();
        }

        /**
         ** Finish this: decide the dataset (similar to how you did in setup())
         **/
        getNextChart();

        cp5.get(Textfield.class, "answer").clear();
        index++;

        if (index == NUM_TIMES_TO_RUN - 1) {
            pagelast = true;
        }
    }
}

public float log2(double f) {
  return (float)Math.log(f) / (float)Math.log(2); 
}

/**
 * This method is called when the participant clicked "CLOSE" button on the "Thanks" page.
 */
public void close() {
    /**
     ** Change this if you need to do some final processing
     **/
    saveExpData();
    exit();
}

/**
 * Calling this method will set everything to the intro page. Use this if you want to run multiple participants without closing Processing (cool!). Make sure you don't overwrite your data.
 */
public void reset(){
    /**
     ** Finish/Use/Change this method if you need 
     **/
    partipantID = 7;
    getNextChart();

    /**
     ** Don't worry about the code below
     **/
    background(255);
    cp5.get("close").remove();
    page1 = true;
    page2 = false;
    pagelast = false;
    index = -1;
}
class Data {
    class DataPoint {
        private float value = -1;
        private boolean marked = false;

        DataPoint(float f, boolean m) {
            this.value = f;
            this.marked = m;
        }

        public boolean isMarked() {
            return marked;
        }

        public void setMark(boolean b) {
            this.marked = b;
        }

        public float getValue() {
            return this.value;
        }
    }

    private DataPoint[] data = null;

    Data() {
        // NUM is a global varibale in support.pde
        data = new DataPoint[NUM];
        
        for (int i = 0; i < NUM; i++) {
          data[i] = new DataPoint(random(101), false);
        }
        
        // Pick marked
        int firstInd = (int)random(NUM);
        int secondInd;
        data[firstInd].marked = true;
        
        do {
          secondInd = (int)random(10);
        }
        while (secondInd == firstInd);
        
        data[secondInd].marked = true;
    }
}

class Datum {
  public final String key = "";
  public final float value;
  public final boolean marked;
  public final int fillColor = color(0, 255, 255);

  public Datum(Data.DataPoint dp) {
    this.value = dp.value; 
    marked = dp.marked;
  }
}


// marker interface for shapes
public interface Shape {}

// This is a point
class Point {
  public final float x;
  public final float y;
  
  public Point(float x, float y) {
    this.x = x;
    this.y = y;
  }
  
  public Point offset(Point other) {
    return new Point(other.x + x, other.y + y);
  }
  
  public Point lerpBetween(Point other, float percent) {
    return new Point(lerp(x, other.x, percent), lerp(y, other.y, percent));
  }
  
  public float distFrom(Point other) {
    float dx = dx(other);
    float dy = dy(other);
    
    return sqrt(dx*dx + dy*dy);
  }
  
  public float dx(Point other) {
    return other.x - x;
  }
  
  public float dy(Point other) {
    return other.y - y;
  }
  
  public float angleBetween(Point other) {
    float dx = dx(other);
    float dy = dy(other);
   
    return atan2(dy, dx); 
  }
  
  public String toString() {
    return "Point{x = " + x + ", y = " + y + "}"; 
  }
}

// Represents a shape formed by connecting a list of points with straight lines
class Path implements Shape {
  
  public static final int NUM_PTS = 100;
  
  // FANCY EDGE comes FIRST in the array, and the points go CLOCKWISE
  public final ArrayList<Point> points = new ArrayList<Point>();
  
  private Path(ArrayList<Point> pts) {
    points.addAll(pts);
  }
  
  // if interpolateLeft is true, then the fancy side is on the left, else on right.
  public Path(Rect r, boolean interpolateLeft) {
    if (interpolateLeft) {
      // fancy edge is LEFT
      Point start = r.getUL();
      Point end = r.getLL();
      
      // NB: start is added when i = 0
      for (int i = 0; i < NUM_PTS; i++) {
         points.add(start.lerpBetween(end, 1.0f/NUM_PTS * i));
      }
      
      points.add(end);
      points.add(r.getLR());
      points.add(r.getUR());
    } else {
      // fancy edge is RIGHT
      Point start = r.getLR();
      Point end = r.getUR();
      
      // NB: start is added when i = 0
      for (int i = 0; i < NUM_PTS; i++) {
         points.add(start.lerpBetween(end, 1.0f/NUM_PTS * i));
      }
      
      // add the other three corners
      points.add(end);
      points.add(r.getUL());
      points.add(r.getLL());
    }
  }
  
  public Path(Wedge w) {
    // fancy edge is the ROUNDED PART
    points.addAll(w.lerpRoundedEdge(NUM_PTS));
    points.add(w.center);
    points.add(w.center);
  }
  
  public Path lerpBetween(Path other, float percent) {
    ArrayList<Point> lerped = new ArrayList<Point>(points.size());
    
    for (int i = 0; i < points.size(); i++) {
      lerped.add(points.get(i).lerpBetween(other.points.get(i), percent)); 
    }
    
    return new Path(lerped);
  }
}

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

class Rect implements Shape {
  
  public final float x, y, w, h;
  
  public Rect(float x, float y, float w, float h) {
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
  }
  
  public Rect(Point ul, Point lr) {
    this(ul.x, ul.y, lr.x - ul.x, lr.y - ul.y);
  }

  public String toString() {
    return "x=" + x + ", y=" + y + ", w=" + w + ", h=" + h; 
  }
  
  public float getAspectRatio() {
    float widthOHeight = w / h;
    float heightOWidth = h / w;
    
    return (widthOHeight > heightOWidth ? widthOHeight : heightOWidth);
  }
  
  public float getArea() {
    return w * h;
  }
  
  // maintains previous center while scaling
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
  
  public Point getCenter() {
    return new Point(x + w/2, y + h/2);    
  }
  
  public float getMaxX() {
    return x + w;
  }
  
  public float getMaxY() {
    return y + h;
  }
  
  public float getMinY() {
    return y;
  }
  
  public float getMinX() {
    return x; 
  }
  
  public Point getUL() {
    return new Point(getMinX(), getMinY());
  }
  
  public Point getLL() {
    return new Point(getMinX(), getMaxY());
  }
  
  public Point getUR() {
    return new Point(getMaxX(), getMinY());
  }
  
  public Point getLR() {
    return new Point(getMaxX(), getMaxY());
  }
}

public void drawLine(Point p, Point q) { 
  line(p.x, p.y, q.x, q.y);
}

public void drawPath(Path path, int stroke, int fill) {
  fill(fill);
  stroke(stroke);
  
  beginShape();
  
  for (int i = 0; i < path.points.size(); i++) {
    Point p = path.points.get(i);
    vertex(p.x, p.y);
  }
  
  endShape(CLOSE);
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

public float clamp(float val, float min, float max) {
  if (val < min) {
    return min;
  }
  if (val > max) {
    return max;
  } 
  
  return val;
}
 
<T> ArrayList<T> makeList(T... values) {
  ArrayList<T> ts = new ArrayList<T>();
  
  for (T v : values) {
    ts.add(v); 
  }
  
  return ts; 
}

// owned by phil
class Layout {
  private class Algorithm {
    private final int INSET_AMOUNT = 0;
    View parent;  // Parent view
    private Point realUL;  // The actual coordinate of the UL point of canvas
    private float canvLong;  // Length of long side of canvas
    private float canvShort;  // Length of short side of canvas
    private float sumOfRects;  // Sum of rects to be placed
    private float scale;  // To scale up to dimenssions of the screen
    private boolean shortIsWidth;  // Which side is the short side?

    // The floats are the unscaled area
    private ArrayList<SQTMDatum> remRects;  // Rectangles to be placed
    private ArrayList<Rect> currentRects;  // Rectangles in current row
    private ArrayList<SQTMDatum> currentDatums;  // Parallel arrays with currentRects corresponding Datums
    private ArrayList<View> finalViews;  // Final view for the level -- exported by class

    public Algorithm(View parent, float canvLong, float canvShort, ArrayList<SQTMDatum> startingVals, Point realUL, boolean shortIsWidth) {
      this.parent = parent;
      this.canvLong = canvLong;
      this.canvShort = canvShort;
      this.remRects = startingVals;
      this.currentRects = null;
      this.currentDatums = null;
      this.finalViews = new ArrayList();
      this.realUL = realUL;
      this.sumOfRects = 0;
      this.scale = 0;
      this.shortIsWidth = shortIsWidth;
    }

    private void squarify() {
      // A Base case -- no remaining rects to place
      if (remRects == null || remRects.isEmpty()) {
        return;
      }

      // Need clean list every time
      currentRects = new ArrayList();
      currentDatums = new ArrayList();

      sumOfRects = getSumOfRects();
      scale = canvShort * canvLong / sumOfRects;

      addFirstRect();
      // Rect added, so need to delete it from remaining rects
      remRects.remove(getIndexLargestRemaining());

      // Loop invariant: All rectangles already in remRects have been scaled
      // to fit the screen
      while (!remRects.isEmpty ()) {
        float oldWorstAR = getWorstAR(currentRects);
        
        // Get next rectangle
        SQTMDatum nodeToConsider = getLargestRemaining();
        float areaCurrent = nodeToConsider.getValueF() * scale;

        // Calculate length of shared long side
        float sharedLong = 0;
        for (int i = 0; i < currentRects.size (); i++) {
          sharedLong += currentRects.get(i).getArea();
        }

        sharedLong += areaCurrent;
        sharedLong /= canvShort;
        
        ArrayList<Rect> tempRects = new ArrayList();
        ArrayList<SQTMDatum> tempDatums = new ArrayList();
        float shortAreaUsedUp = 0;  // Used for placing the rectangles
        // Put in previous rectangles
        for (int i = 0; i < currentRects.size (); i++) {
          Rect cRect = currentRects.get(i);
          SQTMDatum toAdd = currentDatums.get(i);
          shortAreaUsedUp += addNewRectToTemp(tempRects, cRect.getArea(), tempDatums, toAdd, sharedLong, shortAreaUsedUp);
        }
        addNewRectToTemp(tempRects, areaCurrent, tempDatums, nodeToConsider, sharedLong, shortAreaUsedUp); 
        float newWorstAR = getWorstAR(tempRects);

        // If next rectangle makes things worse, then GTFO
        if (newWorstAR >= oldWorstAR) {
          break;
          
        }       
        // If next rectange would improve aspect ratio, add it in
        currentRects = tempRects;  // Yah Garbage collection
        currentDatums = tempDatums;
        // Need to remove the largest one
        remRects.remove(getIndexLargestRemaining());
      } 
      // Update pointUL and longSide of the canvas
      // Update canvas dims
      if (shortIsWidth) {
        float longShared = currentRects.get(0).h;
        realUL = realUL.offset(new Point(0, longShared));
        canvLong -= longShared;
      } else {
        float longShared = currentRects.get(0).w;
        realUL = realUL.offset(new Point(longShared, 0));
        canvLong -= longShared;
      }

      // Check if the short and long sides have swapped
      if  (canvLong < canvShort) {  
        shortIsWidth = !shortIsWidth;
        float temp = canvLong;
        canvLong = canvShort;
        canvShort = temp;
      } 
      
      // Recurse!
      addViews();
      squarify();
    }

    private void addViews() {
      for (int i = 0; i < currentRects.size (); i++) {
        SQTMDatum toAddDatum = currentDatums.get(i);
        Rect toAddRect = currentRects.get(i).inset(INSET_AMOUNT);
        View toAddView = new View(toAddDatum, toAddRect);
        finalViews.add(toAddView);
      }
    }

    // Adds largest datum to currentRect and currentDatum
    private void addFirstRect() {
      SQTMDatum toAdd = getLargestRemaining();
      float areaCurrent = toAdd.getValueF() * scale;
      float longCurrent = areaCurrent / canvShort;
      // Added extra multiplication by scale cause 2D

      // Short side isn't necessarily width or height, constructing rectangle changes based on this
      // NOTE: rectange added in is already scaled
      if (shortIsWidth) {
        currentRects.add(new Rect(realUL.x, realUL.y, canvShort, longCurrent));  
      } else {   // The height is the short side
        currentRects.add(new Rect(realUL.x, realUL.y, longCurrent, canvShort));
      }
      currentDatums.add(toAdd);
    }

    // Returns the worst aspect ratio in an ArayList of Rects
    // Cann't be called on empty ArrayList  -- it will explode
    private float getWorstAR(ArrayList<Rect> inputList) {
      float worstAR = inputList.get(0).getAspectRatio();

      for (int i = 1; i < inputList.size (); i++) {
        float currentAR = inputList.get(i).getAspectRatio();
        if (currentAR > worstAR) {
          worstAR = currentAR;
        }
      }

      return worstAR;
    }

    // Adds in a newRect and returns the amount of the short side that was used
    private float addNewRectToTemp(ArrayList<Rect> tempRects, float areaRect, ArrayList<SQTMDatum> tempDatums, SQTMDatum toAddDatum, float sharedLong, float shortAreaUsedUp) {
      // Area of rectangle has already been scaled
      float shortSideRect = areaRect / sharedLong;

      Rect newRect;
      if (shortIsWidth) {
        float xCoord = realUL.x + shortAreaUsedUp;
        float yCoord = realUL.y;
        float rectWidth = shortSideRect;
        float rectHeight = sharedLong;
        newRect = new Rect(xCoord, yCoord, rectWidth, rectHeight);
      } else {  // Short side is the height
        float xCoord = realUL.x;
        float yCoord = realUL.y + shortAreaUsedUp;
        float rectWidth = sharedLong;
        float rectHeight = shortSideRect;
        newRect = new Rect(xCoord, yCoord, rectWidth, rectHeight);
      }
      tempDatums.add(toAddDatum);

      tempRects.add(newRect);

      return shortSideRect;
    }

    private SQTMDatum getLargestRemaining() {
      SQTMDatum max = remRects.get(0);
      SQTMDatum current = null;
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

  public final SQTMDatum root;
  public final Rect bounds;

  public Layout(SQTMDatum root, Rect bounds) {
    this.root = root;
    this.bounds = bounds;
  }

  private ArrayList<SQTMDatum> copy(ArrayList<SQTMDatum> ds) {
    if (ds == null) {
      return null;
    }
    
    return new ArrayList<SQTMDatum>(ds);
  }

  private View recurSolve(View node) {
    boolean shortIsWidth = node.bounds.h > node.bounds.w;
    float canvShort = shortIsWidth ? node.bounds.w : node.bounds.h;     
    float canvLong = shortIsWidth ? node.bounds.h : node.bounds.w;
    //This part used to assume that the width was always the long side of the view
    Algorithm a = new Algorithm(node, canvLong, canvShort, copy(node.datum.children), new Point(node.bounds.x, node.bounds.y), shortIsWidth);
    a.squarify();
    
    for (View v : a.finalViews) {
      node.subviews.add(v);
    }
    
    for (View v : node.subviews) {
      recurSolve(v);
    }
    
    return node;
  }
  
  public View solve() {
    View viewRoot = new View(root, new Rect(bounds.x, bounds.y, bounds.w, bounds.h));
    if(root.children != null && !root.children.isEmpty()) {
      recurSolve(viewRoot);
    }
    return viewRoot;
  }
  
  public void printTree(View node) {
    print(node.datum.id);
    print(": ");
    println(node.bounds.toString());
    
    for (View v : node.subviews) {
      printTree(v); 
    }
  }

  public void testPrintNumArray(ArrayList<Number> arr) {
    for (int i = 0; i < arr.size (); i++) {
      println(arr.get(i).floatValue());
    }
  }
}


// owned by phil
class RLayout {
  private class Algorithm {
    private final int INSET_AMOUNT = 2;
    View parent;  // Parent view
    private Point realUL;  // The actual coordinate of the UL point of canvas
    private float sumOfRects;  // Sum of rects to be placed
    private float scale;  // To scale up to dimenssions of the screen
    private float vWidth;
    private float vHeight;

    // The floats are the unscaled area
    private ArrayList<SQTMDatum> remRects;  // Rectangles to be placed
    private ArrayList<Rect> currentRects;  // Rectangles in current row
    private ArrayList<SQTMDatum> currentDatums;  // Parallel arrays with currentRects corresponding Datums
    private ArrayList<View> finalViews;  // Final view for the level -- exported by class


    public Algorithm(View parent, float vWidth, float vHeight, ArrayList<SQTMDatum> startingVals, Point realUL) {
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
      sumOfRects = getSumOfRects();
      scale = vWidth * vHeight / sumOfRects;
      float rectHeight = vHeight; // Shared side
      float xCoord = realUL.x;
      float yCoord = realUL.y;

      // Place all the rectangles
      float widthUsed = 0;
      while (!remRects.isEmpty()) {
        SQTMDatum d = getLargestRemaining();
        remRects.remove(getLargestRemaining());  // Take it out of remRects
        float scaledArea = d.value * scale;
        float rectWidth = scaledArea / rectHeight;
        Rect r = new Rect(xCoord + widthUsed, yCoord, vWidth - widthUsed, vHeight);
        finalViews.add(new View(d, r));

        widthUsed += rectWidth;
      }

    }

    private void placeHorizontalSegments() {
      sumOfRects = getSumOfRects();
      scale = vWidth * vHeight / sumOfRects;

      float rectWidth = vWidth;
      float xCoord = realUL.x;
      float yCoord = realUL.y;

      float heightUsed = 0;

      while (!remRects.isEmpty()) {
        SQTMDatum d = getLargestRemaining();
        remRects.remove(getLargestRemaining());
        float scaledArea = d.value * scale;
        float rectHeight = scaledArea / rectWidth;

        Rect r = new Rect(xCoord, yCoord + heightUsed, vWidth, vHeight - heightUsed);

        finalViews.add(new View(d, r));

        heightUsed += rectHeight;
      }
    }

    private SQTMDatum getLargestRemaining() {
      SQTMDatum max = remRects.get(0);
      SQTMDatum current = null;
      for (int i = 1; i < remRects.size (); i++) {
        current = remRects.get(i);
        if (current.value > max.value) {
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

  public final SQTMDatum root;
  public final Rect bounds;

  public RLayout(SQTMDatum root, Rect bounds) {
    this.root = root;
    this.bounds = bounds;
  }

  private ArrayList<SQTMDatum> copy(ArrayList<SQTMDatum> ds) {
    if (ds == null) {
      return null;
    }
    
    return new ArrayList<SQTMDatum>(ds);
  }

  private View recurSolve(View node, boolean segmentsAreVertical) {
    // Boolean shortIsWidth = node.bounds.h > node.bounds.w;
    float vWidth = node.bounds.w;
    float vHeight = node.bounds.h;
    //This part used to assume that the width was always the long side of the view
    Algorithm a = new Algorithm(node, vWidth, vHeight, copy(node.datum.children), new Point(node.bounds.x, node.bounds.y));
    a.squarify(segmentsAreVertical);
    
    for (View v : a.finalViews) {
      node.subviews.add(v);
    }
    
    for (View v : node.subviews) {
      recurSolve(v, !segmentsAreVertical);
    }
    
    return node;
  }
  
  public View solve() {
    View viewRoot = new View(root, bounds);
    if(root.children != null && !root.children.isEmpty()) {
      recurSolve(viewRoot, true);  // Segments start vertical
    }
    return viewRoot;
  }
}


// owned by ben
// displays a squarified tree map
class SQTM {
  
  private Rect bounds; 
//  private final Datum root;

  // the current root view being display. takes up the whole bounds
  private View currentView;
  private SQTMDatum currentDatum;

  public SQTM(Rect bounds, SQTMDatum root) {
    this.bounds = bounds;
    this.currentDatum = root;
  }
  
  public void setBounds(Rect newBounds) {
    this.bounds = newBounds;
  }
  
  // calls render on the root view
  public void render() {
   currentView = new Layout(currentDatum, bounds).solve();
   currentView.render(levelsToRender(), 0);
  }
  
  private int levelsToRender() {
    float percentHeight = bounds.h / height;
    
    if (percentHeight < .05f) {
      return 0;
    }
    return 2;
  }
}


public SQTMDatum makeSQTMDatums(ArrayList<Datum> ds) {
  // wrap each d in a SQTMD
  ArrayList<SQTMDatum> sqds = makeList();
  for (Datum d : ds) {
    SQTMDatum sd = new SQTMDatum(d.key, d.value, d.marked);
    sqds.add(sd); 
  }
  
  // make a root and return it
  SQTMDatum root = new SQTMDatum("THE ROOOOOOT");
  root.children.addAll(sqds);
  root.calculateValue();
  return root;
}

// owned by ilan
class SQTMDatum {
  
  public final static int INVALID_VALUE = -1;
  
  public final String id;
  public final ArrayList<SQTMDatum> children;
  public final boolean isLeaf;
  
  // the actual data of the SQTMDatum
  public boolean marked;
  public float value;
  
  /**
   * Creates a new leaf datum with the given id and value. children will be null
   */
  public SQTMDatum(String id, float value, boolean marked) {
    this(id, value, true, marked);
  }
  
  /**
   * Creates a new NON-leaf datum with the given id. Children can be
   * added by accessing and mutating the list of children.
   */
  public SQTMDatum(String id) {
    this(id, INVALID_VALUE, false, false);
  }
  
  private SQTMDatum(String id, float value, boolean isLeaf, boolean marked) {
    this.id = id;
    this.value = value;
    this.isLeaf = isLeaf;
    this.marked = marked;
   
    if (isLeaf) {
      this.children = null;
    } else {
      this.children = new ArrayList<SQTMDatum>();
    } 
  }
  
  public SQTMDatum getAnyLeaf() {
    if (isLeaf) {
      return this;
    }
    return children.get(0).getAnyLeaf();
  }
  
  public float calculateValue() {
     if (value != INVALID_VALUE) {
       return value;
     }
     
     int sum = 0;
     for (SQTMDatum d : children) {
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
   
    for (SQTMDatum d : children) {
      d.print();
    } 
  }
  
  // legacy
  public float getValueF() {
    return value;
  }
  
  public String toString() {
    return "SQTMDatum{id = " + id + ", value = " + value + ", isLeaf = " + isLeaf + ", kids = " + children + "}"; 
  }
}

// owned by ben
// displays a squarified tree map
class TM {
  
  private final Rect bounds; 
//  private final Datum root;
  
  // the current root view being display. takes up the whole bounds
  private View currentView;
  private SQTMDatum currentDatum;

  public TM(Rect bounds, SQTMDatum root) {
    this.bounds = bounds;
    this.currentDatum = root;
  }
  
  // calls render on the root view
  public void render() {
   currentView = new RLayout(currentDatum, bounds).solve();
   currentView.render(1, 0);
  }
}


// owned by ben
class View {
  
  private final int STROKE_COLOR = color(0, 0, 0);
  private final int REGULAR_FILL = color(255, 255, 255);
  private final int HIGHLIGHTED_FILL = color(0, 0, 255);
  private final SQTMDatum datum;
  private final Rect bounds;
  private final ArrayList<View> subviews;
  
  
  public View(SQTMDatum datum, Rect bounds) {
    this.datum = datum;
    this.bounds = bounds;
    this.subviews = new ArrayList();
  }
  
 
  // rendering a view also renders all subviews
  // bounds for subviews must already be specified
 public void render(int targetLevel, int currentLevel) {
   
    if (datum.isLeaf) {
      doRender();
    } else {
      for (View subview : subviews) {
        subview.render(targetLevel, currentLevel + 1); 
      }
      strokeRect(bounds, STROKE_COLOR);
    }
   
  } 
  
  private void doRender() {

    int viewFill = datum.marked ? HIGHLIGHTED_FILL : REGULAR_FILL;
    drawRect(bounds, STROKE_COLOR, viewFill);
    textAlign(CENTER, CENTER);
    fill(datum.marked ? color(255, 255, 255) : color(0, 0, 0));
    
    if(datum.isLeaf) {
      text(datum.id, bounds.x + bounds.w / 2, bounds.y + bounds.h / 2 );
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
/**
 * These five variables are the data you need to collect from participants.
 */
int partipantID = -1;
int index = -1;
float error = -1;
float truePerc = -1;
float reportPerc = -1;

/**
 * The table saves information for each judgement as a row.
 */
Table expData = null;

/**
 * The visualizations you need to plug in.
 * You can change the name, order, and number of elements in this array.
 */

String[] vis = {
    "BarChart", "PieChart", "StackedBarChart", "TreeMap", "LineChart"
};

/**
 * add the data for this judgement from the participant to the table.
 */ 
public void saveJudgement() {
    if (expData == null) {
        expData = new Table();
        expData.addColumn("PartipantID");
        expData.addColumn("Index");
        expData.addColumn("Chart_Type");
        expData.addColumn("Vis");
        expData.addColumn("VisID");
        expData.addColumn("Error");
        expData.addColumn("TruePerc");
        expData.addColumn("ReportPerc");
    }

    TableRow newRow = expData.addRow();
    newRow.setInt("PartipantID", partipantID);
    newRow.setInt("Index", index);
    newRow.setInt("Chart_Type", chartType);

    /**
     ** finish this: decide the current visualization
     **/
    newRow.setString("Vis", "" + DECIDE_YOURSELF);

    /**
     ** finish this: decide current vis id
     **/
    newRow.setInt("VisID", DECIDE_YOURSELF);
    newRow.setFloat("Error", error);
    newRow.setFloat("TruePerc", truePerc);
    newRow.setFloat("ReportPerc", reportPerc);
}

/**
 * Save the table
 * This method is called when the participant reaches the "Thanks" page and hit the "CLOSE" button.
 */
public void saveExpData() {
    /**
     ** Change this if you need 
     **/
    saveTable(expData, "expData-" + millis() + ".csv");
}
/********************************************************************************************/
/********************************************************************************************/
/********************************************************************************************/
/************************ Don't worry about the code in this file ***************************/
/********************************************************************************************/
/********************************************************************************************/
/********************************************************************************************/

float margin = 50, margin_small = 20, margin_top = 40, chartSize = 300, answerHeight = 100;
float totalWidth = -1, totalHeight = -1;
float chartLeftX = -1, chartLeftY = -1;
int NUM = 10;

int fontSize = 14, fontSizeBig = 20;
int textFieldWidth = 200, textFieldHeight = 30;
int buttonWidth = 60;
int totalMenuWidth = textFieldWidth + buttonWidth + (int) margin_small;

String warning = null;

ControlP5 cp5 = null;
Textarea myTextarea = null;
PFont pfont = null; 
boolean page1 = false, page2 = false, pagelast = false;

public void drawWarning() {
    fill(255);
    noStroke();
    rectMode(CORNER);
    rect(0, totalHeight / 2.0f + chartSize, totalWidth, fontSize * 3);
    if (warning != null) {
        fill(color(255, 0, 0));
        textSize(fontSize);
        textAlign(LEFT);
        text(warning, totalWidth / 2.0f - chartSize / 2.0f, 
        totalHeight / 2.0f + chartSize + fontSize * 1.5f);
    }
}

public void drawInstruction() {
    fill(0);
    textAlign(CENTER);
    textSize(fontSize);
    text("Two values are hi-lighted. \n " 
      + "What percentage is the smaller of the larger? \n" 
      + "Please put your answer below. \n" 
      + "e.g. If you think the smaller is exactly a half of the larger, \n" 
      + "please input \"50\"."
      , totalWidth / 2.0f, totalHeight / 2.0f + chartSize / 2.0f);
}

public void clearInstruction() {
    fill(255);
    noStroke();
    rectMode(CORNER);
    rect(0, chartSize, totalWidth, margin);
}

public void drawTextField() {
    cp5.addTextfield("answer")
        .setPosition(totalWidth / 2.0f - chartSize / 2.0f, totalHeight / 2.0f + chartSize / 2.0f + margin * 2)
        .setSize(textFieldWidth, textFieldHeight)
        .setColorCaptionLabel(color(0, 0, 0))
        .setFont(createFont("arial", 14))
        .setAutoClear(true);

    cp5.addBang("next")
        .setPosition(totalWidth / 2.0f + chartSize / 2.0f - buttonWidth, totalHeight / 2.0f + chartSize / 2.0f + margin * 2)
        .setSize(buttonWidth, textFieldHeight)
        .getCaptionLabel()
        .align(ControlP5.CENTER, ControlP5.CENTER);
}

public void drawIntro() {
    fill(0);
    textSize(fontSizeBig);
    textAlign(CENTER);
    text("In this experiment, \n" 
          + "you are asked to judge \n" 
          + "ratios between graphical elements " 
          + "in serveral charts. \n\n" 
          + "We won't record any other information from you except your answers.\n" 
          + "Click the \"agree\" button to begin. \n\n" 
          + "Thank you!", totalWidth / 2.0f, chartLeftY + chartSize / 4.0f);

    cp5.addBang("agree")
        .setPosition(totalWidth / 2.0f + margin * 2, totalHeight / 2.0f + chartSize / 2.0f)
        .setSize(buttonWidth, textFieldHeight)
        .getCaptionLabel()
        .align(ControlP5.CENTER, ControlP5.CENTER);

    cp5.addBang("disagree")
        .setPosition(totalWidth / 2.0f - margin * 3, totalHeight / 2.0f + chartSize / 2.0f)
        .setSize(buttonWidth, textFieldHeight)
        .getCaptionLabel()
        .align(ControlP5.CENTER, ControlP5.CENTER);
}

public void clearIntro() {
    background(color(255));
    cp5.get("agree").remove();
    cp5.get("disagree").remove();
}

public void agree() {
    index++;
    page2 = true;
}

public void disagree() {
    exit();
}

public void mouseMoved() {
    warning = null;
}

public void drawThanks() {
    background(255, 255, 255);
    fill(0);
    textSize(60);
    cp5.get(Textfield.class, "answer").remove();
    cp5.get("next").remove();
    textAlign(CENTER);
    text("Thanks!", totalWidth / 2.0f, totalHeight / 2.0f);
}

public void drawClose() {
    cp5.addBang("close")
        .setPosition(totalWidth / 2.0f - buttonWidth / 2.0f, totalHeight / 2.0f + margin_top + margin)
        .setSize(buttonWidth, textFieldHeight)
        .getCaptionLabel()
        .align(ControlP5.CENTER, ControlP5.CENTER);
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "a5" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
