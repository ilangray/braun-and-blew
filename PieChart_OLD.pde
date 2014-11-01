
import java.util.*;

class PieChart_OLD extends AbstractView {
  
  class PieChartView {

    private Wedge wedge;
    private Datum datum;


    private Shape bounds;
  
    public PieChartView(Datum d, Shape s) {
      this.datum = d;
      this.bounds = s;
    }

    void renderDatum() {
      renderWedge();
      labelWedge(); 
    }

    private void renderWedge() {
      Wedge w = getWedge();

      stroke(color(0,0,0));
      ellipseMode(RADIUS);
      fill(color(255, 255, 255));

      float start = Math.min(w.startAngle, w.endAngle);
      float end = Math.max(w.startAngle, w.endAngle);

      // fill(datum.fillColor);
      // one problem at a time
      fill(color(Math.round(255 * Math.random()), 124, 124));
      arc(w.center.x, w.center.y, w.radius, w.radius, start, end, PIE);
    }

    private void labelWedge() {
      Wedge w = getWedge();

      float angle = w.getMiddleAngle();
      float radius = w.radius * 1.15;

      float x = w.center.x + radius * cos(angle);
      float y = w.center.y + radius * sin(angle);

      textSize(15);
      fill(color(0,0,0));
      textAlign(CENTER);
      text(datum.getValue(property), x, y);
    }

    public Wedge getWedge() {
      return (Wedge)bounds; 
    }

    // actually renders a little label for each slice of the pie
    void renderTooltip() {
      if (getWedge().containsPoint(new Point(mouseX, mouseY))) {
        String s = "(" + property + ", " + datum.getValue(property) + ")";
        renderLabel(mouseX, mouseY - 10, s);
      }
    }
  }
  
  // the property by which to group/count the data
  private final String property;

  // maps values of the property to the number of times that value occurs
  private final Map<String, Integer> counts;

  // the values of the property
  private final ArrayList<String> labels;

  public PieChart_OLD(ArrayList<Datum> data, String property) {
    super(data);

    this.property = property;
    this.counts = countByValue(data, property);
    this.labels = new ArrayList<String>(counts.keySet());
  }

  // returns a mapping of values of the given property to the number of times
  // that value occurs.
  private Map<String, Integer> countByValue(ArrayList<Datum> data, String property) {
    Map<String, Integer> counts = new HashMap<String, Integer>();

    for (Datum datum : data) {
      String value = datum.getValue(property);

      // increment the count
      Integer count = counts.get(value);
      if (count == null) {
        count = 0;
      }
      count++;

      // associate new count with value
      counts.put(value, count);
    }

    return counts;
  }

  protected Shape getDatumViewBounds(Datum d, int i, ArrayList<DatumView> previous) {
    Point center = getBounds().getCenter();  
    float radius = Math.min(getBounds().w, getBounds().h) * 1.0f/3;
   
    float angleRange = TWO_PI * d.value / totalY;
    
    float startAngle = lastEndingAngle(previous);
    float endAngle = startAngle - angleRange;
    
    return new Wedge(center, radius, startAngle, endAngle);
  }
  
  private float lastEndingAngle(ArrayList<DatumView> vs) {
    if (vs.isEmpty()) {
      return PI + HALF_PI; 
    }
    
    PieChartView pcv = (PieChartView) vs.get(vs.size() - 1);
    return pcv.getWedge().endAngle;
  }
  
  // lets the subclass determines the type of DatumView used
  private DatumView createDatumView(Datum data, Shape bounds) {
    return new PieChartView(data, bounds);
  }

  // HOVERING
  public ArrayList<Datum> getHoveredDatums() {
    return null;
  }

  // RENDERING 
  public void render() {
    ArrayList<Wedge> wedges = createWedges();
    renderWedges(wedges);
    labelWedges(wedges);

    // createDatumViews();
    // renderDatums();
    // renderTooltips();
  }

  // returns an array of Wedges parallel to the array of labels
  private ArrayList<Wedge> createWedges() {
    ArrayList<Wedge> wedges = new ArrayList<Wedge>();

    for (String label : labels) {
      // make the wedge for the label

      Wedge w = null;
      wedges.add(w);
    }

    return wedges;
  }

  private void renderWedge(Wedge w) {
    stroke(color(0,0,0));
    ellipseMode(RADIUS);
    fill(color(255, 255, 255));

    float start = Math.min(w.startAngle, w.endAngle);
    float end = Math.max(w.startAngle, w.endAngle);

    // fill(datum.fillColor);
    // one problem at a time
    fill(color(Math.round(255 * Math.random()), 124, 124));
    arc(w.center.x, w.center.y, w.radius, w.radius, start, end, PIE);
  }





  
  private void createDatumViews() {
    views = new ArrayList<DatumView>(data.size());
    
    for (int i = 0; i < data.size(); i++) {   
      Datum d = data.get(i);
      views.add(createDatumView(d, getDatumViewBounds(d, i, views)));
    }
  }

  // returns the bounds for the given datum view
  private Shape getDatumViewBounds(Datum d, int i, ArrayList<DatumView> previous) {
    float uw = (getLR().x - getO().x) / data.size();
    float uh = getUL().y - getO().x;
    float y = getUL().y;
    float bottomY = getLR().y - THICKNESS / 2;
    
    float x = i * uw + getO().x;
    float nextX = (i + 1) * uw + getO().x;
    return new Rect(new Point(x, y), new Point(nextX, bottomY)).scale(0.5, 1);
  }

  private void renderDatums() {
    if (views == null) {
      return;
    }
    
    for (int i = 0; i < data.size(); i++) {
      DatumView dv = views.get(i);
      if (dv != null) {
        dv.renderDatum(); 
      }
    }
  }
  
  private void renderTooltips() {
    for (int i = 0; i < data.size(); i++) {
      DatumView dv = views.get(i);
      if (dv != null) {
        dv.renderTooltip(); 
      }
    }
  }
}