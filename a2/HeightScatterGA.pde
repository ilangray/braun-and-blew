
// hides the secret for how to render a single frame of the animation from Bar <--> HeightGraph
class HeightScatterGA extends GraphAnimator {
  
  private final HeightGraph hg;
  
  public HeightScatterGA(HeightGraph hg, float percentStart, float percentEnd) {
    super(hg, percentStart, percentEnd, 1);
    
    // save bar specifically
    this.hg = hg;
  }
  
  private Rect getScaledRect(Rect r, float percent) {
    return new Rect(r.x, r.y, r.w, r.h * percent);
  }
  
  protected Graph.DatumView createDatumView(Datum d, Rect r, float percent) {
    HeightGraph.HeightView dv = (HeightGraph.HeightView) hg.createDatumView(d, r);
    
    float y = percent * (dv.bottom.y - dv.top.y) + dv.top.y;
    dv.bottom = new Point(dv.bottom.x, y); 
    
    return dv;
  }
}
