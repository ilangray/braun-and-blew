
class Datum {
  
  private String[] keys;
  private float[] values;
  
  public Datum(String[] keys, float[] values) {
    this.keys = keys;
    this.values = values;
  }
  
  public String[] getKeys(){
    return keys;
  }
  
  public float getValue(String k){
    return values[getIndex(k)];
  }
  
  // returns the index of the given key
  private int getIndex(String k) {
    for (int i = 0; i < keys.length; i++) {
      if (keys[i].equals(k)) {
        return i; 
      }
    } 
    
    return -1;
  } 

  public String toString() {
    return "Datum{keys = " + keyString() + ", values = " + valueString() + "}";
  }
  
  // toString nastiness
  private String keyString() {
    String s = "";
   
    for (int i = 0; i < keys.length; i++) {
      s += keys[i];
      
      if (i != keys.length - 1) {
        s += ", ";  
      }
    }
   
    return s; 
  }
  
  private String valueString() {
    String s = "";
   
    for (int i = 0; i < values.length; i++) {
      s += values[i];
      
      if (i != values.length - 1) {
        s += ", ";  
      }
    }
   
    return s; 
  }
  
}
