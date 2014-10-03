
public interface Continuation {
  // invoked when the continuation should run.
  void onContinue();
}

abstract class GraphAnimator extends Graph {
  
  public final Graph g;
  
  private final Interpolator interpolator;  // tracks percentage progress over time
  
  protected Continuation cont;
  private float percent;
  
  protected GraphAnimator(Graph g, float percentStart, float percentEnd, float duration) {
    super(g.data, g.xLabel, g.yLabel);
    
    this.g = g;
    this.interpolator = new Interpolator(duration, percentStart, percentEnd);
  }
  
  GraphAnimator setContinuation(Continuation cont) {
    this.cont = cont;
    return this;
  }
  
  void render() {
    // calculate current percent
    this.percent = calculateCurrentPercent();
    
    super.render();
    
    if (percent == interpolator.end) {
      cont.onContinue();
    }
  }
  
  // returns a value in [0,1]
  private float calculateCurrentPercent() {
    float p = interpolator.getInterpolatedValue();
    
//    println("percent progress = " + percent);
    
    return clamp(p, 0, 1);
  }
  
  protected Graph.DatumView createDatumView(Datum d, Rect bounds) {
    return createDatumView(d, bounds, this.percent);
  }
  
  // returns a DatumView with bounds adjusted for the current percent
  protected Graph.DatumView createDatumView(Datum d, Rect bounds, float percent) {
    return null; 
  }
}

class GraphSequenceAnimator extends GraphAnimator {
  
  private final ArrayList<GraphAnimator> animators;
  
  private GraphAnimator current;
  
  // no empty lists please
  public GraphSequenceAnimator(ArrayList<GraphAnimator> animators) {
    super(animators.get(0).g, 0, 0, 0); // haaacky
    
    this.animators = animators;
    
    setCurrent(0);
  } 
  
  private void setCurrent(final int i) {
    // base case
    if (i >= animators.size()) {
      cont.onContinue();
      return; 
    }
    
    // move to the next animator
    current = animators.get(i);
    current.interpolator.start();
    current.setContinuation(new Continuation() {
      public void onContinue() {
        setCurrent(i+1); 
      }
    });
  }
  
  public void render() {
    current.render(); 
  }
  
  protected final Graph.DatumView createDatumView(Datum d, Rect bounds) {
    return current.createDatumView(d, bounds);
  }
}

/**
 * methods for instantiating the correct type of animator based on src/dest graphs
 */
 
// BAR <--> HEIGHTGRAPH
Graph animate(Bar bg, HeightGraph hg, Continuation cont) {
  GraphAnimator g = new BarHeightGA(bg, 1, 0).setContinuation(cont);
  g.interpolator.start();
  return g;
}
Graph animate(HeightGraph hb, Bar bg, Continuation cont) {
  GraphAnimator anim = new BarHeightGA(bg, 0, 1).setContinuation(cont);
  
  GraphAnimator seq = new GraphSequenceAnimator(makeList(anim));
  seq.setContinuation(cont);
  return seq;
}

// HEIGHTGRAPH <--> SCATTERPLOT
GraphAnimator animate(HeightGraph hg, Scatterplot scat, Continuation cont) {
  return new HeightScatterGA(hg, 1, 0).setContinuation(cont);
}
GraphAnimator animate(Scatterplot scat, HeightGraph hg, Continuation cont) {
  return new HeightScatterGA(hg, 0, 1).setContinuation(cont);
}

// MULTIPART ANIMATIONS
GraphAnimator animate(Bar bg, Scatterplot scat, Continuation cont) {
  
  HeightGraph hg = new HeightGraph(bg.data, bg.xLabel, bg.yLabel);
  
  return new GraphSequenceAnimator(makeList(
      (GraphAnimator)animate(bg, hg, null),
      (GraphAnimator)animate(hg, scat, null)
  )).setContinuation(cont);
}

GraphAnimator animate(Scatterplot scat, Bar bg, Continuation cont) {
  
  HeightGraph hg = new HeightGraph(bg.data, bg.xLabel, bg.yLabel);
  
  return new GraphSequenceAnimator(makeList(
      (GraphAnimator)animate(scat, hg, null),
      (GraphAnimator)animate(hg, bg, null)
  )).setContinuation(cont);
}
