
// owned by ben
class View {
  
  private final Datum datum;
  private final Rect bounds;
  private final ArrayList<View> subviews;
  
  public View(Datum datum, Rect bounds, ArrayList<View> subviews) {
    this.datum = datum;
    this.bounds = bounds;
    this.subviews = subviews;
  }
 
  // rendering a view also renders all subviews
  // bounds for subviews must already be specified
  public void render() {
    // implement me
  } 
  
}
