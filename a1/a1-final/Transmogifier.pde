import java.util.*;

class Transmogifier {

  private int id = 0;
 
  public ArrayList<GDatum> groupBy(ArrayList<Entry> entries, ArrayList<Property> ps) {
    // Build the Datum tree
    Datum root = new Datum(getNextId());
    root.children.addAll(groupBy(entries, ps, 0));
    root.calculateValue();
    
    //
    ArrayList<GDatum> toReturn = new ArrayList();
    for (Datum d : root.children) {
      // Create Gdatums for the top level of Datums
      String newKey = getGrouper(ps.get(0)).apply(d.getAnyLeaf().entry);
      GDatum newGDatum = new GDatum(newKey, d.value, d);
      toReturn.add(newGDatum);
    }
    
    return toReturn;
  }
  private ArrayList<Datum> groupBy(ArrayList<Entry> entries, ArrayList<Property> ps, int i) {
    if(i >= ps.size()) {
      return makeLeaves(entries);  
    }
    
    Map<String, ArrayList<Entry>> grouped = new Grouper(entries).by(getGrouper(ps.get(i)));

    ArrayList<Datum> toReturn = new ArrayList<Datum>();

    for(ArrayList<Entry> group : grouped.values()) {
      Datum d = new Datum(getNextId());//TODO ID STUFF
      // recurse to get + add the kiddies
      ArrayList<Datum> next = groupBy(group, ps, i+1);
      d.children.addAll(next);
      
      toReturn.add(d);
    }
    
    return toReturn;
    
  }
  
  private ArrayList<Datum> makeLeaves(ArrayList<Entry> entries) {
    ArrayList<Datum> toReturn = new ArrayList<Datum>();
    
    for(Entry e : entries) {
      toReturn.add(makeLeaf(e)); 
    }
    
    return toReturn;
  }
  
  private Datum makeLeaf(Entry e) {
    return new Datum(getNextId(), (int)e.funding, e);    
  }
  
  private int getNextId() {
    return id++;
  }
  
  private Function<Entry, String> getGrouper(String propertyName) {
    return new Function<Entry, String>() {
      public String apply(Entry e) {
        return null; //e.get(propertyName); 
      }
    };
  }
  
  private Function<Entry, String> getGrouper(Property p) {
    if(p == Property.DEPT) {
      return EntryGroupings.BY_DEPT;
    }
    if(p == Property.SPONSOR) {
      return EntryGroupings.BY_SPONSOR;
    }
    else {
      return EntryGroupings.BY_YEAR;
    }
  }
  
  public Transmogifier(){}//;
  
}