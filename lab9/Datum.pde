
class Link {
  private final String authorA;
  private final String authorB;
  private final int weight;
 
  public Link(String authorA, String authorB, int weight) {
    this.authorA = authorA;
    this.authorB = authorB;
    this.weight = weight;
  } 
  
  public boolean hasAuthors(String a1, String a2) {
     return authorA.equals(a1) && authorB.equals(a2) || 
            authorA.equals(a2) && authorB.equals(a1); 
  }
}

class Datum {
  
  private int id;
  private String[] authors;
  private Link[] links;
  
  public Datum(int id, String[] authors, Link[] links) {
    this.id = id;
    this.authors = authors;
    this.links = links;
  }
  
  // defaults to zero if no such link exists
  public int getLink(String a1, String a2) {
    for (Link link : links) {
      if (link.hasAuthors(a1, a2)) {
        return link.weight; 
      }
    }
    
    return 0;
  } 
}
