

String FILENAME = "iris.csv";

ArrayList<Datum> data = null;

Axis a;

void setup(){
  size(600, 600);
  background(color(255,255,255));
  data = new Reader().read(FILENAME);
  a = new Axis(data, data.get(0).getKeys()[0], 20);
}

void draw(){
 a.render();
}
