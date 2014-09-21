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

  void drawRect(Rect r, color stroke, color fill) {
     stroke(stroke);
     fill(fill);
     rect(r.x, r.y, r.w, r.h);
  }
