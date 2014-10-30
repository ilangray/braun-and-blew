
class Axis {
  private ArrayList<Datum> datums;
  public String dimension;
  public float xCoordinate;
  private float maxVal;
  private float minVal;
  private final int numTicks = 10;
  //private float PERCENT_PADDING = .1;
  
  public Axis(ArrayList<Datum> ds, String dim, float xVal){
    this.datums = ds;
    this.dimension = dim;
    this.xCoordinate = xVal;
    
    maxVal = minVal = datums.get(0).getValue(dimension);
    
    for (Datum d : datums) {
      if(d.getValue(dimension) < minVal){
        minVal = d.getValue(dimension);
      }
      if(d.getValue(dimension) > maxVal){
        maxVal = d.getValue(dimension);
      }
    }

  }
  
  public void setXCoordinate(float x) {
    this.xCoordinate = x;
  }
  
  Point getPoint(Datum d){
    float val = d.getValue(dimension) - minVal;
    //float padding = height * PERCENT_PADDING;
    float range = maxVal - minVal;
    float yCoord = val * height / range;
    return new Point(xCoordinate, yCoord);
  }
  
  
  void render(){
    line(xCoordinate, 0, xCoordinate, height);
   /* for(Datum d : datums){
      fill(color(0,0,0));
      ellipseMode(RADIUS);
      ellipse(getPoint(d).x, getPoint(d).y, 15,15);
    }
  */
  }
}
