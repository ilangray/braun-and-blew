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
