
// owned by ilan
class Datum {
  
  public final int value;
  public final ArrayList<Datum> children;
  
  public Datum(int value, ArrayList<Datum> children) {
    this.value = value;
    this.children = children; 
  }
  
  public boolean isLeaf() {
    // implement me!!!! 
  }
}
