// CONSTANTS
int WIDTH = 400;
int HEIGHT = 300;



Graph g;
ArrayList<Datum> ds;

void setup() {
  size(WIDTH, HEIGHT);
  frame.setResizable(true);
  
  Data data = readData("data.csv");
  g = new Line(data.datums, data.xLabel, data.yLabel);
  println("I made a graph");
}

void draw() {
  //println("I am drawing");
  background(255);
  g.render();
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
