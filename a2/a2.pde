// main

// constants 
String FILENAME = "ds1.csv";


Graph current;

void setup() {
  // general canvas setup
  size(900, 600);
  frame.setResizable(true);
  
  frameRate(30);
  
  // init SQTM
  Rect bounds = new Rect(5, 5, width - 10, height - 10);
  
  CSVData data = new CSVReader().read(FILENAME);
  println("root = " + data);
  
  final Bar bg = new Bar(data);
//  final HeightGraph hg = new HeightGraph(data);
  final Scatterplot scat = new Scatterplot(data);
  
  current = animate(bg, scat, new Continuation() {
    public void onContinue() {
      println("YOLO");
      current = scat;
    }
  });
}

void draw() {
  background(color(255, 255, 255)); 
  current.render();
}

