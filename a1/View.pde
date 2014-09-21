
// owned by ben
class View {
  
  private final float area;
  private final Rect bounds;
  private final ArrayList<View> subviews;
  
  public View(float area, Rect bounds, ArrayList<View> subviews) {
    this.area = area;
    this.bounds = bounds;
    this.subviews = subviews;
  }
 
  // rendering a view also renders all subviews
  // bounds for subviews must already be specified
  public void render() {
    // implement me
  } 
  
  // returns the view that should be zoomed in on for a click at point p, 
  // or null if none exists
  public View monkey_turkey_chicken(Point p) {
    return null;
  }
}
