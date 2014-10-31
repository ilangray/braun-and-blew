
class Kontroller {
  
  private final ArrayList<Datum> data;
//  private final Bar bar;
//  private final FDG fdg;
//  private final Line line;
  
  public Kontroller(ArrayList<Datum> data) {
    this.data = data;
    
//    this.bar = null;
//    this.fdg = null;
//    this.line = null;
  } 
  
  public void render() {
    deselectAllData();
    
    // mouse over
    Datum mousedOver = getMousedOverDatum();
    if (mousedOver != null) {
      mousedOver.setSelected(true); 
    }
    
    // render:
    updateGraphPositions();
//    bar.render();
//    line.render();
//    fdg.render(); 
  }
  
  // repositions the graphs based on the current width/height of the screen
  private void updateGraphPositions() {
    
  }
  
  // returns the datum currently moused-over, or null if none.
  private Datum getMousedOverDatum() {
    Datum mousedOver = null;
    
    // ask each graph what Datum is moused over
//    mouseOver = bar.getMousedOverDatum();
//    mouseOver = line.getMousedOverDatum();
//    mouseOver = fdg.getMousedOverDatum();
    
    return mousedOver;
  }
  
  private void selectData(ArrayList<Datum> toSelect) {
    for (Datum d : toSelect) {
      d.setSelected(true);
    } 
  }
  
  private void deselectAllData() {
    for (Datum d : data) {
      d.setSelected(false);
    } 
  }
  
}
