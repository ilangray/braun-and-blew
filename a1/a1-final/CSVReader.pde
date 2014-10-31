
/**
 * Represents a single entry in the CSV file.
 */
class Entry {
  private final String dept;
  private final String sponsor;
  private final String year;
  private final int funding;
  
  public Entry(String dept, String sponsor, String year, int funding) {
    this.dept = dept;
    this.sponsor = sponsor;
    this.year = year;
    this.funding = funding;
  }
  
  public String toString() {
    String d = "dept = " + dept;
    String s = "sponsor = " + sponsor;
    String y = "year = " + year;
    String f = "funding = " + funding;
   
    return "CSV.Node{" + d + ", " + s + ", " + y + ", " + f + "}"; 
  }
}

/**
 * Reads a CSV file
 */
class CSVReader {

  private static final String SEPARATOR = ",";
 
  public CSVReader() { } 
 
  /**
   * Returns all of the nodes read in from the file.
   */
  public ArrayList<Entry> read(String filename) {
    String[] lines = loadStrings(filename);
    
    // we ignore the first line
    
    ArrayList<Entry> nodes = new ArrayList<Entry>();
    for (int i = 1; i < lines.length; i++) {
      nodes.add(parseNode(lines[i]));
    }
    return nodes;
  }
  
  private Entry parseNode(String line) {
    String[] comps = trim(split(line, SEPARATOR)); 
    
    String dept = comps[0];
    String sponsor = comps[1];
    String year = comps[2];
    int money = Integer.parseInt(comps[3]);
    
    return new Entry(dept, sponsor, year, money);
  }
}
