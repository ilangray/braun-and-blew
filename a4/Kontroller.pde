
class Kontroller {
  
  private final ArrayList<Datum> data;
  private final CategoricalView categoricalView;
  
  public Kontroller(ArrayList<Datum> data) {
    this.data = data;
    
    this.categoricalView = new CategoricalView(data);
  } 
  
  public void render() {
    deselectAllData();
    
    // mouse over
    ArrayList<Datum> hovered = getHoveredDatums();
    selectData(hovered);

    // render:
    updateGraphPositions();
    background(color(255, 255, 255));
    categoricalView.render();
  }
  
  // repositions the graphs based on the current width/height of the screen
  private void updateGraphPositions() {
    // position in middle 50% of w, middle 90% of h
    float x = width * 0.25;
    float y = height * 0.05;

    float w = width * 0.5;
    float h = height * 0.9;

    categoricalView.setBounds(new Rect(x, y, w, h));
  }
  
  // returns the datum currently moused-over, or null if none.
  private ArrayList<Datum> getHoveredDatums() {
    
    // ask each graph what Datum is moused over
    return categoricalView.getHoveredDatums();
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