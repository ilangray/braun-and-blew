
class SQTMBar extends Graph {
  
  private class SQTMView extends Graph.DatumView {
    private SQTM tm;
   
    public SQTMView(GDatum gDatum, Rect r) {
      super(gDatum, r);
      
      // need to calculate height of SQTM. width stays the same
      int newHeight = round((gDatum.value / maxY) * r.h);
      float heightDiff = r.h - newHeight;
       
      r = new Rect(r.x, r.y + heightDiff, r.w, newHeight);
      tm = new SQTM(r, gDatum.root);
    }   
    
    public void renderDatum() {
      tm.render();
    }
    
    public void renderTooltip() { }
  }
  
  public SQTMBar(ArrayList<GDatum> data, String xLabel, String yLabel) {
    super(data, xLabel, yLabel); 
  }
  
  public Graph.DatumView createDatumView(GDatum gDatum, Rect bounds) {
    // find the corresponding SQTM Datum
    return new SQTMView(gDatum, bounds);
  }
  
  
  
}
