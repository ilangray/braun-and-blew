
class PathGraph extends Graph {
  public class PathView extends Graph.DatumView {
    
    private Path getPath() {
      return (Path)bounds; 
    }
    
    public PathView(Datum d, Shape s) {
       super(d, s);
    }
    
    public void renderDatum() {
      drawPath(getPath(), color(0, 0, 0), color(255,255,255));
    }
    
    public void renderTooltip() {}
  }
  
  // the graph to approximate with a path
  private final Graph g;
  
  public PathGraph(Graph g) {
    super(g.data, g.xLabel, g.yLabel);
    
    assert g != null;
    
    println("setting g = " + g);
    
    this.g = g;
    
    // must explicitly tell g to create its datum views
    g.createDatumViews();
  }
  
  protected Shape getDatumViewBounds(Datum d, int i, ArrayList<DatumView> previous) {
    DatumView toApprox = g.views.get(i);
    Path approxed = null;
    if (g instanceof Bar) {
      Bar.BarView bv = (Bar.BarView)toApprox;
      Rect bounds = (Rect)bv.hitbox;
      approxed = new Path(bounds, shouldInterpolateLeft(i));
    } 
    else if (g instanceof PieChart) {
      Wedge bounds = (Wedge)toApprox.bounds;
      approxed = new Path(bounds);
    } 
    else if (g instanceof HeightGraph) {
      HeightGraph.HeightView dv = (HeightGraph.HeightView)toApprox;
      
      Point top = dv.top;
      Point bottom = dv.bottom;
      
      Rect bounds = new Rect(top.x, top.y, 0, bottom.y - top.y);
      approxed = new Path(bounds, shouldInterpolateLeft(i));
    } 
    else {
      throw new IllegalArgumentException(); 
    }
    
    return approxed;
  }
  
  // returns true iff the Path should interpolate the left side, otherwise the right.
  private boolean shouldInterpolateLeft(int i) {
    return i < data.size() / 2;
  }    

  
  protected DatumView createDatumView(Datum d, Shape s) {
    return new PathView(d, s); 
  }
  
  protected void renderAxes() {
    g.renderAxes();
  }
}

class PathGA extends GraphAnimator {

  private final PathGraph src;
  private final PathGraph dest;
  
  // src and dest must be either: PieChart, BarGraph, HeightGraph
  public PathGA(PathGraph src, PathGraph dest, float duration, float percentStart, float percentEnd) {
    super(src, duration, percentStart, percentEnd);
    
    this.src = src;
    this.dest = dest;
    
    src.createDatumViews();
    dest.createDatumViews();
  }
  
  protected Graph.DatumView createDatumView(Datum d, Shape r, float percent) {
    int i = src.data.indexOf(d);
    assert i >= 0;

    // interpolate between src and dest
    Path srcPath = (Path)src.views.get(i).bounds;
    Path destPath = (Path)dest.views.get(i).bounds;
    
    Path lerped = srcPath.lerpBetween(destPath, percent);
    return src.new PathView(d, lerped);
  }
}
