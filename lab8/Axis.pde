
class Axis {
  private ArrayList<Datum> datums;
  public String dimension;
  public float xCoordinate;
  private final float maxVal;
  private final float minVal;
  private final int numTicks = 10;
  
  
  
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
  
  Point getPoint(Datum d){
    
  }
  
  
  void render(){
  
  
  }
}
