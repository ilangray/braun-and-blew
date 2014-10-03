
// hides the secret for how to render a single frame of the animation from Bar <--> HeightGraph
class BarHeightGA extends GraphAnimator {
  
  private final Bar bar;
  
  public BarHeightGA(Bar bar, float percentStart, float percentEnd) {
    super(bar, percentStart, percentEnd, 2);
    
    // save bar specifically
    this.bar = bar;
  }
  
  private Rect getScaledRect(Rect r, float percent) {
    return r.scale(percent, 1.0f);
  }
  
  protected Graph.DatumView createDatumView(Datum d, Rect r, float percent) {
    return bar.createDatumView(d, getScaledRect(r, percent));
  }
}
