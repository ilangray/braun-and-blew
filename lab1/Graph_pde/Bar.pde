class Bar extends Graph {
 
   private final color HIGHLIGHTED_FILL = color(237, 119, 0);
   private final color NORMAL_FILL = color(0, 0, 200);
  
   public Bar(ArrayList<Datum> d, String xLabel, String yLabel) {
     super(d, xLabel, yLabel);
   }
   
   public void renderDatum(Datum d, Rect boundingRect) {
     // Since rect draws from top left, we need to move the rectangle
     // down to the axis - use heightDiff
     int newHeight = round((d.value / maxY) * boundingRect.h);
     int heightDiff = boundingRect.h - newHeight;
     
     // detect mouseover
     Rect hitbox = new Rect(boundingRect.x, boundingRect.y + heightDiff, boundingRect.w, newHeight);
     boolean hit = hitbox.containsPoint(mouseX, mouseY);
     
     color fill = hit ? HIGHLIGHTED_FILL : NORMAL_FILL;
     
     drawRect(hitbox, WHITE, fill);
    
     if (hit) {
       // add the label
       renderLabel(hitbox, "(" + d.key + ", " + d.value + ")");
     }
   }
   
   public void renderTooltip(Datum d, Rect boundingRect) {
     
   }
}
