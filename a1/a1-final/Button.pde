
// An instance of this class represents a button.
class Button {
  public Rect frame;
  public color background;
  public color textColor;
  public String title;
  
  public float PERCENT_PADDING = 0.1;
  
  // black text, white background
  public Button(Rect frame, String text) {
    this(frame, color(255, 255, 255), text, color(0, 0, 0));
  }
  
  public Button(Rect frame, color background, String text, color textColor) {
    this.frame = frame;
    this.background = background;
    this.title = text;
    this.textColor = textColor;
  }

  public void render() { 
    fill(background);
    rect(frame.x, frame.y, frame.w, frame.h); 
   
    textAlign(CENTER, CENTER);
    textSize(calculateMaximumTextSize());
    fill(textColor);
    text(title, frame.w / 2 + frame.x, frame.h / 2 + frame.y);
  }
  
  private float calculateMaximumTextSize() {
    float h = frame.h * (1 - 2 * PERCENT_PADDING);
    float w = frame.w * (1 - 2 * PERCENT_PADDING);
    
    textSize(h);
    float textWidth = textWidth(title);
    
    // sometimes, you just get lucky
    if (textWidth <= w) {
      return h;
    }
    
    // ugggh
    float ratio = w / textWidth;
    return h * ratio;
  }
}
