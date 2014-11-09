
// this is less awesome but were still doin physics, ya'll

Simulator sm;
RenderMachine rm;
CenterPusher cp;


boolean done = false;
boolean first = true;

int previous_w;
int previous_h;

void setup() {
  size(1400, 800);
  previous_w = width;
  previous_h = height;
  frame.setResizable(true);
 
  // read data
  DieWelt w = new Configurator("data3.csv").configure();
  
  // System.exit(1);
  // configur renderer and simulator
  rm = new RenderMachine(w.nodes, w.springs, w.externalLinks);
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

