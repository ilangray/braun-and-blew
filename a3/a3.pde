
// this is awesome. lets do some physics yolo swag


Simulator sm;
RenderMachine rm;

boolean done = false;

void setup() {
  size(600, 600);
  
  // read data
  DieWelt w = new Configurator("data.csv").configure();
  
  // configur renderer and simulator
  rm = new RenderMachine(w.nodes, w.springs);
  sm = new Simulator(w.nodes, w.springs, w.zaps, w.dampers);
}

// converts ms to seconds
float seconds(int ms) {
  return ms / 1000.0f; 
}

void draw() {
  if (done) {
    return; 
  }
  
  // update sim
  done = !sm.step(seconds(16));
  
  if (!done) {
    render(); 
  }
}

void render() {
  background(color(255,255,255));
  rm.render();
}

