
// owned by ben
class View {
  
  public final Datum datum;
  public final Rect bounds;
  public final ArrayList<View> subviews;
  
  public View(Datum datum, Rect bounds) {
    this.datum = datum;
    this.bounds = bounds;
    this.subviews = new ArrayList();
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
