

import java.util.*;

public interface Function<K, V> {
  K apply(V value);  
}

class Grouper <K, V> {  

  private final ArrayList<V> input;
  private final Map<K, ArrayList<V>> groups;
  
  public Grouper(ArrayList<V> input) {
    this.input = input;
    this.groups = new HashMap<K, ArrayList<V>>(); 
  }
  
  public Map<K, ArrayList<V>> by(Function<K, V> f) {
    
    for (V value : input) {
       K key = f.apply(value);
       add(key, value);
    }
    
    return groups;
  }
  
  private void add(K k, V v) {
    ArrayList<V> vs = groups.get(k);
    
    if (vs == null) {
      println("Making a new list of values for key = " + k);
      vs = new ArrayList<V>();
      groups.put(k, vs);
    }
    
    vs.add(v);
  }
}
