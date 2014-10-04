class HeightGraph extends Graph {

   private class HeightView extends Graph.DatumView {
     
     public Point top;
     public Point bottom;
     
     public HeightView(Datum d, Shape s) {
       super(d, s);
       
       Rect r = (Rect)s;
       
       float newHeight = (d.value / maxY) * r.h;
       float heightDiff = r.h - newHeight;
       
       float x = r.x + r.w/2;
       
       top = new Point(x, r.y + heightDiff);
       bottom = new Point(x, r.y + r.h);
     }
    
     void renderDatum() {
       stroke(datum.fillColor);
       drawLine(top, bottom);
     }
     
     void renderTooltip() {}
     
     
   }
  
   public HeightGraph(CSVData data) {
     super(data); 
   }
  
   public HeightGraph(ArrayList<Datum> d, String xLabel, String yLabel) {
     super(d, xLabel, yLabel);
   }
   
   protected DatumView createDatumView(Datum datum, Shape bounds) {
     return new HeightView(datum, bounds);
   }
}
