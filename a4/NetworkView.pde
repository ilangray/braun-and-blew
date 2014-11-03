import java.util.Map;

class NetworkView extends AbstractView {

	public final float NODE_WEIGHT = 4;
	public final float SPRING_LENGTH = 100;
	public final int MAX_SPRING_THICKNESS = 4;

	private ForceDirectedGraph fdg;

	public NetworkView(ArrayList<Datum> data, Rect myBounds) {
		super(data);
		ArrayList<Node> nodes = createNodes();
		ArrayList<Spring> springs = createSprings(nodes);
		ArrayList<Zap> zaps = createZaps(nodes);
		ArrayList<Damper> dampers = createDampers(nodes);
		setAllBounds(nodes, myBounds);
		placeNodes(nodes, myBounds);
		addBackingDatums(nodes);
		scaleSpringThickness(springs);
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


	private ArrayList<Spring> createSprings(ArrayList<Node> nodes) {
		ArrayList<Spring> toReturn = new ArrayList<Spring>();

		HashMap<String, Integer> springsToMake= getSpringsToMake();

		for (Map.Entry me : springsToMake.entrySet()) {
			toReturn.add(makeSpring(me, nodes));			
		}

		return toReturn;
	}

	// Creates "destIP,sourceIP" strings to tell calling function
	// which springs to make
	private HashMap<String, Integer> getSpringsToMake() {
		HashMap<String, Integer> toReturn = new HashMap<String, Integer>();

		for (Datum d : getData()) {
			String toBeKey = d.destIP + "," + d.sourceIP;
			if (toReturn.get(toBeKey) ==  null) { // First time seeing it
				toReturn.put(toBeKey, 1);  // First one seen
			} else {  // Seen it before
				toReturn.put(toBeKey, toReturn.get(toBeKey) + 1); // Increment #
			}
		}

		return toReturn;
	}

	private Spring makeSpring(Map.Entry me, ArrayList<Node> nodes) {
		String k = (String)me.getKey();  // UGLY CASTING YUK
		Integer weightPoint = (Integer)me.getValue();
		int weight = weightPoint.intValue();
		String[] listL = split(k, ",");
		String endAID = listL[0];
		String endBID = listL[1];

		Spring spring = new Spring(getCorrectNode(endAID, nodes), 
			getCorrectNode(endBID, nodes), SPRING_LENGTH);

		spring.setWeight(weight);

		return spring;
	}

	private Node getCorrectNode(String id, ArrayList<Node> nodes) {
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
  private ArrayList<Zap> createZaps(ArrayList<Node> nodes) {
    ArrayList<Zap> toReturn = new ArrayList<Zap>();
    for (int i = 0; i < nodes.size(); i++) {
      for (int j = (i + 1); j < nodes.size(); j++) {
        toReturn.add(new Zap(nodes.get(i), nodes.get(j)));
      }
    }
    return toReturn;
  }

  private ArrayList<Damper> createDampers(ArrayList<Node> nodes) {
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
		ArrayList<Datum> toReturn = new ArrayList<Datum>();
		for (Node n : fdg.getNodes()) {
			if (n.containsPoint(mouseX, mouseY)) {
				for (Datum d :  n.datumsEncapsulated) {
					toReturn.add(d);
				}
			}
		}

		return toReturn;
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

  private void addBackingDatums(ArrayList<Node> nodes) {
  	for (Node n : nodes) {
  		n.datumsEncapsulated = new ArrayList<Datum>();
  		for (Datum d : getData()) {
  			if (n.id.equals(d.destIP) || 
  				n.id.equals(d.sourceIP)) {
  				n.datumsEncapsulated.add(d);
  			}
  		}
  	}
  }

  // Finds the most weighted spring, sets this to MAX_SPRING_THICKNESS
  // and the scales the rest of the springs
  private void scaleSpringThickness(ArrayList<Spring> springs) {
  	int currentMaxThickness = getCurrentMaxThickness(springs);

  	for (Spring s : springs) {
  		s.setWeight((s.getWeight() * MAX_SPRING_THICKNESS) / currentMaxThickness);
  	}
  }

  private int getCurrentMaxThickness(ArrayList<Spring> springs) {
  	int maxW = 0;

  	for (Spring s : springs) {
  		if (s.getWeight() > maxW) {
  			maxW = s.getWeight();
  		}
  	}

  	return maxW;
  }

  public ForceDirectedGraph getFDG() {
  	return fdg;
  }
}