
// owned by ilan
class Datum {
  
  public final static int INVALID_VALUE = -1;
  
  public final int id;
  public int value;
  public final ArrayList<Datum> children;
  public final boolean isLeaf;
  public Entry entry;
  
  /**
   * Creates a new leaf datum with the given id and value. children will be null
   */
  public Datum(int id, int value, Entry entry) {
    this(id, value, true, entry);
  }
  
  /**
   * Creates a new NON-leaf datum with the given id. Children can be
   * added by accessing and mutating the list of children.
   */
  public Datum(int id) {
    this(id, INVALID_VALUE, false, null);
  }
  
  private Datum(int id, int value, boolean isLeaf, Entry entry) {
    this.id = id;
    this.value = value;
    this.isLeaf = isLeaf;
    this.entry = entry;
   
    if (isLeaf) {
      this.children = null;
    } else {
      this.children = new ArrayList<Datum>();
    } 
  }
  
  public Datum getAnyLeaf() {
    if (isLeaf) {
      return this;
    }
    return children.get(0).getAnyLeaf();
  }
  
  
  
  public int calculateValue() {
     if (value != INVALID_VALUE) {
       return value;
     }
     
     int sum = 0;
     for (Datum d : children) {
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
   
    for (Datum d : children) {
      d.print();
    } 
  }
  
  public float getValueF()
  {
    return (float)value;
  }
  
  public String toString() {
    return "Datum{id = " + id + ", value = " + value + ", isLeaf = " + isLeaf + ", kids = " + children + "}"; 
  }
}
