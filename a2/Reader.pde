
// Represents the data read in from the CSV file
class CSVData {
  public final ArrayList<Datum> datums;
  public final String xLabel;
  public final String yLabel;
  
  public CSVData(ArrayList<Datum> datums, String xLabel, String yLabel) {
    this.datums = datums;
    this.xLabel = xLabel;
    this.yLabel = yLabel;
  }  
  
  public String toString() {
    return "xLabel = " + xLabel + ", yLabel = " + yLabel + ", datums = " + datums; 
  }
}

class CSVReader {
  public CSVData read(String filename) {
      String[] lines = loadStrings(filename);
      String[] tokens = trim(mapSplit(lines));
      
      String xLabel = tokens[0];
      String yLabel = tokens[1];
      ArrayList<Datum> data = getDatums(tokens, 2);
      
      return new CSVData(data, xLabel, yLabel);
  }
  
  private ArrayList<Datum> getDatums(String[] tokens, int start) {
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
  private String[] mapSplit(String lines[]) {
    String[] parts = new String[lines.length * 2];
    
    for (int i = 0; i < lines.length; i++) {
      String line = lines[i];
      String[] comps = split(lines[i], ",");
      
      parts[2*i]     = comps[0];
      parts[2*i + 1] = comps[1];
    }
    
    return parts;
  } 
  
  public CSVReader() {
    
  }
  
}
