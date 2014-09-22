
// owned by ben
class View {
  
  private final color STROKE_COLOR = color(0, 0, 0);
  private final color REGULAR_FILL = color(255, 255, 255);
  private final color HIGHLIGHTED_FILL = color(0, 0, 255);
  private final Datum datum;
  private final Rect bounds;
  private final ArrayList<View> subviews;
  
  private int depth;
  private int maxDepth = 0;
  private int strokeWidth;
  
  public View(Datum datum, Rect bounds) {
    this.datum = datum;
    this.bounds = bounds;
    this.subviews = new ArrayList();
  }
  
 
  // rendering a view also renders all subviews
  // bounds for subviews must already be specified
 public void render() {
    stroke(strokeWidth);
    if(datum.isLeaf) {
      color viewFill = bounds.containsPoint(mouseX, mouseY) ? HIGHLIGHTED_FILL : REGULAR_FILL;
      drawRect(bounds, STROKE_COLOR, viewFill);
      textAlign(CENTER, CENTER);
      fill(color(0, 0, 0));
      text(datum.id, bounds.x + bounds.w / 2, bounds.y + bounds.h / 2 );
    } else {
    
       for (View subview : subviews) {
         subview.render(); 
       }
       stroke(strokeWidth);
       strokeRect(bounds, STROKE_COLOR);
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
  
  public void calculateStrokes() {
    calculateStrokes(0);
  }
  
  private int calculateStrokes(int currDepth){
    depth = currDepth + 1;
    if(datum.isLeaf){
      maxDepth = depth;
      strokeWidth = 1;// maxDepth - depth + 1;
      return depth;
    }
    else{
      for(int i = 0; i < subviews.size(); i++){
        maxDepth = Math.max(maxDepth, subviews.get(i).calculateStrokes(depth));
      }
      strokeWidth = (maxDepth - depth + 1) + 10;
      println("Stroke w = " + strokeWidth);
      return maxDepth;
    }
  }
  
}
