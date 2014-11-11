import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class a2 extends PApplet {

// main

// constants 
String FILENAME = "ds1.csv";
float PERCENT_GRAPH_WIDTH = 0.8f;
float PERCENT_BUTTON_PADDING = 0.75f;

// the contents of the CSV file
CSVData DATA;

ArrayList<Button> buttons;

// the current graph being rendered
Graph currentGraph;
// what type is the current graph?
int currentType;

boolean animating = false;

int PIE = 0;
int BAR = 1;
int LINE = 2;
int STACKEDBAR = 3;

public void layoutUI() {
  // layout the graph
  currentGraph.setBounds(getGraphBounds());
  
  // layout side bar
  layoutButtons(getButtonFrames(getSidebarBounds()));
}

public void render() {
  currentGraph.render();
  
  renderSeparator();
 
  for (Button b : buttons) {
    b.render();
  } 
}

public void renderSeparator() {
  stroke(color(0,0,0));
  
  Rect frame = getGraphBounds();
  
  Point top = frame.getUR();
  Point bottom = frame.getLR();
  
  strokeWeight(4);
  drawLine(top, bottom);
}

public Rect getGraphBounds() {
  return new Rect(0, 0, PERCENT_GRAPH_WIDTH * width, height);
}

public Rect getSidebarBounds() {
  Rect graphBounds = getGraphBounds();
  float percentWidth = 1 - PERCENT_GRAPH_WIDTH;
  return new Rect(graphBounds.getMaxX(), 0, percentWidth * width, height);
}

public ArrayList<Rect> getButtonFrames(Rect sidebarBounds) {
  ArrayList<Rect> bounds = new ArrayList<Rect>();
  
  float h = sidebarBounds.h / buttons.size();
  
  for (int i = 0; i < buttons.size(); i++) {
    Rect frame = new Rect(sidebarBounds.x, sidebarBounds.y + h * i, sidebarBounds.w, h); 
    bounds.add(frame.scale(PERCENT_BUTTON_PADDING, PERCENT_BUTTON_PADDING));
  }
  
  return bounds;
}

public void layoutButtons(ArrayList<Rect> frames) {
  for (int i = 0; i < frames.size(); i++) {
    Button button = buttons.get(i);
    Rect frame = frames.get(i);
    
    button.frame = frame;
  }
}

public Button makeButton(String text) {
  return new Button(new Rect(0,0,0,0), color(255,255,255), text, color(0,0,0)); 
}

public void setup() {
  // general canvas setup
  size(900, 600);
  frame.setResizable(true);
  frameRate(30);
  
  // read CSV data
  DATA = new CSVReader().read(FILENAME);
  colorize(DATA.datums);
  
  // init buttons -- order must match value of constants
  buttons = makeList(
    makeButton("Pie Chart"),
    makeButton("Bar Graph"),
    makeButton("Line Graph"),
    makeButton("StackedBar")
  );

  // start with a bar graph
  currentGraph = new Bar(DATA);
  currentType = BAR;
}

public void draw() {
  background(color(255, 255, 255)); 
  
  layoutUI();
  render();
}

public void mouseClicked() {
  // find which button was hit
  Button hit = getButtonContainingMouse();
  
  if (hit == null) {
    return; 
  }
  
  animateTransition(buttons.indexOf(hit));
}

public void animateTransition(int newType) {
  // one animation at a time
  if (animating) {
    return;
  }
  
  // only animate if type changed
  if (newType == currentType) {
    return;
  } 
  
  animating = true;
  
  Graph g = currentGraph;
  
  if (isBar(g)) {
    Bar bar = (Bar)currentGraph;
    if (newType == LINE) {
      Line line = new Line(DATA);
      line.setBounds(currentGraph.getBounds());
      currentGraph = animate(bar, line, makeContinuation(line, newType));
    } else if (newType == PIE) {
      PieChart pie = new PieChart(DATA);
      pie.setBounds(getGraphBounds());
      currentGraph = animate(bar, pie, makeContinuation(pie, newType));
    } else {
      StackedBar sb = new StackedBar(DATA);
      sb.setBounds(getGraphBounds());
      currentGraph = animate(bar, sb, makeContinuation(sb, newType));
    }
  }
  else if (isLine(g)) {
    Line line = (Line)currentGraph;
    if (newType == BAR) {
      Bar bar = new Bar(DATA);
      bar.setBounds(getGraphBounds());
      currentGraph = animate(line, bar, makeContinuation(bar, newType));
    } else if (newType == PIE) {
      PieChart pie = new PieChart(DATA);
      pie.setBounds(getGraphBounds());
      currentGraph = animate(line, pie, makeContinuation(pie, newType));
    } else {
      StackedBar sb = new StackedBar(DATA);
      sb.setBounds(getGraphBounds());
      currentGraph = animate(line, sb, makeContinuation(sb, newType));
    }
  }
  else if (isPie(g)) {
    PieChart pie = (PieChart)currentGraph;
    if (newType == LINE) {
      Line line = new Line(DATA);
      line.setBounds(currentGraph.getBounds());
      currentGraph = animate(pie, line, makeContinuation(line, newType));
    } else if (newType == BAR) {
      Bar bar = new Bar(DATA);
      bar.setBounds(getGraphBounds());
      currentGraph = animate(pie, bar, makeContinuation(bar, newType));
    } else {
      StackedBar sb = new StackedBar(DATA);
      sb.setBounds(getGraphBounds());
      currentGraph = animate(pie, sb, makeContinuation(sb, newType)); 
    }
  }  
  else if (isStackedBar(g)) {
    StackedBar sb = (StackedBar)currentGraph;
    if (newType == LINE) {
      Line line = new Line(DATA);
      line.setBounds(getGraphBounds());
      currentGraph = animate(sb, line, makeContinuation(line, newType));
    } else if (newType == BAR) {
      Bar bar = new Bar(DATA);
      bar.setBounds(getGraphBounds());
      currentGraph = animate(sb, bar, makeContinuation(bar, newType));
    } else {  // newType == PIE
      PieChart pc = new PieChart(DATA);
      pc.setBounds(getGraphBounds());
      currentGraph = animate(sb, pc, makeContinuation(pc, newType));
    }
  }
  else {
    throw new IllegalArgumentException(); 
  }
}

public Continuation makeContinuation(final Graph result, final int type) {
  return new Continuation() {
    public void onContinue() {
      currentGraph = result;
      currentType = type;
      
      println("Animating COMPLETED");
      animating = false;
    } 
  };
}

public boolean isBar(Graph g) {
  return g instanceof Bar;
}

public boolean isLine(Graph g) {
  return g instanceof Line;
}

public boolean isPie(Graph g) {
  return g instanceof PieChart;
}

public boolean isStackedBar(Graph g) {
  return g instanceof StackedBar; 
}

public Button getButtonContainingMouse() {
  for (Button b : buttons) {
    if (b.containsMouse()) {
      return b;
    } 
  } 
  
  return null;
}

public void colorize(ArrayList<Datum> ds) {
  int start = color(202, 232, 211);
  int end = color(3, 101, 152);
  
  for (int i = 0; i < ds.size(); i++) {
    Datum datum = ds.get(i);
    float percent = 1.0f / ds.size() * i;
    
    float r = lerp(red(start), red(end), percent);
    float g = lerp(green(start), green(end), percent);
    float b = lerp(blue(start), blue(end), percent);
    
    datum.fillColor = color(r, g, b);
  } 
}


class Bar extends Graph {

   private class BarView extends Graph.DatumView {
    
     private Rect hitbox; 
     private boolean hit;
     
     public BarView(Datum d, Rect r) {
       super(d, r);
     }
     
     protected void onBoundsChange() {
       Rect r = (Rect)bounds;
       Datum d = datum;
       
       float newHeight = (d.value / maxY) * r.h;
       float heightDiff = r.h - newHeight;
       
       hitbox = new Rect(r.x, r.y + heightDiff, r.w, newHeight);
       hit = hitbox.containsPoint(mouseX, mouseY); 
     }
    
     public void renderDatum() {
       int fill = hit ? HIGHLIGHTED_FILL : datum.fillColor;
       strokeWeight(0);
       drawRect(hitbox, fill, fill);
     }
     
     public void renderTooltip() {
       if (hit) {
         String s = "(" + datum.key + ", " + datum.value + ")";
         renderLabel(hitbox, s);
       }
     }
   }
  
   public Bar(CSVData data) {
     super(data);
   }
  
   public Bar(ArrayList<Datum> d, String xLabel, String yLabel) {
     super(d, xLabel, yLabel);
   }
   
   protected DatumView createDatumView(Datum datum, Shape bounds) {
     return new BarView(datum, (Rect)bounds);
   }
}

// hides the secret for how to render a single frame of the animation from Bar <--> HeightGraph
class BarHeightGA extends GraphAnimator {
  
  private final Bar bar;
  
  public BarHeightGA(Bar bar, float duration, float percentStart, float percentEnd) {
    super(bar, duration, percentStart, percentEnd);
    
    // save bar specifically
    this.bar = bar;
  }
  
  private Rect getScaledRect(Rect r, float percent) {
    return r.scale(percent, 1.0f);
  }
  
  protected Graph.DatumView createDatumView(Datum d, Shape r, float percent) {
    return bar.createDatumView(d, getScaledRect((Rect)r, percent));
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
    strokeWeight(1); 
    fill(background);
    rect(frame.x, frame.y, frame.w, frame.h); 
   
    textAlign(CENTER, CENTER);
    textSize(calculateMaximumTextSize());
    fill(textColor);
    text(title, frame.w / 2 + frame.x, frame.h / 2 + frame.y);
  }
  
  public boolean containsMouse() {
     return frame.containsPoint(mouseX, mouseY);
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
/* Graph abstract class */

class Datum {
  public final String key;
  public final float value;
  
  // the first line of teh file, which names all fields
  public final ArrayList<String> fields;
  public final ArrayList<Float> values;
  
  public final float total;
  
  public final int dimensions;
  
  public int fillColor;
  
  public Datum(String key, ArrayList<Float> values, ArrayList<String> fields) {
    this.fields = fields;
    this.values = values;
   
    this.key = key;
    this.value = values.get(0); 
    
    this.total = calculateTotal();
    this.dimensions = values.size();
    
    fillColor = color(255,255,255);
  }
  
  private float calculateTotal() {
    float sum = 0;
   
    for (int i = 0; i < values.size(); i++) {
      sum += values.get(i); 
    }
   
    return sum;  
  }
  
  public float getTotal() {
    return total;
  }
}

public abstract class Graph {
  public abstract class DatumView {
    protected final Datum datum;
    protected Shape bounds;
    
    public DatumView(Datum datum, Shape bounds) {
      this.datum = datum;
      setBounds(bounds);
    }
    
    public final void setBounds(Shape bounds) {
      this.bounds = bounds;
      onBoundsChange();
    }
    
    // called when bounds change. override for fun
    protected void onBoundsChange() {}
    
    public abstract void renderDatum();
  
    // renders the tooltip if 
    public abstract void renderTooltip(); 
  }
  protected final int HIGHLIGHTED_FILL = color(237, 119, 0);
  protected final int NORMAL_FILL = color(0, 0, 200);
  protected final ArrayList<Datum> data;
  protected ArrayList<DatumView> views;
 
  public final int BLACK = color(0, 0, 0);
  public final int WHITE = color(255, 255, 255);
   
  public static final float PADDING_LEFT = 0.15f;
  public static final float PADDING_RIGHT = 0.15f;
  public static final float PADDING_TOP = 0.1f;
  public static final float PADDING_BOTTOM = 0.2f;
  
  public final float LABEL_PERCENT_FONT_SIZE = 0.03f;
  public final float LABEL_PERCENT_OFFSET = 0.5f * LABEL_PERCENT_FONT_SIZE;
  
  public final int THICKNESS = 3;
  public final int TICK_THICKNESS = 1;
  public final int TICK_WIDTH = 10;
  public final int TARGET_NUMBER_OF_TICKS = 11; 
  
  public final float AXIS_NAME_PERCENT_FONT_SIZE = 0.05f;
  public final float AXIS_LABEL_PERCENT_FONT_SIZE = AXIS_NAME_PERCENT_FONT_SIZE / 1.5f;
  
  public final int tickCount;
  public final float maxY;
  
  public final String xLabel;
  public final String yLabel;
  
  // where the graph should draw itself
  private Rect bounds;
  
  public Graph() {
    this(new ArrayList<Datum>(), "", ""); 
  }
  
  public Graph(CSVData data) {
    this(data.datums, data.xLabel, data.yLabel); 
  }
  
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
  
  public void setBounds(Rect bounds) {
    this.bounds = bounds; 
  }
  
  public void setBounds(Graph g) {
    setBounds(g.getBounds()); 
  }
  
  public Rect getBounds() {
    return bounds; 
  }
  
  public void render() {
    renderAxes();
    renderContents();
  }
  
  protected void renderAxes() {
    drawAxes();
    labelAxes(); 
  }
  
  protected void renderContents() {
    createDatumViews();
    renderDatums();
    renderTooltips();
  }
  
  protected void createDatumViews() {
    views = new ArrayList<DatumView>(data.size());
    
    for (int i = 0; i < data.size(); i++) {   
      Datum d = data.get(i);
      views.add(createDatumView(d, getDatumViewBounds(d, i, views)));
    }
  }
  
  // returns the bounds for the given datum view
  protected Shape getDatumViewBounds(Datum d, int i, ArrayList<DatumView> previous) {
    float uw = (getLR().x - getO().x) / data.size();
    float uh = getUL().y - getO().x;
    float y = getUL().y;
    float bottomY = getLR().y - THICKNESS / 2;
    
    float x = i * uw + getO().x;
    float nextX = (i + 1) * uw + getO().x;
    return new Rect(new Point(x, y), new Point(nextX, bottomY)).scale(0.5f, 1);
  }
  
  // lets the subclass determines the type of DatumView used
  protected abstract DatumView createDatumView(Datum data, Shape bounds);
  
  protected void renderDatums() {
    if (views == null) {
      return;
    }
    
    for (int i = 0; i < data.size(); i++) {
      DatumView dv = views.get(i);
      if (dv != null) {
        dv.renderDatum(); 
      }
    }
  }
  
  protected void renderTooltips() {
    for (int i = 0; i < data.size(); i++) {
      DatumView dv = views.get(i);
      if (dv != null) {
        dv.renderTooltip(); 
      }
    }
  }

  private void drawAxes() {
       stroke(color(0, 0, 0));
       drawLine(getO(), getLR(), THICKNESS);
       drawLine(getO(), getUL(), THICKNESS);
  }
  
  private Point getO() {
    if (bounds == null) {  
      println("Bounds is null on graph = " + this); 
    }
    return new Point(bounds.x + bounds.w * PADDING_LEFT, bounds.y + bounds.h * (1 - PADDING_BOTTOM));
  }
  
  private Point getLR() {
    if (bounds == null) {
      println("Bounds is null on graph = " + this); 
    }
    return new Point(bounds.x + bounds.w * (1 - PADDING_RIGHT), bounds.y + bounds.h * (1 - PADDING_BOTTOM));
  }
  
  private Point getUL() {
    return new Point(bounds.x + bounds.w * PADDING_LEFT, bounds.y + bounds.h * PADDING_TOP);
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
    float intWidth = round(lineLength / PApplet.parseFloat(data.size()));
    float y = getO().y + 10;
    
    percentTextSize(AXIS_LABEL_PERCENT_FONT_SIZE);
    for (int i = 0; i < data.size(); i++) {
      float x = getO().x + i * intWidth + intWidth / 2;  // Find right location for label
      fill(0);
      textAlign(RIGHT, CENTER);
      verticalText(data.get(i).key, x, y);
    }
    
    // draw the axis name
    float x = (1 - PADDING_RIGHT / 2) * bounds.w + bounds.x;
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
    float intHeight = round(lineHeight / PApplet.parseFloat(tickCount));
    
    // the center of the tick marks
    float x = getO().x;
    float offset = TICK_WIDTH / 2;
    
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
    float y = PADDING_TOP / 2 * bounds.h + bounds.y;
    textAlign(CENTER, CENTER);
    percentTextSize(AXIS_NAME_PERCENT_FONT_SIZE);
    text(yLabel, x, y);
  }
  
  protected void percentTextSize(float percent) {
    textSize(percent * bounds.h);
  }
  
  protected void drawRect(Rect r, int stroke, int fill) {
     stroke(stroke);
     fill(fill);
     rect(r.x, r.y, r.w, r.h);
  }
  
  // renders the given string as a label above the hitbox
  protected void renderLabel(Rect hitbox, String s) {
     float x = hitbox.getCenter().x;
     float y = hitbox.getMinY() - LABEL_PERCENT_OFFSET * bounds.h;
    
     // set font size because text measurements depend on it
     percentTextSize(LABEL_PERCENT_FONT_SIZE);
     
     // bounding rectangle
     float w = textWidth(s) * 1.1f;
     float h = LABEL_PERCENT_FONT_SIZE * bounds.h * 1.3f;
     Rect r = new Rect(x - w/2, y - h, w, h);
     drawRect(r, BLACK, WHITE);
     
     // text 
     textAlign(CENTER, BOTTOM);
     fill(BLACK);
     text(s, x, y);
   }
   
   protected void renderLabel(float x, float y, String s) {
     // set font size because text measurements depend on it
     percentTextSize(LABEL_PERCENT_FONT_SIZE);
     
     // bounding rectangle
     float w = textWidth(s) * 1.1f;
     float h = LABEL_PERCENT_FONT_SIZE * bounds.h * 1.3f;
     Rect r = new Rect(x - w/2, y - h, w, h);
     drawRect(r, BLACK, WHITE);
     
     // text 
     textAlign(CENTER, BOTTOM);
     fill(BLACK);
     text(s, x, y);
   }
   
   protected float getMaxY() {
     if (data.isEmpty()) {
       return 0; 
     }
     
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


public interface Continuation {
  // invoked when the continuation should run.
  public void onContinue();
}

abstract class GraphAnimator extends Graph {
  
  public final Graph g;
  
  private final Interpolator interpolator;  // tracks percentage progress over time
  
  protected Continuation cont;
  private float percent;
  
  protected GraphAnimator(Graph g, float duration, float percentStart, float percentEnd) {
    super(g.data, g.xLabel, g.yLabel);
    
    this.g = g;
    this.interpolator = new Interpolator(duration, percentStart, percentEnd);
  }
  
  public GraphAnimator setContinuation(Continuation cont) {
    this.cont = cont;
    return this;
  }
  
  protected void updateCurrentPercent() {
    this.percent = calculateCurrentPercent();
  }
  
  protected void checkIfCompleted() {
    if (percent == interpolator.end) {
      cont.onContinue();
    } 
  }
  
  public void render() {
    updateCurrentPercent();
    
    super.render();
   
    checkIfCompleted();
  }
  
  // returns a value in [0,1]
  private float calculateCurrentPercent() {
    float p = interpolator.getInterpolatedValue();  
    return clamp(p, 0, 1);
  }
  
  protected float getCurrentPercent() {
    return percent; 
  }
  
  protected Graph.DatumView createDatumView(Datum d, Shape bounds) {
    return createDatumView(d, bounds, this.percent);
  }
  
  // returns a DatumView with bounds adjusted for the current percent
  protected Graph.DatumView createDatumView(Datum d, Shape bounds, float percent) {
    return null; 
  }
}

class GraphSequenceAnimator extends GraphAnimator {
  
  private final ArrayList<GraphAnimator> animators;
  
  private GraphAnimator current;
  
  // no empty lists please
  public GraphSequenceAnimator(ArrayList<GraphAnimator> animators) {
    super(animators.get(0).g, 0, 0, 0); // haaacky
    
    this.animators = animators;
    
    setCurrent(0);
  } 
  
  public void setBounds(Rect bounds) {
    super.setBounds(bounds);
   
    current.setBounds(bounds); 
  }
  
  private void setCurrent(final int i) {
    // base case
    if (i >= animators.size()) {
      cont.onContinue();
      return; 
    }
    
    // move to the next animator
    current = animators.get(i);
    current.interpolator.start();
    current.setContinuation(new Continuation() {
      public void onContinue() {
        setCurrent(i+1); 
      }
    });
  }
  
  public void render() {
    current.render(); 
  }
  
  protected final Graph.DatumView createDatumView(Datum d, Shape bounds) {
    return current.createDatumView(d, bounds);
  }
}

/**
 * methods for instantiating the correct type of animator based on src/dest graphs
 */
 
// STACKEDBAR <--> BAR
public GraphAnimator animate(StackedBar sb, Bar bg, float duration) {
  final PathGraph sbApprox = new PathGraph(sb, null);
  final PathGraph bgApprox = new PathGraph(bg, getInterpolateHelper(false));
  
  return new PathGA(sbApprox, bgApprox, duration, 0, 1);
}
public GraphAnimator animate(Bar bg, StackedBar sb, float duration) {
  final PathGraph sbApprox = new PathGraph(sb, null);
  final PathGraph bgApprox = new PathGraph(bg, getInterpolateHelper(false));
  
  return new PathGA(sbApprox, bgApprox, duration, 1, 0);
}
 
// BAR <--> HEIGHTGRAPH
public GraphAnimator animate(Bar bg, HeightGraph hg, float duration) {
  return new BarHeightGA(bg, duration, 1, 0);
}
public GraphAnimator animate(HeightGraph hb, Bar bg, float duration) {
  return new BarHeightGA(bg, duration, 0, 1);
}

// HEIGHTGRAPH <--> SCATTERPLOT
public GraphAnimator animate(HeightGraph hg, Scatterplot scat, float duration) {
  return new HeightScatterGA(hg, duration, 1, 0);
}
public GraphAnimator animate(Scatterplot scat, HeightGraph hg, float duration) {
  return new HeightScatterGA(hg, duration, 0, 1);
}

// SCATPLOT <--> LINE
public GraphAnimator animate(Scatterplot scat, Line lg, float duration) {
  return new ScatLineGA(lg, duration, 0, 1);
}
public GraphAnimator animate(Line lg, Scatterplot scat, float duration) {
  return new ScatLineGA(lg, duration, 1, 0);
}

// PIECHART <--> HEIGHTGRAPH
public GraphAnimator animate(PieChart pc, HeightGraph hg, float duration) {
  final PathGraph pcApprox = new PathGraph(pc, null);
  final PathGraph hgApprox = new PathGraph(hg, getInterpolateHelper(pc));
  
  return new PathGA(pcApprox, hgApprox, 2.0f, 0, 1);
}
public GraphAnimator animate(HeightGraph hg, PieChart pc, float duration) {
  final PathGraph pcApprox = new PathGraph(pc, null);
  final PathGraph hgApprox = new PathGraph(hg, getInterpolateHelper(pc));
  
  return new PathGA(pcApprox, hgApprox, 2.0f, 1, 0);
}

// Bar <--> Line
public GraphAnimator animate(Bar bg, Line lg, Continuation cont) {
 
  HeightGraph hg = new HeightGraph(bg.data, bg.xLabel, bg.yLabel);
  Scatterplot scat = new Scatterplot(bg.data, bg.xLabel, bg.yLabel);
  
  return new GraphSequenceAnimator(makeList(
      animate(bg, hg, 1.0f),
      animate(hg, scat, 1.0f),
      animate(scat, lg, 1.0f)
  )).setContinuation(cont);
}
public GraphAnimator animate(Line lg, Bar bg, Continuation cont) {
 
  HeightGraph hg = new HeightGraph(bg.data, bg.xLabel, bg.yLabel);
  Scatterplot scat = new Scatterplot(bg.data, bg.xLabel, bg.yLabel);
  
  return new GraphSequenceAnimator(makeList(
      animate(lg, scat, 1.0f),
      animate(scat, hg, 1.0f),
      animate(hg, bg, 1.0f)
  )).setContinuation(cont);
}

// Pie <--> Bar (direct)
public GraphAnimator animate(PieChart pc, Bar bg, Continuation cont) {
  
  final PathGraph pcApprox = new PathGraph(pc, null);
  final PathGraph bgApprox = new PathGraph(bg, getInterpolateHelper(pc));
  
  return new GraphSequenceAnimator(makeList(
      (GraphAnimator)new PathGA(pcApprox, bgApprox, 2.0f, 0, 1)
  )).setContinuation(cont);
  
}
public GraphAnimator animate(Bar bg, PieChart pc, Continuation cont) {
  
  final PathGraph pcApprox = new PathGraph(pc, null);
  final PathGraph bgApprox = new PathGraph(bg, getInterpolateHelper(pc));
  
  return new GraphSequenceAnimator(makeList(
      (GraphAnimator)new PathGA(bgApprox, pcApprox, 2.0f, 0, 1)
  )).setContinuation(cont);
  
}

// Pie <--> Line (through Height <--> Scatterplot)
public GraphAnimator animate(PieChart pc, Line lg, Continuation cont) {
 
  HeightGraph hg = new HeightGraph(pc.data, pc.xLabel, pc.yLabel);
  hg.setBounds(lg);
  Scatterplot scat = new Scatterplot(pc.data, pc.xLabel, pc.yLabel);
  scat.setBounds(pc);
  
  return new GraphSequenceAnimator(makeList(
      animate(pc, hg, 1.0f),
      animate(hg, scat, 1.0f),
      animate(scat, lg, 1.0f)
  )).setContinuation(cont);
}
public GraphAnimator animate(Line lg, PieChart pc, Continuation cont) {
  
  HeightGraph hg = new HeightGraph(pc.data, pc.xLabel, pc.yLabel);
  hg.setBounds(lg);
  Scatterplot scat = new Scatterplot(pc.data, pc.xLabel, pc.yLabel);
  scat.setBounds(lg);
  
  return new GraphSequenceAnimator(makeList(
      animate(lg, scat, 1.0f),
      animate(scat, hg, 1.0f),
      animate(hg, pc, 1.0f)
  )).setContinuation(cont);
}

// STACKED ****ing BAR
public GraphAnimator animate(StackedBar sb, Bar bg, Continuation cont) {
  return new GraphSequenceAnimator(makeList(
      animate(sb, bg, 1.0f)
  )).setContinuation(cont);
}
public GraphAnimator animate(Bar bg, StackedBar sb, Continuation cont) {
  return new GraphSequenceAnimator(makeList(
      animate(bg, sb, 1.0f)
  )).setContinuation(cont);
}

public GraphAnimator animate(StackedBar sb, Line lg, Continuation cont) {
  Bar bg = new Bar(sb.data, sb.xLabel, sb.yLabel);
  bg.setBounds(sb);
  HeightGraph hg = new HeightGraph(sb.data, sb.xLabel, sb.yLabel);
  hg.setBounds(sb);
  Scatterplot scat = new Scatterplot(sb.data, sb.xLabel, sb.yLabel);
  scat.setBounds(sb);
  
  return new GraphSequenceAnimator(makeList(
      animate(sb, bg, 1.0f),
      animate(bg, hg, 1.0f),
      animate(hg, scat, 1.0f),
      animate(scat, lg, 1.0f)
  )).setContinuation(cont);
}
public GraphAnimator animate(Line lg, StackedBar sb, Continuation cont) {
  Bar bg = new Bar(lg.data, lg.xLabel, lg.yLabel);
  bg.setBounds(sb);
  HeightGraph hg = new HeightGraph(lg.data, lg.xLabel, lg.yLabel);
  hg.setBounds(sb);
  Scatterplot scat = new Scatterplot(lg.data, lg.xLabel, lg.yLabel);
  scat.setBounds(sb);
  
  return new GraphSequenceAnimator(makeList(
      animate(lg, scat, 1.0f),
      animate(scat, hg, 1.0f),
      animate(hg, bg, 1.0f),
      animate(bg, sb, 1.0f)
  )).setContinuation(cont);
}

public GraphAnimator animate(StackedBar sb, PieChart pc, Continuation cont) {
  final Bar bg = new Bar(sb.data, sb.xLabel, sb.yLabel);
  bg.setBounds(sb);
  final PathGraph pcApprox = new PathGraph(pc, null);
  final PathGraph bgApprox = new PathGraph(bg, getInterpolateHelper(pc));
  
  return new GraphSequenceAnimator(makeList(
      animate(sb, bg, 1.0f),
      (GraphAnimator)new PathGA(bgApprox, pcApprox, 2, 0, 1)
  )).setContinuation(cont);
}
public GraphAnimator animate(PieChart pc, StackedBar sb, Continuation cont) {
  final Bar bg = new Bar(pc.data, pc.xLabel, pc.yLabel);
  bg.setBounds(pc);
  final PathGraph pcApprox = new PathGraph(pc, null);
  final PathGraph bgApprox = new PathGraph(bg, getInterpolateHelper(pc));
  
  return new GraphSequenceAnimator(makeList(
      (GraphAnimator)new PathGA(pcApprox, bgApprox, 2, 0, 1),
      animate(bg, sb, 1.0f)
  )).setContinuation(cont);
}

public InterpolateHelper getInterpolateHelper(final boolean interpolateLeft) {
  return new InterpolateHelper() {
    public boolean shouldInterpolateLeft(int i) {
      return interpolateLeft;
    } 
  };
}

public InterpolateHelper getInterpolateHelper(final PieChart pc) {
  return new InterpolateHelper() {
    public boolean shouldInterpolateLeft(int i) {
      Wedge w = (Wedge)pc.views.get(i).bounds;
      float mid = w.getMiddleAngle();
      
      return mid > HALF_PI && mid < PI + HALF_PI;
    } 
  };
}
class HeightGraph extends Graph {

   private class HeightView extends Graph.DatumView {
     
     public Point top;
     public Point bottom;
     
     public HeightView(Datum d, Shape s) {
       super(d, s);
       
       Rect r = (Rect)s;
       
       float newHeight = (d.value / maxY) * r.h;
       float heightDiff = r.h - newHeight;
       
       float x = r.x + r.w/2;
       
       top = new Point(x, r.y + heightDiff);
       bottom = new Point(x, r.y + r.h);
     }
    
     public void renderDatum() {
       stroke(datum.fillColor);
       drawLine(top, bottom);
     }
     
     public void renderTooltip() {}
     
     
   }
  
   public HeightGraph(CSVData data) {
     super(data); 
   }
  
   public HeightGraph(ArrayList<Datum> d, String xLabel, String yLabel) {
     super(d, xLabel, yLabel);
   }
   
   protected DatumView createDatumView(Datum datum, Shape bounds) {
     return new HeightView(datum, bounds);
   }
}

// hides the secret for how to render a single frame of the animation from Bar <--> HeightGraph
class HeightScatterGA extends GraphAnimator {
  
  private final HeightGraph hg;
  
  public HeightScatterGA(HeightGraph hg, float duration, float percentStart, float percentEnd) {
    super(hg, duration, percentStart, percentEnd);
    
    // save bar specifically
    this.hg = hg;
  }
  
  private Rect getScaledRect(Rect r, float percent) {
    return new Rect(r.x, r.y, r.w, r.h * percent);
  }
  
  protected Graph.DatumView createDatumView(Datum d, Shape s, float percent) { 
    Rect r = (Rect)s;
    
    HeightGraph.HeightView dv = (HeightGraph.HeightView) hg.createDatumView(d, r);
    
    float y = percent * (dv.bottom.y - dv.top.y) + dv.top.y;
    dv.bottom = new Point(dv.bottom.x, y); 
    
    return dv;
  }
}


/**
 * An Interpolator provides the ability to track the percentage
 * progress of the passage of a length of time.
 */
class Interpolator {
  
  public final float seconds;
  public final float start;
  public final float end;
  
  private float rate;
  private int startFrame;
  private int totalFrames;
 
  public Interpolator(float seconds, float start, float end) {
    this.seconds = seconds;
    this.start = start;
    this.end = end;
  } 
  
  public Interpolator start() {
    rate = frameRate;
    startFrame = frameCount;
    
    totalFrames = Math.round(rate * seconds);
    
    return this;
  }
  
  private int getElapsedFrames() {
    return frameCount - startFrame;
  }
  
  private float getPercentTime() {
    return getElapsedFrames() / (float)totalFrames; 
  }
  
  public float getInterpolatedValue() {
    float percentTime = getPercentTime();
    
//    println("percent time = " + percentTime);
    
    return lerp(start, end, getPercentTime());
  }
}
class Line extends Graph {
  private class LineView extends Graph.DatumView {
    private final boolean hit;
    private final Rect hitbox;
    private final float DIAMETER_PERCENT = 0.3f;
    private final float diameter; 
    public final Point center;
    
    public LineView(Datum datum, Shape s) {
      super(datum, s);
      
      Rect rect = (Rect)s;
      
      float newHeight = (datum.value / maxY) * rect.h;
      float yCoord = rect.h - newHeight + rect.y;
      
      float xCoord = rect.getCenter().x;
      center = new Point(xCoord, yCoord);
      diameter = DIAMETER_PERCENT * rect.w;
      
      // used to position label
      hitbox = new Rect(rect.x, yCoord, rect.w, newHeight);
      
      // Is mouse in point?
      hit = center.distFrom(new Point(mouseX, mouseY)) < diameter / 2;
    }
    
    public void renderDatum() {
      int fill = hit ? HIGHLIGHTED_FILL : datum.fillColor;
      strokeWeight(0);
      fill(fill);
      ellipse(center.x, center.y, diameter, diameter);
    }
    
    public void renderTooltip() {
      if (hit) {
        String s = "(" + datum.key + ", " + datum.value + ")";
        renderLabel(hitbox, s);
      }
    }
    
  }
  
  public final int LINE_THICKNESS = 2;
  
  // the percent of the lines to draw
  public float linePercent = 1.0f;
  
  public Line(CSVData data) {
    this(data.datums, data.xLabel, data.yLabel); 
  }
  
  public Line(ArrayList<Datum> d, String xLabel, String yLabel) {
    super(d, xLabel, yLabel);
  }
  
  protected void renderContents() {
    createDatumViews();
    connectDatums();
    renderDatums();
    renderTooltips();
  }
 
  protected DatumView createDatumView(Datum datum, Shape bounds) {
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
    
    float endX = lerp(p.x, q.x, linePercent);
    float endY = lerp(p.y, q.y, linePercent);
    
    line(p.x, p.y, endX, endY);
  }
}

interface InterpolateHelper {
  public boolean shouldInterpolateLeft(int i); 
}

class PathGraph extends Graph {
  public class PathView extends Graph.DatumView {
    
    private Path getPath() {
      return (Path)bounds; 
    }
    
    public PathView(Datum d, Shape s) {
       super(d, s);
    }
    
    public void renderDatum() {
//      color(0,0,0)
      drawPath(getPath(), datum.fillColor, datum.fillColor);
    }
    
    public void renderTooltip() {}
  }
  
  // the graph to approximate with a path
  private final Graph g;
  private final InterpolateHelper interpHelper;
  
  public PathGraph(Graph g, InterpolateHelper interpHelper) {
    super(g.data, g.xLabel, g.yLabel);
    
    this.g = g;
    this.interpHelper = interpHelper;
    
    // must explicitly tell g to create its datum views
    g.createDatumViews();
  }
  
  protected Shape getDatumViewBounds(Datum d, int i, ArrayList<DatumView> previous) {
    DatumView toApprox = g.views.get(i);
    Path approxed = null;
    if (g instanceof Bar) {
      Bar.BarView bv = (Bar.BarView)toApprox;
      Rect bounds = (Rect)bv.hitbox;
      approxed = new Path(bounds, interpHelper.shouldInterpolateLeft(i));
    } 
    else if (g instanceof StackedBar) {
      StackedBar.StackedBarView sbv = (StackedBar.StackedBarView) toApprox;
      Rect bounds = (Rect)sbv.hitbox;
      approxed = new Path(bounds, false);
    }
    else if (g instanceof PieChart) {
      Wedge bounds = (Wedge)toApprox.bounds;
      approxed = new Path(bounds);
    } 
    else if (g instanceof HeightGraph) {
      HeightGraph.HeightView dv = (HeightGraph.HeightView)toApprox;
      
      Point top = dv.top;
      Point bottom = dv.bottom;
      
      Rect bounds = new Rect(top.x, top.y, 0, bottom.y - top.y);
//      approxed = new Path(bounds, false);
      approxed = new Path(bounds, interpHelper.shouldInterpolateLeft(i));
    } 
    else {
      throw new IllegalArgumentException(); 
    }
    
    return approxed;
  }
  
  // returns true iff the Path should interpolate the left side, otherwise the right.
  private boolean shouldInterpolateLeft(int i) {
    return i > data.size() / 2;
  }    
  
  private boolean shouldInterpolateLeft(Wedge w) {
    float midAngle = w.getMiddleAngle();
    
    if (midAngle > HALF_PI && midAngle < PI + HALF_PI) {
      return true;
    }
    return false;
  }
  
  protected DatumView createDatumView(Datum d, Shape s) {
    return new PathView(d, s); 
  }
}

class PathGA extends GraphAnimator {

  private final PathGraph src;
  private final PathGraph dest;
  
  // src and dest must be either: PieChart, BarGraph, HeightGraph
  public PathGA(PathGraph src, PathGraph dest, float duration, float percentStart, float percentEnd) {
    super(src, duration, percentStart, percentEnd);
    
    this.src = src;
    this.dest = dest;
    
    src.createDatumViews();
    dest.createDatumViews();
  }
  
  public void setBounds(Rect bounds) {
    super.setBounds(bounds);
    src.setBounds(bounds);
    dest.setBounds(bounds); 
  }
  
  // turns out we actually LIKE rendering the axes here because it gives the viewer some sense of scale/destination
  //protected void renderAxes() {}
  
  protected Graph.DatumView createDatumView(Datum d, Shape r, float percent) {
    int i = src.data.indexOf(d);
    assert i >= 0;

    // interpolate between src and dest
    Path srcPath = (Path)src.views.get(i).bounds;
    Path destPath = (Path)dest.views.get(i).bounds;
    
    Path lerped = srcPath.lerpBetween(destPath, percent);
    return src.new PathView(d, lerped);
  }
}


class PieChart extends Graph {
  
  class PieChartView extends Graph.DatumView {
    
     public PieChartView(Datum d, Shape s) {
       super(d, s);
     }
       
     public void renderDatum() {
       renderWedge();
       labelWedge(); 
     }
     
     private void renderWedge() {
       Wedge w = getWedge();
      
       stroke(color(0,0,0));
       ellipseMode(RADIUS);
       fill(color(255, 255, 255));
       
       float start = Math.min(w.startAngle, w.endAngle);
       float end = Math.max(w.startAngle, w.endAngle);
       
       fill(datum.fillColor);
       arc(w.center.x, w.center.y, w.radius, w.radius, start, end, PIE);
     }
     
     private void labelWedge() {
       Wedge w = getWedge();
      
       float angle = w.getMiddleAngle();
       float radius = w.radius * 1.15f;
       
       float x = w.center.x + radius * cos(angle);
       float y = w.center.y + radius * sin(angle);
       
       textSize(15);
       fill(color(0,0,0));
       textAlign(CENTER);
       text(datum.key, x, y);
     }
     
     public Wedge getWedge() {
       return (Wedge)bounds; 
     }
     
     // actually renders a little label for each slice of the pie
     public void renderTooltip() {
       if (getWedge().containsPoint(new Point(mouseX, mouseY))) {
         String s = "(" + datum.key + ", " + datum.value + ")";
         renderLabel(mouseX, mouseY - 10, s);
       }
     }
  }
  
  private final float totalY;
  
  public PieChart(CSVData data) {
    super(data); 
    
    totalY = sumYValues(data.datums);
  }
  
  public PieChart(ArrayList<Datum> datums, String xLabel, String yLabel) {
    super(datums, xLabel, yLabel);
    
    totalY = sumYValues(datums);
  }
  
  private float sumYValues(ArrayList<Datum> data) {
    float sum = 0;
    
    for (int i = 0; i < data.size(); i++) {
      sum += data.get(i).value; 
    }
    
    return sum; 
  }
 
  protected Shape getDatumViewBounds(Datum d, int i, ArrayList<DatumView> previous) {
    Point center = getBounds().getCenter();  
    float radius = Math.min(getBounds().w, getBounds().h) * 1.0f/3;
   
    float angleRange = TWO_PI * d.value / totalY;
    
    float startAngle = lastEndingAngle(previous);
    float endAngle = startAngle - angleRange;
    
    return new Wedge(center, radius, startAngle, endAngle);
  }
  
  private float lastEndingAngle(ArrayList<DatumView> vs) {
    if (vs.isEmpty()) {
      return PI + HALF_PI; 
    }
    
    PieChartView pcv = (PieChartView) vs.get(vs.size() - 1);
    return pcv.getWedge().endAngle;
  }
  
  // lets the subclass determines the type of DatumView used
  protected DatumView createDatumView(Datum data, Shape bounds) {
    return new PieChartView(data, bounds);
  }
  
  
  // do nothing
  protected void renderAxes() {} 
}

// Represents the data read in from the CSV file
class CSVData {
  public final ArrayList<Datum> datums;
  public final String xLabel;
  public final String yLabel;
  
  public CSVData(ArrayList<Datum> datums, String xLabel, String yLabel) {
    this.datums = datums;
    this.xLabel = xLabel;
    this.yLabel = yLabel;
  }  
  
  public String toString() {
    return "xLabel = " + xLabel + ", yLabel = " + yLabel + ", datums = " + datums; 
  }
}

class CSVReader {
  
  private static final String SEPARATOR = ",";
  
  public CSVData read(String filename) {
      String[] lines = loadStrings(filename);
      
      ArrayList<String> fields = makeList(getComponents(lines[0]));
      fields.remove(0);
      
      ArrayList<Datum> ds = new ArrayList<Datum>();
      for (int i = 1; i < lines.length; i++) {
         ds.add(parseDatum(fields, lines[i]));
      }
      
      String[] firstLineComps = getComponents(lines[0]);
      String xLabel = firstLineComps[0];
      String yLabel = firstLineComps[1];
      
      return new CSVData(ds, xLabel, yLabel);
  }
  
  private String[] getComponents(String line) {
    return trim(split(line, SEPARATOR)); 
  }
  
  // yo if k is >= ss.length, its not gonna go well. youve been warned
  private String[] drop(String[] ss, int k) {
    assert k <= ss.length;
    
    String[] output = new String[ss.length - k];
    
    for (int i = k; i < ss.length; i++) {
      output[i - k] = ss[i];
    }
    
    return output;
  }
  
  private Datum parseDatum(ArrayList<String> fields, String line) {
    
    String[] comps = getComponents(line);
    
    String key = comps[0];
    
    ArrayList<Float> values = new ArrayList<Float>();
    
    for (int i = 1; i < comps.length; i++) {
      values.add(Float.parseFloat(comps[i])); 
    }
    
    return new Datum(key, values, fields);
  }
  
  // takes an array of comma-separated pairs of values
  // splits each line on commas, trims the result, and returns
  // an array of the form:
  //   [ <line1 first half>, <line 1 second half>, <line 2 first half>, ... ]
  private String[] mapSplit(String lines[]) {
    String[] parts = new String[lines.length * 2];
    
    for (int i = 0; i < lines.length; i++) {
      String line = lines[i];
      String[] comps = split(lines[i], ",");
      
      parts[2*i]     = comps[0];  
      parts[2*i + 1] = comps[1];
    }
    
    return parts;
  }
 
  <T> ArrayList<T> makeList(T[] values) {
    ArrayList<T> ts = new ArrayList<T>();
    
    for (T v : values) {
      ts.add(v); 
    }
    
    return ts; 
  } 
  
  public CSVReader() {
    
  }
  
}

class ScatLineGA extends GraphAnimator {
 
  private final Line lg;
  
  public ScatLineGA(Line lg, float duration, float percentStart, float percentEnd) {
    super(lg, duration, percentStart, percentEnd); 
    this.lg = lg;
  }
  
  public void render() {
    super.updateCurrentPercent();
    
    lg.linePercent = getCurrentPercent();
    lg.render(); 
    
    super.checkIfCompleted();
  }
  
}


class Scatterplot extends Graph {
  private class ScatterplotView extends Graph.DatumView {
    private final boolean hit;
    private final Rect hitbox;
    private final float DIAMETER_PERCENT = 0.3f;
    private final float diameter; 
    public final Point center;
    
    public ScatterplotView(Datum datum, Shape shape) {
      super(datum, shape);
      
      Rect rect = (Rect)shape;
      
      float newHeight = (datum.value / maxY) * rect.h;
      float yCoord = rect.h - newHeight + rect.y;
      
      float xCoord = rect.getCenter().x;
      center = new Point(xCoord, yCoord);
      diameter = DIAMETER_PERCENT * rect.w;
      
      // used to position label
      hitbox = new Rect(rect.x, yCoord, rect.w, newHeight);
      
      // Is mouse in point?
      hit = center.distFrom(new Point(mouseX, mouseY)) < diameter / 2;
    }
    
    public void renderDatum() {
      int fill = hit ? HIGHLIGHTED_FILL : datum.fillColor;
      strokeWeight(0);
      fill(fill);
      ellipse(center.x, center.y, diameter, diameter);
    }
    
    public void renderTooltip() {
      if (hit) {
         String s = "(" + datum.key + ", " + datum.value + ")";
         renderLabel(hitbox, s);
      }
    }    
  }
  
  public final int LINE_THICKNESS = 2;
  
  public Scatterplot(CSVData data) {
    super(data); 
  }
  
  public Scatterplot(ArrayList<Datum> d, String xLabel, String yLabel) {
    super(d, xLabel, yLabel);
  }
  
  protected void renderContents() {
    createDatumViews();
    renderDatums();
    renderTooltips();
  }
 
  protected DatumView createDatumView(Datum datum, Shape bounds) {
    return new ScatterplotView(datum, bounds);
  }
}

class StackedBar extends Graph {

   private class StackedBarView extends Graph.DatumView {
    
     public Rect hitbox; 
     private boolean hit;
     
     private ArrayList<Rect> bars;
     
     public StackedBarView(Datum d, Shape r) {
       super(d, r);
     }
     
     protected Rect getBounds() {
       return (Rect)bounds; 
     }
     
     protected void onBoundsChange() {
       bars = new ArrayList<Rect>();
       
       Rect r = getBounds();
       
       // compute hitbox
       float newHeight = (datum.getTotal() / maxY) * r.h;
       float heightDiff = r.h - newHeight;
       
       hitbox = new Rect(r.x, r.y + heightDiff, r.w, newHeight);
       hit = hitbox.containsPoint(mouseX, mouseY);
       
       // use hitbox as bounds for stacked rects
       for (int i = 0; i < datum.dimensions; i++) {
         float start = getPreviousEnd();
         float hght = datum.values.get(i) / datum.getTotal() * hitbox.h;
         
         Rect segment = new Rect(hitbox.x, start - hght, hitbox.w, hght);
         bars.add(segment);
       }
       
     }
     
     private float getPreviousEnd() {
       if (bars.isEmpty()) {
         return getBounds().getMaxY();
       } else {
         return bars.get(bars.size() - 1).getMinY();
       } 
     }
     
     /*
     protected void onBoundsChange() {
       Rect r = (Rect)bounds;
       Datum d = datum;
       
       float newHeight = (d.getTotal() / maxY) * r.h;
       float heightDiff = r.h - newHeight;
       
       hitbox = new Rect(r.x, r.y + heightDiff, r.w, newHeight);
       hit = hitbox.containsPoint(mouseX, mouseY); 
     }*/
    
     public void renderDatum() {
       int fill = hit ? HIGHLIGHTED_FILL : datum.fillColor;
       strokeWeight(0);
       
       for (int i = 0; i < bars.size(); i++) {
         Rect r = bars.get(i);
         
         if (i > 0) {
           strokeWeight(5);
           stroke(color(0, 0, 0));
           drawLine(r.getLL(), r.getLR());
         }
         
         strokeWeight(0);
         drawRect(r, fill, fill); 
        // yolo look here for when the strokes elsewhere fuck up. 
       }
     }
     
     public void renderTooltip() {
       if (hit) {
         String s = "(" + datum.key + ", " + datum.getTotal() + ")";
         renderLabel(hitbox, s);
       }
     }
   }
  
   public StackedBar(CSVData data) {
     super(data);
   }
  
   public StackedBar(ArrayList<Datum> d, String xLabel, String yLabel) {
     super(d, xLabel, yLabel);
   }
   
   protected DatumView createDatumView(Datum datum, Shape bounds) {
     return new StackedBarView(datum, bounds);
   }
   
   protected float getMaxY() {
     if (data.isEmpty()) {
       return 0; 
     }
     
     float max = data.get(0).value;
   
     for (int i = 1; i < data.size(); i++) {
       float v = data.get(i).getTotal();
       if (v > max) {
         max = v;
       }  
     }
   
     return max; 
  }
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "a2" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
