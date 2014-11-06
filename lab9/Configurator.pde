// Reads in the file
class Configurator {
  public final String fileName;
  public final int MASS = 10;
  public final int EXTERNAL_LINK_WEIGHT = 1;
  public final float SPRING_REST_LEN = 100.0;;

  public Configurator(String fileName) {
    this.fileName = fileName;
  }

  public DieWelt configure() {
    ArrayList<String> al = read();
    DieWelt world = initWorld(al);
    placeNodes(world);
    return world;
  }

  // Randomly assigns position of nodes within playing field.
  // TODO: Currently doesn't prevent nodes from overlapping or from part of a node from hanging off the screen
  private void placeNodes(DieWelt world) {
    for (int i = 0; i < world.nodes.size(); i++) {
      Node toEdit = world.nodes.get(i);
      toEdit.pos.x = random(width);
      toEdit.pos.y = random(height);
    }
  }

  // New initWorld
  private DieWelt initWorld(ArrayList<String> al) {
    ArrayList<Node> nodes = new ArrayList<Node>();
    ArrayList<Spring> springs = new ArrayList<Spring>();
    ArrayList<Zap> zaps = new ArrayList<Zap>();
    ArrayList<Damper> dampers = new ArrayList<Damper>();
    ArrayList<Link> externalLinks = new ArrayList<Link>();

    boolean newNode = false;
    boolean inLinks = false;

    int count = -1;
    int nodeID = -1;
    int numPeeps = -1;
    int numLinks = -1;
    ArrayList<String> newAuthors = null;
    ArrayList<Link> newLinks = null;
    for (int i = 0; i < al.size(); i++) {
      if (i == 0) { continue; };  // Num nodes I already know this

      // Does the line start with a number? -- if so it is a new node
      if (Character.isDigit(al.get(i).charAt(0))) {
          newNode = true;
          count = 0;
          int[] nums = int(split(al.get(i), ','));
          nodeID = nums[0];
          numPeeps = nums[1];
          numLinks = nums[2];

          newAuthors = new ArrayList<String>();
          newLinks = new ArrayList<Link>();
      } else if (newNode) {
          if (count  >= numPeeps + numLinks) {  // Don't count descriptor line
          Datum newDatum = new Datum(nodeID ,newAuthors, newLinks);
          nodes.add(new Node(newDatum, MASS, nodeID));
          newNode = false;
          inLinks = false;
          }   else {
          // Reading in the links
            if (inLinks) {
              String[] listL = split(al.get(i), ',');
              newLinks.add(new Link(listL[0], listL[1], Integer.parseInt(listL[2])));
            } else {  // Reading in the peoples
              newAuthors.add(al.get(i));
            }

            if (count >= numPeeps) {   // Now I'll be in links
              inLinks = true;
            }
          } 
      } else if (!Character.isDigit(al.get(i).charAt(0)) && !newNode) {
        // This happens when you have external connections
        // Make the external link

        String[] listL = split(al.get(i), ',');
        externalLinks.add(new Link(listL[0], listL[2], EXTERNAL_LINK_WEIGHT));

        // Make the spring connecting these
        springs.add(new Spring(getNodeAtIndex(Integer.parseInt(listL[1]), nodes), getNodeAtIndex(Integer.parseInt(listL[3]), nodes), SPRING_REST_LEN));
        }
      count++;
        
      }
      return new DieWelt(nodes, springs, createZaps(nodes), createDampers(nodes), externalLinks);
    }


    // Returns null if index not found
    private Node getNodeAtIndex(int ind, ArrayList<Node> nodes) {
      for (Node n : nodes) {
        if (n.id == ind) {
          return n;
        }
      }

      return null;
    }

  // Returns the node with the id
  // If no node exists with that id, than null is returned
  public Node getCorrectNode(int idToFind, ArrayList<Node> nodes) {
    for (int i = 0; i < nodes.size (); i++) {
      if (nodes.get(i).id == idToFind) {
        return nodes.get(i);
      }
    }
    return null;
  }
  
  // Makes a bunch of zaps
  public ArrayList<Zap> createZaps(ArrayList<Node> nodes) {
    ArrayList<Zap> toReturn = new ArrayList<Zap>();
    for (int i = 0; i < nodes.size(); i++) {
      for (int j = (i + 1); j < nodes.size(); j++) {
        toReturn.add(new Zap(nodes.get(i), nodes.get(j)));
      }
    }
    return toReturn;
  }
  
  public ArrayList<Damper> createDampers(ArrayList<Node> nodes) {
    ArrayList<Damper> toReturn = new ArrayList<Damper>();
    for (int i = 0; i < nodes.size(); i++) {
      toReturn.add(new Damper(nodes.get(i)));
    }
    return toReturn;
  }

  // Just reads in lines and puts them in ArrayList -- does nothing else
  private ArrayList<String> read() {
    String[] linesNormalArray = loadStrings(fileName);
    ArrayList<String> lines = new ArrayList<String>();

    for (int i = 0; i < linesNormalArray.length; i++) {
      lines.add(linesNormalArray[i]);
    } 
    return lines;
  }
}
