
class RenderMachine {
  
  private final ArrayList<Datum> ds;
  private final Axis[] axes;
  
  public RenderMachine(ArrayList<Datum> ds, Axis[] axes) {
    this.ds = ds;
    this.axes = axes;
  }
 
  public void render() {
    for (Datum d : ds) {
      renderDatum(d);
    }
  } 
  
  private void renderDatum(Datum d) { 
    // render each property for the given datum
    for (int i = 0; i < d.getKeys().length; i++) {
      renderProperty(d, i);
    }
  }
  
  // renders a line from d.getValue(d.getKeys[propIndex]) to d.getValue(d.getKeys[propIndex+1])
  private void renderProperty(Datum d, int propIndex) {
     // cant render the last property because it has no end point
     if (propIndex == d.getKeys().length - 1) {
       return; 
     }
     
     Axis startAxis = axes[propIndex];
     Axis endAxis = axes[propIndex+1];
     
     renderProperty(d, startAxis, endAxis);
  }
  
  private void renderProperty(Datum d, Axis startAxis, Axis endAxis) {
     Point start = startAxis.getPoint(d);
     Point end = endAxis.getPoint(d);
     
     // draw the line
     line(start.x, start.y, end.x, end.y);
  }
}
