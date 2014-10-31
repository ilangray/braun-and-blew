
// this is awesome. lets do some physics yolo swag

Simulator sm;
RenderMachine rm;

boolean done = false;

void setup() {
  // canvas setup
  size(300, 1000);
  
  ArrayList<Node> ns = makeList(
    new Node(1, 2),
    new Node(2, 2)
  );
  
  ns.get(0).pos.x = 150;
  ns.get(0).pos.y = 400;
  
  ns.get(1).pos.x = 150;
  ns.get(1).pos.y = 600;
  
  ArrayList<Spring> ss = makeList(
    new Spring(ns.get(0), ns.get(1), 300)
  );
  
  ArrayList<Damper> ds = makeList(
    new Damper(ns.get(0)), 
    new Damper(ns.get(1))
  );
  
  rm = new RenderMachine(ns, ss);
  sm = new Simulator(ns, ss, ds);
  
  // TESTING
//  float step = ms(10);
//  sm.step(step);
//  println("---------------------");
//  sm.step(step);
//  println("---------------------");
//  sm.step(step); 
}

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
