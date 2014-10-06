
interface InterpolateHelper {
  boolean shouldInterpolateLeft(int i); 
}

class PathGraph extends Graph {
  public class PathView extends Graph.DatumView {
    
    private Path getPath() {
      return (Path)bounds; 
    }
    
    public PathView(Datum d, Shape s) {
       super(d, s);
    }
    
    public void renderDatum() {
//      color(0,0,0)
      drawPath(getPath(), datum.fillColor, datum.fillColor);
    }
    
    public void renderTooltip() {}
  }
  
  // the graph to approximate with a path
  private final Graph g;
  private final InterpolateHelper interpHelper;
  
  public PathGraph(Graph g, InterpolateHelper interpHelper) {
    super(g.data, g.xLabel, g.yLabel);
    
    this.g = g;
    this.interpHelper = interpHelper;
    
    // must explicitly tell g to create its datum views
    g.createDatumViews();
  }
  
  protected Shape getDatumViewBounds(Datum d, int i, ArrayList<DatumView> previous) {
    DatumView toApprox = g.views.get(i);
    Path approxed = null;
    if (g instanceof Bar) {
      Bar.BarView bv = (Bar.BarView)toApprox;
      Rect bounds = (Rect)bv.hitbox;
      approxed = new Path(bounds, interpHelper.shouldInterpolateLeft(i));
    } 
    else if (g instanceof StackedBar) {
      StackedBar.StackedBarView sbv = (StackedBar.StackedBarView) toApprox;
      Rect bounds = (Rect)sbv.hitbox;
      approxed = new Path(bounds, false);
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
//      approxed = new Path(bounds, false);
      approxed = new Path(bounds, interpHelper.shouldInterpolateLeft(i));
    } 
    else {
      throw new IllegalArgumentException(); 
    }
    
    return approxed;
  }
  
  // returns true iff the Path should interpolate the left side, otherwise the right.
  private boolean shouldInterpolateLeft(int i) {
    return i > data.size() / 2;
  }    
  
  private boolean shouldInterpolateLeft(Wedge w) {
    float midAngle = w.getMiddleAngle();
    
    if (midAngle > HALF_PI && midAngle < PI + HALF_PI) {
      return true;
    }
    return false;
  }
  
  protected DatumView createDatumView(Datum d, Shape s) {
    return new PathView(d, s); 
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
  
  public void setBounds(Rect bounds) {
    super.setBounds(bounds);
    src.setBounds(bounds);
    dest.setBounds(bounds); 
  }
  
  // turns out we actually LIKE rendering the axes here because it gives the viewer some sense of scale/destination
  //protected void renderAxes() {}
  
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
