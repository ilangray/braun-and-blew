
// owned by ben
class View {
  
  private final color STROKE_COLOR = color(0, 0, 0);
  private final color REGULAR_FILL = color(255, 255, 255);
  private final color HIGHLIGHTED_FILL = color(0, 0, 255);
  private final SQTMDatum datum;
  private final Rect bounds;
  private final ArrayList<View> subviews;
  
  
  public View(SQTMDatum datum, Rect bounds) {
    this.datum = datum;
    this.bounds = bounds;
    this.subviews = new ArrayList();
  }
  
 
  // rendering a view also renders all subviews
  // bounds for subviews must already be specified
 public void render(int targetLevel, int currentLevel) {
   
    if (datum.isLeaf) {
      doRender();
    } else {
      for (View subview : subviews) {
        subview.render(targetLevel, currentLevel + 1); 
      }
      strokeRect(bounds, STROKE_COLOR);
    }
   
  } 
  
  private void doRender() {
    if (datum.marked) {
       println("rendering a marked datum");
    } else {
      println("not marked");
    }
    color viewFill = datum.marked ? HIGHLIGHTED_FILL : REGULAR_FILL;
    drawRect(bounds, STROKE_COLOR, viewFill);
    textAlign(CENTER, CENTER);
    fill(datum.marked ? color(255, 255, 255) : color(0, 0, 0));
    
    if(datum.isLeaf) {
      text(datum.id, bounds.x + bounds.w / 2, bounds.y + bounds.h / 2 );
    } 
    
  }
  
  
  // returns the view that should be zoomed in on for a click at point p, 
  // or null if none exists

  public View viewSelected(Point p) {
    if(!bounds.containsPoint(p.x, p.y)) {
      return null;
    }
      
    for(int i = 0; i < subviews.size(); i ++) {
      if(subviews.get(i).bounds.containsPoint(p.x, p.y)) {
        return subviews.get(i);
      }
    }
    return null; //it should never get here, but just in case
  }
  
}
