
// this is awesome. lets do some physics yolo swag


Simulator sm;
RenderMachine rm;
CenterPusher cp;

boolean done = false;

boolean first = true;

int previous_w;
int previous_h;

void setup() {
  size(800, 600);
  previous_w = width;
  previous_h = height;
  frame.setResizable(true);
 
  // read data
  DieWelt w = new Configurator("connected-9.csv").configure();
  
  // configur renderer and simulator
  rm = new RenderMachine(w.nodes, w.springs);
  sm = new Simulator(w.nodes, w.springs, w.zaps, w.dampers);
  cp = new CenterPusher(w.nodes);
}

// converts ms to seconds
float seconds(int ms) {
  return ms / 1000.0f; 
}

void draw() {
  // yoloswag
  if (first) {
    rm.renderLabel(new Point(0,0), "hooha");
    first = false; 
  }
  
  if (!done || dragged != null || previous_w != width || previous_h != height) {
    // update sim
    done = !sm.step(seconds(16));
  }
  
  cp.push();
  render();
  
  previous_w = width;
  previous_h = height;
}

void render() {
  background(color(255,255,255));
  rm.render();
}

