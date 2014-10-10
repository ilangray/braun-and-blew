
SQTMDatum makeSQTMDatums(ArrayList<Datum> ds) {
  // wrap each d in a SQTMD
  ArrayList<SQTMDatum> sqds = makeList();
  for (Datum d : ds) {
    SQTMDatum sd = new SQTMDatum(d.key, d.value, d.marked);
    sqds.add(sd); 
  }
  
  // make a root and return it
  SQTMDatum root = new SQTMDatum("THE ROOOOOOT");
  root.children.addAll(sqds);
  root.calculateValue();
  return root;
}

// owned by ilan
class SQTMDatum {
  
  public final static int INVALID_VALUE = -1;
  
  public final String id;
  public final ArrayList<SQTMDatum> children;
  public final boolean isLeaf;
  public final boolean marked;
  
  public float value;
  
  /**
   * Creates a new leaf datum with the given id and value. children will be null
   */
  public SQTMDatum(String id, float value, boolean marked) {
    this(id, value, true, marked);
  }
  
  /**
   * Creates a new NON-leaf datum with the given id. Children can be
   * added by accessing and mutating the list of children.
   */
  public SQTMDatum(String id) {
    this(id, INVALID_VALUE, false, false);
  }
  
  private SQTMDatum(String id, float value, boolean isLeaf, boolean marked) {
    this.id = id;
    this.value = value;
    this.isLeaf = isLeaf;
    this.marked = marked;
   
    if (isLeaf) {
      this.children = null;
    } else {
      this.children = new ArrayList<SQTMDatum>();
    } 
  }
  
  public SQTMDatum getAnyLeaf() {
    if (isLeaf) {
      return this;
    }
    return children.get(0).getAnyLeaf();
  }
  
  public float calculateValue() {
     if (value != INVALID_VALUE) {
       return value;
     }
     
     int sum = 0;
     for (SQTMDatum d : children) {
       sum += d.calculateValue();
     }
     
     this.value = sum;
     return sum;
  }
  
  public void print() {
    println("id = " + id + ", value = " + value);
    
    if (children == null) {
      return;
    }
   
    for (SQTMDatum d : children) {
      d.print();
    } 
  }
  
  // legacy
  public float getValueF() {
    return value;
  }
  
  public String toString() {
    return "SQTMDatum{id = " + id + ", value = " + value + ", isLeaf = " + isLeaf + ", kids = " + children + "}"; 
  }
}
