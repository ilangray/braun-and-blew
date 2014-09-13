/* Graph abstract class */

class Datum {
  public final String key;
  public final float value;
  
  public Datum(String key, float value) {
    this.key = key;
    this.value = value;
  }
}

abstract class Graph {
  private final ArrayList<Datum> data;
 
  public final color BLACK = color(0, 0, 0);
  public final color WHITE = color(255, 255, 255);
   
//  public final float PERCENT_PADDING = 0.15;
  public final float PADDING_LEFT = 0.15;
  public final float PADDING_RIGHT = 0.15;
  public final float PADDING_TOP = 0.1;
  public final float PADDING_BOTTOM = 0.2;
  
  public final float LABEL_PERCENT_FONT_SIZE = 0.03;
  public final float LABEL_PERCENT_OFFSET = 0.5 * LABEL_PERCENT_FONT_SIZE;
  
  public final int THICKNESS = 3;
  public final int TICK_THICKNESS = 1;
  public final int TICK_WIDTH = 10;
  public final int NUMBER_OF_TICKS = 10; 
  
  public final float AXIS_NAME_PERCENT_FONT_SIZE = 0.05;
  public final float AXIS_LABEL_PERCENT_FONT_SIZE = AXIS_NAME_PERCENT_FONT_SIZE / 1.5;
  
  public final float maxY;
  public final String xLabel;
  public final String yLabel;
  
  // TODO: label X axis *below* the axis, not to the right, so that there is more room for long names
  public Graph(ArrayList<Datum> data, String xLabel, String yLabel) {
    this.data = data;
    maxY = getMaxY();
    
    this.xLabel = xLabel;
    this.yLabel = yLabel;
  }
  
  public void render() {
    renderDatums();
    drawAxes();
    labelAxes();
    
  }
  
  private float getMaxY() {
    float max = data.get(0).value;
   
    for (int i = 1; i < data.size(); i++) {
      float v = data.get(i).value;
      if (v > max) {
        max = v;
      } 
    }
   
    return max; 
  }
  
  private void renderDatums() {
    int uw = (getLR().x - getO().x) / data.size();
    int uh = getUL().y - getO().x;
    int y = getUL().y;
    int bottomY = getLR().y;
    
    for (int i = 0; i < data.size(); i++) {
      int x = i * uw + getO().x;
      int nextX = (i + 1) * uw + getO().x;
      Rect bounds = new Rect(new Point(x, y), new Point(nextX, bottomY));
      Rect adjustedBounds = bounds.scale(0.5, 1);
      renderDatum(data.get(i), adjustedBounds);
    }
  }
  
  private void drawAxes() {
       stroke(color(0, 0, 0));
       drawLine(getO(), getLR(), THICKNESS);
       drawLine(getO(), getUL(), THICKNESS);
  }
  
  private Point getO() {
    return new Point(width * PADDING_LEFT, height * (1 - PADDING_BOTTOM));
  }
  
  private Point getLR() {
    return new Point(width * (1 - PADDING_RIGHT), height * (1 - PADDING_BOTTOM));
  }
  
  private Point getUL() {
    return new Point(width * PADDING_LEFT, height * PADDING_TOP);
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
    int lineLength = getLR().x - getO().x;
    int intWidth = round(lineLength / float(data.size()));
    int y = getO().y + 10;
    
    percentTextSize(AXIS_LABEL_PERCENT_FONT_SIZE);
    for (int i = 0; i < data.size(); i++) {
      int x = getO().x + i * intWidth + intWidth / 2;  // Find right location for label
      fill(0);
      textAlign(RIGHT, CENTER);
      verticalText(data.get(i).key, x, y);
    }
    
    // draw the axis name
    float x = (1 - PADDING_RIGHT / 2) * width;
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
    int lineHeight = getO().y - getUL().y;
    int intHeight = round(lineHeight / float(NUMBER_OF_TICKS));
    
    // the center of the tick marks
    int x = getO().x;
    int offset = TICK_WIDTH / 2;
    
    percentTextSize(AXIS_LABEL_PERCENT_FONT_SIZE);
    for (int i = 0; i < NUMBER_OF_TICKS; i++) {
      int y = getUL().y + i * intHeight;
      
      // draw the tick mark
      fill(0);
      strokeWeight(TICK_THICKNESS);
      line(x - offset, y, x + offset, y);
     
      // label the tick mark
      // value is NOT pixels, its in the units of the Y axis
      int value = round(maxY - i * (maxY / NUMBER_OF_TICKS));
      
      textAlign(RIGHT, CENTER);
      text(value, x - offset - 2, y);
    }
    
    // draw the axis name
    float y = PADDING_TOP / 2 * height;
    textAlign(CENTER, CENTER);
    percentTextSize(AXIS_NAME_PERCENT_FONT_SIZE);
    text(yLabel, x, y);
  }
  
  protected void percentTextSize(float percent) {
    textSize(percent * height);
  }
  
  // override to render a single datum
  protected abstract void renderDatum(Datum d, Rect boundingRect);
  
  protected void drawRect(Rect r, color stroke, color fill) {
     stroke(stroke);
     fill(fill);
     rect(r.x, r.y, r.w, r.h);
  }
  
  // renders the given string as a label above the hitbox
  protected void renderLabel(Rect hitbox, String s) {
     int x = hitbox.getCenter().x;
     float y = hitbox.getMinY() - LABEL_PERCENT_OFFSET * height;
    
     // set font size because text measurements depend on it
     percentTextSize(LABEL_PERCENT_FONT_SIZE);
     
     // bounding rectangle
     float w = textWidth(s) * 1.1;
     float h = LABEL_PERCENT_FONT_SIZE * height * 1.3;
     Rect r = new Rect(x - w/2, y - h, w, h);
     drawRect(r, BLACK, WHITE);
     
     // text 
     textAlign(CENTER, BOTTOM);
     fill(BLACK);
     text(s, x, y);
   }
}
