
// this is awesome. lets do some physics yolo swag


Simulator sm;
RenderMachine rm;
CenterPusher cp;
ForceDirectedGraph fdg;
Rect halfBounds;
Rect bounds;
NetworkView nv;

boolean done = false;

int previous_w;
int previous_h;

void setup() {
  size(1400, 800);
  previous_w = width;
  previous_h = height;
  frame.setResizable(true);
  bounds = new Rect(0, 0, width ,height / 3);
  nv = new NetworkView(new DerLeser("data_aggregate.csv").readIn(), bounds);
  nv.setBounds(bounds);

}

// converts ms to seconds
float seconds(int ms) {
  return ms / 1000.0f; 
}

void draw() {
  nv.render();
  nv.getHoveredDatums();
}

