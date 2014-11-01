class ForceDirectedGraph extends AbstractView {
	private RenderMachine rm;
	private Simulator sm;
	private CenterPusher cp;

	public ForceDirectedGraph(ArrayList<Node> nodes, ArrayList<Spring> springs,
		ArrayList<Zap> zaps, ArrayList<Damper> dampers, ArrayList<Datum> data) {
		super(data);

		rm = new RenderMachine(nodes, springs);
		sm = new Simulator(nodes, springs, zaps, dampers);
		cp = new CenterPusher(nodes);		
	}

	public void render() {
		 if (!done || dragged != null || previous_w != width || previous_h != height) {
   		 // update sim
    	done = !sm.step(seconds(16));
  	}

		background(color(255, 255, 255));
		cp.push();
		rm.render();
	}

	public Simulator getSimulator() {
		return sm;
	}

	public CenterPusher getCenterPusher() {
		return cp;
	}

	public void setBounds(Rect bounds) {
		this.bounds = bounds;
		cp.setBounds(bounds);
	}


	// TODO: Actually write this
	public ArrayList<Datum> getHoveredDatums() {
		return null;
	}

}