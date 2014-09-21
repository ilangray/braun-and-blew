
// owned by ilan
class Datum {
  
  public final static int INVALID_VALUE = -1;
  
  public final int id;
  public int value;
  public final ArrayList<Datum> children;
  public final boolean isLeaf;
  
  /**
   * Creates a new leaf datum with the given id and value. children will be null
   */
  public Datum(int id, int value) {
    this(id, value, true);
  }
  
  /**
   * Creates a new NON-leaf datum with the given id. Children can be
   * added by accessing and mutating the list of children.
   */
  public Datum(int id) {
    this(id, INVALID_VALUE, false);
  }
  
  private Datum(int id, int value, boolean isLeaf) {
    this.id = id;
    this.value = value;
    this.isLeaf = isLeaf;
   
    if (isLeaf) {
      this.children = null;
    } else {
      this.children = new ArrayList<Datum>();
    } 
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
  
  public float getValueF()
  {
    return (float)value;
  }
  
  public String toString() {
    return "Datum{id = " + id + ", value = " + value + ", isLeaf = " + isLeaf + ", kids = " + children + "}"; 
  }
}
