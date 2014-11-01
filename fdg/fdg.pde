
// this is awesome. lets do some physics yolo swag


Simulator sm;
RenderMachine rm;
CenterPusher cp;
ForceDirectedGraph fdg;
Rect halfBounds;
Rect bounds;

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
  
  bounds = new Rect(width / 3, height / 3, 2*width/3 - width/3, 2*height/3 - height/3);

  DieWelt w = new Configurator("data.csv", bounds).configure();

  fdg = new ForceDirectedGraph(w, null);

}

// converts ms to seconds
float seconds(int ms) {
  return ms / 1000.0f; 
}

void draw() {
  fdg.render();
}

