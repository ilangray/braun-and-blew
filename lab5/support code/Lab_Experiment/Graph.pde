/* Graph abstract class */

class Datum {
  public final String key = "";
  public final float value;
  public final boolean marked;
  public final int fillColor = color(0, 255, 255);

  public Datum(Data.DataPoint dp) {
    this.value = dp.value; 
    marked = dp.marked;
  }
}

public abstract class Graph {
  public abstract class DatumView {
    protected final Datum datum;
    protected Shape bounds;
    
    public DatumView(Datum datum, Shape bounds) {
      this.datum = datum;
      setBounds(bounds);
    }
    
    public final void setBounds(Shape bounds) {
      this.bounds = bounds;
      onBoundsChange();
    }
    
    // called when bounds change. override for fun
    protected void onBoundsChange() {}
    
    public abstract void renderDatum();
  
    // renders the tooltip if 
    public abstract void renderTooltip(); 
  }
  protected final color HIGHLIGHTED_FILL = color(237, 119, 0);
  protected final color NORMAL_FILL = color(0, 0, 200);
  protected final ArrayList<Datum> data;
  protected ArrayList<DatumView> views;
 
  public final color BLACK = color(0, 0, 0);
  public final color WHITE = color(255, 255, 255);
   
  public static final float PADDING_LEFT = 0.15;
  public static final float PADDING_RIGHT = 0.15;
  public static final float PADDING_TOP = 0.1;
  public static final float PADDING_BOTTOM = 0.2;
  
  public final float LABEL_PERCENT_FONT_SIZE = 0.03;
  public final float LABEL_PERCENT_OFFSET = 0.5 * LABEL_PERCENT_FONT_SIZE;
  
  public final int THICKNESS = 3;
  public final int TICK_THICKNESS = 1;
  public final int TICK_WIDTH = 10;
  public final int TARGET_NUMBER_OF_TICKS = 11; 
  
  public final float AXIS_NAME_PERCENT_FONT_SIZE = 0.05;
  public final float AXIS_LABEL_PERCENT_FONT_SIZE = AXIS_NAME_PERCENT_FONT_SIZE / 1.5;
  
  public final int tickCount;
  public final float maxY;
  
  public final String xLabel;
  public final String yLabel;
  
  // where the graph should draw itself
  private Rect bounds;
  
  public Graph() {
    this(new ArrayList<Datum>(), "", ""); 
  }
  
  public Graph(ArrayList<Datum> data, String xLabel, String yLabel) {
    this.data = data;    
    this.xLabel = xLabel;
    this.yLabel = yLabel;
    
    // compute the scale for the Y axis
    float actualMaxY = getMaxY(); 
    int interval = (int)(actualMaxY / TARGET_NUMBER_OF_TICKS);
    if (interval == 0) {
      interval++;
    }
    
    // tickCount = how many times you need to add interval to get something >= actualMaxY
    int ticks = TARGET_NUMBER_OF_TICKS;
    while (ticks * interval < actualMaxY) {
      ticks++;
    }
    tickCount = ticks;
    maxY = tickCount * interval;
  }
  
  public void setBounds(Rect bounds) {
    this.bounds = bounds; 
  }
  
  public void setBounds(Graph g) {
    setBounds(g.getBounds()); 
  }
  
  public Rect getBounds() {
    return bounds; 
  }
  
  public void render() {
    renderAxes();
    renderContents();
  }
  
  protected void renderAxes() {
    drawAxes();
    labelAxes(); 
  }
  
  protected void renderContents() {
    createDatumViews();
    renderDatums();
    renderTooltips();
  }
  
  protected void createDatumViews() {
    views = new ArrayList<DatumView>(data.size());
    
    for (int i = 0; i < data.size(); i++) {   
      Datum d = data.get(i);
      views.add(createDatumView(d, getDatumViewBounds(d, i, views)));
    }
  }
  
  // returns the bounds for the given datum view
  protected Shape getDatumViewBounds(Datum d, int i, ArrayList<DatumView> previous) {
    float uw = (getLR().x - getO().x) / data.size();
    float uh = getUL().y - getO().x;
    float y = getUL().y;
    float bottomY = getLR().y - THICKNESS / 2;
    
    float x = i * uw + getO().x;
    float nextX = (i + 1) * uw + getO().x;
    return new Rect(new Point(x, y), new Point(nextX, bottomY)).scale(0.5, 1);
  }
  
  // lets the subclass determines the type of DatumView used
  protected abstract DatumView createDatumView(Datum data, Shape bounds);
  
  protected void renderDatums() {
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
  
  protected void renderTooltips() {
    for (int i = 0; i < data.size(); i++) {
      DatumView dv = views.get(i);
      if (dv != null) {
        dv.renderTooltip(); 
      }
    }
  }

  private void drawAxes() {
       stroke(color(0, 0, 0));
       drawLine(getO(), getLR(), THICKNESS);
       drawLine(getO(), getUL(), THICKNESS);
  }
  
  private Point getO() {
    if (bounds == null) {  
      println("Bounds is null on graph = " + this); 
    }
    return new Point(bounds.x + bounds.w * PADDING_LEFT, bounds.y + bounds.h * (1 - PADDING_BOTTOM));
  }
  
  private Point getLR() {
    if (bounds == null) {
      println("Bounds is null on graph = " + this); 
    }
    return new Point(bounds.x + bounds.w * (1 - PADDING_RIGHT), bounds.y + bounds.h * (1 - PADDING_BOTTOM));
  }
  
  private Point getUL() {
    return new Point(bounds.x + bounds.w * PADDING_LEFT, bounds.y + bounds.h * PADDING_TOP);
  }
  
  private void drawLine(Point p, Point q, int thickness) {
      strokeWeight(thickness);
      line(p.x, p.y, q.x, q.y);
  } 
  
  private void labelAxes() {
    labelX();
    labelY();
  }
  
  private void labelX() {
    float lineLength = getLR().x - getO().x;
    float intWidth = round(lineLength / float(data.size()));
    float y = getO().y + 10;
    
    percentTextSize(AXIS_LABEL_PERCENT_FONT_SIZE);
    for (int i = 0; i < data.size(); i++) {
      float x = getO().x + i * intWidth + intWidth / 2;  // Find right location for label
      fill(0);
      textAlign(RIGHT, CENTER);
      verticalText(data.get(i).key, x, y);
    }
    
    // draw the axis name
    float x = (1 - PADDING_RIGHT / 2) * bounds.w + bounds.x;
    textAlign(CENTER, CENTER);
    percentTextSize(AXIS_NAME_PERCENT_FONT_SIZE);
    text(xLabel, x, getO().y);
  }
  
  // stolen from: http://forum.processing.org/one/topic/vertical-text.html
  private void verticalText(String s, float x, float y) {
    pushMatrix();
    translate(x, y);
    rotate(-HALF_PI/2);
    text(s, 0, 0);
    popMatrix();
  }
  
  private void labelY() {
    float lineHeight = getO().y - getUL().y;
    float intHeight = round(lineHeight / float(tickCount));
    
    // the center of the tick marks
    float x = getO().x;
    float offset = TICK_WIDTH / 2;
    
    percentTextSize(AXIS_LABEL_PERCENT_FONT_SIZE);
    for (int i = 0; i < tickCount; i++) {
      float y = getUL().y + i * intHeight;
      
      // draw the tick mark
      fill(0);
      strokeWeight(TICK_THICKNESS);
      line(x - offset, y, x + offset, y);
     
      // label the tick mark
      // value is NOT pixels, its in the units of the Y axis
      int value = round(maxY - i * (maxY / tickCount));
      
      textAlign(RIGHT, CENTER);
      text(value, x - offset - 2, y);
    }
    
    // draw the axis name
    float y = PADDING_TOP / 2 * bounds.h + bounds.y;
    textAlign(CENTER, CENTER);
    percentTextSize(AXIS_NAME_PERCENT_FONT_SIZE);
    text(yLabel, x, y);
  }
  
  protected void percentTextSize(float percent) {
    textSize(percent * bounds.h);
  }
  
  protected void drawRect(Rect r, color stroke, color fill) {
     stroke(stroke);
     fill(fill);
     rect(r.x, r.y, r.w, r.h);
  }
  
  // renders the given string as a label above the hitbox
  protected void renderLabel(Rect hitbox, String s) {
     float x = hitbox.getCenter().x;
     float y = hitbox.getMinY() - LABEL_PERCENT_OFFSET * bounds.h;
    
     // set font size because text measurements depend on it
     percentTextSize(LABEL_PERCENT_FONT_SIZE);
     
     // bounding rectangle
     float w = textWidth(s) * 1.1;
     float h = LABEL_PERCENT_FONT_SIZE * bounds.h * 1.3;
     Rect r = new Rect(x - w/2, y - h, w, h);
     drawRect(r, BLACK, WHITE);
     
     // text 
     textAlign(CENTER, BOTTOM);
     fill(BLACK);
     text(s, x, y);
   }
   
   protected void renderLabel(float x, float y, String s) {
     // set font size because text measurements depend on it
     percentTextSize(LABEL_PERCENT_FONT_SIZE);
     
     // bounding rectangle
     float w = textWidth(s) * 1.1;
     float h = LABEL_PERCENT_FONT_SIZE * bounds.h * 1.3;
     Rect r = new Rect(x - w/2, y - h, w, h);
     drawRect(r, BLACK, WHITE);
     
     // text 
     textAlign(CENTER, BOTTOM);
     fill(BLACK);
     text(s, x, y);
   }
   
   protected float getMaxY() {
     if (data.isEmpty()) {
       return 0; 
     }
     
     float max = data.get(0).value;
   
     for (int i = 1; i < data.size(); i++) {
       float v = data.get(i).value;
       if (v > max) {
         max = v;
       }  
     }
   
     return max; 
  }
}

