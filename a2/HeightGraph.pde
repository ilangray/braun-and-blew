class HeightGraph extends Graph {

   private class HeightView extends Graph.DatumView {
     
     private final Point top;
     private final Point bottom;
     
     public HeightView(Datum d, Rect r) {
       super(d, r);
       
       float newHeight = (d.value / maxY) * r.h;
       float heightDiff = r.h - newHeight;
       
       float x = r.x + r.w/2;
       
       top = new Point(x, r.y + heightDiff);
       bottom = new Point(x, r.y + r.h);
     }
    
     void renderDatum() {
       drawLine(top, bottom);
     }
     
     void renderTooltip() {}
   }
  
   public HeightGraph(ArrayList<Datum> d, String xLabel, String yLabel) {
     super(d, xLabel, yLabel);
   }
   
   protected DatumView createDatumView(Datum datum, Rect bounds) {
     return new HeightView(datum, bounds);
   }
}
