
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
    
  }  
}
