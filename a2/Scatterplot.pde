
class Scatterplot extends Graph {
  private class ScatterplotView extends Graph.DatumView {
    private final boolean hit;
    private final Rect hitbox;
    private final float DIAMETER_PERCENT = 0.3;
    private final float diameter; 
    public final Point center;
    
    public ScatterplotView(Datum datum, Rect rect) {
      super(datum, rect);
      
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
      color fill = hit ? HIGHLIGHTED_FILL : NORMAL_FILL;
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
  
  public Scatterplot(CSVData data) {
    super(data); 
  }
  
  public Scatterplot(ArrayList<Datum> d, String xLabel, String yLabel) {
    super(d, xLabel, yLabel);
  }
  
  protected void renderContents() {
    createDatumViews();
    renderDatums();
    renderTooltips();
  }
 
  protected DatumView createDatumView(Datum datum, Rect bounds) {
    return new ScatterplotView(datum, bounds);
  }
}
