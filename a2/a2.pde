// main

// constants 
String FILENAME = "ds1.csv";

Graph g;

void setup() {
  // general canvas setup
  size(600, 400);
  frame.setResizable(true);
  
  // init SQTM
  Rect bounds = new Rect(5, 5, width - 10, height - 10);
  
  CSVData data = new CSVReader().read(FILENAME);
  println("root = " + data);
  
  g = new HeightGraph(data.datums, data.xLabel, data.yLabel);
}

void draw() {
  background(color(255, 255, 255)); 
  
  g.render();
}

