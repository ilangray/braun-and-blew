
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
  
  private static final String SEPARATOR = ",";
  
  public CSVData read(String filename) {
      String[] lines = loadStrings(filename);
      
      ArrayList<String> fields = makeList(getComponents(lines[0]));
      fields.remove(0);
      
      ArrayList<Datum> ds = new ArrayList<Datum>();
      for (int i = 1; i < lines.length; i++) {
         ds.add(parseDatum(fields, lines[i]));
      }
      
      String[] firstLineComps = getComponents(lines[0]);
      String xLabel = firstLineComps[0];
      String yLabel = firstLineComps[1];
      
      return new CSVData(ds, xLabel, yLabel);
  }
  
  private String[] getComponents(String line) {
    return trim(split(line, SEPARATOR)); 
  }
  
  // yo if k is >= ss.length, its not gonna go well. youve been warned
  private String[] drop(String[] ss, int k) {
    assert k <= ss.length;
    
    String[] output = new String[ss.length - k];
    
    for (int i = k; i < ss.length; i++) {
      output[i - k] = ss[i];
    }
    
    return output;
  }
  
  private Datum parseDatum(ArrayList<String> fields, String line) {
    
    String[] comps = getComponents(line);
    
    String key = comps[0];
    
    ArrayList<Float> values = new ArrayList<Float>();
    
    for (int i = 1; i < comps.length; i++) {
      values.add(Float.parseFloat(comps[i])); 
    }
    
    return new Datum(key, values, fields);
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
 
  <T> ArrayList<T> makeList(T[] values) {
    ArrayList<T> ts = new ArrayList<T>();
    
    for (T v : values) {
      ts.add(v); 
    }
    
    return ts; 
  } 
  
  public CSVReader() {
    
  }
  
}
