

class PieChart extends Graph {
  
  class PieChartView extends Graph.DatumView {
    
     public PieChartView(Datum d, Shape s) {
       super(d, s);
     }
       
     void renderDatum() {
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
       
       arc(w.center.x, w.center.y, w.radius, w.radius, start, end, PIE);
     }
     
     private void labelWedge() {
       Wedge w = getWedge();
      
       float angle = w.getMiddleAngle();
       float radius = w.radius * 1.15;
       
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
     void renderTooltip() {
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
    Point center = new Point(width/2, height/2);  
    float radius = Math.min(width, height) * 1.0f/3;
    
    // this math is totally wrong. fix it.
    float angleRange = TWO_PI * d.value / totalY;
    
    float startAngle = lastEndingAngle(previous);
    float endAngle = startAngle - angleRange;
    
    println("startAngle = " + startAngle + ", endAngle = " + endAngle);
    
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
