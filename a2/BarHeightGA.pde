
// hides the secret for how to render a single frame of the animation from Bar <--> HeightGraph
class BarHeightGA extends GraphAnimator {
  
  private final Bar bar;
  
  public BarHeightGA(Bar bar, float duration, float percentStart, float percentEnd) {
    super(bar, duration, percentStart, percentEnd);
    
    // save bar specifically
    this.bar = bar;
  }
  
  private Rect getScaledRect(Rect r, float percent) {
    return r.scale(percent, 1.0f);
  }
  
  protected Graph.DatumView createDatumView(Datum d, Shape r, float percent) {
    return bar.createDatumView(d, getScaledRect((Rect)r, percent));
  }
}
