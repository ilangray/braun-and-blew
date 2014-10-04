
public interface Continuation {
  // invoked when the continuation should run.
  void onContinue();
}

abstract class GraphAnimator extends Graph {
  
  public final Graph g;
  
  private final Interpolator interpolator;  // tracks percentage progress over time
  
  protected Continuation cont;
  private float percent;
  
  protected GraphAnimator(Graph g, float duration, float percentStart, float percentEnd) {
    super(g.data, g.xLabel, g.yLabel);
    
    this.g = g;
    this.interpolator = new Interpolator(duration, percentStart, percentEnd);
  }
  
  GraphAnimator setContinuation(Continuation cont) {
    this.cont = cont;
    return this;
  }
  
  protected void updateCurrentPercent() {
    this.percent = calculateCurrentPercent();
  }
  
  protected void checkIfCompleted() {
    if (percent == interpolator.end) {
      cont.onContinue();
    } 
  }
  
  void render() {
    updateCurrentPercent();
    
    super.render();
   
    checkIfCompleted();
  }
  
  // returns a value in [0,1]
  private float calculateCurrentPercent() {
    float p = interpolator.getInterpolatedValue();  
    return clamp(p, 0, 1);
  }
  
  protected float getCurrentPercent() {
    return percent; 
  }
  
  protected Graph.DatumView createDatumView(Datum d, Shape bounds) {
    return createDatumView(d, bounds, this.percent);
  }
  
  // returns a DatumView with bounds adjusted for the current percent
  protected Graph.DatumView createDatumView(Datum d, Shape bounds, float percent) {
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
  
  protected final Graph.DatumView createDatumView(Datum d, Shape bounds) {
    return current.createDatumView(d, bounds);
  }
}

/**
 * methods for instantiating the correct type of animator based on src/dest graphs
 */
 
// BAR <--> HEIGHTGRAPH
GraphAnimator animate(Bar bg, HeightGraph hg, float duration) {
  return new BarHeightGA(bg, duration, 1, 0);
}
GraphAnimator animate(HeightGraph hb, Bar bg, float duration) {
  return new BarHeightGA(bg, duration, 0, 1);
}

// HEIGHTGRAPH <--> SCATTERPLOT
GraphAnimator animate(HeightGraph hg, Scatterplot scat, float duration) {
  return new HeightScatterGA(hg, duration, 1, 0);
}
GraphAnimator animate(Scatterplot scat, HeightGraph hg, float duration) {
  return new HeightScatterGA(hg, duration, 0, 1);
}

// SCATPLOT <--> LINE
GraphAnimator animate(Scatterplot scat, Line lg, float duration) {
  return new ScatLineGA(lg, duration, 0, 1);
}
GraphAnimator animate(Line lg, Scatterplot scat, float duration) {
  return new ScatLineGA(lg, duration, 1, 0);
}

// MULTIPART ANIMATIONS
GraphAnimator animate(Bar bg, Line lg, Continuation cont) {
  
  HeightGraph hg = new HeightGraph(bg.data, bg.xLabel, bg.yLabel);
  Scatterplot scat = new Scatterplot(bg.data, bg.xLabel, bg.yLabel);
  
  return new GraphSequenceAnimator(makeList(
      animate(bg, hg, 1.0f),
      animate(hg, scat, 1.0f),
      animate(scat, lg, 1.0f)
  )).setContinuation(cont);
}

GraphAnimator animate(Line lg, Bar bg, Continuation cont) {
 
  HeightGraph hg = new HeightGraph(bg.data, bg.xLabel, bg.yLabel);
  Scatterplot scat = new Scatterplot(bg.data, bg.xLabel, bg.yLabel);
  
  return new GraphSequenceAnimator(makeList(
      animate(lg, scat, 1.0f),
      animate(scat, hg, 1.0f),
      animate(hg, bg, 1.0f)
  )).setContinuation(cont);
}
