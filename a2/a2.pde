// main

// constants 
String FILENAME = "ds1.csv";
float PERCENT_GRAPH_WIDTH = 0.8;
float PERCENT_BUTTON_PADDING = 0.75;

// the contents of the CSV file
CSVData DATA;

ArrayList<Button> buttons;

// the current graph being rendered
Graph currentGraph;
// what type is the current graph?
int currentType;

boolean animating = false;

int PIE = 0;
int BAR = 1;
int LINE = 2;
int STACKEDBAR = 3;

void layoutUI() {
  // layout the graph
  currentGraph.setBounds(getGraphBounds());
  
  // layout side bar
  layoutButtons(getButtonFrames(getSidebarBounds()));
}

void render() {
  currentGraph.render();
  
  renderSeparator();
 
  for (Button b : buttons) {
    b.render();
  } 
}

void renderSeparator() {
  stroke(color(0,0,0));
  
  Rect frame = getGraphBounds();
  
  Point top = frame.getUR();
  Point bottom = frame.getLR();
  
  strokeWeight(4);
  drawLine(top, bottom);
}

Rect getGraphBounds() {
  return new Rect(0, 0, PERCENT_GRAPH_WIDTH * width, height);
}

Rect getSidebarBounds() {
  Rect graphBounds = getGraphBounds();
  float percentWidth = 1 - PERCENT_GRAPH_WIDTH;
  return new Rect(graphBounds.getMaxX(), 0, percentWidth * width, height);
}

ArrayList<Rect> getButtonFrames(Rect sidebarBounds) {
  ArrayList<Rect> bounds = new ArrayList<Rect>();
  
  float h = sidebarBounds.h / buttons.size();
  
  for (int i = 0; i < buttons.size(); i++) {
    Rect frame = new Rect(sidebarBounds.x, sidebarBounds.y + h * i, sidebarBounds.w, h); 
    bounds.add(frame.scale(PERCENT_BUTTON_PADDING, PERCENT_BUTTON_PADDING));
  }
  
  return bounds;
}

void layoutButtons(ArrayList<Rect> frames) {
  for (int i = 0; i < frames.size(); i++) {
    Button button = buttons.get(i);
    Rect frame = frames.get(i);
    
    button.frame = frame;
  }
}

Button makeButton(String text) {
  return new Button(new Rect(0,0,0,0), color(255,255,255), text, color(0,0,0)); 
}

void setup() {
  // general canvas setup
  size(900, 600);
  frame.setResizable(true);
  frameRate(30);
  
  // read CSV data
  DATA = new CSVReader().read(FILENAME);
  colorize(DATA.datums);
  
  // init buttons -- order must match value of constants
  buttons = makeList(
    makeButton("Pie Chart"),
    makeButton("Bar Graph"),
    makeButton("Line Graph"),
    makeButton("StackedBar")
  );

  // start with a bar graph
  currentGraph = new Bar(DATA);
  currentType = BAR;
}

void draw() {
  background(color(255, 255, 255)); 
  
  layoutUI();
  render();
}

void mouseClicked() {
  // find which button was hit
  Button hit = getButtonContainingMouse();
  
  if (hit == null) {
    return; 
  }
  
  animateTransition(buttons.indexOf(hit));
}

void animateTransition(int newType) {
  // one animation at a time
  if (animating) {
    return;
  }
  
  // only animate if type changed
  if (newType == currentType) {
    return;
  } 
  
  animating = true;
  
  Graph g = currentGraph;
  
  if (isBar(g)) {
    Bar bar = (Bar)currentGraph;
    if (newType == LINE) {
      Line line = new Line(DATA);
      line.setBounds(currentGraph.getBounds());
      currentGraph = animate(bar, line, makeContinuation(line, newType));
    } else if (newType == PIE) {
      PieChart pie = new PieChart(DATA);
      pie.setBounds(getGraphBounds());
      currentGraph = animate(bar, pie, makeContinuation(pie, newType));
    } else {
      StackedBar sb = new StackedBar(DATA);
      sb.setBounds(getGraphBounds());
      currentGraph = animate(bar, sb, makeContinuation(sb, newType));
    }
  }
  else if (isLine(g)) {
    Line line = (Line)currentGraph;
    if (newType == BAR) {
      Bar bar = new Bar(DATA);
      bar.setBounds(getGraphBounds());
      currentGraph = animate(line, bar, makeContinuation(bar, newType));
    } else if (newType == PIE) {
      PieChart pie = new PieChart(DATA);
      pie.setBounds(getGraphBounds());
      currentGraph = animate(line, pie, makeContinuation(pie, newType));
    } else {
      StackedBar sb = new StackedBar(DATA);
      sb.setBounds(getGraphBounds());
      currentGraph = animate(line, sb, makeContinuation(sb, newType));
    }
  }
  else if (isPie(g)) {
    PieChart pie = (PieChart)currentGraph;
    if (newType == LINE) {
      Line line = new Line(DATA);
      line.setBounds(currentGraph.getBounds());
      currentGraph = animate(pie, line, makeContinuation(line, newType));
    } else if (newType == BAR) {
      Bar bar = new Bar(DATA);
      bar.setBounds(getGraphBounds());
      currentGraph = animate(pie, bar, makeContinuation(bar, newType));
    } else {
      StackedBar sb = new StackedBar(DATA);
      sb.setBounds(getGraphBounds());
      currentGraph = animate(pie, sb, makeContinuation(sb, newType)); 
    }
  }  
  else if (isStackedBar(g)) {
    StackedBar sb = (StackedBar)currentGraph;
    if (newType == LINE) {
      Line line = new Line(DATA);
      line.setBounds(getGraphBounds());
      currentGraph = animate(sb, line, makeContinuation(line, newType));
    } else if (newType == BAR) {
      Bar bar = new Bar(DATA);
      bar.setBounds(getGraphBounds());
      currentGraph = animate(sb, bar, makeContinuation(bar, newType));
    } else {  // newType == PIE
      PieChart pc = new PieChart(DATA);
      pc.setBounds(getGraphBounds());
      currentGraph = animate(sb, pc, makeContinuation(pc, newType));
    }
  }
  else {
    throw new IllegalArgumentException(); 
  }
}

Continuation makeContinuation(final Graph result, final int type) {
  return new Continuation() {
    public void onContinue() {
      currentGraph = result;
      currentType = type;
      
      println("Animating COMPLETED");
      animating = false;
    } 
  };
}

boolean isBar(Graph g) {
  return g instanceof Bar;
}

boolean isLine(Graph g) {
  return g instanceof Line;
}

boolean isPie(Graph g) {
  return g instanceof PieChart;
}

boolean isStackedBar(Graph g) {
  return g instanceof StackedBar; 
}

Button getButtonContainingMouse() {
  for (Button b : buttons) {
    if (b.containsMouse()) {
      return b;
    } 
  } 
  
  return null;
}

void colorize(ArrayList<Datum> ds) {
  color start = color(202, 232, 211);
  color end = color(3, 101, 152);
  
  for (int i = 0; i < ds.size(); i++) {
    Datum datum = ds.get(i);
    float percent = 1.0 / ds.size() * i;
    
    float r = lerp(red(start), red(end), percent);
    float g = lerp(green(start), green(end), percent);
    float b = lerp(blue(start), blue(end), percent);
    
    datum.fillColor = color(r, g, b);
  } 
}


