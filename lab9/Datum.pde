
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

  public String toString() {
    return "AuthorA = " + authorA + ", AuthorB = " + authorB + ", weight = " + weight;
  }
}

class Datum {
  
  private int id;
  private ArrayList<String> authors;
  private ArrayList<Link> links;
  
  public Datum(int id, ArrayList<String> authors, ArrayList<Link> links) {
    this.id = id;
    this.authors = authors;
    this.links = links;
  }

  public ArrayList<String> getAllAuthors() {
    return authors;
  }

  public ArrayList<Link> getAllLinks() {
    return links;
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
