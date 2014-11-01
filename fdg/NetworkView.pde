import java.util.*;

class NetworkView extends AbstractView {

	public final float NODE_WEIGHT = 4;
	public final float SPRING_LENGTH = 100;

	public ForceDirectedGraph fdg;

	public NetworkView(ArrayList<Datum> data, Rect myBounds) {
		super(data);
		ArrayList<Node> nodes = createNodes();
		ArrayList<Spring> springs = createSprings(nodes);
		ArrayList<Zap> zaps = createZaps(nodes);
		ArrayList<Damper> dampers = createDampers(nodes);
		setAllBounds(nodes, myBounds);
		placeNodes(nodes, myBounds);
		fdg = new ForceDirectedGraph(nodes, springs, zaps, dampers, data);
	}

	private ArrayList<Node> createNodes() {
		HashSet<String> nodesToMake = getNodesToMake();
		ArrayList<Node> toReturn = new ArrayList<Node>();

		for (String s : nodesToMake) {
			toReturn.add(new Node(s, NODE_WEIGHT));
		}

		return toReturn;
	}


	// HashSet filters out duplicates
	private HashSet<String> getNodesToMake() {
		HashSet<String> toReturn = new HashSet<String>();

		for (Datum d : getData()) {
			toReturn.add(d.destIP);
			toReturn.add(d.sourceIP);
		}

		return toReturn;
	}


	public ArrayList<Spring> createSprings(ArrayList<Node> nodes) {
		ArrayList<Spring> toReturn = new ArrayList<Spring>();

		HashSet<String> springsToMake= getSpringsToMake();

		for (String s : springsToMake) {
			toReturn.add(makeSpring(s, nodes));
		}

		return toReturn;
	}

	// Creates "destIP,sourceIP" strings to tell calling function
	// which springs to make
	public HashSet<String> getSpringsToMake() {
		HashSet<String> toReturn = new HashSet<String>();

		for (Datum d : getData()) {
			toReturn.add(d.destIP + "," + d.sourceIP);
		}

		return toReturn;
	}

	public Spring makeSpring(String s, ArrayList<Node> nodes) {
		String[] listL = split(s, ",");
		String endAID = listL[0];
		String endBID = listL[1];

		return new Spring(getCorrectNode(endAID, nodes), 
			getCorrectNode(endBID, nodes), SPRING_LENGTH);
	}

	public Node getCorrectNode(String id, ArrayList<Node> nodes) {
		for (Node n : nodes) {
			if (n.id.equals(id)) {
				return n;
			}
		}

		// Didn't find a node, something wrong
		println("ERROR: Invalid Node ID in getCorrectNode");
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

	public void render() {
		fdg.render();
	}

	public ArrayList<Datum> getHoveredDatums() {
		return null;
	}

	public void setBounds(Rect bounds) {
		fdg.setBounds(bounds);
	}

	private void setAllBounds(ArrayList<Node> nodes, Rect myBounds) {
    	for (Node n : nodes) {
      		n.setBounds(myBounds);
    	}
	}

  // Randomly assigns position of nodes within playing field.
  private void placeNodes(ArrayList<Node> nodes, Rect myBounds) {
    for (int i = 0; i < nodes.size(); i++) {
      Node toEdit = nodes.get(i);
      toEdit.pos.x = random(myBounds.x, myBounds.w);
      toEdit.pos.y = random(myBounds.y, myBounds.h);

      if (toEdit.id.equals("*.1.0-10")) {
      	toEdit.pos.x = myBounds.x + myBounds.w / 2;
      	toEdit.pos.y = myBounds.y + myBounds.h / 2;
      }
    }
  }
}