
class Kontroller {
  
  private final ArrayList<Datum> data;

  private final NetworkView networkView;
  private final CategoricalView categoricalView;
  private final TemporalView temporalView;
  
  public Kontroller(ArrayList<Datum> data) {
    this.data = data;
    
    this.categoricalView = new CategoricalView(data);
    this.temporalView = new TemporalView(data);

    Rect bounds = new Rect(0, 0, 0.75 * width, height / 3);
    this.networkView = new NetworkView(data, bounds);
    positionView(networkView, 0, 0, 0.75, 0.5);
  } 
  
  public void render() {
    // reposition everything
    updateGraphPositions();
    
    // hover
    ArrayList<Datum> hovered = getHoveredDatums();
    deselectAllData();
    selectData(hovered);

    // render:
    background(color(255, 255, 255));
    categoricalView.render();
    temporalView.render();
    networkView.render();
  }
  
  // repositions the graphs based on the current width/height of the screen
  private void updateGraphPositions() {
    positionView(temporalView, 0, 0.5, 0.75, 0.5);
    positionView(categoricalView, 0.75, 0, 0.25, 1.0);
    positionView(networkView, 0, 0, 0.75, 0.5);
  }

  private void positionView(AbstractView view, float px, float py, float pw, float ph) {
    float x = width * px;
    float y = height * py;
    float w = width * pw;
    float h = height * ph;
    view.setBounds(new Rect(x, y, w, h));
  }
  
  // returns the datum currently moused-over, or null if none.
  private ArrayList<Datum> getHoveredDatums() {
    // ask each graph what Datum is moused over
    return flatten( 
      categoricalView.getHoveredDatums(),
      temporalView.getHoveredDatums()
    );
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