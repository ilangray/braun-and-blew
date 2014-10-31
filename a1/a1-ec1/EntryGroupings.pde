
/**
 * Functions that operate on Entries.
 */
static class EntryGroupings {
  public static final Function<Entry, String> BY_DEPT = new Function<Entry, String>() {
    public String apply(Entry e) {
      return e.dept; 
    }
  };
  
  public static final Function<Entry, String> BY_SPONSOR = new Function<Entry, String>() {
    public String apply(Entry e) {
      return e.sponsor; 
    }
  };
  
  public static final Function<Entry, String> BY_YEAR = new Function<Entry, String>() {
    public String apply(Entry e) {
      return e.year; 
    }
  };
}
