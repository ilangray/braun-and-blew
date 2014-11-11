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

public class a1 extends PApplet {

// main

// constants 
String FILENAME = "soe-funding.csv";
float STARTING_X = 5;
float STARTING_Y = 5;
float X_OFFSET = 10;
float Y_OFFSET = 10;

// globals
SQTM tm;
Datum root;
int indORDERS = 0;
ArrayList<ArrayList<Property>> ORDERS = makeList(
  makeList(Property.DEPT, Property.SPONSOR, Property.YEAR),
  makeList(Property.DEPT, Property.YEAR, Property.SPONSOR),
  makeList(Property.SPONSOR, Property.DEPT, Property.YEAR),
  makeList(Property.SPONSOR, Property.YEAR, Property.DEPT),
  makeList(Property.YEAR, Property.DEPT, Property.SPONSOR),
  makeList(Property.YEAR, Property.SPONSOR, Property.DEPT)
);
ArrayList<Entry> ENTRIES = null; 

Graph g; 
Button toggle;

public void setup() {
  // general canvas setup
  size(1000, 800);

  frame.setResizable(true);
  ENTRIES = new CSVReader().read(FILENAME);
}

<T> ArrayList<T> makeList(T... ts) {
  ArrayList<T> toReturn = new ArrayList();
  
  for (T t : ts) {
    toReturn.add(t);
  }
  
  return toReturn;
}

public void draw() {
  background(color(255, 255, 255));
  ArrayList<GDatum> gds = new Transmogifier().groupBy(ENTRIES, ORDERS.get(indORDERS));
  g = new SQTMBar(gds, ORDERS.get(indORDERS).get(0).name, "Funding");
  g.render();
  float butWidth = g.PADDING_RIGHT / 1.2f * width;
  float butHeight = g.PADDING_TOP / 2.5f * height;
  float offset = butWidth / 10;
  
  toggle = new Button(new Rect(width - butWidth - offset, offset, butWidth, butHeight), color(0, 0, 0), "Toggle", color(255, 255, 255));
  toggle.render();
  
  // render the current order of properties
  textSize(15);
  textAlign(CENTER, CENTER);
  fill(color(0, 0, 0));
  text(ORDERS.get(indORDERS).toString(), width/2, offset + butHeight/2);
}

public void mouseClicked() {
  if (toggle.frame.containsPoint(mouseX, mouseY)){
    indORDERS = (1 + indORDERS) % ORDERS.size();
  }
}

// An instance of this class represents a button.
class Button {
  public Rect frame;
  public int background;
  public int textColor;
  public String title;
  
  public float PERCENT_PADDING = 0.1f;
  
  // black text, white background
  public Button(Rect frame, String text) {
    this(frame, color(255, 255, 255), text, color(0, 0, 0));
  }
  
  public Button(Rect frame, int background, String text, int textColor) {
    this.frame = frame;
    this.background = background;
    this.title = text;
    this.textColor = textColor;
  }

  public void render() { 
    fill(background);
    rect(frame.x, frame.y, frame.w, frame.h); 
   
    textAlign(CENTER, CENTER);
    textSize(calculateMaximumTextSize());
    fill(textColor);
    text(title, frame.w / 2 + frame.x, frame.h / 2 + frame.y);
  }
  
  private float calculateMaximumTextSize() {
    float h = frame.h * (1 - 2 * PERCENT_PADDING);
    float w = frame.w * (1 - 2 * PERCENT_PADDING);
    
    textSize(h);
    float textWidth = textWidth(title);
    
    // sometimes, you just get lucky
    if (textWidth <= w) {
      return h;
    }
    
    // ugggh
    float ratio = w / textWidth;
    return h * ratio;
  }
}

/**
 * Represents a single entry in the CSV file.
 */
class Entry {
  private final String dept;
  private final String sponsor;
  private final String year;
  private final int funding;
  
  public Entry(String dept, String sponsor, String year, int funding) {
    this.dept = dept;
    this.sponsor = sponsor;
    this.year = year;
    this.funding = funding;
  }
  
  public String toString() {
    String d = "dept = " + dept;
    String s = "sponsor = " + sponsor;
    String y = "year = " + year;
    String f = "funding = " + funding;
   
    return "CSV.Node{" + d + ", " + s + ", " + y + ", " + f + "}"; 
  }
}

/**
 * Reads a CSV file
 */
class CSVReader {

  private static final String SEPARATOR = ",";
 
  public CSVReader() { } 
 
  /**
   * Returns all of the nodes read in from the file.
   */
  public ArrayList<Entry> read(String filename) {
    String[] lines = loadStrings(filename);
    
    // we ignore the first line
    
    ArrayList<Entry> nodes = new ArrayList<Entry>();
    for (int i = 1; i < lines.length; i++) {
      nodes.add(parseNode(lines[i]));
    }
    return nodes;
  }
  
  private Entry parseNode(String line) {
    String[] comps = trim(split(line, SEPARATOR)); 
    
    String dept = comps[0];
    String sponsor = comps[1];
    String year = comps[2];
    int money = Integer.parseInt(comps[3]);
    
    return new Entry(dept, sponsor, year, money);
  }
}

// owned by ilan
class Datum {
  
  public final static int INVALID_VALUE = -1;
  
  public final int id;
  public int value;
  public final ArrayList<Datum> children;
  public final boolean isLeaf;
  public Entry entry;
  
  /**
   * Creates a new leaf datum with the given id and value. children will be null
   */
  public Datum(int id, int value, Entry entry) {
    this(id, value, true, entry);
  }
  
  /**
   * Creates a new NON-leaf datum with the given id. Children can be
   * added by accessing and mutating the list of children.
   */
  public Datum(int id) {
    this(id, INVALID_VALUE, false, null);
  }
  
  private Datum(int id, int value, boolean isLeaf, Entry entry) {
    this.id = id;
    this.value = value;
    this.isLeaf = isLeaf;
    this.entry = entry;
   
    if (isLeaf) {
      this.children = null;
    } else {
      this.children = new ArrayList<Datum>();
    } 
  }
  
  public Datum getAnyLeaf() {
    if (isLeaf) {
      return this;
    }
    return children.get(0).getAnyLeaf();
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

/**
 * Functions that operate on Entries.
 */
static class EntryGroupings {
  public static final Function<Entry, String> BY_DEPT = new Function<Entry, String>() {
    public String apply(Entry e) {
      return e.dept; 
    }
  };
  
  public static final Function<Entry, String> BY_SPONSOR = new Function<Entry, String>() {
    public String apply(Entry e) {
      return e.sponsor; 
    }
  };
  
  public static final Function<Entry, String> BY_YEAR = new Function<Entry, String>() {
    public String apply(Entry e) {
      return e.year; 
    }
  };
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
 
/* Graph abstract class */

class GDatum {
  public final String key;
  public final float value;
  
  // the Datum that should be passed into SQTM to render a treemap
  public Datum root;
  
  public GDatum(String key, float value, Datum root) {
    this.key = key;
    this.value = value;
    this.root = root;
  }
}

public abstract class Graph {
  public abstract class DatumView {
    protected final GDatum datum;
    protected final Rect bounds;
    
    public DatumView(GDatum datum, Rect bounds) {
      this.datum = datum;
      this.bounds = bounds;
    }
    
    public abstract void renderDatum();
  
    // renders the tooltip if 
    public abstract void renderTooltip(); 
  }
  
  protected final int HIGHLIGHTED_FILL = color(237, 119, 0);
  protected final int NORMAL_FILL = color(0, 0, 200);
  protected final ArrayList<GDatum> data;
  protected ArrayList<DatumView> views;
 
  public final int BLACK = color(0, 0, 0);
  public final int WHITE = color(255, 255, 255);
  
  public static final float INTERBAR_PERCENT_PADDING = 0.9f;
  public static final float PADDING_LEFT = 0.15f;
  public static final float PADDING_RIGHT = 0.15f;
  public static final float PADDING_TOP = 0.1f;
  public static final float PADDING_BOTTOM = 0.2f;
  
  public final float LABEL_PERCENT_FONT_SIZE = 0.01f;
  public final float LABEL_PERCENT_OFFSET = 0.5f * LABEL_PERCENT_FONT_SIZE;
  
  public final int THICKNESS = 3;
  public final int TICK_THICKNESS = 1;
  public final int TICK_WIDTH = 10;
  public final int TARGET_NUMBER_OF_TICKS = 11; 
  
  public final float AXIS_NAME_PERCENT_FONT_SIZE = 0.02f;
  public final float AXIS_LABEL_PERCENT_FONT_SIZE = AXIS_NAME_PERCENT_FONT_SIZE / 1.5f;
  
  public final int tickCount;
  public final float maxY;
  
  public final String xLabel;
  public final String yLabel;
  
  // TODO: label X axis *below* the axis, not to the right, so that there is more room for long names
  public Graph(ArrayList<GDatum> data, String xLabel, String yLabel) {
    this.data = data;    
    this.xLabel = xLabel;
    this.yLabel = yLabel;
    
    // compute the scale for the Y axis
    float actualMaxY = getMaxY(); 
    int interval = (int)(actualMaxY / TARGET_NUMBER_OF_TICKS);
    if (interval == 0) {
      interval++;
    }
    
    // tickCount = how many times you need to add interval to get something >= actualMaxY
    int ticks = TARGET_NUMBER_OF_TICKS;
    while (ticks * interval < actualMaxY) {
      ticks++;
    }
    tickCount = ticks;
    maxY = tickCount * interval;
  }
  
  public void render() {
    drawAxes();
    labelAxes(); 
    renderContents();
  }
  
  protected void renderContents() {
    createDatumViews();
    renderDatums();
    renderTooltips();
  }
  
  protected void createDatumViews() {
    views = new ArrayList<DatumView>(data.size());
    
    float uw = (getLR().x - getO().x) / data.size();
    float uh = getUL().y - getO().x;
    float y = getUL().y;
    float bottomY = getLR().y - THICKNESS / 2;
    
    for (int i = 0; i < data.size(); i++) {
      float x = i * uw + getO().x;
      float nextX = (i + 1) * uw + getO().x;
      Rect bounds = new Rect(new Point(x, y), new Point(nextX, bottomY));
      Rect adjustedBounds = bounds.scale(INTERBAR_PERCENT_PADDING, 1);
      
      views.add(createDatumView(data.get(i), adjustedBounds));
    }
  }
  
  protected void renderDatums() {
    for (int i = 0; i < data.size(); i++) {
      views.get(i).renderDatum();
    }
  }
  
  protected void renderTooltips() {
    for (int i = 0; i < data.size(); i++) {
      views.get(i).renderTooltip();
    }
  }

  private void drawAxes() {
       stroke(color(0, 0, 0));
       drawLine(getO(), getLR(), THICKNESS);
       drawLine(getO(), getUL(), THICKNESS);
  }
  
  private Point getO() {
    return new Point(width * PADDING_LEFT, height * (1 - PADDING_BOTTOM));
  }
  
  private Point getLR() {
    return new Point(width * (1 - PADDING_RIGHT), height * (1 - PADDING_BOTTOM));
  }
  
  private Point getUL() {
    return new Point(width * PADDING_LEFT, height * PADDING_TOP);
  }
  
  private void drawLine(Point p, Point q, int thickness) {
      strokeWeight(thickness);
      line(p.x, p.y, q.x, q.y);
  } 
  
  private void labelAxes() {
    labelX();
    labelY();
  }
  
  private void labelX() {
    float lineLength = getLR().x - getO().x;
    int intWidth = round(lineLength / PApplet.parseFloat(data.size()));
    float y = getO().y + 10;
    
    percentTextSize(AXIS_LABEL_PERCENT_FONT_SIZE);
    for (int i = 0; i < data.size(); i++) {
      float x = getO().x + i * intWidth + intWidth / 2;  // Find right location for label
      fill(0);
      textAlign(RIGHT, CENTER);
      verticalText(data.get(i).key, x, y);
    }
    
    // draw the axis name
    float x = (1 - PADDING_RIGHT / 2) * width;
    textAlign(CENTER, CENTER);
    percentTextSize(AXIS_NAME_PERCENT_FONT_SIZE);
    text(xLabel, x, getO().y);
  }
  
  // stolen from: http://forum.processing.org/one/topic/vertical-text.html
  private void verticalText(String s, float x, float y) {
    pushMatrix();
    translate(x, y);
    rotate(-HALF_PI/2);
    text(s, 0, 0);
    popMatrix();
  }
  
  private void labelY() {
    float lineHeight = getO().y - getUL().y;
    int intHeight = round(lineHeight / PApplet.parseFloat(tickCount));
    
    // the center of the tick marks
    float x = getO().x;
    int offset = TICK_WIDTH / 2;
    
    percentTextSize(AXIS_LABEL_PERCENT_FONT_SIZE);
    for (int i = 0; i < tickCount; i++) {
      float y = getUL().y + i * intHeight;
      
      // draw the tick mark
      fill(0);
      strokeWeight(TICK_THICKNESS);
      line(x - offset, y, x + offset, y);
     
      // label the tick mark
      // value is NOT pixels, its in the units of the Y axis
      int value = round(maxY - i * (maxY / tickCount));
      
      textAlign(RIGHT, CENTER);
      text(value, x - offset - 2, y);
    }
    
    // draw the axis name
    float y = PADDING_TOP / 2 * height;
    textAlign(CENTER, CENTER);
    percentTextSize(AXIS_NAME_PERCENT_FONT_SIZE);
    text(yLabel, x, y);
  }
  
  protected void percentTextSize(float percent) {
    textSize(percent * height);
  }
  
  // lets the subclass determines the type of DatumView used
  protected abstract DatumView createDatumView(GDatum data, Rect bounds);
  
  protected void drawRect(Rect r, int stroke, int fill) {
     stroke(stroke);
     fill(fill);
     rect(r.x, r.y, r.w, r.h);
  }
  
  // renders the given string as a label above the hitbox
  protected void renderLabel(Rect hitbox, String s) {
     float x = hitbox.getCenter().x;
     float y = hitbox.getMinY() - LABEL_PERCENT_OFFSET * height;
    
     // set font size because text measurements depend on it
     percentTextSize(LABEL_PERCENT_FONT_SIZE);
     
     // bounding rectangle
     float w = textWidth(s) * 1.1f;
     float h = LABEL_PERCENT_FONT_SIZE * height * 1.3f;
     Rect r = new Rect(x - w/2, y - h, w, h);
     drawRect(r, BLACK, WHITE);
     
     // text 
     textAlign(CENTER, BOTTOM);
     fill(BLACK);
     text(s, x, y);
   }
   
   private float getMaxY() {
    float max = data.get(0).value;
   
    for (int i = 1; i < data.size(); i++) {
      float v = data.get(i).value;
      if (v > max) {
        max = v;
      } 
    }
   
    return max; 
  }
}





public interface Function<F, T> {
  public T apply(F value);  
}

class Grouper <K, V> {  

  private final ArrayList<V> input;
  private final Map<K, ArrayList<V>> groups;
  
  public Grouper(ArrayList<V> input) {
    this.input = input;
    this.groups = new HashMap<K, ArrayList<V>>(); 
  }
  
  public Map<K, ArrayList<V>> by(Function<V, K> f) {
    
    for (V value : input) {
       K key = f.apply(value);
       add(key, value);
    }
    
    return groups;
  }
  
  private void add(K k, V v) {
    ArrayList<V> vs = groups.get(k);
    
    if (vs == null) {
      vs = new ArrayList<V>();
      groups.put(k, vs);
    }
    
    vs.add(v);
  }
}

// owned by phil
class Layout {
  private class Algorithm {
    private final int INSET_AMOUNT = 2;
    View parent;  // Parent view
    private Point realUL;  // The actual coordinate of the UL point of canvas
    private float canvLong;  // Length of long side of canvas
    private float canvShort;  // Length of short side of canvas
    private float sumOfRects;  // Sum of rects to be placed
    private float scale;  // To scale up to dimenssions of the screen
    private boolean shortIsWidth;  // Which side is the short side?

    // The floats are the unscaled area
    private ArrayList<Datum> remRects;  // Rectangles to be placed
    private ArrayList<Rect> currentRects;  // Rectangles in current row
    private ArrayList<Datum> currentDatums;  // Parallel arrays with currentRects corresponding Datums
    private ArrayList<View> finalViews;  // Final view for the level -- exported by class


    public Algorithm(View parent, float canvLong, float canvShort, ArrayList<Datum> startingVals, Point realUL, boolean shortIsWidth) {
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
        Datum nodeToConsider = getLargestRemaining();
        float areaCurrent = nodeToConsider.getValueF() * scale;

        // Calculate length of shared long side
        float sharedLong = 0;
        for (int i = 0; i < currentRects.size (); i++) {
          sharedLong += currentRects.get(i).getArea();
        }

        sharedLong += areaCurrent;
        sharedLong /= canvShort;
        
        ArrayList<Rect> tempRects = new ArrayList();
        ArrayList<Datum> tempDatums = new ArrayList();
        float shortAreaUsedUp = 0;  // Used for placing the rectangles
        // Put in previous rectangles
        for (int i = 0; i < currentRects.size (); i++) {
          Rect cRect = currentRects.get(i);
          Datum toAdd = currentDatums.get(i);
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
        Datum toAddDatum = currentDatums.get(i);
        Rect toAddRect = currentRects.get(i).inset(INSET_AMOUNT);
        View toAddView = new View(toAddDatum, toAddRect);
        finalViews.add(toAddView);
      }
    }

    // Adds largest datum to currentRect and currentDatum
    private void addFirstRect() {
      Datum toAdd = getLargestRemaining();
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
    private float addNewRectToTemp(ArrayList<Rect> tempRects, float areaRect, ArrayList<Datum> tempDatums, Datum toAddDatum, float sharedLong, float shortAreaUsedUp) {
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
  public final Rect bounds;

  public Layout(Datum root, Rect bounds) {
    this.root = root;
    this.bounds = bounds;
  }

  private ArrayList<Datum> copy(ArrayList<Datum> ds) {
    if (ds == null) {
      return null;
    }
    
    return new ArrayList<Datum>(ds);
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




// reads a file with a given name and returns a 
// tree of Datums representing the contents
// owned by ilan
class SHFReader {
  
  private final String filename;
  
  public SHFReader(String filename) {
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
    
    //println("WARNING: could not find the datum w/ id = " + id);
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
      
      // No entry that created datum since it is not csv file
      ds.add(new Datum(id, value, null));
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
  
  private Rect bounds; 
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
    View temp = currentView.viewSelected(p);
    if(temp != null){
      zoomOutStack.push(currentDatum);
      currentDatum = temp.datum;
    //  currentView = new Layout(temp.datum).solve();
    //  println(current.datum);
  //    println(current.bounds);
     //urrent = temp;
    }
  }
  
  public void setBounds(Rect newBounds) {
    this.bounds = newBounds;
  }
   
  public void zoomOut() {
    if(!zoomOutStack.isEmpty()) {
      currentDatum = zoomOutStack.pop();
    }
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


class SQTMBar extends Graph {
  
  private class SQTMView extends Graph.DatumView {
    private SQTM tm;
   
    public SQTMView(GDatum gDatum, Rect r) {
      super(gDatum, r);
      
      // need to calculate height of SQTM. width stays the same
      int newHeight = round((gDatum.value / maxY) * r.h);
      float heightDiff = r.h - newHeight;
       
      r = new Rect(r.x, r.y + heightDiff, r.w, newHeight);
      tm = new SQTM(r, gDatum.root);
    }   
    
    public void renderDatum() {
      tm.render();
    }
    
    public void renderTooltip() { }
  }
  
  public SQTMBar(ArrayList<GDatum> data, String xLabel, String yLabel) {
    super(data, xLabel, yLabel); 
  }
  
  public Graph.DatumView createDatumView(GDatum gDatum, Rect bounds) {
    // find the corresponding SQTM Datum
    return new SQTMView(gDatum, bounds);
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


class Transmogifier {

  private int id = 0;
 
  public ArrayList<GDatum> groupBy(ArrayList<Entry> entries, ArrayList<Property> ps) {
    // Build the Datum tree
    Datum root = new Datum(getNextId());
    root.children.addAll(groupBy(entries, ps, 0));
    root.calculateValue();
    
    //
    ArrayList<GDatum> toReturn = new ArrayList();
    for (Datum d : root.children) {
      // Create Gdatums for the top level of Datums
      String newKey = getGrouper(ps.get(0)).apply(d.getAnyLeaf().entry);
      GDatum newGDatum = new GDatum(newKey, d.value, d);
      toReturn.add(newGDatum);
    }
    
    return toReturn;
  }
  private ArrayList<Datum> groupBy(ArrayList<Entry> entries, ArrayList<Property> ps, int i) {
    if(i >= ps.size()) {
      return makeLeaves(entries);  
    }
    
    Map<String, ArrayList<Entry>> grouped = new Grouper(entries).by(getGrouper(ps.get(i)));

    ArrayList<Datum> toReturn = new ArrayList<Datum>();

    for(ArrayList<Entry> group : grouped.values()) {
      Datum d = new Datum(getNextId());//TODO ID STUFF
      // recurse to get + add the kiddies
      ArrayList<Datum> next = groupBy(group, ps, i+1);
      d.children.addAll(next);
      
      toReturn.add(d);
    }
    
    return toReturn;
    
  }
  
  private ArrayList<Datum> makeLeaves(ArrayList<Entry> entries) {
    ArrayList<Datum> toReturn = new ArrayList<Datum>();
    
    for(Entry e : entries) {
      toReturn.add(makeLeaf(e)); 
    }
    
    return toReturn;
  }
  
  private Datum makeLeaf(Entry e) {
    return new Datum(getNextId(), (int)e.funding, e);    
  }
  
  private int getNextId() {
    return id++;
  }
  
  private Function<Entry, String> getGrouper(String propertyName) {
    return new Function<Entry, String>() {
      public String apply(Entry e) {
        return null; //e.get(propertyName); 
      }
    };
  }
  
  private Function<Entry, String> getGrouper(Property p) {
    if(p == Property.DEPT) {
      return EntryGroupings.BY_DEPT;
    }
    if(p == Property.SPONSOR) {
      return EntryGroupings.BY_SPONSOR;
    }
    else {
      return EntryGroupings.BY_YEAR;
    }
  }
  
  public Transmogifier(){}//;
  
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
 public void render(int targetLevel, int currentLevel) {
    if (currentLevel == targetLevel) {
      doRender();
    } else {
      for (View subview : subviews) {
        subview.render(targetLevel, currentLevel + 1); 
      }
      strokeRect(bounds, STROKE_COLOR);
    }
     
     
  } 
  
  private void doRender() {
    boolean hit = bounds.containsPoint(mouseX, mouseY);
    int viewFill = hit ? HIGHLIGHTED_FILL : REGULAR_FILL;
    drawRect(bounds, STROKE_COLOR, viewFill);
    textAlign(CENTER, CENTER);
    fill(hit ? color(255, 255, 255) : color(0, 0, 0));
    
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
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "a1" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
