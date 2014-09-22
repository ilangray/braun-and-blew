
// owned by ben
// displays a squarified tree map
class SQTM {
  
  private Rect bounds; 
//  private final Datum root;
  
  // holds the views that we zoomed through
  private final Stack<Datum> zoomOutStack;
  
  // the current root view being display. takes up the whole bounds
  private View currentView;
  private Datum currentDatum;

  public SQTM(Rect bounds, Datum root) {
    this.bounds = bounds;
//    this.root = root;
    this.currentDatum = root;
    this.zoomOutStack = new Stack<Datum>();
    //println("datum after layout" + current.datum);
  }
  
  // p determines which rectangle to zoom in on
  // NOTE: what happens if p is not inside the bounds of the receiving SQTM
  public void zoomIn(Point p) {
    View temp = currentView.viewSelected(p);
    if(temp != null){
      zoomOutStack.push(currentDatum);
      currentDatum = temp.datum;
    //  currentView = new Layout(temp.datum).solve();
    //  println(current.datum);
  //    println(current.bounds);
     //urrent = temp;
    }
  }
  
  public void setBounds(Rect newBounds) {
    this.bounds = newBounds;
  }
   
  public void zoomOut() {
    if(!zoomOutStack.isEmpty()) {
      currentDatum = zoomOutStack.pop();
    }
  } 
  
  // calls render on the root view
  public void render() {
   currentView = new Layout(currentDatum, bounds).solve();
   currentView.render(levelsToRender(), 0);
  }
  
  private int levelsToRender() {
    float percentHeight = bounds.h / height;
    
    if (percentHeight < .05) {
      return 0;
    }
    return 2;
  }
}

