

import java.util.*;

public interface Function<F, T> {
  T apply(F value);  
}

class Grouper <K, V> {  

  private final ArrayList<V> input;
  private final Map<K, ArrayList<V>> groups;
  
  public Grouper(ArrayList<V> input) {
    this.input = input;
    this.groups = new HashMap<K, ArrayList<V>>(); 
  }
  
  public Map<K, ArrayList<V>> by(Function<V, K> f) {
    
    for (V value : input) {
       K key = f.apply(value);
       add(key, value);
    }
    
    return groups;
  }
  
  private void add(K k, V v) {
    ArrayList<V> vs = groups.get(k);
    
    if (vs == null) {
      vs = new ArrayList<V>();
      groups.put(k, vs);
    }
    
    vs.add(v);
  }
}
