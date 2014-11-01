class ForceDirectedGraph extends AbstractView {
	private RenderMachine rm;
	private Simulator sm;
	private CenterPusher cp;

	public ForceDirectedGraph(DieWelt w, ArrayList<Datum> data) {
		super(data);
		rm = new RenderMachine(w.nodes, w.springs);
		sm = new Simulator(w.nodes, w.springs, w.zaps, w.dampers);
		cp = new CenterPusher(w.nodes, this.bounds);		
	}

	public void render() {
		 // if (!done || dragged != null || previous_w != width || previous_h != height) {
   		 // update sim
    	done = !sm.step(seconds(16));
  	// }

		background(color(255, 255, 255));
		cp.push();
		rm.render();
	}

	public Simulator getSimulator() {
		return sm;
	}


	// TODO: Actually write this
	public ArrayList<Datum> getHoveredDatums() {
		return null;
	}

}