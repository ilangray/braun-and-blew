class Point {
  public float x;
  public float y;
  
  Point(float x, float y) {
    this.x = x;
    this.y = y;
  }
}

class Scatter2 {
  public final int POINT_RAD = 8;
  public ArrayList<Point> data;
  public final int canvWidth;
  public final int canvHeight;
  public final float minX;
  public final float maxX;
  public final float minY;
  public final float maxY;

  public Scatter2(ArrayList<Point> data, int canvWidth, int canvHeight, float minX, float maxX, float minY, float maxY) {
    this.data = data;
    this.data = new ArrayList(data);
    this.canvWidth = canvWidth;
    this.canvHeight = canvHeight;
    this.minX = minX;
    this.maxX = maxX;
    this.minY = minY;
    this.maxY = maxY;
    
    scalePoints(); 
  }
  
  public void scalePoints() {
  /*
    // Get maxs
    float maxX = 0;
    float maxY = 0;
 
 /*   
    for (Point p: data) {
      if (p.x > maxX) {
        maxX = p.x;
      }
      if (p.y > maxY) {
        maxY = p.y;
      }
    }
    */
    
    float xRat = (float(canvWidth) * 0.9) / maxX;
    float yRat = (float(canvHeight) * 0.9) / maxY;
    
    for (Point p : data) {
      p.x = xRat * p.x;
      p.y = yRat * p.y;
    }
    
  }
  
  public void render() {
    ellipseMode(CENTER);
    for (Point p :  data) {
      renderPoint(p);
    }
  }
  
  public void renderPoint(Point p) {
    ellipse(p.x, p.y, POINT_RAD, POINT_RAD);
  }
}
