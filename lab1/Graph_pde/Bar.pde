class Bar extends Graph {

   private class BarView extends Graph.DatumView {
    
     private final Rect hitbox; 
     private final boolean hit;
     
     public BarView(Datum d, Rect r) {
       super(d, r);
       
       int newHeight = round((d.value / maxY) * r.h);
       int heightDiff = r.h - newHeight;
       
       hitbox = new Rect(r.x, r.y + heightDiff, r.w, newHeight);
       hit = hitbox.containsPoint(mouseX, mouseY); 
     }
    
     void renderDatum() {
       color fill = hit ? HIGHLIGHTED_FILL : NORMAL_FILL;
       strokeWeight(0);
       drawRect(hitbox, fill, fill);
     }
     
     void renderTooltip() {
       if (hit) {
         String s = "(" + datum.key + ", " + datum.value + ")";
         renderLabel(hitbox, s);
       }
     }
   }
  
   public Bar(ArrayList<Datum> d, String xLabel, String yLabel) {
     super(d, xLabel, yLabel);
   }
   
   protected DatumView createDatumView(Datum datum, Rect bounds) {
     return new BarView(datum, bounds);
   }
}
