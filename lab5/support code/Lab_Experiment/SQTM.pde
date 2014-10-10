
// owned by ben
// displays a squarified tree map
class SQTM {
  
  private Rect bounds; 
//  private final Datum root;

  // the current root view being display. takes up the whole bounds
  private View currentView;
  private SQTMDatum currentDatum;

  public SQTM(Rect bounds, SQTMDatum root) {
    this.bounds = bounds;
//    this.root = root;
    this.currentDatum = root;
    //println("datum after layout" + current.datum);
  }
  
  public void setBounds(Rect newBounds) {
    this.bounds = newBounds;
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

