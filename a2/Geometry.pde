
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
      Point start = r.getLL();
      Point end = r.getUL();
      
      // NB: start is added when i = 0
      for (int i = 0; i < NUM_PTS; i++) {
         points.add(start.lerpBetween(end, 1.0/NUM_PTS * i));
      }
      
      points.add(end);
      points.add(r.getUR());
      points.add(r.getLR());
    } else {
      // fancy edge is RIGHT
      Point start = r.getUR();
      Point end = r.getLR();
      
      // NB: start is added when i = 0
      for (int i = 0; i < NUM_PTS; i++) {
         points.add(start.lerpBetween(end, 1.0/NUM_PTS * i));
      }
      
      // add the other three corners
      points.add(end);
      points.add(r.getLL());
      points.add(r.getUL());
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
      float percent = 1.0/count * i;
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

  boolean containsPoint(float x, float y) {
    return containsX(x) && containsY(y);
  }
  
  boolean containsX(float x) {
    return (x >= this.x) && x <= (this.x + w);
  }
    
  boolean containsY(float y) {
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

void drawLine(Point p, Point q) { 
  line(p.x, p.y, q.x, q.y);
}

void drawPath(Path path, color stroke, color fill) {
  fill(fill);
  stroke(stroke);
  
  beginShape();
  
  for (int i = 0; i < path.points.size(); i++) {
    Point p = path.points.get(i);
    vertex(p.x, p.y);
  }
  
  endShape(CLOSE);
}

void drawRect(Rect r, color stroke, color fill) {
  stroke(stroke);
  fill(fill);
  rect(r.x, r.y, r.w, r.h);
}

void strokeRect(Rect r, color stroke) {
  noFill();
  stroke(stroke);
  rect(r.x, r.y, r.w, r.h); 
}

float clamp(float val, float min, float max) {
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
