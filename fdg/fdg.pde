
// this is awesome. lets do some physics yolo swag


Simulator sm;
RenderMachine rm;
CenterPusher cp;
ForceDirectedGraph fdg;
Rect halfBounds;
Rect bounds;
NetworkView nv;

boolean done = false;

boolean first = true;

int previous_w;
int previous_h;

void setup() {
  size(1400, 800);
  previous_w = width;
  previous_h = height;
  frame.setResizable(true);
  bounds = new Rect(0, 0, width, height);
  nv = new NetworkView(new DerLeser("data_aggregate.csv").readIn(), bounds);

  // bounds = new Rect(width / 2, height / 2, width - width/2, height - height/2);


  nv.setBounds(bounds);

  // DieWelt w = new Configurator("data.csv", bounds).configure();

  // fdg = new ForceDirectedGraph(w.nodes, w.springs, w.zaps, w.dampers, null);

  // if (fdg == null) {
  //   println("In die Gerate fdg null ist");
  // }

  // fdg.setBounds(bounds);
  // fdg.getCenterPusher().setBounds(bounds);

}

// converts ms to seconds
float seconds(int ms) {
  return ms / 1000.0f; 
}

void draw() {
  nv.render();
}

