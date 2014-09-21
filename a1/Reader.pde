
import java.util.*;

// reads a file with a given name and returns a 
// tree of Datums representing the contents
// owned by ilan
class Reader {
  
  private final String filename;
  
  public Reader(String filename) {
    this.filename = filename;
  }
  
  public Datum read() {
    String[] lines = loadStrings(filename);
    
    // parse the leaves
    int leafCount = Integer.parseInt(lines[0]);
    ArrayList<Datum> leaves = parseDatums(slice(lines, 1, leafCount));
    
    // parse parents + relationships
    int start = leafCount + 1;
    int relCount = Integer.parseInt(lines[start]);
    ArrayList<Datum> parents = parseRelationships(leaves, slice(lines, start+1, relCount));
    
    // find the parent that is the root
    return findRoot(leaves, parents);
  }
  
  private ArrayList<Datum> parseRelationships(ArrayList<Datum> leaves, String[] input) {
    ArrayList<Datum> parents = new ArrayList<Datum>();
    
    for (int i = 0; i < input.length; i++) {  
      // parse the line of input
      String[] comps = split(input[i], " ");
      int id = Integer.parseInt(comps[0]);
      int childID = Integer.parseInt(comps[1]);
      
      // check if that a datum with that id already exists 
      Datum d = findDatumById(parents, id);
      if (d == null) {
        d = new Datum(id);
        parents.add(d); 
      }
      
      // add the child
      Datum child = findDatumById(childID, leaves, parents);
      if (child == null) {
        // create the child if we have not yet seen one with that id
        child = new Datum(childID);
        parents.add(child);
      }
      d.children.add(child);
    }
    
    return parents;
  }
  
  private Datum findDatumById(int id, ArrayList<Datum>... dss) {
    for (ArrayList<Datum> ds : dss) {
      Datum d = findDatumById(ds, id);
      if (d != null) {
        return d;
      } 
    }
    
    println("WARNING: could not find the datum w/ id = " + id);
    return null;
  }
  
  private Datum findDatumById(ArrayList<Datum> ds, int id) {
    for (int i = 0; i < ds.size(); i++) {
      Datum d = ds.get(i);
      
      if (d.id == id) {
        return d;
      }
    } 
    
    return null;
  }
  
  private ArrayList<Datum> parseDatums(String[] input) {
    ArrayList<Datum> ds = new ArrayList<Datum>();
    
    for (int i = 0; i < input.length; i++) {
      String[] comps = split(input[i], " ");
      int id = Integer.parseInt(comps[0]);
      int value = Integer.parseInt(comps[1]);
      
      ds.add(new Datum(id, value));
    }
    
    return ds;
  }
  
  // the root is the one datum that does not appear as a child of any other node
  private Datum findRoot(ArrayList<Datum> leaves, ArrayList<Datum> parents) {    
    HashSet<Integer> childIds = new HashSet<Integer>(getChildIds(parents));
     
    HashSet<Integer> allIds = new HashSet<Integer>();
    allIds.addAll(getIds(leaves));
    allIds.addAll(getIds(parents));
     
    // set difference: all - children => ID of the (only) root
    allIds.removeAll(childIds);
     
    if (allIds.size() == 1) {
       int id = allIds.toArray(new Integer[0])[0];   // that was gross.
       return findDatumById(parents, id);
    }
    
    return null;
  }
  
  private ArrayList<Integer> getIds(ArrayList<Datum> ds) {
    ArrayList<Integer> ids = new ArrayList<Integer>();
    
    for (int i = 0; i < ds.size(); i++) {
      ids.add(ds.get(i).id); 
    }
    
    return ids;
  }
  
  private ArrayList<Integer> getChildIds(ArrayList<Datum> ds) {
    ArrayList<Integer> ids = new ArrayList<Integer>();
    
    for (int i = 0; i < ds.size(); i++) {
      ids.addAll(getIds(ds.get(i).children)); 
    }
    
    return ids;
  }
  
  private String[] slice(String[] list, int start, int count) {
    String[] ss = new String[count];
    for (int i = 0; i < count; i++) {
      ss[i] = list[start + i];
    } 
    return ss;
  }
  
}
