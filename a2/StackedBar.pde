
class StackedBar extends Graph {

   private class StackedBarView extends Graph.DatumView {
    
     public Rect hitbox; 
     private boolean hit;
     
     private ArrayList<Rect> bars;
     
     public StackedBarView(Datum d, Shape r) {
       super(d, r);
     }
     
     protected Rect getBounds() {
       return (Rect)bounds; 
     }
     
     protected void onBoundsChange() {
       bars = new ArrayList<Rect>();
       
       Rect r = getBounds();
       
       // compute hitbox
       float newHeight = (datum.getTotal() / maxY) * r.h;
       float heightDiff = r.h - newHeight;
       
       hitbox = new Rect(r.x, r.y + heightDiff, r.w, newHeight);
       hit = hitbox.containsPoint(mouseX, mouseY);
       
       // use hitbox as bounds for stacked rects
       for (int i = 0; i < datum.dimensions; i++) {
         float start = getPreviousEnd();
         float hght = datum.values.get(i) / datum.getTotal() * hitbox.h;
         
         Rect segment = new Rect(hitbox.x, start - hght, hitbox.w, hght);
         bars.add(segment);
       }
       
     }
     
     private float getPreviousEnd() {
       if (bars.isEmpty()) {
         return getBounds().getMaxY();
       } else {
         return bars.get(bars.size() - 1).getMinY();
       } 
     }
     
     /*
     protected void onBoundsChange() {
       Rect r = (Rect)bounds;
       Datum d = datum;
       
       float newHeight = (d.getTotal() / maxY) * r.h;
       float heightDiff = r.h - newHeight;
       
       hitbox = new Rect(r.x, r.y + heightDiff, r.w, newHeight);
       hit = hitbox.containsPoint(mouseX, mouseY); 
     }*/
    
     void renderDatum() {
       color fill = hit ? HIGHLIGHTED_FILL : datum.fillColor;
       strokeWeight(0);
       
       for (int i = 0; i < bars.size(); i++) {
         Rect r = bars.get(i);
         
         if (i > 0) {
           strokeWeight(5);
           stroke(color(0, 0, 0));
           drawLine(r.getLL(), r.getLR());
         }
         
         strokeWeight(0);
         drawRect(r, fill, fill); 
        // yolo look here for when the strokes elsewhere fuck up. 
       }
     }
     
     void renderTooltip() {
       if (hit) {
         String s = "(" + datum.key + ", " + datum.getTotal() + ")";
         renderLabel(hitbox, s);
       }
     }
   }
  
   public StackedBar(CSVData data) {
     super(data);
   }
  
   public StackedBar(ArrayList<Datum> d, String xLabel, String yLabel) {
     super(d, xLabel, yLabel);
   }
   
   protected DatumView createDatumView(Datum datum, Shape bounds) {
     return new StackedBarView(datum, bounds);
   }
   
   protected float getMaxY() {
     if (data.isEmpty()) {
       return 0; 
     }
     
     float max = data.get(0).value;
   
     for (int i = 1; i < data.size(); i++) {
       float v = data.get(i).getTotal();
       if (v > max) {
         max = v;
       }  
     }
   
     return max; 
  }
}
