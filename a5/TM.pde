
// owned by ben
// displays a squarified tree map
class TM {
  
  private final Rect bounds; 
//  private final Datum root;
  
  // the current root view being display. takes up the whole bounds
  private View currentView;
  private SQTMDatum currentDatum;

  public TM(Rect bounds, SQTMDatum root) {
    this.bounds = bounds;
    this.currentDatum = root;
  }
  
  // calls render on the root view
  public void render() {
   currentView = new RLayout(currentDatum, bounds).solve();
   currentView.render(1, 0);
  }
}

