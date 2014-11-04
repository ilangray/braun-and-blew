
class Kontroller {
  
  private final ArrayList<Datum> data;

  private final NetworkView networkView;
  private final CategoricalView categoricalView;
  private final TemporalView temporalView;

  private SelectionController selectionController;
  
  public Kontroller(ArrayList<Datum> data) {
    this.data = data;
    
    this.categoricalView = new CategoricalView(data);
    this.temporalView = new TemporalView(data);

    Rect bounds = new Rect(0, 0, 0.75 * width, height / 2);
    this.networkView = new NetworkView(data, bounds);
    positionView(networkView, 0, 0, 0.75, 0.5);

    selectionController = new RectSelectionController(
      makeList(networkView, categoricalView, temporalView)
    );
  } 
  
  public void render() {
    // reposition everything
    updateGraphPositions();
    
    // hover
    ArrayList<Datum> hovered = selectionController.getSelectedDatums();
    deselectAllData();
    selectData(hovered);

    // render:
    background(color(255, 255, 255));
    categoricalView.render();
    temporalView.render();
    networkView.render();

    // separators go on top
    renderSeparators();

    selectionController.render();
  }

  private void renderSeparators() {
    stroke(color(0,0,0));
    strokeWeight(3);

    // bottom edge of network
    Rect netBounds = networkView.getBounds();
    float netBottom = netBounds.y + netBounds.h;
    line(netBounds.x, netBottom, netBounds.x + netBounds.w, netBottom);

    // left edge of categorical
    Rect catBounds = categoricalView.getBounds();
    float catBottom = catBounds.y + catBounds.h;
    line(catBounds.x, catBounds.y, catBounds.x, catBottom);
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

  public MouseHandler getMouseHandler() {
    return selectionController;
  }
}