class Bar extends Graph {

   private class BarView extends Graph.DatumView {
    
     private Rect hitbox; 
     private boolean hit;
     
     public BarView(Datum d, Rect r) {
       super(d, r);
     }
     
     protected void onBoundsChange() {
       Rect r = (Rect)bounds;
       Datum d = datum;
       
       float newHeight = (d.value / maxY) * r.h;
       float heightDiff = r.h - newHeight;
       
       hitbox = new Rect(r.x, r.y + heightDiff, r.w, newHeight);
       hit = hitbox.containsPoint(mouseX, mouseY); 
     }
    
     void renderDatum() {
       color fill = hit ? HIGHLIGHTED_FILL : datum.fillColor;
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
  
   public Bar(CSVData data) {
     super(data);
   }
  
   public Bar(ArrayList<Datum> d, String xLabel, String yLabel) {
     super(d, xLabel, yLabel);
   }
   
   protected DatumView createDatumView(Datum datum, Shape bounds) {
     return new BarView(datum, (Rect)bounds);
   }
}
