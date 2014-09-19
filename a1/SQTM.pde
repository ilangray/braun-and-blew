
// owned by ben
// displays a squarified tree map
class SQTM {
  
  private final Rect bounds;
  private final Datum root;
  
  // holds the views that we zoomed through
  private final Stack<View> zoomOutStack;
  
  // the current root view being display. takes up the whole bounds
  private View current;
  
  public SQTM(Rect bounds, Datum root) {
    this.bounds = bounds;
    this.root = root;
  }
  
  // p determines which rectangle to zoom in on
  // NOTE: what happens if p is not inside the bounds of the receiving SQTM
  public void zoomIn(Point p) {
    // impelement me!  
  }
   
  public void zoomOut() {
    // impelement me!
  } 
  
  // calls render on the root view
  public void render() {
    current.render();
  }
}

