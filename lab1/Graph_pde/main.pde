// CONSTANTS
int WIDTH = 400;
int HEIGHT = 300;

// the two kinds of graphs were gonna show ya
Bar bar;
Line line;

// veetch vunnnn yurr kurrently luukin @
boolean currentlyBar;
Graph current;

// button to toggle
Button button;

void setup() {
  size(WIDTH, HEIGHT);
  frame.setResizable(true);
 
  button = new Button(new Rect(0, 0, 100, 100), "Click me, bro");
  
  Data data = readData("data.csv");
  line = new Line(data.datums, data.xLabel, data.yLabel);
  bar = new Bar(data.datums, data.xLabel, data.yLabel);
  
  // dont everybody line up at once
  currentlyBar = false;
  current = line;
}

Rect calculateButtonFrame() {
  int w = round(Graph.PADDING_RIGHT * 0.75 * width);
  int h = round(Graph.PADDING_TOP * 0.75 * height);
  
  Point center = new Point((1 - Graph.PADDING_RIGHT/2)*width, Graph.PADDING_TOP/2 * height);
   
  return new Rect(center.x - w/2, center.y - h/2, w, h);
}

void draw() {
  //println("I am drawing");
  background(255);
  current.render();
  
  button.frame = calculateButtonFrame(); 
  button.render();
}

void mouseClicked() {
  if (button.frame.containsPoint(mouseX, mouseY)) {
    currentlyBar = !currentlyBar;
    current = currentlyBar ? bar : line;
  }
}

// Represents the data read in from the CSV file
class Data {
  public final ArrayList<Datum> datums;
  public final String xLabel;
  public final String yLabel;
  
  public Data(ArrayList<Datum> datums, String xLabel, String yLabel) {
    this.datums = datums;
    this.xLabel = xLabel;
    this.yLabel = yLabel;
  }  
}

Data readData(String filename) {
    String[] lines = loadStrings(filename);
    
    String[] tokens = trim(split(lines));
    
    String xLabel = tokens[0];
    String yLabel = tokens[1];
    ArrayList<Datum> data = getDatums(tokens, 2);
    
    return new Data(data, xLabel, yLabel);
}

ArrayList<Datum> getDatums(String[] tokens, int start) {
  ArrayList<Datum> datums = new ArrayList<Datum>();
  
  for (int i = start; i < tokens.length; i += 2) {
    datums.add(new Datum(tokens[i], Float.parseFloat(tokens[i+1])));
  }
  
  return datums;
}

// takes an array of comma-separated pairs of values
// splits each line on commas, trims the result, and returns
// an array of the form:
//   [ <line1 first half>, <line 1 second half>, <line 2 first half>, ... ]
private static String[] split(String lines[]) {
  String[] parts = new String[lines.length * 2];
  
  for (int i = 0; i < lines.length; i++) {
    String line = lines[i];
    String[] comps = split(lines[i], ",");
    
    parts[2*i]     = comps[0];
    parts[2*i + 1] = comps[1];
  }
  
  return parts;
}
 

ArrayList<Datum> readData() {
  ArrayList<Datum> toReturn = new ArrayList<Datum>();
  toReturn.add(new Datum("Apple", 12));
  toReturn.add(new Datum("Sam", 4));
  toReturn.add(new Datum("GodayGoday", 80));
  
  return toReturn;
}
