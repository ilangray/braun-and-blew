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
  
  public Point offset(Point other) {
    return new Point(other.x + x, other.y + y);
  }
  
  public float distFrom(Point other) {
    float dx = (other.x - x);
    float dy = (other.y - y);
    
    return sqrt(dx*dx + dy*dy);
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
    this.x = 0;
    this.y = 0;
  }

  public void add(Vector v) {
    this.x += v.x;
    this.y += v.y;
  }
  
  public void reset() {
    this.x = 0;
    this.y = 0;
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
 