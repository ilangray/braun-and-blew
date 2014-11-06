

String FILENAME = "iris.csv";

ArrayList<Datum> data = null;

RenderMachine rm = null;
Axis[] axes = null;

public final float W_PADDING = 40;

void setup(){
  size(600, 600);
  
  data = new Reader().read(FILENAME);
  
  String[] keys = data.get(0).getKeys(); 
  int dimens = keys.length;
  
  axes = new Axis[dimens];
  for (int i = 0; i < dimens; i++) {
    axes[i] = new Axis(data, keys[i], 0); 
  }
  
  rm = new RenderMachine(data, axes); 
}

void draw(){
  background(color(255,255,255));
  
  // make sure them axes are in order
  positionAxes();
  
  // render axes
  for (Axis a : axes) {
    a.render(); 
  }
  
  // render lines
  rm.render();
}

void positionAxes() {
  int w = width;
  int dimens = axes.length;
  
  int unitWidth = (w - 2*(int)W_PADDING) / (dimens - 1);
  int left = (int)W_PADDING;
  
  for (Axis axis : axes) {
    axis.setXCoordinate(left);
    left += unitWidth;
  }
}
