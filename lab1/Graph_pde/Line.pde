class Line extends Graph {
  private class LineView extends Graph.DatumView {
    private final boolean hit;
    private final float DIAMETER_PERCENT = 0.3;
    private final float diameter; 
    public final Point center;
    
    public LineView(Datum datum, Rect rect) {
      super(datum, rect);
      
      int newHeight = round((datum.value / maxY) * rect.h);
      int yCoord = rect.h - newHeight + rect.y;
      
      int xCoord = rect.getCenter().x;
      center = new Point(xCoord, yCoord);
      diameter = DIAMETER_PERCENT * rect.w;
      
      // Is mouse in point?
      hit = center.distFrom(new Point(mouseX, mouseY)) < diameter / 2;
    }
    
    void renderDatum() {
      color fill = hit ? HIGHLIGHTED_FILL : NORMAL_FILL;
      strokeWeight(0);
      fill(fill);
      //Point UL = center.offset(new Point(-diameter/2, -diameter/2));
      ellipse(center.x, center.y, diameter, diameter);
    }
    
    void renderTooltip() {
    }
    
  }
  
  public final int LINE_THICKNESS = 2;
  
  public Line(ArrayList<Datum> d, String xLabel, String yLabel) {
    super(d, xLabel, yLabel);
  }
  
  protected void renderContents() {
    createDatumViews();
    connectDatums();
    renderDatums();
    renderTooltips();
  }
 
  protected DatumView createDatumView(Datum datum, Rect bounds) {
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
    line(p.x, p.y, q.x, q.y);
  }
}
