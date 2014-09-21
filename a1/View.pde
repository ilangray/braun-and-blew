
// owned by ben
class View {
  
  private final color STROKE_COLOR = color(0, 0, 0);
  private final color REGULAR_FILL = color(255, 255, 255);
  private final color HIGHLIGHTED_FILL = color(0, 0, 255);
  private final Datum datum;
  private final Rect bounds;
  private final ArrayList<View> subviews;
  
  public View(Datum datum, Rect bounds) {
    this.datum = datum;
    this.bounds = bounds;
    this.subviews = new ArrayList();
  }
  
 
  // rendering a view also renders all subviews
  // bounds for subviews must already be specified
  public void render() {
    if(datum.isLeaf()) {
      color viewFill = bounds.containsPoint(mouseX, mouseY) ? HIGHLIGHTED_FILL : REGULAR_FILL;
      drawRect(bounds, STROKE_COLOR, viewFill);
    } else {
       drawRect(bounds, STROKE_COLOR, REGULAR_FILL);
    }
  } 
  
  // returns the view that should be zoomed in on for a click at point p, 
  // or null if none exists
  public View viewSelected(Point p) {
    if(!bounds.containsPoint(p.x, p.y))
      return null;
      
    for(int i = 0; i < subviews.size(); i ++) {
      if(subviews.get(i).bounds.containsPoint(p.x, p.y))
        return subviews.get(i);
    }
    return null; //it should never get here, but just in case
  }
  
}
