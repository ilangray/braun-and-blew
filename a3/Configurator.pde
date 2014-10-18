// Reads in the file
class Configurator {
  public final String fileName;

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

  // Initializes the nodes and springs from the info from the file.
  // Creates an ArrayList of Zaps and an ArrayList of Dampers, but these
  // last two are independent on the input file.
  // Returns Die Welt.
  private DieWelt initWorld(ArrayList<String> al) {
    ArrayList<Node> nodes = new ArrayList<Node>();
    ArrayList<Spring> springs = new ArrayList<Spring>();
    ArrayList<Zap> zaps = new ArrayList<Zap>();
    ArrayList<Damper> dampers = new ArrayList<Damper>();
    boolean inNodes = false;

    for (int i = 0; i < al.size (); i++) {
      // If no commas - they are list sizes and we don't need those
      if (al.get(i).indexOf(',') == -1) {
        inNodes = !inNodes;
        continue;
      }

      // Split on commas
      String[] listL = split(al.get(i), ',');

      // If get to this point, not in first line or nodes -- definitely in Springs
      if (!inNodes) {
        Spring newSpring = new Spring(getCorrectNode(int(listL[0]), nodes), getCorrectNode(int(listL[1]), nodes), int(listL[2]));
        springs.add(newSpring);
      } else {  // We are in Nodes
        Node newNode = new Node(int(listL[0]), float(listL[1]));
        nodes.add(newNode);
      }
    }
    
    return new DieWelt(nodes, springs, createZaps(nodes), createDampers(nodes));
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

