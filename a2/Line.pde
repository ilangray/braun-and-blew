class Line extends Graph {
  private class LineView extends Graph.DatumView {
    private final boolean hit;
    private final Rect hitbox;
    private final float DIAMETER_PERCENT = 0.3;
    private final float diameter; 
    public final Point center;
    
    public LineView(Datum datum, Shape s) {
      super(datum, s);
      
      Rect rect = (Rect)s;
      
      float newHeight = (datum.value / maxY) * rect.h;
      float yCoord = rect.h - newHeight + rect.y;
      
      float xCoord = rect.getCenter().x;
      center = new Point(xCoord, yCoord);
      diameter = DIAMETER_PERCENT * rect.w;
      
      // used to position label
      hitbox = new Rect(rect.x, yCoord, rect.w, newHeight);
      
      // Is mouse in point?
      hit = center.distFrom(new Point(mouseX, mouseY)) < diameter / 2;
    }
    
    void renderDatum() {
      color fill = hit ? HIGHLIGHTED_FILL : datum.fillColor;
      strokeWeight(0);
      fill(fill);
      ellipse(center.x, center.y, diameter, diameter);
    }
    
    void renderTooltip() {
      if (hit) {
        String s = "(" + datum.key + ", " + datum.value + ")";
        renderLabel(hitbox, s);
      }
    }
    
  }
  
  public final int LINE_THICKNESS = 2;
  
  // the percent of the lines to draw
  public float linePercent = 1.0;
  
  public Line(CSVData data) {
    this(data.datums, data.xLabel, data.yLabel); 
  }
  
  public Line(ArrayList<Datum> d, String xLabel, String yLabel) {
    super(d, xLabel, yLabel);
  }
  
  protected void renderContents() {
    createDatumViews();
    connectDatums();
    renderDatums();
    renderTooltips();
  }
 
  protected DatumView createDatumView(Datum datum, Shape bounds) {
    return new LineView(datum, bounds);
  }
   
  private void connectDatums() {
    for (int i = 0; i < views.size() - 1; i++) {
      drawLine(getLineView(i).center, getLineView(i + 1).center);
    }
  }
  
  private LineView getLineView(int index) {
    return (LineView)views.get(index);
  }
  
  private void drawLine(Point p, Point q) {
    stroke(LINE_THICKNESS);
    
    float endX = lerp(p.x, q.x, linePercent);
    float endY = lerp(p.y, q.y, linePercent);
    
    line(p.x, p.y, endX, endY);
  }
}
