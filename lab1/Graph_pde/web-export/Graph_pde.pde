/* Graph abstract class */

class Datum {
  public final String key;
  public final float value;
  
  public Datum(String key, float value) {
    this.key = key;
    this.value = value;
  }
}

public abstract class Graph {
  public abstract class DatumView {
    protected final Datum datum;
    protected final Rect bounds;
    
    public DatumView(Datum datum, Rect bounds) {
      this.datum = datum;
      this.bounds = bounds;
    }
    
    public abstract void renderDatum();
  
    // renders the tooltip if 
    public abstract void renderTooltip(); 
  }
  protected final color HIGHLIGHTED_FILL = color(237, 119, 0);
  protected final color NORMAL_FILL = color(0, 0, 200);
  protected final ArrayList<Datum> data;
  protected ArrayList<DatumView> views;
 
  public final color BLACK = color(0, 0, 0);
  public final color WHITE = color(255, 255, 255);
   
  public static final float PADDING_LEFT = 0.15;
  public static final float PADDING_RIGHT = 0.15;
  public static final float PADDING_TOP = 0.1;
  public static final float PADDING_BOTTOM = 0.2;
  
  public final float LABEL_PERCENT_FONT_SIZE = 0.03;
  public final float LABEL_PERCENT_OFFSET = 0.5 * LABEL_PERCENT_FONT_SIZE;
  
  public final int THICKNESS = 3;
  public final int TICK_THICKNESS = 1;
  public final int TICK_WIDTH = 10;
  public final int TARGET_NUMBER_OF_TICKS = 11; 
  
  public final float AXIS_NAME_PERCENT_FONT_SIZE = 0.05;
  public final float AXIS_LABEL_PERCENT_FONT_SIZE = AXIS_NAME_PERCENT_FONT_SIZE / 1.5;
  
  public final int tickCount;
  public final float maxY;
  
  public final String xLabel;
  public final String yLabel;
  
  // TODO: label X axis *below* the axis, not to the right, so that there is more room for long names
  public Graph(ArrayList<Datum> data, String xLabel, String yLabel) {
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
    
    int uw = (getLR().x - getO().x) / data.size();
    int uh = getUL().y - getO().x;
    int y = getUL().y;
    int bottomY = getLR().y - THICKNESS / 2;
    
    for (int i = 0; i < data.size(); i++) {
      int x = i * uw + getO().x;
      int nextX = (i + 1) * uw + getO().x;
      Rect bounds = new Rect(new Point(x, y), new Point(nextX, bottomY));
      Rect adjustedBounds = bounds.scale(0.5, 1);
      
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
    int lineLength = getLR().x - getO().x;
    int intWidth = round(lineLength / float(data.size()));
    int y = getO().y + 10;
    
    percentTextSize(AXIS_LABEL_PERCENT_FONT_SIZE);
    for (int i = 0; i < data.size(); i++) {
      int x = getO().x + i * intWidth + intWidth / 2;  // Find right location for label
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
    int lineHeight = getO().y - getUL().y;
    int intHeight = round(lineHeight / float(tickCount));
    
    // the center of the tick marks
    int x = getO().x;
    int offset = TICK_WIDTH / 2;
    
    percentTextSize(AXIS_LABEL_PERCENT_FONT_SIZE);
    for (int i = 0; i < tickCount; i++) {
      int y = getUL().y + i * intHeight;
      
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
  protected abstract DatumView createDatumView(Datum data, Rect bounds);
  
  protected void drawRect(Rect r, color stroke, color fill) {
     stroke(stroke);
     fill(fill);
     rect(r.x, r.y, r.w, r.h);
  }
  
  // renders the given string as a label above the hitbox
  protected void renderLabel(Rect hitbox, String s) {
     int x = hitbox.getCenter().x;
     float y = hitbox.getMinY() - LABEL_PERCENT_OFFSET * height;
    
     // set font size because text measurements depend on it
     percentTextSize(LABEL_PERCENT_FONT_SIZE);
     
     // bounding rectangle
     float w = textWidth(s) * 1.1;
     float h = LABEL_PERCENT_FONT_SIZE * height * 1.3;
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

class Bar extends Graph {

   private class BarView extends Graph.DatumView {
    
     private final Rect hitbox; 
     private final boolean hit;
     
     public BarView(Datum d, Rect r) {
       super(d, r);
       
       int newHeight = round((d.value / maxY) * r.h);
       int heightDiff = r.h - newHeight;
       
       hitbox = new Rect(r.x, r.y + heightDiff, r.w, newHeight);
       hit = hitbox.containsPoint(mouseX, mouseY); 
     }
    
     void renderDatum() {
       color fill = hit ? HIGHLIGHTED_FILL : NORMAL_FILL;
       strokeWeight(0);
       drawRect(hitbox, fill, fill);
     }
     
     void renderTooltip() {
       if (hit) {
         String s = "(" + datum.key + ", " + datum.value + ")";
         renderLabel(hitbox, s);
       }
     }
   }
  
   public Bar(ArrayList<Datum> d, String xLabel, String yLabel) {
     super(d, xLabel, yLabel);
   }
   
   protected DatumView createDatumView(Datum datum, Rect bounds) {
     return new BarView(datum, bounds);
   }
}

// An instance of this class represents a button.
class Button {
  public Rect frame;
  public color background;
  public color textColor;
  public String title;
  
  public float PERCENT_PADDING = 0.1;
  
  // black text, white background
  public Button(Rect frame, String text) {
    this(frame, color(255, 255, 255), text, color(0, 0, 0));
  }
  
  public Button(Rect frame, color background, String text, color textColor) {
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
class Point {
  public final int x;
  public final int y;
  
  public Point(int x, int y) {
    this.x = x;
    this.y = y;
  }
  
  public Point(float x, float y) {
    this(round(x), round(y));
  }
  
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
  int x, y, w, h;
  
  Rect(int x, int y, int w, int h) {
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
  }
  
  Rect(float x, float y, float w, float h) {
    this(round(x), round(y), round(w), round(h));
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
  
  public int getMinY() {
    return y;
  }
  
  public Rect scale(float sx, float sy) {
    float newWidth = w * sx;
    float newHeight = h * sy;
    float heightDiff = h - newHeight;
    float widthDiff = w - newWidth;
    
    return new Rect(x + widthDiff/2, y + heightDiff/2, newWidth, newHeight);
  }
  
  boolean containsPoint(int x, int y) {
    return containsX(x) && containsY(y);
  }
  
  boolean containsX(int x) {
    return (x >= this.x) && x <= (this.x + w);
  }
    
  boolean containsY(int y) {
    return (y >= this.y) && y <= (this.y + h);
  }
}
class Line extends Graph {
  private class LineView extends Graph.DatumView {
    private final boolean hit;
    private final Rect hitbox;
    private final float DIAMETER_PERCENT = 0.3;
    private final float diameter; 
    public final Point center;
    
    public LineView(Datum datum, Rect rect) {
      super(datum, rect);
      
      int newHeight = round((datum.value / maxY) * rect.h);
      int yCoord = rect.h - newHeight + rect.y;
      
      int xCoord = rect.getCenter().x;
      center = new Point(xCoord, yCoord);
      diameter = DIAMETER_PERCENT * rect.w;
      
      // used to position label
      hitbox = new Rect(rect.x, yCoord, rect.w, newHeight);
      
      // Is mouse in point?
      hit = center.distFrom(new Point(mouseX, mouseY)) < diameter / 2;
    }
    
    void renderDatum() {
      color fill = hit ? HIGHLIGHTED_FILL : NORMAL_FILL;
      strokeWeight(0);
      fill(fill);
      ellipse(center.x, center.y, diameter, diameter);
    }
    
    void renderTooltip() {
      if (hit) {
         String s = "(" + datum.key + ", " + datum.value + ")";
         renderLabel(hitbox, s);
         
//         new Rect(bounds.x, bounds.y, bounds.w, 0), s);
       }
    }
    
  }
  
  public final int LINE_THICKNESS = 2;
  
  public Line(ArrayList<Datum> d, String xLabel, String yLabel) {
    super(d, xLabel, yLabel);
  }
  
  protected void renderContents() {
    createDatumViews();
    connectDatums();
    renderDatums();
    renderTooltips();
  }
 
  protected DatumView createDatumView(Datum datum, Rect bounds) {
     return new LineView(datum, bounds);
   }
   
  private void connectDatums() {
    for (int i = 0; i < views.size() - 1; i++) {
      drawLine(getLineView(i).center, getLineView(i + 1).center);
    }
  }
  
  private LineView getLineView(int index) {
    return (LineView)views.get(index);
  }
  
  private void drawLine(Point p, Point q) {
    stroke(LINE_THICKNESS);
    line(p.x, p.y, q.x, q.y);
  }
}

/*
// parses a simple CSV file
class Contents {
  
  public final ArrayList<Datum> data;
  public final String xLabel;
  public final String yLabel;
  
  public static Contents Contents(String filename) {
   
    String lines[] = loadStrings(filename);
     
    
    
    
    String firstLine = lines[0];
    String labels = 
    String xLabel = null;
    String yLabel = null; 
    
    ArrayList<Datum> data = null;
    
   
    return new Contents(data, xLabel, yLabel);
  }
  
  // takes an array of comma-separated pairs of values
  // splits each line on commas, trims the result, and returns
  // an array of the form:
  //   [ <line1 first half>, <line 1 second half>, <line 2 first half>, ... ]
  private static String[] split(String lines[]) {
    String[] parts = new String[lines.size * 2];
    
    for (int i = 0; i < lines.size; i++) {
      String line = lines[i];
      String[] comps = split(lines[i], ",");
      
      parts[2*i]     = comps[0];
      parts[2*i + 1] = comps[1];
    }
    
    return parts;
  }
 
  private Contents(ArrayList<Datum> data, String xLabel, String yLabel) {
  
  }  
}

*/
  
// CONSTANTS
int WIDTH = 400;
int HEIGHT = 300;

// the two kinds of graphs were gonna show ya
Bar bar;
Line line;

// veetch vunnnn yurr kurrently luukin @
boolean currentlyBar;
Graph current;

// button to toggle
Button button;

void setup() {
  size(WIDTH, HEIGHT);
  frame.setResizable(true);
 
  button = new Button(new Rect(0, 0, 100, 100), "Click me, bro");
  
  Data data = readData("data.csv");
  line = new Line(data.datums, data.xLabel, data.yLabel);
  bar = new Bar(data.datums, data.xLabel, data.yLabel);
  
  // dont everybody line up at once
  currentlyBar = false;
  current = line;
}

Rect calculateButtonFrame() {
  int w = round(Graph.PADDING_RIGHT * 0.75 * width);
  int h = round(Graph.PADDING_TOP * 0.75 * height);
  
  Point center = new Point((1 - Graph.PADDING_RIGHT/2)*width, Graph.PADDING_TOP/2 * height);
   
  return new Rect(center.x - w/2, center.y - h/2, w, h);
}

void draw() {
  //println("I am drawing");
  background(255);
  current.render();
  
  button.frame = calculateButtonFrame(); 
  button.render();
}

void mouseClicked() {
  if (button.frame.containsPoint(mouseX, mouseY)) {
    currentlyBar = !currentlyBar;
    current = currentlyBar ? bar : line;
  }
}

// Represents the data read in from the CSV file
class Data {
  public final ArrayList<Datum> datums;
  public final String xLabel;
  public final String yLabel;
  
  public Data(ArrayList<Datum> datums, String xLabel, String yLabel) {
    this.datums = datums;
    this.xLabel = xLabel;
    this.yLabel = yLabel;
  }  
}

Data readData(String filename) {
    String[] lines = loadStrings(filename);
    
    String[] tokens = trim(split(lines));
    
    String xLabel = tokens[0];
    String yLabel = tokens[1];
    ArrayList<Datum> data = getDatums(tokens, 2);
    
    return new Data(data, xLabel, yLabel);
}

ArrayList<Datum> getDatums(String[] tokens, int start) {
  ArrayList<Datum> datums = new ArrayList<Datum>();
  
  for (int i = start; i < tokens.length; i += 2) {
    datums.add(new Datum(tokens[i], Float.parseFloat(tokens[i+1])));
  }
  
  return datums;
}

// takes an array of comma-separated pairs of values
// splits each line on commas, trims the result, and returns
// an array of the form:
//   [ <line1 first half>, <line 1 second half>, <line 2 first half>, ... ]
private static String[] split(String lines[]) {
  String[] parts = new String[lines.length * 2];
  
  for (int i = 0; i < lines.length; i++) {
    String line = lines[i];
    String[] comps = split(lines[i], ",");
    
    parts[2*i]     = comps[0];
    parts[2*i + 1] = comps[1];
  }
  
  return parts;
}
 

ArrayList<Datum> readData() {
  ArrayList<Datum> toReturn = new ArrayList<Datum>();
  toReturn.add(new Datum("Apple", 12));
  toReturn.add(new Datum("Sam", 4));
  toReturn.add(new Datum("GodayGoday", 80));
  
  return toReturn;
}

