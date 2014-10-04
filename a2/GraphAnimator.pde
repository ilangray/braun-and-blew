
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

// PIECHART <--> HEIGHTGRAPH
GraphAnimator animate(PieChart pc, HeightGraph hg, float duration) {
  final PathGraph pcApprox = new PathGraph(pc, null);
  final PathGraph hgApprox = new PathGraph(hg, getInterpolateHelper(pc));
  
  return new PathGA(pcApprox, hgApprox, 5, 0, 1);
}
GraphAnimator animate(HeightGraph hg, PieChart pc, float duration) {
  final PathGraph pcApprox = new PathGraph(pc, null);
  final PathGraph hgApprox = new PathGraph(hg, getInterpolateHelper(pc));
  
  return new PathGA(pcApprox, hgApprox, 5, 1, 0);
}

// Bar <--> Line
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

// Pie <--> Bar (direct)
GraphAnimator animate(PieChart pc, Bar bg, Continuation cont) {
  
  final PathGraph pcApprox = new PathGraph(pc, null);
  final PathGraph bgApprox = new PathGraph(bg, getInterpolateHelper(pc));
  
  return new GraphSequenceAnimator(makeList(
      (GraphAnimator)new PathGA(pcApprox, bgApprox, 5, 0, 1)
  )).setContinuation(cont);
  
}
GraphAnimator animate(Bar bg, PieChart pc, Continuation cont) {
  
  final PathGraph pcApprox = new PathGraph(pc, null);
  final PathGraph bgApprox = new PathGraph(bg, getInterpolateHelper(pc));
  
  return new GraphSequenceAnimator(makeList(
      (GraphAnimator)new PathGA(bgApprox, pcApprox, 5, 0, 1)
  )).setContinuation(cont);
  
}

// Pie <--> Line (through Height <--> Scatterplot)
GraphAnimator animate(PieChart pc, Line lg, Continuation cont) {
 
  HeightGraph hg = new HeightGraph(pc.data, pc.xLabel, pc.yLabel);
  Scatterplot scat = new Scatterplot(pc.data, pc.xLabel, pc.yLabel);
  
  return new GraphSequenceAnimator(makeList(
      animate(pc, hg, 1.0f),
      animate(hg, scat, 1.0f),
      animate(scat, lg, 1.0f)
  )).setContinuation(cont);
}
GraphAnimator animate(Line lg, PieChart pc, Continuation cont) {
  
  HeightGraph hg = new HeightGraph(pc.data, pc.xLabel, pc.yLabel);
  Scatterplot scat = new Scatterplot(pc.data, pc.xLabel, pc.yLabel);
  
  return new GraphSequenceAnimator(makeList(
      animate(lg, scat, 1.0f),
      animate(scat, hg, 1.0f),
      animate(hg, pc, 1.0f)
  )).setContinuation(cont);
}

InterpolateHelper getInterpolateHelper(final PieChart pc) {
  return new InterpolateHelper() {
    public boolean shouldInterpolateLeft(int i) {
      Wedge w = (Wedge)pc.views.get(i).bounds;
      float mid = w.getMiddleAngle();
      
      return mid > HALF_PI && mid < PI + HALF_PI;
    } 
  };
}
