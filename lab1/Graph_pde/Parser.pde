
/*
// parses a simple CSV file
class Contents {
  
  public final ArrayList<Datum> data;
  public final String xLabel;
  public final String yLabel;
  
  public static Contents Contents(String filename) {
   
    String lines[] = loadStrings(filename);
     
    
    
    
    String firstLine = lines[0];
    String labels = 
    String xLabel = null;
    String yLabel = null; 
    
    ArrayList<Datum> data = null;
    
   
    return new Contents(data, xLabel, yLabel);
  }
  
  // takes an array of comma-separated pairs of values
  // splits each line on commas, trims the result, and returns
  // an array of the form:
  //   [ <line1 first half>, <line 1 second half>, <line 2 first half>, ... ]
  private static String[] split(String lines[]) {
    String[] parts = new String[lines.size * 2];
    
    for (int i = 0; i < lines.size; i++) {
      String line = lines[i];
      String[] comps = split(lines[i], ",");
      
      parts[2*i]     = comps[0];
      parts[2*i + 1] = comps[1];
    }
    
    return parts;
  }
 
  private Contents(ArrayList<Datum> data, String xLabel, String yLabel) {
  
  }  
}

*/
  
