
class Reader {
  
  private static final String SEPARATOR = ",";
  
  public Reader() {}
 
  public ArrayList<Datum> read(String filename) {
    String[] lines = loadStrings(filename);
    return parseDatums(lines, parseKeys(lines[0]));
  } 
  
  public String[] parseKeys(String firstLine) {
    return getComponents(firstLine);
  }
  
  public ArrayList<Datum> parseDatums(String[] lines, String[] keys) {
    ArrayList<Datum> ds = new ArrayList<Datum>();
    
    // skip the first line
    for (int i = 1; i < lines.length; i++) {
      ds.add(new Datum(keys, parseFloats(getComponents(lines[i]))));  
    }
    
    return ds;
  }
  
  // splits a line on a string
  private String[] getComponents(String line) {
    return trim(split(line, SEPARATOR));
  }
  
  private float[] parseFloats(String[] strings) {
    float[] fs = new float[strings.length];
    
    for (int i = 0; i < strings.length; i++) {
      fs[i] = Float.parseFloat(strings[i]); 
    }
    
    return fs;
  }
}
