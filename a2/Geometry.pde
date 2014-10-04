
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
  
  public String toString() {
    return "Wedge{center = " + center + ", radius = " + radius + ", startAngle = " + startAngle + ", endAngle = " + endAngle + "}";
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
}

void drawLine(Point p, Point q) { 
  line(p.x, p.y, q.x, q.y);
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
